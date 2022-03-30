package com.example.ambilgambar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    private static final int READ_PERMISSION_STORAGE = 788;
    private static final int CAMERA_PERMISSION = 789;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imagePicture);
        button = findViewById(R.id.btn_image_take);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] takePicture = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Pilih Aksi");
                builder.setItems(takePicture, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                openCamera();
                                break;
                            case 1:
                                openGallery();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    private boolean readPermissionStorage(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_STORAGE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean cameraPermission(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    private String getPath(Uri uri){
        String result = null;
        String[] dataimages = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri,dataimages,null,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                int column_index = cursor.getColumnIndexOrThrow(dataimages[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null){
            result = "Not Found";
        }
        return result;
    }

    private void openCamera(){
        if (cameraPermission()){
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera,CAMERA_PERMISSION);
        }

    }

    private void openGallery(){
        if (readPermissionStorage()){
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery,READ_PERMISSION_STORAGE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_PERMISSION){
                Bundle extra = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extra.get("data");
                imageView.setImageBitmap(imageBitmap);
            }else if (requestCode == READ_PERMISSION_STORAGE){
                Uri selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
//                InputStream imageStream = null;
//                try {
//                    imageStream = this.getContentResolver().openInputStream(selectedImage);
//                }catch (FileNotFoundException e){
//                    e.printStackTrace();
//                }
//                final Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
//                Bitmap scaleBitmap = Bitmap.createScaledBitmap(imageBitmap,(int) (imageBitmap.getWidth() * 0.1),(int) (imageBitmap.getHeight() * 0.1),true);
//                String path = getPath(selectedImage);
//                Matrix matrix = new Matrix();
//                ExifInterface exif;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                    try {
//                        exif = new ExifInterface(path);
//                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
//                        switch (orientation){
//                            case ExifInterface.ORIENTATION_ROTATE_90:
//                                matrix.postRotate(90);
//                                break;
//                            case ExifInterface.ORIENTATION_ROTATE_180:
//                                matrix.postRotate(180);
//                                break;
//                            case ExifInterface.ORIENTATION_ROTATE_270:
//                                matrix.postRotate(270);
//                                break;
//                        }
//                    }catch (IOException e){
//                        e.printStackTrace();
//                    }
//                }
//
//                Bitmap rotatedBitmap = Bitmap.createBitmap(scaleBitmap,scaleBitmap.getWidth(),scaleBitmap.getHeight(),0,0,matrix,true);
//                ByteArrayOutputStream boas = new ByteArrayOutputStream();
//                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,80,boas);
//                imageView.setImageBitmap(rotatedBitmap);
            }
        }
    }
}