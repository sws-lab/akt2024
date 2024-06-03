package eksam1;

import eksam1.ast.OttNode;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static eksam1.ast.OttNode.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OttAstTest {

    @Test
    public void test01_num() {
        legal("1", num(1));
        legal("0", num(0));
        legal("0002", num(2));
        legal("123", num(123));
        legal(" 456  ", num(456));

        illegal("1.0");
        illegal("1 2");
        illegal("-1");
    }

    @Test
    public void test02_var() {
        legal("a", var("a"));
        legal("foo", var("foo"));
        legal("aSd", var("aSd"));
        legal("FOO", var("FOO"));
        legal("_FOO", var("_FOO"));
        legal("  b ", var("b"));

        illegal("foo_");
        illegal("__foo");
        illegal("2a");
        illegal("a2");
    }

    @Test
    public void test03_op() {
        legal("a + b", add(var("a"), var("b")));
        legal("a - b", sub(var("a"), var("b")));
        legal("a | b", decision(var("a"), var("b")));
        legal("(a + b)", add(var("a"), var("b")));

        legal("a + b | c", decision(add(var("a"), var("b")), var("c")));
        legal("a + (b | c)", add(var("a"), decision(var("b"), var("c"))));
        legal("a | b + c", decision(var("a"), add(var("b"), var("c"))));
        legal("(a | b) + c", add(decision(var("a"), var("b")), var("c")));

        illegal("+ a");
        illegal("- 2");
        illegal("a || b");
        illegal("a +");
        illegal("| b");
        illegal("a + | b");
        illegal("((a + b)");
        illegal("(a + b))");
        illegal("a (+ b)");
        illegal("()");
    }

    @Test
    public void test04_assign() {
        legal("x = 10", assign("x", num(10)));
        legal("x = x + 1", assign("x", add(var("x"), num(1))));
        legal("x = y = 0", assign("x", assign("y", num(0))));
        legal("z = x = y + 1", assign("z", assign("x", add(var("y"), num(1)))));

        legal("z = (x = y) + 1", assign("z", add(assign("x", var("y")), num(1))));
        legal("x = a | b", assign("x", decision(var("a"), var("b"))));
        legal("(x = a) | (x = b)", decision(assign("x", var("a")), assign("x", var("b"))));

        illegal("5 = b");
        illegal("(x = 0) = 0");
    }

    @Test
    public void test05_seq() {
        legal("x", var("x"));
        legal("x; y", seq(var("x"), var("y")));
        legal("x; y; z", seq(var("x"), var("y"), var("z")));
        legal("x; y | z", seq(var("x"), decision(var("y"), var("z"))));
        legal("x | y; z", seq(decision(var("x"), var("y")), var("z")));
        legal("x = 10; y = 20; x + y", seq(assign("x", num(10)), assign("y", num(20)), add(var("x"), var("y"))));

        illegal("x;");
        illegal(";x");
        illegal("x; ;y");
        illegal(" ");
    }

    @Test
    public void test06_prog() {
        legal("x | (y; z)", decision(var("x"), seq(var("y"), var("z"))));
        legal("(x; 10) + (y; 20)", add(seq(var("x"), num(10)), seq(var("y"), num(20))));
        legal("(x = 10; 5) + ((y = 20; x) + y)",
                add(seq(assign("x", num(10)), num(5)),
                add(seq(assign("y", num(20)), var("x")), var("y"))));

        legal("(x)", var("x"));
        legal("((x)) + (y)", add(var("x"), var("y")));

        illegal("( )");
        illegal("(x; y) z");
    }

    @Test
    public void test07_prio_assoc() {
        legal("a + b + c", add(add(var("a"), var("b")), var("c")));
        legal("a - b - c", sub(sub(var("a"), var("b")), var("c")));
        legal("a - b + c", add(sub(var("a"), var("b")), var("c")));
        legal("a + b - c", sub(add(var("a"), var("b")), var("c")));
        legal("a | b | c", decision(var("a"), decision(var("b"), var("c"))));
    }

    @Test
    public void test08_multiple() {
        OttNode expr = add(
                seq(
                        assign("x", num(10)),
                        assign("y", num(12)),
                        decision(
                                add(var("x"), num(3)),
                                sub(var("y"), num(1)))),
                decision(var("x"), var("y")));
        legal("(x = 10; y = 12; x + 3 | y - 1) + (x | y)", expr);

        illegal("foo_bar");
    }

    // täiendavad testid, mida eksamil ei kasutatud
    @Test
    public void test09_advanced() {
        legal("x; (y; z)", seq(var("x"), seq(var("y"), var("z"))));
        legal("(x; y); z", seq(seq(var("x"), var("y")), var("z")));
    }

    private void legal(String input, OttNode expectedAst) {
        OttNode actualAst = OttAst.makeOttAst(input);
        assertEquals(expectedAst, actualAst);
    }

    private void illegal(String input) {
        try {
            OttAst.makeOttAst(input);
            fail("expected parse error: " + input);
        } catch (Exception ignored) {

        }
    }
}
