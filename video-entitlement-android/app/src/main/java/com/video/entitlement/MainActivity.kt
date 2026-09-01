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
import android.text.TextWatcher
import android.text.Editable
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
    private var profileContainer: View? = null
    private var browserContainer: View? = null
    private var platformContainer: LinearLayout? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingOverlay: View? = null
    private var loadingText: TextView? = null
    private var errorPage: View? = null
    private var errorMsg: TextView? = null
    private var retryBtn: TextView? = null
    private var greetingText: TextView? = null
    private var searchInput: EditText? = null
    private var profilePhone: TextView? = null
    private var profileExpiry: TextView? = null
    private var inviteCard: View? = null
    private var inviteCodeText: TextView? = null
    private var copyBtn: TextView? = null
    private var entitlementList: LinearLayout? = null
    private var logoutBtn: TextView? = null
    private var bottomNav: View? = null
    private var navHomeLabel: TextView? = null
    private var navProfileLabel: TextView? = null

    private val tabs = mutableListOf<Pair<String, TextView>>()

    private var currentUrl = ""
    private var currentTitle = ""
    private var downloadId: Long = -1L

    private var allPlatforms: List<Platform> = emptyList()
    private var currentTab = "all"
    private var currentSearch = ""

    private val dp1 get() = resources.displayMetrics.density
    private val API_BASE = "http://64.90.19.6:8081"

    // 品牌色映射（用于圆形 logo 背景）
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
        "video" to "视频", "music" to "音乐", "tv" to "直播", "drama" to "影视"
    )

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val token = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("access_token", null)
            if (token.isNullOrEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }

            setContentView(R.layout.activity_main)
            initViews()
            showVersion()
            setupWebView()
            setupTabs()
            setupSearch()
            setupBottomNav()
            showGreeting()
            checkUpdate()
            registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED)
            fetchPlatforms()
            fetchInviteCode()
            checkEntitlementExpiry()
        } catch (e: Exception) {
            showError("启动失败: ${e.message}")
        }
    }

    private fun showVersion() {
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            findViewById<TextView>(R.id.version_text)?.text = "v${info.versionName ?: "1.0"}"
        } catch (_: Exception) { }
    }

    private fun showGreeting() {
        val mobile = getSharedPreferences("auth", MODE_PRIVATE).getString("mobile", null)
        val masked = if (!mobile.isNullOrEmpty() && mobile.length >= 7) {
            mobile.take(3) + "****" + mobile.takeLast(4)
        } else mobile ?: "用户"
        greetingText?.text = "你好，$masked"
    }

    private fun initViews() {
        homeContainer = findViewById(R.id.swipe_refresh)
        profileContainer = findViewById(R.id.profile_scroll)
        browserContainer = findViewById(R.id.browser_container)
        titleText = findViewById(R.id.title_text)
        progressBar = findViewById(R.id.progress_bar)
        backBtn = findViewById(R.id.back_btn)
        platformContainer = findViewById(R.id.platform_container)
        webView = findViewById(R.id.web_view)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        loadingOverlay = findViewById(R.id.loading_overlay)
        loadingText = findViewById(R.id.loading_text)
        errorPage = findViewById(R.id.error_page)
        errorMsg = findViewById(R.id.error_msg)
        retryBtn = findViewById(R.id.retry_btn)
        greetingText = findViewById(R.id.greeting_text)
        searchInput = findViewById(R.id.search_input)
        profilePhone = findViewById(R.id.profile_phone)
        profileExpiry = findViewById(R.id.profile_expiry)
        inviteCard = findViewById(R.id.invite_card)
        inviteCodeText = findViewById(R.id.invite_code_text)
        copyBtn = findViewById(R.id.copy_btn)
        entitlementList = findViewById(R.id.entitlement_list)
        logoutBtn = findViewById(R.id.logout_btn)
        bottomNav = findViewById(R.id.bottom_nav)
        navHomeLabel = findViewById(R.id.nav_home_label)
        navProfileLabel = findViewById(R.id.nav_profile_label)
        val refreshBtn = findViewById<TextView>(R.id.refresh_btn)

        swipeRefresh?.setOnRefreshListener { fetchPlatforms() }
        swipeRefresh?.setColorSchemeColors(0xFF07C160.toInt())

        backBtn?.setOnClickListener {
            if (browserContainer?.visibility == View.VISIBLE) showHome()
        }
        refreshBtn?.setOnClickListener { webView?.reload() }
        retryBtn?.setOnClickListener {
            errorPage?.visibility = View.GONE
            webView?.visibility = View.VISIBLE
            webView?.reload()
        }
        logoutBtn?.setOnClickListener { doLogout() }
        copyBtn?.setOnClickListener {
            val code = inviteCodeText?.text?.toString() ?: return@setOnClickListener
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("invite", code))
            toast("邀请码已复制: $code")
        }
    }

    private fun setupTabs() {
        tabs.clear()
        tabs.add("all" to findViewById(R.id.tab_all))
        tabs.add("video" to findViewById(R.id.tab_video))
        tabs.add("music" to findViewById(R.id.tab_music))
        tabs.add("tv" to findViewById(R.id.tab_tv))
        tabs.add("drama" to findViewById(R.id.tab_drama))
        for ((key, tv) in tabs) {
            tv.setOnClickListener { selectTab(key) }
        }
        selectTab("all")
    }

    private fun selectTab(key: String) {
        currentTab = key
        for ((k, tv) in tabs) {
            val selected = k == key
            tv.background = GradientDrawable().apply {
                setColor(if (selected) 0xFF07C160.toInt() else 0xFFFFFFFF.toInt())
                cornerRadius = dp(15).toFloat()
            }
            tv.setTextColor(if (selected) 0xFFFFFFFF.toInt() else 0xFF888888.toInt())
        }
        renderPlatforms()
    }

    private fun setupSearch() {
        searchInput?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentSearch = s?.toString() ?: ""
                renderPlatforms()
            }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })
    }

    private fun setupBottomNav() {
        findViewById<View>(R.id.nav_home).setOnClickListener { showHomeTab() }
        findViewById<View>(R.id.nav_profile).setOnClickListener { showProfileTab() }
    }

    private fun showHomeTab() {
        homeContainer?.visibility = View.VISIBLE
        profileContainer?.visibility = View.GONE
        browserContainer?.visibility = View.GONE
        bottomNav?.visibility = View.VISIBLE
        navHomeLabel?.setTextColor(0xFF07C160.toInt())
        navProfileLabel?.setTextColor(0xFF888888.toInt())
    }

    private fun showProfileTab() {
        homeContainer?.visibility = View.GONE
        profileContainer?.visibility = View.VISIBLE
        browserContainer?.visibility = View.GONE
        bottomNav?.visibility = View.VISIBLE
        navHomeLabel?.setTextColor(0xFF888888.toInt())
        navProfileLabel?.setTextColor(0xFF07C160.toInt())
        populateProfile()
    }

    private fun populateProfile() {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val mobile = prefs.getString("mobile", null)
        profilePhone?.text = mobile ?: "--"
        val jsonStr = prefs.getString("entitlements", null)
        var expiry = "永久"
        if (jsonStr != null) {
            try {
                val arr = org.json.JSONArray(jsonStr)
                var latest = ""
                for (i in 0 until arr.length()) {
                    val e = arr.getJSONObject(i)
                    val exp = e.optString("expireTime", "")
                    if (exp.isEmpty()) { expiry = "永久"; break }
                    if (exp > latest) latest = exp
                }
                if (latest.isNotEmpty() && expiry != "永久") expiry = latest.substring(0, 10)
            } catch (_: Exception) { }
        }
        profileExpiry?.text = "权益有效期至 $expiry"
        renderEntitlements(jsonStr)
    }

    private fun renderEntitlements(jsonStr: String?) {
        entitlementList?.removeAllViews()
        if (jsonStr.isNullOrEmpty()) {
            addEntitlementRow("暂无权益", "-")
            return
        }
        try {
            val arr = org.json.JSONArray(jsonStr)
            if (arr.length() == 0) { addEntitlementRow("暂无权益", "-"); return }
            for (i in 0 until arr.length()) {
                val e = arr.getJSONObject(i)
                val code = e.optString("entitlementCode", "--")
                val exp = e.optString("expireTime", "")
                addEntitlementRow(code, if (exp.isEmpty()) "永久" else exp.substring(0, 10))
            }
        } catch (_: Exception) {
            addEntitlementRow("暂无权益", "-")
        }
    }

    private fun addEntitlementRow(code: String, exp: String) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(15))
        }
        val codeTv = TextView(this).apply {
            text = code; textSize = 15f; setTextColor(0xFF191919.toInt())
            layoutParams = LinearLayout.LayoutParams(0, WRAP, 1f)
        }
        val expTv = TextView(this).apply {
            text = exp; textSize = 15f; setTextColor(0xFF888888.toInt())
        }
        row.addView(codeTv); row.addView(expTv)
        entitlementList?.addView(row)
        val divider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH, 1)
            setBackgroundColor(0xFFF0F0F0.toInt())
        }
        entitlementList?.addView(divider)
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
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                databaseEnabled = true
                userAgentString = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36"
                setGeolocationEnabled(true); setSupportMultipleWindows(false)
            }
            wv.setDownloadListener { _, _, _, _, _ -> }
            wv.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar?.visibility = View.VISIBLE; if (url != null) currentUrl = url
                    errorPage?.visibility = View.GONE; webView?.visibility = View.VISIBLE
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar?.visibility = View.GONE
                    titleText?.text = view?.title ?: currentTitle
                }
                override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?) = false
                override fun onReceivedError(v: WebView?, r: WebResourceRequest?, e: WebResourceError?) {
                    progressBar?.visibility = View.GONE
                    val msg = e?.description?.toString()?.takeIf { it.isNotEmpty() } ?: "页面加载失败"
                    errorMsg?.text = msg
                    errorPage?.visibility = View.VISIBLE
                    webView?.visibility = View.GONE
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
                    platforms = list; ok = true
                }
                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            runOnUiThread {
                showLoading(false)
                swipeRefresh?.isRefreshing = false
                if (ok && platforms.isNotEmpty()) {
                    allPlatforms = platforms
                    renderPlatforms()
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

    private fun guessType(code: String): String = when {
        code in listOf("wangyiyun", "qqmusic", "kugou") -> "music"
        code == "cctv" -> "tv"
        code in listOf("meiju", "hanju") -> "drama"
        else -> "video"
    }

    // ====== UI 构建（白卡 + 品牌色圆形 logo + 分类/搜索过滤） ======
    private fun renderPlatforms() {
        val filtered = allPlatforms.filter { p ->
            val matchTab = currentTab == "all" || p.type == currentTab
            val matchSearch = currentSearch.isBlank() || p.name.contains(currentSearch, true)
            matchTab && matchSearch
        }
        platformContainer?.removeAllViews()
        if (filtered.isEmpty()) {
            platformContainer?.addView(TextView(this).apply {
                text = "暂无匹配平台"; textSize = 13f; setTextColor(0xFF888888.toInt())
                gravity = Gravity.CENTER; setPadding(0, dp(40), 0, dp(40))
            })
            return
        }
        var row: LinearLayout? = null
        filtered.forEachIndexed { i, p ->
            if (i % 2 == 0) {
                row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(MATCH, WRAP)
                }
                platformContainer?.addView(row)
            }
            val card = buildPlatformCard(p)
            val lp = LinearLayout.LayoutParams(0, WRAP, 1f)
            val isLeft = i % 2 == 0
            lp.setMargins(if (isLeft) 0 else dp(5), 0, if (isLeft) dp(5) else 0, dp(10))
            row?.addView(card, lp)
        }
    }

    private fun buildPlatformCard(p: Platform): LinearLayout {
        val brand = brandColors[p.code] ?: 0xFF07C160.toInt()
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            minimumHeight = dp(120)
            setPadding(dp(14), dp(18), dp(14), dp(18))
            isFocusable = true
            elevation = dp(1).toFloat()
            background = GradientDrawable().apply {
                setColor(0xFFFFFFFF.toInt()); cornerRadius = dp(14).toFloat()
                setStroke(dp(1), 0xFFF0F0F0.toInt())
            }
            setOnClickListener { openPlatform(p) }
        }

        // 品牌色圆形 logo（首字）
        val circle = TextView(this).apply {
            text = p.name.take(1)
            textSize = 20f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48)).apply { bottomMargin = dp(10) }
            background = GradientDrawable().apply { setColor(brand); shape = GradientDrawable.OVAL }
        }

        // 名称
        val name = TextView(this).apply {
            text = p.name; textSize = 17f; setTextColor(0xFF191919.toInt())
            setTypeface(null, Typeface.BOLD); gravity = Gravity.CENTER; maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        // 类型标签
        val tag = TextView(this).apply {
            text = typeLabels[p.type] ?: ""; textSize = 12f; setTextColor(0xFF07C160.toInt())
            setPadding(dp(10), dp(4), dp(10), dp(4))
            layoutParams = LinearLayout.LayoutParams(WRAP, WRAP).apply { topMargin = dp(8) }
            background = GradientDrawable().apply {
                setColor(0xFFE8F8EE.toInt()); cornerRadius = dp(8).toFloat()
            }
        }

        card.addView(circle); card.addView(name); card.addView(tag)
        return card
    }

    private fun openPlatform(p: Platform) {
        try {
            homeContainer?.visibility = View.GONE
            profileContainer?.visibility = View.GONE
            browserContainer?.visibility = View.VISIBLE
            bottomNav?.visibility = View.GONE
            backBtn?.visibility = View.VISIBLE
            currentTitle = p.name; titleText?.text = p.name
            webView?.loadUrl(p.url)
        } catch (_: Exception) { }
    }

    private fun showHome() {
        try {
            browserContainer?.visibility = View.GONE
            homeContainer?.visibility = View.VISIBLE
            bottomNav?.visibility = View.VISIBLE
            navHomeLabel?.setTextColor(0xFF07C160.toInt())
            navProfileLabel?.setTextColor(0xFF888888.toInt())
            webView?.stopLoading(); webView?.loadUrl("about:blank")
        } catch (_: Exception) { }
    }

    private fun doLogout() {
        val view = layoutInflater.inflate(R.layout.dialog_logout, null)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        view.findViewById<TextView>(R.id.dlg_cancel).setOnClickListener { dialog.dismiss() }
        view.findViewById<TextView>(R.id.dlg_confirm).setOnClickListener {
            dialog.dismiss()
            getSharedPreferences("auth", MODE_PRIVATE).edit()
                .remove("access_token")
                .remove("refresh_token")
                .apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        dialog.show()
    }

    private fun showError(msg: String) {
        setContentView(TextView(this).apply {
            text = msg; setPadding(32, 32, 32, 32)
            textSize = 16f; setTextColor(0xFFFA5151.toInt())
            setBackgroundColor(0xFFFFFFFF.toInt())
        })
    }

    private fun checkEntitlementExpiry() {
        try {
            val jsonStr = getSharedPreferences("auth", MODE_PRIVATE).getString("entitlements", null) ?: return
            val arr = org.json.JSONArray(jsonStr)
            val soon = mutableListOf<String>()
            val now = System.currentTimeMillis()
            val threeDays = 3L * 24 * 3600 * 1000
            for (i in 0 until arr.length()) {
                val e = arr.getJSONObject(i)
                val expTime = e.optString("expireTime", "")
                if (expTime.isEmpty()) continue
                val expMs = try { java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).parse(expTime)?.time ?: 0 } catch (_: Exception) { 0L }
                if (expMs > 0 && expMs - now < threeDays && expMs > now) {
                    soon.add(e.getString("entitlementCode"))
                }
            }
            if (soon.isNotEmpty()) {
                runOnUiThread {
                    AlertDialog.Builder(this)
                        .setTitle("权益即将到期")
                        .setMessage("以下权益将在 3 天内到期：\n${soon.joinToString("\n")}\n\n请及时续费以免影响使用")
                        .setPositiveButton("知道了", null)
                        .show()
                }
            }
        } catch (_: Exception) { }
    }

    private fun toast(msg: String) {
        try { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() } catch (_: Exception) { }
    }

    private fun fetchInviteCode() {
        val token = getSharedPreferences("auth", MODE_PRIVATE).getString("access_token", null) ?: return
        Thread {
            try {
                val conn = URL("$API_BASE/api/v1/client/invite/code").openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.connectTimeout = 8000; conn.readTimeout = 8000
                val body = conn.inputStream.reader().readText()
                conn.disconnect()
                val json = JSONObject(body)
                val data = json.optJSONObject("data") ?: return@Thread
                val code = data.optString("code", "")
                if (code.isNotEmpty()) {
                    runOnUiThread {
                        inviteCodeText?.text = code
                        inviteCard?.visibility = View.VISIBLE
                    }
                }
            } catch (_: Exception) { }
        }.start()
    }

    private fun dp(v: Int): Int = (v * dp1 + 0.5f).toInt()

    override fun onBackPressed() {
        if (browserContainer?.visibility == View.VISIBLE) {
            try {
                if (webView?.canGoBack() == true) webView?.goBack() else showHome()
            } catch (_: Exception) { showHome() }
        } else if (profileContainer?.visibility == View.VISIBLE) {
            showHomeTab()
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
            } catch (e: Exception) { e.printStackTrace() }
        }.start()
    }

    private fun showUpdateDialog(vName: String, notes: String, url: String, force: Boolean) {
        val view = layoutInflater.inflate(R.layout.dialog_update, null)
        view.findViewById<TextView>(R.id.dlg_version).text = "v$vName"
        view.findViewById<TextView>(R.id.dlg_notes).text = notes
        val laterBtn = view.findViewById<TextView>(R.id.dlg_later)
        if (force) laterBtn.visibility = View.GONE
        val dialog = AlertDialog.Builder(this).setView(view).setCancelable(!force).create()
        laterBtn.setOnClickListener { dialog.dismiss() }
        view.findViewById<TextView>(R.id.dlg_update).setOnClickListener {
            dialog.dismiss()
            downloadAndInstall(url, vName)
        }
        dialog.show()
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
