package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.FileHistory;
import com.hanhai.cloud.entity.Recycle;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.CreateDirectoryParam;
import com.hanhai.cloud.params.QueryFileParams;
import com.hanhai.cloud.params.ReNameParams;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserFileService extends BaseService {

    public List<UserFile> getDir(String path) {
        return userFileMapper.getDir(path, StpUtil.getLoginIdAsLong());
    }
    public List<UserFile> getFiles(String path) {
        return userFileMapper.getFiles(path, StpUtil.getLoginIdAsLong());
    }
    public List<FileHistory> getFileHistory(Long fileId){
        return fileHistoryMapper.getFileHistory(fileId);
    }
    public List<UserFile>  getByName(String name){
        return userFileMapper.getByName(name,StpUtil.getLoginIdAsLong());
    }
    public Integer reductionById(UserFile file){
        return userFileMapper.reductionById(file,StpUtil.getLoginIdAsLong());
    }
    public List<UserFile> getById(Long userFileId){
        return userFileMapper.getFileById(userFileId,StpUtil.getLoginIdAsLong());
    }

    // 根据userFildId, 得到文件数据
    public UserFile getFileById(Long userFileId){
        return userFileMapper.selectById(userFileId);
    }

//    // 根据userFileId 和 shareId 得到文件（用于下载分享文件 检验）
//    public List<UserFile> getFileByFileIdAndShareId(Long userFileId, String shareId){
//        return userShareMapper.getFileByFileIdAndShareId(userFileId, shareId);
//    }

    // 分享信息的 下载次数+1
    public void addDownTime(@Param("shareId")String shareId){
        userShareMapper.addDownTime(shareId);
    }

//    // 判断文件 是否是分享文件夹的子文件
//    public Boolean isChildFile(Long userFileId, String shareId){
//        String parentPath = userShareMapper.getPathByShareId(shareId);
//        String childPath = userShareMapper.getPathByUserFileId(userFileId);
//        if(childPath==null || parentPath==null)
//            return false;
//        if(childPath.indexOf(parentPath) != -1){
//            return true;
//        }
//        return false;
//    }

    @Transactional
    public void copy(Long [] ids, String target) {
        List<UserFile> sourceFiles = userFileMapper.getByIds(ids, StpUtil.getLoginIdAsLong());
        List<UserFile> targetFiles = userFileMapper.getFilesByParent(target, StpUtil.getLoginIdAsLong());
        User user=userMapper.selectById(StpUtil.getLoginIdAsLong());
        Set<String> targetFilesSet = new HashSet<String>();
        for (UserFile targetFile : targetFiles) {
            targetFilesSet.add((targetFile.getFileName() + " " + targetFile.getFileType()).toLowerCase());
        }
        for (UserFile s : sourceFiles) {
            if ("DIR".equals(s.getFileType())) {
                Queue<String> sourcePath = new LinkedList<>();
                Queue<String> targetPath = new LinkedList<>();
                if (targetFilesSet.contains((s.getFileName() + " " + s.getFileType()).toLowerCase())) {
                    int i = 1;
                    while (targetFilesSet.contains((s.getFileName() + "(" + i + ") " + s.getFileType()).toLowerCase())) {
                        i++;
                    }
                    s.setFileName(s.getFileName() + "(" + i + ")");
                }

                sourcePath.add(s.getFileParentPath()+ s.getUserFileId() + "/");
                s.setFileParentPath(target).setUserFileId(null).setShareCount(0);
                userFileMapper.insert(s);
                user.setUsedSize(user.getUsedSize()+s.getFileSize());
                userMapper.updateById(user);
                targetPath.add(target+ s.getUserFileId() + "/");
                while (!sourcePath.isEmpty()) {
                    List<UserFile> files = userFileMapper.getFilesByParent(sourcePath.element(), StpUtil.getLoginIdAsLong());
                    if (files.size()==0){
                        sourcePath.poll();
                        targetPath.poll();
                        continue;
                    }
                    for (UserFile file : files) {
                        file.setFileParentPath(targetPath.element());
                        if ("DIR".equals(file.getFileType())) {
                            sourcePath.add(sourcePath.element()+file.getUserFileId()+"/");
                            file.setUserFileId(null).setShareCount(0);
                            userFileMapper.insert(file);

                            user.setUsedSize(user.getUsedSize()+file.getFileSize());
                            userMapper.updateById(user);

                            targetPath.add(targetPath.element()+file.getUserFileId()+"/");
                        }else{
                            file.setUserFileId(null).setShareCount(0);
                            userFileMapper.insert(file);

                            user.setUsedSize(user.getUsedSize()+file.getFileSize());
                            userMapper.updateById(user);
                        }

                    }
                    sourcePath.poll();
                    targetPath.poll();
                }
            } else {
                s.setUserFileId(null);
                s.setFileParentPath(target);
                int i = 1;
                int dotIndex = s.getFileName().lastIndexOf(".");
                // 如果重名进行重命名操作
                if ((targetFilesSet.contains((s.getFileName() + " " + s.getFileType()).toLowerCase()))) {
                    String fileName, fileType;
                    if (dotIndex == -1) {
                        fileName = s.getFileName();
                        fileType = "";
                    } else {
                        fileName = s.getFileName().substring(0, dotIndex);
                        fileType = s.getFileName().substring(dotIndex);
                    }
                    while (targetFilesSet.contains((fileName + "(" + i + ")" + fileType + " " + s.getFileType()).toLowerCase())) {
                        i++;
                    }
                    s.setFileName(fileName + "(" + i + ")" + fileType);
                }
                s.setCreatedTime(null);
                s.setShareCount(0);
                userFileMapper.insert(s);

                user.setUsedSize(user.getUsedSize()+s.getFileSize());
                userMapper.updateById(user);
            }
        }
    }
    @Transactional
    public void move(Long [] ids, String target) {
        List<UserFile> sourceFiles = userFileMapper.getByIds(ids, StpUtil.getLoginIdAsLong());
        List<UserFile> targetFiles = userFileMapper.getFilesByParent(target, StpUtil.getLoginIdAsLong());
        Set<String> targetFilesSet = new HashSet<String>();
        for (UserFile targetFile : targetFiles) {
            targetFilesSet.add((targetFile.getFileName() + " " + targetFile.getFileType()).toLowerCase());
        }
        for (UserFile s : sourceFiles) {
            if ("DIR".equals(s.getFileType())) {
                Queue<String> sourcePath = new LinkedList<>();
                Queue<String> targetPath = new LinkedList<>();
                if ((targetFilesSet.contains((s.getFileName() + " " + s.getFileType()).toLowerCase()))) {
                    int i = 1;
                    while (targetFilesSet.contains((s.getFileName() + "(" + i + ")" + s.getFileType()).toLowerCase())) {
                        i++;
                    }
                    s.setFileName(s.getFileName() + "(" + i + ")");
                }
                sourcePath.add(s.getFileParentPath()+ s.getUserFileId() + "/");
                s.setFileParentPath(target);
                s.setCreatedTime(LocalDateTime.now());
                userFileMapper.updateById(s);
                targetPath.add(target+ s.getUserFileId() + "/");
                while (!sourcePath.isEmpty()) {
                    List<UserFile> files = userFileMapper.getFilesByParent(sourcePath.element(), StpUtil.getLoginIdAsLong());
                    if (files.size()==0){
                        sourcePath.poll();
                        targetPath.poll();
                        continue;
                    }
                    for (UserFile file : files) {
                        file.setFileParentPath(targetPath.element());
                        if ("DIR".equals(file.getFileType())) {
                            sourcePath.add(sourcePath.element()+file.getUserFileId()+"/");
                            file.setCreatedTime(LocalDateTime.now());
                            userFileMapper.updateById(file);
                            targetPath.add(targetPath.element()+file.getUserFileId()+"/");
                        }else{
                            file.setCreatedTime(LocalDateTime.now());
                            userFileMapper.updateById(file);
                        }
                    }
                    sourcePath.poll();
                    targetPath.poll();
                }
            } else {
                s.setFileParentPath(target);
                int i = 1;
                int dotIndex = s.getFileName().lastIndexOf(".");
                // 如果重名进行重命名操作
                if ((targetFilesSet.contains((s.getFileName() + " " + s.getFileType()).toLowerCase()))) {
                    String fileName, fileType;
                    if (dotIndex == -1) {
                        fileName = s.getFileName();
                        fileType = "";
                    } else {
                        fileName = s.getFileName().substring(0, dotIndex);
                        fileType = s.getFileName().substring(dotIndex);
                    }
                    while (targetFilesSet.contains((fileName + "(" + i + ")" + fileType + " " + s.getFileType()).toLowerCase())) {
                        i++;
                    }
                    s.setFileName(fileName + "(" + i + ")" + fileType);
                }
                s.setCreatedTime(LocalDateTime.now());
                userFileMapper.updateById(s);
            }
        }
    }

    @Transactional
    public void restoringFiles(Long id){
        FileHistory fileHistory=userFileMapper.getFileHistoryById(id);
        UserFile userFile = userFileMapper.selectById(fileHistory.getUserFileId());
        User user=userMapper.selectById(StpUtil.getLoginIdAsLong());
        FileHistory newHistory = new FileHistory();
        newHistory.setFileId(userFile.getFileId())
                .setFileName(userFile.getFileName())
                .setFileSize(userFile.getFileSize());
        fileHistoryMapper.insert(newHistory);
        fileHistoryMapper.deleteById(fileHistory);
        userFile.setFileId(fileHistory.getFileId()).setFileSize(userFile.getFileSize());
        userFileMapper.updateById(userFile);
        user.setUsedSize(user.getUsedSize()+fileHistory.getFileSize()-userFile.getFileSize());
        userMapper.updateById(user);
    }

    @Transactional
        public void deleted(Long [] ids){
        List<UserFile> sourceFiles = userFileMapper.getByIds(ids, StpUtil.getLoginIdAsLong());
        User user=userMapper.selectById(StpUtil.getLoginIdAsLong());
        for (UserFile s : sourceFiles) {
            if ("DIR".equals(s.getFileType())) {
                Queue<String> sourcePath = new LinkedList<>();
                Queue<String> targetPath = new LinkedList<>();

                sourcePath.add(s.getFileParentPath()+ s.getUserFileId() + "/");

                Recycle recycle=new Recycle();
                recycle.setRecycleId(null)
                        .setFileName(s.getFileName())
                        .setFileType(s.getFileType())
                        .setCreatedTime(s.getCreatedTime())
                        .setDeleted(false)
                        .setUpdatedTime(LocalDateTime.now());
                recycleMapper.insert(recycle);

                s.setRecycleId(recycle.getRecycleId());
                userFileMapper.updateById(s);

                userFileMapper.deleteById(s);
                user.setUsedSize(user.getUsedSize()-s.getFileSize());
                userMapper.updateById(user);
                targetPath.add(s.getFileParentPath()+ s.getUserFileId() + "/");
                while (!sourcePath.isEmpty()) {
                    List<UserFile> files = userFileMapper.getFilesByParent(sourcePath.element(), StpUtil.getLoginIdAsLong());
                    if (files.size()==0){
                        sourcePath.poll();
                        targetPath.poll();
                        continue;
                    }
                    for (UserFile file : files) {
                        file.setFileParentPath(targetPath.element());
                        if ("DIR".equals(s.getFileType())) {
                            sourcePath.add(sourcePath.element()+file.getUserFileId()+"/");

                            file.setRecycleId(recycle.getRecycleId());
                            userFileMapper.updateById(file);
                            userFileMapper.deleteById(file);

                            user.setUsedSize(user.getUsedSize()-file.getFileSize());
                            userMapper.updateById(user);

                            targetPath.add(targetPath.element()+file.getUserFileId()+"/");
                        }else{
                            file.setRecycleId(recycle.getRecycleId());
                            userFileMapper.updateById(file);
                            userFileMapper.deleteById(file);

                            user.setUsedSize(user.getUsedSize()-file.getFileSize());
                            userMapper.updateById(user);
                        }
                    }
                    sourcePath.poll();
                    targetPath.poll();
                }
            } else {
                Recycle recycleFile=new Recycle();
                recycleFile.setRecycleId(null).setFileName(s.getFileName()).setFileType(s.getFileType()).setCreatedTime(s.getCreatedTime()).setDeleted(false).setUpdatedTime(LocalDateTime.now());
                recycleMapper.insert(recycleFile);

                s.setRecycleId(recycleFile.getRecycleId());
                userFileMapper.updateById(s);
                userFileMapper.deleteById(s);

                user.setUsedSize(user.getUsedSize()-s.getFileSize());
                userMapper.updateById(user);
            }
        }
    }
    public UserFile createDir(CreateDirectoryParam param) throws UpdateException {
        List<UserFile> dirByName = userFileMapper.getDirByName(param.getPath(), param.getFileName(), StpUtil.getLoginIdAsLong());
        if (dirByName.size() > 0)
            throw new UpdateException().setMsg("已经存在同名的目录，请更换目录名后尝试");
        UserFile userFile = new UserFile().setFileId(0L).setFileType("DIR").setFileSize(0L).setFileName(param.getFileName()).setUserId(StpUtil.getLoginIdAsLong()).setFileParentPath(param.getPath());
        userFileMapper.insert(userFile);
        return userFile;
    }
    public UserFile reName(ReNameParams param) throws UpdateException{
        UserFile file = userFileMapper.selectById(param.getFileId());
        if (file == null)
            throw new UpdateException().setMsg("参数错误");
        List<UserFile> dirByName =userFileMapper.getFileByNameAndType(file.getFileParentPath(),param.getFileName(),file.getFileType(),StpUtil.getLoginIdAsLong());
        if(dirByName.size()>0)
            throw new UpdateException().setMsg("已经存在同名文件或目录，请更换目录名后尝试");
        int i = 1;
        int dotIndex = file.getFileName().lastIndexOf(".");
        String fileType;
        if (dotIndex == -1) {
            fileType = "";
        } else {
            fileType = file.getFileName().substring(dotIndex);
        }
        file.setFileName(param.getFileName()+fileType);
        userFileMapper.updateById(file);
        return file;
    }


    public String getFileTypeByUserFileId(Long userFileId){
        return userFileMapper.getFileTypeByUserFileId(userFileId, StpUtil.getLoginIdAsLong());
    }
    public Long getFileIdByUserFileId(Long userFileId){
        return userFileMapper.getFileIdByUserFileId(userFileId, StpUtil.getLoginIdAsLong());
    }

    // 模糊搜索 当前目录下的文件
    public List<UserFile> queryByNameAndPath(QueryFileParams fileParams){
        startPage(fileParams);
        return userFileMapper.getFileByNameAndPath(fileParams.getFileParentPath(), fileParams.getFileName(), StpUtil.getLoginIdAsLong());
    }
}
