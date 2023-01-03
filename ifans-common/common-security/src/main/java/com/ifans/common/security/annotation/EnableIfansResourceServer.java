package com.ifans.common.security.annotation;

import com.ifans.common.security.component.IfansResourceServerAutoConfiguration;
import com.ifans.common.security.component.IfansResourceServerConfiguration;
import com.ifans.common.security.feign.IfansFeignClientConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.lang.annotation.*;

/**
 * 资源服务注解
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({IfansResourceServerAutoConfiguration.class, IfansResourceServerConfiguration.class, IfansFeignClientConfiguration.class})
public @interface EnableIfansResourceServer {

}
