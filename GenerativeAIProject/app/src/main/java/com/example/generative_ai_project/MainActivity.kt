package com.example.generative_ai_project
import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.lifecycleScope
import com.example.generative_ai_project.accessibility.MyGestureService
import com.example.generative_ai_project.api.FirebaseFunctionApi
import com.example.generative_ai_project.ui.ImageCaptionScreen
import com.example.generative_ai_project.model.ImageRequest
import com.example.generative_ai_project.service.OverlayService
import com.example.generative_ai_project.util.ImageUtils
import com.example.generative_ai_project.viewmodel.ImageCaptionViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // "다른 앱 위에 표시" 권한이 없을 경우 다이얼로그로 안내
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !Settings.canDrawOverlays(this)
        ) {
            android.app.AlertDialog.Builder(this)
                .setTitle("권한 설정 필요")
                .setMessage("이미지 설명 생성을 위해\n'다른 앱 위에 표시' 권한이 필요합니다.")
                .setPositiveButton("설정으로 이동"
                ) { dialog, which ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
                .setNegativeButton("취소", null)
                .show()
        } else {
            // 권한이 있으면 오버레이 서비스 시작
            startService(Intent(this, OverlayService::class.java))
        }

        // 항상 UI는 먼저 보여주기
        setContent {
            ImageCaptionScreen()
        }
    }
}


fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
    val expectedComponentName = ComponentName(context, service)
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServices)
    for (component in colonSplitter) {
        if (ComponentName.unflattenFromString(component) == expectedComponentName) {
            return true
        }
    }
    return false
}