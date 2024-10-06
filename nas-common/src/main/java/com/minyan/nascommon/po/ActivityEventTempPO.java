package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minyan.nascommon.vo.MActivityEventDetailVO;
import lombok.Data;

/**
 * @author 活动事件临时表
 */
@Data
@TableName("nas_activity_event_temp")
public class ActivityEventTempPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 模块id */
  private Integer moduleId;

  /** 事件名称 */
  private String eventName;

  /** 事件类型 */
  private String eventType;

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
   * 转化po为事件详情出参
   *
   * @param po
   * @return
   */
  public static MActivityEventDetailVO poConvertToVo(ActivityEventTempPO po) {
    MActivityEventDetailVO mActivityEventDetailVO = new MActivityEventDetailVO();
    mActivityEventDetailVO.setEventId(po.getId());
    mActivityEventDetailVO.setActivityId(po.getActivityId());
    mActivityEventDetailVO.setModuleId(po.getModuleId());
    mActivityEventDetailVO.setEventName(po.getEventName());
    mActivityEventDetailVO.setEventType(po.getEventType());
    return mActivityEventDetailVO;
  }
}
