package com.prograiii.algoritmica2

object GameState {
    var vidas: Int = 3
        private set
    
    const val COOLDOWN_SEGUNDOS = 3

    fun resetVidas() {
        vidas = 3
    }

    fun perderVida() {
        if (vidas > 0) {
            vidas--
        }
    }
}
