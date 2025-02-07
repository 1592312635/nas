package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 
 * 活动奖励规则门槛临时表
 */
@Data
@TableName("nas_reward_limit_temp")
public class RewardLimitTempPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 活动id
     */
    private Integer activityId;

    /**
     * 模块id
     */
    private Integer moduleId;

    /**
     * 事件id
     */
    private Long eventId;

    /**
     * 奖励规则id
     */
    private Long rewardRuleId;

    /**
     * 规则类型
     */
    private String limitKey;

    /**
     * 规则详细限制内容
     */
    private String limitJson;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识(0未删除1删除)
     */
    private Integer delTag;

    private static final long serialVersionUID = 1L;
}