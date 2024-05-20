package com.example.listadetarefas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


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

public class MainActivity extends AppCompatActivity {

    private ListView tasksListView;
    private ArrayList<String> taskList;
    private Button addTaskButton;
    private ArrayAdapter<String> tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}