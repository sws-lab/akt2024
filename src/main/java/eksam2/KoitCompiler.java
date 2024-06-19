package eksam2;

import cma.CMaProgram;
import cma.CMaProgramWriter;
import eksam2.ast.*;

import java.util.List;

public class KoitCompiler {

    public static CMaProgram compile(KoitStmt stmt, List<String> aliceVariables, List<String> bobVariables) {
        CMaProgramWriter pw = new CMaProgramWriter();

        return pw.toProgram();
    }
}
