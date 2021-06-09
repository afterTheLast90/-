package com.hanhai.cloud.controller;

import com.hanhai.cloud.base.R;
import com.hanhai.cloud.entity.Recycle;
import com.hanhai.cloud.params.ReducingParams;
import com.hanhai.cloud.params.ReductionParams;
import com.hanhai.cloud.service.RecycleService;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.vo.RecycleVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Controller
@Validated
public class RecycleBinController {
    @Resource
    RecycleService recycleService;


    @GetMapping("/RecycleBin")
    public String RecycleBinPage(Model model) {
    List<Recycle> recycleList=recycleService.getRecycleFiles();
    List<RecycleVO> files=new ArrayList<>(recycleList.size());
    for(Recycle recycleFile :recycleList){
        files.add(BeanUtils.convertTo(recycleFile, RecycleVO.class));
    }
    model.addAttribute("files",files);
    return "RecycleBin";
    }

    @DeleteMapping("/recycle/{recycleId}")
    @ResponseBody
    public R deleted(@PathVariable("recycleId") @NotNull(message="ID不能为空") Long recycleId){
        recycleService.deleted(recycleId);
        return R.getSuccess();
    }

    @PostMapping("/recycle/deletedAll")
    @ResponseBody
    public R deletedAll(){
        recycleService.deletedAll();
        return R.getSuccess();
    }

    @PostMapping("/recycle/reduction")
    @ResponseBody
    public R reduction(@RequestBody @Validated ReductionParams reductionParams){
        recycleService.reduction(reductionParams.getIds(),reductionParams.getTarget());
        return R.getSuccess();
    }

    @PostMapping("/recycle/reducing")
    @ResponseBody
    public R reducing(@RequestBody @Validated ReducingParams reducingParams){
        recycleService.reducing(reducingParams.getId(),reducingParams.getTarget());
        return R.getSuccess();
    }
}