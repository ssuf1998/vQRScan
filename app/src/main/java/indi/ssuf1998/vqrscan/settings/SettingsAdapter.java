package indi.ssuf1998.vqrscan.settings;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsBaseItemVH<?>> {

    private final List<SettingsBaseItem<?>> items;

    public SettingsAdapter(List<SettingsBaseItem<?>> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SettingsBaseItemVH<?> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SettingsBaseItemVH<?> vh = null;
        for (SettingsBaseItem<?> item : items) {
            if (item.hashCode() == viewType) {
                vh = item.createViewHolder(parent);
            }
        }
        return Objects.requireNonNull(vh);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsBaseItemVH<?> holder, int position) {
        items.get(position).bindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).hashCode();
    }
}
