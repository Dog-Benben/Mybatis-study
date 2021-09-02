package com.mybatis.mapper;

import com.mybatis.pojo.User;

import java.util.List;

/**
 * @author Orange
 * @create 2021-07-28  18:30
 */
//操作实体类的接口
public interface UserMapper {

    //根据id查询用户
    User getUserById(int id);

}
