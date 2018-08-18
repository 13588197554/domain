package com.fly.pojo.vo;

/**
 * @author david
 * @date 04/08/18 18:49
 */
public class InputParam {

    private Object body;

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "InputParam{" +
                "body=" + body +
                '}';
    }
}
