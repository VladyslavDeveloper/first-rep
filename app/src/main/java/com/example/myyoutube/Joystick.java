package com.example.myyoutube;

import android.os.Handler;
import android.webkit.WebView;

public class Joystick {

    public static JoystickView joystickView;
    public static Handler joystickHandler;
    public static Runnable joystickRunnable;
    public static boolean isJoystickActive = false;

    public static void setupJoystickControl(WebView webView) {
        joystickHandler = new Handler();
        joystickRunnable = new Runnable() {
            @Override
            public void run() {
                if (isJoystickActive) {
                    JavaScript.makeJoystick(webView,1,1);
                    joystickHandler.postDelayed(this, 200);
                }
            }
        };

        joystickView.setJoystickListener(new JoystickView.JoystickListener() {
            @Override
            public void onJoystickMoved(float xPercent, float yPercent) {
                if (!isJoystickActive) {
                    isJoystickActive = true;
                    joystickHandler.post(joystickRunnable);
                }
                JavaScript.makeJoystick(webView,2, xPercent);
            }

            @Override
            public void onJoystickReleased() {
                isJoystickActive = false;
                JavaScript.makeJoystick(webView,3,3);
            }
        });
    }

}
