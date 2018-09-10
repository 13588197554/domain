package com.fly.dao;

import com.fly.pojo.DoubanDistrict;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author david
 * @date 10/09/18 13:25
 */
public interface DistrictDao extends JpaRepository<DoubanDistrict, String> {
}
