package com.registar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.registar.R;
import com.registar.data.model.AssetRecordList;

import java.util.ArrayList;
import java.util.List;

public class AssetRecordListAdapter extends RecyclerView.Adapter<AssetRecordListAdapter.ViewHolder> {

    private List<AssetRecordList> assetRecordLists;
    private OnAssetRecordListItemClick onAssetRecordListItemClick;

    public AssetRecordListAdapter(OnAssetRecordListItemClick onAssetRecordListItemClick) {
        this.assetRecordLists = new ArrayList<>();
        this.onAssetRecordListItemClick = onAssetRecordListItemClick;
    }

    @NonNull
    @Override
    public AssetRecordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.recycler_item, parent, false);
        AssetRecordListAdapter.ViewHolder vh = new AssetRecordListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AssetRecordListAdapter.ViewHolder holder, final int position) {
        final AssetRecordList assetRecordList = assetRecordLists.get(position);
        holder.tvName.setText(assetRecordList.getListName());
        holder.itemView.setOnClickListener( v -> {
            if(onAssetRecordListItemClick != null)
                onAssetRecordListItemClick.onAssetRecordListClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return assetRecordLists.size();
    }

    public void submitList(List<AssetRecordList> newAssetRecordListList) {
        this.assetRecordLists = newAssetRecordListList;
        notifyDataSetChanged();
    }

    public AssetRecordList getItemAt(int pos) {
        return assetRecordLists.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvName;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_title);
        }

        @Override
        public void onClick(View view) {
            onAssetRecordListItemClick.onAssetRecordListClick(getAdapterPosition());
        }
    }

    public interface OnAssetRecordListItemClick {
        void onAssetRecordListClick(int pos);
    }
}
