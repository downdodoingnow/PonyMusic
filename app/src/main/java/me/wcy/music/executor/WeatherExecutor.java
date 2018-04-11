package me.wcy.music.executor;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;

import java.util.Calendar;

import me.wcy.music.R;
import me.wcy.music.activity.ForcastWeatherActivity;
import me.wcy.music.application.AppCache;
import me.wcy.music.utils.binding.Bind;
import me.wcy.music.utils.binding.ViewBinder;

/**
 * 更新天气
 * Created by wcy on 2016/1/17.
 * <p>
 * 天气现象表
 * <p>
 * 晴
 * 多云
 * 阴
 * 阵雨
 * 雷阵雨
 * 雷阵雨并伴有冰雹
 * 雨夹雪
 * 小雨
 * 中雨
 * 大雨
 * 暴雨
 * 大暴雨
 * 特大暴雨
 * 阵雪
 * 小雪
 * 中雪
 * 大雪
 * 暴雪
 * 雾
 * 冻雨
 * 沙尘暴
 * 小雨-中雨
 * 中雨-大雨
 * 大雨-暴雨
 * 暴雨-大暴雨
 * 大暴雨-特大暴雨
 * 小雪-中雪
 * 中雪-大雪
 * 大雪-暴雪
 * 浮尘
 * 扬沙
 * 强沙尘暴
 * 飑
 * 龙卷风
 * 弱高吹雪
 * 轻霾
 * 霾
 */
public class WeatherExecutor implements IExecutor, WeatherSearch.OnWeatherSearchListener, AMapLocationListener, View.OnClickListener {
    private static final String TAG = "WeatherExecutor";
    private Context mContext;
    private Context mActivity;
    @Bind(R.id.ll_weather)
    private LinearLayout llWeather;
    @Bind(R.id.ll_refresh)
    private LinearLayout ll_refresh;
    @Bind(R.id.iv_weather_icon)
    private ImageView ivIcon;
    @Bind(R.id.tv_weather_temp)
    private TextView tvTemp;
    @Bind(R.id.tv_weather_city)
    private TextView tvCity;
    @Bind(R.id.tv_weather_wind)
    private TextView tvWind;

    private String mLocation;

    public WeatherExecutor(Context context, View navigationHeader) {
        mContext = context.getApplicationContext();
        this.mActivity = context;
        ViewBinder.bind(this, navigationHeader);
        init();
    }

    public void init() {
        llWeather.setOnClickListener(this);
        ll_refresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_weather:
                Intent intent = new Intent(mActivity, ForcastWeatherActivity.class);
                intent.putExtra("location", mLocation);
                mActivity.startActivity(intent);
                break;
            case R.id.ll_refresh:
                break;
            default:
                break;
        }
    }

    @Override
    public void execute() {
//        LocalWeatherLive aMapLocalWeatherLive = AppCache.get().getAMapLocalWeatherLive();
//        if (aMapLocalWeatherLive != null) {
//            updateView(aMapLocalWeatherLive);
//            release();
//        } else {
//            LocationManagerProxy mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);
//            mLocationManagerProxy.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, this);

        //获取位置信息
        AMapLocationClient locationClient = new AMapLocationClient(mContext);
        locationClient.setLocationListener(this);
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        locationOption.setNeedAddress(true);
        //只定位一次
        locationOption.setOnceLocation(true);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
//        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mLocation = aMapLocation.getCity();
        WeatherSearchQuery mquery = new WeatherSearchQuery(mLocation, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        WeatherSearch mweathersearch = new WeatherSearch(mActivity);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (null != localWeatherLiveResult) {
            LocalWeatherLive weatherLive = localWeatherLiveResult.getLiveResult();
            AppCache.get().setAMapLocalWeatherLive(weatherLive);
            updateView(weatherLive);
        } else {
            llWeather.setVisibility(View.INVISIBLE);
            ll_refresh.setVisibility(View.VISIBLE);
            Log.e(TAG, "获取天气预报失败");
        }
        release();
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }
//    @Override
//    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
//        if (aMapLocalWeatherLive != null && aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
//            AppCache.get().setAMapLocalWeatherLive(aMapLocalWeatherLive);
//            updateView(aMapLocalWeatherLive);
//        } else {
//            llWeather.setVisibility(View.INVISIBLE);
//            ll_refresh.setVisibility(View.VISIBLE);
//            Log.e(TAG, "获取天气预报失败  " + aMapLocalWeatherLive.getAMapException().getErrorMessage());
//        }
//        release();
//    }
//
//    @Override
//    public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {
//    }

    private void updateView(LocalWeatherLive aMapLocalWeatherLive) {
        llWeather.setVisibility(View.VISIBLE);
        ll_refresh.setVisibility(View.INVISIBLE);

        ivIcon.setImageResource(getWeatherIcon(aMapLocalWeatherLive.getWeather()));
        tvTemp.setText(mContext.getString(R.string.weather_temp, aMapLocalWeatherLive.getTemperature()));
        tvCity.setText(aMapLocalWeatherLive.getCity());
        tvWind.setText(mContext.getString(R.string.weather_wind, aMapLocalWeatherLive.getWindDirection(),
                aMapLocalWeatherLive.getWindPower(), aMapLocalWeatherLive.getHumidity()));
    }

    public static int getWeatherIcon(String weather) {
        if (TextUtils.isEmpty(weather)) {
            return R.drawable.ic_weather_sunny;
        }

        if (weather.contains("-")) {
            weather = weather.substring(0, weather.indexOf("-"));
        }
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int resId;
        if (weather.contains("晴")) {
            if (hour >= 7 && hour < 19) {
                resId = R.drawable.ic_weather_sunny;
            } else {
                resId = R.drawable.ic_weather_sunny_night;
            }
        } else if (weather.contains("多云")) {
            if (hour >= 7 && hour < 19) {
                resId = R.drawable.ic_weather_cloudy;
            } else {
                resId = R.drawable.ic_weather_cloudy_night;
            }
        } else if (weather.contains("阴")) {
            resId = R.drawable.ic_weather_overcast;
        } else if (weather.contains("雷阵雨")) {
            resId = R.drawable.ic_weather_thunderstorm;
        } else if (weather.contains("雨夹雪")) {
            resId = R.drawable.ic_weather_sleet;
        } else if (weather.contains("雨")) {
            resId = R.drawable.ic_weather_rain;
        } else if (weather.contains("雪")) {
            resId = R.drawable.ic_weather_snow;
        } else if (weather.contains("雾") || weather.contains("霾")) {
            resId = R.drawable.ic_weather_foggy;
        } else if (weather.contains("风") || weather.contains("飑")) {
            resId = R.drawable.ic_weather_typhoon;
        } else if (weather.contains("沙") || weather.contains("尘")) {
            resId = R.drawable.ic_weather_sandstorm;
        } else {
            resId = R.drawable.ic_weather_cloudy;
        }
        return resId;
    }

    private void release() {
        mContext = null;
        llWeather = null;
        ivIcon = null;
        tvTemp = null;
        tvCity = null;
        tvWind = null;
    }

    @Override
    public void onPrepare() {
    }

    @Override
    public void onExecuteSuccess(Object o) {
    }

    @Override
    public void onExecuteFail(Exception e) {
    }
}
