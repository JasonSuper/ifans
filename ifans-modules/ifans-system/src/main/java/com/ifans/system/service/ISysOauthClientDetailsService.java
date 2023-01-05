package com.ifans.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.system.domain.SysOauthClientDetails;

/**
 * 服务类
 */
public interface ISysOauthClientDetailsService extends IService<SysOauthClientDetails> {

    /**
     * 通过ID删除客户端
     *
     * @param id
     * @return
     */
    Boolean removeClientDetailsById(String id);

    /**
     * 修改客户端信息
     *
     * @param sysOauthClientDetails
     * @return
     */
    Boolean updateClientDetailsById(SysOauthClientDetails sysOauthClientDetails);

    /**
     * 清除客户端缓存
     */
    void clearClientCache();

}
