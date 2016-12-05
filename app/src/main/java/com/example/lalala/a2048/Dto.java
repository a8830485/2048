package com.example.lalala.a2048;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by lalala on 2016/12/1.
 */

public class Dto {

    private int score;

    private int MaxScore;

    private Block[][] map;

    private Block[][] temp;

    private Block[][] backMap;

    private SharedPreferences pref;

    private boolean firstOpen;

    private int backTime;

    public Dto(SharedPreferences pref){
        map = new Block[4][4];
        temp = new Block[4][4];
        backMap = new Block[4][4];
        for(int i = 0;i < 4; i ++){
            for(int j = 0; j < 4;j ++){
                map[i][j] = new Block();
                temp[i][j] = new Block();
                backMap[i][j] = new Block();
            }
        }
        score = 0;
        this.pref = pref;
        load();
        if(firstOpen){
            newBlock();
            newBlock();
            mapToTemp();
            tempToBack();
            backTime = 5;
        }
    }

    /*
    将当前地图保存到临时地图里
     */
    private void mapToTemp(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                temp[i][j].value = map[i][j].value;
                temp[i][j].used = map[i][j].used;
            }
        }
    }

    /*
    将临时地图保存到上一步地图里
     */
    private void tempToBack(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                backMap[i][j].value = temp[i][j].value;
                backMap[i][j].used = temp[i][j].used;
            }
        }
    }


    private void newBlock(){
        int x, y;
        do {
            x = (int) (Math.random() * 4);
            y = (int) (Math.random() * 4);
        }while(map[x][y].value != 0);
        map[x][y].value = 2;
    }

    private void clearMap(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                map[i][j].value = 0;
                map[i][j].used = false;
            }
        }
    }

    private void load(){
        MaxScore = pref.getInt("MaxScore", 0);
        score = pref.getInt("score", 0);
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                map[i][j].value = pref.getInt("block" + i + j, 0);
                backMap[i][j].value = pref.getInt("backBlock" + i + j, 0);
            }
        }
        firstOpen = pref.getBoolean("firstOpen", true);
        backTime = pref.getInt("backTime", 5);
    }

    private void save(){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("MaxScore", MaxScore);
        editor.putInt("score", score);
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                editor.putInt("block" + i + j, map[i][j].value);
                editor.putInt("backBlock" + i + j, backMap[i][j].value);
            }
        }
        editor.putBoolean("firstOpen", false);
        editor.putInt("backTime", backTime);
        editor.commit();
    }

    public int back(){
        if(backTime == 0){
            return -1;
        }
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                map[i][j].value = backMap[i][j].value;
                map[i][j].used = backMap[i][j].used;
            }
        }
        backTime--;
        return backTime;
    }

    public void newGame(){
        Log.d("here", "here");
        clearMap();
        newBlock();
        newBlock();
        score = 0;
        backTime = 5;
        mapToTemp();
        tempToBack();
    }

    public boolean moveUp(){
        boolean flag = false;
        mapToTemp();
        int i, j, k;
        for(i = 1; i < 4; i++){
            for(j = 0; j < 4; j++){
                if(map[i][j].value == 0){
                    continue;
                }
                for(k = i; k > 0; k--){
                    if(map[k - 1][j].value == 0){
                        move(k, j, k - 1, j);
                        flag = true;
                    }else if(map[k - 1][j].value == map[k][j].value && !map[k - 1][j].used && !map[k][j].used){
                        merge(k, j, k - 1, j);
                        flag =true;
                    }else{
                        break;
                    }
                }
            }
        }
        if(flag){
            newBlock();
            tempToBack();
        }
        clearUsed();
        return canMove();
    }

    public boolean moveDown(){
        mapToTemp();
        boolean flag = false;
        int i, j, k;
        for(i = 2; i >= 0; i--){
            for(j = 0; j < 4; j++){
                if(map[i][j].value == 0){
                    continue;
                }
                for(k = i; k < 3; k++){
                    if(map[k + 1][j].value == 0){
                        move(k, j, k + 1, j);
                        flag = true;
                    }else if(map[k + 1][j].value == map[k][j].value && !map[k + 1][j].used && !map[k][j].used){
                        merge(k, j, k + 1, j);
                        flag =true;
                    }else{
                        break;
                    }
                }
            }
        }
        if(flag){
            tempToBack();
            newBlock();
        }
        clearUsed();
        return canMove();
    }

    public boolean moveLeft(){
        mapToTemp();
        boolean flag = false;
        int i, j, k;
        for(i = 1; i < 4; i++){
            for(j = 0; j < 4; j++){
                if(map[j][i].value == 0){
                    continue;
                }
                for(k = i; k > 0; k--){
                    if(map[j][k - 1].value == 0){
                        move(j, k, j, k - 1);
                        flag = true;
                    }else if(map[j][k - 1].value == map[j][k].value && !map[j][k - 1].used && !map[j][k].used){
                        merge(j, k, j, k - 1);
                        flag =true;
                    }else{
                        break;
                    }
                }
            }
        }
        if(flag){
            tempToBack();
            newBlock();
        }
        clearUsed();
        return canMove();
    }
    public boolean moveRight(){
        mapToTemp();
        boolean flag = false;
        int i, j, k;
        for(i = 2; i >= 0; i--){
            for(j = 0; j < 4; j++){
                if(map[j][i].value == 0){
                    continue;
                }
                for(k = i; k < 3; k++){
                    if(map[j][k + 1].value == 0){
                        move(j, k, j, k + 1);
                        flag = true;
                    }else if(map[j][k + 1].value == map[j][k].value && !map[j][k + 1].used && !map[j][k].used){
                        merge(j, k, j, k + 1);
                        flag =true;
                    }else{
                        break;
                    }
                }
            }
        }
        if(flag){
            tempToBack();
            newBlock();
        }
        clearUsed();
        return canMove();
    }

    /*
    将方块从x, y位置移动到x1, y1位置
     */
    private void move(int x, int y, int x1, int y1){
        map[x1][y1] .value = map[x][y].value;
        map[x1][y1].used = map[x][y].used;
        map[x][y].value = 0;
        map[x][y].used = false;
    }

    /*
    将处于x, y ; x1, y1位置的方块合并，并放置在x1, y1处
     */
    private void merge(int x, int y, int x1, int y1){
        if(map[x1][y1].value == 2048)
            return;
        map[x1][y1] .value *= 2;
        map[x1][y1].used = true;
        map[x][y].value = 0;
        map[x][y].used = false;
        addScore(map[x1][y1].value);
    }

    private void clearUsed(){
        for(int i = 0; i < 4;i++){
            for(int j = 0; j <  4;j++){
                map[i][j].used = false;
            }
        }
    }
    /*
    增加分数
    */
    private void addScore(int score){
        this.score += score;
        if(this.score > MaxScore){
            MaxScore = this.score;
        }
    }

    /*
    返回是否还可以移动
     */
    private boolean canMove(){
        int i, j, k;

        for(i = 0; i < 4; i++){
            for(j = 0; j < 4; j++){
                if(map[i][j].value == 0){
                    continue;
                }

                //向上
                if(i > 0 && (map[i - 1][j].value == 0 || map[i - 1][j].value == map[i][j].value)){
                    return true;
                }else if(i < 3 && (map[i + 1][j].value == 0 || map[i + 1][j].value == map[i][j].value)){
                    //向下
                    return true;
                }else if(j > 0 && (map[i][j - 1].value == 0 || map[i][j - 1].value == map[i][j].value)){
                    //向左
                    return true;
                }else if(j < 3 && (map[i][j + 1].value == 0 || map[i][j + 1].value == map[i][j].value)){
                    //向右
                    return true;
                }
            }
        }
        return false;
    }
    public void onClose(){
        save();
    }

    public int getMap(int i, int j){
        return map[i][j].value;
    }

    public int getScore(){
        return score;
    }

    public int getMaxScore(){
        return  MaxScore;
    }




    class Block{
        public int value;
        public boolean used;

        public Block(){
            value = 0;
            used = false;
        }
    }
}
