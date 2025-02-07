package com.registar.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.registar.util.Constants;

import java.io.Serializable;

@Entity(tableName = Constants.TABLE_NAME_ASSET_RECORD_LIST)
public class AssetRecordList implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    private long listId;
    @ColumnInfo(name = "list_name")
    private String listName;

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public AssetRecordList() {
    }

    @Ignore
    public AssetRecordList(String listName) {
        this.listName = listName;
    }
    @Ignore
    public AssetRecordList(long listId, String listName) {
        this.listId = listId;
        this.listName = listName;
    }
}
