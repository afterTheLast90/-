package com.hanhai.cloud.controller;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.hanhai.cloud.base.BaseException;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.config.NonStaticResourceHttpRequestHandler;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.Files;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.service.FileService;
import com.hanhai.cloud.service.ShareService;
import com.hanhai.cloud.service.UserFileService;
import com.hanhai.cloud.utils.To_pdf;
import com.hanhai.cloud.utils.UnzipFile;
import com.hanhai.cloud.vo.FileListItem;
import lombok.extern.log4j.Log4j2;
import org.jodconverter.office.OfficeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Controller
@Validated
@Log4j2
public class PreviewController {

    @Autowired
    NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @Autowired
    private UserFileService userFileService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ShareService shareService;
    @Autowired
    SystemInfo systemInfo;
    private Set<String> openOfficeTypeSet = new HashSet<>();
    private Set<String> mediaTypeSet=new HashSet<>();
    private String[] openOfficeTypeArray = {"PPTX", "XLSX", "DOCX", "DOC", "HTML", "RTF", "TXT", "TIFF", "TTF", "PPT", "SVG", "SWF", "DBF", "XLS", "XLT", "SQL"};
    private String[] mediaTypeArray={"MP3","OGG","AAC","FLAC","M4R","MP4","WEBM"};
    private String uploadPath;
    private String unzipPath;
    private String pdfPath;
    public PreviewController(@Autowired SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        uploadPath = systemInfo.getUpLoadPath() + "Uploads/";
        unzipPath = systemInfo.getUpLoadPath() + "unzip/";
        pdfPath = systemInfo.getUpLoadPath() + "topdf/";
        openOfficeTypeSet.addAll(Arrays.asList(openOfficeTypeArray));
        mediaTypeSet.addAll(Arrays.asList(mediaTypeArray));
    }


    @GetMapping(value = "/preview")
    public String test() {
        return "preview";
    }

    /**
     * 获取文件类型
     *
     * @param path
     * @return
     */
    @GetMapping(value = "/getFileType")
    @ResponseBody
    public R<String> getFileType(@RequestParam("path") Long path) {
        String type = userFileService.getFileTypeByUserFileId(path);
        return new R<String>(ResultCode.SUCCESS_NO_SHOW).setData(type);
    }

    /**
     * 媒体文件预览
     *
     * @param userFileId
     * @param request
     * @param response
     * @throws IOException
     * @throws InterruptedException
     */
    @GetMapping(value = "/preview/media")
    public void test1(@RequestParam("userFileId") Long userFileId, HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException, ServletException {
        List<UserFile> userFileList = userFileService.getById(userFileId);


        if (userFileList.size()==0) {
            throw new BaseException(ResultCode.PARAMETER_ERROR);
        }

        UserFile userFile = userFileList.get(0);
        Files file = fileService.getById(userFile.getFileId());
        System.out.println(file.getFilePath());
        response.setContentType("video/mp4");
        log.warn(request.getHeader("Range"));

        String mimeType = java.nio.file.Files.probeContentType(Paths.get(uploadPath + file.getFilePath()));
        if (!StrUtil.isEmpty(mimeType)) {
            response.setContentType(mimeType);
        }
        request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, Paths.get(uploadPath + file.getFilePath()));
        nonStaticResourceHttpRequestHandler.handleRequest(request, response);
        output(uploadPath + file.getFilePath(), request, response);

    }


    /**
     * office文件转pdf
     *
     * @param source
     * @param filePath
     * @return
     * @throws OfficeException
     */
    private String topdf(String source, String filePath, boolean isZipFile) throws OfficeException {
        filePath = filePath.substring(0, filePath.lastIndexOf("."));
        System.out.println(filePath);
        String transFilePath;
        if (isZipFile) {
            transFilePath = pdfPath+"unzip/"+ filePath + ".pdf";
            System.out.println(transFilePath+"111");
        } else {
            transFilePath = pdfPath + "files/" + filePath + ".pdf";
            System.out.println(transFilePath+"111");
        }
        File file = new File(transFilePath);

        if (file.exists())
            return transFilePath;
//        System.out.println(System.getProperty("user.dir"));
//        System.out.println(source+222);
        To_pdf.To_Pdf(source, transFilePath);
        return transFilePath;

    }

    /**
     * 文档预览
     *
     * @param userFileId
     * @param request
     * @param response
     * @throws OfficeException
     * @throws IOException
     */
    @GetMapping("/preview/document")
    public void documentPreview(@RequestParam("userFileId") Long userFileId, HttpServletRequest request, HttpServletResponse response) throws OfficeException, IOException, ServletException {
        List<UserFile> userFileList = userFileService.getById(userFileId);


        if (userFileList.size()==0) {
            throw new BaseException(ResultCode.PARAMETER_ERROR);
        }

        UserFile userFile = userFileList.get(0);

        String type = userFile.getFileType();

        Files file = fileService.getById(userFile.getFileId());
        String path = uploadPath + file.getFilePath();


        if (openOfficeTypeSet.contains(type)) {
            System.out.println(path);
            System.out.println(file.getFilePath());
            path = topdf(path, file.getFilePath(), false);
        } else if (!type.equals("PDF")) {
            return;
        }


        response.setContentType("application/pdf");
        output(path, request, response);

    }

    /**
     * 压缩包文件树预览
     *
     * @param path
     * @return
     * @throws BaseException
     */
    @GetMapping("/preview/zip")
    @ResponseBody
    public R getUnzipDir(@NotNull(message = "必须指定zip文件") @NotBlank(message = "zip文件不存在") @RequestParam("path") String path) throws BaseException {

        // id
        // 文件id/.git/123
        String[] split = path.split("/");
        System.out.println(123);
        Long fileId = userFileService.getFileIdByUserFileId(Long.valueOf(split[0]));
        String filePath = fileService.getFilePathByID(fileId);

        String targetPath = unzipPath + filePath.substring(0, filePath.lastIndexOf("."));
        String zipFilePath = uploadPath + filePath;
        System.out.println(targetPath);
        System.out.println(zipFilePath);
        File file = new File(targetPath);
        if (split.length == 1) {
            if (!file.exists()) {
                try {
                    UnzipFile.unzipFile(targetPath, zipFilePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BaseException(ResultCode.UNZIP_ERROR);
                }
            }
        }
        if (split.length > 1) {
            System.out.println(111111);
            System.out.println(targetPath + path.substring(path.indexOf('/') + 1));
            file = new File(targetPath + path.substring(path.indexOf('/') + 1));
            if (!file.exists()) {
                throw new BaseException(ResultCode.UNZIP_ERROR);
            }
        }


        List<FileListItem> res = new ArrayList<>();
        for (File listFile : file.listFiles(File::isDirectory)) {
            FileListItem fileListItem = new FileListItem();
            fileListItem.setName(listFile.getName());
            fileListItem.setType("dir");
            res.add(fileListItem);
        }
        for (File listFile : file.listFiles(File::isFile)) {
            FileListItem fileListItem = new FileListItem();
            fileListItem.setName(listFile.getName());
            res.add(fileListItem);
        }
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(res);
    }

    /**
     * 压缩包文件预览
     *
     * @param httpServletResponse
     * @param request
     * @param path
     * @throws OfficeException
     * @throws IOException
     */
    @GetMapping(value = "/preview/zip/file")
    public void previewZipFile(HttpServletResponse httpServletResponse, HttpServletRequest request,HttpServletResponse response, @RequestParam("path") String path) throws OfficeException, IOException, ServletException {

        String[] split = path.split("/");

        Long fileId = userFileService.getFileIdByUserFileId(Long.valueOf(split[0]));

        System.out.println(path);
        String unzipFilePath = unzipPath + fileService.getFilePathByID(fileId);
        unzipFilePath = unzipFilePath.substring(0, unzipFilePath.lastIndexOf(".")) + "/";
//        System.out.println("sdafafdf");

        // 完整的文件路径
        String filePath = unzipFilePath + path.substring(path.indexOf("/"));
        String type = filePath.substring(filePath.lastIndexOf(".")+1).toUpperCase();
        if (openOfficeTypeSet.contains(type)) {
            unzipFilePath = fileService.getFilePathByID(fileId);
            unzipFilePath = unzipFilePath.substring(0, unzipFilePath.lastIndexOf(".")) + "/";
            String transFilePath = unzipFilePath + path.substring(path.indexOf("/"));
            filePath = topdf(filePath, transFilePath, true);
        }
        if(mediaTypeSet.contains(type)){
            response.setContentType("video/mp4");
            log.warn(request.getHeader("Range"));
            unzipFilePath = fileService.getFilePathByID(fileId);
            unzipFilePath = unzipFilePath.substring(0, unzipFilePath.lastIndexOf(".")) + "/";
            String filePaths = unzipFilePath + path.substring(path.indexOf("/"));
            System.out.println(filePaths);
            String mimeType = java.nio.file.Files.probeContentType(Paths.get(unzipPath + filePaths));
            if (!StrUtil.isEmpty(mimeType)) {
                response.setContentType(mimeType);
            }
            request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, Paths.get(unzipPath + filePaths));
            nonStaticResourceHttpRequestHandler.handleRequest(request, response);
        }
        System.out.println(filePath+"11111111111111111");
        output(filePath, request, httpServletResponse);
    }

    /**
     * 文件下载
     *
     * @param userFileId
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/fileDownload")
    public void fileDownload(@RequestParam("userFileId") Long userFileId, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<UserFile> userFileList = userFileService.getById(userFileId);


        if (userFileList.size()==0) {
            throw new BaseException(ResultCode.PARAMETER_ERROR);
        }

        UserFile userFile = userFileList.get(0);
        Files file = fileService.getById(userFile.getFileId());
        response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(userFile.getFileName(), "UTF-8"));
        output(uploadPath + file.getFilePath(), request, response);
    }


    /**
     * 分享文件下载
     *
     * @param userFileId
     * @Param shareId
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     * */
    @GetMapping("/shareDownload")
    public void fileDownload(@RequestParam("userFileId") Long userFileId,
                             @RequestParam("shareId") String shareId, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Files downFile = null;

        UserFile file = userFileService.getFileById(userFileId);    // 下载的文件
        UserShare share = shareService.getShareById(shareId);
        if(share==null || file==null){
            throw new BaseException(ResultCode.PARAMETER_ERROR);
        }
        UserFile shareFile = userFileService.getFileById(share.getUserFileId());    // 分享的文件
        // 分享的是文件夹的话，判断下载的文件是否是其子文件
        if ("DIR".equals(shareFile.getFileType())){
            // 不是其子文件
            if(!file.getFileParentPath().contains(shareFile.getFileParentPath() + shareFile.getUserFileId()))
                throw new BaseException(ResultCode.PARAMETER_ERROR);
            else {
                downFile = fileService.getById(file.getFileId());
            }
        }
        else {
            downFile = fileService.getById(shareFile.getFileId());
        }

        // 进行下载
        response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(file.getFileName(), "UTF-8"));
        output(uploadPath + downFile.getFilePath(), request, response);

        // 更新文件下载次数
        userFileService.addDownTime(shareId);
    }


    /**
     * 文件输出流
     *
     * @param filePath
     * @param request
     * @param response
     * @throws IOException
     */
    private void output2(String filePath, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String[] split = filePath.split("/");
        RandomAccessFile randomFile = new RandomAccessFile(filePath, "r");//只读模式
        long contentLength = randomFile.length();
        String range = request.getHeader("Range");
        int start = 0, end = 0;
        if (range != null && range.startsWith("bytes=")) {
            String[] values = range.split("=")[1].split("-");
            start = Integer.parseInt(values[0]);
            if (values.length > 1) {
                end = Integer.parseInt(values[1]);
            }
        }
        int requestSize = 0;
        if (end != 0 && end > start) {
            requestSize = end - start + 1;
        } else {
            requestSize = Integer.MAX_VALUE;
        }

        byte[] buffer = new byte[40960];
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", split[split.length - 1]);
        response.setHeader("Last-Modified", new Date().toString());
        //只第一次请求返回content length来让客户端请求多次实际数据
        if (range == null) {
            response.setHeader("Content-length", contentLength + "");
        } else {
            //以后的多次以断点续传的方式来返回视频数据
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);//206
            long requestStart = 0, requestEnd = 0;
            String[] ranges = range.split("=");
            if (ranges.length > 1) {
                String[] rangeDatas = ranges[1].split("-");
                requestStart = Integer.parseInt(rangeDatas[0]);
                if (rangeDatas.length > 1) {
                    requestEnd = Integer.parseInt(rangeDatas[1]);
                }
            }
            long length = 0;
            if (requestEnd > 0) {
                length = requestEnd - requestStart + 1;
                response.setHeader("Content-length", "" + length);
                response.setHeader("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
            } else {
                length = contentLength - requestStart;
                response.setHeader("Content-length", "" + length);
                response.setHeader("Content-Range", "bytes " + requestStart + "-" + (contentLength - 1) + "/" + contentLength);
            }
        }
        ServletOutputStream out = response.getOutputStream();
        int needSize = requestSize;
        randomFile.seek(start);
        while (needSize > 0) {
            int len = randomFile.read(buffer);
            if (needSize < buffer.length) {
                out.write(buffer, 0, needSize);
            } else {
                out.write(buffer, 0, len);
                if (len < buffer.length) {
                    break;
                }
            }
            needSize -= buffer.length;
        }
        randomFile.close();
        out.close();
    }

    public  void output(String path , HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {

        Path filePath = Paths.get(path);
        if (java.nio.file.Files.exists(filePath)) {
            String mimeType = java.nio.file.Files.probeContentType(filePath);
            if (!StringUtils.isEmpty(mimeType)) {
                response.setContentType(mimeType);
            }
            request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, filePath);
            nonStaticResourceHttpRequestHandler.handleRequest(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
    }



}
