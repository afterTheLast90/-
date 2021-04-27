package com.hanhai.cloud.base;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import java.util.Map;

/**
 * @author wmgx
 * @create 2021-02-17-18:29
 **/
@Data
@Accessors(chain = true)
public class PageParam {

    /**
     * 默认页面条数
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 默认页面
     */
    public static final int DEFAULT_PAGE_NUM = 1;
    /**
     * 如果不传就是1
     */
    @Min(value = 1, message = "页面大小非法")
    private Integer pageSize = DEFAULT_PAGE_SIZE;
    /**
     * 如果不传就是10
     */
    @Min(value = 1, message = "页码非法")
    private Integer pageNum = DEFAULT_PAGE_NUM;

    /**
     * 设置页面大小
     * @param pageSize
     * @return
     */
    public PageParam setPageSize(Integer pageSize) {

        if (pageSize != null)
            this.pageSize = pageSize;
        else
            this.pageSize = DEFAULT_PAGE_SIZE;
        return this;
    }

    /**
     * 设置页码
     * @param pageNum
     * @return
     */
    public PageParam setPageNum(Integer pageNum) {
        if (pageNum != null)
            this.pageNum = pageNum;
        else
            this.pageNum = DEFAULT_PAGE_NUM;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * 将map中的pageSize与pageNum取出来设置
     * @param map
     */
    public void setFormMap(Map map){
        if (map.containsKey("pageSize"))
            this.pageSize = Integer.valueOf(map.get("pageSize").toString());
        if (map.containsKey("pageNum"))
            this.pageNum = Integer.valueOf(map.get("pageNum").toString());
    }
}