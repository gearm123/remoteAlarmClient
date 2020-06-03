package com.mgroup.remotealarm;

import android.os.Build;

public class Utilities {

    public static boolean isGreaterThanOreo(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return true;
        }
        return false;
        }

    }
