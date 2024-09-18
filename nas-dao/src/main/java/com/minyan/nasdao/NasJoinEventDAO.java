package com.minyan.nasdao;

import com.minyan.nascommon.po.JoinEventPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasJoinEventDAO继承基类
 */
@Mapper
@Repository
public interface NasJoinEventDAO extends MyBatisBaseDao<JoinEventPO, Long> {
}