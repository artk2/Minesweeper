package com.example.admin.minesweeper;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    static int screenWidth;
    static int screenHeight;
    static GridLayout grid;
    static TextView textViewName;
    static String name;
    static Button[][] btn;
    static Game game;
    static int fieldSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        name = getIntent().getStringExtra("Name");
        if (name == null || name.equals("")) name = "player";

        grid = (GridLayout) findViewById(R.id.gridLayout);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewName.setText("Hello, " + name);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        game = new Game(minFieldSize,maxFieldSize,bombFrequency);
        fieldSize = game.getFieldSize();
        drawField();
    }

    void drawField(){
        btn = new Button[fieldSize][fieldSize];

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
        Log.i("artk", "vertical: " + vertical);
        Log.i("artk", "screen width: " + screenWidth + ", height: " + screenHeight);
        Log.i("artk", "button amount: " + fieldSize + ", size: " + buttonSize);

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
                        if(game.isPlaying()){
                            game.openCell(x,y);
                        }
                    }
                });

                grid.addView(btn[i][j]);
            }
        }
        Log.i("artk","field drawn");
    }

    static void updateButton(int x, int y, int result){
        textViewName.setText("Your current score: " + game.getScore());
        String btnText = "B";
        if (result == -1) textViewName.setText("Game over. Your score: " + game.getScore());
        else if (result == 0) btnText = "0";
        else if (result > 0) btnText = String.valueOf(result);
        btn[x][y].setText(btnText);
    }
}

class Game{

    int fieldSize;
    Cell[][] cells;
    int score;
    int maxScore;
    boolean playing;

    Game(int minFieldSize, int maxFieldSize, float bombFrequency){
        this.fieldSize = minFieldSize + new Random().nextInt(maxFieldSize - minFieldSize + 1);
        this.cells = generateField(fieldSize, bombFrequency);
        playing = true;
        score = 0;
    }

    Game(Cell[][] cells){

    }


    Cell[][] generateField(int fieldSize, float bombFrequency){
        int bombsCount;
        Cell[][] c;
        do { // prevent from creating empty field (not likely to happen)
            bombsCount = 0;
            c = new Cell[fieldSize][fieldSize];
            for (int i = 0; i < fieldSize; i++) {
                for (int j = 0; j < fieldSize; j++) {
                    c[i][j] = new Cell(bombFrequency);
                    if (c[i][j].isBomb()) bombsCount++;
                }
            }
            Log.i("artk","Generated field with " + bombsCount + " bombs");
        } while (bombsCount==0);
        this.maxScore = (fieldSize*fieldSize) - bombsCount;
        return c;
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
        GameActivity.updateButton(x,y,result);
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }
}

class Cell {
    private boolean bomb;
    private boolean open = false; // not necessary (?)

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