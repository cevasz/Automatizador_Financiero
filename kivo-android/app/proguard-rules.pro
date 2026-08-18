# ============================================================================
# Reglas de R8 para la variante release de Kivo.
#
# R8 quita y renombra todo lo que no ve usado. Lo que rompe eso son las cosas
# que se resuelven en tiempo de ejecucion por NOMBRE (reflexion), porque en el
# codigo no aparece ninguna referencia que R8 pueda seguir. Aqui esta cada caso
# real del proyecto, con el porque — no una lista copiada.
# ============================================================================

# --- Room -------------------------------------------------------------------
# Room genera implementaciones (FinanzasDatabase_Impl, *_Impl de cada DAO) que
# se cargan por nombre desde Room.databaseBuilder. Sin esto, la app arranca y
# falla al abrir la base con "cannot find implementation".
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# --- kotlinx.serialization --------------------------------------------------
# El serializador de cada clase se busca por reflexion sobre el companion.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- PDFBox (lectura de extractos bancarios) --------------------------------
# pdfbox-android carga filtros y fuentes por nombre desde sus recursos, y
# arrastra referencias a clases de Java SE que no existen en Android (por eso
# los dontwarn: son avisos de codigo que en Android nunca se ejecuta).
-keep class com.tom_roush.pdfbox.** { *; }
-keep class com.tom_roush.fontbox.** { *; }
-dontwarn com.tom_roush.pdfbox.**
-dontwarn org.bouncycastle.**
-dontwarn javax.naming.**
-dontwarn java.awt.**

# --- ML Kit (OCR de facturas y capturas) ------------------------------------
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# --- Servicios declarados en el manifiesto ----------------------------------
# El sistema los instancia por nombre desde AndroidManifest.xml. Si R8 los
# renombra, Android no los encuentra: la captura de notificaciones bancarias
# dejaria de funcionar sin ningun error visible — justo el sintoma mas dificil
# de diagnosticar que ya se sufrio con los paquetes de banco equivocados.
-keep class com.finanzas.automatica.service.NotificationCaptureService { *; }
-keep class com.finanzas.automatica.service.MovementProcessorService { *; }
-keep class com.finanzas.automatica.service.BootReceiver { *; }
-keep class com.finanzas.automatica.FinanzasApplication { *; }

# --- Enums guardados como texto en la base ----------------------------------
# Los enums de dominio se persisten por su `name` y se releen con `valueOf`.
# Si R8 renombrara sus constantes, cada fila ya guardada quedaria ilegible.
-keepclassmembers enum com.finanzas.automatica.domain.model.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- Diagnostico ------------------------------------------------------------
# Conserva numeros de linea para que un stack trace de produccion sea legible,
# pero oculta el nombre real del archivo fuente.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
