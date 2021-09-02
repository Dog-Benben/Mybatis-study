package com.mybatis.mapper;

import com.mybatis.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author Orange
 * @create 2021-07-28  18:30
 */
//操作实体类的接口
public interface UserMapper {
    //使用注解
    @Select("select * from user")
    List<User> getUser();

    //根据id查找
    //方法存在多个参数，所有的参数前面必须加上:@Param("")注解
    @Select("select * from user where id=#{id}")
    User getUserById(@Param("id") int id);
//    User getUserById(@Param("id") int id,@Param("name") String name);

    //增加数据
    @Insert("insert into user values(#{id},#{name},#{pwd})")
    int addUser(User user);

    //改数据
    @Update("update user set id=#{id},name=#{name} where id = #{id}")
    int updateUser(User user);

    //删除数据
    @Delete("delete from user where id = #{uid}")
    int deleteUserById(@Param("uid") int id);


}
