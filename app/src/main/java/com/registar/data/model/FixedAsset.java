package com.registar.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.registar.util.Constants;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_FIXED_ASSET,
        foreignKeys =  {
                @ForeignKey(entity = Employee.class,
                parentColumns = "employee_id",
                childColumns = "employee_in_charge_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Location.class,
                        parentColumns = "location_id",
                        childColumns = "on_location_id",
                        onDelete = ForeignKey.CASCADE)},
        indices = {
                @Index(value = "employee_in_charge_id"),
                @Index(value = "on_location_id"),
        }
)
public class FixedAsset implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="asset_id")
    private long assetId;
    @ColumnInfo(name="asset_name")
    private String assetName;
    private String description;
    private long barcode;
    private double price;

    @ColumnInfo(name="created_at")
    private LocalDate createdAt;

    @ColumnInfo(name="employee_in_charge_id")
    private long employeeInChargeId;
    @ColumnInfo(name="on_location_id")
    private long onLocationId;
    @ColumnInfo(name="image_path")
    private String imagePath;

    public long getAssetId() {
        return assetId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public long getEmployeeInChargeId() {
        return employeeInChargeId;
    }

    public void setEmployeeInChargeId(long employeeInChargeId) {
        this.employeeInChargeId = employeeInChargeId;
    }

    public long getOnLocationId() {
        return onLocationId;
    }

    public void setOnLocationId(long onLocationId) {
        this.onLocationId = onLocationId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public FixedAsset() {}

    @Ignore
    public FixedAsset(long assetId, String assetName, String description, long barcode, double price, LocalDate createdAt, long onLocationId, long employeeInChargeId, String imagePath) {
        this.assetId = assetId;
        this.assetName = assetName;
        this.description = description;
        this.barcode = barcode;
        this.price = price;
        this.createdAt = createdAt;
        this.onLocationId = onLocationId;
        this.employeeInChargeId = employeeInChargeId;
        this.imagePath = imagePath;
    }
    @Ignore
    public FixedAsset(String assetName, String description, long barcode, double price, LocalDate createdAt, long employeeInChargeId, long onLocationId, String imagePath) {
        this.assetName = assetName;
        this.description = description;
        this.barcode = barcode;
        this.price = price;
        this.createdAt = createdAt;
        this.employeeInChargeId = employeeInChargeId;
        this.onLocationId = onLocationId;
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FixedAsset)) return false;
        FixedAsset that = (FixedAsset) o;
        return assetId == that.assetId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(assetId);
    }
}
