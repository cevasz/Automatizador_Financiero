package com.finanzas.automatica.data.sync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/** Error de red o del servidor, ya con un mensaje que se le puede mostrar al usuario. */
class SupabaseException(message: String, cause: Throwable? = null) : Exception(message, cause)

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    /** Instante (epoch millis) en que caduca el access token. */
    val expiresAt: Long,
    val userId: String,
    val email: String
)

/**
 * Cliente HTTP minimo contra Supabase (Auth + RPC de PostgREST).
 *
 * Se usa `HttpURLConnection` y no una libreria: son cuatro peticiones en toda
 * la app y el SDK oficial de Supabase para Android arrastra Ktor entero. El
 * proyecto evita dependencias que no se ganen su peso (ver CLAUDE.md).
 *
 * La `anonKey` es publica por diseño — va dentro del APK y no hay forma de
 * esconderla. Lo que protege los datos es Row Level Security en Postgres: sin
 * un JWT de sesion valido, `auth.uid()` es null y ninguna politica deja pasar
 * una sola fila.
 */
class SupabaseClient(
    private val baseUrl: String,
    private val anonKey: String
) {

    val isConfigured: Boolean
        get() = baseUrl.isNotBlank() && anonKey.isNotBlank() && baseUrl.startsWith("https://")

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    // --- Autenticacion -------------------------------------------------------

    suspend fun signUp(email: String, password: String): AuthTokens =
        auth("/auth/v1/signup", buildJsonObject {
            put("email", email)
            put("password", password)
        })

    suspend fun signIn(email: String, password: String): AuthTokens =
        auth("/auth/v1/token?grant_type=password", buildJsonObject {
            put("email", email)
            put("password", password)
        })

    suspend fun refresh(refreshToken: String): AuthTokens =
        auth("/auth/v1/token?grant_type=refresh_token", buildJsonObject {
            put("refresh_token", refreshToken)
        })

    private suspend fun auth(ruta: String, cuerpo: JsonObject): AuthTokens {
        val respuesta = peticion(ruta, cuerpo, accessToken = null).jsonObject

        val accessToken = respuesta["access_token"]?.jsonPrimitive?.contentOrNullSafe()
            ?: throw SupabaseException(
                "El servidor respondió sin sesión. Si acabas de registrarte, puede que " +
                    "falte confirmar el correo."
            )

        val expiresIn = respuesta["expires_in"]?.jsonPrimitive?.contentOrNullSafe()?.toLongOrNull() ?: 3600L
        val usuario = respuesta["user"]?.jsonObject

        return AuthTokens(
            accessToken = accessToken,
            refreshToken = respuesta["refresh_token"]?.jsonPrimitive?.contentOrNullSafe().orEmpty(),
            expiresAt = System.currentTimeMillis() + expiresIn * 1000L,
            userId = usuario?.get("id")?.jsonPrimitive?.contentOrNullSafe().orEmpty(),
            email = usuario?.get("email")?.jsonPrimitive?.contentOrNullSafe().orEmpty()
        )
    }

    // --- Llamadas a las funciones de sincronizacion --------------------------

    suspend fun rpc(nombre: String, argumentos: JsonObject, accessToken: String): JsonElement =
        peticion("/rest/v1/rpc/$nombre", argumentos, accessToken)

    // --- Transporte ----------------------------------------------------------

    private suspend fun peticion(
        ruta: String,
        cuerpo: JsonObject,
        accessToken: String?
    ): JsonElement = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            throw SupabaseException(
                "La sincronización no está configurada en esta compilación. " +
                    "Ver kivo-android/local.properties.example."
            )
        }

        val conexion = (URL(baseUrl.trimEnd('/') + ruta).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            // Sin timeouts, una red que acepta la conexion pero no responde deja
            // la corrutina colgada para siempre y el usuario ve "Sincronizando…"
            // indefinidamente.
            connectTimeout = 15_000
            readTimeout = 60_000
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer ${accessToken ?: anonKey}")
            setRequestProperty("Accept", "application/json")
        }

        try {
            conexion.outputStream.use { it.write(cuerpo.toString().toByteArray(StandardCharsets.UTF_8)) }

            val codigo = conexion.responseCode
            val texto = (if (codigo in 200..299) conexion.inputStream else conexion.errorStream)
                ?.bufferedReader(StandardCharsets.UTF_8)
                ?.use(BufferedReader::readText)
                .orEmpty()

            if (codigo !in 200..299) throw SupabaseException(mensajeDeError(codigo, texto))
            if (texto.isBlank()) return@withContext JsonObject(emptyMap())

            json.parseToJsonElement(texto)
        } catch (e: SupabaseException) {
            throw e
        } catch (e: Throwable) {
            // Throwable y no Exception: un fallo de TLS o de la libreria de red
            // puede llegar como Error, y dejarlo escapar tumbaria el proceso —
            // el mismo patron que ya se corrigio al leer PDFs.
            throw SupabaseException("No se pudo conectar con el servidor: ${e.message ?: e::class.java.simpleName}", e)
        } finally {
            conexion.disconnect()
        }
    }

    private fun mensajeDeError(codigo: Int, cuerpo: String): String {
        val detalle = runCatching {
            val obj = json.parseToJsonElement(cuerpo).jsonObject
            obj["msg"]?.jsonPrimitive?.contentOrNullSafe()
                ?: obj["message"]?.jsonPrimitive?.contentOrNullSafe()
                ?: obj["error_description"]?.jsonPrimitive?.contentOrNullSafe()
                ?: obj["error"]?.jsonPrimitive?.contentOrNullSafe()
        }.getOrNull() ?: cuerpo.take(200)

        return when (codigo) {
            400 -> when {
                detalle.contains("Invalid login credentials", true) -> "Correo o contraseña incorrectos."
                detalle.contains("already registered", true) -> "Ese correo ya tiene cuenta. Entra en vez de registrarte."
                detalle.contains("Email not confirmed", true) -> "Falta confirmar el correo. Revisa tu bandeja de entrada."
                else -> detalle
            }
            401, 403 -> "La sesión caducó o no tiene permiso. Vuelve a entrar."
            404 -> "El servidor no reconoce esta operación. ¿Se ejecutaron las migraciones de backend/supabase/?"
            in 500..599 -> "El servidor tuvo un problema ($codigo). Intenta más tarde."
            else -> "Error $codigo: $detalle"
        }
    }
}

/**
 * `jsonPrimitive.content` devuelve la cadena "null" para un null de JSON, lo
 * que hacia que un campo ausente se guardara literalmente como "null".
 */
private fun kotlinx.serialization.json.JsonPrimitive.contentOrNullSafe(): String? =
    if (this is kotlinx.serialization.json.JsonNull) null else content
