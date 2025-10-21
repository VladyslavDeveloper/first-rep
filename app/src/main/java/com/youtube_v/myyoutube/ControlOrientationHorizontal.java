package com.youtube_v.myyoutube;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.webkit.WebView;

public class ControlOrientationHorizontal {
    public static boolean isLandscape = false;


    public static void toggleOrientation(Activity activity) {
        isLandscape = !isLandscape;
        if (isLandscape) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);


        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
    }

    public  static void changeOrientation(View controlsLayout, WebView webView, Activity activity, Configuration newConfig){
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            JavaScript.videoFullScreen(webView);

            PanelVisible.visibleOf(controlsLayout,activity);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            JavaScript.videoFullScreenCancel(webView);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

            PanelVisible.toggleControlsVisibility(controlsLayout,activity);
        }
    }
}
