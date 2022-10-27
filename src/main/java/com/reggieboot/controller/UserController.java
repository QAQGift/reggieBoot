package com.reggieboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggieboot.common.BaseContext;
import com.reggieboot.common.R;
import com.reggieboot.entity.User;
import com.reggieboot.servicce.UserService;
import com.reggieboot.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    /*发送验证码*/
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        /*获取手机号*/
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            /*生成验证码*/
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            /*发送短信*/
            log.info("code "+code);
            /*保存验证码到session中*/
            session.setAttribute(phone,code);
            return R.success("验证码已发送");
        }
        return R.error("失败");
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        /*获取手机号*/
        log.info(map.toString());
        /*获取手机号*/
        String phone = map.get("phone").toString();
        /*获取验证码*/
        String code = map.get("code").toString();
        /*和session中的验证码进行比较*/
        Object codeInsession = session.getAttribute(phone);
        /*比较成功进行登录*/
        if (codeInsession != null&& codeInsession.equals(code)){
            log.info("正确登录");
            /*判断表中是否有当前手机号，如果是新用户则自动保存到表中*/

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            BaseContext.setCurrent(user.getId());
            return R.success(user);
        }


        return R.error("登陆失败");
    }
}
