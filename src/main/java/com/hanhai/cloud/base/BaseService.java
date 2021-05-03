package com.hanhai.cloud.base;

import com.github.pagehelper.PageHelper;
import com.hanhai.cloud.entity.GroupRelationship;
import com.hanhai.cloud.mapper.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wmgx
 * @create 2021-04-27-19:06
 **/
public class BaseService {

    /**
     * 文件历史
     */
    @Resource
    protected FileHistoryMapper fileHistoryMapper;


    /**
     * 文件收集箱
     */
    @Resource
    protected FileInboxMapper fileInboxMapper;

    /**
     * 文件操作
     */
    @Resource
    protected FileMapper fileMapper;

    /**
     * 组操作
     */
    @Resource
    protected GroupMapper groupMapper;

    /**
     * 组关系
     */
    @Resource
    protected GroupRelationshipMapper groupRelationshipMapper;


    /**
     * 内部分享关系
     */
    @Resource
    protected InnerShareMapper innerShareMapper;

    /**
     * 回收站记录
     */
    @Resource
    protected ReceivingRecordMapper receivingRecordMapper;

    /**
     * 回收站
     */
    @Resource
    protected RecycleMapper recycleMapper;

    /**
     * 系统设置项
     */
    @Resource
    protected SystemSettingsMapper systemSettingsMapper;

    /**
     * 标签
     */
    @Resource
    protected TagMapper tagMapper;

    /**
     * 标签关系
     */
    @Resource
    protected TagRelationshipMapper tagRelationshipMapper;
    /**
     * 用户文件
     */
    @Resource
    protected UserFileMapper userFileMapper;

    /**
     * 用户相关
     */
    @Resource
    protected UserMapper userMapper;

    /**
     * 用户分享
     */
    @Resource
    protected UserShareMapper userShareMapper;


    /**
     * 分页方法，在查询之前调用，传入分页参数
     *
     * @param pageParam
     */
    public void startPage(PageParam pageParam) {
        if (pageParam != null) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            return;
        }

        PageHelper.startPage(PageParam.DEFAULT_PAGE_NUM, PageParam.DEFAULT_PAGE_SIZE);
    }
}
