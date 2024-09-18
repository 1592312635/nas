package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.JoinRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasJoinRecordDAO继承基类
 */
@Mapper
@Repository
public interface NasJoinRecordDAO extends BaseMapper<JoinRecordPO> {
}