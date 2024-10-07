package com.minyan.nasmapi.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.Enum.RedisKeyEnum;
import com.minyan.nascommon.param.MActivityInfoSaveParam;
import com.minyan.nascommon.param.MActivityModuleSaveParam;
import com.minyan.nascommon.po.ModuleInfoTempPO;
import com.minyan.nascommon.service.RedisService;
import com.minyan.nascommon.vo.MModuleInfoDetailVO;
import com.minyan.nasdao.NasModuleInfoTempDAO;
import com.minyan.nasmapi.manager.EventInfoManager;
import com.minyan.nasmapi.manager.ModuleInfoManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @decription 模块维度处理service
 * @author minyan.he
 * @date 2024/10/6 13:25
 */
@Service
public class ModuleInfoMangerImpl implements ModuleInfoManager {
  Logger logger = LoggerFactory.getLogger(ModuleInfoMangerImpl.class);
  @Autowired private RedisService redisService;
  @Autowired private EventInfoManager eventInfoManager;
  @Autowired private NasModuleInfoTempDAO moduleInfoTempDAO;

  /**
   * 通过活动id获得活动下的所有模块信息
   *
   * @param activityId
   * @return
   */
  @Override
  public List<MModuleInfoDetailVO> getModuleInfoByActivityId(Integer activityId) {
    List<MModuleInfoDetailVO> mModuleInfoDetailVOList = Lists.newArrayList();
    QueryWrapper<ModuleInfoTempPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ModuleInfoTempPO::getActivityId, activityId)
        .eq(ModuleInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ModuleInfoTempPO> moduleInfoPOS = moduleInfoTempDAO.selectList(queryWrapper);
    if (!CollectionUtils.isEmpty(moduleInfoPOS)) {
      mModuleInfoDetailVOList =
          moduleInfoPOS.stream().map(ModuleInfoTempPO::poConvertToVo).collect(Collectors.toList());
    }
    return mModuleInfoDetailVOList;
  }

  /**
   * 保存模块信息及模块内信息
   *
   * @param param
   * @return
   */
  @Override
  public Boolean saveActivityModuleTemp(MActivityInfoSaveParam param) {
    Integer activityId = param.getActivityId();
    List<MActivityModuleSaveParam> moduleSaveInfos = param.getModuleSaveInfos();
    QueryWrapper<ModuleInfoTempPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ModuleInfoTempPO::getActivityId, activityId)
        .eq(ModuleInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ModuleInfoTempPO> moduleInfoTempPOS = moduleInfoTempDAO.selectList(queryWrapper);

    // 新增变更删除分离
    List<MActivityModuleSaveParam> toAdd = Lists.newArrayList();
    List<MActivityModuleSaveParam> toUpdate = Lists.newArrayList();
    List<ModuleInfoTempPO> toDelete = Lists.newArrayList();
    Map<Integer, ModuleInfoTempPO> tempMap = Maps.newHashMap();
    for (ModuleInfoTempPO temp : moduleInfoTempPOS) {
      tempMap.put(temp.getModuleId(), temp);
    }
    for (MActivityModuleSaveParam module : moduleSaveInfos) {
      Integer moduleId = module.getModuleId();
      if (!tempMap.containsKey(moduleId)) {
        toAdd.add(module);
      } else {
        toUpdate.add(module);
        tempMap.remove(moduleId);
      }
      toDelete.addAll(tempMap.values());
    }

    // 数据库操作
    for (MActivityModuleSaveParam mActivityModuleSaveParam : toAdd) {
      try {
        if (mActivityModuleSaveParam.getModuleId() == null) {
          mActivityModuleSaveParam.setModuleId(produceModuleId(activityId));
        }
        moduleInfoTempDAO.insert(buildModuleInfoTempPO(activityId, mActivityModuleSaveParam));
      } catch (Exception e) {
        logger.info(
            "[ActivityInfoManagerImpl][saveActivityModuleTemp]新增模块信息时失败，模块id：{}",
            mActivityModuleSaveParam.getModuleId(),
            e);
        return false;
      } finally {
        redisService.releaseLock(
            String.format(
                RedisKeyEnum.MODULE_SAVE_MODULE_ID.getKey(),
                mActivityModuleSaveParam.getModuleId()));
      }
      // 新增事件信息
      eventInfoManager.saveEventInfos(activityId, mActivityModuleSaveParam);
    }
    for (MActivityModuleSaveParam mActivityModuleSaveParam : toUpdate) {
      // 变更事件信息
      eventInfoManager.updateEventInfos(activityId, mActivityModuleSaveParam);

      UpdateWrapper<ModuleInfoTempPO> updateWrapper = new UpdateWrapper<>();
      updateWrapper
          .lambda()
          .set(ModuleInfoTempPO::getModuleName, mActivityModuleSaveParam.getModuleName())
          .set(ModuleInfoTempPO::getBeginTime, mActivityModuleSaveParam.getBeginTime())
          .set(ModuleInfoTempPO::getEndTime, mActivityModuleSaveParam.getEndTime())
          .eq(ModuleInfoTempPO::getModuleId, mActivityModuleSaveParam.getModuleId());
      moduleInfoTempDAO.update(null, updateWrapper);
    }
    if (!CollectionUtils.isEmpty(toDelete)) {
      // 删除模块下的事件信息
      eventInfoManager.delEventInfos(toDelete);

      List<Integer> delModuleIds =
          toDelete.stream().map(ModuleInfoTempPO::getModuleId).collect(Collectors.toList());
      UpdateWrapper<ModuleInfoTempPO> deleteWrapper = new UpdateWrapper<>();
      deleteWrapper
          .lambda()
          .set(ModuleInfoTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ModuleInfoTempPO::getModuleId, delModuleIds);
      moduleInfoTempDAO.update(null, deleteWrapper);
    }
    return true;
  }

  /**
   * 构建模块信息数据
   *
   * @param activityId
   * @param param
   * @return
   */
  ModuleInfoTempPO buildModuleInfoTempPO(Integer activityId, MActivityModuleSaveParam param) {
    ModuleInfoTempPO moduleInfoTempPO = new ModuleInfoTempPO();
    moduleInfoTempPO.setActivityId(activityId);
    moduleInfoTempPO.setModuleId(param.getModuleId());
    moduleInfoTempPO.setModuleName(param.getModuleName());
    moduleInfoTempPO.setBeginTime(param.getBeginTime());
    moduleInfoTempPO.setEndTime(param.getEndTime());
    return moduleInfoTempPO;
  }

  /**
   * 通过活动id顺位生成模块id
   *
   * @param activityId
   * @return
   */
  Integer produceModuleId(Integer activityId) {
    QueryWrapper<ModuleInfoTempPO> moduleInfoTempPOQueryWrapper = new QueryWrapper<>();
    moduleInfoTempPOQueryWrapper
        .select("max(module_id)")
        .lambda()
        .eq(ModuleInfoTempPO::getActivityId, activityId);
    ModuleInfoTempPO moduleInfoTempPO = moduleInfoTempDAO.selectOne(moduleInfoTempPOQueryWrapper);
    Integer targetModuleId =
        ObjectUtils.isEmpty(moduleInfoTempPO)
            ? activityId * 1000 + 1
            : moduleInfoTempPO.getModuleId() + 1;
    redisService.lock(
        String.format("%s:%s", RedisKeyEnum.MODULE_SAVE_MODULE_ID.getKey(), targetModuleId));
    return targetModuleId;
  }
}
