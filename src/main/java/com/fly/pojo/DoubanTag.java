package com.fly.pojo;

/**
 * @author david
 * @date 15/08/18 20:31
 */
public class DoubanTag {

    private Integer count;
    private String name;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "count=" + count +
                ", name=\'" + name + '\"' +
                '}';
    }
}
