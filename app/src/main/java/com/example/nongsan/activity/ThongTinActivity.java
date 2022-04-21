package com.example.nongsan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nongsan.R;
import com.example.lib.common.NetworkConnection;
import com.example.lib.common.Show;
import com.example.nongsan.retrofit.ApiBanHang;
import com.example.nongsan.retrofit.RetrofitClient;
import com.example.nongsan.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

public class ThongTinActivity extends AppCompatActivity {
    ApiBanHang apiBanHang;
    Toolbar toolbar_Gioithieuchung;
    NotificationBadge thongbao_soluong;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin);
        getViewId();
        actionToolbar();

        //check network
        if (NetworkConnection.isConnected(this)) {
            int totalItem = 0;
            for(int i = 0; i < Utils.manggiohang.size(); i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            thongbao_soluong.setText(String.valueOf(totalItem));
        } else {
            Show.Notify(this, getString(R.string.error_network));
            finish();
        }
    }

    private void actionToolbar() {
        setSupportActionBar(toolbar_Gioithieuchung);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_Gioithieuchung.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getViewId() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar_Gioithieuchung = findViewById(R.id.toolbar_Thongtin);
        thongbao_soluong = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);

        frameLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(),GioHangActivity.class);
                startActivity(giohang);
            }
        });
    }

    public void openCart(View view) {
        Intent giohang = new Intent(getApplicationContext(),GioHangActivity.class);
        startActivity(giohang);
    }
    @Override
    protected void onStart() {
        super.onStart();
        int totalItem = 0;
        for(int i = 0; i < Utils.manggiohang.size(); i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        thongbao_soluong.setText(String.valueOf(totalItem));
    }
    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i = 0; i < Utils.manggiohang.size(); i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        thongbao_soluong.setText(String.valueOf(totalItem));
    }


    public void ToHome(View view) {
        Intent trangchu = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(trangchu);
    }

    public void ToLienHe(View view) {
        Intent lienhe = new Intent(getApplicationContext(),LienHeActivity.class);
        startActivity(lienhe);
    }
}