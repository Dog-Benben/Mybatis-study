package com.mybatis.mapper;


import com.mybatis.pojo.User;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

/**
 * @author Orange
 * @create 2021-08-01  22:07
 */
public class UserMapperTest {
    @Test
    public void test(){
        /**
         * 第一步：从MybatisUtils获取到sqlsession对象
         */
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        /**
         * 方式一：getMapper（执行mysql）
         * sqlSession.getMapper(UserDao.class)中的UserDao类在mybatis叫做Mapper类
         */
        //UserMapper(UserDao实现类)是UserDao(接口)实现类，两个都是一个东西，
        // 在面向接口编程，只要拿到接口类就可以实现实现类
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        //实现接口的方法
        List<User> userList = mapper.getUserList();
        /**
         * 方式二：
         * 要具体到返回的类型二设对象类型，不建议用
         */
//        List<User> userList = sqlSession.selectList("com.mybatis.dao.UserDao.getUserList");

        for (User user : userList){
            System.out.println(user);
        }
        //关闭SqlSession
        sqlSession.close();

    }


    /**
     * 测试根据id获取信息
     */
    @Test
    public void test1(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userById = mapper.getUserById(2);
        System.out.println(userById);

        //关闭sqlSession
        sqlSession.close();

    }


    /**
     * 插入数据
     */
    @Test
    public void test3(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User(6,"林o","123456");
        int i = mapper.addUser(user);
        if (i>0){
            System.out.println(i);
            System.out.println("插入数据成功!!!");
        }
        //增删查改,要改动到数据库的,都要提交事务
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();

    }



    /**
     * 修改用户谢谢
     */
    @Test
    public void test4(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User(3,"刘备","123456");
        int i = mapper.updateUser(user);
        if (i>0){
            System.out.println("修改数据成功!!!");
        }
        //提交事务
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();
    }

    /**
     * 删除一个用户
     */
    @Test
    public void test5(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int i = mapper.deleteUser(2);
        if (i>0){
            System.out.println("删除成功一个用户");
        }
        //提交事务
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();
    }
}

