package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 
 */
@Data
public class JoinEventPO implements Serializable {
    private Long id;

    /**
     * 用户注册id
     */
    private String user_id;

    /**
     * 事件id
     */
    private Long event_id;

    /**
     * 奖品规则id
     */
    private Long reward_rule_id;

    /**
     * 调度时间
     */
    private Date schedule_time;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 删除标识(1删除0未删除)
     */
    private Integer del_tag;

    private static final long serialVersionUID = 1L;
}