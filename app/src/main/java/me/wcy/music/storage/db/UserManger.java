package me.wcy.music.storage.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import me.wcy.music.storage.db.greendao.DaoMaster;
import me.wcy.music.storage.db.greendao.DaoSession;
import me.wcy.music.storage.db.greendao.UserDao;

public class UserManger {
    public static final String DB_NAME = "database.db";
    private UserDao mUserDao;

    private static UserManger mUserManger = new UserManger();

    public static UserManger getInstance() {
        if (null == mUserManger) {
            mUserManger = new UserManger();
        }

        return mUserManger;
    }

    public void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();

        mUserDao = daoSession.getUserDao();
    }

    public UserDao getmUserDao() {
        return mUserDao;
    }
}
