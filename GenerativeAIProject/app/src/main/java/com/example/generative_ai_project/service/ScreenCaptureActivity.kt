package com.example.generative_ai_project.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.generative_ai_project.ui.ImageCaptionActivity
import com.example.generative_ai_project.util.ImageUtils

class ScreenCaptureActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 1234
    }

    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var width = 1080
    private var height = 1920

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("확인", "📸 ScreenCaptureActivity 실행됨")

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        // 화면 사이즈 계산
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = getSystemService(WindowManager::class.java).currentWindowMetrics
            val bounds = windowMetrics.bounds
            width = bounds.width()
            height = bounds.height()
        } else {
            @Suppress("DEPRECATION")
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(metrics)
            width = metrics.widthPixels
            height = metrics.heightPixels
        }

        // 널이 아님을 명시
        val captureIntent = mediaProjectionManager?.createScreenCaptureIntent()
        startActivityForResult(captureIntent!!, REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("registerForActivityResult"))
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Log.d("확인", "📸 캡처 권한 승인됨")

            mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)
            imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

            val virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenCapture",
                width,
                height,
                resources.displayMetrics.densityDpi,
                0,
                imageReader?.surface,
                null,
                null
            )

            Thread {
                Thread.sleep(1000) // 화면 준비 시간 확보

                val image = imageReader?.acquireLatestImage()
                val bitmap = image?.let { ImageUtils.imageToBitmap(it, width, height) }
                image?.close()
                virtualDisplay?.release()
                mediaProjection?.stop()

                if (bitmap != null) {
                    val file = ImageUtils.saveBitmapToFile(this, bitmap)
                    val intent = Intent(this, ImageCaptionActivity::class.java)
                    Log.d("확인", file.absolutePath)
                    intent.putExtra("imagePath", file.absolutePath)
                    startActivity(intent)
                } else {
                    Log.e("확인", "이미지 캡처 실패")
                }
                finish()
            }.start()
        } else {
            Log.e("확인", "📸 캡처 권한 거부 또는 실패")
            finish()
        }
    }
}
