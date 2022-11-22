package com.ifans.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginBody {
    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户密码
     */
    private String password;
}
