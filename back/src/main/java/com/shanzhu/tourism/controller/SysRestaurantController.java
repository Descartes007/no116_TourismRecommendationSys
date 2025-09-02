package com.shanzhu.tourism.controller;

import com.shanzhu.tourism.domain.Result;
import com.shanzhu.tourism.domain.SysRestaurant;
import com.shanzhu.tourism.service.ISysRestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 餐厅信息 控制器
 *
 * @author tourism
 */
@RestController
@RequestMapping("/restaurant")
public class SysRestaurantController {
    
    @Autowired
    private ISysRestaurantService restaurantService;
    
    /**
     * 获取餐厅列表
     */
    @GetMapping("/list")
    public Result list(SysRestaurant restaurant) {
        List<SysRestaurant> list = restaurantService.selectRestaurantList(restaurant);
        return Result.success(list);
    }
    
    /**
     * 获取餐厅详情
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable("id") String id) {
        return Result.success(restaurantService.selectRestaurantById(id));
    }
    
    /**
     * 新增餐厅
     */
    @PostMapping
    public Result add(@RequestBody SysRestaurant restaurant) {
        restaurant.setCreateTime(new Date());
        restaurant.setUpdateTime(new Date());
        return restaurantService.insertRestaurant(restaurant) ? Result.success() : Result.fail("新增餐厅失败");
    }
    
    /**
     * 修改餐厅
     */
    @PutMapping
    public Result edit(@RequestBody SysRestaurant restaurant) {
        restaurant.setUpdateTime(new Date());
        return restaurantService.updateRestaurant(restaurant) ? Result.success() : Result.fail("修改餐厅失败");
    }
    
    /**
     * 删除餐厅
     */
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable String[] ids) {
        return restaurantService.deleteRestaurantByIds(ids) ? Result.success() : Result.fail("删除餐厅失败");
    }
} 