package indi.ssuf1998.vqrscan.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import indi.ssuf1998.vqrscan.databinding.SettingsSimpleItemLayoutBinding;

public abstract class SettingsBaseItem<T> {
    protected String itemText;
    protected String itemSubText;
    protected int itemIcon;
    protected ItemClickListener mItemClickListener;

    protected T value;

    public SettingsBaseItem(String itemText, String itemSubText, int itemIcon,
                            ItemClickListener listener, T value) {
        this.itemText = itemText;
        this.itemSubText = itemSubText;
        this.itemIcon = itemIcon;
        this.mItemClickListener = listener;
        this.value = value;
    }

    public SettingsBaseItem(String itemText, T value) {
        this(itemText, "", 0, null, value);
    }

    public SettingsBaseItem(String itemText, ItemClickListener listener, T value) {
        this(itemText, "", 0, listener, value);
    }

    public String getItemText() {
        return itemText;
    }

    public SettingsBaseItem<T> setItemText(String itemText) {
        this.itemText = itemText;
        return this;
    }

    public String getItemSubText() {
        return itemSubText;
    }

    public SettingsBaseItem<T> setItemSubText(String itemSubText) {
        this.itemSubText = itemSubText;
        return this;
    }

    public int getItemIcon() {
        return itemIcon;
    }

    public SettingsBaseItem<T> setItemIcon(int itemIcon) {
        this.itemIcon = itemIcon;
        return this;
    }

    public ItemClickListener getOnItemClickListener() {
        return mItemClickListener;
    }

    public SettingsBaseItem<T> setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
        return this;
    }

    public T getValue() {
        return value;
    }

    public SettingsBaseItem<T> setValue(T value) {
        this.value = value;
        return this;
    }

    protected abstract SettingsBaseItemVH<?> createViewHolder(@NonNull ViewGroup parent);

    protected abstract void bindViewHolder(@NonNull SettingsBaseItemVH<?> holder);

    public interface ItemClickListener {
        void click(View view);
    }

    public static class SettingsSimpleItem extends SettingsBaseItem<Void> {
        public SettingsSimpleItem(String itemText, String itemSubText, int itemIcon,
                                  ItemClickListener listener, Void value) {
            super(itemText, itemSubText, itemIcon, listener, value);
        }

        public SettingsSimpleItem(String itemText, Void value) {
            this(itemText, "", 0, null, value);
        }

        public SettingsSimpleItem(String itemText, ItemClickListener listener, Void value) {
            this(itemText, "", 0, listener, value);
        }

        @Override
        protected SettingsBaseItemVH<?> createViewHolder(@NonNull ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            SettingsSimpleItemLayoutBinding binding = SettingsSimpleItemLayoutBinding.inflate(
                    inflater, parent, false
            );
            return new SettingsBaseItemVH<>(binding);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void bindViewHolder(@NonNull SettingsBaseItemVH<?> holder) {
            final SettingsBaseItemVH<SettingsSimpleItemLayoutBinding> mHolder =
                    (SettingsBaseItemVH<SettingsSimpleItemLayoutBinding>) holder;

            if (getItemIcon() != 0) {
                mHolder.getBinding().itemIcon.setImageResource(getItemIcon());
            }

            mHolder.getBinding().itemText.setText(getItemText());
            if (getItemSubText().isEmpty()) {
                mHolder.getBinding().itemSubText.setVisibility(View.GONE);
            } else {
                mHolder.getBinding().itemSubText.setText(getItemSubText());
            }

            holder.itemView.setOnClickListener(v -> {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().click(v);
                }
            });
        }
    }

}
