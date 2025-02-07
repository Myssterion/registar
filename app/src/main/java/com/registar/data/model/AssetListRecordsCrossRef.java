package com.registar.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.registar.util.Constants;

import java.io.Serializable;

@Entity(tableName = Constants.TABLE_NAME_ASSET_LIST_RECORDS,
        foreignKeys = {
                @ForeignKey(entity = AssetRecord.class,
                        parentColumns = "record_id",
                        childColumns = "asset_record_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = AssetRecordList.class,
                        parentColumns = "list_id",
                        childColumns = "asset_list_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class AssetListRecordsCrossRef implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "asset_list_id")
    private long assetListId;
    @ColumnInfo(name = "asset_record_id")
    private long assetRecordId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAssetRecordId() {
        return assetRecordId;
    }

    public void setAssetRecordId(long assetRecordId) {
        this.assetRecordId = assetRecordId;
    }

    public long getAssetListId() {
        return assetListId;
    }

    public void setAssetListId(long assetListId) {
        this.assetListId = assetListId;
    }
}
