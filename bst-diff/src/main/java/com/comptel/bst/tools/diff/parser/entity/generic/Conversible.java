package com.comptel.bst.tools.diff.parser.entity.generic;


public interface Conversible<O> {
    public void convert(O obj);
}