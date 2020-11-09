package indi.ssuf1998.vqrscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import indi.ssuf1998.vqrscan.databinding.SettingsItemLayoutBinding;


public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.VH> {
    private final Context context;
    private final List<SettingsSimpleItem<?>> items;

    public SettingsAdapter(List<SettingsSimpleItem<?>> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SettingsItemLayoutBinding binding = SettingsItemLayoutBinding.inflate(
                inflater, parent, false
        );

        return new VH(binding);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final SettingsSimpleItem<?> item = items.get(position);

        if (item.getItemIcon() != 0) {
            holder.binding.itemIcon.setImageDrawable(
                    ContextCompat.getDrawable(context, item.getItemIcon())
            );
        }

        holder.binding.itemText.setText(item.getItemText());
        if (item.getItemSubText().isEmpty()) {
            holder.binding.itemSubText.setVisibility(View.GONE);
        } else {
            holder.binding.itemSubText.setText(item.getItemSubText());
        }

        holder.itemView.setOnClickListener(v -> {
            if (item.getOnItemClickListener() != null) {
                item.getOnItemClickListener().click(v);
            }
        });

        if (item instanceof SettingsSwitchItem) {
            final SettingsSwitchItem switchItem = (SettingsSwitchItem) item;

            holder.binding.itemSwitch.setVisibility(View.VISIBLE);
            holder.binding.itemSwitch.setChecked(switchItem.getValue());

            holder.binding.itemSwitch.setOnClickListener(
                    v -> switchItem.setValue(holder.binding.itemSwitch.isChecked()));

            final ConstraintLayout.LayoutParams textLP =
                    (ConstraintLayout.LayoutParams) holder.binding.itemText.getLayoutParams();
            final ConstraintLayout.LayoutParams subTextLP =
                    (ConstraintLayout.LayoutParams) holder.binding.itemSubText.getLayoutParams();

            textLP.rightMargin = textLP.leftMargin;
            subTextLP.rightMargin = subTextLP.leftMargin;

            holder.binding.itemText.setLayoutParams(textLP);
            holder.binding.itemSubText.setLayoutParams(subTextLP);

            holder.itemView.setOnClickListener(v -> {
                if (item.getOnItemClickListener() != null) {
                    item.getOnItemClickListener().click(v);
                }
                if (switchItem.getInnerListener() != null) {
                    switchItem.getInnerListener().click(v);
                }
            });
        }
    }

    protected static class VH extends RecyclerView.ViewHolder {
        public final SettingsItemLayoutBinding binding;

        public VH(@NonNull SettingsItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
