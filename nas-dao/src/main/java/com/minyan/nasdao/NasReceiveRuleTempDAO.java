package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ReceiveRuleTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasReceiveRuleTempDAO继承基类
 */
@Mapper
@Repository
public interface NasReceiveRuleTempDAO extends BaseMapper<ReceiveRuleTempPO> {
}