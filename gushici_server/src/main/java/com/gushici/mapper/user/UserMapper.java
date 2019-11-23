package com.gushici.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gushici.bean.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
