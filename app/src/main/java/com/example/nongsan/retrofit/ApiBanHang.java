package com.example.nongsan.retrofit;

import com.example.nongsan.model.DonHangModel;
import com.example.nongsan.model.LoaiSpModel;
import com.example.nongsan.model.MessageModel;
import com.example.nongsan.model.SanPhamMoiModel;
import com.example.nongsan.model.ThemSanPham;
import com.example.nongsan.model.TokenManagerModel;
import com.example.nongsan.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiBanHang {
    //get data
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getloaisp();

    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();

    //post data
    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham(
            @Field("page") int page,
            @Field("loai") int loai
    );

    //dang ki
    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("loaiuser") int loaiuser,
            @Field("uid") String uid

    );

    //dang nhap
    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email
    );
    //post data don hang
    @POST("donhang.php")
    @FormUrlEncoded
    Observable<UserModel> createOrder(
            @Field("email") String email,
            @Field("sdt") String sdt,
            @Field("tongtien") String tongtien,
            @Field("iduser") int id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );
    //Xem don hang
    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("iduser") int id
    );
    //Tim kiem san pham
    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> search(
            @Field("search") String search
    );
    //lay don hang ship
    @GET("donhangship.php")
    Observable<DonHangModel> layDonHang();
    //update token
    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("id") int id,
            @Field("token") String token
    );
    //get token quan ly
    @GET("gettokenmanager.php")
    Observable<TokenManagerModel> gettokenmanager();

}
