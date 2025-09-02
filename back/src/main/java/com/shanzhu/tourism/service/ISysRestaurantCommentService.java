package com.shanzhu.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shanzhu.tourism.domain.SysRestaurantComment;

import java.util.List;

/**
 * 餐厅评论Service接口
 *
 * @author tourism
 */
public interface ISysRestaurantCommentService extends IService<SysRestaurantComment> {
    
    /**
     * 查询餐厅评论列表
     *
     * @param comment 餐厅评论
     * @return 餐厅评论集合
     */
    List<SysRestaurantComment> selectCommentList(SysRestaurantComment comment);
    
    /**
     * 根据ID查询餐厅评论详细信息
     *
     * @param id 餐厅评论ID
     * @return 餐厅评论
     */
    SysRestaurantComment selectCommentById(String id);
    
    /**
     * 根据餐厅ID查询评论列表
     *
     * @param restaurantId 餐厅ID
     * @return 评论列表
     */
    List<SysRestaurantComment> selectCommentsByRestaurantId(String restaurantId);
    
    /**
     * 新增餐厅评论
     *
     * @param comment 餐厅评论
     * @return 结果
     */
    boolean insertComment(SysRestaurantComment comment);
    
    /**
     * 修改餐厅评论
     *
     * @param comment 餐厅评论
     * @return 结果
     */
    boolean updateComment(SysRestaurantComment comment);
    
    /**
     * 批量删除餐厅评论
     *
     * @param ids 需要删除的餐厅评论ID
     * @return 结果
     */
    boolean deleteCommentByIds(String[] ids);
    
    /**
     * 删除餐厅评论信息
     *
     * @param id 餐厅评论ID
     * @return 结果
     */
    boolean deleteCommentById(String id);
} 