package com.comptel.bst.tools.diff.parser.entity;

import java.util.List;

import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;

public class Yaml extends Element {

    private static final long serialVersionUID = 1L;

    public static final Tag TAG = Tag.unique("yaml").hide();

    public Yaml() {
        super(TAG);
    }

    public List<Difference> compare(Yaml other) {
        return this.compare(null, other);
    }

}
