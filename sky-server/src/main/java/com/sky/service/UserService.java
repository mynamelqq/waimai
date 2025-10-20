package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.User;

import java.io.IOException;

public interface UserService extends IService<User> {
    User wxLogin(String code) throws IOException;
}
