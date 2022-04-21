package com.example.nongsan.adapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.nongsan.Interface.ItemClickListener;
import com.example.nongsan.R;
import com.example.nongsan.activity.ChiTietActivity;
import com.example.nongsan.activity.ThemActivity;
import com.example.nongsan.activity.XoaActivity;
import com.example.nongsan.model.SanPhamMoi;
import com.example.nongsan.model.ThemSanPham;
import com.example.nongsan.retrofit.ApiClient;
import com.example.nongsan.retrofit.ApiInterface;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XoaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int id=0;
    Context context;
    List<SanPhamMoi> array;
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    public XoaAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sanpham_xoa, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            SanPhamMoi sanPham = array.get(position);
            myViewHolder.xoatensp.setText(sanPham.getTensp().trim());
            myViewHolder.xoagiasp.setText("Giá: "+sanPham.getGiasp()+" VND/1KG");
            myViewHolder.xoamota.setText(sanPham.getMota());
            myViewHolder.xoaidsp.setText("ID: "+sanPham.getId());
            Glide.with(context).load(sanPham.getHinhanh()).into(myViewHolder.xoahinhanh);


            myViewHolder.btnxoa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getRootView().getContext());
                    dialog.setCancelable(false)
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có muốn xóa sản phẩm này ?" )
                    .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for "Delete".
                            id = sanPham.getId();
                            String Id = String.valueOf(id);
                            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                            Call<ThemSanPham> call = apiInterface.deleteData(Id);

                            call.enqueue(new Callback<ThemSanPham>() {
                                @Override
                                public void onResponse(Call<ThemSanPham> call, Response<ThemSanPham> response) {
                                    ThemSanPham themSanPham = response.body();
                                    Toast.makeText(context, "Trạng thái: "+themSanPham.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(Call<ThemSanPham> call, Throwable t) {
                                }
                            });
                            notifyItemRemoved(position);
                        }
                    })
                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = dialog.create();
                    alert.show();
                }
            });

            myViewHolder.setItemClickListener(new ItemClickListener(){
                @Override
                public void onClick(View view,int pos, boolean isLongClick) {
                    if(!isLongClick){
                        Intent intent = new Intent(context, ChiTietActivity.class);
                        intent.putExtra("chitiet",sanPham);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return array.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_DATA;
    }
    @Override
    public int getItemCount() {
        return array.size();
    }
    public class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView xoatensp, xoagiasp, xoamota, xoaidsp;
        ImageView xoahinhanh;
        AppCompatButton btnxoa;
        private ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            xoatensp = itemView.findViewById(R.id.item_xoa_ten);
            xoagiasp = itemView.findViewById(R.id.item_xoa_gia);
            xoamota = itemView.findViewById(R.id.item_xoa_mota);
            xoahinhanh = itemView.findViewById(R.id.item_xoa_image);
            btnxoa = itemView.findViewById(R.id.btn_xoasp);
            xoaidsp = itemView.findViewById(R.id.txt_xoa_id);
            itemView.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}

