package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.JoinFlowPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasJoinFlowDAO继承基类
 */
@Mapper
@Repository
public interface NasJoinFlowDAO extends BaseMapper<JoinFlowPO> {
}