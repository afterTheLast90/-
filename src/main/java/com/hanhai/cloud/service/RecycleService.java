package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.dto.RecycleSum;
import com.hanhai.cloud.entity.Files;
import com.hanhai.cloud.entity.Recycle;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.entity.UserFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RecycleService extends BaseService {

    @Autowired
    UserFileService userFileService;
    @Autowired
    UserService userService;
    public List<Recycle> getRecycleFiles() {
        return recycleMapper.getRecycleFile(StpUtil.getLoginIdAsLong());
    }


    @Transactional
    public void deleted(Long id) {
        Files getFilesByRecycleId = userFileMapper.getFilesByRecycleId(id, StpUtil.getLoginIdAsLong());
        User user=userMapper.selectById(StpUtil.getLoginIdAsLong());
        getFilesByRecycleId.setCitationsCount(getFilesByRecycleId.getCitationsCount()-1);
        userFileMapper.updateFiles(getFilesByRecycleId);
        recycleMapper.deleteById(id);
        user.setUsedSize(user.getUsedSize()-getFilesByRecycleId.getFileSize());
        userMapper.updateById(user);
    }
    public void deletedAll(){
        List<Recycle> sourceFiles= recycleMapper.getRecycleAllFiles();
        User user=userMapper.selectById(StpUtil.getLoginIdAsLong());
        for(Recycle s:sourceFiles){
            Files getFilesByRecycleId = userFileMapper.getFilesByRecycleId(s.getRecycleId(), StpUtil.getLoginIdAsLong());
            getFilesByRecycleId.setCitationsCount(getFilesByRecycleId.getCitationsCount()-1);
            userFileMapper.updateFiles(getFilesByRecycleId);

            recycleMapper.deleteById(s);
            user.setUsedSize(user.getUsedSize()-getFilesByRecycleId.getFileSize());
            userMapper.updateById(user);
        }
    }
    @Transactional
    public void reduction(Long[] ids, String target) {
        List<UserFile> targetFiles = userFileMapper.getFilesByParent(target, StpUtil.getLoginIdAsLong());
        User user=userMapper.selectById(StpUtil.getLoginIdAsLong());
        Set<String> targetFilesSet = new HashSet<String>();
        for (UserFile targetFile : targetFiles) {
            targetFilesSet.add((targetFile.getFileName() + " " + targetFile.getFileType()).toLowerCase());
        }
        for (Long id : ids) {
            UserFile byRecycleIdTopOne = userFileMapper.getByRecycleIdTopOne(id, StpUtil.getLoginIdAsLong());
            Recycle recyclefile=recycleMapper.getById(id);
            if ("DIR".equals(byRecycleIdTopOne.getFileType())){
                Queue<String> sourcePath = new LinkedList<>();
                Queue<String> targetPath = new LinkedList<>();
                if ((targetFilesSet.contains((byRecycleIdTopOne.getFileName() + " " + byRecycleIdTopOne.getFileType()).toLowerCase()))) {
                    int i = 1;
                    while (targetFilesSet.contains((byRecycleIdTopOne.getFileName() + "(" + i + ")" + byRecycleIdTopOne.getFileType()).toLowerCase())) {
                        i++;
                    }
                    byRecycleIdTopOne.setFileName(byRecycleIdTopOne.getFileName() + "(" + i + ")");
                }
                    sourcePath.add(byRecycleIdTopOne.getFileParentPath()+byRecycleIdTopOne.getUserFileId()+'/');
                    byRecycleIdTopOne.setFileParentPath(target);
                    byRecycleIdTopOne.setDeleted(false);
                    byRecycleIdTopOne.setRecycleId(0L);
                    byRecycleIdTopOne.setCreatedTime(LocalDateTime.now());
                    userFileService.reductionById(byRecycleIdTopOne);

                    user.setUsedSize(user.getUsedSize()+byRecycleIdTopOne.getFileSize());
                    userMapper.updateById(user);

                    recyclefile.setDeleted(true);
                    recycleMapper.reductionById(recyclefile);

                    targetPath.add(target+byRecycleIdTopOne.getUserFileId()+'/');
                    while(!sourcePath.isEmpty()){
                        List<UserFile> files= userFileMapper.getReductionFilesByParent(sourcePath.element(),StpUtil.getLoginIdAsLong());
                        if (files.size()==0){
                            sourcePath.poll();
                            targetPath.poll();
                            continue;
                        }
                        for(UserFile file:files){
                            file.setFileParentPath(targetPath.element());
                            if("DIR".equals(file.getFileType())){
                                sourcePath.add(sourcePath.element()+file.getUserFileId()+"/");
                                file.setDeleted(false);
                                file.setRecycleId(0L);
                                file.setCreatedTime(LocalDateTime.now());
                                userFileService.reductionById(file);

                                user.setUsedSize(user.getUsedSize()+file.getFileSize());
                                userMapper.updateById(user);

                                targetPath.add(targetPath.element()+file.getUserFileId()+'/');
                            }else{
                                file.setDeleted(false);
                                file.setRecycleId(0L);
                                file.setCreatedTime(LocalDateTime.now());
                                userFileService.reductionById(file);

                                user.setUsedSize(user.getUsedSize()+file.getFileSize());
                                userMapper.updateById(user);
                            }
                        }
                        sourcePath.poll();
                        targetPath.poll();
                    }
            }else{
                byRecycleIdTopOne.setFileParentPath(target);
                int i=1;
                int dotIndex=byRecycleIdTopOne.getFileName().lastIndexOf(".");
                if((targetFilesSet.contains(byRecycleIdTopOne.getFileName()+" "+byRecycleIdTopOne.getFileType().toLowerCase()))){
                    String fileName,fileType;
                    if(dotIndex == -1){
                        fileName =byRecycleIdTopOne.getFileName();
                        fileType="";
                    }else{
                        fileName = byRecycleIdTopOne.getFileName().substring(0, dotIndex);
                        fileType = byRecycleIdTopOne.getFileName().substring(dotIndex);
                    }
                    while (targetFilesSet.contains((fileName + "(" + i + ")" + fileType + " " + byRecycleIdTopOne.getFileType()).toLowerCase())) {
                        i++;
                    }
                    byRecycleIdTopOne.setFileName(fileName + "(" + i + ")" + fileType);
                }
                byRecycleIdTopOne.setDeleted(false);
                byRecycleIdTopOne.setRecycleId(0L);
                byRecycleIdTopOne.setCreatedTime(LocalDateTime.now());
                userFileService.reductionById(byRecycleIdTopOne);

                user.setUsedSize(user.getUsedSize()+byRecycleIdTopOne.getFileSize());
                userMapper.updateById(user);

                recyclefile.setDeleted(true);
                recycleMapper.reductionById(recyclefile);
            }
        }
    }
    @Transactional
    public void reducing(Long id,String target){
        List<UserFile> targetFiles = userFileMapper.getFilesByParent(target, StpUtil.getLoginIdAsLong());
        User user=userMapper.selectById(StpUtil.getLoginIdAsLong());
        Set<String> targetFilesSet = new HashSet<String>();
        for (UserFile targetFile : targetFiles) {
            targetFilesSet.add((targetFile.getFileName() + " " + targetFile.getFileType()).toLowerCase());
        }
        UserFile byRecycleIdTopOne = userFileMapper.getByRecycleIdTopOne(id, StpUtil.getLoginIdAsLong());
        Recycle recyclefile=recycleMapper.getById(id);
        if ("DIR".equals(byRecycleIdTopOne.getFileType())){
            Queue<String> sourcePath = new LinkedList<>();
            Queue<String> targetPath = new LinkedList<>();
            if ((targetFilesSet.contains((byRecycleIdTopOne.getFileName() + " " + byRecycleIdTopOne.getFileType()).toLowerCase()))) {
                int i = 1;
                while (targetFilesSet.contains((byRecycleIdTopOne.getFileName() + "(" + i + ")" + byRecycleIdTopOne.getFileType()).toLowerCase())) {
                    i++;
                }
                byRecycleIdTopOne.setFileName(byRecycleIdTopOne.getFileName() + "(" + i + ")");
            }
                sourcePath.add(byRecycleIdTopOne.getFileParentPath()+byRecycleIdTopOne.getUserFileId()+'/');
                byRecycleIdTopOne.setFileParentPath(target);
                byRecycleIdTopOne.setDeleted(false);
                byRecycleIdTopOne.setRecycleId(0L);
                byRecycleIdTopOne.setCreatedTime(LocalDateTime.now());
                userFileService.reductionById(byRecycleIdTopOne);

                user.setUsedSize(user.getUsedSize()+byRecycleIdTopOne.getFileSize());
                userMapper.updateById(user);

                recyclefile.setDeleted(true);
                recycleMapper.reductionById(recyclefile);

                targetPath.add(target+byRecycleIdTopOne.getUserFileId()+'/');
                while(!sourcePath.isEmpty()){
                    List<UserFile> files= userFileMapper.getReductionFilesByParent(sourcePath.element(),StpUtil.getLoginIdAsLong());
                    if (files.size()==0){
                        sourcePath.poll();
                        targetPath.poll();
                        continue;
                    }
                    for(UserFile file:files){
                        file.setFileParentPath(targetPath.element());
                        if("DIR".equals(file.getFileType())){
                            sourcePath.add(sourcePath.element()+file.getUserFileId()+"/");
                            file.setDeleted(false);
                            file.setRecycleId(0L);
                            file.setCreatedTime(LocalDateTime.now());
                            userFileService.reductionById(file);

                            user.setUsedSize(user.getUsedSize()+file.getFileSize());
                            userMapper.updateById(user);

                            targetPath.add(targetPath.element()+file.getUserFileId()+'/');
                        }else{
                            file.setDeleted(false);
                            file.setRecycleId(0L);
                            file.setCreatedTime(LocalDateTime.now());
                            userFileService.reductionById(file);

                            user.setUsedSize(user.getUsedSize()+file.getFileSize());
                            userMapper.updateById(user);
                        }
                    }
                    sourcePath.poll();
                    targetPath.poll();
                }
        }else{
            byRecycleIdTopOne.setFileParentPath(target);
            int i=1;
            int dotIndex=byRecycleIdTopOne.getFileName().lastIndexOf(".");
            if((targetFilesSet.contains(byRecycleIdTopOne.getFileName()+" "+byRecycleIdTopOne.getFileType().toLowerCase()))){
                String fileName,fileType;
                if(dotIndex == -1){
                    fileName =byRecycleIdTopOne.getFileName();
                    fileType="";
                }else{
                    fileName = byRecycleIdTopOne.getFileName().substring(0, dotIndex);
                    fileType = byRecycleIdTopOne.getFileName().substring(dotIndex);
                }
                while (targetFilesSet.contains((fileName + "(" + i + ")" + fileType + " " + byRecycleIdTopOne.getFileType()).toLowerCase())) {
                    i++;
                }
                byRecycleIdTopOne.setFileName(fileName + "(" + i + ")" + fileType);
            }
            byRecycleIdTopOne.setDeleted(false);
            byRecycleIdTopOne.setRecycleId(0L);
            byRecycleIdTopOne.setCreatedTime(LocalDateTime.now());
            userFileService.reductionById(byRecycleIdTopOne);

            user.setUsedSize(user.getUsedSize()+byRecycleIdTopOne.getFileSize());
            userMapper.updateById(user);

            recyclefile.setDeleted(true);
            recycleMapper.reductionById(recyclefile);
        }
    }

    @Transactional
    public  void update30DaysNotDelete(){

        List<Recycle> allNotDelete = recycleMapper.getAllNotDelete();

        for (Recycle recycle : allNotDelete) {
            List<RecycleSum> fileIdSizeUserId = userFileMapper.getFileIdSizeUserId(recycle.getRecycleId());
            long toolSize = 0L;
            long userId = 0;
            for (RecycleSum recycleSum : fileIdSizeUserId) {
                userId = recycleSum.getUserId();
                toolSize+=recycleSum.getSum();
                Files files = fileMapper.selectById(recycleSum.getFileId());
                files.setCitationsCount(files.getCitationsCount()-recycleSum.getCou());
                fileMapper.updateById(files);
            }
            if(fileIdSizeUserId.size()!=0){
                User user = userService.getUserById(userId);
                user.setUsedSize(user.getUsedSize()-toolSize);
                userService.updateById(user);
            }
            recycleMapper.deleteById(recycle.getRecycleId());
        }
    }
}

