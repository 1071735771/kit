package com.lnwazg.kit.datastructure;

/**
 * 三个对象
 * @author nan.li
 * @version 2018年5月16日
 */
public class Triple<L,M,R> {
    private L l;
    private M m;
    private R r;

    public Triple() {
    }

    public Triple(L left,M middle, R right) {
        this.l = left;
        this.m = middle;
        this.r = right;
    }

    public L getLeft() {
        return l;
    }

    public void setLeft(L l) {
        this.l = l;
    }

    public R getRight() {
        return r;
    }

    public void setRight(R r) {
        this.r = r;
    }

    public M getM()
    {
        return m;
    }

    public void setM(M m)
    {
        this.m = m;
    }
}
