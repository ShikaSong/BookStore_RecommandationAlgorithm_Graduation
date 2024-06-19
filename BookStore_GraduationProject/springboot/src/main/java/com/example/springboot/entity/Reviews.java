package com.example.springboot.entity;
import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("review")
public class Reviews {

    @TableId(type=IdType.AUTO)
    @Alias("序号")
    private Integer id;
    @Alias("评论人编号")
    private String reviewerId;
    @Alias("书籍亚马逊号")
    private String asin;
    @Alias("评论人姓名")
    private String reviewerName;
    @Alias("有帮助的人数")
    private Integer helpful;
    @Alias("总人数")
    private Integer helpfulAll;
    @Alias("评论")
    private String reviewText;
    @Alias("评分")
    private Integer overall;
    @Alias("总结")
    private String summary;
    @Alias("评论时间")
    private String reviewTime;


}

