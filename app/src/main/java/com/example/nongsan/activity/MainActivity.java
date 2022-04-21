package com.example.nongsan.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
//import android.widget.Toolbar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.nongsan.R;
import com.example.nongsan.adapter.LoaiSpAdapter;
import com.example.nongsan.adapter.SanPhamMoiAdapter;
import com.example.nongsan.model.LoaiSp;
import com.example.nongsan.model.SanPhamMoi;
import com.example.nongsan.model.User;
import com.example.nongsan.retrofit.ApiBanHang;
import com.example.nongsan.retrofit.RetrofitClient;
import com.example.nongsan.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if(Paper.book().read("user") != null){
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        getToken();
        Anhxa();
        ActionBar();
        if(isConnected(this)){
            //Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show(); câu thông báo
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else {
            Toast.makeText(getApplicationContext(),"Không có Internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
        }
    }

    public void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>(){
                    @Override
                    public void onSuccess(String s) {
                        if(!TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(),s)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {

                                    },
                                    throwable -> {
                                        Log.d("Log",throwable.getMessage());
                                    }
                            ));
                        }
                    }
                });
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){

                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                        case 1:
                            Intent luongthuc = new Intent(getApplicationContext(), LuongThucActivity.class);
                            luongthuc.putExtra("loai",1);
                            startActivity(luongthuc);
                            break;
                            case 2:
                                Intent hoaqua = new Intent(getApplicationContext(), HoaQuaActivity.class);
                                hoaqua.putExtra("loai",2);
                                startActivity(hoaqua);
                                break;
                                case 3:
                                    Intent thongtin = new Intent(getApplicationContext(), ThongTinActivity.class);
                                    startActivity(thongtin);
                                    break;
                                    case 4:
                                        Intent lienhe = new Intent(getApplicationContext(), LienHeActivity.class);
                                        startActivity(lienhe);
                                        break;
                                        case 5:
                                            Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                                            startActivity(donhang);
                                            break;
                                            case 6:
                                                //xoa key user
                                                Paper.book().delete("user");
                                                FirebaseAuth.getInstance().signOut();
                                                Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                                                startActivity(dangnhap);
                                                finish();
                                                break;
                                                case 7:
                                                    if(Utils.user_current.getLoaiuser() == 2) {
                                                            Intent quanly = new Intent(getApplicationContext(), QuanLyActivity.class);
                                                            startActivity(quanly);
                                                    }else {
                                                        Toast.makeText(getApplicationContext(), "Danh mục chỉ dành cho Quản trị viên", Toast.LENGTH_LONG).show();
                                                    }
                                                    break;
                                                    case 8:
                                                        if(Utils.user_current.getLoaiuser() == 3) {
                                                            Intent shipper = new Intent(getApplicationContext(), ShipperActivity.class);
                                                            startActivity(shipper);
                                                        }else {
                                                            Toast.makeText(getApplicationContext(), "Danh mục chỉ dành cho Shipper", Toast.LENGTH_LONG).show();
                                                        }
                                                        break;
                }
            }
        });
    }

    private void getSpMoi() {

        compositeDisposable.add(apiBanHang.getSpMoi()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                sanPhamMoiModel -> {
                    if(sanPhamMoiModel.isSuccess()){
                        mangSpMoi = sanPhamMoiModel.getResult();
                        spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                        recyclerViewManHinhChinh.setAdapter(spAdapter);
                    }
                },
                throwable -> {
                    Toast.makeText(getApplicationContext(),"Không kết nối được với Server"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
        ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getloaisp()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                loaiSpModel -> {
                    if (loaiSpModel.isSuccess()){
                        //Toast.makeText(getApplicationContext(), loaiSpModel.getResult().get(0).getTensanpham(), Toast.LENGTH_LONG).show();
                        mangloaisp = loaiSpModel.getResult();
                        loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                        listViewManHinhChinh.setAdapter(loaiSpAdapter);
                    }
                }
        ));

    }

    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<String>();
        mangquangcao.add("https://hbmedia.com.vn/wp-content/uploads/2019/10/266.jpg");
        mangquangcao.add("https://tnfoods.vn/wp-content/uploads/2019/03/banner2.jpg");
        mangquangcao.add("https://bizweb.dktcdn.net/100/376/624/themes/751042/assets/slider_1.jpg?1639414221981");
        mangquangcao.add("http://www.nongsantoancau.com/wp-content/uploads/2015/03/14509_original.jpg");
        for(int i=0; i<mangquangcao.size();i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);

        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Context context;
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void Anhxa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toobarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        //khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if(Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else{
            int totalItem = 0;
            for(int i = 0; i <Utils.manggiohang.size();i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(),GioHangActivity.class);
                startActivity(giohang);
            }
        });

        imgsearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i = 0; i <Utils.manggiohang.size();i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //nho them quyen de ko loi
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
    }

    public void ToHome(View view) {
        Intent trangchu = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(trangchu);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
