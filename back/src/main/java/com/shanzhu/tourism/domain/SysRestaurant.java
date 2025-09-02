package com.shanzhu.tourism.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 餐厅信息对象 sys_restaurant
 *
 * @author tourism
 */
@Data
@TableName("sys_restaurant")
public class SysRestaurant {
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId
    private String id;

    /** 餐厅名称 */
    private String name;

    /** 所在地区 */
    private String region;

    /** 详细地址 */
    private String address;
    
    /** 联系电话 */
    private String phone;
    
    /** 菜系类型 */
    private String cuisineType;
    
    /** 价格等级(1-5) */
    private Integer priceLevel;
    
    /** 营业时间 */
    private String businessHours;
    
    /** 餐厅介绍 */
    private String introduce;
    
    /** 餐厅图片 */
    private String images;
    
    /** 状态(0禁用 1启用) */
    private Integer state;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;
    
    /** 餐厅下的菜品列表 */
    @TableField(exist = false)
    private java.util.List<SysDish> dishes;
} 