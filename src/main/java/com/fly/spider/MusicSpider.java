package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.config.SystemConfig;
import com.fly.dao.TagObjectDao;
import com.fly.enums.StatusEnum;
import com.fly.pojo.DoubanMusic;
import com.fly.pojo.TagObject;
import com.fly.util.Arr;
import com.fly.util.LogUtil;
import com.fly.util.RedisUtil;
import com.fly.util.Util;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Component
public class MusicSpider {

    @Autowired
    private TagObjectDao tod;
    @Autowired
    private RedisUtil jedis;

    private static Properties prop = SystemConfig.global;
    private static String MUSIC_BASE_URL = Arr.get(prop, "MUSIC_BASE_URL", String.class);
    private static Integer START = 0;
    private static String TAG_NAME = "";
    private static String TAG_ID = "";
    private static String MUSIC_TAG = "MUSIC_TAG";
    private static String TAG_MAP = "DOUBAN_TAG_MAP";

    public void musicSpider() throws InterruptedException {
        while (true) {
            Long remain = jedis.llen(MUSIC_TAG);
            System.out.println("There are remain tags : " + remain + " in redis!");
            if (remain == 0) {
                break;
            }

            while (true) {
                TAG_NAME = jedis.lpop(MUSIC_TAG);
                TAG_ID = jedis.hget(TAG_MAP, TAG_NAME);
                String url = MUSIC_BASE_URL + TAG_NAME + "&start=" + START;
                System.out.println("processing url : " + url + ", process time : " + Util.getCurrentFormatTime());
                try {
                    Connection.Response res = Jsoup.connect(url)
                            .ignoreContentType(true)
                            .userAgent("GurujiBot/1.0 (+http://www.guruji.com/WebmasterFAQ.html)")
                            .execute();
                    String body = res.body();
                    List<JSONObject> joArr = this.getJsonArray(body);
                    for (JSONObject jo : joArr) {
                        DoubanMusic music = this.musicUnit(jo);
                        this.musicUnit(jo);
                    }
                } catch (HttpStatusException hse) {
                    LogUtil.info(MusicSpider.class, "musicSpider", hse);
                    if (400 == hse.getStatusCode()) {
                        Util.getRandomSleep(15 * 60);
                        jedis.rpush(MUSIC_TAG, TAG_NAME);
                        continue;
                    }

                    if (404 == hse.getStatusCode()) {
                        Util.getRandomSleep(30, 45);
                        continue;
                    }

                    if (403 == hse.getStatusCode()) {
                        jedis.rpush(MUSIC_TAG, TAG_NAME);
                        Util.getRandomSleep(3 * 3600);
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DoubanMusic musicUnit(JSONObject jo) {
        DoubanMusic music = new DoubanMusic();
        music.setAlt(Arr.get(jo, "alt", "", String.class));
        music.setAltTitle(Arr.get(jo, "alt_title", "", String.class));
        music.setAuthor(Arr.get(jo, "author", "", String.class));
        music.setStars(Arr.get(jo, "stars", "", String.class));
        music.setImage(Arr.get(jo, "image", "", String.class));
        music.setMobileLink(Arr.get(jo, "mobile_link", "", String.class));
        music.setId(Integer.valueOf(Arr.get(jo, "id", String.class)));

        String attrJson = jo.getString("attrs");
        JSONObject ajo = JSON.parseObject(attrJson);
        music.setPublisher(Arr.get(ajo, "publisher", "", String.class));
        music.setPubdate(Arr.get(ajo, "pubdate", "", String.class));
        music.setSinger(Arr.get(ajo, "singer", "", String.class));
        music.setAka(Arr.get(ajo, "title", "", String.class));
        music.setTracks(Arr.get(ajo, "tracks", "", String.class));
        music.setDiscs(Arr.get(ajo, "discs", "", String.class));
        music.setMedia(Arr.get(ajo, "media", "", String.class));
        music.setVersion(Arr.get(ajo, "version", "", String.class));
        return music;
    }

    private void tagUnit(JSONObject jo, DoubanMusic music) {
        String tagJson = Arr.get(jo, "tags");
        JSONObject tjo = JSON.parseObject(tagJson);
        List<TagObject> tags = tod.findByFkAndTagId(String.valueOf(music.getId()), TAG_ID);
        if (tags.size() == 0) {
            TagObject to = new TagObject();
            to.setFk(String.valueOf(music.getId()));
            to.setTagId(TAG_ID);
            to.setExtra(tjo.toJSONString());
            to.setCreateTime(Util.getCurrentFormatTime());
            to.setUpdateTime(Util.getCurrentFormatTime());
            to.setStatus(StatusEnum.ACTIVE.getName());
            tod.save(to);
        }
        TagObject to = new TagObject();
    }

    private List<JSONObject> getJsonArray(String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String subJson = Arr.get(jsonObject, "musics");
        List<JSONObject> array = JSON.parseArray(subJson, JSONObject.class);
        return array;
    }

}
