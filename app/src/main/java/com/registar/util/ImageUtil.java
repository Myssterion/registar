package com.registar.util;

import static com.registar.util.Constants.OPTION_GALLERY;
import static com.registar.util.Constants.OPTION_TAKE_PHOTO;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.registar.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ImageUtil extends AppCompatActivity {

    public static ActivityResultLauncher<Intent> galleryLauncher, cameraLauncher;
    public static Uri imageUri;
    public static String imagePath;

    public interface ImageSaveCallback {
        void onImageSaved(File imageFile);
    }

    public static void showImageDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.select))
                .setItems(new String[]{context.getString(R.string.asset_dialog_gallery), context.getString(R.string.asset_dialog_photo)}, (dialogInterface, i) -> {
                    switch (i) {
                        case OPTION_GALLERY:
                            openGallery();
                            break;
                        case OPTION_TAKE_PHOTO:
                            openCamera(context);
                            break;
                    }
                }).show();
    }

    private static void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private static void openCamera(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                imageUri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".provider",
                        createImageFile(context)
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(intent);
            }
        } else {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions((Activity) context, Constants.CAMERA_PERMISSIONS, Constants.CAMERA_PERMISSION_CODE);
        }
    }

    private static File createImageFile(Context context) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, "IMG_" + timestamp + ".jpg");
        try {
            if (imageFile.createNewFile()) {
                imagePath = imageFile.getAbsolutePath();
                return imageFile;
            } else {
                throw new IOException("File creation failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createImageFileFromUri(Context context, Uri imageUri, ImageSaveCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
        try {
            // Get InputStream from the Uri
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Log.e("ImageSave", "Unable to open input stream from URI");
                return;
            }

            // Define the app's directory
            File appDir = new File(context.getFilesDir(), "images"); // You can change "images" to another folder name
            if (!appDir.exists()) {
                appDir.mkdirs(); // Create the directory if it doesn't exist
            }

            // Create a file to save the image
            String fileName = "image_" + System.currentTimeMillis() + ".jpg"; // Generate a unique file name
            File imageFile = new File(appDir, fileName);
            imagePath = imageFile.getAbsolutePath();

            // Write the InputStream to the file
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Close streams
            outputStream.close();
            inputStream.close();
            Log.i("ImageSave", "Image saved to: " + imageFile.getAbsolutePath());
            new Handler(Looper.getMainLooper()).post(() -> {
                if (callback != null) {
                    callback.onImageSaved(imageFile);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ImageSave", "Error saving image: " + e.getMessage());
        }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        System.out.println("HAHAHHA");
        if (requestCode == Constants.CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                System.out.println("GRANTEDdddddddddddddddddd");
                openCamera(this);
            } else {
                // Permission denied
              /*  if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // "Don't Ask Again" scenario detected
                    openAppSettings();
                } else {
                    // Permission denied without "Don't Ask Again"
                    Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
                }*/
            }
        }
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
