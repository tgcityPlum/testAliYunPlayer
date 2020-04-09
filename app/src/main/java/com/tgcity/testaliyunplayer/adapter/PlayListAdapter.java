package com.tgcity.testaliyunplayer.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tgcity.testaliyunplayer.R;
import com.tgcity.testaliyunplayer.bean.PlayData;

import java.util.List;

/**
 * @author TGCity
 * @description 视频列表适配器
 */
public class PlayListAdapter extends RecyclerView.Adapter {
    private List<PlayData> dataList;
    private OnItemClickListener itemClickListener;

    public PlayListAdapter(List<PlayData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_play, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;

        PlayData data = dataList.get(position);
        holder.tvTitle.setText(data.getTitle());

        if (data.isChoose()){
            holder.tvTitle.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.color_ffffff));
        }else {
            holder.tvTitle.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.color_999999));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
