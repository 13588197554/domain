package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fly.config.SystemConfig;
import com.fly.dao.MusicDao;
import com.fly.dao.TagDao;
import com.fly.dao.TagObjectDao;
import com.fly.enums.StatusEnum;
import com.fly.pojo.DoubanMusic;
import com.fly.pojo.FlyTag;
import com.fly.pojo.TagObject;
import com.fly.util.Arr;
import com.fly.util.LogUtil;
import com.fly.util.RedisUtil;
import com.fly.util.Util;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Component
public class MusicSpider {

    @Autowired
    private MusicDao md;
    @Autowired
    private TagDao td;
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
    private static String MUSIC_TAG_MAP = "MUSIC_TAG_MAP";

    public void musicSpider() {
        this.initRedis();

        while (true) {
            Long remain = jedis.llen(MUSIC_TAG);
            if (remain == 0) {
                break;
            }

            START = 0;
            TAG_NAME = jedis.lpop(MUSIC_TAG);
            TAG_ID = jedis.hget(MUSIC_TAG_MAP, TAG_NAME);
            while (true) {
                String url = MUSIC_BASE_URL + TAG_NAME + "&start=" + START;
                System.out.println("processing url : " + url + ", process time : " + Util.getCurrentFormatTime());
                try {
                    Connection.Response res = Jsoup.connect(url)
                            .ignoreContentType(true)
                            .userAgent("GurujiBot/1.0 (+http://www.guruji.com/WebmasterFAQ.html)")
                            .execute();
                    String body = res.body();
                    List<JSONObject> joArr = this.getJsonArray(body);

                    if (joArr.size() == 0) {
                        break;
                    }

                    for (JSONObject jo : joArr) {
                        this.spider(jo);
                    }

                    START += 20;
                } catch (HttpStatusException hse) {
                    LogUtil.info(MusicSpider.class, "musicSpider", hse);
                    if (400 == hse.getStatusCode()) {
                        Util.getRandomSleep(15 * 60);
                        jedis.rpush(MUSIC_TAG, TAG_NAME);
                        continue;
                    }

                    if (404 == hse.getStatusCode()) {
                        START++;
                        Util.getRandomSleep(30, 45);
                        continue;
                    }

                    if (403 == hse.getStatusCode()) {
                        jedis.rpush(MUSIC_TAG, TAG_NAME);
                        Util.getRandomSleep(3 * 3600);
                        continue;
                    }
                } catch (IOException e) {
                    LogUtil.info(MusicSpider.class, "musicSpider", e);
                    e.printStackTrace();
                } catch (Exception e) {
                    LogUtil.info(MusicSpider.class, "musicSpider", e);
                    e.printStackTrace();
                } finally {
                    System.out.println("--- normally sleeping: " + Util.getCurrentFormatTime());
                    System.out.println("=== There are remain tags : " + remain + " in redis! ===");
                    Util.getRandomSleep(35, 45);
                }
            }
        }
    }

    public void musicTypeSpider() {
        try {
            String url = "https://music.douban.com/tag";
            String type = "DOUBAN_MUSIC";
            Document document = Jsoup.connect(url)
                    .userAgent("findlinks/1.1.1 (+http://wortschatz.uni-leipzig.de/findlinks/)")
                    .get();
            Elements divTags = document.select("div.mod");
            for (Element divTag : divTags) {
                String pname = divTag.attr("id");
                FlyTag parent = new FlyTag();
                this.saveTag(parent, pname, "0");

                Elements trTags = divTag.select("div.bd table.tagCol tr");
                for (Element trTag : trTags) {
                    Elements tdTags = trTag.select("td");
                    for (Element tdTag : tdTags) {
                        String cname = tdTag.select("a").text();
                        FlyTag child = new FlyTag();
                        this.saveTag(child, cname, parent.getId());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Transactional
    public void spider(JSONObject jo) {
        DoubanMusic music = this.musicUnit(jo);
        this.tagUnit(jo, music);
    }

    private DoubanMusic musicUnit(JSONObject jo) {
        DoubanMusic music = new DoubanMusic();
        music.setId(Integer.valueOf(Arr.get(jo, "id", String.class)));

        Optional<DoubanMusic> op = md.findById(Integer.valueOf(Arr.get(jo, "id", String.class)));
        if (op.isPresent()) {
            return op.get();
        }

        String ratingJson = Arr.get(jo, "rating");
        JSONObject rjo = JSON.parseObject(ratingJson);
        music.setStars(Arr.get(rjo, "average", "", String.class));
        music.setAlt(Arr.get(jo, "alt", "", String.class));
        music.setAltTitle(Arr.get(jo, "alt_title", "", String.class));
        music.setAuthor(Arr.get(jo, "author", "[]"));
        music.setImage(Arr.get(jo, "image", "", String.class));
        music.setMobileLink(Arr.get(jo, "mobile_link", "", String.class));
        music.setTitle(Arr.get(jo, "title", "", String.class));
        music.setExtra(jo.toJSONString());

        String attrJson = jo.getString("attrs");
        JSONObject ajo = JSON.parseObject(attrJson);
        music.setPublisher(Arr.get(ajo, "publisher", "[]"));
        music.setPubdate(Arr.get(ajo, "pubdate", "[]"));
        music.setSinger(Arr.get(ajo, "singer", "[]"));
        music.setAka(Arr.get(ajo, "title", "[]"));
        music.setTracks(Arr.get(ajo, "tracks", "[]"));
        music.setDiscs(Arr.get(ajo, "discs", "[]"));
        music.setMedia(Arr.get(ajo, "media", "[]"));
        music.setVersion(Arr.get(ajo, "version", "[]"));

        music.setCreateTime(Util.getCurrentFormatTime());
        music.setUpdateTime(Util.getCurrentFormatTime());
        md.save(music);
        return music;
    }

    private void tagUnit(JSONObject jo, DoubanMusic music) {
        String tagJson = Arr.get(jo, "tags");
        List<TagObject> tags = tod.findByFkAndTagId(String.valueOf(music.getId()), TAG_ID);
        if (tags.size() == 0) {
            TagObject to = new TagObject();
            to.setFk(String.valueOf(music.getId()));
            to.setTagId(TAG_ID);
            to.setExtra(tagJson);
            to.setCreateTime(Util.getCurrentFormatTime());
            to.setUpdateTime(Util.getCurrentFormatTime());
            to.setStatus(StatusEnum.ACTIVE.getName());
            tod.save(to);
        }
    }

    private List<JSONObject> getJsonArray(String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String subJson = Arr.get(jsonObject, "musics");
        List<JSONObject> array = JSON.parseArray(subJson, JSONObject.class);
        return array;
    }

    @Transactional
    public void saveTag(FlyTag tag, String name, String pid) {
        tag.setId(Util.getUUid());
        tag.setPid(pid);
        tag.setTagName(name);
        tag.setTagType("DOUBAN_MUSIC");
        tag.setStatus(StatusEnum.ACTIVE.getName());
        tag.setUpdateTime(Util.getCurrentSqlTimestamp());
        tag.setCreateTime(Util.getCurrentSqlTimestamp());
        td.save(tag);
    }

    private void initRedis() {
        if (!jedis.exists(MUSIC_TAG)) {
            List<String> names = td.findNameByTypeAndPid("DOUBAN_MUSIC");
            String[] nameArr = new String[]{};
            nameArr = names.toArray(nameArr);
            jedis.rpush(MUSIC_TAG, nameArr);

        }

        if (!jedis.exists(MUSIC_TAG_MAP)) {
            List<FlyTag> tags = td.findIdAndNameByType("DOUBAN_MUSIC");
            for (FlyTag tag : tags) {
                jedis.hset(MUSIC_TAG_MAP, tag.getTagName(), tag.getId());
            }
        }

    }

    private void temp(JSONArray arr) {

    }
}
