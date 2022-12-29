package com.ifans.auth.controller;

import com.ifans.auth.service.SysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

//import com.ifans.common.security.service.TokenService;

/**
 * token控制层
 */
@RestController
public class TokenController {

    /*@Autowired
    private TokenService tokenService;*/

    @Autowired
    private SysLoginService sysLoginService;

    /*@PostMapping("logina")
    public R<?> login(@RequestBody LoginBody form) {
        // 用户登录
        LoginUser userInfo = sysLoginService.login(form.getEmail(), form.getPassword());
        // 获取登录token
        return R.ok(tokenService.createToken(userInfo));
    }*/

//    @DeleteMapping("logout")
//    public R<?> logout(HttpServletRequest request) {
//        String token = SecurityUtils.getToken(request);
//        if (StringUtils.isNotEmpty(token)) {
//            String username = JwtUtils.getUserName(token);
//            // 删除用户缓存记录
//            AuthUtil.logoutByToken(token);
//            // 记录用户退出日志
//            sysLoginService.logout(username);
//        }
//        return R.ok();
//    }
//
//    @PostMapping("register")
//    public R<?> register(@RequestBody RegisterBody registerBody) {
//        // 用户注册
//        sysLoginService.register(registerBody);
//        return R.ok();
//    }
}
