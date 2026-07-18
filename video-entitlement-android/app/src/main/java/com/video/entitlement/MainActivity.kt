package com.video.entitlement

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Platform(val name: String, val url: String, val type: String, val code: String)

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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // 全屏沉浸式：内容延伸到状态栏和导航栏
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)
            initViews()
            showVersion()
            setupWebView()
            fetchPlatforms()
        } catch (e: Exception) {
            showError("启动失败: ${e.message}")
        }
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
                setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
            }
        }
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
                        list.add(Platform(p.getString("platformName"), p.getString("homeUrl"), guessType(code), code))
                    }
                    platforms = list; ok = true
                }
                conn.disconnect()
            } catch (_: Exception) { }

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
            setPadding(0, dp(24), 0, dp(14))
        }
        val dot = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(6), dp(6)).apply { marginEnd = dp(8) }
            background = GradientDrawable().apply {
                setColor(0xFFD4AF37.toInt()); shape = GradientDrawable.OVAL
            }
        }
        val tv = TextView(this).apply {
            text = label; textSize = 15f; setTypeface(null, Typeface.BOLD)
            setTextColor(0xFFD4AF37.toInt())
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
            setPadding(dp(20), dp(22), dp(20), dp(22))
            background = GradientDrawable().apply {
                setColor(0xFF1E1035.toInt()); cornerRadius = dp(16).toFloat()
                setStroke(1, 0x22D4AF37)
            }
            setOnClickListener { openPlatform(p) }
        }

        // 顶部三点装饰
        val dots = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(WRAP, WRAP).apply { bottomMargin = dp(14) }
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

        // 品牌色圆点
        val circle = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply { bottomMargin = dp(12) }
            background = GradientDrawable().apply { setColor(brand); shape = GradientDrawable.OVAL }
        }

        // 名称
        val name = TextView(this).apply {
            text = p.name; textSize = 13f; setTextColor(0xFFF0E8FF.toInt())
            setTypeface(null, Typeface.BOLD); gravity = Gravity.CENTER; maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        // 标签
        val tag = TextView(this).apply {
            text = typeLabels[p.type] ?: ""; textSize = 10f; setTextColor(0xFFD4AF37.toInt())
            setPadding(dp(8), dp(3), dp(8), dp(3))
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
        try { webView?.destroy() } catch (_: Exception) { }
        super.onDestroy()
    }

    companion object {
        private const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        private const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}
