package com.minyan.nasdao;

import com.minyan.nascommon.po.ActivityInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityInfoDAO继承基类
 */
@Mapper
@Repository
public interface NasActivityInfoDAO extends MyBatisBaseDao<ActivityInfoPO, Long> {
}