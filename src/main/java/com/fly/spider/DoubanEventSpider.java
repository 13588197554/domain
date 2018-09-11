package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.dao.CityDao;
import com.fly.dao.DistrictDao;
import com.fly.dao.EventDao;
import com.fly.dao.UserDao;
import com.fly.pojo.DoubanEvent;
import com.fly.pojo.DoubanUser;
import com.fly.util.Arr;
import com.fly.util.LogUtil;
import com.fly.util.Util;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author david
 * @date 19/07/18 20:24
 */
@Component
public class DoubanEventSpider {

    @Autowired
    private EventDao ed;
    @Autowired
    private CityDao cd;
    @Autowired
    private UserDao ud;
    @Autowired
    private DistrictDao dd;

    private static String baseUrl = "https://api.douban.com/v2/event/list";
    private static int count = 20;
    private static String TYPE = "all";
    private static String DAY_TYPE = "future";
    private static String reg1 = "<(.*?)>";
    private static String cityName = "上海";
    private static Integer locId = 108296;

//    @Async
//    @Scheduled(fixedDelay = 24 * 3600 * 1000)
    public void eventSpider() {
        String url0 = baseUrl + "?loc=" + locId + "&day_type=" + DAY_TYPE + "&type=" + TYPE;
        int start = 0;
        for (;;) {
            try {
                long startTime = Util.getCurrentTimestamp();
                String url = url0 + "&start=" + start * count;
                System.out.println("-- processing url: " + url + ", -- process time: " + Util.getCurrentFormatTime());
                Connection connection = Jsoup.connect(url).ignoreContentType(true);
                connection.userAgent("Mozilla/2.0 (compatible; Ask Jeeves/Teoma; +http://sp.ask.com/docs/about/tech_crawling.html)");
                Connection.Response res = connection.execute();
                String body = res.body();
                JSONObject jo = JSON.parseObject(body);

                String eventJson = jo.getString("events");
                List<DoubanEvent> events = JSON.parseArray(eventJson, DoubanEvent.class);
                if (events.size() <= 0) {
                    // no events
                    System.out.println("job has finished!");
                    break;
                }

                // event and user
                for (DoubanEvent event : events) {
                    DoubanUser owner = event.getOwner();
                    owner.setCreatTime(Util.getCurrentFormatTime());
                    owner.setUpdateTime(Util.getCurrentFormatTime());
                    ud.save(owner);
                    ed.save(event);
                }

                long endTime = Util.getCurrentTimestamp();
                if ((endTime - startTime) <= 30 * 1000) {
                    System.out.println("normally sleeping");
                    Util.getRandomSleep(28, 32);
                }
                Util.getRandomSleep(1, 3);
                start += 20;
            } catch (HttpStatusException he) {
                LogUtil.info(DoubanEventSpider.class, "eventSpider", he);
                he.printStackTrace();
                switch (he.getStatusCode()) {
                    case 400:
                        Util.getRandomSleep(15 * 60); // 15 minutes
                    case 404:
                        continue;
                    case 403:
                        Util.getRandomSleep(3 * 3600);
                        continue;
                }
            } catch (Exception e) {
                LogUtil.info(DoubanEventSpider.class, "eventSpider", e);
                e.printStackTrace();
            }
        }
    }

    private void handleInfo(DoubanEvent event, JSONObject jo) {
        event.setId(Arr.get(jo, "id"));
        event.setAdaptUrl(Arr.get(jo, "adapt_url"));
        event.setAddress(Arr.get(jo, "address"));
        event.setAlbum(Arr.get(jo, "album"));
        event.setAlt(Arr.get(jo, "alt"));
        event.setBeginTime(Arr.get(jo, "begin_time"));
        event.setEndTime(Arr.get(jo, "end_time"));
        event.setCanInvite(Arr.get(jo, "can_invite"));
        event.setCategory(Arr.get(jo, "category"));
        event.setCategoryName(Arr.get(jo, "category_name"));
        event.setContent(Arr.get(jo, "content"));
        event.setCreateTime(Arr.get(jo, "create_time"));
        event.setFeeStr(Arr.get(jo, "fee_str"));
        event.setGeo(Arr.get(jo, "geo"));
        event.setHasTicket(Arr.get(jo, "has_ticket"));
        event.setImageHlrange(Arr.get(jo, "image_hlrange"));
        event.setImageLmobile(Arr.get(jo, "image_lmobile"));
        event.setLocName(Arr.get(jo, "loc_name"));
        event.setParticipantCount(Arr.get(jo, "participant_count"));
        event.setPriceRange(Arr.get(jo, "price_range"));
        event.setImage(Arr.get(jo, "images"));
        event.setTags(Arr.get(jo, "tags"));
        event.setLabel(Arr.get(jo, "label"));
        event.setLocId(Arr.get(jo, "loc_id"));
        event.setTimeStr(Arr.get(jo, "time_str"));
        event.setTitle(Arr.get(jo, "title"));
        event.setUpdateTime(Arr.get(jo, "update_time"));
        event.setUri(Arr.get(jo, "url"));
        event.setUrl(Arr.get(jo, "uri"));
        event.setWisherCount(Integer.valueOf(Arr.get(jo, "wisher_count")));
    }
}
