package com.registar.ui.employee;

import android.app.Application;
import android.widget.WrapperListAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.registar.data.RegistarDatabase;
import com.registar.data.dao.EmployeeDao;
import com.registar.data.model.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EmployeeViewModel extends AndroidViewModel {
    private MutableLiveData<List<Employee>> employees = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Employee> employee = new MutableLiveData<>();
    private List<Employee> originalList  = new ArrayList<>();
    private EmployeeDao employeeDao;
    private final MutableLiveData<Boolean> isUpdateMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> title = new MutableLiveData<>();

    public EmployeeViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        this.employeeDao = db.getEmployeeDao();

        employeeDao.getEmployees().observeForever(new Observer<List<Employee>>() {
            @Override
            public void onChanged(List<Employee> employeeList) {
                // Update the MutableLiveData with the new data


                    originalList = new ArrayList<>();
                    originalList.addAll(employeeList);
                    employees.setValue(employeeList);
                    employeeDao.getEmployees().removeObserver(this);

            }
        });
    }

    public LiveData<List<Employee>> getAllEmployees() {
        return this.employees;
    }

    public void setUpdateMode(boolean value) {
        isUpdateMode.setValue(value);
    }

    public LiveData<Boolean> getUpdateMode() {
        return isUpdateMode;
    }

    public void setTitle(String value) {
        title.setValue(value);
    }

    public LiveData<String> getTitle() {
        return title;
    }

    public void deleteItem(Employee employee) {
        // Perform delete in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            employeeDao.deleteEmployee(employee);
            List<Employee> currentList = employees.getValue();
            if (currentList != null) {
                originalList.remove(employee);
                currentList.remove(employee); // Remove from the current list
                employees.postValue(new ArrayList<>(currentList)); // Post the updated list
            }
        });
    }

    public void filter(String nameQuery, String surnameQuery) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Employee> filteredList = originalList.stream()
                .filter(employee ->
                employee.getName().toLowerCase().startsWith(nameQuery.toLowerCase())
                        && employee.getSurname().toLowerCase().startsWith(surnameQuery.toLowerCase()))
                .collect(Collectors.toList());
            employees.postValue(filteredList);
        });
    }

    public LiveData<Employee> getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee.setValue(employee);
    }

    public void updateEmployee(Employee employee) {
        Executors.newSingleThreadExecutor().execute(() -> {
                employeeDao.updateEmployee(employee);
                /*Optional<Employee> employeeToUpdate = originalList.stream()
                        .filter(emp -> emp.getEmployeeId() == employee.getEmployeeId()).findFirst();

                employeeToUpdate.ifPresent(emp -> {
                    int index = originalList.indexOf(emp);
                    originalList.set(index, emp);
                    List<Employee> currentList = new ArrayList<>();
                    employees.postValue(currentList);
                });*/
            this.employee.postValue(employee);
        });
    }

    public void addEmployee(Employee newEmployee) {
        Executors.newSingleThreadExecutor().execute(() -> {
            employeeDao.insertEmployee(newEmployee);
            //setEmployee(newEmployee);
            this.employee.postValue(newEmployee);
        });
    }
}