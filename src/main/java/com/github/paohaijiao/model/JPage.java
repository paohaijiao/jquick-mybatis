package com.github.paohaijiao.model;

import java.util.List;

public class JPage<T> {

    private List<T> records;

    private long total;

    private int size;

    private int current;

    private int pages;


    public List<T> getRecords() {
        return records;
    }

    public JPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public JPage<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public int getSize() {
        return size;
    }

    public JPage<T> setSize(int size) {
        this.size = size;
        return this;
    }

    public int getCurrent() {
        return current;
    }

    public JPage<T> setCurrent(int current) {
        this.current = current;
        return this;
    }

    public int getPages() {
        return pages;
    }

    public JPage<T> setPages(int pages) {
        this.pages = pages;
        return this;
    }
}
