package eksam2.ast;

public abstract class KoitAstVisitor<T> {

    protected abstract T visit(KoitNum num);
    protected abstract T visit(KoitVar var);
    protected abstract T visit(KoitAdd add);
    protected abstract T visit(KoitMul mul);

    protected abstract T visit(KoitRet ret);
    protected abstract T visit(KoitSend send);
    protected abstract T visit(KoitBlock block);
    protected abstract T visit(KoitCheck check);

    public final T visit(KoitNode node) {
        return node.accept(this);
    }
}
