package com.fly.dao;

import com.fly.pojo.TagObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author david
 * @date 19/08/18 14:43
 */
public interface TagObjectDao extends JpaRepository<TagObject, Integer> {

    @Query("SELECT to FROM TagObject to WHERE to.fk = :fk AND to.tagId = :tag_id")
    List<TagObject> findByFkAndTagId(@Param("fk") String id, @Param("tag_id") String tagId);

}
