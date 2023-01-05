package com.ifans.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.system.domain.SysOauthClientDetails;
import com.ifans.common.core.constant.CacheConstants;
import com.ifans.system.mapper.SysOauthClientDetailsMapper;
import com.ifans.system.service.ISysOauthClientDetailsService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 */
@Service
public class SysOauthClientDetailsServiceImpl extends ServiceImpl<SysOauthClientDetailsMapper, SysOauthClientDetails> implements ISysOauthClientDetailsService {

    /**
     * 通过ID删除客户端
     *
     * @param id
     * @return
     */
    @Override
    @CacheEvict(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#id")
    public Boolean removeClientDetailsById(String id) {
        return this.removeById(id);
    }

    /**
     * 根据客户端信息
     *
     * @param clientDetails
     * @return
     */
    @Override
    @CacheEvict(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#clientDetails.clientId")
    public Boolean updateClientDetailsById(SysOauthClientDetails clientDetails) {
        return this.updateById(clientDetails);
    }

    /**
     * 清除客户端缓存
     */
    @Override
    @CacheEvict(value = CacheConstants.CLIENT_DETAILS_KEY, allEntries = true)
    public void clearClientCache() {

    }

}
