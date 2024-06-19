package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.AuthAccess;
import com.example.springboot.common.HoneyLogs;
import com.example.springboot.common.LogType;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Reviews;
import com.example.springboot.entity.User;
import com.example.springboot.service.ReviewsService;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewsController {

    @Autowired
    ReviewsService reviewsService;

    @Autowired
    UserService userService;

    /**
     * 新增评论
     */
    @HoneyLogs(operation = "评论", type = LogType.ADD)
    @PostMapping("/add")
    public Result add(@RequestBody Reviews reviews) {
        User currentUser = TokenUtils.getCurrentUser();  // 获取到当前登录的用户信息
        assert currentUser != null;
        reviews.setReviewerId(currentUser.getId().toString());
        reviews.setReviewTime(DateUtil.now());  //   2023-09-12 21:09:12
        reviews.setReviewerName(currentUser.getName());
        reviews.setHelpful(0);
        reviews.setHelpfulAll(0);
        reviewsService.save(reviews);
        return Result.success();
    }

    /**
     * 修改信息
     */
    @HoneyLogs(operation = "评论", type = LogType.UPDATE)
    @PutMapping("/update")
    public Result update(@RequestBody Reviews reviews) {
        reviewsService.updateById(reviews);
        return Result.success();
    }

    /**
     * 删除信息
     */
    @HoneyLogs(operation = "评论", type = LogType.DELETE)
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        reviewsService.removeById(id);
        return Result.success();
    }


    /**
     * 批量删除信息
     */
    @HoneyLogs(operation = "评论", type = LogType.BATCH_DELETE)
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        reviewsService.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * 查询全部信息
     */
    @AuthAccess
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Reviews> userList = reviewsService.list(new QueryWrapper<Reviews>().orderByDesc("id"));
        return Result.success(userList);
    }

    /**
     * 多条件模糊查询信息
     * pageNum 当前的页码
     * pageSize 每页查询的个数
     */
    @AuthAccess
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String reviewerId,
                               @RequestParam String asin) {
        QueryWrapper<Reviews> queryWrapper = new QueryWrapper<Reviews>().orderByDesc("id");  // 默认倒序，让最新的数据在最上面
        queryWrapper.eq(StrUtil.isNotBlank(reviewerId), "reviewer_id", reviewerId);
        queryWrapper.eq(StrUtil.isNotBlank(asin), "asin", asin);
        Page<Reviews> page = reviewsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }


}
