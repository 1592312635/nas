package com.minyan.nasdao;

import com.minyan.nascommon.po.ModuleInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * NasModuleInfoDAO继承基类
 */
@Mapper
@Repository
public interface NasModuleInfoDAO extends MyBatisBaseDao<ModuleInfoPO, Long> {
}