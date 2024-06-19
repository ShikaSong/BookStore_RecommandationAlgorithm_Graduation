package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.AuthAccess;
import com.example.springboot.common.HoneyLogs;
import com.example.springboot.common.LogType;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Books;
import com.example.springboot.entity.User;
import com.example.springboot.service.BooksService;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    @Autowired
    BooksService booksService;

    @Autowired
    UserService userService;

    /**
     * 新增信息
     */
    @HoneyLogs(operation = "书籍", type = LogType.ADD)
    @PostMapping("/add")
    public Result add(@RequestBody Books books) {
        User currentUser = TokenUtils.getCurrentUser();  // 获取到当前登录的用户信息
        books.setAuthorid(currentUser.getId());
        books.setTime(DateUtil.now());  //   2023-09-12 21:09:12
        booksService.save(books);
        return Result.success();
    }

    /**
     * 修改信息
     */
    @HoneyLogs(operation = "书籍", type = LogType.UPDATE)
    @PutMapping("/update")
    public Result update(@RequestBody Books books) {
        booksService.updateById(books);
        return Result.success();
    }

    /**
     * 删除信息
     */
    @HoneyLogs(operation = "书籍", type = LogType.DELETE)
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        booksService.removeById(id);
        return Result.success();
    }


    /**
     * 批量删除信息
     */
    @HoneyLogs(operation = "书籍", type = LogType.BATCH_DELETE)
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        booksService.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * 查询全部信息
     */
    @AuthAccess
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Books> userList = booksService.list(new QueryWrapper<Books>().orderByDesc("id"));
        return Result.success(userList);
    }

    /**
     * 根据ID查询信息
     */
    @AuthAccess
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Books books = booksService.getById(id);
        User user = userService.getById(books.getAuthorid());
        if (user != null) {
            books.setAuthor(user.getName());
        }
        return Result.success(books);
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
                               @RequestParam String title,
                                @RequestParam String categories) {
        QueryWrapper<Books> queryWrapper = new QueryWrapper<Books>().orderByDesc("id");  // 默认倒序，让最新的数据在最上面
        queryWrapper.like(StrUtil.isNotBlank(title), "categories", title);
        queryWrapper.like(StrUtil.isNotBlank(categories), "categories", categories);
        Page<Books> page = booksService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Books> records = page.getRecords();
//        List<User> list = userService.list();
        for (Books record : records) {
            Integer authorid = record.getAuthorid();
            User user = userService.getById(authorid);
//            String author = list.stream().filter(u -> u.getId().equals(authorid)).findFirst().map(User::getName).orElse("");
            if (user != null) {
                record.setAuthor(user.getName());
            }
        }
        return Result.success(page);
    }


}
