package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ActivityEventTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityEventTempDAO继承基类
 */
@Mapper
@Repository
public interface NasActivityEventTempDAO extends BaseMapper<ActivityEventTempPO> {
}