package com.video.entitlement.ui.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.video.entitlement.data.model.PlaybackReportRequest
import com.video.entitlement.data.model.PlaybackResolveRequest
import com.video.entitlement.data.repository.EntitleRepository
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    platformCode: String,
    contentKey: String,
    repo: EntitleRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var playUrl by remember { mutableStateOf<String?>(null) }
    var requestId by remember { mutableStateOf("") }
    var attemptNo by remember { mutableStateOf(1) }
    var error by remember { mutableStateOf<String?>(null) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val resp = repo.resolvePlayback(PlaybackResolveRequest(platformCode, contentKey, null, null))
            if (resp.isSuccessful && resp.body()?.code == 0) {
                val data = resp.body()!!.data!!
                requestId = data.requestId
                attemptNo = data.attemptNo ?: 1
                playUrl = data.targetUrl
            } else error = "解析失败: ${resp.body()?.message}"
        } catch (e: Exception) { error = e.message }
    }

    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("播放中") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") }
            }, actions = {
                if (error != null || !loaded) {
                    IconButton(onClick = {
                        scope.launch {
                            error = null
                            try {
                                val resp = repo.resolvePlayback(PlaybackResolveRequest(platformCode, contentKey, null, null))
                                if (resp.isSuccessful && resp.body()?.code == 0) {
                                    val data = resp.body()!!.data!!
                                    requestId = data.requestId
                                    playUrl = data.targetUrl
                                }
                            } catch (e: Exception) { error = e.message }
                        }
                    }) { Icon(Icons.Default.Refresh, "重试") }
                }
            })
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (error != null) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onBack) { Text("返回") }
                }
            } else if (playUrl != null) {
                AndroidView(factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) { loaded = false }
                            override fun onPageFinished(view: WebView?, url: String?) { loaded = true }
                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                scope.launch {
                                    try {
                                        repo.reportPlayback(PlaybackReportRequest(requestId, attemptNo, "FAILED", "PAGE_LOAD_FAILED", null))
                                    } catch (_: Exception) {}
                                    onBack()
                                }
                            }
                        }
                        loadUrl(playUrl!!)
                    }
                }, modifier = Modifier.fillMaxSize())
                if (!loaded) LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
        }
    }
}
