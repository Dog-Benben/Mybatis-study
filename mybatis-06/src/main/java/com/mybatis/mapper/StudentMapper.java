package com.mybatis.mapper;

import com.mybatis.pojo.Student;


import java.util.List;

/**
 * @author Orange
 * @create 2021-08-09  21:22
 */
public interface StudentMapper {
//    @Select("select * from student")
    List<Student> getStudent();

    List<Student> getStudent2();
}
