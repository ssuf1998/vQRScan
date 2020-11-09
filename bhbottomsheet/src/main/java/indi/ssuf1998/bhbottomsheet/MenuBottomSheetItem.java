package indi.ssuf1998.bhbottomsheet;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public class MenuBottomSheetItem {
    private String itemText;
    private int itemIcon;
    private int itemTextColor;
    private int typefaceStyle;

    public MenuBottomSheetItem(String itemText, int itemIcon, int itemTextColor, int typefaceStyle) {
        this.itemText = itemText;
        this.itemIcon = itemIcon;
        this.itemTextColor = itemTextColor;
        this.typefaceStyle = typefaceStyle;
    }

    public MenuBottomSheetItem() {
        this("", 0, 0, Typeface.NORMAL);
    }

    public String getItemText() {
        return itemText;
    }

    public MenuBottomSheetItem setItemText(String itemText) {
        this.itemText = itemText;
        return this;
    }

    public int getItemIcon() {
        return itemIcon;
    }

    public MenuBottomSheetItem setItemIcon(int itemIcon) {
        this.itemIcon = itemIcon;
        return this;
    }

    public int getItemTextColor() {
        return itemTextColor;
    }

    public MenuBottomSheetItem setItemTextColor(int itemTextColor) {
        this.itemTextColor = itemTextColor;
        return this;
    }

    public int getTypefaceStyle() {
        return typefaceStyle;
    }

    public MenuBottomSheetItem setTypefaceStyle(int typefaceStyle) {
        this.typefaceStyle = typefaceStyle;
        return this;
    }
}
