package com.shanzhu.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shanzhu.tourism.domain.SysDish;
import com.shanzhu.tourism.domain.SysRestaurant;
import com.shanzhu.tourism.mapper.SysDishMapper;
import com.shanzhu.tourism.mapper.SysRestaurantMapper;
import com.shanzhu.tourism.service.ISysDishService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 菜品信息Service实现
 *
 * @author tourism
 */
@Service
public class SysDishServiceImpl extends ServiceImpl<SysDishMapper, SysDish> implements ISysDishService {

    @Autowired
    private SysDishMapper dishMapper;
    
    @Autowired
    private SysRestaurantMapper restaurantMapper;

    /**
     * 查询菜品信息列表
     *
     * @param dish 菜品信息
     * @return 菜品信息
     */
    @Override
    public List<SysDish> selectDishList(SysDish dish) {
        LambdaQueryWrapper<SysDish> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加条件查询
        if (StringUtils.isNotBlank(dish.getName())) {
            queryWrapper.like(SysDish::getName, dish.getName());
        }
        
        if (StringUtils.isNotBlank(dish.getRestaurantId())) {
            queryWrapper.eq(SysDish::getRestaurantId, dish.getRestaurantId());
        }
        
        if (StringUtils.isNotBlank(dish.getDishType())) {
            queryWrapper.eq(SysDish::getDishType, dish.getDishType());
        }
        
        if (dish.getIsSpecial() != null) {
            queryWrapper.eq(SysDish::getIsSpecial, dish.getIsSpecial());
        }
        
        List<SysDish> dishes = dishMapper.selectList(queryWrapper);
        
        // 填充所属餐厅名称
        for (SysDish item : dishes) {
            SysRestaurant restaurant = restaurantMapper.selectById(item.getRestaurantId());
            if (restaurant != null) {
                item.setRestaurantName(restaurant.getName());
            }
        }
        
        return dishes;
    }

    /**
     * 根据ID查询菜品详细信息
     *
     * @param id 菜品ID
     * @return 菜品信息
     */
    @Override
    public SysDish selectDishById(String id) {
        SysDish dish = dishMapper.selectById(id);
        if (dish != null && StringUtils.isNotBlank(dish.getRestaurantId())) {
            SysRestaurant restaurant = restaurantMapper.selectById(dish.getRestaurantId());
            if (restaurant != null) {
                dish.setRestaurantName(restaurant.getName());
            }
        }
        return dish;
    }
    
    /**
     * 根据餐厅ID查询菜品列表
     *
     * @param restaurantId 餐厅ID
     * @return 菜品列表
     */
    @Override
    public List<SysDish> selectDishesByRestaurantId(String restaurantId) {
        LambdaQueryWrapper<SysDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDish::getRestaurantId, restaurantId);
        return dishMapper.selectList(queryWrapper);
    }

    /**
     * 新增菜品信息
     *
     * @param dish 菜品信息
     * @return 结果
     */
    @Override
    public boolean insertDish(SysDish dish) {
        dish.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        return dishMapper.insert(dish) > 0;
    }

    /**
     * 修改菜品信息
     *
     * @param dish 菜品信息
     * @return 结果
     */
    @Override
    public boolean updateDish(SysDish dish) {
        return dishMapper.updateById(dish) > 0;
    }

    /**
     * 批量删除菜品信息
     *
     * @param ids 需要删除的菜品ID
     * @return 结果
     */
    @Override
    public boolean deleteDishByIds(String[] ids) {
        return dishMapper.deleteBatchIds(Arrays.asList(ids)) > 0;
    }

    /**
     * 删除菜品信息信息
     *
     * @param id 菜品ID
     * @return 结果
     */
    @Override
    public boolean deleteDishById(String id) {
        return dishMapper.deleteById(id) > 0;
    }
} 