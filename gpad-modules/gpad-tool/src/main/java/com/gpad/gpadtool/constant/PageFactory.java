package com.gpad.gpadtool.constant;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class PageFactory {


    private static final String ASC = "asc";
    public static final int PAGE_SIZE = 20;
    public static final int PAGE_NO = 1;

    public static <T> Page<T> defaultPage() {
        return new Page(1L, 20L);
    }


    public static <T> Page<T> createPage(PageCondition pageCondition) {
        if (ObjectUtil.isNull(pageCondition)) {
            return defaultPage();
        }

        int pageSize = 20;
        int pageNo = 1;

        if (ObjectUtil.isNotNull(Integer.valueOf(pageCondition.getPageSize()))) {
            pageSize = pageCondition.getPageSize();
        }

        if (ObjectUtil.isNotNull(Integer.valueOf(pageCondition.getPageNum()))) {
            pageNo = pageCondition.getPageNum();
        }

        Page<T> page = new Page(pageNo, pageSize);

        return page;
    }

}
