package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ActivityInfoTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityInfoTempDAO继承基类
 */
@Mapper
@Repository
public interface NasActivityInfoTempDAO extends BaseMapper<ActivityInfoTempPO> {
}