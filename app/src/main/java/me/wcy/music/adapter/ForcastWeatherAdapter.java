package me.wcy.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.weather.LocalDayWeatherForecast;

import java.util.List;

import me.wcy.music.R;

public class ForcastWeatherAdapter extends BaseAdapter {
    private List<LocalDayWeatherForecast> mData;
    private Context mContext;

    public ForcastWeatherAdapter(Context mContext, List<LocalDayWeatherForecast> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mData.remove(0);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (null == convertView) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.forcast_weather_item, null);

            holder.mTime = convertView.findViewById(R.id.time);
            holder.mTemperature = convertView.findViewById(R.id.temperature);
            holder.mWeather = convertView.findViewById(R.id.weather);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.mTime.setText(mData.get(position).getDate());
        holder.mWeather.setText(mData.get(position).getDayWeather());
        holder.mTemperature.setText(mContext.getString(R.string.weather_temp, mData.get(position).getDayTemp()) + "~" + mContext.getString(R.string.weather_temp, mData.get(position).getNightTemp()));

        return convertView;
    }

    static class Holder {
        TextView mTime;
        TextView mWeather;
        TextView mTemperature;
    }

    public void getWeek() {

    }
}
