package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.CreateDirectoryParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFileService extends BaseService {

    public List<UserFile> getDir(String path){
        return userFileMapper.getDir(path,StpUtil.getLoginIdAsLong());
    }

    public List<UserFile> getFiles(String path){
        return userFileMapper.getFiles(path, StpUtil.getLoginIdAsLong());
    }


    public UserFile createDir(CreateDirectoryParam param) throws UpdateException{

        List<UserFile> dirByName = userFileMapper.getDirByName(param.getFileName(), StpUtil.getLoginIdAsLong());

        if (dirByName.size()>0)
            throw new UpdateException().setMsg("已经存在同名的目录，请更换目录名后尝试");


        UserFile userFile = new UserFile().setFileId(0L).setFileType("DIR").setFileSize(0L).setFileName(param.getFileName()).setUserId(StpUtil.getLoginIdAsLong()).setFileParentPath(param.getPath());
        userFileMapper.insert(userFile);

        return userFile;

    }
}
