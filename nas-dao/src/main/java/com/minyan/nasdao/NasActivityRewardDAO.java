package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ActivityRewardPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityRewardDAO继承基类
 */
@Mapper
@Repository
public interface NasActivityRewardDAO extends BaseMapper<ActivityRewardPO> {

}