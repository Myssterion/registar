package com.registar.ui.employee;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.registar.R;
import com.registar.adapter.EmployeeAdapter;
import com.registar.data.model.Employee;

import static com.registar.util.Constants.*;

public class EmployeeFragment extends Fragment implements EmployeeAdapter.OnEmployeeItemClick{

    private EmployeeViewModel employeeViewModel;
    private RecyclerView recyclerView;
    private EmployeeAdapter employeeAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout filterLayout;

    private EditText filterByName;
    private EditText filterBySurname;
    private FloatingActionButton addEmployee;
    public static EmployeeFragment newInstance() {
        return new EmployeeFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        employeeViewModel =
                new ViewModelProvider(this).get(EmployeeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_employee, container, false);


        recyclerView = root.findViewById(R.id.recycler_employee);
        filterLayout = root.findViewById(R.id.layout_filter_employee);
        filterByName = root.findViewById(R.id.text_filter_employee_name);
        filterBySurname = root.findViewById(R.id.text_filter_employee_surname);
        addEmployee = root.findViewById(R.id.button_employee_add);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        employeeAdapter = new EmployeeAdapter(getContext(), this);
        recyclerView.setAdapter(employeeAdapter);



        addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               navigate(null, true);
            }
        });
        // Attach touch listener to RecyclerView
        /*root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;  // Allow RecyclerView to handle the touch event as well
            }
        });*/

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //stavi da scroll down sakrije filtere
                }
        });


        employeeViewModel.getAllEmployees().observe(getViewLifecycleOwner(), items -> {
            // Update adapter with new data
            employeeAdapter.submitList(items);
        });

        filterByName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(filterByName, filterBySurname);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        filterBySurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(filterByName, filterBySurname);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return root;
    }

    public void onEmployeeClick(final int pos) {
        Employee employee = employeeAdapter.getItemAt(pos);
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select))
                .setItems(new String[]{ getString(R.string.details), getString(R.string.update), getString(R.string.delete) }, (dialogInterface, i) -> {
                    switch (i) {
                        case OPTION_DETAILS:
                            navigate(employee, false);
                            //dialogInterface.dismiss();
                            //employeeViewModel.getItemDetails(employee);
                            break;
                        case OPTION_UPDATE:
                            navigate(employee, true);
                            //dialogInterface.dismiss();
                            //employeeViewModel.editItem(employee);
                            break;
                        case OPTION_DELETE:
                            employeeViewModel.deleteItem(employee);
                            break;
                    }
                }).show();
    }

    private void filter(EditText filterByName, EditText filterBySurname) {
        String nameQuery = filterByName.getText().toString();
        String surnameQuery = filterBySurname.getText().toString();

        employeeViewModel.filter(nameQuery, surnameQuery);
    }

    private void navigate(Employee employee, boolean isUpdate) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isUpdateMode", isUpdate);
        bundle.putSerializable("employee", employee);
        navController.navigate(R.id.action_nav_employees_to_employeeDetailFragment, bundle);
    }
}