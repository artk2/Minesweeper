package com.example.admin.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EnterNameActivity extends AppCompatActivity {

    static EditText editTextName;
    static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);
        editTextName = (EditText)findViewById(R.id.editTextName);
    }

    void startButtonPressed(View v){
        startActivity(new Intent(this, EnterNameActivity.class));
        name = editTextName.getText().toString();
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        intent.putExtra("Name", name);
        startActivity(intent);
    }
}
