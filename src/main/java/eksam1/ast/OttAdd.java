package eksam1.ast;

import toylangs.AbstractNode;

import java.util.List;

public class OttAdd extends OttNode {
    private final OttNode left;
    private final OttNode right;

    public OttAdd(OttNode left, OttNode right) {
        this.left = left;
        this.right = right;
    }

    public OttNode getLeft() {
        return left;
    }

    public OttNode getRight() {
        return right;
    }

    @Override
    protected List<? extends AbstractNode> getAbstractNodeList() {
        return List.of(left, right);
    }

    @Override
    public <T> T accept(OttAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
