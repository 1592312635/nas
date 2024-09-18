package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 活动事件表
 */
@Data
public class ActivityEventPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 活动id
     */
    private Long activity_id;

    /**
     * 模块id
     */
    private Long module_id;

    /**
     * 事件名称
     */
    private String event_name;

    /**
     * 事件类型
     */
    private String event_type;

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