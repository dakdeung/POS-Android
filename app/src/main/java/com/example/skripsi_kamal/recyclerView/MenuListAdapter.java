package com.example.skripsi_kamal.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skripsi_kamal.R;
import com.example.skripsi_kamal.config.UserConfig;
import com.example.skripsi_kamal.model.MenuResponse;

import java.util.List;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MenuListAdapterVH>{
    private List<MenuResponse> menuList;
    private Context context;
    private MenuListAdapter.ClickedItem clickedItem;

    public MenuListAdapter(MenuListAdapter.ClickedItem clickedItem){
        this.clickedItem = clickedItem;
    }

    public void setData(List<MenuResponse> menuList) {
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuListAdapter.MenuListAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new MenuListAdapter.MenuListAdapterVH(LayoutInflater.from(context).inflate(R.layout.list_menu_2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuListAdapter.MenuListAdapterVH holder, int position) {
        final MenuResponse response = menuList.get(position);
        if (!UserConfig.getInstance().getUserEmail().isEmpty()){
            holder.btnHapus.setVisibility(View.GONE);
        }

        holder.mTvProductName.setText(response.getName());
        holder.mTvQty.setText(response.getQty());
        holder.mTvProductPrice.setText(response.getPrice());
        holder.mTvProductTotalPrice.setText(response.getTotal());
        holder.btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.ClickedMenu(response);
            }
        });
    }

    public interface ClickedItem{
        public void ClickedMenu(MenuResponse menuResponse);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public class MenuListAdapterVH extends RecyclerView.ViewHolder {
        TextView mTvProductName, mTvQty, mTvProductPrice, mTvProductTotalPrice;
        Button btnHapus;
        public MenuListAdapterVH(@NonNull View itemView) {
            super(itemView);
            mTvProductName = itemView.findViewById(R.id.tv_product_name);
            mTvQty = itemView.findViewById(R.id.tv_qty);
            mTvProductPrice = itemView.findViewById(R.id.tv_product_price);
            mTvProductTotalPrice = itemView.findViewById(R.id.tv_product_total_price);
            btnHapus = itemView.findViewById(R.id.btn_hapus);
        }
    }
}
