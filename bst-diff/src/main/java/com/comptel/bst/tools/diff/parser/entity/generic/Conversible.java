package com.comptel.bst.tools.diff.parser.entity.generic;

/*
 * Interface for any objects that can be converted from another object.
 * This isn't used to any great benefit and is mostly just a remainder in many classes
 * that they represent some other class in another form.
 */
public interface Conversible<O> {
    public void convert(O obj);
}