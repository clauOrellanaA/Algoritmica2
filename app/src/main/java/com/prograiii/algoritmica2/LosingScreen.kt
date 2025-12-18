package com.prograiii.algoritmica2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.prograiii.algoritmica2.databinding.ActivityLosingScreenBinding

class LosingScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLosingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLosingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnTryAgain.setOnClickListener {
            val game = intent.getStringExtra("GAME")
            if (game == "NumsPrimosGame") {
                val intentGame = Intent(this, NumsPrimosGame::class.java)
                startActivity(intentGame)
            } else if (game == "GameScreen") {
                val tipoOperacion = intent.getStringExtra("OPERACION")
                val intentGame = Intent(this, GameScreen::class.java)
                intentGame.putExtra("OPERACION", tipoOperacion)
                startActivity(intentGame)
            }
            finish()
        }


        binding.btnBackToMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
