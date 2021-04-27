package com.hanhai.cloud.base;

import com.github.pagehelper.Page;
import com.hanhai.cloud.utils.BeanUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分页返回数据的包装类
 * @author wmgx
 * @create 2021-02-17-18:30
 **/
@Data
@Accessors(chain = true)
@Log4j2
public class PageResult {
//    @ApiModelProperty("总页数")
    /**
     * 总页数
     */
    private Integer pageCount;

//    @ApiModelProperty("当前页")
    /**
     * 当前页
     */
    private Integer currentPage;
    /**
     * 当前页条数
     */
//    @ApiModelProperty("当前页条数")
    private Integer currentSize;

    /**
     * 参数要求条数
     */
//    @ApiModelProperty("参数要求条数")
    private Integer pageSize;

    /**
     * 总条数
     */
//    @ApiModelProperty("总条数")
    private Long total;

    /**
     * 数据
     */
//    @ApiModelProperty("数据")
    private List<Object> list;

    /**
     * 将传入的分页的数据包装成分页的参数的包装类
     * @param list
     */
    public PageResult(List list) {
        if (list instanceof Page) {
            Page pageInfo = (Page) list;
            this.pageCount = pageInfo.getPages();
            this.currentPage = pageInfo.getPageNum();
            this.currentSize = list.size();
            this.pageSize = pageInfo.getPageSize();
            this.total = pageInfo.getTotal();
            this.list = pageInfo.getResult();
        } else {
            log.error("转换PageINFO失败");
            this.pageCount = 1;
            this.currentPage = 1;
            this.currentSize = list.size();
            this.pageSize = list.size();
            this.currentSize = list.size();
            this.total = (long) list.size();
            this.list = list;
        }

    }

    /**
     * 将传入的list转换为分页的结果的包装类并转换为相应的类
     * @param list
     * @param cla
     */
    public PageResult(List list, Class cla) {
        if (list instanceof Page) {
            Page pageInfo = (Page) list;
            this.pageCount = pageInfo.getPages();
            this.currentPage = pageInfo.getPageNum();
            this.currentSize = list.size();
            this.pageSize = pageInfo.getPageSize();
            this.total = pageInfo.getTotal();
            this.list =
                    (List<Object>) list.stream().map(i -> BeanUtils.convertTo(i, cla)).collect(Collectors.toList());

        } else {
            System.err.println("转换PageINFO失败");
            this.pageCount = 1;
            this.currentPage = 1;
            this.currentSize = list.size();
            this.pageSize = list.size();
            this.currentSize = list.size();
            this.total = (long) list.size();
            this.list =  (List<Object>) list.stream().map(i -> BeanUtils.convertTo(i, cla)).collect(Collectors.toList());
        }

    }
}
