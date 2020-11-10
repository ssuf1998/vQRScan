package indi.ssuf1998.vqrscan.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import indi.ssuf1998.vqrscan.R;
import indi.ssuf1998.vqrscan.databinding.SettingsSwitchItemLayoutBinding;

public class SettingsSwitchItem extends SettingsBaseItem<Boolean> {
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

    @Override
    protected SettingsBaseItemVH<?> createViewHolder(@NonNull ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SettingsSwitchItemLayoutBinding binding = SettingsSwitchItemLayoutBinding.inflate(
                inflater, parent, false
        );
        return new SettingsBaseItemVH<>(binding);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void bindViewHolder(@NonNull SettingsBaseItemVH<?> holder) {
        final SettingsBaseItemVH<SettingsSwitchItemLayoutBinding> mHolder =
                (SettingsBaseItemVH<SettingsSwitchItemLayoutBinding>) holder;

        if (getItemIcon() != 0) {
            mHolder.getBinding().itemIcon.setImageResource(getItemIcon());
        }

        mHolder.getBinding().itemText.setText(getItemText());
        if (getItemSubText().isEmpty()) {
            mHolder.getBinding().itemSubText.setVisibility(View.GONE);
        } else {
            mHolder.getBinding().itemSubText.setText(getItemSubText());
        }

        mHolder.getBinding().itemSwitch.setVisibility(View.VISIBLE);
        mHolder.getBinding().itemSwitch.setChecked(getValue());

        mHolder.getBinding().itemSwitch.setOnClickListener(
                v -> setValue(mHolder.getBinding().itemSwitch.isChecked()));

        holder.itemView.setOnClickListener(v -> {
            if (getOnItemClickListener() != null) {
                getOnItemClickListener().click(v);
            }
            if (getInnerListener() != null) {
                getInnerListener().click(v);
            }
        });

    }
}
