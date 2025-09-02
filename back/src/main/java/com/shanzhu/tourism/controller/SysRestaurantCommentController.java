package com.shanzhu.tourism.controller;

import com.shanzhu.tourism.domain.Result;
import com.shanzhu.tourism.domain.SysRestaurantComment;
import com.shanzhu.tourism.service.ISysRestaurantCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 餐厅评论 控制器
 *
 * @author tourism
 */
@RestController
@RequestMapping("/restaurantComment")
public class SysRestaurantCommentController {
    
    @Autowired
    private ISysRestaurantCommentService commentService;
    
    /**
     * 获取餐厅评论列表
     */
    @GetMapping("/list")
    public Result list(SysRestaurantComment comment) {
        List<SysRestaurantComment> list = commentService.selectCommentList(comment);
        return Result.success(list);
    }
    
    /**
     * 获取餐厅评论详情
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable("id") String id) {
        return Result.success(commentService.selectCommentById(id));
    }
    
    /**
     * 根据餐厅ID获取评论列表
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Result getCommentsByRestaurantId(@PathVariable("restaurantId") String restaurantId) {
        List<SysRestaurantComment> comments = commentService.selectCommentsByRestaurantId(restaurantId);
        return Result.success(comments);
    }
    
    /**
     * 新增餐厅评论
     */
    @PostMapping
    public Result add(@RequestBody SysRestaurantComment comment) {
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        return commentService.insertComment(comment) ? Result.success() : Result.fail("新增评论失败");
    }
    
    /**
     * 修改餐厅评论
     */
    @PutMapping
    public Result edit(@RequestBody SysRestaurantComment comment) {
        comment.setUpdateTime(new Date());
        return commentService.updateComment(comment) ? Result.success() : Result.fail("修改评论失败");
    }
    
    /**
     * 删除餐厅评论
     */
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable String[] ids) {
        return commentService.deleteCommentByIds(ids) ? Result.success() : Result.fail("删除评论失败");
    }
} 