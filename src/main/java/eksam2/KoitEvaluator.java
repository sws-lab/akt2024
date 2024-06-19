package eksam2;

import eksam2.ast.*;

import java.util.HashMap;
import java.util.Map;

public class KoitEvaluator {

    public static int eval(KoitStmt stmt, Map<String, Integer> initialAliceEnv, Map<String, Integer> initialBobEnv) {
        Map<String, Integer> aliceEnv = new HashMap<>(initialAliceEnv); // teeme koopia, sest initialAliceEnv pole muudetav
        Map<String, Integer> bobEnv = new HashMap<>(initialBobEnv); // teeme koopia, sest initialBobEnv pole muudetav
        throw new UnsupportedOperationException();
    }
}
