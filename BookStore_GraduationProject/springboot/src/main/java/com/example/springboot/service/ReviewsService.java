package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Reviews;
import com.example.springboot.mapper.ReviewsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ReviewsService extends ServiceImpl<ReviewsMapper, Reviews> {

    @Resource
    ReviewsMapper reviewsMapper;

}
