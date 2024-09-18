package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.JoinEventPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasJoinEventDAO继承基类
 */
@Mapper
@Repository
public interface NasJoinEventDAO extends BaseMapper<JoinEventPO> {
}