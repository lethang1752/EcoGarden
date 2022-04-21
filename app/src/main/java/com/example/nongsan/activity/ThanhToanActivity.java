package com.example.nongsan.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nongsan.Helper.AppInfo;
import com.example.nongsan.Helper.CreateOrder;
import com.example.nongsan.R;
import com.example.nongsan.adapter.SanPhamMoiAdapter;
import com.example.nongsan.adapter.ShipAdapter;
import com.example.nongsan.adapter.ThanhToanSpAdapter;

import com.example.nongsan.model.NotiSendData;
import com.example.nongsan.model.User;
import com.example.nongsan.retrofit.ApiBanHang;
import com.example.nongsan.retrofit.ApiPushNotification;
import com.example.nongsan.retrofit.RetrofitClient;
import com.example.nongsan.retrofit.RetrofitClientNoti;
import com.example.nongsan.utils.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ThanhToanActivity extends AppCompatActivity {

    String amount;
    String tokenmanager;
    Toolbar toolbar;
    TextView txttongtien, txtsodt, txtemail;
    EditText edtdiachi;
    AppCompatButton btndathang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    long tongtien;
    int totalItem;
    RecyclerView recyclerViewsp;
    ThanhToanSpAdapter adapter;
    List<User> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        initView();
        countItem();
        initControl();

        getTokenmanager();


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        ZaloPaySDK.tearDown();
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

    }

    private void countItem() {
        totalItem = 0;
        for(int i = 0; i <Utils.mangmuahang.size();i++){
            totalItem = totalItem + Utils.mangmuahang.get(i).getSoluong();
        }
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerViewsp.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewsp.setLayoutManager(layoutManager);

        adapter = new ThanhToanSpAdapter(getApplicationContext(), Utils.mangmuahang);
        recyclerViewsp.setAdapter(adapter);

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien = getIntent().getLongExtra("tongtien",0);
        txttongtien.setText(decimalFormat.format(tongtien)+" VND");
        txtemail.setText(Utils.user_current.getEmail());
        txtsodt.setText(Utils.user_current.getMobile());

        btndathang.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Thanh toán bằng ZaloPay !", Toast.LENGTH_SHORT).show();
                CreateOrder orderApi = new CreateOrder();

                String str_diachi = edtdiachi.getText().toString().trim();
                if(TextUtils.isEmpty(str_diachi)){
                    Toast.makeText(getApplicationContext(),"Bạn chưa nhập địa chỉ !",Toast.LENGTH_SHORT).show();
                }else{
                    //post data
                    amount = String.valueOf(tongtien);
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();
                    Log.d("test",new Gson().toJson(Utils.mangmuahang)); //Kiem tra
                    compositeDisposable.add(apiBanHang.createOrder(str_email, str_sdt, String.valueOf(tongtien), id, str_diachi, totalItem, new Gson().toJson(Utils.mangmuahang))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            userModel -> {
                                try {
                                    JSONObject data = orderApi.createOrder(amount);
                                    String code = data.getString("returncode");

                                    if (code.equals("1")) {

                                        String token = data.getString("zptranstoken");

                                        ZaloPaySDK.getInstance().payOrder(ThanhToanActivity.this, token, "demozpdk://app", new PayOrderListener() {
                                            @Override
                                            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                                                Toast.makeText(ThanhToanActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();

                                                pushNotiToAdmin();

                                                Utils.mangmuahang.clear();
                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }

                                            @Override
                                            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                                                Toast.makeText(ThanhToanActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                                                Toast.makeText(ThanhToanActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));
                }
            }
        });
    }

    private void getTokenmanager(){
        compositeDisposable.add(apiBanHang.gettokenmanager()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tokenManagerModel -> {
                            if(tokenManagerModel.isSuccess()){
                                tokenmanager = tokenManagerModel.getToken();
                            }
                        },
                        throwable -> {
                        }
                ));
    }

    private void pushNotiToAdmin() {
//        "fhhPVZPaSw2zKp4DaHZWZp:APA91bG9Q2qP0jaBLNgK5lxpZx-hQg5Sf4di8zSQDtagRFnb75oGPRIFUWzq_YXVspMqw8rIPmHBKjqws6qZY5gSbQ3UbhOASL301JtgNwlg3AL3X3mzihyWIFNGlPY3AlZn4LTcJleB"
        String token = "dhGlyAj5QUWpTeKrX08u6w:APA91bHdIjTqlxbVYX6u_uCMJhrZ73-i9L9H-41iwUFwoEyHlXCeOkGxe38wEFleWcnNPTxZVcPR_7SMcvTiGbYABagq6ECacISxFN4vwiGNp52KOLwmwatr3sznBZpx25t7vMsfBN3w";
        Map<String, String> data = new HashMap<>();
        data.put("title", "thong bao");
        data.put("body", "Ban co don hang moi");
        NotiSendData notiSendData = new NotiSendData(token, data);
        ApiPushNotification apiPushNotification = RetrofitClientNoti.getInstance().create(ApiPushNotification.class);
        compositeDisposable.add(apiPushNotification.sendNotification(notiSendData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        notiResponse->{

                        },
                        throwable->{
                            Log.d("logg",throwable.getMessage());
                        }
                ));
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toobar);
        txttongtien = findViewById(R.id.txttongtien);
        txtsodt = findViewById(R.id.txtsodienthoai);
        txtemail = findViewById(R.id.txtemail);
        edtdiachi = findViewById(R.id.edtdiachi);
        btndathang = findViewById(R.id.btndathang);
        recyclerViewsp = findViewById(R.id.recyclerview_thanhtoan);

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}