package com.example.nongsan.retrofit;

import com.example.nongsan.model.DonHang;
import com.example.nongsan.model.Ship;
import com.example.nongsan.model.ThemSanPham;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("themsanpham.php")
    Call<ThemSanPham> uploadImage(
            @Field("tensp") String tensp,
            @Field("giasp") String giasp,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") String loai
    );

    @FormUrlEncoded
    @POST("suasanpham.php")
    Call<ThemSanPham> updateImage(
            @Field("id") String id,
            @Field("tensp") String tensp,
            @Field("giasp") String giasp,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") String loai
    );

    @FormUrlEncoded
    @POST("xoasanpham.php")
    Call<ThemSanPham> deleteData(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("updatetrangthai.php")
    Call<Ship> updateStatus(
            @Field("id") String id,
            @Field("trangthai") String trangthai
    );
}
