package com.fly.dao;

import com.fly.pojo.DoubanMusic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author david
 * @date 04/09/18 15:11
 */
public interface MusicDao extends JpaRepository<DoubanMusic, Integer> {
}
