package com.shanzhu.tourism.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 餐厅评论对象 sys_restaurant_comment
 *
 * @author tourism
 */
@Data
@TableName("sys_restaurant_comment")
public class SysRestaurantComment {
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId
    private String id;
    
    /** 餐厅ID */
    private String restaurantId;
    
    /** 用户ID */
    private String userId;
    
    /** 评论内容 */
    private String content;
    
    /** 评分(1-5星) */
    private Integer score;
    
    /** 用户头像 */
    private String avatar;

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
    
    /** 用户名称 */
    @TableField(exist = false)
    private String userName;
} 