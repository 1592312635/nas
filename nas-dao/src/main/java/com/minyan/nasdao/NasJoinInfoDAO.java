package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.JoinInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasJoinInfoDAO继承基类
 */
@Mapper
@Repository
public interface NasJoinInfoDAO extends BaseMapper<JoinInfoPO> {
}