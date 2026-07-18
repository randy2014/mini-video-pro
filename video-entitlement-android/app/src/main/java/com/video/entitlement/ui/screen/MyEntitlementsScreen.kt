package com.video.entitlement.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.video.entitlement.data.model.UserEntitlementVO
import com.video.entitlement.data.repository.EntitleRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEntitlementsScreen(repo: EntitleRepository, onBack: () -> Unit) {
    var entitlements by remember { mutableStateOf<List<UserEntitlementVO>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val resp = repo.getMyEntitlements()
            if (resp.isSuccessful && resp.body()?.code == 0)
                entitlements = resp.body()!!.data ?: emptyList()
        } catch (_: Exception) {}
        loading = false
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("我的权益") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") }
        })}
    ) { padding ->
        if (loading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        } else {
            LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (entitlements.isEmpty()) item { Text("暂未拥有权益", Modifier.padding(32.dp)) }
                items(entitlements) { e ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(e.productName ?: e.productCode ?: "权益", style = MaterialTheme.typography.titleMedium)
                            Text("状态: ${e.status} | 来源: ${e.sourceType}", style = MaterialTheme.typography.bodySmall)
                            Text("已用: ${e.usedTotal}次 | 有效期至: ${e.expiresAt ?: "永久"}",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
