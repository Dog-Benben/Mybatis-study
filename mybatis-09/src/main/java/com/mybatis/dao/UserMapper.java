package com.mybatis.dao;

import com.mybatis.pojo.User;
import org.apache.ibatis.annotations.Param;

/**
 * @author Orange
 * @create 2021-08-19  0:05
 */
public interface UserMapper {

    //查询一个用户
    User queryUserById(@Param("id") int id);

    //修改用户
    int updateUser(User user);
}
