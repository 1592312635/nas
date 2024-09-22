package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ReceiveLimitTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasReceiveLimitTempDAO继承基类
 */
@Mapper
@Repository
public interface NasReceiveLimitTempDAO extends BaseMapper<ReceiveLimitTempPO> {
}