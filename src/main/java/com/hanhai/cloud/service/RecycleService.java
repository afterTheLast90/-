package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.Recycle;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecycleService extends BaseService {

    public List<Recycle> getRecycleFiles(Long userId) { return recycleMapper.getRecycleFile(StpUtil.getLoginIdAsLong()); }

}
