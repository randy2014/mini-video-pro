package com.video.entitlement

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Platform(val name: String, val url: String, val type: String)
data class VipApi(val name: String, val url: String)

class MainActivity : AppCompatActivity() {

    private var webView: WebView? = null
    private var titleText: TextView? = null
    private var progressBar: ProgressBar? = null
    private var vipBtn: Button? = null
    private var nextBtn: Button? = null
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

    private val defaultPlatforms = listOf(
        Platform("爱奇艺", "https://m.iqiyi.com/", "video"),
        Platform("腾讯视频", "https://m.qq.com/", "video"),
        Platform("芒果TV", "https://m.mgtv.com/", "video"),
        Platform("哔哩哔哩", "https://m.bilibili.com/", "video"),
        Platform("优酷", "https://m.youku.com/", "video"),
        Platform("1905电影", "https://vip.1905.com/", "video"),
        Platform("西瓜视频", "https://m.ixigua.com/", "video"),
        Platform("网易云音乐", "https://m.music.163.com/", "music"),
        Platform("QQ音乐", "https://m.y.qq.com/", "music"),
        Platform("酷狗音乐", "https://m.kugou.com/", "music"),
        Platform("CCTV直播", "https://tv.cctv.com/live/", "tv"),
        Platform("美剧", "https://mjw21.com/", "drama"),
        Platform("韩剧", "https://www.kan.cc/", "drama"),
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            initViews()
            setupWebView()
            loadVipApis()
            loadPlatforms()
        } catch (e: Exception) {
            showError("启动失败: ${e.message}")
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
        webView = findViewById(R.id.web_view)

        backBtn?.setOnClickListener {
            if (browserContainer?.visibility == View.VISIBLE) showHome()
        }

        vipBtn?.setOnClickListener {
            if (vipApis.isEmpty()) {
                toast("没有解析线路")
                return@setOnClickListener
            }
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
            wv.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
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
            }

            wv.webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    if (title != null && !usingVip) titleText?.text = title
                }
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progressBar?.progress = newProgress
                }
            }
        } catch (e: Exception) {
            showError("WebView初始化失败: ${e.message}")
        }
    }

    private fun loadVipApis() {
        Thread {
            try {
                val conn = URL("https://iodefog.github.io/text/viplist.json").openConnection() as HttpURLConnection
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
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

            runOnUiThread {
                vipBtn?.text = "VIP (${vipApis.size}条线路)"
            }
        }.start()
    }

    private fun loadPlatforms() {
        val container = platformContainer ?: return
        renderPlatforms(container, defaultPlatforms)

        Thread {
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
                    conn.disconnect()
                    runOnUiThread { renderPlatforms(container, list) }
                }
            } catch (_: Exception) { }
        }.start()
    }

    private fun renderPlatforms(container: LinearLayout, platforms: List<Platform>) {
        try {
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

                val flow = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(16, 0, 16, 0)
                }

                for (p in group) {
                    val card = TextView(this).apply {
                        text = p.name
                        setPadding(24, 12, 24, 12)
                        textSize = 14f
                        setBackgroundColor(0xFFF0F0F0.toInt())
                        setOnClickListener { openPlatform(p) }
                    }
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(0, 0, 16, 16)
                    flow.addView(card, lp)
                }
                container.addView(flow)
            }
        } catch (e: Exception) {
            toast("加载平台失败: ${e.message}")
        }
    }

    private fun openPlatform(p: Platform) {
        try {
            homeContainer?.visibility = View.GONE
            browserContainer?.visibility = View.VISIBLE
            backBtn?.visibility = View.VISIBLE
            currentTitle = p.name
            titleText?.text = p.name
            currentVipIdx = -1
            usingVip = false
            originalUrl = p.url
            webView?.loadUrl(p.url)
        } catch (e: Exception) {
            toast("打开失败: ${e.message}")
        }
    }

    private fun showHome() {
        try {
            browserContainer?.visibility = View.GONE
            homeContainer?.visibility = View.VISIBLE
            webView?.stopLoading()
            webView?.loadUrl("about:blank")
            currentVipIdx = -1
            usingVip = false
        } catch (_: Exception) { }
    }

    private fun applyVipApi() {
        if (vipApis.isEmpty() || currentVipIdx < 0) return
        try {
            val api = vipApis[currentVipIdx]
            val targetUrl = originalUrl.ifEmpty { currentUrl }
            val vipUrl = api.url + targetUrl
            usingVip = true
            titleText?.text = "VIP: ${api.name} (${currentVipIdx + 1}/${vipApis.size})"
            webView?.loadUrl(vipUrl)
            toast("切换: ${api.name}")
        } catch (e: Exception) {
            toast("解析失败: ${e.message}")
        }
    }

    private fun showError(msg: String) {
        try {
            val tv = TextView(this).apply {
                text = "错误: $msg"
                setPadding(32, 32, 32, 32)
                textSize = 16f
                setTextColor(0xFFFF0000.toInt())
            }
            setContentView(tv)
        } catch (_: Exception) { }
    }

    private fun toast(msg: String) {
        try {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) { }
    }

    override fun onBackPressed() {
        if (browserContainer?.visibility == View.VISIBLE) {
            try {
                if (webView?.canGoBack() == true) webView?.goBack() else showHome()
            } catch (_: Exception) {
                showHome()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        try {
            webView?.destroy()
        } catch (_: Exception) { }
        super.onDestroy()
    }
}
