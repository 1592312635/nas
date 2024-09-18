package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 领取门槛
 */
@Data
public class ReceiveLimitPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 规则id
     */
    private Long receive_rule_id;

    /**
     * 门槛标识
     */
    private String event_key;

    /**
     * 门槛数据json
     */
    private String limit_json;

    /**
     * 门槛类型(0初始门槛1有效门槛2达标门槛3规则引擎有效门槛4规则引擎达标门槛)
     */
    private Integer limit_type;

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