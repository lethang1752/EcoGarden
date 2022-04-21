package com.example.nongsan.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nongsan.R;
import com.example.nongsan.retrofit.ApiBanHang;
import com.example.nongsan.retrofit.RetrofitClient;
import com.example.nongsan.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {
    EditText email, pass, repass, mobile, username, loaiuser;
    AppCompatButton button;
    ApiBanHang apiBanHang;
    FirebaseAuth firebaseAuth;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        initView();
        initControl();
    }

    private void initControl() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKi();
            }
        });

    }

    private void dangKi() {
        String str_emai = email.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_user = username.getText().toString().trim();
        String str_loaiuser = loaiuser.getText().toString().trim();
        int int_loai = Integer.parseInt(str_loaiuser);

        if(TextUtils.isEmpty(str_emai)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập Email",Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_pass)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập mật khẩu",Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_repass)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập lại mật khẩu",Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_mobile)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập số điện thoại",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(str_user)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập tên tài khoản",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(str_loaiuser)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập loại người dùng",Toast.LENGTH_SHORT).show();
        } else {
            if (str_pass.equals(str_repass)){
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(str_emai,str_pass)
                        .addOnCompleteListener(DangKiActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if(user != null){
                                        postData(str_emai, str_pass, str_user, str_mobile, int_loai, user.getUid());
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),"Email đã tồn tại hoặc không thành công !", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(getApplicationContext(),"Mật khẩu nhập lại chưa khớp",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postData(String str_emai, String str_pass, String str_user, String str_mobile, int int_loai, String uid){
        //Post data
        compositeDisposable.add(apiBanHang.dangKi(str_emai,str_pass,str_user,str_mobile,int_loai, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                Utils.user_current.setEmail(str_emai);
                                Utils.user_current.setPass(str_pass);
                                Intent intent = new Intent(getApplicationContext(),DangNhapActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),userModel.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        repass = findViewById(R.id.repass);
        button = findViewById(R.id.btndangki);
        mobile = findViewById(R.id.mobile);
        username = findViewById(R.id.username);
        loaiuser = findViewById(R.id.loaiuser);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}