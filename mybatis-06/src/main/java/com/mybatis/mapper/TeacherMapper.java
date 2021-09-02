package com.mybatis.mapper;

import com.mybatis.pojo.Teacher;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Orange
 * @create 2021-08-09  21:23
 */
public interface TeacherMapper {
//    @Select("select * from teacher")
    List<Teacher> getTeacher();
}
