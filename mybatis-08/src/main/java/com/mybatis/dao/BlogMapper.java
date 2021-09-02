package com.mybatis.dao;

import com.mybatis.pojo.Blog;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author Orange
 * @create 2021-08-13  0:01
 */
public interface BlogMapper {
    //插入数据
    int addBlog(Blog blog);

    //查询博客,使用mybatisSQL的if
    List<Blog> queryBlogIF(Map map);

    //查询博客，使用mybatisSQl的choose
    List<Blog> queryBlogChoose(Map map);

    //更新博客，使用mybatisSQl的set
    int updateBlog(Map map);

    //查询第1-2-3号记录的博客
    List<Blog> queryBlogForeach(Map map);

}
