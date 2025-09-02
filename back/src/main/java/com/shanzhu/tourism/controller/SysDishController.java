package com.shanzhu.tourism.controller;

import com.shanzhu.tourism.domain.Result;
import com.shanzhu.tourism.domain.SysDish;
import com.shanzhu.tourism.service.ISysDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 菜品信息 控制器
 *
 * @author tourism
 */
@RestController
@RequestMapping("/dish")
public class SysDishController {
    
    @Autowired
    private ISysDishService dishService;
    
    /**
     * 获取菜品列表
     */
    @GetMapping("/list")
    public Result list(SysDish dish) {
        List<SysDish> list = dishService.selectDishList(dish);
        return Result.success(list);
    }
    
    /**
     * 获取菜品详情
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable("id") String id) {
        return Result.success(dishService.selectDishById(id));
    }
    
    /**
     * 根据餐厅ID获取菜品列表
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Result getDishesByRestaurantId(@PathVariable("restaurantId") String restaurantId) {
        List<SysDish> dishes = dishService.selectDishesByRestaurantId(restaurantId);
        return Result.success(dishes);
    }
    
    /**
     * 新增菜品
     */
    @PostMapping
    public Result add(@RequestBody SysDish dish) {
        dish.setCreateTime(new Date());
        dish.setUpdateTime(new Date());
        return dishService.insertDish(dish) ? Result.success() : Result.fail("新增菜品失败");
    }
    
    /**
     * 修改菜品
     */
    @PutMapping
    public Result edit(@RequestBody SysDish dish) {
        dish.setUpdateTime(new Date());
        return dishService.updateDish(dish) ? Result.success() : Result.fail("修改菜品失败");
    }
    
    /**
     * 删除菜品
     */
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable String[] ids) {
        return dishService.deleteDishByIds(ids) ? Result.success() : Result.fail("删除菜品失败");
    }
} 