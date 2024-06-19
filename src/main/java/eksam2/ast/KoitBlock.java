package eksam2.ast;

import toylangs.AbstractNode;

import java.util.List;

public class KoitBlock extends KoitStmt {
    private final List<KoitStmt> stmts;

    public KoitBlock(List<KoitStmt> stmts) {
        this.stmts = stmts;
    }

    public List<KoitStmt> getStmts() {
        return stmts;
    }

    @Override
    protected List<? extends AbstractNode> getAbstractNodeList() {
        return stmts;
    }

    @Override
    public <T> T accept(KoitAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
