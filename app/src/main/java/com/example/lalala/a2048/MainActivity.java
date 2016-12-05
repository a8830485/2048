package com.example.lalala.a2048;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mainActivity;

    private TextView score;

    private TextView maxScore;

    private Button button_replace;

    private Button button_back;

    private Button button_menu;

    private View layout_game;

    private View layout_score;

    private View layout_button;

    private ImageView[][] blocks;

    private Dto dto;

    private Map<Integer, Integer> blockMap;

    private GameController ctrl;

    public static final int REDRAW = 1;

    public static final int GAMEOVER = 2;

    public static final int SHOWMENU  = 3;

    public static final int BACK = 4;

    private int screenWidth;

    private int screenHeight;


    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case REDRAW:
                    //Toast.makeText(MainActivity.this, "收到了", Toast.LENGTH_SHORT).show();
                    redraw();
                    break;
                case GAMEOVER:
                    Toast.makeText(MainActivity.this, "游戏结束", Toast.LENGTH_SHORT).show();
                    break;
                case BACK:
                    redraw();
                    int backTime = msg.arg1;
                    if(backTime >= 0) {
                        Toast.makeText(MainActivity.this, "本局游戏你还可以后退一步" + backTime + "次", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "本局游戏的后退一步次数已用完", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SHOWMENU:
                    Dialog dlg = new AlertDialog.Builder(MainActivity.this).create();
                    dlg.show();
                    dlg.getWindow().setContentView(R.layout.layout_menu);
                    hide();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dto = new Dto(getSharedPreferences("data", MODE_PRIVATE));
        ctrl = new GameController(dto, handler);
        initCompnent();
        adapt();
        redraw();
        hide();

    }

    @Override
    protected void onStart(){
        super.onStart();
        hide();
    }

    @Override
    protected void onStop(){
        super.onStop();
        dto.onClose();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        dto.onClose();
    }



    private void adapt(){
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        screenWidth = mDisplayMetrics.widthPixels;
        screenHeight = mDisplayMetrics.heightPixels;
        Toast.makeText(MainActivity.this, screenHeight + "*" + screenWidth, Toast.LENGTH_SHORT).show();

        //设置layout_score 宽高
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)layout_score.getLayoutParams();
        params.width = screenWidth;
        params.height = screenHeight * 2 / 10;
        layout_score.setLayoutParams(params);
        //设置分数字体大小与间隔
        score.setTextSize(params.width / 30);
        maxScore.setTextSize(params.width / 30);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, 1);
        lp.setMargins(5, params.height * 1 /3 , 5, 5);
        score.setLayoutParams(lp);
        maxScore.setLayoutParams(lp);

        //设置layout_button宽高
        params = (LinearLayout.LayoutParams)layout_button.getLayoutParams();
        params.width = screenWidth;
        params.height = screenHeight * 2 / 10;
        layout_button.setLayoutParams(params);

        //设置layout_game宽高
        params = (LinearLayout.LayoutParams)layout_game.getLayoutParams();
        params.height = params.width;
        layout_game.setLayoutParams(params);
    }

    private void initCompnent(){
        blockMap = new HashMap<Integer, Integer>();
        blockMap.put(0, R.drawable.nullblock);
        blockMap.put(2, R.drawable.two);
        blockMap.put(4, R.drawable.four);
        blockMap.put(8, R.drawable.eight);
        blockMap.put(16, R.drawable.sixteen);
        blockMap.put(32, R.drawable.thirtytwo);
        blockMap.put(64, R.drawable.sixtyfour);
        blockMap.put(128, R.drawable.yeb);
        blockMap.put(256, R.drawable.ewl);
        blockMap.put(512, R.drawable.wye);
        blockMap.put(1024, R.drawable.yles);
        blockMap.put(2048, R.drawable.elsb);

        mainActivity = findViewById(R.id.activity_main);

        score = (TextView) findViewById(R.id.text_score);

        maxScore = (TextView) findViewById(R.id.text_max_score);

        button_back = (Button) findViewById(R.id.button_back);
        button_back.setOnClickListener(ctrl);

        button_menu = (Button) findViewById(R.id.button_menu);
        button_menu.setOnClickListener(ctrl);

        button_replace = (Button) findViewById(R.id.button_replace);
        button_replace.setOnClickListener(ctrl);

        layout_game = findViewById(R.id.layout_game);
        layout_game.setOnTouchListener(ctrl);

        layout_button = findViewById(R.id.layout_button);

        layout_score = findViewById(R.id.layout_score);

        blocks = new ImageView[4][4];
        int id = R.id.block_00;
        for (int i = 0; i <  4; i++){
            for(int j = 0; j < 4; j++){
                blocks[i][j] = (ImageView)findViewById(id++);
            }
        }
    }

    private void redraw(){
        for (int i = 0; i <  4; i++){
            for(int j = 0; j < 4; j++){
                blocks[i][j].setImageResource(blockMap.get(dto.getMap(i, j)));
            }
        }
        maxScore.setText(dto.getMaxScore()+"");
        score.setText(dto.getScore()+"");
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mainActivity.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Handler mHideHandler = new Handler();


}
