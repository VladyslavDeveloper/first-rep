package com.youtube_v.myyoutube;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.civ3.R;

public class PanelVisible {
    public static boolean isControlVisible = true;  // Инициализация переменной


    public static void visibleOf(View controlsLayout, Activity activity) {
        controlsLayout.setVisibility(View.GONE);
        Joystick.joystickView.setVisibility(View.GONE);
        // Set video container height to 0dp
        FrameLayout videoContainer = activity.findViewById(R.id.controls_scroll);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoContainer.getLayoutParams();
        params.height = 0; // Set height to 0dp
        videoContainer.setLayoutParams(params);
    }

    public static void toggleControlsVisibility(View controlsLayout, Activity activity) {
        if (controlsLayout.getVisibility() == View.VISIBLE) {
            visibleOf(controlsLayout,activity);
        } else {
            controlsLayout.setVisibility(View.VISIBLE);
            Joystick.joystickView.setVisibility(View.VISIBLE);
            // Optionally set video container height back to wrap_content or desired height
            FrameLayout videoContainer = activity.findViewById(R.id.controls_scroll);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoContainer.getLayoutParams();
            params.height = 234;
            videoContainer.setLayoutParams(params);
        }
    }
}
