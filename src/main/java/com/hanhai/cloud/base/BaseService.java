package com.hanhai.cloud.base;

import com.github.pagehelper.PageHelper;
import com.hanhai.cloud.entity.GroupRelationship;
import com.hanhai.cloud.mapper.*;

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
    FileHistoryMapper fileHistoryMapper;


    /**
     * 文件收集箱
     */
    @Resource
    FileInboxMapper fileInboxMapper;

    /**
     * 文件操作
     */
    @Resource
    FileMapper fileMapper;

    /**
     * 组操作
     */
    @Resource
    GroupMapper groupMapper;

    /**
     * 组关系
     */
    @Resource
    GroupRelationship groupRelationship;


    /**
     * 内部分享关系
     */
    @Resource
    InnerShareMapper innerShareMapper;

    /**
     * 回收站记录
     */
    @Resource
    ReceivingRecordMapper receivingRecordMapper;

    /**
     * 回收站
     */
    @Resource
    RecycleMapper recycleMapper;

    /**
     * 系统设置项
     */
    @Resource
    SystemSettingsMapper systemSettingsMapper;

    /**
     * 标签
     */
    @Resource
    TagMapper tagMapper;

    /**
     * 标签关系
     */
    @Resource
    TagRelationshipMapper tagRelationshipMapper;
    /**
     * 用户文件
     */
    @Resource
    UserFileMapper userFileMapper;

    /**
     * 用户相关
     */
    @Resource
    UserMapper userMapper;

    /**
     * 用户分享
     */
    @Resource
    UserShareMapper userShareMapper;


    /**
     * 分页方法，在查询之前调用，传入分页参数
     * @param pageParam
     */
    public void startPage(PageParam pageParam){
        if (pageParam!=null){
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            return;
        }

        PageHelper.startPage(PageParam.DEFAULT_PAGE_NUM, PageParam.DEFAULT_PAGE_SIZE);
    }
}
