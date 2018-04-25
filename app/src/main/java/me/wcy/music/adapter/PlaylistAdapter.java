package me.wcy.music.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.IInterface.OnMoreClickListener;
import me.wcy.music.model.Music;
import me.wcy.music.model.OnlineMusic;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.binding.Bind;
import me.wcy.music.utils.binding.ViewBinder;

/**
 * 本地音乐列表适配器
 */
public class PlaylistAdapter extends BaseAdapter {
    private List<Music> musicList;
    private List<OnlineMusic> mOnlineMisicList;
    private OnMoreClickListener listener;
    private boolean isPlaylist;
    private String type = "";

    public PlaylistAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }

    public PlaylistAdapter(List<OnlineMusic> mOnlineMisicList, String type) {
        this.mOnlineMisicList = mOnlineMisicList;
        this.type = type;
    }

    public void setIsPlaylist(boolean isPlaylist) {
        this.isPlaylist = isPlaylist;
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        if (type.equals("recommoned")) {
            return mOnlineMisicList.size();
        } else {
            return musicList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (type.equals("recommoned")) {
            return mOnlineMisicList.get(position);
        } else {
            return musicList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_music, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.vPlaying.setVisibility((isPlaylist && position == AudioPlayer.get().getPlayPosition()) ? View.VISIBLE : View.INVISIBLE);
        if (type.equals("recommoned")) {
            setOnlineData(holder, position, parent);
        } else {
            setLocalData(holder, position);
        }
        holder.ivMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreClick(position);
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    public void setOnlineData(ViewHolder holder, int position, ViewGroup parent) {
        OnlineMusic onlineMusic = mOnlineMisicList.get(position);
        Glide.with(parent.getContext())
                .load(onlineMusic.getPic_small())
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .into(holder.ivCover);
        holder.tvTitle.setText(onlineMusic.getTitle());
        String artist = FileUtils.getArtistAndAlbum(onlineMusic.getArtist_name(), onlineMusic.getAlbum_title());
        holder.tvArtist.setText(artist);
    }

    public void setLocalData(ViewHolder holder, int position) {
        Music music = musicList.get(position);

        Bitmap cover = CoverLoader.get().loadThumb(music);
        holder.ivCover.setImageBitmap(cover);
        holder.tvTitle.setText(music.getTitle());
        String artist = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        holder.tvArtist.setText(artist);
    }

    private boolean isShowDivider(int position) {
        if (type.equals("recommoned")) {
            return position != mOnlineMisicList.size() - 1;
        } else {
            return position != musicList.size() - 1;
        }

    }

    private static class ViewHolder {
        @Bind(R.id.v_playing)
        private View vPlaying;
        @Bind(R.id.iv_cover)
        private ImageView ivCover;
        @Bind(R.id.tv_title)
        private TextView tvTitle;
        @Bind(R.id.tv_artist)
        private TextView tvArtist;
        @Bind(R.id.iv_more)
        private ImageView ivMore;
        @Bind(R.id.v_divider)
        private View vDivider;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
