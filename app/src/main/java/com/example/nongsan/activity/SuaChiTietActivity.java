package com.example.nongsan.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nongsan.R;
import com.example.nongsan.model.SanPhamMoi;
import com.example.nongsan.model.ThemSanPham;
import com.example.nongsan.retrofit.ApiClient;
import com.example.nongsan.retrofit.ApiInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuaChiTietActivity extends AppCompatActivity {
    public static final String TAG = ThemActivity.class.getName();
    private static final int MY_REQUEST_CODE = 10;
    int id=0;
    EditText tensp, giasp, mota, loai;
    TextView idsp, suatensp, suagiasp, suamota, sualoai;
    AppCompatButton btnsua, btn_select_img_sua;
    ImageView imghinhanh, imganhgoc;
    SanPhamMoi sanPhamMoi;
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
                            imghinhanh.setImageBitmap(bitmap);
                            btnsua.setEnabled(true);
                            btn_select_img_sua.setEnabled(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_chi_tiet);
        ActionToolBar();
        initView();
        initData();

        suamota.setMovementMethod(new ScrollingMovementMethod());

        btn_select_img_sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermissions();
            }
        });

        btnsua.setOnClickListener(new View.OnClickListener() {
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
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
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

    private void UploadImage(){
        id = sanPhamMoi.getId();
        String Id = String.valueOf(id);
        String Image = imageToString();
        String Tensp = tensp.getText().toString();
        String Giasp = giasp.getText().toString();
        String Mota = mota.getText().toString();
        String Loai = loai.getText().toString();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ThemSanPham> call = apiInterface.updateImage(Id,Tensp,Giasp,Image,Mota,Loai);

        call.enqueue(new Callback<ThemSanPham>() {
            @Override
            public void onResponse(Call<ThemSanPham> call, Response<ThemSanPham> response) {
                ThemSanPham themSanPham = response.body();
                Toast.makeText(SuaChiTietActivity.this,"Trạng thái: "+themSanPham.getMessage(), Toast.LENGTH_LONG).show();
                btn_select_img_sua.setEnabled(true);
                btnsua.setEnabled(true);
                tensp.setText("");
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

    @SuppressLint("SetTextI18n")
    private void initData() {
        sanPhamMoi = (SanPhamMoi) getIntent().getSerializableExtra("chitiet");
        suatensp.setText("Tên sản phẩm: "+sanPhamMoi.getTensp());
        suamota.setText("Mô tả: "+sanPhamMoi.getMota());
        Glide.with(getApplicationContext()).load(sanPhamMoi.getHinhanh()).into(imganhgoc);
        suagiasp.setText("Giá sản phẩm: "+sanPhamMoi.getGiasp()+" VND/1KG");
        sualoai.setText("Loại sản phẩm: "+Integer.toString(sanPhamMoi.getLoai()));
        idsp.setText("ID sản phẩm: "+sanPhamMoi.getId());
    }

    private void ActionToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_SuachitietSp);
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
        tensp = findViewById(R.id.edtsuatensp);
        giasp = findViewById(R.id.edtsuagiasp);
        mota = findViewById(R.id.edtsuamotasp);
        btnsua = findViewById(R.id.btn_renew);
        imghinhanh = findViewById(R.id.img_sua);
        loai = findViewById(R.id.edtsualoaisp);
        btn_select_img_sua = findViewById(R.id.btn_select_img_sua);
        idsp = findViewById(R.id.txtidsp);

        suatensp = findViewById(R.id.txtsuatensp);
        suagiasp = findViewById(R.id.txtsuagiasp);
        suamota = findViewById(R.id.txtsuamota);
        sualoai = findViewById(R.id.txtsualoai);
        imganhgoc = findViewById(R.id.img_suagoc);
    }
}