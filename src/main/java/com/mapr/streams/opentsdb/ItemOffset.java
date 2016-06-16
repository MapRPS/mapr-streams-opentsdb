package com.mapr.streams.opentsdb;

/**
 * Created by chufe on 16/06/16.
 */
public class ItemOffset {
    private String name;
    private Integer offset;

    public ItemOffset(String name, Integer offset) {
        this.name = name;
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public Integer getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "ItemOffset{" +
                "name='" + name + '\'' +
                ", offset=" + offset +
                '}';
    }
}
