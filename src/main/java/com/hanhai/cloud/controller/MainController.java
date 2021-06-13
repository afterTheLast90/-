package com.hanhai.cloud.controller;

import com.hanhai.cloud.base.BaseException;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.*;
import com.hanhai.cloud.service.UserFileService;
import com.hanhai.cloud.service.UserService;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.vo.HistoryVO;
import com.hanhai.cloud.vo.UserFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Validated
public class MainController {
    @Autowired
    UserFileService userFileService;
    @Autowired
    UserService userService;

    @GetMapping(value = {"/main","/"})
    public String mainPage(@RequestParam(name = "path",required = false, defaultValue = "/")String path, Model model) {
        model.addAttribute("path", path);
        return "main";
    }

    // 得到文件数据
    @GetMapping("/main/data")
    @ResponseBody
    public R<List<UserFileVO>> getMainData(@RequestParam(value = "path", required = false, defaultValue = "/")String path, Model model){
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
        Integer number = userFileList.size() + dirs.size();
        System.out.println(number);
        List<UserFileVO> files = new ArrayList<>(number);
        for (UserFile dir : dirs) {
            files.add(BeanUtils.convertTo(dir,UserFileVO.class).setFileNumber(number));
        }
        for (UserFile userFile : userFileList) {
            files.add(BeanUtils.convertTo(userFile,UserFileVO.class).setFileNumber(number));
        }

//        List<UserFileVO> files = userFileService.getFiles(path).stream().map(i -> BeanUtils.convertTo(i, UserFileVO.class)).collect(Collectors.toList());

        return new R(ResultCode.SUCCESS_NO_SHOW).setData(files);

    }

    // 模糊搜索 当前目录下的文件
    @GetMapping("/main/query")
    @ResponseBody
    public R<PageResult> getFileByNameAndPath( QueryFileParams fileParams){
        List<UserFile> userFiles = userFileService.queryByNameAndPath(fileParams);
            return new R(ResultCode.SUCCESS_NO_SHOW).setData(new PageResult(userFiles, UserFileVO.class));
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

    @PostMapping("/search/restoreFiles")
    @ResponseBody
    public R restoreFiles(@NotNull(message="ID不能为空") Long id){
        userFileService.restoringFiles(id);
        return R.getSuccess();
    }

    @GetMapping("/getFileHistory")
    @ResponseBody
    public R history(@NotNull(message = "ID不能为空") Long fileId){
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(userFileService.getFileHistory(fileId).stream().map(i->{
            return BeanUtils.convertTo(i,HistoryVO.class).setUpdatePersonName(i.getUpdatePerson()==-1?"匿名用户":userService.getUserById(i.getUpdatePerson()).getUserName());
        }));
    }

    @GetMapping("/fileInfo")
    @ResponseBody
    public R getFileId(@NotNull(message = "文件名不能为空") Long id){
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(userFileService.getById(id));
    }

    @PostMapping("/file/reduction")
    @ResponseBody
    public R fileReduction(@RequestBody Map map){
        Object id = map.get("id");
        if (id==null)
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("id不能为空");
        Long reductionId = Long.parseLong(id.toString());
        userFileService.restoringFiles(reductionId);
        return new R(ResultCode.SUCCESS).setMsg("还原成功");
    }
}
