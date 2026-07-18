package com.video.entitlement

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    private var backBtn: TextView? = null
    private var homeContainer: View? = null
    private var browserContainer: View? = null
    private var platformContainer: LinearLayout? = null

    private var currentUrl = ""
    private var currentTitle = ""
    private var vipApis = mutableListOf<VipApi>()
    private var currentVipIdx = -1
    private var usingVip = false
    private var originalUrl = ""

    private val dp1 get() = resources.displayMetrics.density

    private val platformIcons = mapOf(
        "iqiyi" to "\uD83D\uDFE2", "tencent" to "\uD83D\uDD35", "mgtv" to "\uD83D\uDFE0",
        "bilibili" to "\uD83D\uDFE3", "youku" to "\uD83D\uDD34",
        "1905" to "\uD83C\uDFAC", "ixigua" to "\uD83C\uDF49", "wangyiyun" to "\uD83D\uDD34\u26AA",
        "qqmusic" to "\uD83C\uDFB5", "kugou" to "\uD83C\uDFB6", "cctv" to "\uD83D\uDCFA",
        "meiju" to "\uD83C\uDDFA\uD83C\uDDF8", "hanju" to "\uD83C\uDDF0\uD83C\uDDF7"
    )

    private val defaultPlatforms = listOf(
        Platform("爱奇艺", "https://m.iqiyi.com/", "video", "\uD83D\uDFE2"),
        Platform("腾讯视频", "https://m.qq.com/", "video", "\uD83D\uDD35"),
        Platform("芒果TV", "https://m.mgtv.com/", "video", "\uD83D\uDFE0"),
        Platform("哔哩哔哩", "https://m.bilibili.com/", "video", "\uD83D\uDFE3"),
        Platform("优酷", "https://m.youku.com/", "video", "\uD83D\uDD34"),
        Platform("1905电影", "https://vip.1905.com/", "video", "\uD83C\uDFAC"),
        Platform("西瓜视频", "https://m.ixigua.com/", "video", "\uD83C\uDF49"),
        Platform("网易云音乐", "https://m.music.163.com/", "music", "\uD83D\uDD34\u26AA"),
        Platform("QQ音乐", "https://m.y.qq.com/", "music", "\uD83C\uDFB5"),
        Platform("酷狗音乐", "https://m.kugou.com/", "music", "\uD83C\uDFB6"),
        Platform("CCTV直播", "https://tv.cctv.com/live/", "tv", "\uD83D\uDCFA"),
        Platform("美剧", "https://mjw21.com/", "drama", "\uD83C\uDDFA\uD83C\uDDF8"),
        Platform("韩剧", "https://www.kan.cc/", "drama", "\uD83C\uDDF0\uD83C\uDDF7"),
    )

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
            loadPlatforms()
            updateStats()
        } catch (e: Exception) {
            setContentView(TextView(this).apply {
                text = "启动失败: ${e.message}"
                setPadding(32, 32, 32, 32)
                textSize = 16f
                setTextColor(0xFFFF0000.toInt())
            })
        }
    }

    private fun initViews() {
        homeContainer = findViewById(R.id.home_container)
        browserContainer = findViewById(R.id.browser_container)
        titleText = findViewById(R.id.title_text)
        progressBar = findViewById(R.id.progress_bar)
        vipBtn = findViewById(R.id.vip_btn)
        nextBtn = findViewById(R.id.next_btn)
        backBtn = findViewById(R.id.back_btn)
        platformContainer = findViewById(R.id.platform_container)

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
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val wv = webView ?: return
        try {
            // 调试模式
            WebView.setWebContentsDebuggingEnabled(true)

            wv.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                allowFileAccess = true
                allowContentAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
                // 伪装桌面浏览器 UA，避免被视频站拦截
                userAgentString = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36"
                setGeolocationEnabled(true)
                setSupportMultipleWindows(false)
            }

            wv.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar?.visibility = View.VISIBLE
                    if (url != null) currentUrl = url
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar?.visibility = View.GONE
                    if (url != null && !usingVip) originalUrl = url
                    if (!usingVip) titleText?.text = view?.title ?: currentTitle
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return false // 让 WebView 自己处理所有 URL
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    progressBar?.visibility = View.GONE
                    val msg = error?.description?.toString() ?: "加载失败"
                    titleText?.text = "⚠ $msg"
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
            }
        } catch (e: Exception) {
            toast("WebView初始化失败: ${e.message}")
        }
    }

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
            runOnUiThread { vipBtn?.text = "VIP·${vipApis.size}" }
        }.start()
    }

    private fun loadPlatforms() {
        val c = platformContainer ?: return
        renderPlatforms(c, defaultPlatforms)
        Thread {
            try {
                val conn = URL("http://43.161.222.78:8081/api/v1/client/platforms").openConnection() as HttpURLConnection
                conn.connectTimeout = 5000; conn.readTimeout = 5000
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
                    conn.disconnect()
                    runOnUiThread { renderPlatforms(c, list) }
                }
            } catch (_: Exception) { }
        }.start()
    }

    private fun guessType(code: String): String = when {
        code in listOf("wangyiyun", "qqmusic", "kugou") -> "music"
        code == "cctv" -> "tv"
        code in listOf("meiju", "hanju") -> "drama"
        else -> "video"
    }

    private fun updateStats() {
        val vc = defaultPlatforms.count { it.type == "video" }
        val mc = defaultPlatforms.count { it.type == "music" }
        (findViewById<TextView>(R.id.stat_platforms))?.text = "$vc"
        (findViewById<TextView>(R.id.stat_vip))?.text = "${defaultVipApis.size}"
        (findViewById<TextView>(R.id.stat_music))?.text = "$mc"
    }

    // ====== 核心：现代卡片UI构建 ======
    private fun renderPlatforms(container: LinearLayout, platforms: List<Platform>) {
        try {
            container.removeAllViews()
            for ((type, label) in typeLabels) {
                val group = platforms.filter { it.type == type }
                if (group.isEmpty()) continue
                container.addView(buildSectionHeader(label))
                container.addView(buildCardGrid(group, type))
            }
        } catch (e: Exception) {
            toast("加载失败: ${e.message}")
        }
    }

    private fun buildSectionHeader(label: String): TextView {
        return TextView(this).apply {
            text = label
            setPadding(0, dp(20), 0, dp(10))
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
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(16), dp(14), dp(16))
            gravity = Gravity.CENTER
            setBackgroundColor(0xFFFFFFFF.toInt())
            elevation = dp(1).toFloat()
            clipToPadding = false
            // rounded corners
            val shape = GradientDrawable().apply {
                setColor(0xFFFFFFFF.toInt())
                cornerRadius = dp(12).toFloat()
                setStroke(1, bgLight)
            }
            background = shape
            setOnClickListener { openPlatform(p) }
        }

        // icon circle
        val iconBg = GradientDrawable().apply {
            setColor(bgLight); shape = GradientDrawable.OVAL
        }
        val icon = TextView(this).apply {
            text = p.icon; textSize = 24f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48)).apply {
                bottomMargin = dp(10)
            }
            background = iconBg
        }

        val name = TextView(this).apply {
            text = p.name; textSize = 13f
            setTextColor(0xFF333333.toInt())
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER; maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val tag = TextView(this).apply {
            text = typeLabels[type] ?: ""; textSize = 10f
            setTextColor(bgColor)
            setPadding(dp(8), dp(2), dp(8), dp(2))
            layoutParams = LinearLayout.LayoutParams(WRAP, WRAP).apply { topMargin = dp(6) }
            background = GradientDrawable().apply {
                setColor(bgLight); cornerRadius = dp(8).toFloat()
            }
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
        } catch (e: Exception) { toast("打开失败") }
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
            vipBtn?.text = "VIP·${currentVipIdx + 1}"
            webView?.loadUrl(api.url + target)
            toast(api.name)
        } catch (e: Exception) { toast("解析失败") }
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
