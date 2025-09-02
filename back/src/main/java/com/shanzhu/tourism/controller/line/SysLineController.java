package com.shanzhu.tourism.controller.line;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanzhu.tourism.domain.Result;
import com.shanzhu.tourism.domain.SysFavor;
import com.shanzhu.tourism.domain.SysLine;
import com.shanzhu.tourism.enums.ResultCode;
import com.shanzhu.tourism.service.SysFavorService;
import com.shanzhu.tourism.service.SysLineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 旅游线路controller
 *
 * @author: ShanZhu
 * @date: 2024-08-10
 */
@Controller
@ResponseBody
@RequestMapping("line")
public class SysLineController {

    @Resource
    private SysLineService sysLineService;
    @Resource
    private SysFavorService sysFavorService;

    /** 分页获取旅游线路 */
    @PostMapping("getSysLinePage")
    public Result getSysLinePage(@RequestBody SysLine sysLine) {
        System.out.println("接收到分页查询请求，参数：" + sysLine);
        
        try {
            // 如果页码参数为空，设置默认值
            if (sysLine.getPageNumber() == null || sysLine.getPageNumber() < 1) {
                sysLine.setPageNumber(1);
                System.out.println("页码参数无效，设置为默认值1");
            }
            if (sysLine.getPageSize() == null || sysLine.getPageSize() < 1) {
                sysLine.setPageSize(10);
                System.out.println("每页条数参数无效，设置为默认值10");
            }
            
            // 修复价格区间查询问题
            if (sysLine.getMinPrice() != null && sysLine.getMinPrice() == 0) {
                sysLine.setMinPrice(null);
                System.out.println("最小价格为0，设置为null以避免过滤");
            }
            
            if (sysLine.getMaxPrice() != null && sysLine.getMaxPrice() == 0) {
                sysLine.setMaxPrice(null);
                System.out.println("最大价格为0，设置为null以避免过滤");
            }
            
            // 创建分页对象
            Page<SysLine> page = new Page<>(sysLine.getPageNumber(), sysLine.getPageSize());
            
            // 检查数据库中是否有数据
            long totalCount = sysLineService.count();
            System.out.println("数据库中旅游线路总数: " + totalCount);
            
            if (totalCount == 0) {
                System.out.println("数据库中没有旅游线路数据，尝试创建一条测试数据");
                
                // 如果没有数据，创建一条测试数据
                SysLine testLine = new SysLine();
                testLine.setName("测试旅游线路");
                testLine.setGeography("北京");
                testLine.setDuration("3天2晚");
                testLine.setPrice(1999.0f);
                testLine.setIntroduce("这是一个测试线路");
                testLine.setAttractions("故宫,长城,颐和园");
                testLine.setSchedule("第一天:故宫,第二天:长城,第三天:颐和园");
                testLine.setState(1); // 启用状态
                
                boolean saved = sysLineService.save(testLine);
                System.out.println("测试数据创建" + (saved ? "成功" : "失败"));
            }
            
            // 构建查询条件，包含所有搜索参数
        QueryWrapper<SysLine> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                    .like(StringUtils.isNotBlank(sysLine.getName()), SysLine::getName, sysLine.getName())
                    .like(StringUtils.isNotBlank(sysLine.getGeography()), SysLine::getGeography, sysLine.getGeography())
                    .like(StringUtils.isNotBlank(sysLine.getDuration()), SysLine::getDuration, sysLine.getDuration())
                    .like(StringUtils.isNotBlank(sysLine.getAttractions()), SysLine::getAttractions, sysLine.getAttractions())
                    .ge(sysLine.getMinPrice() != null && sysLine.getMinPrice() > 0, SysLine::getPrice, sysLine.getMinPrice())
                    .le(sysLine.getMaxPrice() != null && sysLine.getMaxPrice() > 0, SysLine::getPrice, sysLine.getMaxPrice())
                    .eq(sysLine.getUpdateTime() != null, SysLine::getUpdateTime, sysLine.getUpdateTime())
                    .eq(sysLine.getTemperature() != null, SysLine::getTemperature, sysLine.getTemperature())
                    .eq(sysLine.getState() != null, SysLine::getState, sysLine.getState())
                .orderByDesc(SysLine::getCreateTime);
            
            System.out.println("查询条件构建完成，准备查询数据库");
        Page<SysLine> sysLinePage = sysLineService.page(page, queryWrapper);
            System.out.println("查询结果：总记录数=" + sysLinePage.getTotal() + ", 当前页数据条数=" + sysLinePage.getRecords().size());
            
            if (sysLinePage.getRecords().isEmpty()) {
                System.out.println("警告：查询结果为空，这可能是由于查询条件过滤掉了所有数据");
                // 记录查询条件详情
                System.out.println("查询条件详情：");
                System.out.println("name=" + sysLine.getName());
                System.out.println("geography=" + sysLine.getGeography());
                System.out.println("duration=" + sysLine.getDuration());
                System.out.println("minPrice=" + sysLine.getMinPrice());
                System.out.println("maxPrice=" + sysLine.getMaxPrice());
                System.out.println("state=" + sysLine.getState());
            } else {
                System.out.println("查询成功，返回数据");
            }
            
        return Result.success(sysLinePage);
        } catch (Exception e) {
            System.err.println("查询线路数据异常：" + e.getMessage());
            e.printStackTrace();
            return Result.fail("查询线路数据失败：" + e.getMessage());
        }
    }

    @GetMapping("/getSysLineIndex")
    public Result getSysLineIndex(@RequestParam(value = "limit", required = false, defaultValue = "4") Integer limit) {
        QueryWrapper<SysLine> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysLine::getState, 1) // 只查询启用状态
                .orderByDesc(SysLine::getCreateTime)
                .last("limit " + limit);
        List<SysLine> lineList = sysLineService.list(queryWrapper);
        return Result.success(lineList);
    }

    /** 根据id获取旅游线路 */
    @GetMapping("getSysLineById")
    public Result getSysLineById(@RequestParam("id")String id) {
        SysLine sysLine = sysLineService.getById(id);
        return Result.success(sysLine);
    }

    /** 保存旅游线路 */
    @PostMapping("saveSysLine")
    public Result saveSysLine(@RequestBody SysLine sysLine) {
        System.out.println("接收到保存线路请求，参数：" + sysLine);
        
        try {
            // 设置默认状态为启用
            if (sysLine.getState() == null) {
                sysLine.setState(1);
            }
            
        boolean save = sysLineService.save(sysLine);
        if (save) {
                System.out.println("保存线路成功，ID：" + sysLine.getId());
            return Result.success();
        } else {
                System.out.println("保存线路失败");
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
            }
        } catch (Exception e) {
            System.err.println("保存线路异常：" + e.getMessage());
            e.printStackTrace();
            return Result.fail("保存线路失败：" + e.getMessage());
        }
    }

    /** 编辑旅游线路 */
    @PostMapping("editSysLine")
    public Result editSysLine(@RequestBody SysLine sysLine) {
        boolean save = sysLineService.updateById(sysLine);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除旅游线路 */
    @GetMapping("removeSysLine")
    @Transactional(rollbackFor = Exception.class)
    public Result removeSysLine(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                sysLineService.removeById(id);
                QueryWrapper<SysFavor> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(SysFavor::getLineId,id);
                sysFavorService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("旅游线路id不能为空！");
        }
    }

    /** 启用/禁用旅游线路 */
    @GetMapping("toggleSysLineState")
    public Result toggleSysLineState(@RequestParam("id") String id, @RequestParam("state") Integer state) {
        if (StringUtils.isBlank(id)) {
            return Result.fail("旅游线路id不能为空！");
        }
        SysLine sysLine = sysLineService.getById(id);
        if (sysLine == null) {
            return Result.fail("旅游线路不存在！");
        }
        sysLine.setState(state);
        boolean updated = sysLineService.updateById(sysLine);
        if (updated) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }
}
