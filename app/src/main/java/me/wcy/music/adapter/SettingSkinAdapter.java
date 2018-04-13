package me.wcy.music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.wcy.music.R;
import me.wcy.music.adapter.IInterface.IOnItemClick;

public class SettingSkinAdapter extends RecyclerView.Adapter<SettingSkinAdapter.ColorListHolder> {

    private Context mContext;
    private Integer[] mColorList;
    private IOnItemClick mIOnItemClick;

    public SettingSkinAdapter(Context mContext, Integer[] mColorList) {
        this.mContext = mContext;
        this.mColorList = mColorList;
    }

    @Override
    public ColorListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.setting_skin_item, parent, false);

        return new ColorListHolder(view);
    }

    @Override
    public void onBindViewHolder(ColorListHolder holder, final int position) {
        holder.ll_item.setBackgroundColor(mContext.getResources().getColor(mColorList[position]));

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIOnItemClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mColorList.length;
    }

    public void setOnItemClick(IOnItemClick iOnItemClick) {
        this.mIOnItemClick = iOnItemClick;
    }

    class ColorListHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_item;

        public ColorListHolder(View itemView) {
            super(itemView);
            ll_item = itemView.findViewById(R.id.ll_item);
        }
    }

}
