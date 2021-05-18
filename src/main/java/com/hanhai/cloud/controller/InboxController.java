package com.hanhai.cloud.controller;

import com.hanhai.cloud.base.PageParam;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.service.InboxService;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.vo.FileInboxListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@Controller
public class InboxController {

    @Autowired
    private InboxService inboxService;

    @GetMapping("/inbox")
    public String stu() {
        return "inbox.html";
    }


    @GetMapping("/inboxList")
    @ResponseBody
    public R<PageResult> getInboxList(@Validated PageParam param) {
        PageResult pageResult = new PageResult(inboxService.getInboxListForCurrentUser(param));

        pageResult.setList(pageResult.getList()
                .stream()
                .map(o -> BeanUtils.convertTo(o, FileInboxListVO.class))
                .collect(Collectors.toList()));

//        List<Object> list = pageResult.getList();
//
//        List<Object> vo = new ArrayList<>(list.size());
//
//        for (Object o : list) {
//            vo.add(BeanUtils.convertTo(o, FileInboxListVO.class));
//        }
//
//        pageResult.setList(vo);
        return R.getSuccess().setData(pageResult);
    }
}
