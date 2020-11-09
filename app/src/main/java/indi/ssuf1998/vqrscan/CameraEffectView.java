package indi.ssuf1998.vqrscan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CameraEffectView extends View {
    public CameraEffectView(Context context) {
        this(context, null, 0, 0);
    }

    public CameraEffectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public CameraEffectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    private boolean drawReady = false;

    private float focusDotAnimValue = 0;
    private final ValueAnimator focusDotAnim = ValueAnimator.ofFloat(0, 1);
    private final Paint focusDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean drawingFocusDot = false;
    private float touchedX = -1, touchedY = -1;

    private float scanBarAnimValue = 0;
    private final ValueAnimator scanBarAnim = ValueAnimator.ofFloat(0, 1);
    private Bitmap coloredScanBarBmp;
    private final Paint coloredScanBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float detectDotAnimValue = 0;
    private final ValueAnimator detectDotAnim = ValueAnimator.ofFloat(0, 1);
    private boolean drawingDetectDot = false;
    private float detectX, detectY;

    public CameraEffectView(Context context, @Nullable AttributeSet attrs,
                            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setClickable(true);
        setFocusable(true);

        initDrawing();
    }

    private void initDrawing() {
        focusDotPaint.setStyle(Paint.Style.FILL);
        focusDotPaint.setColor(Color.WHITE);

        focusDotAnim.setInterpolator(new DecelerateInterpolator());
        focusDotAnim.setDuration(1500);

        focusDotAnim.addUpdateListener(anim -> {
            focusDotAnimValue = (float) anim.getAnimatedValue();
            invalidate();
        });

        focusDotAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawingFocusDot = false;
                invalidate();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                drawingFocusDot = true;
            }
        });

        detectDotAnim.setInterpolator(new DecelerateInterpolator());
        detectDotAnim.setDuration(500);

        detectDotAnim.addUpdateListener(anim -> {
            detectDotAnimValue = (float) anim.getAnimatedValue();
            invalidate();
        });

        detectDotAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawingDetectDot = false;
                invalidate();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                drawingDetectDot = true;
            }
        });

        scanBarAnim.setInterpolator(new LinearInterpolator());
        scanBarAnim.setDuration(2000);
        scanBarAnim.setRepeatCount(ValueAnimator.INFINITE);
        scanBarAnim.setRepeatMode(ValueAnimator.RESTART);

        scanBarAnim.addUpdateListener(anim -> {
            scanBarAnimValue = (float) anim.getAnimatedValue();
            invalidate();
        });

        post(() -> {
            colorizeScanBar();
            drawReady = true;
        });
    }

    private void colorizeScanBar() {
        final Bitmap scanBarBmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.scan_bar);

        coloredScanBarBmp = Utils.applyColorMaskBmp(scanBarBmp, Color.WHITE);

        final int scaledW = (int) (getWidth() * 0.9f);
        final int scaledH = (int) (scaledW *
                ((float) coloredScanBarBmp.getHeight() / coloredScanBarBmp.getWidth()));
        coloredScanBarBmp = Bitmap.createScaledBitmap(
                coloredScanBarBmp,
                scaledW, scaledH,
                true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (!drawReady)
            return;

        drawScanBar(canvas);
        drawFocusDot(canvas);
        drawDetectDot(canvas);
    }

    private void drawFocusDot(Canvas canvas) {
        if (!drawingFocusDot)
            return;

        focusDotPaint.setAlpha((int) (192 * Math.min(focusDotAnimValue / 0.6, 1)));
        canvas.drawCircle(
                touchedX, touchedY,
                (float) (64 * (1 - (Math.min(focusDotAnimValue / 0.6, 1))) + 128),
                focusDotPaint
        );
    }

    private void drawDetectDot(Canvas canvas) {
        if (!drawingDetectDot)
            return;

        final Drawable dot = ContextCompat.getDrawable(getContext(), R.drawable.detect_dot);
        final float r = (float) (dp2px(24) * Math.min(detectDotAnimValue / 0.8, 1));

        dot.setAlpha((int) (230 * Math.min(detectDotAnimValue / 0.8, 1)));
        dot.setBounds(
                (int) (detectX - r),
                (int) (detectY - r),
                (int) (detectX + r),
                (int) (detectY + r)
        );
        dot.draw(canvas);
    }

    private void drawScanBar(Canvas canvas) {
        final int centerLeft = (getWidth() - coloredScanBarBmp.getWidth()) >> 1;
        final int startTop = (int) (getHeight() * 0.25f);
        final int topRange = getHeight() / 3;

        if (scanBarAnimValue <= 0.15) {
            coloredScanBarPaint.setAlpha((int) (255 * scanBarAnimValue / 0.15));
        } else if (scanBarAnimValue >= 0.85) {
            coloredScanBarPaint.setAlpha((int) (255 * (1 - scanBarAnimValue) / 0.15));
        } else {
            coloredScanBarPaint.setAlpha(255);
        }

        canvas.drawBitmap(
                coloredScanBarBmp,
                centerLeft, startTop + scanBarAnimValue * topRange,
                coloredScanBarPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            touchedX = event.getX();
            touchedY = event.getY();
            focusDotAnim.start();
        }

        return super.onTouchEvent(event);
    }

    public ValueAnimator getScanBarAnim() {
        return scanBarAnim;
    }

    public ValueAnimator getDetectDotAnim() {
        return detectDotAnim;
    }

    public void setDetectDotPos(float x, float y) {
        detectX = x;
        detectY = y;
    }

    private float dp2px(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}
