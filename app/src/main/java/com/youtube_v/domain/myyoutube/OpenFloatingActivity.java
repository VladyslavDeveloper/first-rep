package com.youtube_v.domain.myyoutube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.webkit.WebView;

public class OpenFloatingActivity {
    private static final int REQUEST_CODE = 101;

    public static void checkOverlayPermission(Context context, Activity activity,WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_CODE);
            } else {
                startFloatingActivity(context,activity,webView);
            }
        } else {
            startFloatingActivity(context,activity,webView);
        }
    }

    public static void startFloatingActivity(Context context, Activity activity,WebView webView) {
        String currentUrl = webView.getUrl(); // Get current URL from WebView
        Intent intent = new Intent(context, FloatingActivity.class);
        intent.putExtra("video_url", currentUrl); // Pass URL in Intent
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        activity.finish(); // Close MainActivity
    }

}
