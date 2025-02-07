package com.registar.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;

public class AssetRecordWithDetails implements Serializable {
    @Embedded
    public AssetRecord assetRecord;

    @Relation(
            parentColumn = "asset_id",
            entityColumn = "asset_id"
    )
    public FixedAsset asset;

    @Relation(
            parentColumn = "asset_record_list_id",
            entityColumn = "list_id"
    )
    public AssetRecordList assetRecordList;

    @Relation(
            parentColumn = "prev_employee_in_charge_id",
            entityColumn = "employee_id"
    )
    public Employee prevEmployee;

    @Relation(
            parentColumn = "new_employee_in_charge_id",
            entityColumn = "employee_id"
    )
    public Employee newEmployee;

    @Relation(
            parentColumn = "prev_on_location_id",
            entityColumn = "location_id"
    )
    public Location prevLocation;

    @Relation(
            parentColumn = "new_on_location_id",
            entityColumn = "location_id"
    )
    public Location newLocation;

    public AssetRecord getAssetRecord() {
        return assetRecord;
    }

    public void setAssetRecord(AssetRecord assetRecord) {
        this.assetRecord = assetRecord;
    }

    public AssetRecordList getAssetRecordList() {
        return assetRecordList;
    }

    public void setAssetRecordList(AssetRecordList assetRecordList) {
        this.assetRecordList = assetRecordList;
    }

    public Employee getPrevEmployee() {
        return prevEmployee;
    }

    public void setPrevEmployee(Employee prevEmployee) {
        this.prevEmployee = prevEmployee;
    }

    public Employee getNewEmployee() {
        return newEmployee;
    }

    public void setNewEmployee(Employee newEmployee) {
        this.newEmployee = newEmployee;
    }

    public Location getPrevLocation() {
        return prevLocation;
    }

    public void setPrevLocation(Location prevLocation) {
        this.prevLocation = prevLocation;
    }

    public Location getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(Location newLocation) {
        this.newLocation = newLocation;
    }

    public FixedAsset getAsset() {
        return asset;
    }

    public void setAsset(FixedAsset asset) {
        this.asset = asset;
    }
}