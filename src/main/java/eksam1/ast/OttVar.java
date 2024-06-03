package eksam1.ast;

import toylangs.AbstractNode;

import java.util.Collections;
import java.util.List;

public class OttVar extends OttNode {
    private final String name;

    public OttVar(String name) {
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
    public <T> T accept(OttAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
