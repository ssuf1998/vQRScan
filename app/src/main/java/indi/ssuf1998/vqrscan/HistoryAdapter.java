package indi.ssuf1998.vqrscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import indi.ssuf1998.vqrscan.databinding.HistoryItemLayoutBinding;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {
    private final Context context;
    private final List<HistoryItem> items;
    private ItemClickListener mItemClickListener;

    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public HistoryAdapter(List<HistoryItem> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        HistoryItemLayoutBinding binding = HistoryItemLayoutBinding.inflate(
                inflater, parent, false
        );

        final VH vh = new VH(binding);
        if (mItemClickListener != null) {
            vh.itemView.setOnClickListener(view -> mItemClickListener.click(view));
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final HistoryItem item = items.get(position);

        holder.binding.itemIcon.setImageDrawable(
                ContextCompat.getDrawable(context,
                        Utils.ResultTypeIcon.get(item.getResultType(),
                                R.drawable.ic_baseline_text_fields_24))
        );

        holder.binding.itemText.setText(item.getResultText());
        holder.binding.itemSubText.setText(dateFormat.format(new Date(item.getCreateTime())));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void click(View view);
    }

    protected static class VH extends RecyclerView.ViewHolder {
        public final HistoryItemLayoutBinding binding;

        public VH(@NonNull HistoryItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
