package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Books;
import com.example.springboot.mapper.BooksMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BooksService extends ServiceImpl<BooksMapper, Books> {

    @Resource
    BooksMapper booksMapper;

}
