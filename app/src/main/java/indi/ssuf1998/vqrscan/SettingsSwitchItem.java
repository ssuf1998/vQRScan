package indi.ssuf1998.vqrscan;

import android.view.View;

import androidx.appcompat.widget.SwitchCompat;

public class SettingsSwitchItem extends SettingsSimpleItem<Boolean> {
    private final ItemClickListener innerListener;

    public SettingsSwitchItem(String itemText, String itemSubText, int itemIcon,
                              ItemClickListener listener, Boolean value) {
        super(itemText, itemSubText, itemIcon, listener, value);

        innerListener = view -> {
            if (view != null && view.findViewById(R.id.itemSwitch) != null) {
                final SwitchCompat switcher = view.findViewById(R.id.itemSwitch);
                switcher.toggle();
                setValue(switcher.isChecked());
            }
        };
    }

    public SettingsSwitchItem(String itemText, Boolean value) {
        this(itemText, "", 0, null, value);
    }

    public SettingsSwitchItem(String itemText, ItemClickListener listener, Boolean value) {
        this(itemText, "", 0, listener, value);
    }

    protected ItemClickListener getInnerListener() {
        return innerListener;
    }
}
