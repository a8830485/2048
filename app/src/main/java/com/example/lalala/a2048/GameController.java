package com.example.lalala.a2048;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by lalala on 2016/12/1.
 */

public class GameController implements View.OnClickListener, View.OnTouchListener{

    private Dto dto;

    private Handler handler;

    private double x1, x2, y1, y2;

    public GameController(Dto dto, Handler handler){
        this.dto = dto;
        this.handler = handler;
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_replace:
                Toast.makeText(MyApplication.getContext(), "新游戏", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dto.newGame();
                        Message msg = new Message();
                        msg.what = MainActivity.REDRAW;
                        handler.sendMessage(msg);
                    }
                }).start();
                break;
            case R.id.button_back:
                //Toast.makeText(MyApplication.getContext(), "后退按钮", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int backTime = dto.back();
                        Message msg = new Message();
                        msg.what = MainActivity.BACK;
                        msg.arg1 = backTime;
                        handler.sendMessage(msg);
                    }
                }).start();
                break;
            case R.id.button_menu:
              // Toast.makeText(MyApplication.getContext(), "菜单按钮", Toast.LENGTH_SHORT).show();
                Message msg = new Message();
                msg.what = MainActivity.SHOWMENU;
                handler.sendMessage(msg);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        int action = event.getAction();
        if(action  == MotionEvent.ACTION_DOWN){
            x1 = event.getX();
            y1 = event.getY();
        }
        if(action  == MotionEvent.ACTION_UP){
            x2 = event.getX();
            y2 = event.getY();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    double x = Math.abs(x1 - x2);
                    double y = Math.abs(y1 - y2);
                    boolean gameOver = true;
                    if(x > y){
                        if(x1 - x2 > 50){
                            gameOver = dto.moveLeft();
                            Log.d("xiangzuo", "xiangzuo");
                            //向左
                            //Toast.makeText(MyApplication.getContext(), "向左", Toast.LENGTH_SHORT).show();
                        }
                        else if(x1 - x2 < -50){
                            //向右
                            gameOver = dto.moveRight();
                            Log.d("xiangyou", "xiangyou");
                            //Toast.makeText(MyApplication.getContext(), "向右", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if(y1 - y2 > 50){
                            //向上
                            gameOver = dto.moveUp();
                            Log.d("xiangzuo", "xiangshang");
                            //Toast.makeText(MyApplication.getContext(), "向上", Toast.LENGTH_SHORT).show();
                        }else if(y1 - y2 < -50){
                            //向下
                            gameOver = dto.moveDown();
                            Log.d("xiangzuo", "xiangxia");
                            //  Toast.makeText(MyApplication.getContext(), "向下", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Message msg = new Message();
                    msg.what = MainActivity.REDRAW;
                    handler.sendMessage(msg);
                    if(!gameOver){
                        msg = new Message();
                        msg.what = MainActivity.GAMEOVER;
                        handler.sendMessage(msg);
                    }
                }
            }).start();

        }

        return true;
    }
}
