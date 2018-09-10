package com.fly.dao;

import com.fly.pojo.DoubanUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author david
 * @date 10/09/18 13:23
 */
public interface UserDao extends JpaRepository<DoubanUser, String> {
}
