package ir.minicartoon.myexopractice.fullscreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import ir.minicartoon.myexopractice.R
import ir.minicartoon.myexopractice.databinding.ActivityDialogFullScreenBinding

class DialogFullScreenActivity : AppCompatActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var playerView: StyledPlayerView
    private  var exoFullScreenIcon: ImageView? = null
    private  var exoFullScreenBtn: FrameLayout? = null
    private  var mainFrameLayout: FrameLayout? = null

    private var fullscreenDialog: Dialog? = null
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isFullscreen = false
    private var isPlayerPlaying = true
    private val mediaItem = MediaItem.Builder()
        .setUri(HLS_STATIC_URL)
        .setMimeType(MimeTypes.APPLICATION_M3U8)
        .build()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dialog_full_screen)
        playerView = findViewById(R.id.player_view)
        mainFrameLayout = findViewById(R.id.main_media_frame)
        exoFullScreenBtn = playerView.findViewById(R.id.exo_fullscreen_button)
        exoFullScreenIcon = playerView.findViewById(R.id.exo_fullscreen_icon)

        dataSourceFactory = DefaultDataSource.Factory(this)





        initFullScreenDialog()
        initFullScreenButton()
        playerView.setFullscreenButtonClickListener {
            if (it){
                Toast.makeText(this, "full click", Toast.LENGTH_SHORT).show()
openFullscreenDialog()
            }else{
                closeFullscreenDialog()
            }
        }

        if(savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            playbackPosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            isFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
            isPlayerPlaying = savedInstanceState.getBoolean(STATE_PLAYER_PLAYING)
        }
    }


    private fun initPlayer() {

        exoPlayer = ExoPlayer.Builder(this).build()

        //Create a Media Item
        val mediaItem = MediaItem.Builder()
            // .setUri("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")
            .setUri("https://dls2.top-movies2filmha.click/DonyayeSerial/series/The.Sopranos/Soft.Sub/S01/720p.x265.BluRay/The.Sopranos.S01E01.720p.x265.HET.SoftSub.DonyayeSerial.mkv")

            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()

        //Create a Media Source And pass the Media Item
        val mediaSource= ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(this)
        ).createMediaSource(mediaItem)


        //Finally Assign this Media Source to the Player
        exoPlayer?.apply {
            setMediaSource(mediaSource)
            playWhenReady=true //Start playing when the exoPlayer has setup
            seekTo(0,0L)// Start From the beginning
            prepare() //Change the stat from idle.
        }.also {

            //Don't Forget to attach the player to the view
          playerView.player=it

        }

        if(isFullscreen) openFullscreenDialog()
    }
/*    private fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            playWhenReady = isPlayerPlaying
            seekTo(currentWindow, playbackPosition)
            setMediaItem(mediaItem)
            prepare()
        }
        playerView.player = exoPlayer

        if(isFullscreen) {
            openFullscreenDialog()
        }
    }*/

    private fun releasePlayer() {
        isPlayerPlaying = exoPlayer.playWhenReady
        playbackPosition = exoPlayer.currentPosition
        currentWindow = exoPlayer.currentMediaItemIndex
        exoPlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_RESUME_WINDOW, exoPlayer.currentMediaItemIndex)
        outState.putLong(STATE_RESUME_POSITION, exoPlayer.currentPosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, isFullscreen)
        outState.putBoolean(STATE_PLAYER_PLAYING, isPlayerPlaying)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        if(Util.SDK_INT > 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if(Util.SDK_INT <= 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if(Util.SDK_INT <= 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if(Util.SDK_INT > 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    // FULLSCREEN PART

    private fun initFullScreenDialog() {
        fullscreenDialog =
            object : Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                @Deprecated("Deprecated in Java")
                override fun onBackPressed() {
                    if(isFullscreen) closeFullscreenDialog()
                    super.onBackPressed()
                }
            }
    }

    private fun initFullScreenButton() {
        exoFullScreenBtn?.setOnClickListener {
            if(!isFullscreen) {
                openFullscreenDialog()
            } else {
                closeFullscreenDialog()
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun openFullscreenDialog() {
        exoFullScreenIcon?.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_shrink)
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (playerView.parent as ViewGroup).removeView(playerView)
        fullscreenDialog?.addContentView(
            playerView,
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
        (playerView.parent as ViewGroup).removeView(playerView)
        mainFrameLayout?.addView(playerView)
        exoFullScreenIcon?.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand)
        )
        isFullscreen = false
        fullscreenDialog?.dismiss()
    }

    companion object {
        const val HLS_STATIC_URL =
            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
        const val STATE_RESUME_WINDOW = "resumeWindow"
        const val STATE_RESUME_POSITION = "resumePosition"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
    }
}