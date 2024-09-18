package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 
 */
@Data
public class ActivityInfoPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 活动id
     */
    private Integer activity_id;

    /**
     * 活动名称
     */
    private String activity_name;

    /**
     * 开始时间
     */
    private Date begin_time;

    /**
     * 结束时间
     */
    private Date end_time;

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