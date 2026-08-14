package com.finanzas.automatica.domain.importer

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * OCR 100% local: ML Kit Text Recognition corre en el dispositivo (no envia la imagen a
 * ningun servidor ni usa un LLM), asi que respeta la regla de CLAUDE.md de no depender de
 * servicios de IA externos. Se usa para "leer" fotos de facturas/recibos y capturas de
 * pantalla de movimientos que no llegaron como notificacion.
 */
object ImageTextRecognizer {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    /** Para la foto tomada con la camara del sistema (llega como Bitmap en memoria). */
    suspend fun recognize(bitmap: Bitmap): String =
        recognizeImage(InputImage.fromBitmap(bitmap, 0))

    /** Para una imagen elegida de la galeria (llega como Uri de content://). */
    suspend fun recognize(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        recognizeImage(InputImage.fromFilePath(context, uri))
    }

    private suspend fun recognizeImage(image: InputImage): String =
        suspendCancellableCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { visionText -> cont.resume(visionText.text) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
}
