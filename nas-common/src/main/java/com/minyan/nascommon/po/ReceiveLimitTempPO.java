package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 
 * 领取门槛临时表
 */
@Data
@TableName("nas_receive_limit_temp")
public class ReceiveLimitTempPO implements Serializable {
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
     * 规则id
     */
    private Long receiveRuleId;

    /**
     * 门槛标识
     */
    private String limitKey;

    /**
     * 门槛数据json
     */
    private String limitJson;

    /**
     * 门槛类型(0初始门槛1有效门槛2达标门槛3规则引擎有效门槛4规则引擎达标门槛)
     */
    private Integer limitType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识(1删除0未删除)
     */
    private Integer delTag;

    private static final long serialVersionUID = 1L;
}