package com.fly.pojo.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author david
 * @date 03/08/18 20:33
 */
public class Page<T> {

    private Integer total;
    private Integer page;
    private Integer count;
    private List<T> body = new ArrayList<>();

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<T> getBody() {
        return body;
    }

    public void setBody(List<T> body) {
        this.body = body;
    }
}
