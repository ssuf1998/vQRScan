package indi.ssuf1998.vqrscan.cameraeffectview;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.LinearInterpolator;

import indi.ssuf1998.vqrscan.Utils;

public class ScanBarEffectElement extends EffectElement {
    private final Bitmap scanBarBmp;
    private final int scanBarColor;
    private final int scanBarWidth;
    private final int scanBarAnimHeight;

    private final int scanBarLeft;
    private final int startTop;

    public ScanBarEffectElement(View view, int drawW, int drawH,
                                Bitmap scanBarBmp, int scanBarColor,
                                float scanBarWidthRatio, float scanBarAnimHeightRatio,
                                float startTopRatio) {
        super(view, drawW, drawH);
        this.scanBarColor = scanBarColor;
        this.scanBarWidth = (int) (drawW * scanBarWidthRatio);
        this.scanBarBmp = colorizeScanBar(scanBarBmp);
        this.scanBarAnimHeight = (int) (drawH * scanBarAnimHeightRatio);

        this.scanBarLeft = (int) ((drawW - this.scanBarWidth) / 2f);
        this.startTop = (int) (drawH * startTopRatio);

        init();
    }

    @Override
    protected void init() {
        super.init();
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
    }

    @Override
    protected void draw(Canvas canvas) {
        if (animPos <= 0.15) {
            paint.setAlpha((int) (255 * animPos / 0.15));
        } else if (animPos >= 0.85) {
            paint.setAlpha((int) (255 * (1 - animPos) / 0.15));
        } else {
            paint.setAlpha(255);
        }

        canvas.drawBitmap(
                scanBarBmp,
                scanBarLeft,
                startTop + animPos * scanBarAnimHeight,
                paint);
    }

    private Bitmap colorizeScanBar(Bitmap src) {
        final Bitmap colored = Utils.applyColorMaskBmp(src, scanBarColor);

        final int scaledH = (int) (scanBarWidth *
                ((float) colored.getHeight() / colored.getWidth()));

        return Bitmap.createScaledBitmap(
                colored,
                scanBarWidth, scaledH,
                true);
    }
}
