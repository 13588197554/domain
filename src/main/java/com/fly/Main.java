package com.fly;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fly.util.Arr;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import redis.clients.jedis.Jedis;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Jedis jedis = new Jedis("118.25.37.27", 6379);
        jedis.auth("654328");
        jedis.hget("", "");
    }
}
