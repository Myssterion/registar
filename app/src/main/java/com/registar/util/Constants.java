package com.registar.util;

import android.Manifest;

final public class Constants {
    private Constants() {}

    public static final String TABLE_NAME_FIXED_ASSET = "fixed_asset";
    public static final String TABLE_NAME_LOCATION = "location";
    public static final String TABLE_NAME_EMPLOYEE = "employee";
    public static final String TABLE_NAME_ASSET_RECORD = "asset_record";
    public static final String TABLE_NAME_ASSET_RECORD_LIST = "asset_record_list";
    public static final String TABLE_NAME_ASSET_LIST_RECORDS = "asset_list_records";
    public static final String DB_NAME = "registardb.db";


    public static final int OPTION_DETAILS = 0;
    public static final int OPTION_UPDATE = 1;
    public static final int OPTION_DELETE = 2;

    public static final int OPTION_GALLERY = 0;
    public static final int OPTION_TAKE_PHOTO = 1;

    public static final int OPTION_BARCODE_MANUALLY = 0;
    public static final int OPTION_BARCODE_SCAN = 1;

    public static final String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA};
    public static final int CAMERA_PERMISSION_CODE = 100;


}
