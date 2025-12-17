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
    import com.prograiii.algoritmica2.databinding.ActivityGameScreenBinding
    import kotlin.math.abs
    
    class GameScreen : AppCompatActivity() {
    
        private lateinit var binding: ActivityGameScreenBinding
        private var tipoOperacion: String? = null
    
        private var currentInput = ""
    
        private var score = 0
    
        private var vidas = 3
    
        private val destruidosPorJugador = mutableSetOf<View>()
    
        private val handler = Handler(Looper.getMainLooper())
        private var gameEnded = false
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
    
            binding = ActivityGameScreenBinding.inflate(layoutInflater)
            setContentView(binding.root)
    
            tipoOperacion = intent.getStringExtra("OPERACION")
    
            binding.inputDisplay.setText("")
            setupKeypad()
    
            lanzarMeteoritos(binding.meteoritoContainer)
    
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    
    
    
        // ---------------- KEYPAD ----------------
        private fun setupKeypad() {
    
            fun addDigit(d: String) {
                currentInput += d
                binding.inputDisplay.setText(currentInput)
            }
    
            binding.key0.setOnClickListener { addDigit("0") }
            binding.key1.setOnClickListener { addDigit("1") }
            binding.key2.setOnClickListener { addDigit("2") }
            binding.key3.setOnClickListener { addDigit("3") }
            binding.key4.setOnClickListener { addDigit("4") }
            binding.key5.setOnClickListener { addDigit("5") }
            binding.key6.setOnClickListener { addDigit("6") }
            binding.key7.setOnClickListener { addDigit("7") }
            binding.key8.setOnClickListener { addDigit("8") }
            binding.key9.setOnClickListener { addDigit("9") }
    
            binding.keyCancelar.setOnClickListener {
                currentInput = ""
                binding.inputDisplay.setText("")
            }
        }
    
        // ---------------- METEORITOS ----------------
        private fun lanzarMeteoritos(contenedor: ViewGroup) {
    
            val runnable = object : Runnable {
                override fun run() {
    
                    if (gameEnded) return
    
                    val meteoritoView =
                        layoutInflater.inflate(R.layout.item_meteorito, contenedor, false)

                    meteoritoView.post {
                        val maxX = contenedor.width - meteoritoView.width
                        meteoritoView.x =
                            if (maxX > 0) (0..maxX).random().toFloat() else 0f
                    }
    
                    meteoritoView.y = 0f

                    val txtOperacion =
                        meteoritoView.findViewById<TextView>(R.id.txtOperacion)
    
                    val a = (2..9).random()
                    val b = (2..9).random()
                    val x = (2..50).random()
                    val y = (2..50).random()
    
                    if (tipoOperacion == "MCM") {
                        txtOperacion.text = "$a ∧ $b"
                    }
    
                    if (tipoOperacion == "MCD") {
                        txtOperacion.text = "$x v $y"
                    }
    
                    meteoritoView.setOnClickListener {
    
                        if (gameEnded) return@setOnClickListener
    
                        val userValue = binding.inputDisplay.text.toString()
    
                        if (tipoOperacion == "MCM") {
                            val correcto = lcm(a, b).toString()
                            if (userValue == correcto) {
                                destruidosPorJugador.add(meteoritoView)
                                destruirMeteorito(meteoritoView)
                                currentInput = ""
                                binding.inputDisplay.setText("")
                                score++
                                if (score >= 30) endGameWin()
                            }
                        }
    
                        if (tipoOperacion == "MCD") {
                            val correcto = gcd(x, y).toString()
                            if (userValue == correcto) {
                                destruidosPorJugador.add(meteoritoView)
                                destruirMeteorito(meteoritoView)
                                currentInput = ""
                                binding.inputDisplay.setText("")
                                score++
                                if (score >= 30) endGameWin()
                            }
                        }
                    }
    
                    contenedor.addView(meteoritoView)
                    moverMeteorito(meteoritoView)
    
                    handler.postDelayed(this, 5000)
                }
            }
    
            handler.post(runnable)
        }
    
        // ---------------- MOVIMIENTO ----------------
        private fun moverMeteorito(meteoritoView: View) {

            meteoritoView.post {

                val alturaPantalla = binding.meteoritoContainer.height.toFloat()

                if (alturaPantalla <= 0f) return@post

                val puntoImpacto = alturaPantalla * 0.90f

                val anim = ObjectAnimator.ofFloat(
                    meteoritoView,
                    "translationY",
                    0f,
                    puntoImpacto
                )

                anim.duration = 9000
                meteoritoView.tag = anim

                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {

                        if (!gameEnded && !destruidosPorJugador.contains(meteoritoView)) {
                            vidas--
                            if (vidas <= 0) {
                                endGameDefeat()
                            }
                        }

                        destruidosPorJugador.remove(meteoritoView)
                        (meteoritoView.parent as? ViewGroup)?.removeView(meteoritoView)
                    }
                })

                anim.start()
            }
        }



        // ---------------- DESTRUIR ----------------
        private fun destruirMeteorito(meteoritoView: View) {
            val anim = meteoritoView.tag as? ObjectAnimator
            anim?.cancel()
            (meteoritoView.parent as? ViewGroup)?.removeView(meteoritoView)
        }
    
        // ---------------- FINES ----------------
        private fun endGameWin() {
            gameEnded = true
            handler.removeCallbacksAndMessages(null)
            binding.meteoritoContainer.removeAllViews()

            val intent = Intent(this, WinningScreen::class.java)
            startActivity(intent)
            finish()
        }

        private fun endGameDefeat() {
            gameEnded = true
            handler.removeCallbacksAndMessages(null)
            binding.meteoritoContainer.removeAllViews()

            val intent = Intent(this, LosingScreen::class.java)
            startActivity(intent)
            finish()
        }


        // ---------------- MCD / MCM ----------------
        private fun gcd(a: Int, b: Int): Int {
            return if (b == 0) abs(a) else gcd(b, a % b)
        }
    
        private fun lcm(a: Int, b: Int): Int {
            return a * b / gcd(a, b)
        }
    }
