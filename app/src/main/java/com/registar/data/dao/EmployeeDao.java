package com.registar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.registar.data.model.Employee;
import com.registar.util.Constants;

import java.util.List;
@Dao
public interface EmployeeDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_EMPLOYEE)
    LiveData<List<Employee>> getEmployees();

    @Query("SELECT * FROM " + Constants.TABLE_NAME_EMPLOYEE + " WHERE employee_id = :id")
    Employee getEmployee(long id);

    /*
     * Insert the object in database
     * @param employee, object to be inserted
     */
    @Insert
    long insertEmployee(Employee employee);

    /*
     * update the object in database
     * @param employee, object to be updated
     */
    @Update
    void updateEmployee(Employee repos);

    /*
     * delete the object from database
     * @param employee, object to be deleted
     */
    @Delete
    void deleteEmployee(Employee employee);

    // Employee... is varargs, here employee is an array
    /*
     * delete list of objects from database
     * @param employee, array of oject to be deleted
     */
    @Delete
    void deleteEmployees(Employee... employee);
}
