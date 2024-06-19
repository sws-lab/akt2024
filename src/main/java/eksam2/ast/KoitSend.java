package eksam2.ast;

import eksam2.KoitActor;
import toylangs.AbstractNode;

import java.util.List;

public class KoitSend extends KoitStmt {
    private final KoitActor sender;
    private final KoitActor receiver;
    private final String name;
    private final KoitExpr expr;

    public KoitSend(KoitActor sender, KoitActor receiver, String name, KoitExpr expr) {
        this.sender = sender;
        this.receiver = receiver;
        this.name = name;
        this.expr = expr;
    }

    public KoitActor getSender() {
        return sender;
    }

    public KoitActor getReceiver() {
        return receiver;
    }

    public String getName() {
        return name;
    }

    public KoitExpr getExpr() {
        return expr;
    }

    @Override
    protected List<AbstractNode> getAbstractNodeList() {
        return List.of(dataNode(sender), dataNode(receiver), dataNode(name), expr);
    }

    @Override
    public <T> T accept(KoitAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
