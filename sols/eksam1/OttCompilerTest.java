package eksam1;

import cma.CMaInterpreter;
import cma.CMaProgram;
import cma.CMaStack;
import cma.CMaUtils;
import eksam1.ast.OttNode;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.*;
import java.util.function.BooleanSupplier;

import static eksam1.OttEvaluatorTest.*;
import static eksam1.ast.OttNode.*;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OttCompilerTest {

    @SuppressWarnings("FieldCanBeLocal")
    private final int ORACLE_SIZE = 10;

    private Map<String, Integer> env;

    @Before
    public void setUp() {
        env = new HashMap<>();
        env.put("x", 0);
        env.put("y", 0);
        env.put("z", 0);
        env.put("kala", 10);
        env.put("foo", 5);
        env.put("bar", -2);

        env.put(OttCompiler.ORACLE_NAME, env.size());

        env = Collections.unmodifiableMap(env);
    }

    @Test
    public void test01_num() {
        checkCompile(num(1), trueOracle, 1);
        checkCompile(num(0), trueOracle, 0);

        checkCompile(num(1), falseOracle, 1);
        checkCompile(num(0), falseOracle, 0);
    }

    @Test
    public void test02_var() {
        checkCompile(var("x"), trueOracle, 0);
        checkCompile(var("kala"), trueOracle, 10);
        checkCompile(var("foo"), trueOracle, 5);
        checkCompile(var("bar"), trueOracle, -2);
    }

    @Test
    public void test03_binop() {
        checkCompile(add(num(2), num(3)), trueOracle, 5);
        checkCompile(sub(num(2), num(3)), trueOracle, -1);
        checkCompile(add(sub(num(2), num(3)), sub(num(5), num(10))), trueOracle, -6);
    }

    @Test
    public void test04_decision() {
        checkCompile(decision(num(2), num(3)), trueOracle, 2);
        checkCompile(decision(num(2), num(3)), falseOracle, 3);

        checkCompile(decision(decision(num(2), num(3)), decision(num(5), num(10))), trueOracle, 2);
        checkCompile(decision(decision(num(2), num(3)), decision(num(5), num(10))), falseOracle, 10);
        checkCompile(decision(decision(num(2), num(3)), decision(num(5), num(10))), altOracle(true), 3);
        checkCompile(decision(decision(num(2), num(3)), decision(num(5), num(10))), altOracle(false), 5);

        checkCompile(add(decision(num(2), num(3)), decision(num(5), num(10))), trueOracle, 7);
        checkCompile(add(decision(num(2), num(3)), decision(num(5), num(10))), falseOracle, 13);
        checkCompile(add(decision(num(2), num(3)), decision(num(5), num(10))), altOracle(true), 12);
        checkCompile(add(decision(num(2), num(3)), decision(num(5), num(10))), altOracle(false), 8);
    }

    @Test
    public void test05_seq() {
        checkCompile(seq(num(1), num(2)), trueOracle, 2);
        checkCompile(seq(num(1), num(2), num(3)), trueOracle, 3);
        checkCompile(add(seq(num(1), num(2)), num(3)), trueOracle, 5);
        checkCompile(add(seq(num(1), num(2)), num(3)), falseOracle, 5);
        checkCompile(add(seq(num(1), num(2)), seq(num(4), num(5))), trueOracle, 7);
        checkCompile(seq(seq(num(1), num(2)), num(3)), trueOracle, 3);
        checkCompile(seq(num(1), seq(num(2)), num(3)), trueOracle, 3);
        checkCompile(seq(seq(num(5))), trueOracle, 5);
    }

    @Test
    public void test06_assign() {
        checkCompile(seq(assign("x", num(1)), var("x")), trueOracle, 1);
        checkCompile(seq(assign("x", num(1)), assign("y", num(2)), add(var("x"),  var("y"))), trueOracle, 3);
        checkCompile(seq(assign("x", num(6)), assign("y", add(var("x"), num(1))), var("y")), trueOracle, 7);

        checkCompile(add(seq(assign("x", num(10)), num(5)), var("x")), trueOracle, 15);
        checkCompile(add(var("x"), seq(assign("x", num(10)), num(5))), trueOracle, 5);

        checkCompile(seq(assign("x", decision(num(1), num(2))), add(var("x"), num(1))), trueOracle, 2);
        checkCompile(seq(assign("x", decision(num(1), num(2))), add(var("x"), num(1))), falseOracle, 3);
        checkCompile(seq(decision(assign("x", num(1)), assign("y", num(1))), var("x")), trueOracle, 1);
        checkCompile(seq(decision(assign("x", num(1)), assign("y", num(1))), var("x")), falseOracle, 0);
        checkCompile(seq(decision(assign("x", num(1)), assign("y", num(1))), var("y")), trueOracle, 0);
        checkCompile(seq(decision(assign("x", num(1)), assign("y", num(1))), var("y")), falseOracle, 1);
    }

    @Test
    public void test07_assign_value() {
        checkCompile(seq(assign("x", assign("y", num(42))), var("x")), trueOracle, 42);
        checkCompile(seq(assign("x", assign("y", num(42))), var("y")), trueOracle, 42);
        checkCompile(add(assign("x", num(10)), num(5)), trueOracle, 15);

        checkCompile(assign("foo", num(42)), trueOracle, 42);
        checkCompile(assign("foo", decision(num(42), num(24))), altOracle(true), 42);
    }

    @Test
    public void test08_multiple() {
        checkCompile(seq(assign("foo", num(42)), var("foo")), trueOracle, 42);
        checkCompile(seq(assign("foo", seq(assign("x", add(var("x"), num(1))))), var("x")), trueOracle, 1);

        OttNode expr = add(
                seq(
                        assign("x", num(10)),
                        assign("y", num(12)),
                        decision(
                                add(var("x"), num(3)),
                                sub(var("y"), num(1)))),
                decision(var("x"), var("y")));

        checkCompile(expr, trueOracle, 13 + 10);
        checkCompile(expr, falseOracle, 11 + 12);
        checkCompile(expr, altOracle(true), 13 + 12);
        checkCompile(expr, altOracle(false), 11 + 10);
    }

    // täiendavad testid, mida eksamil ei kasutatud
    @Test
    public void test09_advanced() {
        // max depth 10
        checkCompile(decision(decision(decision(decision(decision(decision(decision(decision(decision(decision(num(1), num(4)), num(5)), num(6)), num(7)), num(8)), num(9)), num(10)), num(11)), num(12)), num(13)), trueOracle, 1);
        // deeper than max, but dead
        checkCompile(decision(decision(decision(decision(decision(decision(decision(decision(decision(decision(decision(decision(num(1),  num(2)), num(3)), num(4)), num(5)), num(6)), num(7)), num(8)), num(9)), num(10)), num(11)), num(12)), num(13)), falseOracle, 13);
    }

    private void checkCompile(OttNode node, BooleanSupplier oracle, int expected) {
        List<String> variables = new ArrayList<>();
        CMaStack initialStack = new CMaStack();
        for (Map.Entry<String, Integer> entry : env.entrySet()) {
            variables.add(entry.getKey());
            initialStack.push(entry.getValue());
        }

        for (int i = 0; i < ORACLE_SIZE; i++) {
            initialStack.push(CMaUtils.bool2int(oracle.getAsBoolean()));
        }

        CMaProgram program = OttCompiler.compile(node, variables);
        CMaStack finalStack = CMaInterpreter.run(program, initialStack);

        assertEquals("stack size", initialStack.size() + 1, finalStack.size());
        assertEquals("result", expected, finalStack.peek());
        for (int i = 0; i < ORACLE_SIZE; i++) {
            assertEquals("oracle unmodified", initialStack.get(variables.size() + i), finalStack.get(variables.size() + i));
        }
    }
}
