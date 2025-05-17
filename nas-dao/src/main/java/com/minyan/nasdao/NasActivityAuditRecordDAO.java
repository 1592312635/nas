package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ActivityAuditRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/** NasActivityAuditDAO继承基类 */
@Mapper
@Repository
public interface NasActivityAuditRecordDAO extends BaseMapper<ActivityAuditRecordPO> {}
