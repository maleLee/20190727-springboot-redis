package com.aaa.lee.springboot.service;

import com.aaa.lee.springboot.mapper.UserMapper;
import com.aaa.lee.springboot.model.User;
import com.aaa.lee.springboot.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Company AAA软件教育
 * @Author Seven Lee
 * @Date Create in 2019/7/27 9:23
 * @Description
 *      对用户的业务逻辑处理类
 **/
@Service
public class UserService {

    @Value("${user_redis_key}")
    private String users;
    @Value("${spring.redis.expire}")
    private Integer expireTime;

    @Autowired
    private UserMapper userMapper;

    /**
     * @author Seven Lee
     * @description
     *      查询所有的用户信息，并且存入缓存库中
     * @param
     * @date 2019/7/27
     * @return java.util.List<com.aaa.lee.springboot.model.User>
     * @throws
     **/
    public Map<String, Object> selectAllUsers(RedisService redisService) {
        String resutlSet = "";
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 1.从redis中查询用户信息(不允许出现任何一行和硬编码有关的代码)
        // 在当前的service中，必须要使用其他的service，需要从controller作为形式参数传递
        String userJson = redisService.get(users);
        // 2.判断userJson数据是否为null
            // 如果存在，说明redis中有数据，可以直接使用
            // 如果为null,说明redis并没有缓存数据，需要注入
        if ("".equals(userJson) || null == userJson) {
            // 需要从mysql中查询数据并且存入redis的缓存库中
            List<User> userList = userMapper.selectAll();
            // 3.再次判断从mysql中查询的数据是否存在
            if(userList.size() > 0) {
                // 说明mysql中有数据
                // 4.把数据存入到redis缓存库中
                try {
                    resutlSet = redisService.set(users, JSONUtil.toJsonString(userList));
                } catch (Exception e) {
                    // 如果进入catch，则再次连接一遍
                    resutlSet = redisService.set(users, JSONUtil.toJsonString(userList));
                    e.printStackTrace();
                }
                // 5.判断redis缓存库是否存储成功
                if("OK".equals(resutlSet.toUpperCase())) {
                    // 说明存储成功，可以返回数据
                    redisService.expire(users, expireTime);
                    resultMap.put("code", 200);
                    resultMap.put("data", userList);
                } else {
                    // 跳转错误页面
                    resultMap.put("code", 500);
                }

            } else {
                // 说明mysql数据库中并没有数据，跳转错误页面，并且自己定义状态码返回(最好使用枚举!!!!!!)
                resultMap.put("code", 404);
            }
        } else {
            // 说明缓存库中有数据，可以直接返回
            resultMap.put("code", 200);
            resultMap.put("data", JSONUtil.toList(userJson, User.class));
        }

        return resultMap;
    }

}
