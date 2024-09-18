package com.minyan.nasdao;

import com.minyan.nascommon.po.AcitvityRewardPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasAcitvityRewardDAO继承基类
 */
@Mapper
@Repository
public interface NasAcitvityRewardDAO extends MyBatisBaseDao<AcitvityRewardPO, Long> {
}