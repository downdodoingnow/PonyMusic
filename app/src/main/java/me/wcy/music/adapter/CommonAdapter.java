package me.wcy.music.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.activity.SettingInfoActivity;
import me.wcy.music.adapter.IInterface.IOnItemClick;
import me.wcy.music.adapter.IInterface.OnPraiseClick;
import me.wcy.music.model.Common;
import me.wcy.music.utils.CircleImageView;
import me.wcy.music.utils.ToastUtils;

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.Holder> {

    private Context mContext;
    private List<Common> mData;
    private OnPraiseClick onPraiseClick;
    private View view;

    public CommonAdapter(Context mContext, List<Common> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public void setOnPraiseClick(OnPraiseClick onPraiseClick) {
        this.onPraiseClick = onPraiseClick;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.common_item, parent, false);
        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        Common common = mData.get(position);
        holder.username.setText(common.getUsername());
        holder.common.setText(common.getContent());
        holder.time.setText(common.getTime());
        holder.praisenum.setText(common.getPraiseNum() + "");
        if (0 == common.getPraiseType()) {
            holder.praiseImg.setImageResource(R.drawable.ic_not_praise);
        } else {
            holder.praiseImg.setImageResource(R.drawable.ic_praise);
        }
        setClick(holder, position);
    }

    private void setClick(Holder holder, int position) {
        holder.headPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SettingInfoActivity.class);
                intent.putExtra("userID", mData.get(position).getUserID());
                intent.putExtra("userName", mData.get(position).getUsername());
                mContext.startActivity(intent);
            }
        });
        holder.praiseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPraiseClick.onPraiseClick(holder.praiseImg, holder.praisenum, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        CircleImageView headPhoto;
        TextView username;
        TextView time;
        TextView praisenum;
        TextView common;

        ImageView praiseImg;

        public Holder(View itemView) {
            super(itemView);

            headPhoto = itemView.findViewById(R.id.head_photo);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.time);
            praisenum = itemView.findViewById(R.id.praisenum);
            common = itemView.findViewById(R.id.common);

            praiseImg = itemView.findViewById(R.id.praised_img);
        }
    }

}
