package com.video.entitlement.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.video.entitlement.data.model.UrlStandardizeRequest
import com.video.entitlement.data.repository.EntitleRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformDetailScreen(
    platformCode: String,
    repo: EntitleRepository,
    onPlay: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("平台: $platformCode") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") }
        })}
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("输入视频URL") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                loading = true
                scope.launch {
                    try {
                        val resp = repo.standardizeUrl(UrlStandardizeRequest(url))
                        if (resp.isSuccessful && resp.body()?.code == 0) {
                            val data = resp.body()!!.data!!
                            result = "标准化URL: ${data.canonicalUrl}\nContentKey: ${data.contentKey}\n已匹配: ${data.matched}"
                            if (data.matched) onPlay(platformCode, data.contentKey)
                        } else {
                            result = "识别失败: ${resp.body()?.message}"
                        }
                    } catch (e: Exception) { result = "错误: ${e.message}" }
                    loading = false
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = url.isNotBlank() && !loading) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp)) else Text("识别并播放")
            }
            if (result.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(result, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
