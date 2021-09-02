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

    //模糊查询
    List<User> getUserLike(String name);

    //根据id查询用户
    User getUserById(int id);

    //使用map集合根据id查询用户
    User getUserById2(Map<String,Object> map);

    //insert一个用户
    int addUser(User user);

    //使用万能map集合添加一个用户
    int addUserMap(Map<String,Object> map);

    //修改用户
    int updateUser(User user);

    //删除一个用户
    int deleteUser(int id);


}
