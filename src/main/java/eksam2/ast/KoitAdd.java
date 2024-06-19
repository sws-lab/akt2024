package eksam2.ast;

import toylangs.AbstractNode;

import java.util.List;

public class KoitAdd extends KoitExpr {
    private final KoitExpr left;
    private final KoitExpr right;

    public KoitAdd(KoitExpr left, KoitExpr right) {
        this.left = left;
        this.right = right;
    }

    public KoitExpr getLeft() {
        return left;
    }

    public KoitExpr getRight() {
        return right;
    }

    @Override
    protected List<? extends AbstractNode> getAbstractNodeList() {
        return List.of(left, right);
    }

    @Override
    public <T> T accept(KoitAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
