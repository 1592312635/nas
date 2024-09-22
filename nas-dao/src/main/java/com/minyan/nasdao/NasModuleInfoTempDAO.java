package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ModuleInfoTempPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasModuleInfoTempDAO继承基类
 */
@Mapper
@Repository
public interface NasModuleInfoTempDAO extends BaseMapper<ModuleInfoTempPO> {
}