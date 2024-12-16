package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 活动奖励发放记录表
 */
@Data
public class NasSendRecordPO implements Serializable {
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
     * 奖品规则id
     */
    private Integer rewardRuleId;

    /**
     * 奖品id
     */
    private Integer rewardId;

    /**
     * 奖品类型
     */
    private Integer rewardType;

    /**
     * 奖品名称
     */
    private String rewardName;

    /**
     * 图片链接
     */
    private String imageUrl;

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