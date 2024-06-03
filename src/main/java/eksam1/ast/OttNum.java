package eksam1.ast;

import toylangs.AbstractNode;

import java.util.List;

public class OttNum extends OttNode {
    private final int value;

    public OttNum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    protected List<? extends AbstractNode> getAbstractNodeList() {
        return List.of(dataNode(value));
    }

    @Override
    public <T> T accept(OttAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
