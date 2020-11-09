package indi.ssuf1998.vqrscan;

import android.view.View;

public class SettingsSimpleItem<T> {
    protected String itemText;
    protected String itemSubText;
    protected int itemIcon;
    protected ItemClickListener mItemClickListener;

    protected T value;

    public SettingsSimpleItem(String itemText, String itemSubText, int itemIcon,
                              ItemClickListener listener, T value) {
        this.itemText = itemText;
        this.itemSubText = itemSubText;
        this.itemIcon = itemIcon;
        this.mItemClickListener = listener;
        this.value = value;
    }

    public SettingsSimpleItem(String itemText, T value) {
        this(itemText, "", 0, null, value);
    }

    public SettingsSimpleItem(String itemText, ItemClickListener listener, T value) {
        this(itemText, "", 0, listener, value);
    }

    public String getItemText() {
        return itemText;
    }

    public SettingsSimpleItem setItemText(String itemText) {
        this.itemText = itemText;
        return this;
    }

    public String getItemSubText() {
        return itemSubText;
    }

    public SettingsSimpleItem setItemSubText(String itemSubText) {
        this.itemSubText = itemSubText;
        return this;
    }

    public int getItemIcon() {
        return itemIcon;
    }

    public SettingsSimpleItem setItemIcon(int itemIcon) {
        this.itemIcon = itemIcon;
        return this;
    }

    public ItemClickListener getOnItemClickListener() {
        return mItemClickListener;
    }

    public SettingsSimpleItem setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
        return this;
    }

    public T getValue() {
        return value;
    }

    public SettingsSimpleItem setValue(T value) {
        this.value = value;
        return this;
    }

    public interface ItemClickListener {
        void click(View view);
    }
}
