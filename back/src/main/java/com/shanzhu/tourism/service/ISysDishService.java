package com.shanzhu.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shanzhu.tourism.domain.SysDish;

import java.util.List;

/**
 * 菜品信息Service接口
 *
 * @author tourism
 */
public interface ISysDishService extends IService<SysDish> {
    
    /**
     * 查询菜品信息列表
     *
     * @param dish 菜品信息
     * @return 菜品信息集合
     */
    List<SysDish> selectDishList(SysDish dish);
    
    /**
     * 根据ID查询菜品详细信息
     *
     * @param id 菜品ID
     * @return 菜品信息
     */
    SysDish selectDishById(String id);
    
    /**
     * 根据餐厅ID查询菜品列表
     *
     * @param restaurantId 餐厅ID
     * @return 菜品列表
     */
    List<SysDish> selectDishesByRestaurantId(String restaurantId);
    
    /**
     * 新增菜品信息
     *
     * @param dish 菜品信息
     * @return 结果
     */
    boolean insertDish(SysDish dish);
    
    /**
     * 修改菜品信息
     *
     * @param dish 菜品信息
     * @return 结果
     */
    boolean updateDish(SysDish dish);
    
    /**
     * 批量删除菜品信息
     *
     * @param ids 需要删除的菜品ID
     * @return 结果
     */
    boolean deleteDishByIds(String[] ids);
    
    /**
     * 删除菜品信息信息
     *
     * @param id 菜品ID
     * @return 结果
     */
    boolean deleteDishById(String id);
} 