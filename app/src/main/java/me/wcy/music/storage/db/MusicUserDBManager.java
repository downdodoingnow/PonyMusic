package me.wcy.music.storage.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import me.wcy.music.storage.db.greendao.DaoMaster;
import me.wcy.music.storage.db.greendao.DaoSession;
import me.wcy.music.storage.db.greendao.MusicUserDao;

public class MusicUserDBManager {
    private static final String DB_NAME = "database.db";
    private MusicUserDao mMusicUserDao;

    private static MusicUserDBManager mMusicUserDBManager = new MusicUserDBManager();

    public static MusicUserDBManager getInstance() {
        if (null == mMusicUserDBManager) {
            mMusicUserDBManager = new MusicUserDBManager();
        }
        return mMusicUserDBManager;
    }

    public void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        mMusicUserDao = daoSession.getMusicUserDao();
    }

    public MusicUserDao getmMusicUserDao() {
        return mMusicUserDao;
    }
}
