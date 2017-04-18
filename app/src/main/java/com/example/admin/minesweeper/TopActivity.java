package com.example.admin.minesweeper;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TopActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        textView = (TextView) findViewById(R.id.textViewTop10);
        textView.setTypeface(Typeface.MONOSPACE);
        SharedPreferences prefs = getSharedPreferences("Top10", MODE_PRIVATE);
        List<String> topList = new ArrayList<String>(prefs.getStringSet("Top10",new HashSet<String>()));
        Collections.sort(topList);
        Collections.reverse(topList);
        String text = ("Top 10 scores:\n");
        for (String result : topList){
            String score = result.substring(0,3);
            String name = result.substring(17);
            text = text + "\n" + String.format("%-4s- %s" , score, name);
            Log.i("artk", text);
        }
        textView.setText(text);
    }

}
