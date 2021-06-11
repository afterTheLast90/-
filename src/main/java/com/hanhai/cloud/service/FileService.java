package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.Files;
import com.hanhai.cloud.mapper.FileMapper;
import org.springframework.stereotype.Service;


@Service
public class FileService extends BaseService {

    public Files getById(Long id){
        return fileMapper.selectById(id);
    }

    public String getFilePathByID(Long id){
        return fileMapper.getByfileId(id);
    }

}
