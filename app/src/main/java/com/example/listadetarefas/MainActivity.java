package com.example.listadetarefas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioDeviceCallback;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.media.AudioDeviceInfo;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ListView tasksListView;
    private ArrayList<String> taskList;
    private Button addTaskButton;
    private ArrayAdapter<String> tasksAdapter;

    private AudioHelper audioHelper;

    private boolean isSpeakerAvailable;
    private boolean isBluetoothHeadsetConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isTalkBackEnabled()) {
            showTalkBackInstructions();
        }

        //conexao de audio
        audioHelper = new AudioHelper(this);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        isSpeakerAvailable = audioHelper.audioOutputAvailable(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);

        Toast.makeText(MainActivity.this, "Favor conectar Fone de ouvido Bluetooth!", Toast.LENGTH_SHORT).show();
        if( !isSpeakerAvailable ){
            isBluetoothHeadsetConnected = audioHelper.audioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);
            if( !isBluetoothHeadsetConnected ) {
                openBluetoothSettings();
            }
        }

        Log.d("isSpeakerAvailable",  Boolean.toString(isSpeakerAvailable ));
        Log.d("isBluetoothHeadsetConnected",  Boolean.toString(isBluetoothHeadsetConnected ));
        audioManager.registerAudioDeviceCallback(new AudioDeviceCallback() {
            @Override
            public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                super.onAudioDevicesAdded(addedDevices);
                for (AudioDeviceInfo device : addedDevices) {
                    if (device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
                        // Um fone de ouvido Bluetooth acabou de ser conectado
                        Toast.makeText(MainActivity.this, "Fone de ouvido Bluetooth conectado", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                super.onAudioDevicesRemoved(removedDevices);
                for (AudioDeviceInfo device : removedDevices) {
                    if (device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
                        // Um fone de ouvido Bluetooth não está mais conectado
                        Toast.makeText(MainActivity.this, "Fone de ouvido Bluetooth desconectado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, null);


        //Lista
        tasksAdapter = new ArrayAdapter<>(  this,android.R.layout.simple_list_item_1);
        ListView tasksListView = findViewById(R.id.taskListView);
        tasksListView.setAdapter(tasksAdapter);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult (
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult (ActivityResult result) {
                        if (result.getResultCode () == Activity.RESULT_OK) {
                            // Adicionar a tarefa ao adaptador
                            tasksAdapter.add (result.getData().getStringExtra(  "task"));
                        }
                    }
                }
        );

        addTaskButton = findViewById(R.id.addTaskButton);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityResultLauncher.launch(new Intent(MainActivity.this, AddTaskActivity.class));
            }
        });

        tasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Completar tarefa");
                dialog.setMessage("Deseja completar a tarefa?");
                dialog.setPositiveButton("sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tasksAdapter.remove(tasksAdapter.getItem(position));
                    }
                });
                dialog.setNegativeButton("Não", null);
                dialog.create();
                dialog.show();
                return true;
            }

        });
    }

    private void openBluetoothSettings() {
        Log.d("MainActivity", "testeeee");
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("EXTRA_CONNECTION_ONLY", true);
        intent.putExtra("EXTRA_CLOSE_ON_CONNECT", true);
        intent.putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 1);
        startActivity(intent);
    }


    private boolean isTalkBackEnabled() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return accessibilityEnabled == 1;
    }

    private void showTalkBackInstructions() {
        new AlertDialog.Builder(this)
                .setTitle("Ativar TalkBack")
                .setMessage("Para usar este aplicativo com TalkBack, vá para Configurações -> Acessibilidade -> TalkBack e ative-o.")
                .setPositiveButton("Ir para Configurações", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}