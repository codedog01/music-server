package com.cmx.music.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class AliOSSUtils {
    private static final Logger logger = LoggerFactory.getLogger(AliOSSUtils.class);

    private final Date OSS_URL_EXPIRATION = DateUtils.addDays(new Date(), 365 * 100);
    /**
     * 当Bucket不存在时创建Bucket（设置标准，公开的存储空间）
     *
     * @throws OSSException    异常
     * @throws ClientException Bucket命名规则：
     *                         1.只能包含小写字母、数字和短横线，
     *                         2.必须以小写字母和数字开头和结尾
     *                         3.长度在3-63之间
     */
    private OSS getBucket() {
        // 创建OSSClient实例。
        //OSS 的地址
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            if (!ossClient.doesBucketExist("cloudleng")) {//判断是否存在该Bucket，不存在时再重新创建
                // 创建CreateBucketRequest对象。
                CreateBucketRequest createBucketRequest = new CreateBucketRequest("cloudleng");
                // 此处以设置存储空间的存储类型为标准存储为例。
                createBucketRequest.setStorageClass(StorageClass.Standard);
                // 设置存储空间的权限为公共读，默认是私有。
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                // 创建存储空间。
                ossClient.createBucket(createBucketRequest);
            }
        } catch (Exception e) {
            logger.error("{}", "创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
            throw new RuntimeException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
        return ossClient;
    }

    /**
     * 上传到OSS服务器  如果同名文件会覆盖服务器上的
     *
     * @param file    文件
     * @param fileDir 上传到OSS上文件的路径
     * @return 文件的访问地址
     */

    public String uploadFile(MultipartFile file, String fileDir) {

        //处理文件夹路径前无/后加/
        if (StringUtils.isEmpty(fileDir)) {
            fileDir = "";
        } else {
            fileDir = (fileDir.indexOf("/") == 0) ? fileDir.substring(1) : fileDir;
            fileDir = (fileDir.lastIndexOf("/") == fileDir.length() - 1) ? fileDir : fileDir + "/";
        }

        // 创建OSSClient实例。
        OSS ossClient = getBucket();

        String fileName = String.format("%s.%s", UUID.randomUUID().toString().replace("-", ""), FilenameUtils.getExtension(file.getOriginalFilename()));

        String resultUrl = null;
        try (InputStream inputStream = file.getInputStream()) {

            //创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");

            //设置对应文件类型的ContentType
            objectMetadata.setContentType(getContentType(FilenameUtils.getExtension("." + file.getOriginalFilename())));

            //inline在线预览,中文乱码已处理,下载文件的时候可以用原来上传的名字
            objectMetadata.setContentDisposition("inline;filename=" + URLEncoder.encode(file.getOriginalFilename(), "utf-8"));

            //上传文件
            ossClient.putObject("cloudleng", fileDir + fileName, inputStream, objectMetadata);

            if (StringUtils.isEmpty(fileName)) {
                logger.error("{}", "文件地址为空");
                throw new RuntimeException("文件地址为空");
            }
            String[] split = fileName.split("/");

            //获取oss图片URL失败
            URL url = ossClient.generatePresignedUrl("cloudleng", fileDir + split[split.length - 1], OSS_URL_EXPIRATION);

            if (url == null) {
                logger.error("{}", "获取oss文件URL失败");
                throw new RuntimeException("获取oss文件URL失败");
            }

            resultUrl = url.toString();
            int firstChar = resultUrl.indexOf("?");
            if (firstChar > 0 ) {
                resultUrl = resultUrl.substring(0, firstChar);
            }
        } catch (Exception e) {
            logger.error("{}", "上传文件失败");
            throw new RuntimeException("上传文件失败");
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
        return fileName;
    }

    private String accessKeyId;
    /**
     * 批量上传到OSS服务器  如果同名文件会覆盖服务器上的
     *
     * @param fileDir 上传到OSS上文件的路径
     * @return 文件的访问地址","拼接
     */
    public String batchUploadFile(MultipartFile[] files, String fileDir) {
        if (!(files != null && files.length > 0)) {
            return null;
        }
        //处理文件夹路径前无/后加/
        if (StringUtils.isEmpty(fileDir)) {
            fileDir = "";
        } else {
            fileDir = (fileDir.indexOf("/") == 0) ? fileDir.substring(1) : fileDir;
            fileDir = (fileDir.lastIndexOf("/") == fileDir.length() - 1) ? fileDir : fileDir + "/";
        }

        // 创建OSSClient实例。
        OSS ossClient = getBucket();

        String resultUrls = "";
        try {
            for (MultipartFile file : files) {

                InputStream inputStream = file.getInputStream();
                String resultUrl = null;
                String fileName = String.format("%s.%s", UUID.randomUUID().toString().replace("-", ""), FilenameUtils.getExtension(file.getOriginalFilename()));

                //创建上传Object的Metadata
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(inputStream.available());
                objectMetadata.setCacheControl("no-cache");
                objectMetadata.setHeader("Pragma", "no-cache");
                objectMetadata.setContentEncoding("utf-8");

                //设置对应文件类型的ContentType
                objectMetadata.setContentType(getContentType(FilenameUtils.getExtension("." + file.getOriginalFilename())));

                //inline在线预览,中文乱码已处理,下载文件的时候可以用原来上传的名字
                objectMetadata.setContentDisposition("inline;filename=" + URLEncoder.encode(file.getOriginalFilename(), "utf-8"));

                //上传文件
                ossClient.putObject("cloudleng", fileDir + fileName, inputStream, objectMetadata);

                if (StringUtils.isEmpty(fileName)) {
                    logger.error("{}", "文件地址为空");
                    throw new RuntimeException("文件地址为空");
                }
                String[] split = fileName.split("/");

                //获取oss图片URL失败
                URL url = ossClient.generatePresignedUrl("cloudleng", fileDir + split[split.length - 1], OSS_URL_EXPIRATION);

                if (url == null) {
                    logger.error("{}", "获取oss文件URL失败");
                    throw new RuntimeException("获取oss文件URL失败");
                }

                resultUrl = url.toString();
                int firstChar = resultUrl.indexOf("?");
                if (firstChar > 0) {
                    resultUrl = resultUrl.substring(0, firstChar);
                }
                resultUrls += "," + resultUrl;
            }

        } catch (Exception e) {
            logger.error("{}", "上传文件失败");
            throw new RuntimeException("上传文件失败");
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
        return StringUtils.isEmpty(resultUrls) ? null : resultUrls.substring(1);
    }
    private String accessKeySecret;

    /**
     * 删除指定路径下的一个文件
     *
     * @param fileURL 文件的全称
     */
    public void deleteFile(String fileURL) {

        //获取完整路径中的文件名，截去"http://oss-cn-shenzhen.aliyuncs.com/"剩下的就是文件名
        String key = fileURL.substring(fileURL.indexOf("/", 9) + 1);

        // 创建OSSClient实例。
        OSS ossClient = getBucket();

        // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
        if (ossClient.doesObjectExist("cloudleng", key)) {
            ossClient.deleteObject("cloudleng", key);
        }

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    /**
     * 下载文件
     *
     * @param fileURL
     * @param response
     */
    public void downloadFile(String fileURL, HttpServletResponse response) {
        OSS ossClient = getBucket();

        //获取完整路径中的文件名，截去"http://oss-cn-shenzhen.aliyuncs.com/"剩下的就是文件名
        String key = fileURL.substring(fileURL.indexOf("/", 9) + 1);

        if (!ossClient.doesObjectExist("cloudleng", key)) {
            logger.error("{}", "oss文件不存在");
            throw new RuntimeException("oss文件不存在");
        }

        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject("cloudleng", key);

        //文件下载设置
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", ossObject.getObjectMetadata().getContentDisposition().replace("inline", "attachment"));

        try {

            // 读取文件内容。
            OutputStream out = response.getOutputStream();
            BufferedInputStream br = new BufferedInputStream(ossObject.getObjectContent());
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = br.read(buf)) > 0) out.write(buf, 0, len);
            out.close();
            br.close();
            ossObject.close();

        } catch (IOException e) {
            logger.error("{}", "oss文件下载失败");
            throw new RuntimeException("oss文件下载失败");
        } finally {

            // 关闭OSSClient。
            ossClient.shutdown();
        }

    }

    {
        this.accessKeyId = RSAUtil.decryptHex("752B7A2F76573273533134676E3455506F426B5331775A32577862537978374B4546495A786E6B66763670585255444561356F45397743767244494D6976392B6D556B2B735770706F304B494878514F4669716A62316857663141626535744136327772776F46394A6E32576345586E5738765150716B3759577636524B555756484F423130542B4B446B3570564E315750504E43314C7236634E677255773235746A74434138444333673D");
        this.accessKeySecret = RSAUtil.decryptHex("5A365559676D585754715332725A386A4765326F71794A643439726951514A6A78646E706F6852455847673756416D6F6178797276494241373131796563736C497048616A524C6B5A4F424F454654317151366C4B3845377A474E62687A68746A716B3663596A326170695432686A63587634456B4C76636A66755A5745426B7A5156454C56547A47544D6E5251497A59426564517057447869514D6A727466633976442F355430796A553D");
    }
    /**
     * 根据文件夹路径获取时间倒序的文件
     *
     * @param dirPath
     * @return
     */
    public List<String> getListByDirPath(String dirPath) {

        //处理文件夹路径前无/后加/
        if (StringUtils.isEmpty(dirPath)) {
            dirPath = "";
        } else {
            dirPath = (dirPath.indexOf("/") == 0) ? dirPath.substring(1) : dirPath;
            dirPath = (dirPath.lastIndexOf("/") == dirPath.length() - 1) ? dirPath : dirPath + "/";
        }

        OSS ossClient = getBucket();

        List<String> ossUrls = new ArrayList<String>();

        // 列举文件。如果不设置KeyPrefix，则列举存储空间下的所有文件。如果设置KeyPrefix，则列举包含指定前缀的文件。
        ObjectListing objectListing = ossClient.listObjects("cloudleng", dirPath);
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();

        //List自定义排序：根据文件最后修改时间参数倒序（最新的在前面）
        Collections.sort(sums, new Comparator<OSSObjectSummary>() {

            @Override
            public int compare(OSSObjectSummary o1, OSSObjectSummary o2) {

                long dateTime1 = o1.getLastModified().getTime();
                long dateTime2 = o2.getLastModified().getTime();
                if (dateTime1 == dateTime2) {
                    return 0;
                } else {
                    //时间正序
//					return dateTime1 > dateTime2 ? 1 : -1;

                    //时间倒序
                    return dateTime1 > dateTime2 ? -1 : 1;
                }
            }

        });

        //处理完整路径
        for (OSSObjectSummary s : sums) {

            URL url = ossClient.generatePresignedUrl("cloudleng", s.getKey(), OSS_URL_EXPIRATION);
            String finalUrl = url.toString();
            int firstChar = finalUrl.indexOf("?");
            if (firstChar > 0) finalUrl = finalUrl.substring(0, firstChar);
            System.out.println("完整文件路径：" + finalUrl + " 时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(s.getLastModified()));
            ossUrls.add(finalUrl);

        }

        // 关闭OSSClient。
        ossClient.shutdown();

        return ossUrls;
    }

    /**
     * 判断OSS服务文件上传时文件的contentType
     *
     * @param FilenameExtension 文件后缀
     * @return ContentType
     */
    private String getContentType(String FilenameExtension) {

        //image/jpg 可以在线预览
        if (FilenameExtension.equalsIgnoreCase("gif") || FilenameExtension.equalsIgnoreCase("jpeg") || FilenameExtension.equalsIgnoreCase("jpg") || FilenameExtension.equalsIgnoreCase("png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase("pptx") || FilenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase("docx") || FilenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase("pdf")) {
            return "application/pdf";
        }
        if (FilenameExtension.equalsIgnoreCase("ppt")) {
            return "application/x-ppt";
        }
        if (FilenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        if (FilenameExtension.equalsIgnoreCase("mp3")) {
            return "audio/mp3";
        }
        if (FilenameExtension.equalsIgnoreCase("mp4")) {
            return "video/mp4";
        }
        if (FilenameExtension.equalsIgnoreCase("avi")) {
            return "video/avi";
        }
        if (FilenameExtension.equalsIgnoreCase("wmv")) {
            return "video/x-ms-wmv";
        }
        return "image/jpg";
    }

}