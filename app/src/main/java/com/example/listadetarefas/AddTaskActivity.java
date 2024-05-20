package com.example.listadetarefas;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AddTaskActivity extends AppCompatActivity {
    private Button addTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obter o texto da tarefa
                String task = ((EditText) findViewById(R.id.taskEditText)).getText().toString();

                // Criar intent do resultado
                Intent resultIntent = new Intent();
                resultIntent.putExtra("task", task);

                // Definir o resultado e terminar a atividade
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}