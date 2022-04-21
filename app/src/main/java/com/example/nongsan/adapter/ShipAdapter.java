package com.example.nongsan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nongsan.Interface.ItemClickListener;
import com.example.nongsan.R;
import com.example.nongsan.model.DonHang;
import com.example.nongsan.model.Ship;
import com.example.nongsan.retrofit.ApiClient;
import com.example.nongsan.retrofit.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipAdapter extends RecyclerView.Adapter<ShipAdapter.MyViewHolder>{
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<DonHang> listdonhang;
    String status;
    int id;

    public ShipAdapter(Context context, List<DonHang> listdonhang) {
        this.context = context;
        this.listdonhang = listdonhang;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shiphang, parent, false);
        return new MyViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listdonhang.get(position);
        id = donHang.getId();

        holder.txtdonhang.setText("Đơn hàng: " + donHang.getId());
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView txtdonhang, trangthai;
        RecyclerView reChitiet;
        CardView cardview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            trangthai = itemView.findViewById(R.id.trangthai);
            txtdonhang = itemView.findViewById(R.id.idshiphang);
            reChitiet = itemView.findViewById(R.id.recyclerview_chitietship);
            cardview = itemView.findViewById(R.id.xcardview);
            cardview.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Chọn trạng thái");
            contextMenu.add(this.getBindingAdapterPosition(),121,0,"Lấy hàng");
            contextMenu.add(this.getBindingAdapterPosition(),122,1,"Vận chuyển");
            contextMenu.add(this.getBindingAdapterPosition(),123,2,"Đã giao");
        }
    }
    public void updatestatus(String Id, String status){
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Ship> call = apiInterface.updateStatus(Id,status);

        call.enqueue(new Callback<Ship>() {
            @Override
            public void onResponse(Call<Ship> call, Response<Ship> response) {
                Ship ship = response.body();
            }
            @Override
            public void onFailure(Call<Ship> call, Throwable t) {
            }
        });
    }


    public void StatusGet(int position){
        DonHang donHang = listdonhang.get(position);
        String id = String.valueOf(donHang.getId());
        donHang.setTrangthai(status);
        status = "Lấy hàng";
        updatestatus(id,status);
    }


    public void StatusDeli(int position){
        DonHang donHang = listdonhang.get(position);
        String id = String.valueOf(donHang.getId());
        donHang.setTrangthai(status);
        status = "Vận chuyển";
        updatestatus(id,status);
    }

    public void StatusDone(int position){
        DonHang donHang = listdonhang.get(position);
        String id = String.valueOf(donHang.getId());
        donHang.setTrangthai(status);
        status = "Đã giao";
        updatestatus(id,status);
    }
}


