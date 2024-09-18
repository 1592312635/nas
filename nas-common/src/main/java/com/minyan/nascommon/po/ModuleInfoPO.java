package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 
 */
@Data
public class ModuleInfoPO implements Serializable {
    private Long id;

    /**
     * 活动id
     */
    private Integer activity_id;

    /**
     * 模块id
     */
    private Integer module_id;

    /**
     * 模块名称
     */
    private String module_name;

    /**
     * 模块开始时间
     */
    private Date begin_time;

    /**
     * 模块截止时间
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
     * 删除标识
     */
    private Integer del_tag;

    private static final long serialVersionUID = 1L;
}