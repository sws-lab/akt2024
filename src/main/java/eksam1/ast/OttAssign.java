package eksam1.ast;

import toylangs.AbstractNode;

import java.util.Arrays;
import java.util.List;

public class OttAssign extends OttNode {
    private final String name;
    private final OttNode expr;

    public OttAssign(String name, OttNode expr) {
        this.name = name;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public OttNode getExpr() {
        return expr;
    }

    @Override
    protected List<AbstractNode> getAbstractNodeList() {
        return Arrays.asList(dataNode(name), expr);
    }

    @Override
    public <T> T accept(OttAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
