package eksam2;

import cma.CMaInterpreter;
import cma.CMaProgram;
import cma.CMaStack;
import eksam2.ast.KoitStmt;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.*;

import static eksam2.KoitActor.Alice;
import static eksam2.KoitActor.Bob;
import static eksam2.ast.KoitNode.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KoitCompilerTest {

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
        checkCompile(block(ret(num(1))), 1);
        checkCompile(block(ret(num(0))), 0);
        checkCompile(block(ret(num(2))), 2);
    }

    @Test
    public void test02_var() {
        checkCompile(block(ret(var("x"))), 0);
        checkCompile(block(ret(var("y"))), 0);
        checkCompile(block(ret(var("z"))), 0);
        checkCompile(block(ret(var("kala"))), 10);
        checkCompile(block(ret(var("foo"))), 5);
        checkCompile(block(ret(var("bar"))), -2);
    }

    @Test
    public void test03_exp() {
        checkCompile(block(ret(add(num(1), num(2)))), 3);
        checkCompile(block(ret(add(num(0), num(0)))), 0);
        checkCompile(block(ret(mul(num(2), num(3)))), 6);
    }


    @Test
    public void test04_check() {
        checkCompile(block(check(Alice, num(1),
                block(ret(num(10))),
                block(ret(num(20))))), 10);
        checkCompile(block(check(Alice, num(0),
                block(ret(num(10))),
                block(ret(num(20))))), 20);
    }

    @Test
    public void test05_send() {
        checkCompile(block(
                send(Bob, Alice, "x", num(1)),
                ret(var("x"))), 1);
        checkCompile(block(
                send(Alice, Alice, "x", num(20)),
                ret(var("x"))), 20);
        checkCompile(block(
                send(Alice, Bob, "x", var("foo")),
                send(Bob, Alice, "y", add(var("x"), num(1))),
                ret(var("y"))), 6);
    }

    @Test
    public void test06_ret() {
        checkCompile(block(ret(num(1)), ret(num(2))), 1);
        checkCompile(block(
                check(Alice, num(1),
                        block(ret(num(10))),
                        send(Alice, Alice, "x", num(20))),
                ret(var("x"))), 10);
        checkCompile(block(
                check(Alice, num(0),
                    block(ret(num(10))),
                    send(Alice, Alice, "x", num(20))),
                ret(var("x"))), 20);
    }

    @Test
    public void test07_undef() {
        checkCompileException(block(ret(var("a"))));
        checkCompileException(block(send(Alice, Bob, "x", var("a"))));
        checkCompileException(block(send(Alice, Bob, "qwe", var("x"))));
        checkCompileException(block(
                send(Bob, Alice, "x", var("foo")),
                ret(var("x"))));
        checkCompile(block(
                send(Alice, Bob, "x", var("foo")),
                ret(var("x"))), 0);

        // README näide
        checkCompileException(block(
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
        checkCompile(stmt, 5);
    }


    private void checkCompile(KoitStmt stmt, int expected) {
        List<String> aliceVariables = new ArrayList<>();
        List<String> bobVariables = new ArrayList<>();
        CMaStack initialStack = new CMaStack();
        for (Map.Entry<String, Integer> entry : aliceEnv.entrySet()) {
            aliceVariables.add(entry.getKey());
            initialStack.push(entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : bobEnv.entrySet()) {
            bobVariables.add(entry.getKey());
            initialStack.push(entry.getValue());
        }

        CMaProgram program = KoitCompiler.compile(stmt, aliceVariables, bobVariables);
        CMaStack finalStack = CMaInterpreter.run(program, initialStack);

        assertEquals("stack size", initialStack.size() + 1, finalStack.size());
        assertEquals("result", expected, finalStack.peek());
    }

    private void checkCompileException(KoitStmt stmt) {
        List<String> aliceVariables = new ArrayList<>();
        List<String> bobVariables = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : aliceEnv.entrySet()) {
            aliceVariables.add(entry.getKey());
        }
        for (Map.Entry<String, Integer> entry : bobEnv.entrySet()) {
            bobVariables.add(entry.getKey());
        }

        assertThrows(KoitException.class, () -> KoitCompiler.compile(stmt, aliceVariables, bobVariables));
    }
}
