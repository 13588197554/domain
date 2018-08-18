package com.fly.config;

import com.fly.filter.CharacterFilter;
import com.fly.filter.CrossFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class Config {

    @Bean(name = "crossFilter")
    public Filter crossFilter() {
        return new CrossFilter();
    }

    @Bean(name = "characterFilter")
    public Filter characterFilter() {
        return new CharacterFilter();
    }

}
