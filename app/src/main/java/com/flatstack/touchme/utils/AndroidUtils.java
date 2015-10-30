package com.flatstack.touchme.utils;

import android.util.DisplayMetrics;

import com.flatstack.touchme.App;

/**
 * Created by Ilya Eremin on 10/30/15.
 */
public class AndroidUtils {

    public static float density = 1;
    public static DisplayMetrics displayMetrics;

    private static Boolean isTablet = null;

    public static Integer photoSize = null;

    static {
        displayMetrics = App.applicationContext.getResources().getDisplayMetrics();
        density = displayMetrics.density;
    }


    public static int dp(float value) {
        return (int) Math.ceil(density * value);
    }

}
