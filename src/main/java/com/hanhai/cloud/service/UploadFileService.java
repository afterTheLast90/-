package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.entity.FileHistory;
import com.hanhai.cloud.entity.Files;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.params.FastUploadParam;
import com.hanhai.cloud.params.MultipartFileParam;
import com.hanhai.cloud.utils.FileMd5Util;
import com.hanhai.cloud.utils.FileNameUtil;
import com.hanhai.cloud.utils.utils.FileUploadRedisUtils;
import com.hanhai.cloud.vo.FileUploadVO;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UploadFileService extends BaseService {
    @Autowired
    private FileUploadRedisUtils fileUploadRedisUtils;
    @Autowired
    private SystemInfo systemInfo;
    @Autowired
    private UserService userService;
    private FileUploadVO fileUploadVO=null;

    /**
     *  通过名字查找虚拟目录是否存在同名文件
     */
    public Long findByName(String path, String name , String userId){
        if(userId==null)
            userId=String.valueOf(StpUtil.getLoginIdAsLong());
        System.out.println("userId="+userId);
        List<UserFile> lists=new ArrayList<UserFile>();
        Long userFileId=null;
        lists=userFileMapper.getByName(path,name,Long.parseLong(userId));
        for(UserFile userFile:lists){
            userFileId=userFile.getUserFileId();
            if(userFileId!=null)
                break;
        }
        return userFileId;
    }

    public List<UserFile> findByNames(String path, String[] names, String userId){
        List<UserFile> userFiles=new ArrayList<>();
        if(userId==null)
            userId=String.valueOf(StpUtil.getLoginIdAsLong());
        userFiles=userFileMapper.getByNames(path,names,Long.parseLong(userId));
        for(UserFile userFile:userFiles){
            System.out.println(userFile);
        }
        return userFiles;
    }

    /**
     * 通过md5值查找文件对象
     * @param md5
     * @return
     */
    public FileUploadVO findByFileMd5(String md5){
        fileUploadVO = (FileUploadVO) fileUploadRedisUtils.get(md5);
        if(fileUploadVO==null)
            fileUploadVO=new FileUploadVO();
        Files file=fileMapper.findByFileMd5(md5);
        Long fileId= null;
        if(file==null){ //没有上传完文件，判断是否上传完成，需要断点续传
            System.out.println("********************************   检测断点续传   ***********************************");
            Integer chunk=fileUploadVO.getChunk();
            Long tempId=fileUploadVO.getFileId();
            String fileMd5=fileUploadVO.getMd5();
            if(tempId!=null)
                fileId=tempId;
            if(md5.equals(fileMd5) && chunk!=null){
                //上传了部分，需要断点续传
                fileUploadVO.setFlag("1");
                fileUploadVO.setFileId(fileId);
            }else{
                //没有上传过文件
                fileId=YitIdHelper.nextId(); //获取下一个要插入的记录的文件ID
                fileUploadVO.setFlag("0");
                fileUploadVO.setFileId(fileId);
                fileUploadVO.setMd5(md5);
            }
        }else{
            //上传过该文件，秒传即可
            fileUploadVO.setFlag("2");
            fileUploadVO.setFileId(file.getFileId());
        }
        fileUploadRedisUtils.set(md5,fileUploadVO);
        return (FileUploadVO)fileUploadRedisUtils.get(md5);
    }

    /**
     * 上传文件
     * @param param 文件表单信息
     * @param multipartFile 文件
     * @return
     */
    public FileUploadVO realUpload(MultipartFileParam param, MultipartFile multipartFile)
                                    throws IOException,Exception{
        fileUploadVO = (FileUploadVO) fileUploadRedisUtils.get(param.getMd5());
        if(fileUploadVO==null)
            fileUploadVO=new FileUploadVO();
        String md5=param.getMd5(); //md5值
        String partMd5=param.getPartMd5(); //分片md5值
        String fileId=param.getId(); //文件id
        String fileName=param.getName();  //文件名
        String size=param.getSize();  //文件大小
        Integer total=Integer.valueOf(param.getTotal()); //总片数
        Integer index=Integer.valueOf(param.getIndex()); //分片序号,当前第几片(从1开始)
        String action=param.getAction(); //上传状态 check：检测；upload：上传
        /*不带扩展名的文件名*/
        String filenameNoEx= FileNameUtil.getFileNameNoEx(fileName);
        /*扩展名*/
        String suffix= FileNameUtil.getExtensionName(fileName);
        /**
         * 目录验证
         *  year:当前年份
         *  month:当前月份
         *  day:当前日子
         *  fileSaveDirectory:分片合成文件的路径  上传文件的文件夹名(目录)
         *  tempSaveDirectory:上传分片的路径
         *  filePath:上传分片的文件名 fileId.suffix (文件名.文件扩展名)
         */
        Date date= DateUtil.date();
        String year=String.valueOf(DateUtil.year(date));
        String month=String.valueOf(DateUtil.month(date)+1);
        String day=String.valueOf(DateUtil.dayOfMonth(date));
        String fileSaveDirectory = systemInfo.getUpLoadPath()+"Uploads" + "/" + year + "/"
                + month + "/" + day ; //上传文件的文件夹
        String tempSaveDirectory = fileSaveDirectory + "/" + fileId+"_temp"; //上传文件的分片temp文件夹
//        String filePath = tempSaveDirectory + "/" + filenameNoEx + "." + suffix;
        String savePath = year+"/"+month+"/"+day+"/"+fileId+"."+suffix;
        //验证路径是否存在，不存在则创建目录
        File path = new File(tempSaveDirectory);
        if (!path.exists()) {
            path.mkdirs();
        }
        //文件分片名
        File file = new File(tempSaveDirectory, filenameNoEx + "_" + index);
        //根据action不同执行不同操作. check:校验分片是否上传过; upload:直接上传分片
        if("check".equals(action)){
            String md5Str= FileMd5Util.getFileMD5(file);//分片MD5
            if (md5Str != null && md5Str.equals(partMd5)) {
                //分片已上传过
                fileUploadVO.setFlag("1");
                fileUploadVO.setFileId(Long.parseLong(fileId));
            } else {
                //分片未上传
                fileUploadVO.setFlag("0");
                fileUploadVO.setFileId(Long.parseLong(fileId));
                fileUploadRedisUtils.set(md5,fileUploadVO);
                return (FileUploadVO)fileUploadRedisUtils.get(md5);
            }
        } else if("upload".equals(action)){
            //分片上传过程中出错,有残余时需删除分块后,重新上传;上传前检测，如果存在则删除重新上传.
            if (file.exists()) {
                file.delete();
            }
            multipartFile.transferTo(new File(tempSaveDirectory, filenameNoEx + "_" + index));
            fileUploadVO.setFlag("1");
            fileUploadVO.setFileId(Long.parseLong(fileId));
        }

        if(path.isDirectory()){
            File[] fileArray=path.listFiles();
            if(fileArray!=null){
                if (fileArray.length >= total) {
                    //分块全部上传完毕，合并
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@开始合并@@@@@@@@@@@@@@@@@@@@@@@");
                    File newFile=new File(fileSaveDirectory,fileId+"."+suffix);
                    FileOutputStream outputStream=new FileOutputStream(newFile,true);//文件追加写入
                    for (int i = 0; i < fileArray.length; i++) {
                        File tmpFile = new File(tempSaveDirectory, filenameNoEx + "_" + (i + 1));
                        if(tmpFile.exists())
                            FileUtils.copyFile(tmpFile,outputStream);
                    }
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@合并结束，开始删除@@@@@@@@@@@@@@@@@@@@@@@");
                    FileUtil.del(tempSaveDirectory); //删除分片文件夹
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@删除完成@@@@@@@@@@@@@@@@@@@@@@@");
                    outputStream.close();

                    //======================================================================
                    //============================ 对数据库表操作 =============================
                    //======================================================================

                    //文件表插入记录
                    Files files=new Files();
                    files.setFileId(Long.parseLong(fileId));
                    files.setFileMd5(md5);
//                    files.setFilePath(filePath);
                    files.setFilePath(savePath);
                    files.setFileSize(Long.valueOf(size));
                    files.setStorageLocation(0);
                    files.setCitationsCount(1L);
                    fileMapper.insert(files);

                    //用户文件表插入记录
                    String userFileId=param.getUserFileId();
                    System.out.println("******************************");
                    System.out.println("userFileId=##"+userFileId+"##");
                    System.out.println("******************************");
                    UserFile userFile=new UserFile();
                    if(userFileId==null || "".equals(userFileId) || "undefined".equals(userFileId)){
                        //新建用户文件表记录
                        userFile.setFileId(Long.parseLong(fileId));
                        userFile.setFileName(filenameNoEx+"."+suffix);
                        userFile.setFileSize(Long.parseLong(size));
                        userFile.setFileParentPath(param.getUploadPath());
                        userFile.setFileType(suffix.toUpperCase());
                        if(param.getUserId()==null)
                            userFile.setUserId(StpUtil.getLoginIdAsLong()); //获取当前登陆的用户ID
                        else
                            userFile.setUserId(Long.parseLong(param.getUserId()));
                        userFileMapper.insert(userFile);

                        //更新内存空间大小
                        Long userId=null;
                        if(param.getUserId()==null)
                            userId=StpUtil.getLoginIdAsLong(); //获取当前登陆的用户ID
                        else
                            userId=Long.parseLong(param.getUserId());
                        User user=userService.getUserById(userId);
                        user.setUsedSize(user.getUsedSize()+Long.parseLong(size));
                        userService.updateById(user);

                    }else{  //覆盖同名文件，修改用户文件表记录
                        //历史版本表插入记录
                        FileHistory fileHistory=new FileHistory();
                        UserFile userFile1=userFileMapper.selectById(Long.parseLong(userFileId));
                        fileHistory.setUserFileId(Long.parseLong(userFileId)).setFileId(userFile1.getFileId()).setFileName(userFile1.getFileName())
                                .setFileSize(userFile1.getFileSize());
                        if(StpUtil.isLogin())
                            fileHistory.setUpdatePerson(StpUtil.getLoginIdAsLong());
                        else
                            fileHistory.setUpdatePerson(0L);
                        fileHistoryMapper.insert(fileHistory);

                        userFile.setUserFileId(Long.parseLong(param.getUserFileId()));
                        userFile.setFileId(Long.parseLong(fileId));
                        userFile.setFileSize(Long.parseLong(size));
                        userFileMapper.updateById(userFile);

                        //更新内存空间大小
                        Long userId=null;
                        if(param.getUserId()==null)
                            userId=StpUtil.getLoginIdAsLong(); //获取当前登陆的用户ID
                        else
                            userId=Long.parseLong(param.getUserId());
                        User user=userService.getUserById(userId);
                        user.setUsedSize(user.getUsedSize()+Long.parseLong(param.getSize())-userFile1.getFileSize());
                        userService.updateById(user);
                    }
                    fileUploadVO.setUserFileId(userFile.getUserFileId()); //获取用户文件ID
                    fileUploadVO.setChunk(null);
                    fileUploadVO.setSucceed(null);
                    fileUploadVO.setFlag("2");
                    fileUploadVO.setFileId(Long.parseLong(fileId));
                    System.out.println("@@@@@@@@@@@@@@@@@@@flag==2@@@@@@@@@@@@@@@@@@@@");
                    fileUploadRedisUtils.set(md5,fileUploadVO);
                    return (FileUploadVO)fileUploadRedisUtils.get(md5);
                }else if(index>=1){
                    System.out.println("*************************    @@@"+index+"@@@    ***************************");
                    fileUploadVO.setChunk(index-1);
                    fileUploadVO.setSucceed(index);
                }
            }
        }
        fileUploadRedisUtils.set(md5,fileUploadVO);
        return (FileUploadVO)fileUploadRedisUtils.get(md5);
    }

    //秒传逻辑，秒传后插入或者修改用户文件表
    public Long fastUpload(FastUploadParam param){
        System.out.println("===============================");
        System.out.println("秒传逻辑");
        System.out.println("===============================");

        Long fileId = Long.parseLong(param.getFileId());
        //秒传文件表引用次数+1
        Files files=new Files();
        files=fileMapper.selectById(fileId);
        files.setCitationsCount(files.getCitationsCount()+1);
        fileMapper.updateById(files);

        String name = param.getName();
        String filenameNoEx = FileNameUtil.getFileNameNoEx(name);  //不带扩展名的文件名
        String suffix = FileNameUtil.getExtensionName(name);  //扩展名
        Long size = Long.parseLong(param.getSize());
        String userFileId = param.getUserFileId();
        System.out.println("******************************");
        System.out.println("userFileId="+userFileId);
        System.out.println("******************************");
        UserFile userFile=new UserFile();
        if(userFileId==null || "".equals(userFileId) || "undefined".equals(userFileId)){
            //新建用户文件表记录
            userFile.setFileId(fileId);
            userFile.setFileName(filenameNoEx+"."+suffix);
            userFile.setFileSize(size);
            userFile.setFileParentPath(param.getUploadPath());
            userFile.setFileType(suffix.toUpperCase());
            if(param.getUserId()==null)
                userFile.setUserId(StpUtil.getLoginIdAsLong()); //获取当前登陆的用户ID
            else
                userFile.setUserId(Long.parseLong(param.getUserId()));
            userFileMapper.insert(userFile);

            //更新内存空间大小
            Long userId=null;
            if(param.getUserId()==null)
                userId=StpUtil.getLoginIdAsLong(); //获取当前登陆的用户ID
            else
                userId=Long.parseLong(param.getUserId());
            User user=userService.getUserById(userId);
            user.setUsedSize(user.getUsedSize()+Long.parseLong(param.getSize()));
            userService.updateById(user);
        }else{  //覆盖同名文件，修改用户文件表记录
            //历史版本表插入记录
            FileHistory fileHistory=new FileHistory();
            UserFile userFile1=userFileMapper.selectById(Long.parseLong(userFileId));
            fileHistory.setUserFileId(Long.parseLong(userFileId)).setFileId(userFile1.getFileId()).setFileName(userFile1.getFileName())
                    .setFileSize(userFile1.getFileSize());
            if(StpUtil.isLogin())
                fileHistory.setUpdatePerson(StpUtil.getLoginIdAsLong());
            else
                fileHistory.setUpdatePerson(0L);
            fileHistoryMapper.insert(fileHistory);

            userFile.setUserFileId(Long.parseLong(param.getUserFileId()));
            userFile.setFileId(fileId);
            userFile.setFileSize(size);
            userFileMapper.updateById(userFile);

            //更新内存空间大小
            Long userId=null;
            if(param.getUserId()==null)
                userId=StpUtil.getLoginIdAsLong(); //获取当前登陆的用户ID
            else
                userId=Long.parseLong(param.getUserId());
            User user=userService.getUserById(userId);
            user.setUsedSize(user.getUsedSize()+Long.parseLong(param.getSize())-userFile1.getFileSize());
            userService.updateById(user);
        }
        return userFile.getUserFileId();
    }

    public Long getOriSize(FastUploadParam param){
        System.out.println("=========getOriSize===========");
        System.out.println("userFileId="+param.getUserFileId());
        Long fileSize=null;
        UserFile userFile=userFileMapper.selectById(Long.parseLong(param.getUserFileId()));
        if(userFile!=null)
            fileSize=userFile.getFileSize();
        System.out.println("fileSize="+fileSize);
        System.out.println("==============================");
        return fileSize;
    }

}
