package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 活动领取规则临时表
 */
@Data
@TableName("nas_receive_rule_temp")
public class ReceiveRuleTempPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 模块id */
  private Integer moduleId;

  /** 事件id */
  private Long eventId;

  /** 规则类型(1前台规则2异步发奖规则) */
  private Integer ruleType;

  /** 创建时间 */
  private Date createTime;

  /** 更新时间 */
  private Date updateTime;

  /** 删除标识(1删除0未删除) */
  private Integer delTag;

  private static final long serialVersionUID = 1L;
}
