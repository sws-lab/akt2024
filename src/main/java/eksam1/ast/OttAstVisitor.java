package eksam1.ast;

public abstract class OttAstVisitor<T> {

    protected abstract T visit(OttNum num);
    protected abstract T visit(OttAdd add);
    protected abstract T visit(OttSub sub);

    protected abstract T visit(OttVar var);
    protected abstract T visit(OttAssign assign);

    protected abstract T visit(OttSeq seq);
    protected abstract T visit(OttDecision decision);

    public final T visit(OttNode node) {
        return node.accept(this);
    }
}
