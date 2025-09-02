package com.shanzhu.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shanzhu.tourism.domain.SysRestaurant;

import java.util.List;

/**
 * 餐厅信息Service接口
 *
 * @author tourism
 */
public interface ISysRestaurantService extends IService<SysRestaurant> {
    
    /**
     * 查询餐厅信息列表
     *
     * @param restaurant 餐厅信息
     * @return 餐厅信息集合
     */
    List<SysRestaurant> selectRestaurantList(SysRestaurant restaurant);
    
    /**
     * 根据ID查询餐厅详细信息（包含菜品列表）
     *
     * @param id 餐厅ID
     * @return 餐厅信息
     */
    SysRestaurant selectRestaurantById(String id);
    
    /**
     * 新增餐厅信息
     *
     * @param restaurant 餐厅信息
     * @return 结果
     */
    boolean insertRestaurant(SysRestaurant restaurant);
    
    /**
     * 修改餐厅信息
     *
     * @param restaurant 餐厅信息
     * @return 结果
     */
    boolean updateRestaurant(SysRestaurant restaurant);
    
    /**
     * 批量删除餐厅信息
     *
     * @param ids 需要删除的餐厅ID
     * @return 结果
     */
    boolean deleteRestaurantByIds(String[] ids);
    
    /**
     * 删除餐厅信息信息
     *
     * @param id 餐厅ID
     * @return 结果
     */
    boolean deleteRestaurantById(String id);
} 