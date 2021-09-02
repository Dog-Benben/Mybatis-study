package com.mybatis.dao;


import com.mybatis.pojo.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Orange
 * @create 2021-08-09  21:23
 */
public interface TeacherMapper {
    //获取老师信息
//    List<Teacher> getTeacher();

    //获取指定老师下的所有学生信息及老师信息，按照结果嵌套查询
    Teacher getTeacher(@Param("tid") int id);
    //获取指定老师下的所有学生信息及老师信息，按照查询嵌套查询
    Teacher getTeacher2(@Param("tid") int id);


}
