package com.youtube_v.domain.myyoutube;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {
    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    private float joystickX;
    private float joystickY;
    private JoystickListener joystickListener;
    private Paint basePaint;
    private Paint hatPaint;
    private boolean isPressed;

    public interface JoystickListener {
        void onJoystickMoved(float xPercent, float yPercent);
        void onJoystickReleased();
    }

    public JoystickView(Context context) {
        super(context);
        init();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        basePaint = new Paint();
        basePaint.setColor(Color.GRAY);
        basePaint.setAlpha(50);
        basePaint.setStyle(Paint.Style.FILL);
        basePaint.setAntiAlias(true);

        hatPaint = new Paint();
        hatPaint.setColor(Color.DKGRAY);
        hatPaint.setAlpha(150);
        hatPaint.setStyle(Paint.Style.FILL);
        hatPaint.setAntiAlias(true);
    }

    public void setJoystickListener(JoystickListener listener) {
        this.joystickListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        baseRadius = Math.min(w, h) / 2f;
        hatRadius = Math.min(w, h) / 5f;
        joystickX = centerX;
        joystickY = centerY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint);
        canvas.drawCircle(joystickX, joystickY, hatRadius, hatPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPressed = true;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - centerX;
                float dy = event.getY() - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < baseRadius) {
                    joystickX = event.getX();
                    joystickY = event.getY();
                } else {
                    float ratio = (float) (baseRadius / distance);
                    joystickX = centerX + dx * ratio;
                    joystickY = centerY + dy * ratio;
                }

                if (joystickListener != null && isPressed) {
                    float xPercent = (joystickX - centerX) / baseRadius;
                    float yPercent = (joystickY - centerY) / baseRadius;
                    joystickListener.onJoystickMoved(xPercent, yPercent);
                }
                break;

            case MotionEvent.ACTION_UP:
                isPressed = false;
                joystickX = centerX;
                joystickY = centerY;
                if (joystickListener != null) {
                    joystickListener.onJoystickReleased();
                }
                break;
        }

        invalidate();
        return true;
    }
} 