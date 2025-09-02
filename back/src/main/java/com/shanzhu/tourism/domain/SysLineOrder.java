package com.shanzhu.tourism.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 旅游线路订单
 *
 * @author: ShanZhu
 * @date: 2025-03-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_line_order")
public class SysLineOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 线路id
     */
    private String lineId;

    /**
     * 线路名称
     */
    private String name;

    /**
     * 线路简介
     */
    private String introduce;

    /**
     * 图片
     */
    private String images;

    /**
     * 价格
     */
    private Float price;

    /**
     * 人数
     */
    private Integer num;

    /**
     * 总价
     */
    private Float totalPrice;

    /**
     * 出行时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date travelTime;

    /**
     * 出行人信息，JSON格式
     */
    private String people;

    /**
     * 状态：0-待支付，1-已支付待出行，2-已出行，3-已取消，4-已完成
     */
    private Integer state;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 创建者
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;
} 