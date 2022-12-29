package com.ifans.auth.service;

import com.ifans.api.system.FeignUserService;
import com.ifans.api.system.model.LoginUser;
import com.ifans.auth.domain.LoginUserDetails;
import com.ifans.common.core.constant.SecurityConstants;
import com.ifans.common.core.domain.R;
import com.ifans.common.core.exception.ServiceException;
import com.ifans.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class IfansUserDetailService implements UserDetailsService {

    @Autowired
    private FeignUserService feignUserService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 查询用户信息
        R<LoginUser> userResult = feignUserService.getUserInfo(email, SecurityConstants.INNER);

        if (R.FAIL == userResult.getCode()) {
            throw new ServiceException(userResult.getMsg());
        }

        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData())) {
            //recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "登录用户不存在");
            throw new ServiceException("用户不存在");
        }
        return new LoginUserDetails(userResult.getData());
    }
}
