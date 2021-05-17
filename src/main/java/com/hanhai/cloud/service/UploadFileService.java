package com.hanhai.cloud.service;

import com.github.yitter.idgen.YitIdHelper;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.Files;
import com.hanhai.cloud.params.MultipartFileParam;
import com.hanhai.cloud.mapper.FileMapper;
import com.hanhai.cloud.utils.FileMd5Util;
import com.hanhai.cloud.utils.NameUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UploadFileService extends BaseService {
    /**
     * Map
     *  @param flag(String) 0:未上传,1:部分上传,2:已上传
     *  @param fileId(Long) 文件ID
     */
    private Map<String,Object> map=new HashMap<>();

    @Resource
    FileMapper fileMapper;
    /**
     * 通过md5值查找文件对象
     * @param md5
     * @return
     */
    public Map<String,Object> findByFileMd5(String md5){
        Files file=fileMapper.findByFileMd5(md5);
        Long fileId= null;
        if(file==null){ //没有上传完文件，判断是否上传完成，需要断点续传
            System.out.println("********************************   检测断点续传   ***********************************");
            Integer chunk=(Integer) map.get("chunk");
            System.out.println("********************************   @@@chunk="+chunk+"@@@   ***********************************");
            Long str=(Long) map.get("fileId");
            System.out.println("********************************   @@@fileId="+str+"@@@   ***********************************");
            if(str!=null)
                fileId=str;
            if(chunk!=null){
                //上传了部分，需要断点续传
                map.put("flag","1");
                map.put("filedId",fileId);
            }else{
                //没有上传过文件
                fileId=YitIdHelper.nextId(); //获取下一个要插入的记录的文件ID
                map.put("flag","0");
                map.put("fileId",fileId);
            }
        }else{
            //上传过该文件，秒传即可
            map.put("flag","2");
//            map.put("fileId",file.getFileId());
        }
        return map;
    }

    /**
     * 上传文件
     * @param param 文件表单信息
     * @param multipartFile 文件
     * @return
     */
    public Map<String,Object> realUpload(MultipartFileParam param, MultipartFile multipartFile)
                                    throws IOException,Exception{
        String md5=param.getMd5(); //md5值
        String partMd5=param.getPartMd5(); //分片md5值
        String fileId=param.getId(); //文件id
        String fileName=param.getName();  //文件名
        String size=param.getSize();  //文件大小
        Integer total=Integer.valueOf(param.getTotal()); //总片数
        Integer index=Integer.valueOf(param.getIndex()); //分片序号,当前第几片(从1开始)
        String action=param.getAction(); //上传状态 check：检测；upload：上传
        /*不带扩展名的文件名*/
        String filenameNoEx=NameUtil.getFileNameNoEx(fileName);
        /*扩展名*/
        String suffix= NameUtil.getExtensionName(fileName);
        /**
         * 目录验证
         *  saveDirectory:上传文件的文件夹名(目录)
         *  filePath:上传文件的文件名 fileId.suffix (文件名.文件扩展名)
         */
        String saveDirectory = "F:\\Uploads" + File.separator + fileId;
        String filePath = saveDirectory + File.separator + filenameNoEx + "." + suffix;
        //验证路径是否存在，不存在则创建目录
        File path = new File(saveDirectory);
        if (!path.exists()) {
            path.mkdirs();
        }
        //文件分片名
        File file = new File(saveDirectory, filenameNoEx + "_" + index);
        //根据action不同执行不同操作. check:校验分片是否上传过; upload:直接上传分片
        if("check".equals(action)){
            String md5Str= FileMd5Util.getFileMD5(file);//分片MD5
            if (md5Str != null && md5Str.equals(partMd5)) {
                //分片已上传过
                map.put("flag", "1");
                map.put("fileId", Long.parseLong(fileId));
//                if(!index.equals(total))
//                    return map;
            } else {
                //分片未上传
                map.put("flag", "0");
                map.put("fileId", Long.parseLong(fileId));
                return map;
            }
        } else if("upload".equals(action)){
            //分片上传过程中出错,有残余时需删除分块后,重新上传;上传前检测，如果存在则删除重新上传.
            if (file.exists()) {
                file.delete();
            }
            multipartFile.transferTo(new File(saveDirectory, filenameNoEx + "_" + index));
            map.put("flag", "1");
            map.put("fileId", Long.parseLong(fileId));
//            if(!index.equals(total))
//                return map;
        }

        if(path.isDirectory()){
            File[] fileArray=path.listFiles();
            if(fileArray!=null){
                if (fileArray.length >= total) {
                    //分块全部上传完毕，合并
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@开始合并@@@@@@@@@@@@@@@@@@@@@@@");
                    File newFile=new File(saveDirectory,filenameNoEx+"."+suffix);
                    FileOutputStream outputStream=new FileOutputStream(newFile,true);//文件追加写入
                    for (int i = 0; i < fileArray.length; i++) {
                        File tmpFile = new File(saveDirectory, filenameNoEx + "_" + (i + 1));
                        if(tmpFile.exists())
                            FileUtils.copyFile(tmpFile,outputStream);
                    }
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@合并结束，开始删除@@@@@@@@@@@@@@@@@@@@@@@");
                    for(int i=0;i<fileArray.length;i++){
                        //应该放在循环结束删除 可以避免 因为服务器突然中断 导致文件合并失败 下次也无法再次合并
                        File tmpFile=new File(saveDirectory,filenameNoEx+"_"+(i+1));
                        if(tmpFile.exists())
                            tmpFile.delete();
                    }
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@删除完成@@@@@@@@@@@@@@@@@@@@@@@");
                    outputStream.close();
                    //修改FileRes记录为上传成功
                    Files files=new Files();
                    files.setFileId(Long.parseLong(fileId));
                    files.setFileMd5(md5);
                    files.setFilePath(filePath);
                    files.setFileSize(Long.valueOf(size));
                    files.setStorageLocation(0);
                    files.setCitationsCount(1L);
                    fileMapper.insert(files);
                    map.clear();    //上传完成，清除map
                    map.put("fileId",Long.parseLong(fileId));
                    map.put("flag","2");
                    System.out.println("@@@@@@@@@@@@@@@@@@@flag==2@@@@@@@@@@@@@@@@@@@@");
                    return map;
                }else if(index>=1){
                    System.out.println("*************************    index>=1    ***************************");
                    System.out.println("*************************    @@@"+index+"@@@    ***************************");
                    map.put("chunk",index-1); //保存分片
                    map.put("succeed",index);
                }
            }
        }
        return map;
    }

}
