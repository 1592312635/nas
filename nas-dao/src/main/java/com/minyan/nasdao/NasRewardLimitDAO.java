package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.NasRewardLimitPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasRewardLimitDAO继承基类
 */
@Mapper
@Repository
public interface NasRewardLimitDAO extends BaseMapper<NasRewardLimitPO> {
}