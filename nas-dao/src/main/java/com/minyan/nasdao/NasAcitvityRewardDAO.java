package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.AcitvityRewardPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasAcitvityRewardDAO继承基类
 */
@Mapper
@Repository
public interface NasAcitvityRewardDAO extends BaseMapper<AcitvityRewardPO> {

}