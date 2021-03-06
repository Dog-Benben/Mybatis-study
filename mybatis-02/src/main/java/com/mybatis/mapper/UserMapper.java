package com.mybatis.mapper;

import com.mybatis.pojo.User;

import java.util.List;
import java.util.Map;

/**
 * @author Orange
 * @create 2021-07-28  18:30
 */
//操作实体类的接口
public interface UserMapper {
    //查询全部用户
    List<User> getUserList();

    //根据id查询用户
    User getUserById(int id);

    //insert一个用户
    int addUser(User user);

    //修改用户
    int updateUser(User user);

    //删除一个用户
    int deleteUser(int id);


}
