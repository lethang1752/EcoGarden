package com.example.nongsan.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nongsan.R;
import com.example.nongsan.adapter.GioHangAdapter;
import com.example.nongsan.model.EventBus.TinhTongEvent;
import com.example.nongsan.model.GioHang;
import com.example.nongsan.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

public class GioHangActivity extends AppCompatActivity {
    TextView tongtien;
    ImageView giohangtrong;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button btnmuahang;
    GioHangAdapter adapter;
    long tongtiensp;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        initView();
        initControl();
        tinhTongTien();

    }

    private void tinhTongTien() {
        tongtiensp = 0;
        for(int i = 0; i <Utils.mangmuahang.size();i++) {
            tongtiensp = tongtiensp + (Utils.mangmuahang.get(i).getGiasp()*Utils.mangmuahang.get(i).getSoluong());
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien.setText(decimalFormat.format(tongtiensp) + " VND");
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.mangmuahang.clear();
                finish();
            }
        });

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(Utils.manggiohang.size() == 0){
            giohangtrong.setVisibility(View.VISIBLE);
        }else{
            adapter = new GioHangAdapter(getApplicationContext(), Utils.manggiohang);
            recyclerView.setAdapter(adapter);
        }

        btnmuahang.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(Utils.manggiohang.size()==0){
                    Toast.makeText(getApplicationContext(),"Giỏ hàng trống !",Toast.LENGTH_SHORT).show();

                }
                else if(Utils.mangmuahang.size()==0){
                    Toast.makeText(getApplicationContext(),"Chưa chọn hàng !",Toast.LENGTH_SHORT).show();
                }
                else{
                Intent intent = new Intent(getApplicationContext(), ThanhToanActivity.class);
                intent.putExtra("tongtien",tongtiensp);
                startActivity(intent);
                //Utils.mangmuahang.clear();
                }
            }
        });
    }

    private void initView() {
        giohangtrong = findViewById(R.id.imggiohangtrong);
        tongtien = findViewById(R.id.txttongtien);
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recyclerviewgiohang);
        btnmuahang = findViewById(R.id.btnmuahang);
        checkBox = findViewById(R.id.item_giohang_check);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event){
        if(event != null){
            tinhTongTien();
        }
    }
}