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
        name = editTextName.getText().toString();
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        intent.putExtra("Name", name);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==1){
            finish();
        }
    }
}
