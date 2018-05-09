package me.wcy.music.utils;

import android.util.Log;

import java.util.ArrayList;

import me.wcy.music.model.MusicUser;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class ReptilianUtils implements PageProcessor {

    private final static int RETRY_NUM = 10;
    private final static int time = 1000;
    public static ArrayList<MusicUser> mMusicUsers = new ArrayList<>();
    public static final String USERID = "若风";

    //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(RETRY_NUM).setSleepTime(time);

    /**
     * process 方法是webmagic爬虫的核心<br>
     * 编写抽取【待爬取目标链接】的逻辑代码在html中。
     */
    @Override
    public void process(Page page) {
        //1. 如果是用户列表页面 【入口页面】，将所有用户的详细页面的url放入target集合中。
        if (page.getUrl().regex("https://www\\.music\\.baidu\\.com/search\\?type=people").match()) {
            page.addTargetRequests(page.getHtml().xpath("//ul[@class='list users']/li/div/div[@class='body']/div[@class='line']").links().all());
        }
        //2. 如果是用户详细页面
        else {
            MusicUser user = new MusicUser();
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            String userID = page.getHtml().xpath("//div[@class='title-section ellipsis']/span[@class='userID']/text()").get();
            String songName = page.getHtml().xpath("//div[@class='title-section ellipsis']/span[@class='song_name']/@text").get();
            String singer = page.getHtml().xpath("//div[@class='item editable-group']/span[@class='singer']/@text']/@text").get();
            int count = Integer.parseInt(page.getHtml().xpath("//div[@class='profile-navbar clearfix']/a[2]/span[@class='num']/text()").get());
            //对数据进行过滤
            if (null != userID && null != songName && null != singer && 0 != count) {
                user.setUserID(userID);
                user.setSongName(songName);
                user.setSinger(singer);
                user.setCount(count);

                integrationData(user);
            }

            Log.i("LocalMusicFragment", "process: " + user.toString());
        }
    }

    /**
     * 如果其中存在某一个用户听歌的记录，那么就将获取到的歌曲次数添加到该记录上面并返回
     *
     * @param user
     */
    public void integrationData(MusicUser user) {
        MusicUser user1;
        for (int i = 0; i < mMusicUsers.size(); i++) {
            user1 = mMusicUsers.get(i);
            if (user.getSinger().equals(user1.getSinger()) && user.getSongName().equals(user1.getSongName()) && user.getUserID().equals(user1.getUserID())) {
                user1.setCount(user.getCount() + user1.getCount());
                return;
            }
        }
        if (user.getUserID().equals(USERID)) {
            mMusicUsers.add(0, user);
        } else {
            mMusicUsers.add(user);
        }
    }

    @Override
    public Site getSite() {
        return this.site;
    }
}
