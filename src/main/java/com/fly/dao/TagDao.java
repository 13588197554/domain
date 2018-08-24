package com.fly.dao;

import com.fly.pojo.FlyTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author david
 * @date 19/08/18 14:39
 */
public interface TagDao extends JpaRepository<FlyTag, String> {

    @Query(value = "SELECT t.id FROM FlyTag t WHERE t.tagName = :tag_name AND t.tagType = :tag_type")
    String findIdByNameAndType(@Param("tag_name") String movieTag, @Param("tag_type") String tagType);

    @Query(value = "SELECT t.tag_name FROM tag t WHERE t.tag_type = :tag_type AND t.pid != 0", nativeQuery = true)
    List<String> findNameByTypeAndPid(@Param("tag_type") String douban_book);
}
