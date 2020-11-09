package indi.ssuf1998.bhbottomsheet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Date;

import indi.ssuf1998.bhbottomsheet.databinding.BaseLayoutBinding;

public class BaseBottomSheet extends BottomSheetDialogFragment {
    private BaseLayoutBinding binding;
    private String titleText;
    private String subTitleText;
    private boolean immersiveModeXML;
    private boolean immersiveMode = true;
    private boolean showing = false;

    private DismissListener mDismissListener;
    private ShowListener mShowListener;

    public BaseBottomSheet(String titleText, String subTitleText) {
        this.titleText = titleText.toUpperCase();
        this.subTitleText = subTitleText;
    }

    public BaseBottomSheet(String titleText) {
        this(titleText, "");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        showing = true;
        if (mShowListener != null) {
            mShowListener.show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        showing = false;
        if (mDismissListener != null) {
            mDismissListener.dismiss();
        }
    }

    public void show(@NonNull FragmentManager manager) {
        // 以防短时间内多次show导致错误……
        // 面向接口编程就能够很“优雅”地解决这个问题了
        // 但是还要去整第三方包，不划算

        if (!showing) {
            this.show(manager, new Date().toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BaseLayoutBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
        solveImmersive();
    }

    private void solveImmersive() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            final Dialog dialog = getDialog();
            final Window win = dialog.getWindow();

            if (immersiveMode && immersiveModeXML) {
                if (binding.sheetView.getPaddingBottom() <= getNavBarHeightInPixel()) {
                    binding.sheetView.setPadding(
                            binding.sheetView.getPaddingLeft(),
                            binding.sheetView.getPaddingTop(),
                            binding.sheetView.getPaddingRight(),
                            binding.sheetView.getPaddingBottom() + getNavBarHeightInPixel());
                }
                win.findViewById(R.id.container).setFitsSystemWindows(false);
            } else {
                win.findViewById(R.id.container).setFitsSystemWindows(true);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyStyle();
    }

    private void applyStyle() {
        assert getContext() != null;

        final TypedArray attrArray = getContext().obtainStyledAttributes(
                null,
                R.styleable.BHBottomSheet);
        final int themeId = attrArray.getResourceId(R.styleable.BHBottomSheetStyle_BHBottomSheetTheme,
                R.style.BHBottomSheet_DefaultTheme);
        attrArray.recycle();

        final TypedArray styleArray =
                getContext().getTheme().obtainStyledAttributes(themeId, R.styleable.BHBottomSheet);
        immersiveModeXML = styleArray.getBoolean(R.styleable.BHBottomSheet_immersiveMode, true);
        styleArray.recycle();

        setStyle(STYLE_NORMAL, themeId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            FrameLayout designView = dialog.findViewById(R.id.design_bottom_sheet);
            designView.setBackgroundColor(Color.TRANSPARENT);

            final BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(designView);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        });
        return dialog;
    }

    protected void initUI() {
        binding.sheetTitle.setText(titleText);
        if (subTitleText.isEmpty()) {
            binding.sheetSubTitle.setVisibility(View.GONE);
        } else {
            binding.sheetSubTitle.setText(subTitleText);
        }
    }

    protected void bindListeners() {

    }

    private int getNavBarHeightInPixel() {
        if (getContext() != null) {
            final Resources res = getContext().getResources();
            final int resId = res.getIdentifier(
                    "navigation_bar_height",
                    "dimen",
                    "android");
            if (resId == 0) {
                return 0;
            }
            return res.getDimensionPixelSize(resId);
        } else {
            return 0;
        }
    }


    protected int getFullScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
        binding.sheetTitle.setText(titleText);
    }

    public String getSubTitleText() {
        return subTitleText;
    }

    public void setSubTitleText(String subTitleText) {
        this.subTitleText = subTitleText;
        binding.sheetSubTitle.setText(subTitleText);
    }

    public boolean isImmersiveMode() {
        return immersiveMode;
    }

    public void setImmersiveMode(boolean immersiveMode) {
        this.immersiveMode = immersiveMode;
    }

    public void setViewIntoSlot(View newView) {
        binding.slotView.addView(newView);
    }

    public View getSlotView() {
        return binding.slotView;
    }

    public boolean isShowing() {
        return showing;
    }

    public interface DismissListener {
        void dismiss();
    }

    public void setOnDismissListener(@NonNull DismissListener listener) {
        this.mDismissListener = listener;
    }

    public interface ShowListener {
        void show();
    }

    public void setOnShowListener(@NonNull ShowListener listener) {
        this.mShowListener = listener;
    }
}
