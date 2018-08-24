package com.fly.dao;

import com.fly.pojo.DoubanImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageDao extends JpaRepository<DoubanImage, Integer> {

    @Query("SELECT i FROM DoubanImage i WHERE i.fk = :fk")
    DoubanImage findByFk(@Param("fk") String id);
}
