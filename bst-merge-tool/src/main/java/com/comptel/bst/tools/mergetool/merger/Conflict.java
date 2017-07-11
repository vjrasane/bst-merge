package com.comptel.bst.tools.mergetool.merger;

import java.util.HashMap;
import java.util.Map;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.OutputTree;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.comparison.differences.Difference.Branch;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

/*
 * Represents a conflict between two differences.
 */
public class Conflict implements OutputTree.OutputElement {

    /*
     * Stores the differences based on their branch. In theory we could have as many separate
     * branches as we wanted, but right now we support only LOCAL and REMOTE
     */
    private Map<Branch, Difference> branches = new HashMap<Branch, Difference>();

    // The diffs are always passed in this order
    public Conflict(Difference local, Difference remote) {
        setLocal(local);
        setRemote(remote);
    }

    public Difference getLocal() {
        return this.branches.get(Branch.LOCAL);
    }

    public Difference getRemote() {
        return this.branches.get(Branch.REMOTE);
    }

    // Checks whether this conflict contains the given difference
    public boolean hasDiff(Difference diff) {
        return CommonUtils.nullSafeEquals(this.getLocal(), diff) || CommonUtils.nullSafeEquals(this.getRemote(), diff);
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

    // Returns the element that the conflict should be placed at in the output
    public Element getTargetElement() {
        return getLocal().getTargetElement();
    }

    // Enables adding conflicts to an output tree
    @Override
    public void addTo(OutputTree tree) {
        tree.add(Branch.LOCAL, this.getTargetElement(), this.getLocal()).add(Branch.REMOTE, this.getTargetElement(), this.getRemote());
    }

    public Difference get(Branch branch) {
        return this.branches.get(branch);
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLocal() == null) ? 0 : getLocal().hashCode());
        result = prime * result + ((getRemote() == null) ? 0 : getRemote().hashCode());
        return result;
    }

}
