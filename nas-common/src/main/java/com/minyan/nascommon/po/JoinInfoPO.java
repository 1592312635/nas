package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 活动参与信息表
 */
@Data
public class JoinInfoPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户注册id
     */
    private String user_id;

    /**
     * 活动id
     */
    private Integer activity_id;

    /**
     * 模块id
     */
    private Integer module_id;

    /**
     * 事件id
     */
    private Integer event_id;

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