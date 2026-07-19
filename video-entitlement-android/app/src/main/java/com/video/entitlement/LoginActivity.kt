package com.video.entitlement

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var etAccount: EditText
    private lateinit var etPassword: EditText
    private lateinit var etEntitlementCode: EditText
    private lateinit var etCaptcha: EditText
    private lateinit var ivCaptcha: ImageView
    private lateinit var btnLogin: Button
    private lateinit var progress: ProgressBar

    private var captchaKey: String = ""

    companion object {
        const val PREFS_NAME = "auth"
        const val KEY_TOKEN = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_MOBILE = "mobile"
        val API_BASE = "http://43.161.222.78:8081"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 已有登录态则直接跳转主页
        val existingToken = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getString(KEY_TOKEN, null)
        if (!existingToken.isNullOrEmpty()) {
            startActivity(android.content.Intent(this, MainActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        etAccount = findViewById(R.id.et_account)
        etPassword = findViewById(R.id.et_password)
        etEntitlementCode = findViewById(R.id.et_entitlement_code)
        etCaptcha = findViewById(R.id.et_captcha)
        ivCaptcha = findViewById(R.id.iv_captcha)
        btnLogin = findViewById(R.id.btn_login)
        progress = ProgressBar(this).also { it.visibility = View.GONE }

        // 权益码必填：输入验证 + 红色提示
        etEntitlementCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    etEntitlementCode.error = "权益码为必填项"
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        ivCaptcha.setOnClickListener { loadCaptcha() }
        btnLogin.setOnClickListener { doLogin() }

        // 显示版本号
        findViewById<TextView>(R.id.tv_login_version)?.text = "v${getAppVersion()}"

        loadCaptcha()
    }

    private fun loadCaptcha() {
        Thread {
            try {
                val conn = URL("$API_BASE/api/v1/auth/captcha").openConnection() as HttpURLConnection
                conn.connectTimeout = 8000; conn.readTimeout = 8000
                val json = JSONObject(conn.inputStream.reader().readText())
                val data = json.getJSONObject("data")
                captchaKey = data.getString("captchaKey")
                val b64 = data.getString("imageBase64").substringAfter("base64,")
                val bytes = android.util.Base64.decode(b64, android.util.Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                runOnUiThread { ivCaptcha.setImageBitmap(bmp) }
                conn.disconnect()
            } catch (e: Exception) {
                val msg = when {
                    e is java.net.ConnectException -> "无法连接服务器"
                    e is java.net.SocketTimeoutException -> "网络超时"
                    else -> "验证码加载失败: ${e.message}"
                }
                runOnUiThread { toast(msg) }
            }
        }.start()
    }

    private fun doLogin() {
        val mobile = etAccount.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val entitlementCode = etEntitlementCode.text.toString().trim()
        val captchaCode = etCaptcha.text.toString().trim()

        // 必填校验
        if (mobile.isEmpty()) { etAccount.error = "请输入账号"; return }
        if (password.isEmpty()) { etPassword.error = "请输入密码"; return }
        if (entitlementCode.isEmpty()) { etEntitlementCode.error = "权益码为必填项"; return }
        if (entitlementCode.length != 8) { etEntitlementCode.error = "权益码须为8位数字"; return }
        if (captchaCode.isEmpty()) { etCaptcha.error = "请输入图形验证码"; return }
        if (captchaKey.isEmpty()) { toast("验证码未加载，请点击刷新"); loadCaptcha(); return }

        setLoading(true)

        Thread {
            try {
                val body = JSONObject().apply {
                    put("mobile", mobile)
                    put("password", password)
                    put("entitlementCode", entitlementCode)
                    put("captchaKey", captchaKey)
                    put("captchaCode", captchaCode)
                    put("devicePublicId", getMyDeviceId())
                    put("clientType", "ANDROID")
                    put("appVersion", getAppVersion())
                }

                val conn = URL("$API_BASE/api/v1/auth/login").openConnection() as HttpURLConnection
                conn.connectTimeout = 10000; conn.readTimeout = 10000
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                val os: OutputStream = conn.outputStream
                os.write(body.toString().toByteArray())
                os.close()

                val respJson = JSONObject(conn.inputStream.reader().readText())
                val code = respJson.optInt("code", -1)
                conn.disconnect()

                if (code == 0 || code == 200) {
                    val data = respJson.getJSONObject("data")
                    val token = data.getString("accessToken")
                    val refresh = data.optString("refreshToken", "")
                    saveAuth(token, refresh, mobile)

                    runOnUiThread {
                        setLoading(false)
                        startActivity(android.content.Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else {
                    val msg = respJson.optString("message", "登录失败")
                    runOnUiThread {
                        setLoading(false)
                        toast(msg)
                        loadCaptcha()
                    }
                }
            } catch (e: Exception) {
                val msg = when {
                    e is java.net.ConnectException -> "无法连接服务器，请检查网络"
                    e is java.net.SocketTimeoutException -> "连接超时，请重试"
                    e is java.net.UnknownHostException -> "DNS解析失败，请检查网络"
                    else -> "登录失败: ${e.message}"
                }
                runOnUiThread {
                    setLoading(false)
                    toast(msg)
                    loadCaptcha()
                }
            }
        }.start()
    }

    private fun saveAuth(token: String, refresh: String, mobile: String) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_REFRESH, refresh)
            .putString(KEY_MOBILE, mobile)
            .apply()
    }

    private fun setLoading(loading: Boolean) {
        btnLogin.isEnabled = !loading
        btnLogin.text = if (loading) "请稍候..." else "登录 / 注册"
    }

    private fun getMyDeviceId(): String {
        return android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ).take(32)
    }

    private fun getAppVersion(): String {
        return try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0"
        } catch (_: Exception) { "1.0" }
    }

    private fun toast(msg: String) {
        try { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() } catch (_: Exception) {}
    }

    private fun enableEdgeToEdge() {
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
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
}
