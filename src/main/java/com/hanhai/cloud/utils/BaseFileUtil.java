package com.hanhai.cloud.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.hanhai.cloud.configuration.SystemInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;

/**
 * 操作文件的工具类的父类，每个操作文件的工具类都继承这个父类
 * 通过写子类的构造方法，设置不同的目录。
 * @author wmgx
 * @create 2021-04-27-20:30
 **/
@Log4j2
public class BaseFileUtil {

    private String path;
    private String basePath;

    private String deletePath;

    /**
     * 父类构造方法，通过子类传入的配置类设置配置
     * @param systemInfo
     */
    public BaseFileUtil(SystemInfo systemInfo) {
        this.basePath = systemInfo.getUpLoadPath();
    }

    /**
     * 子类调用父类的setPath方法来设置存储文件的路径
     * @param path
     */
    protected void setPath(String path) {
        path = path.replaceAll("\\\\", "/");
        if (!(path.endsWith("/"))) {
            path += "/";
        }
        basePath = basePath.replaceAll("\\\\", "/");
        if (!(basePath.endsWith("/"))) {
            basePath += "/";
        }
        this.path = basePath + path;
        this.deletePath = this.path.substring(0,this.path.length()-1)+"del/";
    }

    /**
     * 保存文件方法，将传入的MultipartFile 保存到磁盘
     * @param file
     * @return
     * @throws IOException
     */
    public String saveFile(MultipartFile file) throws IOException {
        LocalDate now = LocalDate.now();
        String fileName =
                now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + "/" +
                        IdUtil.fastSimpleUUID() + file.getOriginalFilename();
        File f = new File(path + fileName);
        if (f.exists()) {
            fileName = now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + "/" +
                    IdUtil.fastSimpleUUID() + file.getOriginalFilename();
            f = new File(path + fileName);
        }
        FileUtil.touch(f);
        file.transferTo(f);
        return fileName;

    }

    /**
     * 获取文件的输入流
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public InputStream getFile(String fileName) throws FileNotFoundException {
        File f = new File(path + fileName);
        return new FileInputStream(f);
    }

    /**
     * 将文件写入输出流，默认自动关闭
     * @param fileName
     * @param response
     * @throws IOException
     */
    public void getFileWrite(String fileName, HttpServletResponse response) throws IOException {
        getFileWrite(fileName, true, response);
    }

    /**
     * 将文件写入输出流，可选是否关闭输出流
     * @param fileName
     * @param closed
     * @param response
     * @throws IOException
     */
    public void getFileWrite(String fileName, Boolean closed, HttpServletResponse response) throws IOException {
        IoUtil.write(response.getOutputStream(), closed,
                IoUtil.readBytes(new FileInputStream(new File(path + fileName))));
    }

    /**
     * 删除文件
     * @param fileName
     * @throws FileNotFoundException
     */
    public void deleteFile(String fileName) throws FileNotFoundException {
        File source = new File(path+fileName);
        if (!source.exists()){
            throw new  FileNotFoundException();
        }
        FileUtil.move(source,new File(deletePath+fileName),true);
    }
}
