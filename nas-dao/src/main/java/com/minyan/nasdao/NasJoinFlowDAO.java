package com.minyan.nasdao;

import com.minyan.nascommon.po.JoinFlowPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasJoinFlowDAO继承基类
 */
@Mapper
@Repository
public interface NasJoinFlowDAO extends MyBatisBaseDao<JoinFlowPO, Long> {
}