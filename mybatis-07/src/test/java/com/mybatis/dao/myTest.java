package com.mybatis.dao;

import com.mybatis.pojo.Teacher;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

/**
 * @author Orange
 * @create 2021-08-12  18:05
 */
public class myTest {

    //按照结果嵌套查询
    @Test
    public void getTeacher(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        TeacherMapper mapper = sqlSession.getMapper(TeacherMapper.class);
        Teacher teacher = mapper.getTeacher(1);
        System.out.println(teacher);
        /**
         * Teacher(id=1, name=五老师, students=[
         * Student(id=1, name=lisa, tid=1),
         * Student(id=2, name=make, tid=1),
         * Student(id=3, name=amos, tid=1),
         * Student(id=4, name=jon, tid=1),
         * Student(id=5, name=marria, tid=1)
         * ])
         */
        sqlSession.close();
    }

    //按照查询嵌套查询
    @Test
    public void getTeacher2(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        TeacherMapper mapper = sqlSession.getMapper(TeacherMapper.class);
        Teacher teacher2 = mapper.getTeacher2(1);
        System.out.println(teacher2);

        sqlSession.close();
    }
}
