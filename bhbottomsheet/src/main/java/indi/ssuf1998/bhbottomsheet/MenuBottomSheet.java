package indi.ssuf1998.bhbottomsheet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import indi.ssuf1998.bhbottomsheet.databinding.MenuLayoutBinding;

public class MenuBottomSheet extends BaseBottomSheet {
    private MenuLayoutBinding binding;

    private final List<MenuBottomSheetItem> items;
    private ItemClickListener mItemClickListener;
    private RecyclerView.ItemDecoration decoration;

    public MenuBottomSheet(String titleText, String subTitleText,
                           List<MenuBottomSheetItem> items) {
        super(titleText, subTitleText);
        this.items = items;
    }

    public MenuBottomSheet(String titleText, List<MenuBottomSheetItem> items) {
        this(titleText, "", items);
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
        bindListeners();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View fatherView = super.onCreateView(inflater, container, savedInstanceState);
        assert fatherView != null;
        final LinearLayout slotView = (LinearLayout) fatherView.findViewById(R.id.slotView);
        binding = MenuLayoutBinding.inflate(inflater, slotView, true);

        return fatherView;
    }

    @Override
    protected void initUI() {
        super.initUI();

        final MenuBottomSheetAdapter adapter = new MenuBottomSheetAdapter(items, getContext());
        final LinearLayoutManager layoutMgr = new LinearLayoutManager(getContext());
        if (decoration != null) {
            binding.getRoot().addItemDecoration(decoration);
        }
        binding.getRoot().setLayoutManager(layoutMgr);
        binding.getRoot().setAdapter(adapter);

        if (mItemClickListener != null) {
            adapter.setOnItemClickListener(view -> {
                final int idx = binding.getRoot().getChildAdapterPosition(view);
                mItemClickListener.onItemClick(idx);
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindListeners() {
        super.bindListeners();

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            binding.getRoot().requestDisallowInterceptTouchEvent(
                    binding.getRoot().canScrollVertically(-1));
            return false;
        });
    }

    public List<MenuBottomSheetItem> getItems() {
        return items;
    }

    public RecyclerView.ItemDecoration getDecoration() {
        return decoration;
    }

    public void setDecoration(RecyclerView.ItemDecoration decoration) {
        this.decoration = decoration;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(int idx);
    }
}
