package com.fly.dao;

import com.fly.pojo.DoubanUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoubanUserDao extends JpaRepository<DoubanUser, Integer> {
}
