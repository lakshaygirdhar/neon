package com.gaadi.neon.util;

import android.app.Application;
import java.util.ArrayList;

/**
 * Created by Lakshay
 * @since 12-03-2015.
 *
 */
public class ApplicationController extends Application {


    public static ArrayList<String> selectedFiles = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
