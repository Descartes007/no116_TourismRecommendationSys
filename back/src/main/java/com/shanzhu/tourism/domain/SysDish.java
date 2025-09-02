package com.shanzhu.tourism.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 菜品信息对象 sys_dish
 *
 * @author tourism
 */
@Data
@TableName("sys_dish")
public class SysDish {
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId
    private String id;
    
    /** 餐厅ID */
    private String restaurantId;

    /** 菜品名称 */
    private String name;

    /** 价格 */
    private Float price;

    /** 菜品类型 */
    private String dishType;
    
    /** 描述介绍 */
    private String description;
    
    /** 菜品图片 */
    private String images;
    
    /** 是否特色菜(0否 1是) */
    private Integer isSpecial;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;
    
    /** 所属餐厅名称 */
    @TableField(exist = false)
    private String restaurantName;
} 