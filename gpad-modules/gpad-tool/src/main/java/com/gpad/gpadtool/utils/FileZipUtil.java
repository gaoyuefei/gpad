package com.gpad.gpadtool.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class FileZipUtil {

    /**
     * 打包成zip下载
     */
    public void downloadZipFile(HttpServletResponse response, ZipFileDTO fileDTO) {
        List<String> names = fileDTO.getFileNms();
        List<ByteArrayOutputStream> streams = fileDTO.getStreams();
        //输出Excel文件
        try (
                OutputStream output = response.getOutputStream();
                ZipOutputStream zipStream = new ZipOutputStream(output)
        ) {
            String filename = URLEncoder.encode(fileDTO.getZipFileNm(), StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filename=" + filename + ";filename*=utf-8''" + filename);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString());

            //创建压缩文件,并进行打包
            for (int i = 0; i < names.size(); i++) {
                ZipEntry z = new ZipEntry(names.get(i));
                zipStream.putNextEntry(z);
                streams.get(i).writeTo(zipStream);
            }
            zipStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取一个文件，暂存入ByteArrayOutputStream
     *
     * @param fullPath 完整的文件路径
     * @return 文件输出流
     */
    public ByteArrayOutputStream getByteArrayOutputStream(String fullPath) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (
                FileInputStream inputStream = new FileInputStream(fullPath)
        ) {
            //小数组读取文件流，提高性能
            byte[] buff = new byte[8 * 1024];
            int len;
            while (-1 != (len = inputStream.read(buff))) {
                outputStream.write(buff, 0, len);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件读取错误");
        }
        return outputStream;
    }

    /**
     * 读取一个文件，暂存入ByteArrayOutputStream
     *
     * @param inputStream 完整的文件路径
     * @return 文件输出流
     */
    public ByteArrayOutputStream getByteArrayOutputStream(FileInputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            //小数组读取文件流，提高性能
            byte[] buff = new byte[8 * 1024];
            int len;
            while (-1 != (len = inputStream.read(buff))) {
                outputStream.write(buff, 0, len);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件读取错误");
        }
        return outputStream;
    }

}
