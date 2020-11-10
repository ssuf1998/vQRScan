package indi.ssuf1998.vqrscan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.SparseIntArray;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.regex.Pattern;

public class Utils {
    public static void applyShadow(ImageView v,
                                   int offsetX, int offsetY, int r,
                                   int shadowColor, int alpha
    ) {
        final Drawable drawable = v.getDrawable();
        Bitmap bmp = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
        );
        Bitmap shadow = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
        );

        final Canvas cvs = new Canvas(bmp);
        drawable.setBounds(0, 0,
                cvs.getWidth(), cvs.getHeight());
        drawable.draw(cvs);
        bmp = Objects.requireNonNull(
                applyColorMaskBmp(bmp, v.getImageTintList().getDefaultColor())
        );

        cvs.setBitmap(shadow);
        drawable.setBounds(offsetX, offsetY,
                cvs.getWidth() + offsetX, cvs.getHeight() + offsetY);
        drawable.draw(cvs);
        shadow = Objects.requireNonNull(
                applyColorMaskBmp(shadow, shadowColor)
        );
        bmpBlur(shadow, r);

        final Bitmap result = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
        );
        cvs.setBitmap(result);

        final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setAlpha(alpha);
        cvs.drawBitmap(
                shadow, offsetX, offsetY, shadowPaint
        );

        cvs.drawBitmap(
                bmp, 0, 0, null
        );

        v.setImageBitmap(result);
        v.setImageTintList(null);

        bmp.recycle();
        shadow.recycle();
    }

    public static Bitmap applyColorMaskBmp(@NonNull Bitmap src, int color) {
        if (src.isRecycled())
            return null;

        final Bitmap maskBmp = Bitmap.createBitmap(
                src.getWidth(),
                src.getHeight(),
                Bitmap.Config.RGB_565);
        final Bitmap coloredBmp = Bitmap.createBitmap(
                src.getWidth(),
                src.getHeight(),
                Bitmap.Config.ARGB_8888);

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Canvas cvs = new Canvas(maskBmp);
        cvs.drawColor(color);

        cvs.setBitmap(coloredBmp);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cvs.drawBitmap(maskBmp, 0, 0, null);
        cvs.drawBitmap(src, 0, 0, paint);

        paint.setXfermode(null);
        maskBmp.recycle();

        return coloredBmp;
    }

    public static void bmpBlur(@NonNull Bitmap bmp, int r) {
        final Context c = MainActivity.getWeakRef().get();
        if (c == null || bmp.isRecycled())
            return;

        final RenderScript rs = RenderScript.create(c);
        final Bitmap inBmp = bmp.getConfig() == Bitmap.Config.ARGB_8888 ? bmp :
                bmp.copy(Bitmap.Config.ARGB_8888, true);

        final Allocation input = Allocation.createFromBitmap(rs, inBmp);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        script.setRadius(r);
        script.setInput(input);
        script.forEach(output);

        output.copyTo(bmp);
    }

    public static class ResultType {
        public static int URL = 1;
        public static int PLAIN_TEXT = 99;
    }

    public static SparseIntArray ResultTypeIcon = new SparseIntArray();

    static {
        ResultTypeIcon.put(ResultType.URL,
                R.drawable.ic_baseline_link_24);
        ResultTypeIcon.put(ResultType.PLAIN_TEXT,
                R.drawable.ic_baseline_text_fields_24);
    }

    public static int getResultType(String str) {
        final Pattern urlP = Pattern.compile(Const.URL_DETECT_REGEX);
        if (urlP.matcher(str).find()) {
            return ResultType.URL;
        }

        return ResultType.PLAIN_TEXT;
    }
}
