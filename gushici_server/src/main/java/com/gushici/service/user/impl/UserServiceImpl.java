package com.gushici.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.user.User;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.mapper.user.UserMapper;
import com.gushici.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService{
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 将用户信息存入数据库
     * @param user
     * @return
     */
    @Override
    @Transactional
    public GlobalResult saveUser(User user) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            int insert = userMapper.insert(user);
            if(insert <= 0){
                logger.error("用户信息入库失败");
                throw new GlobalException(ResultCode.数据库新增数据失败.getCode(), ResultCode.数据库新增数据失败.getMsg());
            }
        }catch (Exception e){
            logger.error("用户信息入库失败", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }

    /**
     * 根据openId修改用户信息
     * @return
     */
    @Override
    @Transactional
    public GlobalResult updateUser(User user) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("open_id",user.getOpenId());
            int update = userMapper.update(user, userQueryWrapper);
            if(update <= 0){
                logger.error("更新数据库失败");
                throw new GlobalException(ResultCode.更新数据库失败.getCode(), ResultCode.更新数据库失败.getMsg());
            }
        }catch (Exception e){
           logger.error("根据openId修改用户信息接口异常", e);
           throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
        }
        return globalResult;
    }

    /**
     * 查询用户
     * @return
     */
    @Override
    public User selectUserOne(String openId) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("open_id",openId);
        return userMapper.selectOne(userQueryWrapper);
    }
}
