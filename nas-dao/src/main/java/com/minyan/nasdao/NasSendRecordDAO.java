package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.SendRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/** NasSendRecordDAO继承基类 */
@Mapper
@Repository
public interface NasSendRecordDAO extends BaseMapper<SendRecordPO> {}
