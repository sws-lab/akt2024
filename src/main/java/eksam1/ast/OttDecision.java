package eksam1.ast;

import java.util.List;

public class OttDecision extends OttNode {
    private final OttNode trueExpr;
    private final OttNode falseExpr;

    public OttDecision(OttNode trueChoice, OttNode falseChoice) {
        this.trueExpr = trueChoice;
        this.falseExpr = falseChoice;
    }

    public OttNode getTrueExpr() {
        return trueExpr;
    }

    public OttNode getFalseExpr() {
        return falseExpr;
    }

    @Override
    protected List<? extends OttNode> getAbstractNodeList() {
        return List.of(trueExpr, falseExpr);
    }

    @Override
    public <T> T accept(OttAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
