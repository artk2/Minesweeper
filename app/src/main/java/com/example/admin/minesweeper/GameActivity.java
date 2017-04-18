package com.example.admin.minesweeper;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {


    // GAME CONFIGURATION
    public final int minFieldSize = 5;
    public final int maxFieldSize = 10;
    public final int minCellWidth = 60;
    public final int maxCellWidth = 100;
    public final float bombFrequency = 0.2f;
    public final String fieldColor = "#aacccc";
    //
    TextView textViewName;
    String name;
    int fieldSize;
    int screenWidth;
    int screenHeight;
    Cell[][] cells;
    Button[][] btn;
    int score;
    int maxScore;
    boolean playing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        name = getIntent().getStringExtra("Name");
        if (name==null || name.equals("")) name = "player";
        textViewName = (TextView)findViewById(R.id.textViewName);
        textViewName.setText("Hello, " + name);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        fieldSize = minFieldSize + new Random().nextInt(maxFieldSize - minFieldSize + 1);
//        textViewName.setText(fieldSize + "");
        generateField();
        drawField();
        playing = true;
        score = 0;
    }

    void generateField(){
        int bombsCount;
        do { // prevent from creating empty field (not likely to happen)
            bombsCount = 0;
            cells = new Cell[fieldSize][fieldSize];
            for (int i = 0; i < fieldSize; i++) {
                for (int j = 0; j < fieldSize; j++) {
                    cells[i][j] = new Cell(bombFrequency);
                    if (cells[i][j].isBomb()) bombsCount++;
                }
            }
//            Log.i("artk","Generated field with " + bombsCount + " bombs");
        } while (bombsCount==0);
        maxScore = (fieldSize*fieldSize) - bombsCount;
    }

    void drawField(){
        btn = new Button[fieldSize][fieldSize];

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout);
        grid.setColumnCount(fieldSize);
        grid.setBackgroundColor(Color.parseColor(fieldColor));

        int buttonSize;
        boolean vertical = (screenHeight > screenWidth);
        if(vertical) buttonSize = screenWidth / fieldSize;
        else buttonSize = screenHeight / fieldSize;
        buttonSize *= 0.9;
//        textViewName.setText(fieldSize + "");
        if(buttonSize < minCellWidth) buttonSize = minCellWidth;
        if(buttonSize > maxCellWidth) buttonSize = maxCellWidth;
//        Log.i("artk", "vertical: " + vertical);
//        Log.i("artk", "screen width: " + screenWidth + ", height: " + screenHeight);
//        Log.i("artk", "button amount: " + fieldSize + ", size: " + buttonSize);

        String btnText;
        for(int i = 0; i < fieldSize; i++) {
            for(int j = 0; j < fieldSize; j++) {

                btn[i][j] = new Button(this);
//                if (cells[i][j].isBomb()) btnText = "B";
//                else btnText = "";
//                btn[i][j].setText(btnText);

                LinearLayout.LayoutParams layoutParams = new  LinearLayout.LayoutParams(buttonSize , buttonSize);
                layoutParams.setMargins(1, 1, 1, 1);
//                btn[i][j].setHeight(buttonSize);
//                btn[i][j].setWidth(buttonSize);
                btn[i][j].setPadding(0,0,0,0);
                btn[i][j].setLayoutParams(layoutParams);
                final int x = i;
                final int y = j;
                btn[i][j].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(playing) openCell(x,y);
                        textViewName.setText("Your current score: " + score);
                    }
                });

                grid.addView(btn[i][j]);
            }
        }
    }

    void openCell(int x, int y){
        int result;
        cells[x][y].setOpen(true);
        if (cells[x][y].isBomb()){
            result = -1;
            playing = false;
        }
        else {
            score++;
            result = 0;
            int x1, x2, y1, y2;
            x1 = (x > 0) ? x-1 : x;
            x2 = (x < (fieldSize - 1)) ? x+1 : x;
            y1 = (y > 0) ? y-1 : y;
            y2 = (y < (fieldSize - 1)) ? y+1 : y;
            for (int i = x1; i <= x2; i++){
                for (int j = y1; j <= y2; j++){
                    if (cells[i][j].isBomb()){
                        result++;
//                        Log.i("artk","Checking cell: " + x + "," + y);
//                        Log.i("artk","Bomb at: " + i + "," + j);
                    }
                }
            }
            if (result == 0){ // open neighbour fields (no bombs there)
                for (int i = x1; i <= x2; i++) {
                    for (int j = y1; j <= y2; j++) {
                        if(!(cells[i][j].isOpen())) openCell(i,j);
                    }
                }
            }
        }
        String btnText = "B";
        if (result == 0) btnText = "0";
        else if (result > 0) btnText = String.valueOf(result);
        btn[x][y].setText(btnText);

        if (!playing) textViewName.setText("Game over. Your score: " + score);
//        btn[x][y].setBackgroundColor(Color.parseColor("#cccccc"));
    }

}

class Cell {
    private boolean bomb;
    private boolean open = false;

    public Cell(float freq) {
        Random r = new Random();
        float f = r.nextFloat();
        this.bomb = f < freq;
    }

    public boolean isBomb() {
        return bomb;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}