package com.example.nongsan.model;

import com.google.gson.annotations.SerializedName;

public class ThemSanPham {
    @SerializedName("id")
    private String Id;
    @SerializedName("tensp")
    private String Tensp;
    @SerializedName("giasp")
    private String Giasp;
    @SerializedName("hinhanh")
    private String Hinhanh;
    @SerializedName("mota")
    private String Mota;
    @SerializedName("loai")
    private String Loai;
    @SerializedName("trangthai")
    private String Trangthai;
    @SerializedName("message")
    private String Message;

    public String getMessage() {
        return Message;
    }
}
