package eksam2.ast;

import eksam2.KoitActor;
import toylangs.AbstractNode;

import java.util.List;

public abstract class KoitNode extends AbstractNode {

    public static KoitNum num(int value) {
        return new KoitNum(value);
    }

    public static KoitVar var(String name) {
        return new KoitVar(name);
    }

    public static KoitAdd add(KoitExpr left, KoitExpr right) {
        return new KoitAdd(left, right);
    }

    public static KoitMul mul(KoitExpr left, KoitExpr right) {
        return new KoitMul(left, right);
    }


    public static KoitRet ret(KoitExpr expr) {
        return new KoitRet(expr);
    }

    public static KoitSend send(KoitActor sender, KoitActor receiver, String name, KoitExpr expr) {
        return new KoitSend(sender, receiver, name, expr);
    }

    public static KoitBlock block(List<KoitStmt> stmts) {
        return new KoitBlock(stmts);
    }

    public static KoitBlock block(KoitStmt... stmts) {
        return block(List.of(stmts));
    }

    public static KoitCheck check(KoitActor actor, KoitExpr expr, KoitStmt trueStmt, KoitStmt falseStmt) {
        return new KoitCheck(actor, expr, trueStmt, falseStmt);
    }


    public abstract <T> T accept(KoitAstVisitor<T> visitor);
}
