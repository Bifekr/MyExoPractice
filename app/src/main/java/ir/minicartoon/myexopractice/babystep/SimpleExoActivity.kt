package ir.minicartoon.myexopractice.babystep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import ir.minicartoon.myexopractice.databinding.ActivitySimpleExoBinding

class SimpleExoActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySimpleExoBinding
    private var player: ExoPlayer?=null
    private val isPlaying get()=player?.isPlaying?:false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySimpleExoBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        initializePlayer()

    }

    private fun initializePlayer() {

        player = ExoPlayer.Builder(this).build()

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
        player?.apply {
            setMediaSource(mediaSource)
            playWhenReady=true //Start playing when the exoPlayer has setup
            seekTo(0,0L)// Start From the beginning
            prepare() //Change the stat from idle.
        }.also {

            //Don't Forget to attach the player to the view
            binding.playerView.player=it

        }

    }
}