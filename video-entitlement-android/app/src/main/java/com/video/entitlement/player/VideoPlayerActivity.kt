package com.video.entitlement.player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.video.entitlement.R

class VideoPlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var titleText: TextView? = null
    private var videoTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.player_view)
        titleText = findViewById(R.id.video_title)
        val backBtn = findViewById<ImageView>(R.id.player_back)
        val fullscreenBtn = findViewById<ImageView>(R.id.player_fullscreen)

        videoTitle = intent.getStringExtra("title") ?: "视频播放"
        val url = intent.getStringExtra("url") ?: run {
            Toast.makeText(this, "未提供视频地址", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        titleText?.text = videoTitle
        initPlayer(url)
        backBtn.setOnClickListener { finish() }
        fullscreenBtn.setOnClickListener { toggleFullscreen() }
    }

    private fun initPlayer(url: String) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setUserAgent("Mozilla/5.0 Chrome/120.0.6099.144")

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this).setDataSourceFactory(dataSourceFactory))
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()

        playerView?.player = player
        playerView?.useController = true
        playerView?.setShowNextButton(false)
        playerView?.setShowPreviousButton(false)
        playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

        player?.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
        player?.prepare()
        player?.playWhenReady = true

        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) titleText?.text = videoTitle
            }
            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(this@VideoPlayerActivity, "播放错误: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun toggleFullscreen() {
        requestedOrientation = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val landscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (landscape) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        playerView?.resizeMode = if (landscape) AspectRatioFrameLayout.RESIZE_MODE_FILL else AspectRatioFrameLayout.RESIZE_MODE_FIT
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !isInPictureInPictureMode) {
            player?.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.release(); player = null
    }
}
