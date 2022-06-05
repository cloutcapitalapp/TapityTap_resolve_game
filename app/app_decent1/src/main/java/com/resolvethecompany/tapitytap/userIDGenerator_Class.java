package com.resolvethecompany.tapitytap;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Random;


public class userIDGenerator_Class {

    final int min = 111111111;
    final int max = 999999999;
    final int random = new Random().nextInt((max - min) + 1) + min;

    public Integer initUserID(){
        Log.d("random_check", "" + random);
        return random;
    }
}
