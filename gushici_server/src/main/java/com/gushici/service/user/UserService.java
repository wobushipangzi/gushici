package com.gushici.service.user;

import com.gushici.bean.user.User;
import com.gushici.common.result.GlobalResult;

public interface UserService {

    /**
     * 将用户信息存入数据库
     */
    GlobalResult saveUser(User user);

    /**
     * 根据openId修改用户信息
     */
    GlobalResult updateUser(User user);

    /**
     * 查询用户
     */
    User selectUserOne(String openId);
}
