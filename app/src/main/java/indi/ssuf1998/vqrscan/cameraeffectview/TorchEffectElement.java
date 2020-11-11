package indi.ssuf1998.vqrscan.cameraeffectview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.graphics.ColorUtils;

public class TorchEffectElement extends EffectElement {
    private final static int TIP_ICON_SPACE = 16;

    //    private final Drawable torchIco;
    private final String tipText;
    //    private final int torchIcoSize;
    private final int tipTextSize;
    private final int color;

    private final int offsetY;

    //    private int torchTop;
    private int tipTop;
    //    private int torchLeft;
    private int tipLeft;

    public TorchEffectElement(View view, int drawW, int drawH,
                              String tipText,
                              int color,
                              int tipTextSizeDP,
                              float offsetYRatio) {
        super(view, drawW, drawH);
//        this.torchIco = torchIco;
        this.tipText = tipText;
//        this.torchIcoSize = (int) dp2px(torchIcoSizeDP);
        this.tipTextSize = (int) dp2px(tipTextSizeDP);
        this.color = color;

        this.offsetY = (int) (offsetYRatio * drawH);

        init();
    }

    @Override
    protected void init() {
        super.init();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setTextSize(tipTextSize);

//        torchIco.setTint(color);

        final Rect textRect = new Rect();
        paint.getTextBounds(tipText, 0, tipText.length(), textRect);

        tipTop = offsetY + textRect.height() / 2;
//        torchTop = offsetY - torchIcoSize / 2;

        final int totalWidth = TIP_ICON_SPACE + textRect.width();
        tipLeft = (drawW - totalWidth) / 2 + TIP_ICON_SPACE;
//        torchLeft = (drawW - totalWidth) / 2;

        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationResume(Animator animation) {
                drawMe();
            }
        });
    }

    @Override
    protected void draw(Canvas canvas) {
        if (!drawing && animPos <= 0.01) {
            animator.pause();
            return;
        }

//        torchIco.setAlpha((int) (255 * animPos));
        paint.setAlpha((int) (255 * animPos));
        paint.setShadowLayer(11, 0, 0,
                ColorUtils.setAlphaComponent(Color.BLACK, paint.getAlpha() / 2)
        );

//        torchIco.setBounds(
//                torchLeft,
//                torchTop,
//                torchLeft + torchIcoSize,
//                torchTop + torchIcoSize
//        );
//        torchIco.draw(canvas);

        canvas.drawText(tipText, tipLeft, tipTop, paint);
    }
}
