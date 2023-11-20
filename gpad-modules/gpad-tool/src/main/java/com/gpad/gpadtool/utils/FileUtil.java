package com.gpad.gpadtool.utils;

import cn.hutool.core.io.FileTypeUtil;
import com.gpad.gpadtool.constant.StatusCode;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.gpadtool.constant.FileTypeConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@Slf4j
public class FileUtil {

    /**
     * 下载文件
     */
    public static void downloadFile(String filePath, String fileName, HttpServletResponse res) {
        log.info("下载文件--->>> fileFullPath = {} fileName = {}", filePath, fileName);

        if (Strings.isEmpty(filePath) || Strings.isEmpty(fileName)) {
            throw new ServiceException("文件路径或文件名不能为空!", StatusCode.PARAMETER_ILLEGAL.getValue());
        }

        InputStream inputStream = null;
        BufferedInputStream bis = null;
        OutputStream outputStream = null;

        //设置返回文件的名字
        try {
            inputStream = new FileInputStream(filePath.concat(File.separator).concat(fileName));
            res.setHeader("Content-Disposition",
                    "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            //设置返回值的类型
            res.setHeader("content-type", "application/octet-stream");
            bis = new BufferedInputStream(inputStream);
            outputStream = res.getOutputStream();
            byte[] buffer = new byte[1024];
            int i = bis.read(buffer);
            while (i != -1) {
                outputStream.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            log.error("下载文件出错! {}", e.getMessage());
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != bis) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 写出文件到磁盘
     *
     * @param path 文件存储路径
     */
    public static void uploadFile(MultipartFile multipartFile, String path, String fileName) {
        log.info("写出文件  {}", System.currentTimeMillis());
        log.info("写出文件--->>> path={}", path);

        File savedFile = null;
        try (InputStream ins = multipartFile.getInputStream()) {
            File pathDir = new File(path);
            if (!pathDir.exists()) {
                pathDir.mkdirs();
            }
            log.info("上传文件文件名 = {}", fileName);
            log.info("开始上传文件到服务器  {}", System.currentTimeMillis());
            savedFile = new File(path.concat(File.separator).concat(fileName));
            inputStreamToFile(ins, savedFile);
            log.info("上传文件到服务器完成  {}", System.currentTimeMillis());
            ins.close();
        } catch (Exception e) {
            log.warn("上传文件到服务器出错! Exception message:{}", e.getMessage(), e);
            throw new ServiceException("上传文件出错! ".concat(e.getMessage()), StatusCode.SYS_ERR.getValue());
        }
        finally {
//            savedFile.delete();

        }
    }


    /**
     * 写出文件到磁盘
     *
     * @param path 文件存储路径
     */
    public static void uploadJzqPngFile(InputStream ins, String path, String fileName) {
        log.info("写出文件  {}", System.currentTimeMillis());
        log.info("写出文件--->>> path={}", path);

        File savedFile = null;
        try  {
            File pathDir = new File(path);
            if (!pathDir.exists()) {
                pathDir.mkdirs();
            }
            log.info("上传文件文件名 = {}", fileName);
            log.info("开始上传文件到服务器  {}", System.currentTimeMillis());
            savedFile = new File(path.concat(File.separator).concat(fileName));
            inputStreamToFile(ins, savedFile);
            log.info("上传文件到服务器完成  {}", System.currentTimeMillis());
            ins.close();
        } catch (Exception e) {
            log.warn("上传文件到服务器出错! Exception message:{}", e.getMessage(), e);
            throw new ServiceException("上传文件出错! ".concat(e.getMessage()), StatusCode.SYS_ERR.getValue());
        }
        finally {
//            savedFile.delete();

        }
    }

    /**
     * 获取文件流转base64方法
     **/
    public static void getBase64FromInputStream(String filePath, String fileName, HttpServletResponse response) {
        log.info("获取文件流转base64--->>> filePath = {} fileName = {}", filePath, fileName);
        if (Strings.isEmpty(filePath) || Strings.isEmpty(fileName)) {
            throw new ServiceException("文件路径或文件名不能为空!", StatusCode.PARAMETER_ILLEGAL.getValue());
        }
        byte[] bs = new byte[1024];
        int len;
        OutputStream out = null;
        InputStream in = null;
        try {
            in = new FileInputStream(filePath.concat(File.separator).concat(fileName));
            String contentType = "text/html;charset=UTF-8";
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline;filename="
                    + fileName);
            out = response.getOutputStream();
            while ((len = in.read(bs)) > 0) {
                out.write(bs, 0, len);
            }
        } catch (Exception e) {
            log.error("转换流异常 {}", e.getMessage());
        } finally {
            if (null != out) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 获取流文件
     *
     * @param ins, file
     */
    private static void inputStreamToFile(InputStream ins, File file) {
        try (FileOutputStream os = new FileOutputStream(file)) {
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.warn("OssUtils.uploadFile() Exception message:{}", e.getMessage(), e);
        } finally {
            if (null != ins) {
                try {
                    ins.close();
                } catch (IOException e) {
                    log.warn("OssUtils.uploadFile() close() Exception message:{}", e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查文件类型
     */
    public static boolean checkFileType(MultipartFile file) {
        return Strings.isNotEmpty(getFileType(file));
    }

    /**
     * 获取文件类型
     */
    public static String getFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        log.info("文件名为{}",filename);
        assert filename != null;
        String[] split = filename.split("\\.");
        String fileType = "png";

        //先判断后缀名
        for (FileTypeConstant constant : FileTypeConstant.values()) {
            if (constant.getValue().equalsIgnoreCase(fileType)) {
                try {
                    String type = FileTypeUtil.getType(file.getInputStream());
                    if (FileTypeConstant.checkFileType(type)) {
                        return fileType;
                    }
                } catch (Exception e) {
                    throw new ServiceException("文件后缀名校验不通过，请检查上传的文件是否符合要求!", StatusCode.PARAMETER_ILLEGAL.getValue());
                }
            }
        }
        return null;
    }


    /**
     * 获取文件类型
     */
    public static Boolean getFileType(String suffix) {
        log.info("文件名为{}",suffix);
        //先判断后缀名
        for (FileTypeConstant constant : FileTypeConstant.values()) {
            if (constant.getValue().equalsIgnoreCase(suffix)) {
                log.info("校验成功{}",suffix);
                return true;
            }
        }
        return false;
    }


}
