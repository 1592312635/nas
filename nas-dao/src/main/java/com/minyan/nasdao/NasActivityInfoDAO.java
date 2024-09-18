package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ActivityInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityInfoDAO继承基类
 */
@Mapper
public interface NasActivityInfoDAO extends BaseMapper<ActivityInfoPO> {
}