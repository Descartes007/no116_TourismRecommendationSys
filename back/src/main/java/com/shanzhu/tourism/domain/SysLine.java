package com.shanzhu.tourism.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * 旅游线路
 *
 * @author: ShanZhu
 * @date: 2023-11-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_line")
public class SysLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private Integer temperature;

    private String geography;
    
    /**
     * 价格
     */
    private Float price;
    
    /**
     * 出行时间
     */
    private String duration;
    
    /**
     * 包含景点
     */
    private String attractions;
    
    /**
     * 行程安排
     */
    private String schedule;

    /**
     * 名称
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    private String introduce;

    private String images;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer state;

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
    
    /**
     * 最小价格（查询条件）
     */
    @TableField(exist = false)
    private Float minPrice;
    
    /**
     * 最大价格（查询条件）
     */
    @TableField(exist = false)
    private Float maxPrice;
}
