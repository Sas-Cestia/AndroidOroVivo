package fr.cestia.common_files.tools.scan

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import fr.cestia.common_files.R

class ScanSoundManager(context: Context) {
    private val soundPool: SoundPool
    private val successSoundId: Int
    private val errorSoundId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        // Charger les fichiers audio
        successSoundId = soundPool.load(context, R.raw.success_sound, 1)
        errorSoundId = soundPool.load(context, R.raw.error_sound, 1)
    }

    fun playSuccessSound() {
        soundPool.play(successSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playErrorSound() {
        soundPool.play(errorSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}