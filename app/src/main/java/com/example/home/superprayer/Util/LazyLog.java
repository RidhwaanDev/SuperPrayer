package com.example.home.superprayer.Util;

import android.content.Context;
import android.util.Log;

/**
 * Created by Home on 1/13/2018.
 *
 *
 * For some reason the normal Log.d command is extremely annoying to type out. Therefore I have created a grand and innovative new method to take care of that crap
 */

public class LazyLog {

    public static void log(String c, String message){
        Log.d(c,"  " + message);
    }
}
