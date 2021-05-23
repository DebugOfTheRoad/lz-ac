package com.lckjsoft.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lckjsoft.common.base.JsonResult;
import com.lckjsoft.gateway.annotation.JwtCheck;
import com.lckjsoft.gateway.constant.WebRequestRoutePrefix;
import com.lckjsoft.gateway.dto.UserDTO;
import com.lckjsoft.gateway.util.JwtModel;
import com.lckjsoft.gateway.util.JwtUtil;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private ObjectMapper objectMapper;

    @Value("${com.lckjsoft.jwt.effective-time}")
    private String effectiveTime;

    public AuthController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 登陆认证接口
     * @param userDTO
     * @return
     */
    @PostMapping("/login")
    public JsonResult<String> login(@RequestBody UserDTO userDTO) throws Exception {
        ArrayList<String> roleIdList = new ArrayList<>(1);
        roleIdList.add("role_test_1");
        JwtModel jwtModel = new JwtModel("test", roleIdList);
        int effectivTimeInt = Integer.valueOf(effectiveTime.substring(0,effectiveTime.length()-1));
        String effectivTimeUnit = effectiveTime.substring(effectiveTime.length()-1,effectiveTime.length());
        String jwt = null;
        switch (effectivTimeUnit){
            case "s" :{
                //秒
                jwt = JwtUtil.createJWT("test", "test", objectMapper.writeValueAsString(jwtModel), effectivTimeInt * 1000L);
                break;
            }
            case "m" :{
                //分钟
                jwt = JwtUtil.createJWT("test", "test", objectMapper.writeValueAsString(jwtModel), effectivTimeInt * 60L * 1000L);
                break;
            }
            case "h" :{
                //小时
                jwt = JwtUtil.createJWT("test", "test", objectMapper.writeValueAsString(jwtModel), effectivTimeInt * 60L * 60L * 1000L);
                break;
            }
            case "d" :{
                //小时
                jwt = JwtUtil.createJWT("test", "test", objectMapper.writeValueAsString(jwtModel), effectivTimeInt * 24L * 60L * 60L * 1000L);
                break;
            }
        }
        return JsonResult.success("成功！",jwt);
    }

    /**
     * 为授权提示
     */
    @GetMapping("/unauthorized")
    public JsonResult<String> unauthorized(){
        return JsonResult.fail("未认证,请重新登陆");
    }

    /**
     * jwt 检查注解测试 测试
     * @return
     */
    @GetMapping("/testJwtCheck")
//    @JwtCheck
    public JsonResult<String> testJwtCheck(
            @RequestHeader("Authorization")String token,@RequestParam("name")
    @Valid String name){

        return JsonResult.success("请求成功咯","请求成功咯"+name);

    }

    /**
     * jwt 检查注解测试 测试
     * @return
     */
    @GetMapping("/testJwtCheck2")
//    @JwtCheck
    public JsonResult<String> testJwtCheck2(
            @RequestParam("name")
            @Valid String name){

        return JsonResult.success("请求成功咯","请求成功咯"+name);

    }
}
