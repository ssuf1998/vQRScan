package indi.ssuf1998.bhbottomsheet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import indi.ssuf1998.bhbottomsheet.databinding.MenuItemLayoutBinding;

public class MenuBottomSheetAdapter extends RecyclerView.Adapter<MenuBottomSheetAdapter.VH> {
    private final Context context;
    private final List<MenuBottomSheetItem> items;
    private ItemClickListener mItemClickListener;

    public MenuBottomSheetAdapter(List<MenuBottomSheetItem> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final MenuItemLayoutBinding binding = MenuItemLayoutBinding.inflate(
                inflater, parent, false);

        final VH vh = new VH(binding);

        if (mItemClickListener != null) {
            vh.itemView.setOnClickListener(view -> mItemClickListener.click(view));
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final MenuBottomSheetItem item = items.get(position);
        if (item.getItemIcon() != 0) {
            holder.binding.itemIcon.setImageDrawable(
                    ContextCompat.getDrawable(context, item.getItemIcon())
            );
        } else {
            holder.binding.itemIcon.setVisibility(View.GONE);
        }
        holder.binding.itemText.setText(item.getItemText());
        if (item.getItemTextColor() != 0) {
            holder.binding.itemText.setTextColor(item.getItemTextColor());
            holder.binding.itemIcon.setImageTintList(
                    ColorStateList.valueOf(item.getItemTextColor()));
        }
        holder.binding.itemText.setTypeface(
                holder.binding.itemText.getTypeface(),
                item.getTypefaceStyle()
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    // VH
    protected static class VH extends RecyclerView.ViewHolder {
        public final MenuItemLayoutBinding binding;

        public VH(@NonNull MenuItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ItemClickListener {
        void click(View view);
    }
}
