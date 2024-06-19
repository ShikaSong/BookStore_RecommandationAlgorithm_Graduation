package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class News {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String title;
    private String description;
    private String content;
    private Integer authorid;
    private String time;
    private BigDecimal price;

    // 这个注解表示这个字段不在数据库表里  是用来做业务处理用的
    @TableField(exist = false)
    private String author;

}

