package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.Recycle;
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
    public List<Recycle> getRecycleFiles() {
        return recycleMapper.getRecycleFile(StpUtil.getLoginIdAsLong());
    }

    @Transactional
    public void deleted(Long id) {
        recycleMapper.deleteById(id);
    }
    public void deletedAll(){
        List<Recycle> sourceFiles= recycleMapper.getRecycleAllFiles();
        for(Recycle s:sourceFiles){
            recycleMapper.deleteById(s);
        }
    }
    @Transactional
    public void reduction(Long[] ids, String target) {
        List<UserFile> targetFiles = userFileMapper.getFilesByParent(target, StpUtil.getLoginIdAsLong());
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
                                targetPath.add(targetPath.element()+file.getUserFileId()+'/');
                            }else{
                                file.setDeleted(false);
                                file.setRecycleId(0L);
                                file.setCreatedTime(LocalDateTime.now());
                                userFileService.reductionById(file);
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

                recyclefile.setDeleted(true);
                recycleMapper.reductionById(recyclefile);
            }
        }
    }
    @Transactional
    public void reducing(Long id,String target){
        List<UserFile> targetFiles = userFileMapper.getFilesByParent(target, StpUtil.getLoginIdAsLong());
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
                            targetPath.add(targetPath.element()+file.getUserFileId()+'/');
                        }else{
                            file.setDeleted(false);
                            file.setRecycleId(0L);
                            file.setCreatedTime(LocalDateTime.now());
                            userFileService.reductionById(file);
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

            recyclefile.setDeleted(true);
            recycleMapper.reductionById(recyclefile);
        }
    }
}

