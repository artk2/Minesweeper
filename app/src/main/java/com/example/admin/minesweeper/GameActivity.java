package com.example.admin.minesweeper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameActivity extends AppCompatActivity {


    // GAME CONFIGURATION
    public static final int minFieldSize = 10;
    public static final int maxFieldSize = 20;
    public static final int minCellWidth = 60;
    public static final int maxCellWidth = 100;
    public static final float bombFrequency = 0.15f; // not used
    public static final String fieldColor = "#aacccc";
    /////////////////////
    private static String playerName;
    private static Game game;
    private static Button[][] btn;
    private static int screenWidth;
    private static int screenHeight;
    private static Context context;
    private static Activity activity;
    private static ViewGroup root;
    private static GridLayout grid;
    private static TextView textViewName;
    private static LayoutInflater layoutInflater;
    private static ViewGroup container;
    private static SharedPreferences prefs;
    private static final String pFile = "Top10";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setResult(1);

        grid = (GridLayout) findViewById(R.id.gridLayout);

        playerName = getIntent().getStringExtra("Name");
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewName.setText(getString(R.string.msg_welcome, playerName));

        context = this;
        activity = this;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        game = new Game(minFieldSize,maxFieldSize,bombFrequency);

    }

    static void drawField(int fieldSize){
        btn = new Button[fieldSize][fieldSize];

        grid.removeAllViews();
        grid.setColumnCount(fieldSize);
        grid.setBackgroundColor(Color.parseColor(fieldColor));

        int buttonSize;
        boolean vertical = (screenHeight > screenWidth);
        if(vertical) buttonSize = screenWidth / fieldSize;
        else buttonSize = screenHeight / fieldSize;
        buttonSize *= 0.9;
        if(buttonSize < minCellWidth) buttonSize = minCellWidth;
        if(buttonSize > maxCellWidth) buttonSize = maxCellWidth;

        for(int i = 0; i < fieldSize; i++) {
            for(int j = 0; j < fieldSize; j++) {

                btn[i][j] = new Button(context);
                LinearLayout.LayoutParams layoutParams = new  LinearLayout.LayoutParams(buttonSize , buttonSize);
//                layoutParams.setMargins(0,0,0,0);
                btn[i][j].setPadding(0,0,0,0);
                btn[i][j].setLayoutParams(layoutParams);
                final int x = i;
                final int y = j;
                btn[i][j].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(game.isPlaying()){
                            if(!game.getCells()[x][y].isOpen()) game.openCell(x,y);
                        }
                    }
                });
                grid.addView(btn[i][j]);
            }
        }
        textViewName.append(context.getString(R.string.msg_field_size, fieldSize,fieldSize));
    }

    static void updateCell(int x, int y, int result){
        textViewName.setText(context.getString(R.string.msg_current_score, game.getScore()));
        String btnText = "B";
        if (result >= 0)  btnText = String.valueOf(result);
        btn[x][y].setText(btnText);

        if (result == -1) GameActivity.endGame(false);
        if (game.getScore() == game.getMaxScore()) endGame(true);
    }

    private static void endGame(boolean win){
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        container = (ViewGroup)layoutInflater.inflate(R.layout.game_end,null);
        final PopupWindow popupWindow = new PopupWindow(container,500,750,true);
        popupWindow.showAtLocation(grid, Gravity.CENTER,0,0);

        root = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, screenWidth, screenHeight);//root.getWidth(), root.getHeight());
        dim.setAlpha((int) (255 * 0.4f));
        ViewGroupOverlay overlay = root.getOverlay();
        overlay.add(dim);

        TextView textViewResult = (TextView) container.findViewById(R.id.textResult);
        textViewResult.setTypeface(Typeface.MONOSPACE);
        if(win) textViewResult.setText(context.getString(R.string.msg_win, game.getScore()));
        else textViewResult.setText(context.getString(R.string.msg_lose, game.getScore()));

        List<String> topResults = makeTopTenList();
        textViewResult.append("\n" + context.getString(R.string.msg_top_10_scores));
        for (String result : topResults){
            String score = result.substring(0,3);
            String name = result.substring(17);
            textViewResult.append("\n" + String.format("%-5s- %s" , score, name));
        }

        Button repeatButton = (Button) container.findViewById(R.id.btnRepeat);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = new Game(game.getCells(),game.getMaxScore());
                popupWindow.dismiss();
            }
        });
        Button newGameButton = (Button) container.findViewById(R.id.btnNewGame);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = new Game(minFieldSize, maxFieldSize, bombFrequency);
                popupWindow.dismiss();
            }
        });
        Button endButton = (Button) container.findViewById(R.id.btnEnd);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                root.getOverlay().clear();
            }
        });
    }

    private static List<String> makeTopTenList(){
        String score = String.valueOf(game.getScore());
        while (score.length()<3) score = " " + score;
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // length: 14
        String entry = score + timeStamp + playerName;

        prefs = context.getSharedPreferences(pFile, MODE_PRIVATE);
        Set<String> topSet = prefs.getStringSet("Top10",new HashSet<String>());
        topSet.add(entry);
        List<String> topList = new ArrayList<String>(topSet);
        Collections.sort(topList);
        Collections.reverse(topList);
        while(topList.size()>10) topList.remove(10);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("Top10",new HashSet<String>(topList));
        editor.apply();
        return topList;
    }

}

class Game{

    private Cell[][] cells;
    private int score;
    private int maxScore;
    private boolean playing;

    public Game(int minFieldSize, int maxFieldSize, float bombFrequency){
        int fieldSize = minFieldSize + new Random().nextInt(maxFieldSize - minFieldSize + 1);
        this.cells = generateField(fieldSize, (float)fieldSize/100 /*bombFrequency*/); // dynamic frequency 0,1...0,2 (probably better)
        this.playing = true;
        this.score = 0;
        GameActivity.drawField(cells.length);
    }

    public Game(Cell[][] cells, int maxScore){
        this.cells = cells;
        for(Cell[] c : cells){
            for (Cell cell : c) cell.setOpen(false);
        }
        this.playing = true;
        this.score = 0;
        this.maxScore = maxScore;
        GameActivity.drawField(cells.length);
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
            x2 = (x < (cells.length - 1)) ? x+1 : x;
            y1 = (y > 0) ? y-1 : y;
            y2 = (y < (cells.length - 1)) ? y+1 : y;
            for (int i = x1; i <= x2; i++){
                for (int j = y1; j <= y2; j++){
                    if (cells[i][j].isBomb()){
                        result++;
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
        GameActivity.updateCell(x,y,result);
    }

    private Cell[][] generateField(int fieldSize, float bombFrequency){
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
        } while (bombsCount==0);
        this.maxScore = (fieldSize*fieldSize) - bombsCount;
        return c;
    }
}

class Cell {
    private boolean bomb;
    private boolean open;

    public Cell(float freq) {
        float f = new Random().nextFloat();
        this.bomb = f < freq;
        this.open = false;
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