package eksam1.ast;

import toylangs.AbstractNode;

import java.util.List;

public class OttSeq extends OttNode {
    private final List<OttNode> exprs;

    public OttSeq(List<OttNode> exprs) {
        this.exprs = exprs;
    }

    public List<OttNode> getExprs() {
        return exprs;
    }

    @Override
    protected List<? extends AbstractNode> getAbstractNodeList() {
        return exprs;
    }

    @Override
    public <T> T accept(OttAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
