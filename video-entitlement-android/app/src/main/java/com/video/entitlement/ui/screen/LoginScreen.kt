package com.video.entitlement.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.video.entitlement.data.api.RetrofitClient
import com.video.entitlement.data.model.UserLoginRequest
import com.video.entitlement.data.repository.EntitleRepository
import com.video.entitlement.util.TokenManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(repo: EntitleRepository, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mobile by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("000000") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("视频权益", fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text("登录后可享受VIP特权", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(48.dp))
        OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("手机号") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("验证码 (开发输000000)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            loading = true; error = null
            scope.launch {
                try {
                    val tm = TokenManager(context)
                    val resp = repo.userLogin(UserLoginRequest(mobile.ifEmpty { "13800138000" }, code, tm.deviceId))
                    if (resp.isSuccessful && resp.body()?.code == 0) {
                        val data = resp.body()!!.data!!
                        tm.accessToken = data.accessToken
                        tm.refreshToken = data.refreshToken
                        RetrofitClient.tokenManager = tm
                        onLoginSuccess()
                    } else error = resp.body()?.message ?: "登录失败"
                } catch (e: Exception) { error = e.message }
                loading = false
            }
        }, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = !loading) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("登录")
        }
    }
}
