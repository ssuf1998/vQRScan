package indi.ssuf1998.vqrscan.cameraeffectview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import indi.ssuf1998.vqrscan.R;

public class CameraEffectViewX extends View {
    public CameraEffectViewX(Context context) {
        this(context, null, 0, 0);
    }

    public CameraEffectViewX(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public CameraEffectViewX(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    private boolean drawReady = false;

    private ScanBarEffectElement scanBar;
    private DetectDotEffectElement detectDot;
    private FocusDotEffectElement focusDot;
    private TorchEffectElement torch;

    public CameraEffectViewX(Context context, @Nullable AttributeSet attrs,
                             int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setClickable(true);
        setFocusable(true);

        post(this::initDrawing);
    }

    private void initDrawing() {

        scanBar = new ScanBarEffectElement(
                this,
                getWidth(), getHeight(),
                BitmapFactory.decodeResource(getResources(), R.drawable.scan_bar),
                Color.WHITE,
                0.9f, 0.33f, 0.25f
        );

        detectDot = new DetectDotEffectElement(
                this,
                getWidth(), getHeight(),
                ContextCompat.getDrawable(getContext(), R.drawable.detect_dot),
                24
        );

        focusDot = new FocusDotEffectElement(
                this,
                getWidth(), getHeight(),
                Color.WHITE,
                32
        );

        torch = new TorchEffectElement(
                this,
                getWidth(), getHeight(),
                "轻触打开闪光灯",
                Color.WHITE,
                16,
                0.5f
        );

        drawReady = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (!drawReady)
            return;

        scanBar.draw(canvas);
        detectDot.draw(canvas);
        focusDot.draw(canvas);
        torch.draw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            focusDot.setX((int) event.getX());
            focusDot.setY((int) event.getY());
            focusDot.getAnimator().start();
        }

        return super.onTouchEvent(event);
    }

    public DetectDotEffectElement getDetectDot() {
        return detectDot;
    }

    public ScanBarEffectElement getScanBar() {
        return scanBar;
    }

    public TorchEffectElement getTorch() {
        return torch;
    }
}
