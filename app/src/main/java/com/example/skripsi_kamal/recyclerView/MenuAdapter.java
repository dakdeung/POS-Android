package com.example.skripsi_kamal.recyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skripsi_kamal.R;
import com.example.skripsi_kamal.model.MenuResponse;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import timber.log.Timber;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuAdapterVH> {
    private List<MenuResponse> menuList;
    private Context context;
    private MenuAdapter.ClickedItem clickedItem;

    public MenuAdapter(MenuAdapter.ClickedItem clickedItem){
        this.clickedItem = clickedItem;
    }

    public void setData(List<MenuResponse> menuList) {
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuAdapter.MenuAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new MenuAdapter.MenuAdapterVH(LayoutInflater.from(context).inflate(R.layout.list_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.MenuAdapterVH holder, int position) {
        final MenuResponse response = menuList.get(position);
        if (StringUtils.isNotEmpty(menuList.get(position).getImageUrl())) {
            try {
                Glide.with(context).load(menuList
                        .get(position).getImageUrl())
                        .into(holder.mImvProduk);
            } catch (Exception ex) {
                Timber.e(ex);
            }
        }

        if (response.getCount() == 0){
            holder.mTvCount.setVisibility(View.GONE);
        } else {
            holder.mTvCount.setVisibility(View.VISIBLE);
        }
        holder.mTvNamaProduk.setText(response.getName());
        holder.mTvHarga.setText(response.getPrice());
        holder.mTvDeskripsi.setText(response.getDescription());
        holder.mTvCount.setText(response.getCount().toString() + "x");
        holder.card.setOnClickListener(view -> clickedItem.ClickedMenu(response));

//        if (response.getCount() == 0){
//            holder.btnMin.setVisibility(View.GONE);
//            holder.btnPlus.setOnClickListener(view -> setCountAdd(holder, menuList.get(position), position));
//        } else {
//            holder.btnMin.setVisibility(View.VISIBLE);
//            holder.btnPlus.setOnClickListener(view -> setCountAdd(holder, menuList.get(position), position));
//            holder.btnMin.setOnClickListener(view -> setCountRemove(holder, menuList.get(position), position));
//        }
    }

    private void setCountAdd(MenuAdapterVH holder,MenuResponse menuResponse, int position){
        int count = menuList.get(position).getCount();
        count = count + 1;
        menuResponse.setCount(count);
        notifyItemChanged(position);
        clickedItem.ClickedMenu(menuResponse);
        holder.mTvCount.setText(String.valueOf(count));
    }

    private void setCountRemove(MenuAdapterVH holder,MenuResponse menuResponse, int position){
        int count = menuList.get(position).getCount();
        count = count - 1;
        menuResponse.setCount(count);
        notifyItemChanged(position);
        clickedItem.ClickedMenu(menuResponse);
        holder.mTvCount.setText(String.valueOf(count) + "x");
    }

    public interface ClickedItem{
        public void ClickedMenu(MenuResponse menuResponse);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }


    public class MenuAdapterVH extends RecyclerView.ViewHolder {
        ImageView mImvProduk;
        TextView mTvNamaProduk, mTvDeskripsi, mTvHarga, mTvCount;
        Button btnPlus, btnMin;
        LinearLayout card;
        public MenuAdapterVH(@NonNull View itemView) {
            super(itemView);
            mImvProduk = itemView.findViewById(R.id.imv_produk);
            mTvNamaProduk = itemView.findViewById(R.id.tv_product_name);
            mTvHarga = itemView.findViewById(R.id.tv_product_price);
            mTvDeskripsi = itemView.findViewById(R.id.tv_product_deskripsi);
            mTvCount = itemView.findViewById(R.id.tv_count);
            btnMin = itemView.findViewById(R.id.btn_min);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            card = itemView.findViewById(R.id.card);
        }
    }
}
