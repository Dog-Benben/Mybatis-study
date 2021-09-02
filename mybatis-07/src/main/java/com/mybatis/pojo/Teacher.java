package com.mybatis.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author Orange
 * @create 2021-08-09  21:21
 */
@Data
public class Teacher {
    private int id;
    private String name;
    //一个老师拥有多个学生
    private  List<Student> students;
}
