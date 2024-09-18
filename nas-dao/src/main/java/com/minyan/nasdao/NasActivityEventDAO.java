package com.minyan.nasdao;

import com.minyan.nascommon.po.ActivityEventPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityEventDAO继承基类
 */
@Mapper
@Repository
public interface NasActivityEventDAO extends MyBatisBaseDao<ActivityEventPO, Long> {
}