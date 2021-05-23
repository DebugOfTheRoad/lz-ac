package com.lckjsoft.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/23 20:55
 * @Modified By:
 * @Modified Date:      2021/5/23
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping("/index")
    public String index(){
        return "admin/home/index";
    }
}
