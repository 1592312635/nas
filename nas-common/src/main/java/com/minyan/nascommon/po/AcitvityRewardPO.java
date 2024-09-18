package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 活动奖品表
 */
@Data
public class AcitvityRewardPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 奖品类型(RewardTypeEnum)
     */
    private Integer reward_type;

    /**
     * 奖品名称
     */
    private String reward_name;

    /**
     * 奖品批次编码
     */
    private String batch_code;

    /**
     * 图片链接
     */
    private String image_url;

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