package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ReceiveLimitPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasReceiveLimitDAO继承基类
 */
@Mapper
@Repository
public interface NasReceiveLimitDAO extends BaseMapper<ReceiveLimitPO> {
}