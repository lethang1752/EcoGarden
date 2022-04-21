package com.example.nongsan.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nongsan.R;
import com.example.nongsan.adapter.ShipAdapter;
import com.example.nongsan.retrofit.ApiBanHang;
import com.example.nongsan.retrofit.RetrofitClient;
import com.example.nongsan.utils.Utils;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ShipperActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView redonhang;
    Toolbar toolbar;
    ShipAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper);
        initView();
        initToolbar();
        getOrder();
    }

    private void getOrder() {
        compositeDisposable.add(apiBanHang.layDonHang()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                                adapter = new ShipAdapter(getApplicationContext(), donHangModel.getResult());
                                redonhang.setAdapter(adapter);
                        },
                        throwable -> {
                        }
                ));
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        redonhang = findViewById(R.id.recyclerview_shiphang);
        toolbar = findViewById(R.id.toobar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        redonhang.setLayoutManager(layoutManager);
        redonhang.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 121:
                adapter.StatusGet(item.getGroupId());
                adapter.notifyDataSetChanged();
                return true;
            case 122:
                adapter.StatusDeli(item.getGroupId());
                adapter.notifyDataSetChanged();
                return true;
            case 123:
                adapter.StatusDone(item.getGroupId());
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}