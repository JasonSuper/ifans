package com.ifans.common.core.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.Protocol;
import com.ifans.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
@ConditionalOnClass(com.aliyun.oss.OSS.class)
public class OssService {

    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    @Value("${alibaba.cloud.oss.bucket}")
    private String bucketName;

    @Value("${alibaba.cloud.access-key}")
    private String keyId;

    @Value("${alibaba.cloud.secret-key}")
    private String keySecret;

    private OSS ossBuilder() {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = this.endpoint;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = keyId;
        String accessKeySecret = keySecret;

        // 创建ClientConfiguration。ClientConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();

        // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
        conf.setMaxConnections(1024);
        // 设置Socket层传输数据的超时时间，默认为50000毫秒。
        conf.setSocketTimeout(50000);
        // 设置建立连接的超时时间，默认为50000毫秒。
        conf.setConnectionTimeout(50000);
        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
        //conf.setConnectionRequestTimeout(1000);
        // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
        conf.setIdleConnectionTime(60000);
        // 设置失败请求重试次数，默认为3次。
        conf.setMaxErrorRetry(3);
        // 设置是否支持将自定义域名作为Endpoint，默认支持。
        conf.setSupportCname(true);
        // 设置是否开启二级域名的访问方式，默认不开启。
        conf.setSLDEnabled(false);
        // 设置连接OSS所使用的协议（HTTP或HTTPS），默认为HTTP。
        conf.setProtocol(Protocol.HTTP);
        // 设置用户代理，指HTTP的User-Agent头，默认为aliyun-sdk-java。
        //conf.setUserAgent("aliyun-sdk-java");
        // 设置代理服务器端口。
        //conf.setProxyHost("<yourProxyHost>");
        // 设置代理服务器验证的用户名。
        //conf.setProxyUsername("<yourProxyUserName>");
        // 设置代理服务器验证的密码。
        //conf.setProxyPassword("<yourProxyPassword>");
        // 设置是否开启HTTP重定向，默认开启。
        conf.setRedirectEnable(true);
        // 设置是否开启SSL证书校验，默认开启。
        conf.setVerifySSLEnable(true);

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, conf);

        // 关闭OSSClient。
        //ossClient.shutdown();
        return ossClient;
    }

    /**
     * 简单上传
     * @param fileName 文件名
     * @param path 所属文件文件夹
     * @param inputStream 数据输入流
     * @return 图片路径
     */
    public String simpleUpload(String fileName, String path, InputStream inputStream) {
        path = formatPath(path);

        // 创建OSS实例。
        OSS ossClient = ossBuilder();
        try {
            //1、在文件名称里面添加随机唯一值（因为如果上传文件名称相同的话，后面的问价会将前面的文件给覆盖了）
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");//因为生成后的值有横岗，我们就把它去除，不替换也可以，也没有错
            fileName = uuid + fileName;

            //2、把文件安装日期进行分类： 2022/10/11/1.jpg
            //获取当前日期
            String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date());//在依赖中引入了该工具类

            //拼接
            fileName = path + "/" + datePath + "/" + fileName;

            //调用oss方法实现上传
            //参数一：Bucket名称  参数二：上传到oss文件路径和文件名称  比如 /aa/bb/1.jpg 或者直接使用文件名称  参数三：上传文件的流
            ossClient.putObject(bucketName, fileName, inputStream);
            //把上传之后的文件路径返回
            //需要把上传到阿里云路径返回    https://edu-guli-eric.oss-cn-beijing.aliyuncs.com/1.jpg
            String url = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + fileName;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //关闭OSSClient
            ossClient.shutdown();
        }
    }

    /**
     * 如果路径首字符是"/"，则去除首字符
     */
    private String formatPath(String path) {
        if (StringUtils.isNotEmpty(path) && path.startsWith("/")) {
            return path.substring(1, path.length());
        }
        return path;
    }
}
