package com.video.entitlement.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.video.entitlement.data.model.RedeemRequest
import com.video.entitlement.data.repository.EntitleRepository
import com.video.entitlement.util.TokenManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemScreen(repo: EntitleRepository, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var code by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("兑换权益码") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") }
        })}
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("输入权益码兑换VIP权益", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("权益码") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                loading = true; result = ""
                scope.launch {
                    try {
                        val tm = TokenManager(context)
                        val resp = repo.redeemCode(RedeemRequest(code, tm.deviceId))
                        if (resp.isSuccessful && resp.body()?.code == 0)
                            result = "兑换成功! 产品: ${resp.body()!!.data!!.userEntitlement.productName}"
                        else result = "兑换失败: ${resp.body()?.message}"
                    } catch (e: Exception) { result = "错误: ${e.message}" }
                    loading = false
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = code.isNotBlank() && !loading) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp)) else Text("兑换")
            }
            if (result.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Card(Modifier.fillMaxWidth()) { Text(result, Modifier.padding(16.dp)) }
            }
        }
    }
}
