package com.ifans.common.oss;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.aliyun.oss.*;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnClass(OSS.class)
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
     * 文件名格式化
     *
     * @param fileName 原始文件名
     * @param path     所属文件夹
     * @return OSS文件名
     */
    private String filenameFormat(String fileName, String path) {
        //1、在文件名称里面添加随机唯一值（因为如果上传文件名称相同的话，后面的问价会将前面的文件给覆盖了）
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");//因为生成后的值有横岗，我们就把它去除，不替换也可以，也没有错
        fileName = uuid + fileName;

        //2、把文件安装日期进行分类： 2022/10/11/1.jpg
        //获取当前日期
        String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date());//在依赖中引入了该工具类

        //拼接
        return fileName = path + "/" + datePath + "/" + fileName;
    }

    /**
     * 简单上传
     *
     * @param fileName    文件名
     * @param path        所属文件文件夹
     * @param inputStream 数据输入流
     * @return 图片路径
     */
    public String simpleUpload(String fileName, String path, InputStream inputStream) {
        path = formatPath(path);

        // 创建OSS实例。
        OSS ossClient = ossBuilder();
        try {
            // 格式化文件名
            fileName = filenameFormat(fileName, path);

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

    public String multipart(String fileName, String path) {
        // 创建OSS实例。
        OSS ossClient = ossBuilder();
        try {
            // 格式化文件名
            fileName = filenameFormat(fileName, path);
            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, fileName);

            // 如果需要在初始化分片时设置请求头，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // 指定该Object的网页缓存行为。
            // metadata.setCacheControl("no-cache");
            // 指定该Object被下载时的名称。
            // metadata.setContentDisposition("attachment;filename=oss_MultipartUpload.txt");
            // 指定该Object的内容编码格式。
            // metadata.setContentEncoding(OSSConstants.DEFAULT_CHARSET_NAME);
            // 指定初始化分片上传时是否覆盖同名Object。此处设置为true，表示禁止覆盖同名Object。
            // metadata.setHeader("x-oss-forbid-overwrite", "true");
            // 指定上传该Object的每个part时使用的服务器端加密方式。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
            // 指定Object的加密算法。如果未指定此选项，表明Object使用AES256加密算法。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_DATA_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
            // 指定KMS托管的用户主密钥。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_KEY_ID, "9468da86-3509-4f8d-a61e-6eab1eac****");
            // 指定Object的存储类型。
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard);
            // 指定Object的对象标签，可同时设置多个标签。
            // metadata.setHeader(OSSHeaders.OSS_TAGGING, "a:1");
            // request.setObjectMetadata(metadata);

            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId，它是分片上传事件的唯一标识。您可以根据该uploadId发起相关的操作，例如取消分片上传、查询分片上传等。
            String uploadId = upresult.getUploadId();

            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> partETags = new ArrayList<PartETag>();
            // 每个分片的大小，用于计算文件有多少个分片。单位为字节。
            final long partSize = 1 * 1024 * 1024L;   //1 MB。

            // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
            final File sampleFile = new File("D:\\localpath\\examplefile.txt");
            long fileLength = sampleFile.length();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                InputStream instream = new FileInputStream(sampleFile);
                // 跳过已经上传的分片。
                instream.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(fileName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(uploadPartResult.getPartETag());
            }


            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, fileName, uploadId, partETags);

            // 如果需要在完成分片上传的同时设置文件访问权限，请参考以下示例代码。
            // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.Private);
            // 指定是否列举当前UploadId已上传的所有Part。如果通过服务端List分片数据来合并完整文件时，以上CompleteMultipartUploadRequest中的partETags可为null。
            // Map<String, String> headers = new HashMap<String, String>();
            // 如果指定了x-oss-complete-all:yes，则OSS会列举当前UploadId已上传的所有Part，然后按照PartNumber的序号排序并执行CompleteMultipartUpload操作。
            // 如果指定了x-oss-complete-all:yes，则不允许继续指定body，否则报错。
            // headers.put("x-oss-complete-all","yes");
            // completeMultipartUploadRequest.setHeaders(headers);

            // 完成分片上传。
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println(completeMultipartUploadResult.getETag());
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return "";
    }
}
