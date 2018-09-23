package com.fly.job;

import com.fly.dao.BookReviewDao;
import com.fly.spider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MovieJob implements CommandLineRunner {

    @Autowired
    private BookReviewSpider brs;

    @Override
    public void run(String... args) {
        brs.start();
    }
}
