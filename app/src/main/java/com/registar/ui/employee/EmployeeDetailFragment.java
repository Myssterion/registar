package com.registar.ui.employee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.registar.R;
import com.registar.data.model.Employee;

public class EmployeeDetailFragment extends Fragment {

    private EmployeeViewModel employeeViewModel;
    private EditText name;
    private EditText surname;
    private EditText age;
    private Button saveButton;
    private TextWatcher textWatcher;
    public static EmployeeDetailFragment newInstance() {
        return new EmployeeDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        employeeViewModel = new ViewModelProvider(this).get(EmployeeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_employee_detail, container, false);
        name = root.findViewById(R.id.text_employee_name);
        surname = root.findViewById(R.id.text_employee_surname);
        age = root.findViewById(R.id.text_employee_age);
        saveButton = root.findViewById(R.id.button_employee_save);
        saveButton.setEnabled(false);
        createTextWatcher();
        setTextWatcherToFields();

        saveButton.setOnClickListener(listener -> {
            saveData();
        });

        return  root;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(employeeViewModel.getTitle().getValue());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        if (getArguments() != null && getArguments().containsKey("isUpdateMode") && getArguments().containsKey("employee")) {
            boolean isUpdateMode = getArguments().getBoolean("isUpdateMode");
            employeeViewModel.setUpdateMode(isUpdateMode);
            Employee employee = (Employee) getArguments().getSerializable("employee");
            employeeViewModel.setEmployee(employee);

            if (employee != null) {
                if (isUpdateMode)
                    employeeViewModel.setTitle(getString(R.string.employee_detail_update));
                else
                    employeeViewModel.setTitle(getString(R.string.employee_detail_info));
            } else {
                employeeViewModel.setTitle(getString(R.string.employee_detail_add));
            }
        }

        checkAndChangeIfViewMode(Boolean.TRUE.equals(employeeViewModel.getUpdateMode().getValue()));

        employeeViewModel.getEmployee().observe(getViewLifecycleOwner(), employee -> {
            if (employee != null) {
                name.setText(employee.getName());
                surname.setText(employee.getSurname());
                age.setText(String.valueOf(employee.getAge()));
            }
        });
    }

    public void checkAndChangeIfViewMode(boolean isUpdateMode) {
        if(!isUpdateMode)  {
            name.setEnabled(false);
            surname.setEnabled(false);
            age.setEnabled(false);
            saveButton.setVisibility(View.GONE);
        }
    }

    private void saveData() {
        String newName = name.getText().toString();
        String newSurname = surname.getText().toString();
        int newAge = age.getText().toString().isBlank() ? 0 : Integer.parseInt(age.getText().toString());
        if (employeeViewModel.getEmployee().getValue() == null) {
            // Add new employee logic
            Employee newEmployee = new Employee(newName, newSurname, newAge);
            employeeViewModel.addEmployee(newEmployee);
        } else {
            // Update employee data in the ViewModel
            long id = employeeViewModel.getEmployee().getValue().getEmployeeId();
            Employee employee = new Employee(id, newName, newSurname, newAge);
            employeeViewModel.updateEmployee(employee);

        }
    }

    private void createTextWatcher() {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if all fields are valid
                String nameValue = name.getText().toString().trim();
                String surnameValue = surname.getText().toString().trim();
                String ageValue = age.getText().toString().trim();

                boolean isNameValid = !nameValue.isEmpty(); // Add more checks if needed
                boolean isAgeValid = !ageValue.isEmpty() && ageValue.matches("\\d+"); // Check for a valid number
                boolean isSurnameValid = !surnameValue.isEmpty();

                // Enable or disable the button
                saveButton.setEnabled(isNameValid && isAgeValid && isSurnameValid);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void setTextWatcherToFields() {
        name.addTextChangedListener(textWatcher);
        surname.addTextChangedListener(textWatcher);
        age.addTextChangedListener(textWatcher);
    }
}