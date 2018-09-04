package com.fly;

import redis.clients.jedis.Jedis;

public class Main {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("118.25.37.27", 6379);
        jedis.auth("654328");
        jedis.rpush("DOUBAN_MUSIC", "UK");
    }
}
