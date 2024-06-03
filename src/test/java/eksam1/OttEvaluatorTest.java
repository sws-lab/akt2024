package eksam1;

import eksam1.ast.OttNode;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static eksam1.ast.OttNode.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OttEvaluatorTest {

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

    static final BooleanSupplier falseOracle = () -> false;
    static final BooleanSupplier trueOracle = () -> true;

    static BooleanSupplier altOracle(boolean start) {
        return new BooleanSupplier() {
            boolean state = !start;

            @Override
            public boolean getAsBoolean() {
                state = !state;
                return state;
            }
        };
    }

    @Test
    public void test01_num() {
        checkEval(num(1), trueOracle, 1);
        checkEval(num(0), trueOracle, 0);

        checkEval(num(1), falseOracle, 1);
        checkEval(num(0), falseOracle, 0);
    }

    @Test
    public void test02_var() {
        checkEval(var("x"), trueOracle, 0);
        checkEval(var("kala"), trueOracle, 10);
        checkEval(var("foo"), trueOracle, 5);
        checkEval(var("bar"), trueOracle, -2);
    }

    @Test
    public void test03_binop() {
        checkEval(add(num(2), num(3)), trueOracle, 5);
        checkEval(sub(num(2), num(3)), trueOracle, -1);
        checkEval(add(sub(num(2), num(3)), sub(num(5), num(10))), trueOracle, -6);
    }

    @Test
    public void test04_decision() {
        checkEval(decision(num(2), num(3)), trueOracle, 2);
        checkEval(decision(num(2), num(3)), falseOracle, 3);

        checkEval(decision(decision(num(2), num(3)), decision(num(5), num(10))), trueOracle, 2);
        checkEval(decision(decision(num(2), num(3)), decision(num(5), num(10))), falseOracle, 10);
        checkEval(decision(decision(num(2), num(3)), decision(num(5), num(10))), altOracle(true), 3);
        checkEval(decision(decision(num(2), num(3)), decision(num(5), num(10))), altOracle(false), 5);

        checkEval(add(decision(num(2), num(3)), decision(num(5), num(10))), trueOracle, 7);
        checkEval(add(decision(num(2), num(3)), decision(num(5), num(10))), falseOracle, 13);
        checkEval(add(decision(num(2), num(3)), decision(num(5), num(10))), altOracle(true), 12);
        checkEval(add(decision(num(2), num(3)), decision(num(5), num(10))), altOracle(false), 8);
    }

    @Test
    public void test05_seq() {
        checkEval(seq(num(1), num(2)), trueOracle, 2);
        checkEval(seq(num(1), num(2), num(3)), trueOracle, 3);
        checkEval(add(seq(num(1), num(2)), num(3)), trueOracle, 5);
        checkEval(add(seq(num(1), num(2)), num(3)), falseOracle, 5);
        checkEval(add(seq(num(1), num(2)), seq(num(4), num(5))), trueOracle, 7);
        checkEval(seq(seq(num(1), num(2)), num(3)), trueOracle, 3);
        checkEval(seq(num(1), seq(num(2)), num(3)), trueOracle, 3);
        checkEval(seq(seq(num(5))), trueOracle, 5);
    }

    @Test
    public void test06_assign() {
        checkEval(seq(assign("x", num(1)), var("x")), trueOracle, 1);
        checkEval(seq(assign("x", num(1)), assign("y", num(2)), add(var("x"),  var("y"))), trueOracle, 3);
        checkEval(seq(assign("x", num(6)), assign("y", add(var("x"), num(1))), var("y")), trueOracle, 7);

        checkEval(add(seq(assign("x", num(10)), num(5)), var("x")), trueOracle, 15);
        checkEval(add(var("x"), seq(assign("x", num(10)), num(5))), trueOracle, 5);

        checkEval(seq(assign("x", decision(num(1), num(2))), add(var("x"), num(1))), trueOracle, 2);
        checkEval(seq(assign("x", decision(num(1), num(2))), add(var("x"), num(1))), falseOracle, 3);
        checkEval(seq(decision(assign("x", num(1)), assign("y", num(1))), var("x")), trueOracle, 1);
        checkEval(seq(decision(assign("x", num(1)), assign("y", num(1))), var("x")), falseOracle, 0);
        checkEval(seq(decision(assign("x", num(1)), assign("y", num(1))), var("y")), trueOracle, 0);
        checkEval(seq(decision(assign("x", num(1)), assign("y", num(1))), var("y")), falseOracle, 1);
    }

    @Test
    public void test07_assign_value() {
        fail("See test avalikustatakse pärast eksamit! Omistamisel on ka väärtus: (x = 6) + x peaks töötama!");
    }

    @Test
    public void test08_multiple() {
        fail("See test avalikustatakse pärast eksamit! Lihtsalt natuke keerulisemad avaldised!");
    }


    private void checkEval(OttNode node, BooleanSupplier oracle, int expected) {
        assertEquals(expected, OttEvaluator.eval(node, env, oracle));
    }
}
