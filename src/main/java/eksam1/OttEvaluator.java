package eksam1;

import eksam1.ast.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class OttEvaluator {

    // Väärtusta avaldis otse. Valikuoperaator kasutab oraaklit,
    // et küsida nextBoolean abil, kas valida vasakpoolne või parempoolne argument.
    public static int eval(OttNode node, Map<String, Integer> initialEnv, BooleanSupplier oracle) {
        Map<String, Integer> env = new HashMap<>(initialEnv); // teeme koopia, sest initialEnv pole muudetav
        throw new UnsupportedOperationException();
    }
}


