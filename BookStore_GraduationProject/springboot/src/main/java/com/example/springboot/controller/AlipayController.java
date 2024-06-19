package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.db.sql.Order;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.*;
import com.example.springboot.entity.Orders;
import com.example.springboot.mapper.OrdersMapper;
import com.example.springboot.service.OrdersService;


import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController

@RequestMapping("/alipay") //alipay/pay?orderNo=xxx
public class AlipayController {

    //支付宝沙箱网关地址
    private static final String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    //json
    private static final String format = "JSON";
    //UTF-8
    private static final String charset = "UTF-8";
    //签名方式
    private static final String signType = "RSA2";
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private AliPayConfig aliPayConfig;
    @Resource
    private OrdersService ordersService;
//    @HoneyLogs(operation = "支付宝", type = LogType.ADD)
    @AuthAccess
    @GetMapping("/pay")
    public void pay(@RequestParam String id, HttpServletResponse Response) throws AlipayApiException, IOException {
        //查询订单信息
        Orders order = ordersService.getById(id);
        if (order == null){
            return ;
        }
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, "9021000132691178", aliPayConfig.getPrivateKey(), format, charset, aliPayConfig.getPublicKey(), signType);
        //实例化请求对象,并设置参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", order.getNo());
        bizContent.set("total_amount", order.getMoney());
        bizContent.set("subject", order.getName());
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");
        System.out.println(bizContent);

        request.setBizContent(bizContent.toString());
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        //请求支付宝,拿到结果，返回给浏览器
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        String form = "";
        try{
            form = response.getBody();
        } catch (Exception e){
            e.printStackTrace();
        }
        //打印form
//        System.out.println(form);

        Response.setContentType("text/html;charset=" + charset);
        Response.getWriter().write(form);
        Response.getWriter().flush();
        Response.getWriter().close();
    }
    @AuthAccess
    @PostMapping("/notify")
    public void notify(HttpServletRequest request) throws AlipayApiException {
//        //获取支付宝POST过来反馈信息
//        String notifyData = request.getParameter("notify_data");
//        System.out.println("notifyData = " + notifyData);
//        //验证签名
//        boolean signVerified = AlipaySignature.rsaCheckV1(notifyData, aliPayConfig.getPublicKey(), charset, signType);
//        System.out.println("signVerified = " + signVerified);
//        if (signVerified) {
//            //验证成功
//            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
//            String outTradeNo = new JSONObject(notifyData).get("out_trade_no").toString();
//            String tradeNo = new JSONObject(notifyData).get("trade_no").toString();
//            String tradeStatus = new JSONObject(notifyData).get("trade_status").toString();
//            System.out.println("outTradeNo = " + outTradeNo);
//            System.out.println("tradeNo = " + tradeNo);
//            System.out.println("tradeStatus = " + tradeStatus);
//        }
        if(request.getParameter("trade_status").equals("TRADE_SUCCESS")){
//            System.out.println("===========支付异步回调===========");
            Map<String, String> map = new HashMap<>();
            Map<String, String[]> parameterMap = request.getParameterMap();
            for(String key : parameterMap.keySet()){
                map.put(key, request.getParameter(key));
//                System.out.println(key + " = " + request.getParameter(key));
            }
            String sign = request.getParameter("sign");
            String content = AlipaySignature.getSignCheckContentV1(map);
//            boolean signVerified = AlipaySignature.rsaCheckV1(map, aliPayConfig.getPublicKey(), "UTF-8", "RSA2");
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm7uxG9WovSRLKUaIKxMYlr90wIZIhQDgEQ/U4MXPWW14XI6orjT6TZcbxrSdBaUUnf0qhoa4stBlHefKbolNshfBIGWkkIiRK5qjVTwOy49d2oGAJ4pHSrwDlelD72G5yiLZwdEerSbWHBzacLBwETm1A2UZ4Auku7H4cGfaQ9sAsvhN4qkJlwIl9wRoUbxsPCvqaHVL2rIjcjP9aH3HUYNx5M9wa5mxE3NCsBw9kKwtMriicwT7ctR74Mcg0+auh6+AIrckN3A4IowObhlOScyQL07swGBEECchyS+qUCzBk9uZl7h9o0xtk6eNbFX4WmXLC2higXboacHDeMYk+QIDAQAB", "UTF-8"); // 验证签名
            if (checkSignature) {
//                System.out.println("签名验证成功");
//                System.out.println("交易名称: " + map.get("subject"));
//                System.out.println("交易状态: " + map.get("trade_status"));
//                System.out.println("支付宝交易凭证号: " + map.get("trade_no"));
//                System.out.println("商户订单号: " + map.get("out_trade_no"));
//                System.out.println("交易金额: " + map.get("total_amount"));
//                System.out.println("买家在支付宝唯一id: " + map.get("buyer_id"));
//                System.out.println("买家付款时间: " + map.get("gmt_payment"));
//                System.out.println("买家付款金额: " + map.get("buyer_pay_amount"));

                //查询订单
                String outTradeNo = map.get("out_trade_no");
                QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("no", outTradeNo);
                Orders orders = ordersMapper.selectOne(queryWrapper);

                String alipayTradeNo = map.get("trade_no");

                if (orders != null) {
                    orders.setAlipayNo(alipayTradeNo);
                    orders.setPayTime(DateUtil.now());
                    orders.setState("已支付");
                    ordersMapper.updateById(orders);
                }
                System.out.println("订单支付成功");
            }
        }
    }
}
