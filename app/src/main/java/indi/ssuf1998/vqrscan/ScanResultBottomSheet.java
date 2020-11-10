package indi.ssuf1998.vqrscan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import indi.ssuf1998.bhbottomsheet.BaseBottomSheet;
import indi.ssuf1998.vqrscan.databinding.ScanResultLayoutBinding;

public class ScanResultBottomSheet extends BaseBottomSheet {
    public static class ButtonId {
        public static int COPY_BTN = 0;
        public static int OPEN_BTN = 1;
    }

    private ScanResultLayoutBinding binding;
    private String resultText;
    private int resultType;
    private BtnClickListener mBtnClickListener;

    public ScanResultBottomSheet(String titleText,
                                 String subTitleText) {
        super(titleText, subTitleText);
    }

    public ScanResultBottomSheet(String titleText) {
        this(titleText, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View fatherView = super.onCreateView(inflater, container, savedInstanceState);
        assert fatherView != null;
        final LinearLayout slotView = fatherView.findViewById(R.id.slotView);
        binding = ScanResultLayoutBinding.inflate(inflater, slotView, true);

        return fatherView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
        bindListeners();
    }

    @Override
    protected void initUI() {
        super.initUI();

        if (resultType == Utils.ResultType.URL) {
            binding.openResultBtn.setText(getString(R.string.open_url_btn));
        }

        binding.openResultBtn.setVisibility(
                resultType != Utils.ResultType.PLAIN_TEXT ?
                        View.VISIBLE : View.GONE);

        binding.resultText.setText(resultText);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindListeners() {
        super.bindListeners();

        binding.copyResultBtn.setOnClickListener(v -> {
            if (getContext() != null) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(resultText, resultText);
                clipboard.setPrimaryClip(clip);
                Toasty.info(getContext(), getString(R.string.copy_suc_msg), Toast.LENGTH_SHORT).show();
            }
            if (mBtnClickListener != null) {
                mBtnClickListener.onClick(v, ButtonId.COPY_BTN);
            }
        });
        binding.openResultBtn.setOnClickListener(v -> {
            innerOpenResult(getContext(), 0);
            if (mBtnClickListener != null) {
                mBtnClickListener.onClick(v, ButtonId.OPEN_BTN);
            }
        });

        binding.resultText.setMovementMethod(ScrollingMovementMethod.getInstance());
        binding.resultText.setOnTouchListener((v, event) -> {
            binding.resultText.getParent().requestDisallowInterceptTouchEvent(
                    binding.resultText.canScrollVertically(-1));
            return false;
        });
    }

    private void innerOpenResult(Context context, int code) {
        if (context == null)
            return;

        if (resultType == Utils.ResultType.URL) {
            final Uri uri = Uri.parse(resultText);
            final Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(uri);
            ((Activity) Objects.requireNonNull(context)).startActivityForResult(intent, code);
        }
    }

    public void openResult(Context context) {
        innerOpenResult(context, Const.DIRECTLY_OPENED);
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
        resultType = Utils.getResultType(resultText);
    }

    public int getResultType() {
        return resultType;
    }

    public boolean isResultOpenable() {
        return resultType != Utils.ResultType.PLAIN_TEXT;
    }

    public interface BtnClickListener {
        void onClick(View v, int btn);
    }

    public void setOnBtnClickListener(@NonNull BtnClickListener listener) {
        this.mBtnClickListener = listener;
    }

}
