package com.example.nongsan.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.nongsan.R;
import com.example.nongsan.model.ThemSanPham;

import com.example.nongsan.retrofit.ApiClient;
import com.example.nongsan.retrofit.ApiInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemActivity extends AppCompatActivity{
    public static final String TAG = ThemActivity.class.getName();
    private static final int MY_REQUEST_CODE = 10;
    EditText edtTensp, edtGiasp, edtMotasp, edtLoaisp;
    ImageView imgUploadPic;
    AppCompatButton btnSelectImg, btnUpload;
    Toolbar toolbar;
    Bitmap bitmap;

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult (ActivityResult result) {
            Log.e(TAG, "onActivityResult");
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                Intent data = result.getData();
                if (data == null) {
                    return;
                }
                Uri path = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                        imgUploadPic.setImageBitmap(bitmap);
                        btnUpload.setEnabled(true);
                        btnSelectImg.setEnabled(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them);

        initView();
        actionToolbar();

        btnSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermissions();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
            }
        });

    }


    private void onClickRequestPermissions() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            selectImage();
            return;
        }
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            selectImage();
        }else{
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_REQUEST_CODE){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
        }
    }

    private void actionToolbar() {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
    }

    private void initView() {
        edtTensp = findViewById(R.id.edtthemtensp);
        edtGiasp = findViewById(R.id.edtthemgiasp);
        edtMotasp = findViewById(R.id.edtthemmotasp);
        edtLoaisp = findViewById(R.id.edtthemloaisp);
        imgUploadPic = findViewById(R.id.img_upload);
        btnSelectImg = findViewById(R.id.btn_select_img);
        btnUpload = findViewById(R.id.btn_upload);
        toolbar = findViewById(R.id.toolbar_ThemSp);
    }

    private void UploadImage(){
        String Image = imageToString();
        String Tensp = edtTensp.getText().toString();
        String Giasp = edtGiasp.getText().toString();
        String Mota = edtMotasp.getText().toString();
        String Loai = edtLoaisp.getText().toString();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ThemSanPham> call = apiInterface.uploadImage(Tensp,Giasp,Image,Mota,Loai);

        call.enqueue(new Callback<ThemSanPham>() {
            @Override
            public void onResponse(Call<ThemSanPham> call, Response<ThemSanPham> response) {
                ThemSanPham themSanPham = response.body();
                Toast.makeText(ThemActivity.this,"Trạng thái: "+themSanPham.getMessage(), Toast.LENGTH_LONG).show();
                btnSelectImg.setEnabled(true);
                btnUpload.setEnabled(true);
                edtTensp.setText("");
            }

            @Override
            public void onFailure(Call<ThemSanPham> call, Throwable t) {

            }
        });

    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "select picture"));
    }

    private String imageToString() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }
}