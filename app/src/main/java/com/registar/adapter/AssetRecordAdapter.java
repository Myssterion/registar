package com.registar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.registar.R;
import com.registar.data.model.AssetRecordWithDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetRecordAdapter extends RecyclerView.Adapter<AssetRecordAdapter.ViewHolder>{

    private List<AssetRecordWithDetails> assetRecords;
    private Map<Long, String> assetNames;
    private OnAssetRecordWithDetailsItemClick onAssetRecordWithDetailsItemClick;

    public AssetRecordAdapter(OnAssetRecordWithDetailsItemClick onAssetRecordWithDetailsItemClick) {
        this.assetRecords = new ArrayList<>();
        this.assetNames = new HashMap<>();
        this.onAssetRecordWithDetailsItemClick = onAssetRecordWithDetailsItemClick;
    }

    @NonNull
    @Override
    public AssetRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.recycler_item, parent, false);
        AssetRecordAdapter.ViewHolder vh = new AssetRecordAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AssetRecordAdapter.ViewHolder holder, final int position) {
        final AssetRecordWithDetails assetRecord = assetRecords.get(position);
        holder.tvName.setText(assetRecord.getAsset().getAssetName());
        holder.itemView.setOnClickListener( v -> {
            if(onAssetRecordWithDetailsItemClick != null)
                onAssetRecordWithDetailsItemClick.onAssetRecordWithDetailsClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return assetRecords.size();
    }

    public void submitList(List<AssetRecordWithDetails> newAssetRecordWithDetailsList) {
        this.assetRecords = newAssetRecordWithDetailsList;
        notifyDataSetChanged();
    }

    public void submitNamesMap(Map<Long, String> newAssetNames) {
        this.assetNames = newAssetNames;
    }

    public AssetRecordWithDetails getItemAt(int pos) {
        return assetRecords.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvName;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_title);
        }

        @Override
        public void onClick(View view) {
            onAssetRecordWithDetailsItemClick.onAssetRecordWithDetailsClick(getAdapterPosition());
        }
    }

    public interface OnAssetRecordWithDetailsItemClick {
        void onAssetRecordWithDetailsClick(int pos);
    }
}
