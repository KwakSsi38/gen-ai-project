package com.example.generative_ai_project.ui
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.ByteArrayOutputStream
import android.provider.Settings
import androidx.compose.ui.Alignment
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import com.example.generative_ai_project.ui.theme.YellowPrimary

@Composable
fun ImageCaptionScreen() {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F6E7) // 연한 베이지 배경
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF0A5), // 연한 베이지
                            Color.White        // 아래로 갈수록 하얗게
                        )
                    )
                )
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 무슨 앱인가요?
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "무슨 앱인가요?",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• 이 앱은 시각 장애인을 위한 AI 캡션 생성 앱입니다.")
                        Text("• 이 앱은 현재 화면을 캡처합니다.")
                        Text("• 캡처한 화면에 포함된 이미지에 대한 설명을 생성합니다.")
                        Text("• 유튜브 썸네일, 문장 상세 페이지 등에 대해서 사용할 수 있습니다.")
                    }
                }

                // 사용 설명서
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "사용 설명서",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "1. 원활한 진행을 위해 다른 앱 위에 표시 권한이 필요합니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Text("   • 설정 → 애플리케이션 → 왼쪽 상단 표시 메뉴 → 특별한 접근 → 다른 앱 위에 표시 → '시각 도우미'를 활성화 해주세요.")
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + context.packageName)
                                )
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowPrimary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .padding(top = 16.dp)
                        ) {
                            Text("권한 설정 바로가기")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "2. 권한 설정 시 오른쪽 상단의 카메라 로고의 동그라미 버튼이 표시됩니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Text("   • 해당 버튼은 현재 화면을 캡처하는 버튼입니다.")
                        Text("   • 누를 시 화면 캡처 권한을 확인 후 AI 캡션 생성이 실행됩니다.")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "3. 결과창",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Text("   • 화면을 캡처한 직후 대기하면 설명이 생성됩니다.")
                        Text("   • TalkBack 기능을 활용하면 설명이 생성되자마자 음성으로 출력됩니다.")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "4. 화면 나가기",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Text("   • 설명을 다 들으셨다면 뒤로 가기 버튼을 눌러 원래 화면으로 돌아갈 수 있습니다.")
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "자주 하는 문의 사항",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "1. 버튼이 나타나지 않아요.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Text("   • 앱을 껐다 다시 실행해주세요.")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "2. 캡처를 했는데 앱의 설명 화면이 캡처돼요.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Text("   • 앱이 백그라운드로 실행되지 않도록 해주세요.")
                        Text("   • 백그라운드로 실행 중인 앱을 확인하는 버튼을 눌러 앱을 종료해주세요.")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "3. 플로팅 버튼이 안 떴으면 좋겠어요.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Text("   • 권한 설정 바로가기 버튼을 눌러 다른 앱 위에 표시를 꺼주세요.")
                    }
                }
            }
        }
    }
}
