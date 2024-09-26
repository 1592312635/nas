package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ActivityRewardTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityRewardTempDAO继承基类
 */
@Mapper
@Repository
public interface NasActivityRewardTempDAO extends BaseMapper<ActivityRewardTempPO> {
}