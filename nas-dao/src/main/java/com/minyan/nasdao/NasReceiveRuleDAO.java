package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ReceiveRulePO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasReceiveRuleDAO继承基类
 */
@Mapper
@Repository
public interface NasReceiveRuleDAO extends BaseMapper<ReceiveRulePO> {
}