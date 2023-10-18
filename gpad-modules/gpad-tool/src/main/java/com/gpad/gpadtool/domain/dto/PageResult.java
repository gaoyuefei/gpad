package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName PageResult.java
 * @Description 分页结果集
 * @createTime 2023年09月22日 16:13:00
 */
@Data
@ApiModel(value = "PageResult",description = "分页结果集")
public class PageResult<T> {
    @ApiModelProperty(value =  "总数量")
    private long total;
    @ApiModelProperty(value =  "页数")
    private long page;
    @ApiModelProperty(value =  "每页显示的数据大小")
    private long pageSize;
    @ApiModelProperty(value =  "数据列表")
    private List<T> data;

    public PageResult<T> setTotal(int total) { this.total = total; return this; } public PageResult<T> setPage(int page) { this.page = page; return this; }
    public PageResult<T> setPageSize(int pageSize) { this.pageSize = pageSize; return this; }
    public PageResult<T> setData(List<T> data) { this.data = data; return this; }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public PageResult() {}

    public PageResult(List<T> list, long page, long pageSize, long total) {
        this.data = list;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }
}
