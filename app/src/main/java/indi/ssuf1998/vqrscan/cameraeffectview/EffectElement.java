package indi.ssuf1998.vqrscan.cameraeffectview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public abstract class EffectElement {
    protected float animPos = 0;
    protected final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    protected final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected boolean drawing = false;

    protected int drawW, drawH;
    protected View view;

    public EffectElement(View view, int drawW, int drawH) {
        this.view = view;
        this.drawW = drawW;
        this.drawH = drawH;
    }

    protected void init() {
        animator.addUpdateListener(anim -> {
            animPos = (float) anim.getAnimatedValue();
            view.invalidate();
        });
    }

    protected abstract void draw(Canvas canvas);

    public ValueAnimator getAnimator() {
        return animator;
    }

    public void dontDrawMe() {
        drawing = false;
    }

    public void drawMe() {
        drawing = true;
    }

    protected float dp2px(float dp) {
        return dp * view.getContext().getResources().getDisplayMetrics().density;
    }
}
