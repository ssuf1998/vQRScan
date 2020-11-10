package indi.ssuf1998.vqrscan.settings;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class SettingsBaseItemVH<T extends ViewBinding> extends RecyclerView.ViewHolder {
    private final T binding;

    public SettingsBaseItemVH(@NonNull T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public T getBinding() {
        return binding;
    }
}

