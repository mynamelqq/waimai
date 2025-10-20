package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User wxLogin(String code) throws IOException {
        String appid = weChatProperties.getAppid();
        String secret = weChatProperties.getSecret();
        Map<String, String> params = new HashMap<String, String>();
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");
        params.put("appid", appid);
        ;
        params.put("secret", secret);
        String s = HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session", params);
        JSONObject jsonObject = JSON.parseObject(s);
        String openid = jsonObject.getString("openid");
        if (openid == null) throw new LoginFailedException("登录失败");
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        if (user == null) {
            user = User.builder().createTime(LocalDateTime.now()).openid(openid).build();
            userMapper.insert(user);
        }


        return user;
    }
}
