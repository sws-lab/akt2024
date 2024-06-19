package eksam2.ast;

import eksam2.KoitActor;
import toylangs.AbstractNode;

import java.util.List;

public class KoitCheck extends KoitStmt {
    private final KoitActor actor;
    private final KoitExpr expr;
    private final KoitStmt trueStmt;
    private final KoitStmt falseStmt;

    public KoitCheck(KoitActor actor, KoitExpr expr, KoitStmt trueChoice, KoitStmt falseChoice) {
        this.actor = actor;
        this.expr = expr;
        this.trueStmt = trueChoice;
        this.falseStmt = falseChoice;
    }

    public KoitActor getActor() {
        return actor;
    }

    public KoitExpr getExpr() {
        return expr;
    }

    public KoitStmt getTrueStmt() {
        return trueStmt;
    }

    public KoitStmt getFalseStmt() {
        return falseStmt;
    }

    @Override
    protected List<? extends AbstractNode> getAbstractNodeList() {
        return List.of(dataNode(actor), expr, trueStmt, falseStmt);
    }

    @Override
    public <T> T accept(KoitAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
