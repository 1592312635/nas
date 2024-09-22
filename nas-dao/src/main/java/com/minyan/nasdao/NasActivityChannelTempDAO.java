package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ActivityChannelTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasActivityChannelTempDAO继承基类
 */
@Mapper
@Repository
public interface NasActivityChannelTempDAO extends BaseMapper<ActivityChannelTempPO> {
}