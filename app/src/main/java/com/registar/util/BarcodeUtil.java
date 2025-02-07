package com.registar.util;

import static com.registar.util.Constants.OPTION_BARCODE_MANUALLY;
import static com.registar.util.Constants.OPTION_BARCODE_SCAN;
import static com.registar.util.Constants.OPTION_GALLERY;
import static com.registar.util.Constants.OPTION_TAKE_PHOTO;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.registar.R;

public class BarcodeUtil {

    public static ActivityResultLauncher<Intent> barcodeLauncher;

    public static void showBarcodeDialog(EditText assetBarcode) {
        Context context = assetBarcode.getContext();
        new AlertDialog.Builder(context)
                .setTitle(R.string.select)
                .setItems(new String[]{context.getString(R.string.asset_dialog_barcode_manually), context.getString(R.string.asset_dialog_barcode_scan)}, (dialogInterface, i) -> {
                    switch (i) {
                        case OPTION_BARCODE_MANUALLY:
                            assetBarcode.setFocusable(true);
                            assetBarcode.setFocusableInTouchMode(true);
                            assetBarcode.requestFocus();
                            break;
                        case OPTION_BARCODE_SCAN:
                           scanBarcode(context);
                            break;
                    }
                }).show();
    }

    public static void scanBarcode(Context context) {
        Intent intent = new Intent(context, CaptureActivity.class);
        barcodeLauncher.launch(intent);
    }

}
