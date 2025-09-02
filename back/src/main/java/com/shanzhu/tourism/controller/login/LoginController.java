package com.shanzhu.tourism.controller.login;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shanzhu.tourism.domain.*;
import com.shanzhu.tourism.service.*;
import com.shanzhu.tourism.service.SysAttractionOrderService;
import com.shanzhu.tourism.service.SysHotelOrderService;
import com.shanzhu.tourism.service.UserService;
import com.shanzhu.tourism.utils.JwtUtil;
import com.shanzhu.tourism.utils.PasswordUtils;
import com.shanzhu.tourism.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 登陆
 *
 * @author: ShanZhu
 * @date: 2024-08-10
 */
@Controller
@ResponseBody
@RequestMapping("login")
public class LoginController {

    @Resource
    private UserService userService;
    @Resource
    private SysHotelOrderService sysHotelOrderService;
    @Resource
    private SysAttractionOrderService sysAttractionOrderService;
    @Resource
    private SysAttractionsService sysAttractionsService;
    @Resource
    private SysHotelService sysHotelService;

    @PostMapping()
    public Result login(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("loginAccount");
        String password = jsonObject.getString("password");
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getLoginAccount,username);
        User user = userService.getOne(query);
        if (user == null) {
            return Result.fail("用户名不存在！");
        }
        //比较加密后得密码
        boolean decrypt = PasswordUtils.decrypt(password, user.getPassword() + "$" + user.getSalt());
        if (!decrypt) {
            return Result.fail("用户名或密码错误！");
        }
        if (user.getStatus() == 1) {
            return Result.fail("用户被禁用！");
        }
        
        // 检查用户类型和客户端类型
        String clientType = jsonObject.getString("clientType");
        if ("user".equals(clientType) && user.getUserType() == 0) {
            return Result.fail("管理员账号不能登录用户端！");
        } else if ("admin".equals(clientType) && user.getUserType() == 1) {
            return Result.fail("普通用户不能登录管理员端！");
        }
        
        //密码正确生成token返回
        String token = JwtUtil.sign(user.getId(), user.getPassword());
        JSONObject json = new JSONObject();
        json.put("token", token);
        return Result.success(json);
    }

    @GetMapping("logout")
    public Result logout() {
        return Result.success();
    }

    @GetMapping("getManageData")
    public Result getManageData() {
        JSONObject jsonObject = new JSONObject();

        // 获取景点总数
        QueryWrapper<SysAttractions> attractionsWrapper = new QueryWrapper<>();
        int attractionsCount = sysAttractionsService.count(attractionsWrapper);
        jsonObject.put("attractionsCount", attractionsCount);

        // 获取用户总数
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        int userCount = userService.count(userWrapper);
        jsonObject.put("userCount", userCount);

        // 获取今日预约数量
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // 今日景点预约
        QueryWrapper<SysAttractionOrder> todayOrderWrapper = new QueryWrapper<>();
        todayOrderWrapper.lambda()
            .ge(SysAttractionOrder::getCreateTime, today + " 00:00:00")
            .le(SysAttractionOrder::getCreateTime, today + " 23:59:59");
        int attractionOrderCount = sysAttractionOrderService.count(todayOrderWrapper);
        
        // 今日酒店预约
        QueryWrapper<SysHotelOrder> todayHotelOrderWrapper = new QueryWrapper<>();
        todayHotelOrderWrapper.lambda()
            .ge(SysHotelOrder::getCreateTime, today + " 00:00:00")
            .le(SysHotelOrder::getCreateTime, today + " 23:59:59");
        int hotelOrderCount = sysHotelOrderService.count(todayHotelOrderWrapper);
        
        // 今日总预约 = 景点预约 + 酒店预约
        int todayOrderCount = attractionOrderCount + hotelOrderCount;
        jsonObject.put("todayOrderCount", todayOrderCount);

        // 获取酒店总数
        QueryWrapper<SysHotel> hotelWrapper = new QueryWrapper<>();
        int hotelCount = sysHotelService.count(hotelWrapper);
        jsonObject.put("hotelCount", hotelCount);

        // 获取最近七日数据
        List<String> dates = new ArrayList<>();
        List<Integer> nums = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) {
            Date date = calendar.getTime();
            String formattedDate = dateFormat.format(date);
            // 最近七日景点预约
            QueryWrapper<SysAttractionOrder> wrapper = new QueryWrapper<>();
            wrapper.lambda().ge(SysAttractionOrder::getCreateTime, formattedDate + " 00:00:00")
                    .le(SysAttractionOrder::getCreateTime, formattedDate + " 23:59:59");
            nums.add(sysAttractionOrderService.count(wrapper));
            // 最近七日酒店预约
            QueryWrapper<SysHotelOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().ge(SysHotelOrder::getCreateTime, formattedDate + " 00:00:00")
                    .le(SysHotelOrder::getCreateTime, formattedDate + " 23:59:59");
            orders.add(sysHotelOrderService.count(queryWrapper));
            dates.add(formattedDate);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        jsonObject.put("dates", dates);
        jsonObject.put("nums", nums);
        jsonObject.put("orders", orders);
        return Result.success(jsonObject);
    }

}
