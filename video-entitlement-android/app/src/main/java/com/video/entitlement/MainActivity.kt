package com.video.entitlement

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class Platform(val name: String, val url: String, val type: String, val code: String, val logo: String)

class MainActivity : AppCompatActivity() {

    private var webView: WebView? = null
    private var titleText: TextView? = null
    private var progressBar: ProgressBar? = null
    private var backBtn: TextView? = null
    private var homeContainer: View? = null
    private var browserContainer: View? = null
    private var platformContainer: LinearLayout? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingOverlay: View? = null
    private var loadingText: TextView? = null

    private var currentUrl = ""
    private var currentTitle = ""
    private var downloadId: Long = -1L

    private val dp1 get() = resources.displayMetrics.density
    private val API_BASE = "http://43.161.222.78:8081"

    // 品牌色映射
    private val brandColors = mapOf(
        "iqiyi" to 0xFF1FB47C.toInt(), "tencent" to 0xFFFF7028.toInt(),
        "mgtv" to 0xFFFFB617.toInt(), "bilibili" to 0xFFFB7299.toInt(),
        "youku" to 0xFF1991EA.toInt(), "1905" to 0xFF1565C0.toInt(),
        "ixigua" to 0xFFFF3B30.toInt(), "wangyiyun" to 0xFFEC4141.toInt(),
        "qqmusic" to 0xFF31C27C.toInt(), "kugou" to 0xFFFFA810.toInt(),
        "cctv" to 0xFFC62828.toInt(), "meiju" to 0xFFFF5722.toInt(),
        "hanju" to 0xFF9C27B0.toInt()
    )

    private val typeLabels = mapOf(
        "video" to "视频网站", "music" to "音乐平台", "tv" to "电视直播", "drama" to "影视剧"
    )

    private val logoCache = mutableMapOf<String, Bitmap>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // 登录检查：未登录则跳转 LoginActivity
            val token = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("access_token", null)
            if (token.isNullOrEmpty()) {
                startActivity(android.content.Intent(this, LoginActivity::class.java))
                finish()
                return
            }

            setContentView(R.layout.activity_main)
            enableEdgeToEdge()
            initViews()
            showVersion()
            setupWebView()
            checkUpdate()
            registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED)
            fetchPlatforms()
        } catch (e: Exception) {
            showError("启动失败: ${e.message}")
        }
    }

    private fun enableEdgeToEdge() {
        // 内容延伸到状态栏后面
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
        }
        // 状态栏和导航栏透明
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }

    private fun showVersion() {
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            val vName = info.versionName ?: "1.0"
            val vCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                info.longVersionCode else info.versionCode.toLong()
            findViewById<TextView>(R.id.version_text)?.text = "v$vName ($vCode)"
        } catch (_: Exception) { }
    }

    private fun initViews() {
        homeContainer = findViewById(R.id.swipe_refresh)
        browserContainer = findViewById(R.id.browser_container)
        titleText = findViewById(R.id.title_text)
        progressBar = findViewById(R.id.progress_bar)
        backBtn = findViewById(R.id.back_btn)
        platformContainer = findViewById(R.id.platform_container)
        webView = findViewById(R.id.web_view)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        loadingOverlay = findViewById(R.id.loading_overlay)
        loadingText = findViewById(R.id.loading_text)

        swipeRefresh?.setOnRefreshListener { fetchPlatforms() }
        swipeRefresh?.setColorSchemeColors(0xFF7C5CBF.toInt())

        backBtn?.setOnClickListener {
            if (browserContainer?.visibility == View.VISIBLE) showHome()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val wv = webView ?: return
        try {
            WebView.setWebContentsDebuggingEnabled(true)
            wv.settings.apply {
                javaScriptEnabled = true; domStorageEnabled = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                useWideViewPort = true; loadWithOverviewMode = true
                setSupportZoom(true); builtInZoomControls = true; displayZoomControls = false
                allowFileAccess = true; allowContentAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
                userAgentString = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36"
                setGeolocationEnabled(true); setSupportMultipleWindows(false)
            }
            wv.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar?.visibility = View.VISIBLE; if (url != null) currentUrl = url
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar?.visibility = View.GONE
                    titleText?.text = view?.title ?: currentTitle
                }
                override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?) = false
                override fun onReceivedError(v: WebView?, r: WebResourceRequest?, e: WebResourceError?) {
                    progressBar?.visibility = View.GONE
                    titleText?.text = "\u26A0 ${e?.description ?: "加载失败"}"
                }
            }
            wv.webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    if (title != null) titleText?.text = title
                }
                override fun onProgressChanged(view: WebView?, p: Int) {
                    progressBar?.progress = p
                    if (p == 100) progressBar?.visibility = View.GONE
                }
                override fun onShowCustomView(view: View?, cb: CustomViewCallback?) {
                    cb?.onCustomViewHidden()
                }
            }
        } catch (_: Exception) { }
    }

    // ====== API 平台加载 ======
    private fun fetchPlatforms() {
        showLoading(true)
        Thread {
            var platforms: List<Platform> = emptyList()
            var ok = false
            try {
                val conn = URL("$API_BASE/api/v1/client/platforms").openConnection() as HttpURLConnection
                conn.connectTimeout = 10000; conn.readTimeout = 10000
                val json = JSONObject(conn.inputStream.reader().readText())
                val data = json.optJSONArray("data")
                if (data != null && data.length() > 0) {
                    val list = mutableListOf<Platform>()
                    for (i in 0 until data.length()) {
                        val p = data.getJSONObject(i)
                        val code = p.getString("platformCode")
                        val logoUrl = p.optString("logo", "")
                        list.add(Platform(p.getString("platformName"), p.getString("homeUrl"), guessType(code), code, logoUrl))
                    }
                    // 预加载 logo 到缓存
                    for (plt in list) {
                        if (plt.logo.isNotEmpty() && plt.code !in logoCache) {
                            try {
                                logoCache[plt.code] = BitmapFactory.decodeStream(URL(plt.logo).openStream())
                            } catch (_: Exception) { }
                        }
                    }
                    platforms = list; ok = true
                }
                conn.disconnect()
            } catch (e: Exception) {
                // 网络/解析错误会被下面 runOnUiThread 的 else 分支处理
                e.printStackTrace()
            }

            runOnUiThread {
                showLoading(false)
                swipeRefresh?.isRefreshing = false
                if (ok && platforms.isNotEmpty()) {
                    renderPlatforms(platformContainer!!, platforms)
                    updateStats(platforms)
                } else {
                    loadingText?.text = "加载失败，下拉重试"
                    loadingOverlay?.visibility = View.VISIBLE
                }
            }
        }.start()
    }

    private fun showLoading(show: Boolean) {
        loadingOverlay?.visibility = if (show) View.VISIBLE else View.GONE
        if (show) loadingText?.text = "加载中..."
    }

    private fun updateStats(platforms: List<Platform>) {
        findViewById<TextView>(R.id.stat_platforms)?.text = "${platforms.count { it.type == "video" }}"
        findViewById<TextView>(R.id.stat_music)?.text = "${platforms.count { it.type == "music" }}"
        findViewById<TextView>(R.id.stat_tv)?.text = "${platforms.count { it.type == "tv" }}"
    }

    private fun guessType(code: String): String = when {
        code in listOf("wangyiyun", "qqmusic", "kugou") -> "music"
        code == "cctv" -> "tv"
        code in listOf("meiju", "hanju") -> "drama"
        else -> "video"
    }

    // ====== UI 构建 ======
    private fun renderPlatforms(container: LinearLayout, platforms: List<Platform>) {
        try {
            container.removeAllViews()
            for ((type, label) in typeLabels) {
                val group = platforms.filter { it.type == type }
                if (group.isEmpty()) continue
                container.addView(buildSectionHeader(label))
                container.addView(buildCardGrid(group))
            }
        } catch (_: Exception) { }
    }

    private fun buildSectionHeader(label: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(26), 0, dp(12))
        }
        val dot = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(6), dp(6)).apply { marginEnd = dp(8) }
            background = GradientDrawable().apply {
                setColor(0xFFD4AF37.toInt()); shape = GradientDrawable.OVAL
            }
        }
        val tv = TextView(this).apply {
            text = label; textSize = 16f; setTypeface(null, Typeface.BOLD)
            setTextColor(0xFFF1D77A.toInt())
        }
        row.addView(dot); row.addView(tv)
        return row
    }

    private fun buildCardGrid(platforms: List<Platform>): LinearLayout {
        val grid = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(MATCH, WRAP)
        }
        var row: LinearLayout? = null
        platforms.forEachIndexed { i, p ->
            if (i % 2 == 0) {
                row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(MATCH, WRAP)
                }
                grid.addView(row)
            }
            val card = buildPlatformCard(p)
            val lp = LinearLayout.LayoutParams(0, WRAP, 1f)
            val isLeft = i % 2 == 0
            lp.setMargins(if (isLeft) 0 else dp(5), 0, if (isLeft) dp(5) else 0, dp(10))
            row?.addView(card, lp)
        }
        return grid
    }

    private fun buildPlatformCard(p: Platform): LinearLayout {
        val brand = brandColors[p.code] ?: 0xFF7C5CBF.toInt()
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            minimumHeight = dp(136)
            setPadding(dp(14), dp(18), dp(14), dp(18))
            contentDescription = "${p.name}，${typeLabels[p.type] ?: "平台"}"
            isFocusable = true
            elevation = dp(2).toFloat()
            background = GradientDrawable().apply {
                setColor(0xF21E1035.toInt()); cornerRadius = dp(16).toFloat()
                setStroke(dp(1), 0x3DD4AF37)
            }
            setOnClickListener { openPlatform(p) }
        }

        // 顶部三点装饰
        val dots = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(WRAP, WRAP).apply { bottomMargin = dp(10) }
        }
        for (j in 0..2) {
            dots.addView(View(this).apply {
                val s = if (j == 0) dp(4) else dp(3)
                layoutParams = LinearLayout.LayoutParams(s, s).apply {
                    marginEnd = if (j < 2) dp(3) else 0
                }
                background = GradientDrawable().apply {
                    setColor(if (j == 0) brand else 0x66D4AF37); shape = GradientDrawable.OVAL
                }
            })
        }

        // Logo 图标 / 品牌色圆点（有 logo 则显示图标，否则回落原色圆点）
        val logo = logoCache[p.code]
        val circle = if (logo != null) {
            ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(dp(32), dp(32)).apply { bottomMargin = dp(10) }
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageBitmap(logo)
                clipToOutline = true
                addOnLayoutChangeListener(object : android.view.View.OnLayoutChangeListener {
                    override fun onLayoutChange(v: View?, l: Int, t: Int, r: Int, b: Int, ol: Int, ot: Int, or: Int, ob: Int) {
                        v?.outlineProvider = object : android.view.ViewOutlineProvider() {
                            override fun getOutline(view: View, outline: Outline) {
                                outline.setOval(0, 0, view.width, view.height)
                            }
                        }
                        v?.removeOnLayoutChangeListener(this)
                    }
                })
            }
        } else {
            View(this).apply {
                layoutParams = LinearLayout.LayoutParams(dp(32), dp(32)).apply { bottomMargin = dp(10) }
                background = GradientDrawable().apply { setColor(brand); shape = GradientDrawable.OVAL }
            }
        }

        // 名称
        val name = TextView(this).apply {
            text = p.name; textSize = 15f; setTextColor(0xFFF7F2FF.toInt())
            setTypeface(null, Typeface.BOLD); gravity = Gravity.CENTER; maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            setPadding(dp(4), 0, dp(4), 0)
        }

        // 标签
        val tag = TextView(this).apply {
            text = typeLabels[p.type] ?: ""; textSize = 11f; setTextColor(0xFFF1D77A.toInt())
            setPadding(dp(10), dp(4), dp(10), dp(4))
            layoutParams = LinearLayout.LayoutParams(WRAP, WRAP).apply { topMargin = dp(8) }
            background = GradientDrawable().apply {
                setColor(0x18D4AF37); cornerRadius = dp(8).toFloat()
            }
        }

        card.addView(dots); card.addView(circle); card.addView(name); card.addView(tag)
        return card
    }

    private fun openPlatform(p: Platform) {
        try {
            homeContainer?.visibility = View.GONE
            browserContainer?.visibility = View.VISIBLE
            backBtn?.visibility = View.VISIBLE
            currentTitle = p.name; titleText?.text = p.name
            webView?.loadUrl(p.url)
        } catch (_: Exception) { }
    }

    private fun showHome() {
        try {
            browserContainer?.visibility = View.GONE
            homeContainer?.visibility = View.VISIBLE
            webView?.stopLoading(); webView?.loadUrl("about:blank")
        } catch (_: Exception) { }
    }

    private fun showError(msg: String) {
        setContentView(TextView(this).apply {
            text = msg; setPadding(32, 32, 32, 32)
            textSize = 16f; setTextColor(0xFFE94560.toInt())
            setBackgroundColor(0xFF1A0A2E.toInt())
        })
    }

    private fun toast(msg: String) {
        try { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() } catch (_: Exception) { }
    }

    private fun dp(v: Int): Int = (v * dp1 + 0.5f).toInt()

    override fun onBackPressed() {
        if (browserContainer?.visibility == View.VISIBLE) {
            try {
                if (webView?.canGoBack() == true) webView?.goBack() else showHome()
            } catch (_: Exception) { showHome() }
        } else super.onBackPressed()
    }

    override fun onDestroy() {
        try { unregisterReceiver(downloadReceiver) } catch (_: Exception) {}
        try { webView?.destroy() } catch (_: Exception) { }
        super.onDestroy()
    }

    // ====== 版本更新检查 ======
    private fun checkUpdate() {
        Thread {
            try {
                val conn = URL("$API_BASE/api/v1/client/version").openConnection() as HttpURLConnection
                conn.connectTimeout = 8000; conn.readTimeout = 8000
                val json = JSONObject(conn.inputStream.reader().readText())
                val data = json.optJSONObject("data") ?: return@Thread
                val serverCode = data.optInt("versionCode", 0)
                val serverName = data.optString("versionName", "")
                val downloadUrl = data.optString("downloadUrl", "")
                val notes = data.optString("releaseNotes", "")
                val force = data.optBoolean("forceUpdate", false)
                conn.disconnect()

                val localCode = packageManager.getPackageInfo(packageName, 0).versionCode
                if (serverCode > localCode && downloadUrl.isNotEmpty()) {
                    runOnUiThread { showUpdateDialog(serverName, notes, downloadUrl, force) }
                }
            } catch (_: Exception) { /* 版本检查静默失败 */ }
        }.start()
    }

    private fun showUpdateDialog(vName: String, notes: String, url: String, force: Boolean) {
        val msg = "发现新版本 $vName\n\n$notes"
        AlertDialog.Builder(this)
            .setTitle("版本更新")
            .setMessage(msg)
            .setCancelable(!force)
            .setPositiveButton("立即更新") { _, _ -> downloadAndInstall(url, vName) }
            .apply { if (!force) setNegativeButton("稍后", null) }
            .show()
    }

    private fun downloadAndInstall(url: String, vName: String) {
        try {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "mini-video-$vName.apk")
            if (file.exists()) file.delete()

            val req = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle("迷你视频更新")
                setDescription("正在下载 $vName ...")
                setDestinationUri(Uri.fromFile(file))
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadId = dm.enqueue(req)
            toast("开始下载更新...")
        } catch (e: Exception) {
            toast("下载失败: ${e.message}")
        }
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id != downloadId) return
            try {
                val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = dm.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val uri = dm.getUriForDownloadedFile(id)
                        installApk(uri)
                    } else {
                        toast("下载失败")
                    }
                }
                cursor.close()
            } catch (e: Exception) {
                toast("安装准备失败: ${e.message}")
            }
        }
    }

    private fun installApk(uri: Uri) {
        try {
            // Android 8+ 需要检查安装未知来源权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                !packageManager.canRequestPackageInstalls()) {
                toast("请允许安装未知应用后重试")
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse("package:$packageName"))
                startActivity(intent)
                return
            }

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (uri.scheme == "content") {
                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
                } else {
                    val apkUri = FileProvider.getUriForFile(this,
                        "${packageName}.fileprovider",
                        File(uri.path ?: return))
                    installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                }
            } else {
                installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
            }
            startActivity(installIntent)
        } catch (e: Exception) {
            toast("安装失败: ${e.message}")
        }
    }

    companion object {
        private const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        private const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}
