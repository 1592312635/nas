package com.minyan.nasdao;

import com.minyan.nascommon.po.ActivityChannelPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityChannelDAO继承基类
 */
@Mapper
@Repository
public interface NasActivityChannelDAO extends MyBatisBaseDao<ActivityChannelPO, Long> {
}