package me.wcy.music.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.IInterface.IOnItemClick;
import me.wcy.music.adapter.IInterface.OnMoreClickListener;
import me.wcy.music.model.User;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHandler> {

    private List<User> mData;
    private Context mContext;

    private IOnItemClick mOnItemClick;
    private OnMoreClickListener onMoreClickListener;

    public FriendAdapter(List<User> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public void setmOnItemClick(IOnItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.onMoreClickListener = onMoreClickListener;
    }

    @Override
    public FriendHandler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_item, parent, false);
        FriendHandler holder = new FriendHandler(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(FriendHandler holder, final int position) {
        User user = mData.get(position);
        holder.username.setText(user.getUserName());
        holder.signture.setText(user.getSignature());

        if (user.getSex().equals("男")) {
            holder.sex.setImageResource(R.drawable.ic_boy);
        } else {
            holder.sex.setImageResource(R.drawable.ic_girl);
        }

        holder.friend_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClick.onItemClick(position);
            }
        });

        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMoreClickListener.onMoreClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class FriendHandler extends RecyclerView.ViewHolder {

        TextView username;
        TextView signture;
        ImageView sex;
        ImageView ivMore;

        LinearLayout friend_ll;

        public FriendHandler(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            signture = itemView.findViewById(R.id.signature);
            sex = itemView.findViewById(R.id.sex);
            ivMore = itemView.findViewById(R.id.iv_more);

            friend_ll = itemView.findViewById(R.id.friend_ll);
        }
    }
}
