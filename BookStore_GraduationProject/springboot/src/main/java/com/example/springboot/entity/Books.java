package com.example.springboot.entity;
import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@Data
public class Books {
    @TableId(type = IdType.AUTO)
    @Alias("序号")
    private Integer id;
    @Alias("name")
    private String title;
    @Alias("imUrl")
    private String imurl;
    @Alias("price")
    private String price;
    @Alias("categories")
    private String categories;
    @Alias("salesrank")
    private String salesrank;
    @Alias("asin")
    private String asin;
    @Alias("description")
    private String description;
    @Alias("also_bought")
    private String alsoBought;
    @Alias("bought_together")
    private String boughtTogether;
    @Alias("buy_after_viewing")
    private String buyAfterViewing;
    @Alias("authorid")
    private int authorid;
    @Alias("open")
    private Boolean open;
    @Alias("time")
    private String time;

    @TableField(exist = false)
    private String author;
}
