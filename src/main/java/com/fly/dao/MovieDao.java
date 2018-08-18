package com.fly.dao;

import com.fly.pojo.DoubanMovie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieDao extends JpaRepository<DoubanMovie, String> {
}
