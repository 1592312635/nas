package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.minyan.nascommon.vo.MActivityChannelDetailVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 * 活动渠道表
 */
@Data
@TableName("nas_activity_channel")
public class ActivityChannelPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 渠道编码
     */
    private String channelCode;

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

    /**
     * 转化出参为查询活动详情时的渠道详情信息VO
     * @param po
     * @return
     */
    public static MActivityChannelDetailVO poConvertToVo(ActivityChannelPO po) {
        MActivityChannelDetailVO mActivityChannelDetailVO = new MActivityChannelDetailVO();
        mActivityChannelDetailVO.setActivityId(po.getActivityId());
        mActivityChannelDetailVO.setChannelCode(po.getChannelCode());
        mActivityChannelDetailVO.setChannelName(po.getChannelName());
        mActivityChannelDetailVO.setChannelUrl(po.getChannelCode());
        return mActivityChannelDetailVO;
    }
}