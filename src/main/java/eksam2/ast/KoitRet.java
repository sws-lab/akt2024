package eksam2.ast;

import toylangs.AbstractNode;

import java.util.List;

public class KoitRet extends KoitStmt {
    private final KoitExpr expr;

    public KoitRet(KoitExpr expr) {
        this.expr = expr;
    }

    public KoitExpr getExpr() {
        return expr;
    }

    @Override
    protected List<? extends AbstractNode> getAbstractNodeList() {
        return List.of(expr);
    }

    @Override
    public <T> T accept(KoitAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
