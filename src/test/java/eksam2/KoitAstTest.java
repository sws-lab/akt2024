package eksam2;

import eksam2.ast.KoitStmt;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static eksam2.KoitActor.Alice;
import static eksam2.KoitActor.Bob;
import static eksam2.ast.KoitNode.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KoitAstTest {

    @Test
    public void test01_num() {
        legal("return 1", block(ret(num(1))));
        legal("return 0", block(ret(num(0))));
        legal("return 0002", block(ret(num(2))));
        legal("return 123", block(ret(num(123))));
        legal(" return \n 456 \n ", block(ret(num(456))));

        illegal("1");
        illegal("return 1.0");
        illegal("return 1 2");
        illegal("return -1");
    }

    @Test
    public void test02_var() {
        legal("return x", block(ret(var("x"))));
        legal("return y", block(ret(var("y"))));
        legal("return z", block(ret(var("z"))));
        legal("return kala", block(ret(var("kala"))));
        legal("return foo", block(ret(var("foo"))));
        legal("return bar", block(ret(var("bar"))));

        illegal("x");
        illegal("return x y");
        illegal("return Alice.x");
    }

    @Test
    public void test03_exp() {
        legal("return 1 + 2", block(ret(add(num(1), num(2)))));
        legal("return 0 + 0", block(ret(add(num(0), num(0)))));
        legal("return 2 * 3", block(ret(mul(num(2), num(3)))));
        legal("return 2 * 3 + 4", block(ret(add(mul(num(2), num(3)), num(4)))));
        legal("return 2 * (3 + 4)", block(ret(mul(num(2), add(num(3), num(4))))));
        legal("return 2 + 3 * 4", block(ret(add(num(2), mul(num(3), num(4))))));

        illegal("1");
        illegal("return 1 2");
        illegal("return 1 +");
        illegal("return 1 + 2 3");
        illegal("return 1 + 2 *");
        illegal("return 1 + 2 * 3 4");
    }

    @Test
    public void test04_send() {
        legal("Alice.x -> Bob.x", block(send(Alice, Bob, "x", var("x"))));
        legal("Alice.(x) -> Bob.x", block(send(Alice, Bob, "x", var("x"))));
        legal("Alice.(x + 1) -> Bob.y", block(send(Alice, Bob, "y", add(var("x"), num(1)))));

        illegal("Alice.x -> Bob.(x)");
        illegal("Alice.x -> Bob.(x+1)");

    }

    @Test
    public void test05_assign() {
        legal("Alice.(x = y)", block(send(Alice, Alice, "x", var("y"))));
        legal("Bob.(x = y + 1)", block(send(Bob, Bob, "x", add(var("y"), num(1)))));
        illegal("Alice . x = 10");
        illegal("Alice . (x = y = 10)");
    }

    @Test
    public void test06_stmts() {
        legal("return 1 return 2", block(ret(num(1)), ret(num(2))));
        legal("if Alice ? x then return 0 else return 1 endif", block(
                check(Alice, var("x"),
                        block(ret(num(0))),
                        block(ret(num(1))))));
        legal("if Alice ? (x+1) then return 0 else return 1 endif", block(
                check(Alice, add(var("x"), num(1)),
                        block(ret(num(0))),
                        block(ret(num(1))))));

        legal("if Alice ? x then Alice.x -> Bob.x else Bob.x -> Alice.x endif", block(
                check(Alice, var("x"),
                        block(send(Alice, Bob, "x", var("x"))),
                        block(send(Bob, Alice, "x", var("x"))))));
        legal("if Alice ? x then Alice.x -> Bob.x else Bob.x -> Alice.x endif return 0", block(
                check(Alice, var("x"),
                        block(send(Alice, Bob, "x", var("x"))),
                        block(send(Bob, Alice, "x", var("x")))),
                ret(num(0))));
        legal("if Alice ? x then Alice.x -> Bob.y else Bob.y -> Alice.x return x endif", block(
                check(Alice, var("x"),
                        block(send(Alice, Bob, "y", var("x"))),
                        block(send(Bob, Alice, "x", var("y")),
                                ret(var("x"))))));
        legal("if Alice ? x then if Bob ? y then Alice.x -> Bob.y else Bob.y -> Alice.x endif else return 0 endif", block(
                check(Alice, var("x"),
                        block(check(Bob, var("y"),
                                block(send(Alice, Bob, "y", var("x"))),
                                block(send(Bob, Alice, "x", var("y"))))),
                        block(ret(num(0))))));
    }



    @Test
    public void test07_send_strict() {
        fail("See test avalikustatakse pärast eksamit! Ilma sulgudeta aritmeetiline avaldis ei ole lubatud term: Alice.x + 1 -> Bob.y on viga!");
    }

    @Test
    public void test08_misc() {
        fail("See test avalikustatakse pärast eksamit! Lihtsalt natuke keerulisemad programmid!");
    }


    private void legal(String input, KoitStmt expectedAst) {
        KoitStmt actualAst = KoitAst.makeKoitAst(input);
        assertEquals(expectedAst, actualAst);
    }

    private void illegal(String input) {
        try {
            KoitAst.makeKoitAst(input);
            fail("expected parse error: " + input);
        } catch (Exception ignored) {

        }
    }
}
