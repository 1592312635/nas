package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.RewardRulePO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasRewardRuleDAO继承基类
 */
@Mapper
@Repository
public interface NasRewardRuleDAO extends BaseMapper<RewardRulePO> {
}