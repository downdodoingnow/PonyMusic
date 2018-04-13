package me.wcy.music.application;

import android.app.Application;
import android.content.Intent;

import me.wcy.music.R;
import me.wcy.music.service.PlayService;
import me.wcy.music.storage.db.DBManager;
import me.wcy.music.storage.db.HistorySearchDBManager;

/**
 * 自定义Application
 */
public class MusicApplication extends Application {

    public static Integer[] mStyleList = new Integer[]{
            R.style.AppBaseThemeBlack, R.style.AppBaseTheme, R.style.AppBaseThemePurple, R.style.AppBaseThemeGrey,
            R.style.AppBaseThemePink, R.style.AppBaseThemeRoyalBlue, R.style.AppBaseThemeDeepSkyBlue, R.style.AppBaseThemeDeepLightGreen,
            R.style.AppBaseThemeDeepLightOrange, R.style.AppBaseThemeDarkSlateBlue
    };

    @Override
    public void onCreate() {
        super.onCreate();

        AppCache.get().init(this);
        ForegroundObserver.init(this);
        DBManager.get().init(this);
        HistorySearchDBManager.getInstance().init(this);

        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }
}
