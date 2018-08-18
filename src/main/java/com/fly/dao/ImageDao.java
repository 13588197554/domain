package com.fly.dao;

import com.fly.pojo.DoubanImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDao extends JpaRepository<DoubanImage, Integer> {

}
