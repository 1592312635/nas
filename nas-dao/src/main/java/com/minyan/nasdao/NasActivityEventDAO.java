package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ActivityEventPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityEventDAO继承基类
 */
@Mapper
@Repository
public interface NasActivityEventDAO extends BaseMapper<ActivityEventPO> {
}