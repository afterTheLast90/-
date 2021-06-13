package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.base.PageParam;
import com.hanhai.cloud.entity.Files;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FileService extends BaseService {

    public Files getById(Long id){
        return fileMapper.selectById(id);
    }

    public String getFilePathByID(Long id){
        return fileMapper.getByfileId(id);
    }

    public List<Files> getAllFiles(PageParam param){
        startPage(param);
        return  fileMapper.getAll();
    }

    public void delete(Long id){
        fileMapper.deleteById(id);
    }
    public List<Files>getAllZerroCitationsCount(){
        return fileMapper.getAllZerroCitationsCount();
    };
}
