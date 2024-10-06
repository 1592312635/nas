package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minyan.nascommon.vo.MModuleInfoDetailVO;
import lombok.Data;

/**
 * @author 活动模块信息临时表
 */
@Data
@TableName("nas_module_info_temp")
public class ModuleInfoTempPO implements Serializable {
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 模块id */
  private Integer moduleId;

  /** 模块名称 */
  private String moduleName;

  /** 模块开始时间 */
  private Date beginTime;

  /** 模块截止时间 */
  private Date endTime;

  /** 创建时间 */
  @TableField(fill = FieldFill.INSERT)
  private Date createTime;

  /** 更新时间 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Date updateTime;

  /** 删除标识(1删除0未删除) */
  @TableField(fill = FieldFill.INSERT)
  private Integer delTag;

  private static final long serialVersionUID = 1L;

  /**
   * 转化查询模块详情出参
   *
   * @param po
   * @return
   */
  public static MModuleInfoDetailVO poConvertToVo(ModuleInfoTempPO po) {
    MModuleInfoDetailVO mModuleInfoDetailVO = new MModuleInfoDetailVO();
    mModuleInfoDetailVO.setActivityId(po.getActivityId());
    mModuleInfoDetailVO.setModuleId(po.getModuleId());
    mModuleInfoDetailVO.setModuleName(po.getModuleName());
    mModuleInfoDetailVO.setBeginTime(po.getBeginTime());
    mModuleInfoDetailVO.setEndTime(po.getEndTime());
    return mModuleInfoDetailVO;
  }
}
