package com.minyan.nasdao;

import com.minyan.nascommon.po.ReceiveLimitPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasReceiveLimitDAO继承基类
 */
@Mapper
@Repository
public interface NasReceiveLimitDAO extends MyBatisBaseDao<ReceiveLimitPO, Long> {
}