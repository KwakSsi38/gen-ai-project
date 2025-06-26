package com.example.generative_ai_project.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.generative_ai_project.R
import com.example.generative_ai_project.util.ImageUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class ImageCaptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caption)

        val uriString = intent.getStringExtra("imagePath")
        if (uriString == null) {
            Log.e("확인", "Uri가 전달되지 않음")
            return
        }
        val uri = Uri.parse(uriString)
        val file = File(uri.path ?: return)
        Log.d("확인", "파일 크기: ${file.length()} bytes")

        uploadImageToFirebaseFromFile(file)
    }

    private fun uploadImageToFirebaseFromFile(file: File) {
        val url = "http://13.124.208.197:3000/api/upload"
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)   // 연결 시도 시간
            .writeTimeout(30, TimeUnit.SECONDS)     // 업로드 시간
            .readTimeout(60, TimeUnit.SECONDS)      // 응답 대기 시간 (🔥 가장 중요)
            .build()


        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                file.name,
                file.asRequestBody("image/png".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("확인", "업로드 실패: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val captionView = findViewById<TextView>(R.id.captionTextView)
                    if (!response.isSuccessful) {
                        Log.e("확인", "응답 실패: ${response.code}")
                        runOnUiThread {
                            captionView.text = "응답 실패"
                        }
                        return
                    }
                    val responseBody = response.body?.string()
                    val captionText = JSONObject(responseBody).getString("caption")
                    runOnUiThread {
                        captionView.text = captionText
                    }
                }
            }
        })
    }
}
