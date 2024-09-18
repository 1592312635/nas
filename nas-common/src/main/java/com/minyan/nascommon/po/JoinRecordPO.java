package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 
 */
@Data
public class JoinRecordPO implements Serializable {
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
     * 用户注册id
     */
    private String user_id;

    /**
     * 参与类型(JoinTypeEnum)
     */
    private Integer join_type;

    /**
     * 额外参与信息
     */
    private String join_info;

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