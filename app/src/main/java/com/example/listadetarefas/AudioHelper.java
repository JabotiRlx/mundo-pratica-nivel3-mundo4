package com.example.listadetarefas;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class AudioHelper {

    private final AudioManager audioManager;
    private final Context context;

    public AudioHelper(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean audioOutputAvailable(int type) {
        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
            return false;
        }
        for (AudioDeviceInfo device : audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {
            if (device.getType() == type) {
                return true;
            }
        }
        return false;
    }


//    public boolean isMicrophoneActive() {
//        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//
//        // Verifique se o microfone está mudo
//        if (audioManager != null && audioManager.isMicrophoneMute()) {
//            return false;
//        }
//
//        boolean isActive = false;
//        MediaRecorder recorder = new MediaRecorder();
//
//        try {
//            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            recorder.setOutputFile("/dev/null");
//            recorder.prepare();
//            recorder.start();
//            isActive = true;
//            recorder.stop();
//        } catch (IOException e) {
//            Log.e("AudioHelper", "Microfone já está em uso ou ocorreu um erro: " + e.getMessage());
//        } finally {
//            recorder.release();
//        }
//
//        return isActive;
//    }
}