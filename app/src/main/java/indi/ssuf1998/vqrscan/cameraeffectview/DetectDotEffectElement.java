package indi.ssuf1998.vqrscan.cameraeffectview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class DetectDotEffectElement extends EffectElement {
    private final Drawable dot;
    private final int radius;
    private int x, y;

    public DetectDotEffectElement(View view, int drawW, int drawH,
                                  Drawable dot, int radiusDP) {
        super(view, drawW, drawH);
        this.dot = dot;
        this.radius = (int) dp2px(radiusDP);

        init();
    }

    @Override
    protected void init() {
        super.init();
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(500);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dontDrawMe();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                drawMe();
            }
        });
    }

    @Override
    protected void draw(Canvas canvas) {
        if (!drawing)
            return;

        final float r = (float) (this.radius * Math.min(animPos / 0.8, 1));
        dot.setAlpha((int) (230 * Math.min(animPos / 0.8, 1)));
        dot.setBounds(
                (int) (x - r),
                (int) (y - r),
                (int) (x + r),
                (int) (y + r)
        );
        dot.draw(canvas);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
