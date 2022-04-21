package com.example.nongsan.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nongsan.R;

public class QuanLyActivity extends AppCompatActivity {

    Toolbar toolbar_Quanly;
    AppCompatButton btnthem, btnsua, btnxoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly);
        getViewId();
        actionToolbar();
        initControl();
    }

    private void initControl() {
        btnsua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SuaActivity.class);
                startActivity(intent);
            }
        });
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ThemActivity.class);
                startActivity(intent);
            }
        });
        btnxoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),XoaActivity.class);
                startActivity(intent);
            }
        });
    }

    private void actionToolbar() {
        setSupportActionBar(toolbar_Quanly);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_Quanly.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void getViewId() {
        toolbar_Quanly = findViewById(R.id.toolbar_Quanly);
        btnsua = findViewById(R.id.btnsua);
        btnxoa = findViewById(R.id.btnxoa);
        btnthem = findViewById(R.id.btnthem);
    }

    public void ToHome(View view) {
        Intent trangchu = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(trangchu);
    }
}