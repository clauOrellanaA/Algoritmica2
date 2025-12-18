package com.prograiii.algoritmica2

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.prograiii.algoritmica2.databinding.ActivityNumsPrimosGameBinding

class NumsPrimosGame : AppCompatActivity() {

    private lateinit var binding: ActivityNumsPrimosGameBinding
    private val musicManager = MusicManager.getInstance()
    private var numeroSeleccionado: Int? = null
    private var meteoritoSeleccionado: View? = null

    private var score = 0
    private var gameEnded = false
    private val handler = Handler(Looper.getMainLooper())
    private val maximo = 100
    val criba = MutableList(maximo+10) { true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Reiniciar estado del juego
        GameState.resetVidas()
        score = 0
        gameEnded = false
        numeroSeleccionado = null
        meteoritoSeleccionado = null

        runCriba(maximo)
        binding = ActivityNumsPrimosGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoBack.setOnClickListener {
            gameEnded = true
            handler.removeCallbacksAndMessages(null)
            binding.meteoritoContainer.removeAllViews()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        setupMusicButton()
        iniciarCountdown()

        binding.btnTrue.setOnClickListener {
            verificarRespuesta(true)
        }
        binding.btnFalse.setOnClickListener {
            verificarRespuesta(false)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameEnded = true
        handler.removeCallbacksAndMessages(null)
    }

    private fun iniciarCountdown() {
        var countdown = GameState.COOLDOWN_SEGUNDOS
        binding.inputDisplay.text = "¡Prepárate! $countdown"
        
        val countdownRunnable = object : Runnable {
            override fun run() {
                countdown--
                if (countdown > 0) {
                    binding.inputDisplay.text = "¡Prepárate! $countdown"
                    handler.postDelayed(this, 1000)
                } else {
                    binding.inputDisplay.text = "Identifica los números primos en el nivel"
                    lanzarMeteoritos(binding.meteoritoContainer)
                }
            }
        }
        handler.postDelayed(countdownRunnable, 1000)
    }

    private fun lanzarMeteoritos(contenedor: ViewGroup) {
        val runnable = object : Runnable {
            override fun run() {
                if (gameEnded) return
                val meteoritoView = layoutInflater.inflate(R.layout.item_meteorito, contenedor, false)

                val meteoritoWidth = meteoritoView.layoutParams.width.takeIf { it > 0 } ?: 100
                val maxX = contenedor.width - meteoritoWidth
                meteoritoView.x = (0..maxX).random().toFloat()
                meteoritoView.y = 0f

                val txtOperacion = meteoritoView.findViewById<TextView>(R.id.txtOperacion)
                val numero = generarNumeroPrimo()
                txtOperacion.text = numero.toString()

                meteoritoView.setOnClickListener {
                    numeroSeleccionado = numero
                    meteoritoSeleccionado = meteoritoView
                    binding.inputDisplay.text = "Número seleccionado: $numero"
                }

                contenedor.addView(meteoritoView)
                moverMeteorito(meteoritoView, contenedor)

                handler.postDelayed(this, 3500)
            }
        }
        handler.post(runnable)
    }

    private fun moverMeteorito(meteoritoView: View, contenedor: ViewGroup) {
        val alturaColision = contenedor.height * 0.60f
        val anim = ObjectAnimator.ofFloat(meteoritoView, "translationY", 0f, contenedor.height.toFloat())
        anim.duration = 12000

        var vidaQuitada = false
        anim.addUpdateListener { animation ->
            val yActual = animation.animatedValue as Float
            if (!vidaQuitada && yActual >= alturaColision) {
                vidaQuitada = true

                if (meteoritoView.parent != null && meteoritoView != meteoritoSeleccionado) {
                    (meteoritoView.parent as? ViewGroup)?.removeView(meteoritoView)
                    GameState.perderVida()
                    binding.inputDisplay.text = "Un meteorito alcanzó al pingüino. Puntos: $score | Vidas: ${GameState.vidas}"

                    if (!gameEnded && GameState.vidas <= 0) {
                        terminarJuego("¡Perdiste! Se acabaron tus vidas.")
                    }
                }
            }
        }

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                (meteoritoView.parent as? ViewGroup)?.removeView(meteoritoView)
            }
        })

        anim.start()
    }
    private fun generarNumeroPrimo(): Int {
        return (2..maximo).random()
    }

    private fun verificarRespuesta(respuestaJugador: Boolean) {
        val numero = numeroSeleccionado
        val meteorito = meteoritoSeleccionado
        if (numero == null || meteorito == null) {
            binding.inputDisplay.text = "Selecciona un meteorito primero"
            return
        }

        val correcto = criba[numero]
        if (respuestaJugador == correcto) {
            score++
            binding.inputDisplay.text = "¡Correcto! Puntos: $score | Vidas: ${GameState.vidas}"
            val anim = meteorito.tag as? ObjectAnimator
            anim?.cancel()
            (meteorito.parent as? ViewGroup)?.removeView(meteorito)
        } else {
            GameState.perderVida()
            binding.inputDisplay.text = "Incorrecto. Puntos: $score | Vidas: ${GameState.vidas}"
        }

        (meteorito.parent as? ViewGroup)?.removeView(meteorito)

        numeroSeleccionado = null
        meteoritoSeleccionado = null

        if (!gameEnded && GameState.vidas <= 0) {
            terminarJuego("¡Perdiste! Se acabaron tus vidas.")
        } else if (!gameEnded && score >= 30) {
            terminarJuego("¡Ganaste! Llegaste a 30 puntos.")
        }
    }

    private fun terminarJuego(mensaje: String) {
        if (gameEnded) return
        gameEnded = true
        handler.removeCallbacksAndMessages(null)
        binding.inputDisplay.text = mensaje
        binding.btnTrue.isEnabled = false
        binding.btnFalse.isEnabled = false

        if (mensaje.contains("Perdiste")) {
            val intent = Intent(this, LosingScreen::class.java)
            intent.putExtra("GAME", "NumsPrimosGame")
            startActivity(intent)
            finish()
        } else if (mensaje.contains("Ganaste")) {
            val intent = Intent(this, WinningScreen::class.java)
            intent.putExtra("GAME", "NumsPrimosGame")
            startActivity(intent)
            finish()
        }
    }

    private fun endGameWin() {
        val intent = Intent(this, WinningScreen::class.java)
        startActivity(intent)
        finish()
    }

    private fun endGameDefeat() {
        val intent = Intent(this, LosingScreen::class.java)
        startActivity(intent)
        finish()
    }

    fun runCriba(n: Int) {
        criba[0] = false
        criba[1] = false

        for (i in 2..n) {
            if (i * i > n) break
            if (criba[i]) {
                for (j in i * i..n step i) {
                    criba[j] = false
                }
            }
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
}
