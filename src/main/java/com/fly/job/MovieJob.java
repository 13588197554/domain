package com.fly.job;

import com.fly.spider.BookInfoSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MovieJob implements CommandLineRunner {

    @Autowired
    private BookInfoSpider mis;

    @Override
    public void run(String... args) throws InterruptedException {
        mis.start();
    }
}
