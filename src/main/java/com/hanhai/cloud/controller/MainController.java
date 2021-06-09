package com.hanhai.cloud.controller;

import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.CopyMoveFileParams;
import com.hanhai.cloud.params.CreateDirectoryParam;
import com.hanhai.cloud.params.DeletedParams;
import com.hanhai.cloud.params.ReNameParams;
import com.hanhai.cloud.service.UserFileService;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.vo.UserFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Controller
@Validated
public class MainController {
    @Autowired
    UserFileService userFileService;




    @GetMapping("/main")
    public String mainPage(@RequestParam(value = "path",required = false,defaultValue = "/") String path
                           ,Model model){
        // 当前目录
        if(path.endsWith("/"))
            model.addAttribute("current",path);
        else
            model.addAttribute("current",path+"/");
        // 上一个目录
        if (!path.endsWith("/")){
            path=path+"/";
        }
        if("/".equals(path)){
            model.addAttribute("per","/");
        }else{
            String t;
            t=path.substring(0,path.length()-1);
            t = t.substring(0,t.lastIndexOf('/'));
            model.addAttribute("per",t);
        }

        List<UserFile> userFileList = userFileService.getFiles(path);
        List<UserFile> dirs = userFileService.getDir(path);

        List<UserFileVO> files = new ArrayList<>(userFileList.size()+dirs.size());
        for (UserFile dir : dirs) {
            files.add(BeanUtils.convertTo(dir,UserFileVO.class));
        }
        for (UserFile userFile : userFileList) {
            files.add(BeanUtils.convertTo(userFile,UserFileVO.class));
        }


//        List<UserFileVO> files = userFileService.getFiles(path).stream().map(i -> BeanUtils.convertTo(i, UserFileVO.class)).collect(Collectors.toList());

        model.addAttribute("files",files);
        return "main";
    }


    @PostMapping("/file/createDirectory")
    @ResponseBody
    public R createDirectory(@RequestBody @Validated CreateDirectoryParam param)throws UpdateException {
        UserFile dir = userFileService.createDir(param);
        return R.getSuccess().setData(BeanUtils.convertTo(dir,UserFileVO.class)).setMsg("文件夹创建成功");
    }

    @PostMapping("/file/rename")
    @ResponseBody
    public R reName(@RequestBody @Validated ReNameParams param)throws UpdateException{
        userFileService.reName(param);
        return R.getSuccess();
    }
    @PostMapping("/file/copy")
    @ResponseBody
    public R copy(@RequestBody @Validated  CopyMoveFileParams copyMoveFileParams){
        System.out.println(copyMoveFileParams);
        userFileService.copy(copyMoveFileParams.getIds(),copyMoveFileParams.getTarget());
        return R.getSuccess();
    }
    @PostMapping("/file/move")
    @ResponseBody
    public R move(@RequestBody @Validated  CopyMoveFileParams copyMoveFileParams){
        System.out.println(copyMoveFileParams);
        userFileService.move(copyMoveFileParams.getIds(),copyMoveFileParams.getTarget());
        return R.getSuccess();
    }
    @PostMapping("/file/deleted")
    @ResponseBody
    public R deleted(@RequestBody @Validated DeletedParams deletedParams){
        System.out.println(deletedParams);
        userFileService.deleted(deletedParams.getIds());
        return R.getSuccess();
    }
//    @GetMapping("/getFileHistory")
//    public String fileHistory( @RequestParam("fileId") String fileId,Model model){
//        List <FileHistory> fileHistoryList= userFileService.getFileHistory(fileId);
//        List <FileHistoryVO>history =new ArrayList<>(fileHistoryList.size());
//        for(FileHistory fileHistory:fileHistoryList){
//            history.add(BeanUtils.convertTo(fileHistory,FileHistoryVO.class));
//        }
//        model.addAttribute("history",history);
//        return "main";
//    }
    @GetMapping("/getFileHistory")
    @ResponseBody
    public R history(@NotBlank(message = "ID不能为空") @NotNull(message = "ID不能为空") Long fileId){
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(userFileService.getFileHistory(fileId));
    }
    @GetMapping("/getByName")
    @ResponseBody
    public R getByName(@NotBlank(message = "文件名不能为空") @NotNull(message = "文件名不能为空") String name){
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(userFileService.getByName(name));
    }
}
