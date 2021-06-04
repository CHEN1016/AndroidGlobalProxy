package com.chen.globalproxy;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ProxyViewModel proxyViewModel;

    List<Proxy> proxyList = new ArrayList<>();

    public void setProxyList(List<Proxy> proxyList) {
        this.proxyList = proxyList;
    }

    public MyAdapter(Context context, ProxyViewModel proxyViewModel) {
        this.context = context;
        this.proxyViewModel = proxyViewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cell_normal, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Proxy proxy = proxyList.get(position);
        holder.proxyTextView.setText(proxy.getHost());
        holder.itemView.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.recylervew_item_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.set_proxy:
                            proxy.setModifiedAt(new Date());
                            proxyViewModel.updateProxy(proxy);
                            String proxyStr = proxy.getHost() + ":" + proxyViewModel.getPortLiveData().getValue();
                            proxyViewModel.setProxyStrLiveData(proxyStr);
                            Toast.makeText(context, "当前代理为:" + proxyStr, Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.delete_proxy:
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("是否删除该代理")
                                    .setPositiveButton(R.string.ok, (dialog, which) -> proxyViewModel.deleteProxy(proxy))
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();

                    }
                    return false;
                }
            });
            // todo 弹出位置跟随点击位置
            popupMenu.setGravity(Gravity.END); // 居右显示
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return proxyList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView proxyTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            proxyTextView = itemView.findViewById(R.id.proxyTextView);
        }
    }
}
