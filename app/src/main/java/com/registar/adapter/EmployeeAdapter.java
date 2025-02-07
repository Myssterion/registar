package com.registar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.registar.R;
import com.registar.data.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder>{

    private List<Employee> employees;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnEmployeeItemClick onEmployeeItemClick;

    public EmployeeAdapter(Context context, OnEmployeeItemClick onEmployeeItemClick) {
        this.context = context;
        this.employees = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
        this.onEmployeeItemClick = onEmployeeItemClick;
    }

    @NonNull
    @Override
    public EmployeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.recycler_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.ViewHolder holder,@SuppressLint("RecyclerView")  final int position) {
        final Employee employee = employees.get(position);
        holder.tvName.setText(employee.getName() + " " + employee.getSurname());

        holder.itemView.setOnClickListener(v -> {
           /* @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Kliknuli ste na: " + employees.get(position).getName(), Toast.LENGTH_SHORT).show();
            }*/
             if (onEmployeeItemClick != null)
                onEmployeeItemClick.onEmployeeClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public void submitList(List<Employee> newEmployeeList) {
        this.employees = newEmployeeList;
        notifyDataSetChanged(); // Notify RecyclerView to refresh
    }

    public Employee getItemAt(int pos) {
        return employees.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            tvName = v.findViewById(R.id.tv_title);
            layout.setOnClickListener(view -> {
                if (onEmployeeItemClick != null) {
                    onEmployeeItemClick.onEmployeeClick(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            onEmployeeItemClick.onEmployeeClick(getAdapterPosition());
        }
    }

    public interface OnEmployeeItemClick {
        void onEmployeeClick(int pos);
    }
}
