package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ReceiveRulePO;
import com.minyan.nascommon.po.RewardLimitPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/** NasRewardLimitDAO继承基类 */
@Mapper
@Repository
public interface NasRewardLimitDAO extends BaseMapper<RewardLimitPO> {}
