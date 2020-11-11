package indi.ssuf1998.vqrscan.cameraeffectview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class FocusDotEffectElement extends EffectElement {
    private final int dotColor;
    private final int radius;
    private int x, y;

    public FocusDotEffectElement(View view, int drawW, int drawH,
                                 int dotColor, int radiusDP) {
        super(view, drawW, drawH);
        this.dotColor = dotColor;
        this.radius = (int) dp2px(radiusDP);

        init();
    }

    @Override
    protected void init() {
        super.init();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(dotColor);

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(1500);

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

        paint.setAlpha((int) (192 * Math.min(animPos / 0.6, 1)));
        canvas.drawCircle(
                x, y,
                (float) (radius * (1 - (Math.min(animPos / 0.6, 1))) + 128),
                paint
        );
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
