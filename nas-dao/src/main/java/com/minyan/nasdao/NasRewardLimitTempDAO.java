package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.RewardLimitTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasRewardLimitTempDAO继承基类
 */
@Mapper
@Repository
public interface NasRewardLimitTempDAO extends BaseMapper<RewardLimitTempPO> {
}