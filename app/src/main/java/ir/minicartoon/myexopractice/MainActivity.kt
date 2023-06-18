package ir.minicartoon.myexopractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ir.minicartoon.myexopractice.databinding.ActivityMainBinding
import ir.minicartoon.myexopractice.babystep.StreamHLSActivity
import ir.minicartoon.myexopractice.fullscreen.DialogFullScreenActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.tv.setOnClickListener {
            startActivity(Intent(this, StreamHLSActivity::class.java))
        }

        binding.dialogFullScreen.setOnClickListener {
        startActivity(Intent(this, DialogFullScreenActivity::class.java))

    }
        binding.paramFullScreen.setOnClickListener {
            startActivity(Intent(this, DialogFullScreenActivity::class.java))

        }
    }
}