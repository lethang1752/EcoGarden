package com.example.nongsan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.nongsan.model.EventBus.TinhTongEvent;
import com.example.nongsan.model.GioHang;
import com.example.nongsan.R;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class ThanhToanSpAdapter extends RecyclerView.Adapter<ThanhToanSpAdapter.MyViewHolder> {
    Context context;
    List<GioHang> gioHangList;

    public ThanhToanSpAdapter(Context context,List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thanhtoan, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);
        holder.item_giohang_tensp.setText(gioHang.getTensp());
        holder.item_giohang_soluong.setText(gioHang.getSoluong()+" ");
        Glide.with(context).load(gioHang.getHinhsp()).into(holder.item_giohang_image);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        long gia = gioHang.getSoluong()*gioHang.getGiasp();
        holder.item_giohang_giasp2.setText(decimalFormat.format(gia) + " VND");
        EventBus.getDefault().postSticky(new TinhTongEvent());
    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView item_giohang_image;
        TextView item_giohang_tensp, item_giohang_soluong, item_giohang_giasp2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_giohang_image = itemView.findViewById(R.id.item_thanhtoan_image);
            item_giohang_tensp = itemView.findViewById(R.id.item_thanhtoan_tensp);
            item_giohang_soluong = itemView.findViewById(R.id.item_thanhtoan_soluong);
            item_giohang_giasp2 = itemView.findViewById(R.id.item_thanhtoan_giasp2);
        }
    }
}
