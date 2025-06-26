package com.example.generative_ai_project.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.media.Image
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

object ImageUtils {

    fun saveBitmapToFile(context: Context, bitmap: Bitmap): File {
        val filename = "captured_${System.currentTimeMillis()}.png"
        val imagesDir = File(context.cacheDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val file = File(imagesDir, filename)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun encodeImageToBase64(file: File): String {
        val bytes = file.readBytes()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun imageToBitmap(image: Image, width: Int, height: Int): Bitmap? {
        val plane = image.planes[0]
        val buffer: ByteBuffer = plane.buffer
        val pixelStride: Int = plane.pixelStride
        val rowStride: Int = plane.rowStride
        val rowPadding: Int = rowStride - pixelStride * width

        return Bitmap.createBitmap(
            width + rowPadding / pixelStride,
            height,
            Bitmap.Config.ARGB_8888
        ).apply {
            copyPixelsFromBuffer(buffer)
        }
    }
}
