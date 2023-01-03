/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ifans.api.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ifans.common.core.util.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 客户端信息
 */
@Data
//@EqualsAndHashCode(callSuper = true)
@TableName("oauth_client_details")
public class SysOauthClientDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端ID
     */
    @NotBlank(message = "client_id 不能为空")
    @TableId(value = "client_id", type = IdType.INPUT)
    private String clientId;

    /**
     * 客户端密钥
     */
    @TableField("client_secret")
    @NotBlank(message = "client_secret 不能为空")
    private String clientSecret;

    /**
     * 资源ID
     */
    @TableField("resource_ids")
    private String resourceIds;

    /**
     * 作用域
     */
    @TableField("scope")
    @NotBlank(message = "scope 不能为空")
    private String scope;

    /**
     * 授权方式（A,B,C）
     */
    @TableField("authorized_grant_types")
    private String authorizedGrantTypes;

    /**
     * 回调地址
     */
    @TableField("web_server_redirect_uri")
    private String webServerRedirectUri;

    /**
     * 权限
     */
    @TableField("authorities")
    private String authorities;

    /**
     * 请求令牌有效时间
     */
    @TableField("access_token_validity")
    private Integer accessTokenValidity;

    /**
     * 刷新令牌有效时间
     */
    @TableField("refresh_token_validity")
    private Integer refreshTokenValidity;

    /**
     * 扩展信息
     */
    @TableField("additional_information")
    private String additionalInformation;

    /**
     * 是否自动放行
     */
    @TableField("autoapprove")
    private String autoapprove;

}
