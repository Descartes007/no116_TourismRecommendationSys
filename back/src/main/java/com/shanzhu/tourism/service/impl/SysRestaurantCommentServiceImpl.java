package com.shanzhu.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shanzhu.tourism.domain.SysRestaurant;
import com.shanzhu.tourism.domain.SysRestaurantComment;
import com.shanzhu.tourism.domain.User;
import com.shanzhu.tourism.mapper.SysRestaurantCommentMapper;
import com.shanzhu.tourism.mapper.SysRestaurantMapper;
import com.shanzhu.tourism.mapper.UserMapper;
import com.shanzhu.tourism.service.ISysRestaurantCommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 餐厅评论Service实现
 *
 * @author tourism
 */
@Service
public class SysRestaurantCommentServiceImpl extends ServiceImpl<SysRestaurantCommentMapper, SysRestaurantComment> implements ISysRestaurantCommentService {

    @Autowired
    private SysRestaurantCommentMapper commentMapper;
    
    @Autowired
    private SysRestaurantMapper restaurantMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询餐厅评论列表
     *
     * @param comment 餐厅评论
     * @return 餐厅评论
     */
    @Override
    public List<SysRestaurantComment> selectCommentList(SysRestaurantComment comment) {
        LambdaQueryWrapper<SysRestaurantComment> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加条件查询
        if (StringUtils.isNotBlank(comment.getRestaurantId())) {
            queryWrapper.eq(SysRestaurantComment::getRestaurantId, comment.getRestaurantId());
        }
        
        if (StringUtils.isNotBlank(comment.getUserId())) {
            queryWrapper.eq(SysRestaurantComment::getUserId, comment.getUserId());
        }
        
        if (comment.getScore() != null) {
            queryWrapper.eq(SysRestaurantComment::getScore, comment.getScore());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(SysRestaurantComment::getCreateTime);
        
        List<SysRestaurantComment> comments = commentMapper.selectList(queryWrapper);
        
        // 填充所属餐厅名称和用户名信息
        for (SysRestaurantComment item : comments) {
            // 填充餐厅名称
            SysRestaurant restaurant = restaurantMapper.selectById(item.getRestaurantId());
            if (restaurant != null) {
                item.setRestaurantName(restaurant.getName());
            }
            
            // 填充用户信息
            if (StringUtils.isNotBlank(item.getUserId())) {
                User user = userMapper.selectById(item.getUserId());
                if (user != null) {
                    item.setUserName(user.getUserName());
                }
            }
        }
        
        return comments;
    }

    /**
     * 根据ID查询餐厅评论详细信息
     *
     * @param id 餐厅评论ID
     * @return 餐厅评论
     */
    @Override
    public SysRestaurantComment selectCommentById(String id) {
        SysRestaurantComment comment = commentMapper.selectById(id);
        if (comment != null) {
            // 填充餐厅名称
            if (StringUtils.isNotBlank(comment.getRestaurantId())) {
                SysRestaurant restaurant = restaurantMapper.selectById(comment.getRestaurantId());
                if (restaurant != null) {
                    comment.setRestaurantName(restaurant.getName());
                }
            }
            
            // 填充用户信息
            if (StringUtils.isNotBlank(comment.getUserId())) {
                User user = userMapper.selectById(comment.getUserId());
                if (user != null) {
                    comment.setUserName(user.getUserName());
                }
            }
        }
        return comment;
    }
    
    /**
     * 根据餐厅ID查询评论列表
     *
     * @param restaurantId 餐厅ID
     * @return 评论列表
     */
    @Override
    public List<SysRestaurantComment> selectCommentsByRestaurantId(String restaurantId) {
        LambdaQueryWrapper<SysRestaurantComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRestaurantComment::getRestaurantId, restaurantId);
        queryWrapper.orderByDesc(SysRestaurantComment::getCreateTime);
        List<SysRestaurantComment> comments = commentMapper.selectList(queryWrapper);
        
        // 填充用户信息
        for (SysRestaurantComment comment : comments) {
            if (StringUtils.isNotBlank(comment.getUserId())) {
                User user = userMapper.selectById(comment.getUserId());
                if (user != null) {
                    comment.setUserName(user.getUserName());
                }
            }
        }
        
        return comments;
    }

    /**
     * 新增餐厅评论
     *
     * @param comment 餐厅评论
     * @return 结果
     */
    @Override
    public boolean insertComment(SysRestaurantComment comment) {
        comment.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        
        // 如果没有传入用户名但传入了用户ID，则从用户表获取用户名
        if ((StringUtils.isBlank(comment.getCreateBy()) || StringUtils.isBlank(comment.getUserName())) 
            && StringUtils.isNotBlank(comment.getUserId())) {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                if (StringUtils.isBlank(comment.getCreateBy())) {
                    comment.setCreateBy(user.getUserName());
                }
                comment.setUserName(user.getUserName());
                
                // 如果没有设置头像，同时获取用户头像
                if (StringUtils.isBlank(comment.getAvatar())) {
                    comment.setAvatar(user.getAvatar());
                }
            }
        }
        
        return commentMapper.insert(comment) > 0;
    }

    /**
     * 修改餐厅评论
     *
     * @param comment 餐厅评论
     * @return 结果
     */
    @Override
    public boolean updateComment(SysRestaurantComment comment) {
        return commentMapper.updateById(comment) > 0;
    }

    /**
     * 批量删除餐厅评论
     *
     * @param ids 需要删除的餐厅评论ID
     * @return 结果
     */
    @Override
    public boolean deleteCommentByIds(String[] ids) {
        return commentMapper.deleteBatchIds(Arrays.asList(ids)) > 0;
    }

    /**
     * 删除餐厅评论信息
     *
     * @param id 餐厅评论ID
     * @return 结果
     */
    @Override
    public boolean deleteCommentById(String id) {
        return commentMapper.deleteById(id) > 0;
    }
} 