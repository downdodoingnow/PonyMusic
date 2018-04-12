package me.wcy.music.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.bumptech.glide.Glide;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.ForcastWeatherAdapter;
import me.wcy.music.application.AppCache;
import me.wcy.music.executor.WeatherExecutor;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.binding.Bind;

public class ForcastWeatherActivity extends BaseActivity implements WeatherSearch.OnWeatherSearchListener {
    private String mLocation;
    private static final String TAG = "ForcastWeatherActivity";

    @Bind(R.id.location)
    TextView mLocationText;
    @Bind(R.id.time)
    TextView mTimeText;
    @Bind(R.id.weather_img)
    ImageView mWreatherImg;
    @Bind(R.id.weather_txt)
    TextView mWeatherText;
    @Bind(R.id.temperature)
    TextView mTemperatureText;
    @Bind(R.id.wind_direction)
    TextView mWindDirection;
    @Bind(R.id.wind_num)
    TextView mWindNum;
    @Bind(R.id.moisture)
    TextView mMoisture;
    @Bind(R.id.forcast_time)
    TextView mForcastTime;
    @Bind(R.id.forcast)
    ListView mForcastList;

    @Bind(R.id.forcast_weather)
    LinearLayout mForcastWeather;

    private ForcastWeatherAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forcast_weather);
        init();
    }

    public void init() {
        Intent intent = getIntent();
        mLocation = intent.getStringExtra("location");

        mLocationText.setText(mLocation);

        initView();
        getWeatherData();
    }

    private void initView() {
        LocalWeatherLive aMapLocalWeatherLive = AppCache.get().getAMapLocalWeatherLive();
        //获取当日的天气数据
        if (null != aMapLocalWeatherLive) {
            String weather = aMapLocalWeatherLive.getWeather();
            mTimeText.setText(aMapLocalWeatherLive.getReportTime());
            mWreatherImg.setImageResource(WeatherExecutor.getWeatherIcon(weather));
            mWeatherText.setText(weather);
            mTemperatureText.setText(getString(R.string.weather_temp, aMapLocalWeatherLive.getTemperature()));
            mWindDirection.setText(aMapLocalWeatherLive.getWindDirection() + "风");
            mWindNum.setText(aMapLocalWeatherLive.getWindPower() + "级");
            mMoisture.setText(aMapLocalWeatherLive.getHumidity() + "%");
            if (Preferences.isNightMode()) {
                mForcastWeather.setBackgroundColor(getResources().getColor(R.color.grey_900));
            } else {
                setWeatherBack(weather);
            }
        }
    }

    private void setWeatherBack(String weather) {
        if (weather.contains("多云")) {
            mForcastWeather.setBackgroundResource(R.drawable.ic_cloudy);
        } else if (weather.contains("晴")) {
            mForcastWeather.setBackgroundResource(R.drawable.ic_sunny);
        } else if (weather.equals("雨")) {
            mForcastWeather.setBackgroundResource(R.drawable.ic_rain);
        } else if (weather.contains("雪")) {
            mForcastWeather.setBackgroundResource(R.drawable.ic_snow);
        } else {
            mForcastWeather.setBackgroundResource(R.drawable.ic_overcast);
        }
    }

    //开始获取天气数据
    public void getWeatherData() {
        WeatherSearchQuery mquery = new WeatherSearchQuery(mLocation, WeatherSearchQuery.WEATHER_TYPE_FORECAST);
        WeatherSearch mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        //异步搜索
        mweathersearch.searchWeatherAsyn();
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {

    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

        if (null != localWeatherForecastResult) {
            LocalWeatherForecast localWeatherForecast = localWeatherForecastResult.getForecastResult();
            mForcastTime.setText(localWeatherForecast.getReportTime());
            mAdapter = new ForcastWeatherAdapter(ForcastWeatherActivity.this, localWeatherForecast.getWeatherForecast());
            mForcastList.setAdapter(mAdapter);
        }
    }
}
