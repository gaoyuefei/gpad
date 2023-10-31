package com.gpad.gpadtool.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.utils
 * @Author: LF
 * @Description: TODO
 * @Version: 1.0
 */
public class FTPUntil {
    //FTP IP
    private static final String hostname = "198.168.198.128";

    //FTP 端口号
    private static final int port = 21;

    //FTP 登录账号
    private static final String username = "user-padFile";

    //FTP 登录密码
    private static final String password = "123";

    //FTP 工作路径
    private static final String pathname = "/pad/";

    //FTP服务器上文件
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");


    /**
     * 上传文件
     *
     * @param fileName    上传到FTP服务器后的文件名称
     * @param inputStream 输入文件流
     * @return
     */
    public static boolean uploadFile(String fileName, InputStream inputStream) {

        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        //设置超时
        ftpClient.setConnectTimeout(60 * 60 * 1000);
        //设置编码
        ftpClient.setControlEncoding("UTF-8");
        try {
            //连接FTP服务器
            ftpClient.connect(hostname, port);
            //登录FTP服务器
            ftpClient.login(username, password);
            //是否成功登录FTP服务器
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return flag;
            }
            System.out.println("===========登录FTP成功了==========");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //切换路径 创建路径
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.enterLocalPassiveMode();

            String pathDir1 = pathname+df.format(new Date());
            boolean pathIsExis = ftpClient.changeWorkingDirectory(pathDir1);
            System.out.println("查看文件夹是否存在");
            if(!pathIsExis){//不存在文件夹则创建并且进入文件夹
                ftpClient.makeDirectory(pathDir1);
                ftpClient.changeWorkingDirectory(pathDir1);
            }

            //设置缓冲
            ftpClient.setBufferSize(1024 * 1024 * 20);
            //保持连接
            ftpClient.setKeepAlive(true);
            boolean a = ftpClient.storeFile(new String(fileName.getBytes("utf-8"), "iso-8859-1"), inputStream);
            if (a) {
                flag = true;
                System.out.println("===========创建文件成功==============" + a);
                // String fileName2 = fileName;  //这里可以将名字更改
                // boolean status = ftpClient.rename(fileName, fileName2);
                // if (status)
                //     System.out.println("===========修改文件名称成功==============" + status);
            }else{
                flag = false;
            }
            inputStream.close();
            ftpClient.logout();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;

    }

    /**
     * 生成写入文件
     * @param data 文件内容
     * @return
     */
    public static InputStream write(String data){
        InputStream input = null;
        try {
            input = new ByteArrayInputStream(data.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("文件写入异常："+e);
        }
        return input;
    }

}

