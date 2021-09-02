package com.mybatis.mapper;


import com.mybatis.pojo.User;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Orange
 * @create 2021-08-01  22:07
 */
public class UserMapperTest {
    //括号是填写一个类，想那个类输出就选那个类
    static Logger logger = Logger.getLogger(UserMapperTest.class);

    @Test
    public void test1(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        logger.info("成功进入到test1方法");
        User userById = mapper.getUserById(1);
        System.out.println(userById);

        //关闭sqlSession
        sqlSession.close();

    }
    @Test
    public void testLog4j(){
        //log4j的级别
        //info：信息
        logger.info("info:进入testLog4j");
        // 调试
        logger.debug("debug:进入testLog4j");
        //紧急错误
        logger.error("error:进入testLog4j");
    }

    /**
     * 使用limit分页
     */
    @Test
    public void getUserByLimit(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Map<String, Integer> map = new HashMap<>();
        map.put("startIndex",0);
        map.put("pageSize",2);
        List<User> userByLimit = mapper.getUserByLimit(map);
        for (User user:userByLimit){
            System.out.println(user);
        }
        sqlSession.close();
    }

    /**
     * 使用RowBound分页，是完全使用java的面对对象思想
     */
    @Test
    public void getUserByRowBounds(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();

        //RowBounds实现分页
        RowBounds rowBounds = new RowBounds(1,2);

        //通过java代码层面实现分页
        List<User> listUser = sqlSession.selectList("com.mybatis.mapper.UserMapper.getUserByRowBounds",null,rowBounds);

        for (User user : listUser) {
            System.out.println(user);
        }
        sqlSession.close();
    }
}

