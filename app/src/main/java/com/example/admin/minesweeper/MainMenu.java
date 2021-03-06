package com.example.admin.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void playButtonPressed(View v){
        startActivity(new Intent(this, EnterNameActivity.class));
    }

    public void topButtonPressed(View v){
        startActivity(new Intent(this, TopActivity.class));
    }
}
