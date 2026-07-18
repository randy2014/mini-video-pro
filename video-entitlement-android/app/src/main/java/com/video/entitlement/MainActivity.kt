package com.video.entitlement

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

// ========== 数据模型 ==========
data class Platform(val name: String, val url: String, val type: String)
data class VipApi(val name: String, val url: String)

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var titleText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var vipBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var backBtn: ImageView
    private lateinit var homeContainer: LinearLayout
    private lateinit var browserContainer: LinearLayout

    private var currentUrl = ""
    private var currentTitle = ""
    private var vipApis = mutableListOf<VipApi>()
    private var currentVipIdx = -1
    private var usingVip = false
    private var originalUrl = ""

    // ========== 平台数据（本地默认 + 后端覆盖） ==========
    private val defaultPlatforms = listOf(
        // 视频网站
        Platform("爱奇艺", "https://m.iqiyi.com/", "video"),
        Platform("腾讯视频", "https://m.qq.com/", "video"),
        Platform("芒果TV", "https://m.mgtv.com/", "video"),
        Platform("哔哩哔哩", "https://m.bilibili.com/", "video"),
        Platform("优酷", "https://m.youku.com/", "video"),
        Platform("1905电影", "https://vip.1905.com/", "video"),
        Platform("西瓜视频", "https://m.ixigua.com/", "video"),
        // 音乐
        Platform("网易云音乐", "https://m.music.163.com/", "music"),
        Platform("QQ音乐", "https://m.y.qq.com/", "music"),
        Platform("酷狗音乐", "https://m.kugou.com/", "music"),
        // 电视台
        Platform("CCTV直播", "https://tv.cctv.com/live/", "tv"),
        // 影视剧
        Platform("美剧", "https://mjw21.com/", "drama"),
        Platform("韩剧", "https://www.kan.cc/", "drama"),
    )

    // ========== 默认VIP解析源 ==========
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // find views
        homeContainer = findViewById(R.id.home_container)
        browserContainer = findViewById(R.id.browser_container)
        titleText = findViewById(R.id.title_text)
        progressBar = findViewById(R.id.progress_bar)
        vipBtn = findViewById(R.id.vip_btn)
        nextBtn = findViewById(R.id.next_btn)
        backBtn = findViewById(R.id.back_btn)
        webView = findViewById(R.id.web_view)

        // WebView setup
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.visibility = ProgressBar.VISIBLE
                if (url != null) currentUrl = url
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = ProgressBar.GONE
                if (url != null && !usingVip) originalUrl = url
                if (!usingVip) titleText.text = view?.title ?: currentTitle
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                if (title != null && !usingVip) titleText.text = title
            }
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
            }
        }

        // 后退按钮
        backBtn.setOnClickListener {
            if (browserContainer.visibility == android.view.View.VISIBLE) {
                showHome()
            }
        }

        // VIP按钮 - 切换VIP解析
        vipBtn.setOnClickListener {
            if (vipApis.isEmpty()) {
                Toast.makeText(this, "没有解析线路", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentVipIdx < 0) currentVipIdx = 0
            applyVipApi()
        }

        // 下一条线路
        nextBtn.setOnClickListener {
            if (vipApis.isEmpty()) return@setOnClickListener
            currentVipIdx = (currentVipIdx + 1) % vipApis.size
            applyVipApi()
        }

        // 加载VIP解析源
        loadVipApis()
        // 加载平台列表
        loadPlatforms()
    }

    // ========== 加载VIP解析源（远程+本地） ==========
    private fun loadVipApis() {
        thread {
            try {
                val conn = URL("https://iodefog.github.io/text/viplist.json").openConnection() as HttpURLConnection
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                val json = JSONObject(conn.inputStream.reader().readText())
                val vips = json.optJSONArray("vips")
                if (vips != null && vips.length() > 0) {
                    vipApis.clear()
                    for (i in 0 until vips.length()) {
                        val item = vips.getJSONObject(i)
                        vipApis.add(VipApi(item.getString("name"), item.getString("url")))
                    }
                }
            } catch (_: Exception) {
                vipApis.addAll(defaultVipApis)
            }
            if (vipApis.isEmpty()) vipApis.addAll(defaultVipApis)
            runOnUiThread {
                vipBtn.text = "VIP (${vipApis.size}条线路)"
            }
        }
    }

    // ========== 加载平台（尝试后端API，失败用本地） ==========
    private fun loadPlatforms() {
        val container = findViewById<LinearLayout>(R.id.platform_container)
        // 先用默认平台渲染
        renderPlatforms(container, defaultPlatforms)
        // 尝试加载后端
        thread {
            try {
                val conn = URL("http://43.161.222.78:8081/api/v1/client/platforms").openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                val json = JSONObject(conn.inputStream.reader().readText())
                val data = json.optJSONArray("data")
                if (data != null && data.length() > 0) {
                    val list = mutableListOf<Platform>()
                    for (i in 0 until data.length()) {
                        val p = data.getJSONObject(i)
                        list.add(Platform(p.getString("platformName"), p.getString("homeUrl"), "video"))
                    }
                    runOnUiThread { renderPlatforms(container, list) }
                }
            } catch (_: Exception) {}
        }
    }

    private fun renderPlatforms(container: LinearLayout, platforms: List<Platform>) {
        container.removeAllViews()
        val types = linkedMapOf("video" to "视频网站", "music" to "音乐平台", "tv" to "电视直播", "drama" to "影视剧")
        for ((type, label) in types) {
            val group = platforms.filter { it.type == type }
            if (group.isEmpty()) continue
            val tv = TextView(this).apply {
                text = label
                setPadding(32, 24, 0, 8)
                textSize = 15f
                setTextColor(0xFF666666.toInt())
            }
            container.addView(tv)
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(16, 0, 16, 0) }
            val flow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            for (p in group) {
                val card = TextView(this).apply {
                    text = p.name
                    setPadding(24, 12, 24, 12)
                    textSize = 14f
                    setBackgroundColor(0xFFF0F0F0.toInt())
                    setOnClickListener { openPlatform(p) }
                }
                (card.layoutParams as? LinearLayout.LayoutParams)?.setMargins(0, 0, 16, 16)
                flow.addView(card)
            }
            row.addView(flow)
            container.addView(row)
        }
    }

    private fun openPlatform(p: Platform) {
        homeContainer.visibility = android.view.View.GONE
        browserContainer.visibility = android.view.View.VISIBLE
        backBtn.visibility = android.view.View.VISIBLE
        currentTitle = p.name
        titleText.text = p.name
        currentVipIdx = -1
        usingVip = false
        originalUrl = p.url
        webView.loadUrl(p.url)
    }

    private fun showHome() {
        browserContainer.visibility = android.view.View.GONE
        homeContainer.visibility = android.view.View.VISIBLE
        webView.stopLoading()
        webView.loadUrl("about:blank")
        currentVipIdx = -1
        usingVip = false
    }

    private fun applyVipApi() {
        if (vipApis.isEmpty() || currentVipIdx < 0) return
        val api = vipApis[currentVipIdx]
        val targetUrl = originalUrl.ifEmpty { currentUrl }
        val vipUrl = api.url + targetUrl
        usingVip = true
        titleText.text = "VIP: ${api.name} (${currentVipIdx + 1}/${vipApis.size})"
        webView.loadUrl(vipUrl)
        Toast.makeText(this, "切换: ${api.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (browserContainer.visibility == android.view.View.VISIBLE) {
            if (webView.canGoBack()) webView.goBack() else showHome()
        } else super.onBackPressed()
    }
}
