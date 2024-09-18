package com.minyan.nasdao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minyan.nascommon.po.ModuleInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasModuleInfoDAO继承基类
 */
@Mapper
@Repository
public interface NasModuleInfoDAO extends BaseMapper<ModuleInfoPO> {
}