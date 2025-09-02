package com.shanzhu.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shanzhu.tourism.domain.SysDish;
import com.shanzhu.tourism.domain.SysRestaurant;
import com.shanzhu.tourism.mapper.SysDishMapper;
import com.shanzhu.tourism.mapper.SysRestaurantMapper;
import com.shanzhu.tourism.service.ISysRestaurantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 餐厅信息Service实现
 *
 * @author tourism
 */
@Service
public class SysRestaurantServiceImpl extends ServiceImpl<SysRestaurantMapper, SysRestaurant> implements ISysRestaurantService {

    @Autowired
    private SysRestaurantMapper restaurantMapper;
    
    @Autowired
    private SysDishMapper dishMapper;

    /**
     * 查询餐厅信息列表
     *
     * @param restaurant 餐厅信息
     * @return 餐厅信息
     */
    @Override
    public List<SysRestaurant> selectRestaurantList(SysRestaurant restaurant) {
        LambdaQueryWrapper<SysRestaurant> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加条件查询
        if (StringUtils.isNotBlank(restaurant.getName())) {
            queryWrapper.like(SysRestaurant::getName, restaurant.getName());
        }
        
        if (StringUtils.isNotBlank(restaurant.getRegion())) {
            queryWrapper.eq(SysRestaurant::getRegion, restaurant.getRegion());
        }
        
        if (StringUtils.isNotBlank(restaurant.getCuisineType())) {
            queryWrapper.eq(SysRestaurant::getCuisineType, restaurant.getCuisineType());
        }
        
        if (restaurant.getPriceLevel() != null) {
            queryWrapper.eq(SysRestaurant::getPriceLevel, restaurant.getPriceLevel());
        }
        
        if (restaurant.getState() != null) {
            queryWrapper.eq(SysRestaurant::getState, restaurant.getState());
        }
        
        return restaurantMapper.selectList(queryWrapper);
    }

    /**
     * 根据ID查询餐厅详细信息
     *
     * @param id 餐厅ID
     * @return 餐厅信息
     */
    @Override
    public SysRestaurant selectRestaurantById(String id) {
        SysRestaurant restaurant = restaurantMapper.selectById(id);
        if (restaurant != null) {
            // 查询该餐厅的菜品列表
            LambdaQueryWrapper<SysDish> dishQueryWrapper = new LambdaQueryWrapper<>();
            dishQueryWrapper.eq(SysDish::getRestaurantId, id);
            List<SysDish> dishes = dishMapper.selectList(dishQueryWrapper);
            restaurant.setDishes(dishes);
        }
        return restaurant;
    }

    /**
     * 新增餐厅信息
     *
     * @param restaurant 餐厅信息
     * @return 结果
     */
    @Override
    public boolean insertRestaurant(SysRestaurant restaurant) {
        restaurant.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        return restaurantMapper.insert(restaurant) > 0;
    }

    /**
     * 修改餐厅信息
     *
     * @param restaurant 餐厅信息
     * @return 结果
     */
    @Override
    public boolean updateRestaurant(SysRestaurant restaurant) {
        return restaurantMapper.updateById(restaurant) > 0;
    }

    /**
     * 批量删除餐厅信息
     *
     * @param ids 需要删除的餐厅ID
     * @return 结果
     */
    @Override
    public boolean deleteRestaurantByIds(String[] ids) {
        // 先删除关联的菜品信息
        LambdaQueryWrapper<SysDish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.in(SysDish::getRestaurantId, Arrays.asList(ids));
        dishMapper.delete(dishQueryWrapper);
        
        // 再删除餐厅信息
        return restaurantMapper.deleteBatchIds(Arrays.asList(ids)) > 0;
    }

    /**
     * 删除餐厅信息信息
     *
     * @param id 餐厅ID
     * @return 结果
     */
    @Override
    public boolean deleteRestaurantById(String id) {
        // 先删除关联的菜品信息
        LambdaQueryWrapper<SysDish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(SysDish::getRestaurantId, id);
        dishMapper.delete(dishQueryWrapper);
        
        // 再删除餐厅信息
        return restaurantMapper.deleteById(id) > 0;
    }
} 