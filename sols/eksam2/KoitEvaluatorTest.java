package eksam2;

import eksam2.ast.KoitStmt;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static eksam2.KoitActor.Alice;
import static eksam2.KoitActor.Bob;
import static eksam2.ast.KoitNode.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KoitEvaluatorTest {

    private Map<String, Integer> aliceEnv;
    private Map<String, Integer> bobEnv;

    @Before
    public void setUp() {
        aliceEnv = new HashMap<>();
        aliceEnv.put("x", 0);
        aliceEnv.put("y", 0);
        aliceEnv.put("z", 0);
        aliceEnv.put("result", 0);
        aliceEnv.put("kala", 10);
        aliceEnv.put("foo", 5);
        aliceEnv.put("bar", -2);

        bobEnv = new HashMap<>();
        bobEnv.put("x", 0);
        bobEnv.put("y", 0);
        bobEnv.put("kala", 10);
        bobEnv.put("tmp", -1);

        aliceEnv = Collections.unmodifiableMap(aliceEnv);
        bobEnv = Collections.unmodifiableMap(bobEnv);
    }

    @Test
    public void test01_num() {
        checkEval(block(ret(num(1))), 1);
        checkEval(block(ret(num(0))), 0);
        checkEval(block(ret(num(2))), 2);
    }

    @Test
    public void test02_var() {
        checkEval(block(ret(var("x"))), 0);
        checkEval(block(ret(var("y"))), 0);
        checkEval(block(ret(var("z"))), 0);
        checkEval(block(ret(var("kala"))), 10);
        checkEval(block(ret(var("foo"))), 5);
        checkEval(block(ret(var("bar"))), -2);
    }

    @Test
    public void test03_exp() {
        checkEval(block(ret(add(num(1), num(2)))), 3);
        checkEval(block(ret(add(num(0), num(0)))), 0);
        checkEval(block(ret(mul(num(2), num(3)))), 6);
    }


    @Test
    public void test04_check() {
        checkEval(block(check(Alice, num(1),
                block(ret(num(10))),
                block(ret(num(20))))), 10);
        checkEval(block(check(Alice, num(0),
                block(ret(num(10))),
                block(ret(num(20))))), 20);
    }

    @Test
    public void test05_send() {
        checkEval(block(
                send(Bob, Alice, "x", num(1)),
                ret(var("x"))), 1);
        checkEval(block(
                send(Alice, Alice, "x", num(20)),
                ret(var("x"))), 20);
        checkEval(block(
                send(Alice, Bob, "x", var("foo")),
                send(Bob, Alice, "y", add(var("x"), num(1))),
                ret(var("y"))), 6);
    }

    @Test
    public void test06_ret() {
        checkEval(block(ret(num(1)), ret(num(2))), 1);
        checkEval(block(
                check(Alice, num(1),
                        block(ret(num(10))),
                        send(Alice, Alice, "x", num(20))),
                ret(var("x"))), 10);
        checkEval(block(
                check(Alice, num(0),
                    block(ret(num(10))),
                    send(Alice, Alice, "x", num(20))),
                ret(var("x"))), 20);
    }

    @Test
    public void test07_undef() {
        checkEvalException(block(ret(var("a"))));
        checkEvalException(block(send(Alice, Bob, "x", var("a"))));
        checkEvalException(block(send(Alice, Bob, "qwe", var("x"))));
        checkEvalException(block(
                send(Bob, Alice, "x", var("foo")),
                ret(var("x"))));
        checkEval(block(
                send(Alice, Bob, "x", var("foo")),
                ret(var("x"))), 0);

        // README näide
        checkEvalException(block(
                send(Alice, Bob, "x", var("x")),
                send(Alice, Bob, "y", add(var("x"), num(1))),
                check(Bob, add(var("x"), var("y")),
                        block(
                                send(Bob, Bob, "tmp", mul(num(10), var("x"))),
                                send(Alice, Bob, "kala", var("tmp")), // Alice.tmp undefined
                                send(Bob, Alice, "result", add(var("kala"), num(1)))),
                        block(
                                send(Bob, Alice, "result", mul(num(10), var("y"))))),
                ret(var("result"))));
    }

    @Test
    public void test08_misc() {
        KoitStmt stmt = block(
                send(Alice, Bob, "x", var("x")),
                send(Alice, Bob, "y", add(var("x"), num(1))),
                check(Bob, add(var("x"), var("y")),
                        block(
                                send(Bob, Bob, "tmp", mul(num(10), var("x"))),
                                send(Alice, Bob, "kala", var("foo")),
                                send(Bob, Alice, "result", add(var("kala"), var("tmp")))),
                        block(
                                send(Bob, Alice, "result", mul(num(10), var("y"))))),
                ret(var("result")));
        checkEval(stmt, 5);
    }


    private void checkEval(KoitStmt stmt, int expected) {
        assertEquals(expected, KoitEvaluator.eval(stmt, aliceEnv, bobEnv));
    }

    private void checkEvalException(KoitStmt stmt) {
        assertThrows(KoitException.class, () -> KoitEvaluator.eval(stmt, aliceEnv, bobEnv));
    }
}
