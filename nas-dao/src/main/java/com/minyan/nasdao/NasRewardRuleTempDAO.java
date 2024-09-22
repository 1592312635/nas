package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.RewardRuleTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasRewardRuleTempDAO继承基类
 */
@Mapper
@Repository
public interface NasRewardRuleTempDAO extends BaseMapper<RewardRuleTempPO> {
}