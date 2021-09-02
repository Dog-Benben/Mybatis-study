package com.mybatis.utils;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author Orange
 * @create 2021-08-13  0:02
 *
 * 在企业中一般数据库的属性id不会使用innodb自动顺序生成，
 * 企业是使用随机生成id保证唯一性（UUID）！
 */
@SuppressWarnings("all")//抑制警告
public class IDUtils {
    public static String getId(){
        //UUID.randomUUID().toString()是生成这类的id:UUID.randomUUID().toString()
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    @Test
    public void test(){
        System.out.println(IDUtils.getId());
        System.out.println(IDUtils.getId());
        System.out.println(IDUtils.getId());
    }
}
