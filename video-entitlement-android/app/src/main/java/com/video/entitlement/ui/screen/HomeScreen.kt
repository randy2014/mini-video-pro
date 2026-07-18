package com.video.entitlement.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.video.entitlement.data.model.VideoPlatform
import com.video.entitlement.data.repository.EntitleRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repo: EntitleRepository,
    onPlatformClick: (String) -> Unit,
    onMyEntitlements: () -> Unit,
    onRedeem: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var platforms by remember { mutableStateOf<List<VideoPlatform>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val resp = repo.getPlatforms()
            if (resp.isSuccessful && resp.body()?.code == 0)
                platforms = resp.body()!!.data ?: emptyList()
        } catch (_: Exception) {}
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("视频权益") }, actions = {
                IconButton(onClick = onRedeem) { Icon(Icons.Default.Star, "兑换") }
                IconButton(onClick = onMyEntitlements) { Icon(Icons.Default.Star, "我的权益") }
                IconButton(onClick = onLogout) { Icon(Icons.Default.ExitToApp, "退出") }
            })
        }
    ) { padding ->
        if (loading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(platforms) { platform ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { onPlatformClick(platform.platformCode) }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(platform.platformName, style = MaterialTheme.typography.titleMedium)
                                Text(platform.homeUrl, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.KeyboardArrowRight, null)
                        }
                    }
                }
                if (platforms.isEmpty()) item {
                    Text("暂无可用的视频平台", modifier = Modifier.padding(32.dp))
                }
            }
        }
    }
}
