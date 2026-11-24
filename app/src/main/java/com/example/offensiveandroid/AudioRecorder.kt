package com.example.offensiveandroid

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.IOException

class AudioRecorder : Service() {
    private var mediaRecorder : MediaRecorder? = null
    private var audioFile : File? = null
    private var audioManager: AudioManager? = null
    private var isMicAvailable = true

    companion object {
        var lastRecordedFileUri : String? = null
        private var isRecording = false

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        checkMicAvailability()
        createNotificationChannel()
        startForeground(1,createNotification())

        try {
            if (isMicAvailable){
                startRecording()
            } else{
                Log.e("AudioRecorderService","Mic is currently in use")
                stopSelf()
            }
        } catch (e: Exception){
            Log.e("AudioRecorderService","Error initializing recording: ${e.message}")
            e.printStackTrace()
            stopSelf()
        }
    }

    private fun checkMicAvailability(){
        isMicAvailable = audioManager?.isMicrophoneMute == false
        Log.d("AudioRecorderService","Mic available: $isMicAvailable")
    }
    private fun stopRecordingAndService(){
        if (isRecording){
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null
                isRecording = false

                if (audioFile?.exists() == true && (audioFile?.length() ?: 0)>0){
                    lastRecordedFileUri = audioFile?.absolutePath
                    Log.d("AudioRecorderService","Recording Stopped. File:${lastRecordedFileUri}")
                } else {
                    Log.e("AudioRecorderService","File doesn't exist or is empty")
                }
            } catch (e:RuntimeException){
                Log.e("AudioRecorderService","RunTimeException while stopping recording : ${e.message}")
                e.printStackTrace()
            } catch (e:Exception){
                Log.e("AudioRecorderService","Error stopping recording :${e.message}")
                e.printStackTrace()
            }
        }
        stopForeground(true)
        stopSelf()

    }


    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this,"audio_service_channel")
            .setContentTitle("Audio Recorder")
            .setContentText("Recording audio in bg")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "audio_service_channel",
            "Audio Recorder Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    private fun startRecording(){
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            ?: filesDir //fallback to internal storage if external is not availbale

        if (!storageDir.exists()){
            if (!storageDir.mkdirs()){
                Log.e("AudioRecorderService","Failed to create storage directory")
                stopSelf()
                return
            }
        }

        audioFile = File(storageDir,"audio_${System.currentTimeMillis()}.3gp")
        Log.d("AudioRecorderService","AudioFile Path : ${audioFile?.absolutePath}")

        try{
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            lastRecordedFileUri = audioFile?.absolutePath
            Log.d("AudioRecorderService","Rec started File:${lastRecordedFileUri}")
        } catch (e: IllegalStateException){
            Log.e("AudioRecorderService","IllegalStateException during rec: ${e.message}")
            e.printStackTrace()
            stopSelf()
        } catch (e: IOException){
            Log.e("AudioRecorderService","IOException during recording: ${e.message}")
            e.printStackTrace()
            stopSelf()
        } catch (e: Exception) {
            Log.e("AudioRecorderService", "Recording failed: ${e.message}")
            e.printStackTrace()
            stopSelf()
        }
    }

    override fun onDestroy() {
        try{
            stopRecordingAndService()
        } catch (e:Exception){
            Log.e("AudioRecorderService","Error during service destruction: ${e.message}")
            e.printStackTrace()
        }
        super.onDestroy()
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}