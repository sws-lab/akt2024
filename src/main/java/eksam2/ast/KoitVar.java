package eksam2.ast;

import toylangs.AbstractNode;

import java.util.Collections;
import java.util.List;

public class KoitVar extends KoitExpr {
    private final String name;

    public KoitVar(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    protected List<AbstractNode> getAbstractNodeList() {
        return Collections.singletonList(dataNode(name));
    }

    @Override
    public <T> T accept(KoitAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
