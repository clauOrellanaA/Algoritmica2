package com.prograiii.algoritmica2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.prograiii.algoritmica2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val musicManager = MusicManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        musicManager.initialize(application)
        musicManager.start()

        GameState.resetVidas()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupMusicButton()

        binding.btnMCM.setOnClickListener {
            val intent = Intent(this, GameScreen::class.java)
            intent.putExtra("OPERACION", "MCM")
            startActivity(intent)
        }

        binding.btnPrimos.setOnClickListener {
            val intent = Intent(this, NumsPrimosGame::class.java)
            startActivity(intent)
        }

        binding.btnMCD.setOnClickListener {
            val intent = Intent(this, GameScreen::class.java)
            intent.putExtra("OPERACION", "MCD")
            startActivity(intent)
        }
    }

    private fun setupMusicButton() {
        updateMusicIcon()
        binding.btnMusicToggle.setOnClickListener {
            musicManager.toggleMute()
            updateMusicIcon()
        }
    }

    private fun updateMusicIcon() {
        val iconRes = if (musicManager.isMuted()) {
            R.drawable.ic_volume_off
        } else {
            R.drawable.ic_volume_on
        }
        binding.btnMusicToggle.setImageResource(iconRes)
    }

    override fun onResume() {
        super.onResume()
        musicManager.start()
    }

    override fun onPause() {
        super.onPause()
        musicManager.pause()
    }
}
