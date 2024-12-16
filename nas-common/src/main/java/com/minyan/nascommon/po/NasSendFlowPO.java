package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 活动奖励发放流水表
 */
@Data
public class NasSendFlowPO implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 活动id
     */
    private Integer activityId;

    /**
     * 模块id
     */
    private Integer moduleId;

    /**
     * 用户注册id
     */
    private String userId;

    /**
     * 奖品id
     */
    private Integer rewardId;

    /**
     * 奖品规则
     */
    private Integer rewardType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 响应结果
     */
    private String response;

    /**
     * 调度时间
     */
    private Date scheduleTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识
     */
    private Integer delTag;

    private static final long serialVersionUID = 1L;
}