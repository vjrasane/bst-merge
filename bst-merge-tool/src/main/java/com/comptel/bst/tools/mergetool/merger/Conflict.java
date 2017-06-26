package com.comptel.bst.tools.mergetool.merger;

import java.util.HashMap;
import java.util.Map;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.OutputTree;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.comparison.differences.Difference.Branch;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

public class Conflict implements OutputTree.OutputElement {

    private Map<Branch, Difference> branches = new HashMap<Branch, Difference>();

    public Conflict(Difference local, Difference remote) {
        setLocal(local);
        setRemote(remote);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Conflict other = (Conflict) obj;
        if (!this.hasDiff(other.getLocal()))
            return false;
        if (!this.hasDiff(other.getRemote()))
            return false;
        return true;
    }

    public Difference getLocal() {
        return this.branches.get(Branch.LOCAL);
    }

    public Difference getRemote() {
        return this.branches.get(Branch.REMOTE);
    }

    public boolean hasDiff(Difference diff) {
        return CommonUtils.nullSafeEquals(this.getLocal(), diff) || CommonUtils.nullSafeEquals(this.getRemote(), diff);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLocal() == null) ? 0 : getLocal().hashCode());
        result = prime * result + ((getRemote() == null) ? 0 : getRemote().hashCode());
        return result;
    }

    public void setLocal(Difference diff) {
        this.branches.put(Branch.LOCAL, diff);
    }

    public void setRemote(Difference diff) {
        this.branches.put(Branch.REMOTE, diff);
    }

    @Override
    public String toString() {
        return "[ " + getLocal() + ", " + getRemote() + " ]";
    }

    public Element getTargetElement() {
        return getLocal().getTargetElement();
    }

    @Override
    public void addTo(OutputTree tree) {
        tree.add(Branch.LOCAL, this.getTargetElement(), this.getLocal()).add(Branch.REMOTE, this.getTargetElement(), this.getRemote());
    }

    public Difference get(Branch branch) {
        return this.branches.get(branch);
    }

}
