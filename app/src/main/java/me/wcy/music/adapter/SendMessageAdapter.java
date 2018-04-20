package me.wcy.music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.model.Message;

public class SendMessageAdapter extends RecyclerView.Adapter<SendMessageAdapter.SendMessageHolder> {

    private List<Message> mData;
    private Context mContext;

    private static final int MY = 1;//我所发送的消息
    private static final int OTHER = 2;//好友所发送的信息

    public SendMessageAdapter(List<Message> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public SendMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (MY == viewType) {
            view = LayoutInflater.from(mContext).inflate(R.layout.right_message_item, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.left_message_item, parent, false);
        }
        SendMessageHolder holder = new SendMessageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SendMessageHolder holder, int position) {
        holder.message.setText(mData.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    class SendMessageHolder extends RecyclerView.ViewHolder {

        TextView message;

        public SendMessageHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
        }
    }
}
