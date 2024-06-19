package eksam2.ast;

import toylangs.AbstractNode;

import java.util.List;

public class KoitNum extends KoitExpr {
    private final int value;

    public KoitNum(int value) {
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
    public <T> T accept(KoitAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
