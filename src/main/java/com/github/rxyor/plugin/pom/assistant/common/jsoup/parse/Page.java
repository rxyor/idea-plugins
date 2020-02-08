package com.github.rxyor.plugin.pom.assistant.common.jsoup.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/8 周六 21:45:00
 * @since 1.0.0
 */
public class Page<T> {

    private int pageIndex = 1;
    private int pageSize;
    private int total;
    private List<T> items;

    public Page(Integer pageIndex, Integer pageSize, Integer total, List<T> items) {
        if (pageIndex == null || pageIndex <= 0) {
            throw new IllegalArgumentException("pageIndex must satisfy [pageIndex  > 0]");
        }
        if (pageSize == null || pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must satisfy [pageSize  > 0]");
        }
        if (total == null || total < 0) {
            throw new IllegalArgumentException("total must satisfy [total  >= 0]");
        }

        this.pageSize = pageSize;
        this.total = total;
        this.items = Optional.ofNullable(items).orElse(new ArrayList<>(0));
    }

    public Page(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPages() {
        int extra = total % pageSize == 0 ? 0 : 1;
        return (total / pageSize) + extra;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getItems() {
        return items;
    }

    public boolean hasNextPage() {
        return pageIndex < this.getPages();
    }

    public boolean hasPrePage() {
        return pageIndex > 1;
    }

    public void nextPage() {
        if (hasNextPage()) {
            pageIndex++;
        }
    }

    public void prePage() {
        if (hasPrePage()) {
            pageIndex--;
        }
    }

    @Override
    public String toString() {
        return "Page{" +
            "items=" + items +
            ", pageIndex=" + pageIndex +
            ", pageSize=" + pageSize +
            ", pages=" + getPages() +
            ", total=" + total +
            '}';
    }
}
