package com.gpad.gpadtool.controller;

import cn.hutool.core.util.IdUtil;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.UploadFileOutputDto;
import com.gpad.gpadtool.utils.DateUtil;
import com.gpad.gpadtool.utils.FileUtil;
import com.gpad.gpadtool.utils.UuidUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileController.java
 * @Description 文件管理接口
 * @createTime 2023年10月07日 10:49:00
 */
@Slf4j
@Api(value = "文件管理接口", tags = "文件管理接口")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${config.file.path}")
    private String FILE_PATH;

    /**
     * 文件上传
     */
    @Operation(summary = "文件上传")
    @PostMapping("/uploadFile")
    public R<UploadFileOutputDto> uploadFile(@RequestBody MultipartFile file) {
        log.info("文件上传!配置的存储路径=== {}", FILE_PATH);
        if (!FileUtil.checkFileType(file)) {
            return R.fail("上传附件失败,请输入正确的文件类型");
        }
        String filename = file.getOriginalFilename();
        if (Strings.isEmpty(filename)) {
            return R.fail("文件名不能为空!");
        }
        String fileType = FileUtil.getFileType(file);
        if (fileType == null) {
            return R.fail("文件类型为空!");
        }
        //文件存储路径
        String filePath = FILE_PATH.concat(File.separator).concat(DateUtil.generateDateTimeStr()).concat(File.separator);
        log.info("文件上传!当前文件存储路径=== {}", filePath);
        String newFilename = UuidUtil.generateUuidWithDate() + "." + fileType;
        FileUtil.uploadFile(file, filePath, newFilename);

        String result = filePath.concat(newFilename).replaceAll("\\\\", "/");
        String subResult = result.substring(4);
        UploadFileOutputDto uploadFileOutputDto = new UploadFileOutputDto();
        uploadFileOutputDto.setFileName(newFilename);
        uploadFileOutputDto.setFilePath(subResult);
        return R.ok(uploadFileOutputDto);
    }

    /**
     * 文件下载
     */
    @Operation(summary = "文件下载")
    @GetMapping("/downloadFile")
    public void downloadFile(HttpServletResponse response, @RequestParam("filePath") String filePath, @RequestParam("fileName") String fileName) {
        log.info("文件下载!存储路径=== {} ,文件名=== {}", filePath, fileName);
        FileUtil.downloadFile(filePath, fileName, response);
    }

    /**
     * 文件预览
     */
    @Operation(summary = "文件预览")
    @PostMapping("/previewFile")
    public void previewFile(HttpServletResponse response, @RequestParam("filePath") String filePath, @RequestParam("fileName") String fileName) {
        log.info("文件下载!存储路径=== {} ,文件名=== {}", filePath, fileName);
        FileUtil.getBase64FromInputStream(filePath, fileName, response);
    }

    /**
     * 视频在线播放
     */
    @Operation(summary = "视频在线播放")
    @GetMapping("/viewTo")
    public void viewTo(HttpServletResponse response, HttpServletRequest request, @RequestParam("filePath") String filePath) throws Exception {
        long skip = -1;
        long length = -1;
        File file = new File(filePath);
        response.setHeader("Content-Type", "video/mp4");
        long fileLength = file.length();
        long end = fileLength - 1;
        String range = request.getHeader("Range");
        if (range != null && range.length() > 0) {
            int idx = range.indexOf("-");
            skip = Long.parseLong(range.substring(6, idx));
            if ((idx + 1) < range.length()) {
                end = Long.parseLong(range.substring(idx + 1));
            }
            length = end - skip + 1;
        }

        if (range == null || range.length() <= 0) {//bytes=32523-32523
            response.setHeader("Content-Length", "" + fileLength);
            response.setStatus(200);
        } else {
            response.setHeader("Content-Length", "" + length);
            response.setHeader("Content-Range", "bytes " + skip + "-" + end + "/" + fileLength);
            response.setStatus(206);
        }
        System.out.println("bytes " + skip + "-" + end + "/" + fileLength);
        download(response.getOutputStream(), file, skip, length);
    }

    /**
     * 文件下载基础类
     * 断点续读
     *
     * @param outputStream 文件输出流
     * @param skip         跳过多少字节 <=0忽略
     * @param length       输出字节长度 <=忽略
     * @throws Exception
     */
    public void download(OutputStream outputStream, File file, long skip, long length) throws Exception {
        InputStream inputStream = null;
        try {
            //打开流下载对象
            inputStream = new FileInputStream(file);
            if (skip > 0) {
                inputStream.skip(skip);
            }
            byte[] bs = new byte[1024];
            int len;
            while ((len = inputStream.read(bs)) != -1) {
                if (length > 0) {
                    if (length > len) {
                        outputStream.write(bs, 0, len);
                        outputStream.flush();
                        length -= len;
                    } else {
                        outputStream.write(bs, 0, (int) length);
                        outputStream.flush();
                        break;
                    }
                } else {
                    outputStream.write(bs, 0, len);
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


    /**
     * 文件上传
     */
    @Operation(summary = "FTP文件服务器上传")
    @PostMapping("/uploadFtpFile")
    public R<UploadFileOutputDto> uploadFtpFile(@RequestParam("file") MultipartFile file) throws IOException {
        //获取文件上传流
        InputStream inputStream = file.getInputStream();
        //1、获取原文件后缀名
        String originalFileName = file.getOriginalFilename();
        //截取后缀
        String suffix = originalFileName.substring(originalFileName.lastIndexOf('.'));
        //2、使用UUID生成新文件名
        String newFileName = IdUtil.fastSimpleUUID() + suffix ;
        //3、将MultipartFile转化为File
//             file = FileUtils.multipartFileToFile(multipartFile);
//        boolean flag = FTPUntil.uploadFile(newFileName,inputStream);
        UploadFileOutputDto uploadFileOutputDto = new UploadFileOutputDto();
        return R.ok(uploadFileOutputDto);
    }


}
