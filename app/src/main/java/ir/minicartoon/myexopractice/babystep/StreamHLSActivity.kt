package ir.minicartoon.myexopractice.babystep

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder
import ir.minicartoon.myexopractice.R

class StreamHLSActivity : AppCompatActivity() {
    private lateinit var styledPlayerView: StyledPlayerView
    private lateinit var exoPlayer: ExoPlayer
    private var exoQuality: ImageButton? =null
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var exoFullScreenIcon: ImageView
    private lateinit var exoFullScreenBtn: FrameLayout
    private lateinit var mainFrameLayout: FrameLayout

    private var fullscreenDialog: Dialog? = null
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isFullscreen = false
    private var isPlayerPlaying = true
    private var trackDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream_hlsactivity)

        supportActionBar?.hide()
        //add the flag to keep the screen always on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        styledPlayerView = findViewById(R.id.player_view)
        exoQuality = styledPlayerView.findViewById(R.id.exo_quality)
       // mainFrameLayout = findViewById(R.id.main_media_frame)

        initExoPlayer()
        //initFullScreenDialog()
        initFullScreenButton()
        if (savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            playbackPosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            isFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
            isPlayerPlaying = savedInstanceState.getBoolean(STATE_PLAYER_PLAYING)
        }
    /*    exoQuality.setOnClickListener {
            if(trackDialog == null) initPopupQuality()
            trackDialog?.show()
        }*/
    }
    private fun initPopupQuality() {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        var videoRenderer: Int? = null

   /*    if(mappedTrackInfo == null) return else exoQuality?.visibility ?:  = View.VISIBLE

        if (mappedTrackInfo != null) {
            for (i in 0 until mappedTrackInfo.rendererCount) {
                if(isVideoRenderer(mappedTrackInfo, i)) videoRenderer = i
            }
        }*/

        if(videoRenderer == null) {
            exoQuality?.visibility = View.GONE
            return
        }

        val trackSelectionDialogBuilder = TrackSelectionDialogBuilder(
            this,
            getString(R.string.qualitySelector),
            exoPlayer,
            videoRenderer
        )
        trackSelectionDialogBuilder.setTrackNameProvider {
            // Override function getTrackName
            getString(R.string.exo_track_resolution_pixel, it.height)
        }
        trackDialog = trackSelectionDialogBuilder.build()
    }

    private fun isVideoRenderer(
        mappedTrackInfo: MappingTrackSelector.MappedTrackInfo,
        rendererIndex: Int
    ): Boolean {
        val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
        if(trackGroupArray.length == 0) return false
        val trackType = mappedTrackInfo.getRendererType(rendererIndex)
        return C.TRACK_TYPE_VIDEO == trackType
    }
    private fun initExoPlayer() {
        trackSelector = DefaultTrackSelector(this)
        // When player is initialized it'll be played with a quality of MaxVideoSize to prevent loading in 1080p from the start
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSize(MAX_WIDTH, MAX_HEIGHT)
        )
        exoPlayer = ExoPlayer.Builder(this).build()
        //set the view in to the exoPlayer
        styledPlayerView.player = exoPlayer

        //Create MediaItem........this is a HLS test URL.
        exoPlayer.setMediaItem(
            MediaItem.fromUri(

                Uri.parse(
                    "https://dls2.top-movies2filmha.click/DonyayeSerial/series/The.Sopranos/Soft.Sub/S01/720p.x265.BluRay/The.Sopranos.S01E01.720p.x265.HET" +
                            ".SoftSub.DonyayeSerial.mkv"
                ) //HLS(Http Live Stream) is a video transform protocol .. // the suffix of the video is m3ua
            )
        )

        exoPlayer.seekTo(currentWindow, playbackPosition)
        if(isFullscreen) {
            openFullscreenDialog()
        }
        exoPlayer.prepare()
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if(playbackState == Player.STATE_READY) {
                    exoQuality?.visibility = View.VISIBLE
                }
            }
        })
        //There are a lot of Methode in it for use
        exoPlayer.addListener(object : Player.Listener {

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()

                //After the video was Playing . you can do something here
                //for example, show a Toast, or Change Ui, make UI into FullScreen
            }
        })

    }

    private fun initFullScreenDialog() {
        fullscreenDialog =
            object : Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                @Deprecated("Deprecated in Java")
                override fun onBackPressed() {
                    if (isFullscreen) closeFullscreenDialog()
                    super.onBackPressed()
                }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_RESUME_WINDOW, exoPlayer.currentMediaItemIndex)
        outState.putLong(STATE_RESUME_POSITION, exoPlayer.currentPosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, isFullscreen)
        outState.putBoolean(STATE_PLAYER_PLAYING, isPlayerPlaying)
        super.onSaveInstanceState(outState)
    }

    private fun initFullScreenButton() {
        styledPlayerView.setFullscreenButtonClickListener {
            if (it) {
                Toast.makeText(this, "full click", Toast.LENGTH_SHORT).show()
                //openFullscreenDialog()
                openFullscreen()
            } else {
                Toast.makeText(this, "Expend click", Toast.LENGTH_SHORT).show()
               // closeFullscreenDialog()
                closeFullscreen()
            }

        }

    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun openFullscreenDialog() {

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (styledPlayerView.parent as ViewGroup).removeView(styledPlayerView)
        fullscreenDialog?.addContentView(
            styledPlayerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        isFullscreen = true
        fullscreenDialog?.show()
    }

    private fun closeFullscreenDialog() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        (styledPlayerView.parent as ViewGroup).removeView(styledPlayerView)
        mainFrameLayout.addView(styledPlayerView)

        isFullscreen = false
        fullscreenDialog?.dismiss()
    }


    @SuppressLint("SourceLockedOrientationActivity")
    private fun openFullscreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
       /* exoFullScreenIcon.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_shrink)
        )*/
        styledPlayerView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        val params: LinearLayout.LayoutParams = styledPlayerView.layoutParams as LinearLayout.LayoutParams
        params.width = LinearLayout.LayoutParams.MATCH_PARENT
        params.height = LinearLayout.LayoutParams.MATCH_PARENT
        styledPlayerView.layoutParams = params
        supportActionBar?.hide()
        hideSystemUi()
        isFullscreen = true
    }

    private fun closeFullscreen() {
    /*    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        exoFullScreenIcon.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand)
        )*/
        styledPlayerView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        val params: LinearLayout.LayoutParams = styledPlayerView.layoutParams as LinearLayout.LayoutParams
        params.width = LinearLayout.LayoutParams.MATCH_PARENT
        params.height = 0
        styledPlayerView.layoutParams = params
        supportActionBar?.show()
        styledPlayerView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        isFullscreen = false
    }

    private fun hideSystemUi() {
        styledPlayerView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    ////////////////////////////////Lifecycle Handle //////////////////////////////////
    //We should Pause or play ExoPlayer by each Lifecycle of the Activity
    override fun onResume() {
        super.onResume()
        exoPlayer.playWhenReady = true
        exoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.pause()
        exoPlayer.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.pause()
        exoPlayer.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }

    companion object {
        const val HLS_STATIC_URL =
            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
        const val STATE_RESUME_WINDOW = "resumeWindow"
        const val STATE_RESUME_POSITION = "resumePosition"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
        const val MAX_HEIGHT = 539
        const val MAX_WIDTH = 959
    }

}