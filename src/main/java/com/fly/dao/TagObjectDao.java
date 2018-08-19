package com.fly.dao;

import com.fly.pojo.TagObject;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author david
 * @date 19/08/18 14:43
 */
public interface TagObjectDao extends JpaRepository<TagObject, Integer> {
}
