package com.example.nongsan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nongsan.R;
import com.example.nongsan.model.DonHang;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder>{
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<DonHang> listdonhang;

    public DonHangAdapter(Context context, List<DonHang> listdonhang) {
        this.context = context;
        this.listdonhang = listdonhang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listdonhang.get(position);
        holder.txtdonhang.setText("Đơn hàng: " + donHang.getId()+" ");
        holder.trangthai.setText("Trạng thái: " + donHang.getTrangthai());
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.reChitiet.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        //adapter chi tiet
        ChitietAdapter chitietAdapter = new ChitietAdapter(context, donHang.getItem());
        holder.reChitiet.setLayoutManager(layoutManager);
        holder.reChitiet.setAdapter(chitietAdapter);
        holder.reChitiet.setRecycledViewPool(viewPool);

    }

    @Override
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtdonhang, trangthai;
        RecyclerView reChitiet;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            trangthai = itemView.findViewById(R.id.idtrangthai);
            txtdonhang = itemView.findViewById(R.id.iddonhang);
            reChitiet = itemView.findViewById(R.id.recyclerview_chitiet);
        }
    }
}
