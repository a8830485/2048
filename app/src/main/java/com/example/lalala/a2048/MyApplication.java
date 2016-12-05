package com.example.lalala.a2048;

import android.app.Application;
import android.content.Context;

/**
 * Created by lalala on 2016/12/1.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate(){
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
