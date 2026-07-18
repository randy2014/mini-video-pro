package com.video.entitlement

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.video.entitlement.player.VideoPlayerActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Platform(val name: String, val url: String, val type: String, val icon: String)
data class VipApi(val name: String, val url: String)

class MainActivity : AppCompatActivity() {

    private var webView: WebView? = null
    private var titleText: TextView? = null
    private var progressBar: ProgressBar? = null
    private var vipBtn: TextView? = null
    private var nextBtn: TextView? = null
    private var playBtn: TextView? = null
    private var backBtn: TextView? = null
    private var homeContainer: View? = null
    private var browserContainer: View? = null
    private var platformContainer: LinearLayout? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingOverlay: View? = null
    private var loadingText: TextView? = null

    private var currentUrl = ""
    private var currentTitle = ""
    private var vipApis = mutableListOf<VipApi>()
    private var currentVipIdx = -1
    private var usingVip = false
    private var originalUrl = ""

    private val dp1 get() = resources.displayMetrics.density
    private val API_BASE = "http://43.161.222.78:8081"

    // 平台图标映射 (后端返回platformCode对应图标)
    private val platformIcons = mapOf(
        "iqiyi" to "\uD83D\uDFE2", "tencent" to "\uD83D\uDD35", "mgtv" to "\uD83D\uDFE0",
        "bilibili" to "\uD83D\uDFE3", "youku" to "\uD83D\uDD34",
        "1905" to "\uD83C\uDFAC", "ixigua" to "\uD83C\uDF49", "wangyiyun" to "\uD83D\uDD34\u26AA",
        "qqmusic" to "\uD83C\uDFB5", "kugou" to "\uD83C\uDFB6", "cctv" to "\uD83D\uDCFA",
        "meiju" to "\uD83C\uDDFA\uD83C\uDDF8", "hanju" to "\uD83C\uDDF0\uD83C\uDDF7"
    )

    // VIP解析源 (后端没接口，保留本地默认)
    private val defaultVipApis = listOf(
        VipApi("线路1", "https://jx.m3u8.tv/jiexi/?url="),
        VipApi("线路2", "https://jx.xmflv.com/?url="),
        VipApi("线路3", "https://jx.aidouer.net/?url="),
        VipApi("线路4", "https://jx.bozrc.com:4433/player/?url="),
        VipApi("线路5", "https://jx.jsonplayer.com/player/?url="),
        VipApi("线路6", "https://jx.mm58.top/jx/api.php?url="),
        VipApi("线路7", "https://www.yemu.xyz/?url="),
        VipApi("线路8", "https://jx.nnxv.cn/tv.php?url="),
        VipApi("线路9", "https://vip.ffzy-play6.com/?url="),
        VipApi("线路10", "https://jx.playerjy.com/?url="),
    )

    private val typeLabels = mapOf(
        "video" to "视频网站", "music" to "音乐平台", "tv" to "电视直播", "drama" to "影视剧"
    )
    private val typeColors = mapOf(
        "video" to 0xFF1677FF.toInt(), "music" to 0xFF52C41A.toInt(),
        "tv" to 0xFFFA8C16.toInt(), "drama" to 0xFF722ED1.toInt()
    )
    private val typeBgColors = mapOf(
        "video" to 0xFFE6F0FF.toInt(), "music" to 0xFFF0FFE6.toInt(),
        "tv" to 0xFFFFF5E6.toInt(), "drama" to 0xFFF5E6FF.toInt()
    )

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            initViews()
            setupWebView()
            loadVipApis()
            // 从 API 加载平台数据 (无硬编码)
            fetchPlatforms()
        } catch (e: Exception) {
            showError("启动失败: ${e.message}")
        }
    }

    private fun initViews() {
        homeContainer = findViewById(R.id.swipe_refresh)
        browserContainer = findViewById(R.id.browser_container)
        titleText = findViewById(R.id.title_text)
        progressBar = findViewById(R.id.progress_bar)
        vipBtn = findViewById(R.id.vip_btn)
        nextBtn = findViewById(R.id.next_btn)
        playBtn = findViewById(R.id.play_btn)
        backBtn = findViewById(R.id.back_btn)
        platformContainer = findViewById(R.id.platform_container)
        webView = findViewById(R.id.web_view)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        loadingOverlay = findViewById(R.id.loading_overlay)
        loadingText = findViewById(R.id.loading_text)

        // 下拉刷新
        swipeRefresh?.setOnRefreshListener { fetchPlatforms() }
        swipeRefresh?.setColorSchemeColors(0xFF1677FF.toInt())

        backBtn?.setOnClickListener {
            if (browserContainer?.visibility == View.VISIBLE) showHome()
        }
        vipBtn?.setOnClickListener {
            if (vipApis.isEmpty()) { toast("没有解析线路"); return@setOnClickListener }
            if (currentVipIdx < 0) currentVipIdx = 0
            applyVipApi()
        }
        nextBtn?.setOnClickListener {
            if (vipApis.isEmpty()) return@setOnClickListener
            currentVipIdx = (currentVipIdx + 1) % vipApis.size
            applyVipApi()
        }
        playBtn?.setOnClickListener { openInPlayer() }
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
                setSupportZoom(true); builtInZoomControls = true
                displayZoomControls = false
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
                    if (url != null && !usingVip) originalUrl = url
                    if (!usingVip) titleText?.text = view?.title ?: currentTitle
                }
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    progressBar?.visibility = View.GONE
                    titleText?.text = "\u26A0 ${error?.description ?: "加载失败"}"
                }
            }
            wv.webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    if (title != null && !usingVip) titleText?.text = title
                }
                override fun onProgressChanged(view: WebView?, p: Int) {
                    progressBar?.progress = p
                    if (p == 100) progressBar?.visibility = View.GONE
                }
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    callback?.onCustomViewHidden()
                    playBtn?.visibility = View.VISIBLE
                }
            }
        } catch (_: Exception) { }
    }

    // ====== 从 API 加载平台数据 ======
    private fun fetchPlatforms() {
        showLoading(true)
        Thread {
            var platforms: List<Platform> = emptyList()
            var success = false
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
                        val icon = platformIcons[code] ?: "\uD83D\uDCF1"
                        list.add(Platform(p.getString("platformName"), p.getString("homeUrl"), guessType(code), icon))
                    }
                    platforms = list
                    success = true
                }
                conn.disconnect()
            } catch (e: Exception) {
                platforms = emptyList()
            }

            runOnUiThread {
                showLoading(false)
                swipeRefresh?.isRefreshing = false
                if (success && platforms.isNotEmpty()) {
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
        if (show) {
            loadingOverlay?.visibility = View.VISIBLE
            loadingText?.text = "加载中..."
        } else {
            loadingOverlay?.visibility = View.GONE
        }
    }

    private fun updateStats(platforms: List<Platform>) {
        val vc = platforms.count { it.type == "video" }
        val mc = platforms.count { it.type == "music" }
        findViewById<TextView>(R.id.stat_platforms)?.text = "$vc"
        findViewById<TextView>(R.id.stat_vip)?.text = "${vipApis.size}"
        findViewById<TextView>(R.id.stat_music)?.text = "$mc"
    }

    private fun guessType(code: String): String = when {
        code in listOf("wangyiyun", "qqmusic", "kugou") -> "music"
        code == "cctv" -> "tv"
        code in listOf("meiju", "hanju") -> "drama"
        else -> "video"
    }

    // ====== VIP加载 ======
    private fun loadVipApis() {
        Thread {
            try {
                val conn = URL("https://iodefog.github.io/text/viplist.json").openConnection() as HttpURLConnection
                conn.connectTimeout = 8000; conn.readTimeout = 8000
                val json = JSONObject(conn.inputStream.reader().readText())
                val vips = json.optJSONArray("vips")
                if (vips != null && vips.length() > 0) {
                    vipApis.clear()
                    for (i in 0 until vips.length()) {
                        val item = vips.getJSONObject(i)
                        vipApis.add(VipApi(item.getString("name"), item.getString("url")))
                    }
                }
                conn.disconnect()
            } catch (_: Exception) { }
            if (vipApis.isEmpty()) vipApis.addAll(defaultVipApis)
            runOnUiThread { vipBtn?.text = "VIP\u00B7${vipApis.size}" }
        }.start()
    }

    // ====== UI构建 ======
    private fun renderPlatforms(container: LinearLayout, platforms: List<Platform>) {
        try {
            container.removeAllViews()
            for ((type, label) in typeLabels) {
                val group = platforms.filter { it.type == type }
                if (group.isEmpty()) continue
                container.addView(buildSectionHeader(label))
                container.addView(buildCardGrid(group, type))
            }
        } catch (e: Exception) { }
    }

    private fun buildSectionHeader(label: String): TextView {
        return TextView(this).apply {
            text = label; setPadding(0, dp(20), 0, dp(10))
            textSize = 15f; setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF333333.toInt())
        }
    }

    private fun buildCardGrid(platforms: List<Platform>, type: String): LinearLayout {
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
            val card = buildPlatformCard(p, type)
            val lp = LinearLayout.LayoutParams(0, WRAP, 1f)
            lp.setMargins(if (i % 2 == 0) 0 else dp(5), 0, if (i % 2 == 0) dp(5) else 0, dp(10))
            row?.addView(card, lp)
        }
        return grid
    }

    private fun buildPlatformCard(p: Platform, type: String): LinearLayout {
        val bgColor = typeColors[type] ?: 0xFF1677FF.toInt()
        val bgLight = typeBgColors[type] ?: 0xFFE6F0FF.toInt()
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(dp(14), dp(16), dp(14), dp(16))
            gravity = Gravity.CENTER; setBackgroundColor(0xFFFFFFFF.toInt())
            elevation = dp(1).toFloat(); clipToPadding = false
            background = GradientDrawable().apply {
                setColor(0xFFFFFFFF.toInt()); cornerRadius = dp(12).toFloat(); setStroke(1, bgLight)
            }
            setOnClickListener { openPlatform(p) }
        }
        val iconBg = GradientDrawable().apply { setColor(bgLight); shape = GradientDrawable.OVAL }
        val icon = TextView(this).apply {
            text = p.icon; textSize = 24f; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48)).apply { bottomMargin = dp(10) }
            background = iconBg
        }
        val name = TextView(this).apply {
            text = p.name; textSize = 13f; setTextColor(0xFF333333.toInt())
            setTypeface(null, Typeface.BOLD); gravity = Gravity.CENTER; maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
        val tag = TextView(this).apply {
            text = typeLabels[type] ?: ""; textSize = 10f; setTextColor(bgColor)
            setPadding(dp(8), dp(2), dp(8), dp(2))
            layoutParams = LinearLayout.LayoutParams(WRAP, WRAP).apply { topMargin = dp(6) }
            background = GradientDrawable().apply { setColor(bgLight); cornerRadius = dp(8).toFloat() }
        }
        card.addView(icon); card.addView(name); card.addView(tag)
        return card
    }

    private fun openPlatform(p: Platform) {
        try {
            homeContainer?.visibility = View.GONE
            browserContainer?.visibility = View.VISIBLE
            backBtn?.visibility = View.VISIBLE
            currentTitle = p.name; titleText?.text = p.name
            currentVipIdx = -1; usingVip = false
            originalUrl = p.url; webView?.loadUrl(p.url)
        } catch (_: Exception) { }
    }

    private fun showHome() {
        try {
            browserContainer?.visibility = View.GONE
            homeContainer?.visibility = View.VISIBLE
            webView?.stopLoading(); webView?.loadUrl("about:blank")
            currentVipIdx = -1; usingVip = false
        } catch (_: Exception) { }
    }

    private fun applyVipApi() {
        if (vipApis.isEmpty() || currentVipIdx < 0) return
        try {
            val api = vipApis[currentVipIdx]
            val target = originalUrl.ifEmpty { currentUrl }
            usingVip = true
            titleText?.text = "${api.name} (${currentVipIdx + 1}/${vipApis.size})"
            vipBtn?.text = "VIP\u00B7${currentVipIdx + 1}"
            webView?.loadUrl(api.url + target)
            toast(api.name)
        } catch (_: Exception) { }
    }

    private fun openInPlayer() {
        try {
            val url = currentUrl.ifEmpty { originalUrl }
            if (url.isBlank() || url == "about:blank") { toast("没有可播放的地址"); return }
            startActivity(Intent(this, VideoPlayerActivity::class.java).apply {
                putExtra("url", url)
                putExtra("title", titleText?.text?.toString() ?: "视频播放")
            })
        } catch (_: Exception) { }
    }

    private fun showError(msg: String) {
        setContentView(TextView(this).apply {
            text = msg; setPadding(32, 32, 32, 32); textSize = 16f; setTextColor(0xFFFF0000.toInt())
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
