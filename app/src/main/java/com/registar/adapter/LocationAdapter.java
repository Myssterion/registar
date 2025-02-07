package com.registar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.registar.R;
import com.registar.data.model.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Location> locations;
    private OnLocationItemClick onLocationItemClick;
    public LocationAdapter(OnLocationItemClick onLocationItemClick) {
        locations = new ArrayList<>();
        this.onLocationItemClick = onLocationItemClick;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.recycler_item, parent, false);
        ViewHolder vh = new LocationAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Location location = locations.get(position);
        holder.tvName.setText(location.getLocationName());
        holder.itemView.setOnClickListener(v -> {
            if(onLocationItemClick != null)
                onLocationItemClick.onLocationClick(position);
        });
    }


    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void submitList(List<Location> newLocationList) {
        this.locations = newLocationList;
        notifyDataSetChanged();
    }

    public Location getItemAt(int pos) {
        return locations.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_title);
        }


        @Override
        public void onClick(View v) {
            onLocationItemClick.onLocationClick(getAdapterPosition());
        }
    }

    public interface OnLocationItemClick {
        void onLocationClick(int pos);
    }
}
