package com.registar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.registar.R;
import com.registar.data.model.FixedAsset;

import java.util.ArrayList;
import java.util.List;

public class FixedAssetAdapter extends RecyclerView.Adapter<FixedAssetAdapter.ViewHolder>{

    private List<FixedAsset> fixedAssets;
    private OnFixedAssetItemClick onFixedAssetItemClick;

    public FixedAssetAdapter(OnFixedAssetItemClick onFixedAssetItemClick) {
        this.fixedAssets = new ArrayList<>();
        this.onFixedAssetItemClick = onFixedAssetItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.recycler_item, parent, false);
        FixedAssetAdapter.ViewHolder vh = new FixedAssetAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        final FixedAsset fixedAsset = fixedAssets.get(position);
        holder.tvName.setText(fixedAsset.getAssetName());
        holder.itemView.setOnClickListener( v -> {
            if(onFixedAssetItemClick != null)
                onFixedAssetItemClick.onFixedAssetClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return fixedAssets.size();
    }

    public void submitList(List<FixedAsset> newFixedAssetList) {
        this.fixedAssets = newFixedAssetList;
        notifyDataSetChanged();
    }

    public FixedAsset getItemAt(int pos) {
        return fixedAssets.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvName;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_title);
        }

        @Override
        public void onClick(View view) {
            onFixedAssetItemClick.onFixedAssetClick(getAdapterPosition());
        }
    }

    public interface OnFixedAssetItemClick {
        void onFixedAssetClick(int pos);
    }
}
