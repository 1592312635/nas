package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.AcitvityRewardTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasAcitvityRewardTempDAO继承基类
 */
@Mapper
@Repository
public interface NasAcitvityRewardTempDAO extends BaseMapper<AcitvityRewardTempPO> {
}