package com.fly;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fly.util.Arr;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Connection.Response res = Jsoup
                .connect("https://api.douban.com/v2/movie/search?tag=美国&start=100051")
                .ignoreContentType(true)
                .execute();
        String body = res.body();
        JSONObject jo = JSON.parseObject(body);
        String subjects = Arr.get(jo, "subjects");
        JSONArray objects = JSON.parseArray(subjects);
        System.out.println(objects.size());
    }
}
