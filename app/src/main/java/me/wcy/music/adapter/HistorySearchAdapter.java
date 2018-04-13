package me.wcy.music.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.IInterface.IOnItemClick;
import me.wcy.music.model.HistorySearch;
import me.wcy.music.storage.db.HistorySearchDBManager;
import me.wcy.music.utils.AlertDialogUtils;

public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.HistoryHolder> {

    private List<HistorySearch> mData;
    private Context mContext;
    private LinearLayout searchHistoryll;
    private IOnItemClick mOnItemClick;

    public HistorySearchAdapter(Context mContext, List<HistorySearch> mData, LinearLayout searchHistoryll) {
        this.mContext = mContext;
        this.mData = mData;
        this.searchHistoryll = searchHistoryll;
    }

    public void setOnItemClick(IOnItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_search_item, parent, false);
        HistoryHolder historyHolder = new HistoryHolder(view);
        return historyHolder;
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, final int position) {
        holder.name.setText(mData.get(position).getName());
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialogUtils(mContext, new AlertDialogUtils.IConfirmCallBack() {
                    @Override
                    public void operate(DialogInterface dialog) {
                        HistorySearchDBManager.getInstance().getmHistorySearchDao().delete(mData.get(position));
                        mData.remove(position);
                        HistorySearchAdapter.this.notifyDataSetChanged();

                        if (0 == mData.size()) {
                            searchHistoryll.setVisibility(View.GONE);
                        }
                    }
                }).build(R.string.is_delete);
            }
        });

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class HistoryHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView deleteItem;
        RelativeLayout mItemView;

        public HistoryHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            deleteItem = itemView.findViewById(R.id.delete_item);
            mItemView = itemView.findViewById(R.id.item_view);
        }
    }
}
