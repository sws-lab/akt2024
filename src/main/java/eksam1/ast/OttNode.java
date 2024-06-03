package eksam1.ast;

import toylangs.AbstractNode;

import java.util.List;

public abstract class OttNode extends AbstractNode {

    public static OttNum num(int value) {
        return new OttNum(value);
    }

    public static OttAdd add(OttNode left, OttNode right) {
        return new OttAdd(left, right);
    }

    public static OttSub sub(OttNode left, OttNode right) {
        return new OttSub(left, right);
    }


    public static OttVar var(String name) {
        return new OttVar(name);
    }

    public static OttAssign assign(String name, OttNode expr) {
        return new OttAssign(name, expr);
    }


    public static OttSeq seq(List<OttNode> exprs) {
        return new OttSeq(exprs);
    }

    public static OttSeq seq(OttNode... exprs) {
        return seq(List.of(exprs));
    }

    public static OttDecision decision(OttNode trueExpr, OttNode falseExpr) {
        return new OttDecision(trueExpr, falseExpr);
    }


    public abstract <T> T accept(OttAstVisitor<T> visitor);
}
