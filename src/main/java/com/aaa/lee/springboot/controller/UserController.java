package com.aaa.lee.springboot.controller;

import com.aaa.lee.springboot.model.User;
import com.aaa.lee.springboot.service.RedisService;
import com.aaa.lee.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Company AAA软件教育
 * @Author Seven Lee
 * @Date Create in 2019/7/27 9:25
 * @Description
 *      用户的控制器
 **/
@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    /**
     * @author Seven Lee
     * @description
     *      查询所有的用户信息
     * @param
     * @date 2019/7/27
     * @return java.util.List<com.aaa.lee.springboot.model.User>
     * @throws
     **/
    @RequestMapping("/all")
    public List<User> selectAllUsers() {
        Map<String, Object> resultMap = userService.selectAllUsers(redisService);
        Integer code = (Integer)resultMap.get("code");
        if(200 == code) {
            // 说明已经搞定了
            List<User> userList = (List<User>)resultMap.get("data");
            return userList;
        } else if(404 == code) {
            // 跳转到错误页面进行处理
            return null;
        } else if (500 == code) {
            // 跳转错误页面进行处理
            return null;
        } else {
            String msg = "暂时无法解决，请于管理员联系！";
            // 直接添加一句话，跳转到页面
            return null;
        }
    }

}
