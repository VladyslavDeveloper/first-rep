package com.example.myyoutube;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class SizeFloatingActivity {
    private static LinearLayout sizeControlLayout;
    private static SeekBar sizeSeekBar;
    private boolean isSizeControlVisible = false;
    private int initialWidth;
    private int initialHeight;
    public static final int STATIC_WIDTH = 370; // Static width in dp
    public static final float DEFAULT_HEIGHT_PERCENT = 0.5f; // 40% of screen height



    public static void setupSizeControl(Activity activity, WindowManager.LayoutParams params, WindowManager windowManager, LinearLayout linearLayout1) {
        int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;

        sizeControlLayout = linearLayout1.findViewById(R.id.sizeControlLayout);
        sizeSeekBar = linearLayout1.findViewById(R.id.sizeSeekBar);

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float scale = Math.max(0.04f, progress / 100f);
                    params.height = (int) (screenHeight * scale);
                    windowManager.updateViewLayout(linearLayout1, params);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


}
