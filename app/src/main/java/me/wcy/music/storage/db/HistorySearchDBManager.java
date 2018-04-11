package me.wcy.music.storage.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import me.wcy.music.storage.db.greendao.DaoMaster;
import me.wcy.music.storage.db.greendao.DaoSession;
import me.wcy.music.storage.db.greendao.HistorySearchDao;

public class HistorySearchDBManager {
    private static final String DB_NAME = "database.db";
    private HistorySearchDao mHistorySearchDao;

    private static HistorySearchDBManager mHistorySearchDBManager = new HistorySearchDBManager();

    public static HistorySearchDBManager getInstance() {
        if (null == mHistorySearchDBManager) {
            mHistorySearchDBManager = new HistorySearchDBManager();
        }
        return mHistorySearchDBManager;
    }

    public void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        mHistorySearchDao = daoSession.getHistorySearchDao();
    }

    public HistorySearchDao getmHistorySearchDao() {
        return mHistorySearchDao;
    }
}
