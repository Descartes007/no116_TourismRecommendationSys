package com.shanzhu.tourism.controller.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanzhu.tourism.domain.Result;
import com.shanzhu.tourism.domain.SysLine;
import com.shanzhu.tourism.domain.SysLineOrder;
import com.shanzhu.tourism.enums.ResultCode;
import com.shanzhu.tourism.service.SysLineOrderService;
import com.shanzhu.tourism.service.SysLineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 旅游线路订单controller
 *
 * @author: ShanZhu
 * @date: 2025-03-01
 */
@Controller
@ResponseBody
@RequestMapping("lineOrder")
public class SysLineOrderController {

    @Resource
    private SysLineOrderService sysLineOrderService;
    
    @Resource
    private SysLineService sysLineService;

    /**
     * 分页获取旅游线路订单
     */
    @PostMapping("getSysLineOrderPage")
    public Result getSysLineOrderPage(@RequestBody SysLineOrder sysLineOrder) {
        // 添加日志
        System.out.println("查询线路订单参数: " + sysLineOrder);
        
        Page<SysLineOrder> page = new Page<>(sysLineOrder.getPageNumber(), sysLineOrder.getPageSize());
        QueryWrapper<SysLineOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(sysLineOrder.getName()), SysLineOrder::getName, sysLineOrder.getName())
                .eq(StringUtils.isNotBlank(sysLineOrder.getUserId()), SysLineOrder::getUserId, sysLineOrder.getUserId())
                .eq(sysLineOrder.getState() != null, SysLineOrder::getState, sysLineOrder.getState())
                .orderByDesc(SysLineOrder::getCreateTime);
        
        // 添加日志
        System.out.println("查询条件: userId=" + sysLineOrder.getUserId());
        
        Page<SysLineOrder> sysLineOrderPage = sysLineOrderService.page(page, queryWrapper);
        
        // 添加日志
        System.out.println("查询结果数量: " + sysLineOrderPage.getRecords().size());
        
        return Result.success(sysLineOrderPage);
    }

    /**
     * 根据id获取旅游线路订单
     */
    @GetMapping("getSysLineOrderById")
    public Result getSysLineOrderById(@RequestParam("id") String id) {
        SysLineOrder sysLineOrder = sysLineOrderService.getById(id);
        return Result.success(sysLineOrder);
    }

    /**
     * 创建旅游线路订单
     */
    @PostMapping("saveSysLineOrder")
    @Transactional(rollbackFor = Exception.class)
    public Result saveSysLineOrder(@RequestBody SysLineOrder sysLineOrder) {
        // 打印接收到的参数
        System.out.println("创建订单接收参数: " + sysLineOrder);
        
        // 1. 验证线路信息
        SysLine sysLine = sysLineService.getById(sysLineOrder.getLineId());
        if (sysLine == null) {
            System.out.println("线路不存在: " + sysLineOrder.getLineId());
            return Result.fail("旅游线路不存在");
        }
        if (sysLine.getState() != 1) {
            System.out.println("线路已停用: " + sysLine.getState());
            return Result.fail("该旅游线路已停用，无法预订");
        }
        
        // 2. 设置订单信息
        sysLineOrder.setName(sysLine.getName());
        sysLineOrder.setIntroduce(sysLine.getIntroduce());
        sysLineOrder.setImages(sysLine.getImages());
        sysLineOrder.setPrice(sysLine.getPrice());
        
        // 3. 计算总价
        if (sysLineOrder.getNum() != null && sysLineOrder.getPrice() != null) {
            sysLineOrder.setTotalPrice(sysLineOrder.getNum() * sysLineOrder.getPrice());
        }
        
        // 4. 设置初始状态为待支付
        sysLineOrder.setState(0);
        
        // 检查用户ID是否设置
        if (StringUtils.isBlank(sysLineOrder.getUserId())) {
            System.out.println("警告: 用户ID为空!");
        } else {
            System.out.println("订单关联用户ID: " + sysLineOrder.getUserId());
        }
        
        System.out.println("准备保存订单: " + sysLineOrder);
        boolean save = sysLineOrderService.save(sysLineOrder);
        System.out.println("订单保存结果: " + save);
        
        if (save) {
            System.out.println("订单保存成功: " + sysLineOrder.getId());
            return Result.success(sysLineOrder);
        } else {
            System.out.println("订单保存失败");
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /**
     * 更新旅游线路订单状态
     */
    @PostMapping("updateSysLineOrderState")
    public Result updateSysLineOrderState(@RequestParam("id") String id, @RequestParam("state") Integer state) {
        if (StringUtils.isBlank(id)) {
            return Result.fail("订单id不能为空");
        }
        
        SysLineOrder sysLineOrder = sysLineOrderService.getById(id);
        if (sysLineOrder == null) {
            return Result.fail("订单不存在");
        }
        
        // 状态检验逻辑，例如只能从未支付变为已支付，或从已支付变为已出行等
        if (state == 1 && sysLineOrder.getState() != 0) {
            return Result.fail("订单状态错误，只有待支付状态才能变更为已支付");
        }
        if (state == 2 && sysLineOrder.getState() != 1) {
            return Result.fail("订单状态错误，只有已支付状态才能变更为已出行");
        }
        if (state == 4 && sysLineOrder.getState() != 2) {
            return Result.fail("订单状态错误，只有已出行状态才能变更为已完成");
        }
        
        sysLineOrder.setState(state);
        sysLineOrder.setUpdateTime(new Date());
        
        boolean updated = sysLineOrderService.updateById(sysLineOrder);
        if (updated) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /**
     * 取消旅游线路订单
     */
    @PostMapping("cancelSysLineOrder")
    public Result cancelSysLineOrder(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            return Result.fail("订单id不能为空");
        }
        
        SysLineOrder sysLineOrder = sysLineOrderService.getById(id);
        if (sysLineOrder == null) {
            return Result.fail("订单不存在");
        }
        
        // 已出行或已完成的订单不能取消
        if (sysLineOrder.getState() == 2 || sysLineOrder.getState() == 4) {
            return Result.fail("已出行或已完成的订单不能取消");
        }
        
        sysLineOrder.setState(3); // 已取消
        sysLineOrder.setUpdateTime(new Date());
        
        boolean updated = sysLineOrderService.updateById(sysLineOrder);
        if (updated) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /**
     * 获取所有订单（用于调试）
     */
    @GetMapping("getAllOrders")
    public Result getAllOrders() {
        System.out.println("查询所有订单记录");
        QueryWrapper<SysLineOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(SysLineOrder::getCreateTime);
        
        List<SysLineOrder> list = sysLineOrderService.list(queryWrapper);
        System.out.println("查询到订单总数: " + list.size());
        for (SysLineOrder order : list) {
            System.out.println("订单ID: " + order.getId() + ", 用户ID: " + order.getUserId());
        }
        
        return Result.success(list);
    }
} 