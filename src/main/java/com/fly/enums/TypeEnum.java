package com.fly.enums;

public enum TypeEnum {
    CAST("CAST"),
    DIRECTOR("DIRECTOR"),
    WRITER("WRITER"),
    TOP250("TOP250"),
    IN_THEATERS("IN_THEATERS"),
    COMMING_SOON("COMMING_SOON");

    private String name;

    TypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StatusEnum{" +
                "name='" + name + '\'' +
                '}';
    }
}
