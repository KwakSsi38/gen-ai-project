
package com.example.generative_ai_project.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.generative_ai_project.R

class CaptureService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("확인", "📸 CaptureService 실행됨")

        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
            Log.d("확인", "버전 if문 실행됨")
        } else {
            startForeground(1, notification)
        }

        val projectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projectionIntent = Intent(this, ScreenCaptureActivity::class.java)
        projectionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(projectionIntent)
        Log.d("확인", "projectionIntent 시작")

        return START_NOT_STICKY
    }

    private fun buildNotification(): Notification {
        val channelId = "capture_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Capture Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        Log.d("확인", "buildNotification if문 통과")

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("캡처 서비스 실행 중")
            .setContentText("화면을 캡처하려고 합니다")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
