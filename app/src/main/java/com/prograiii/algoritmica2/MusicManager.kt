package com.prograiii.algoritmica2

import android.app.Application
import android.media.MediaPlayer

class MusicManager private constructor() {
    private var mediaPlayer: MediaPlayer? = null
    private var isMuted = false

    companion object {
        @Volatile
        private var instance: MusicManager? = null

        fun getInstance(): MusicManager {
            return instance ?: synchronized(this) {
                instance ?: MusicManager().also { instance = it }
            }
        }
    }

    fun initialize(application: Application) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(application, R.raw.stargame)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(1f, 1f)
        }
    }

    fun start() {
        if (!isMuted) {
            mediaPlayer?.start()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun toggleMute() {
        isMuted = !isMuted
        if (isMuted) {
            mediaPlayer?.setVolume(0f, 0f)
        } else {
            mediaPlayer?.setVolume(1f, 1f)
        }
    }

    fun isMuted(): Boolean = isMuted

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
