package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.NasSendFlowPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/** NasSendFlowDAO继承基类 */
@Mapper
@Repository
public interface NasSendFlowDAO extends BaseMapper<NasSendFlowPO> {}
