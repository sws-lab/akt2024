package eksam3;

import cma.CMaProgram;
import cma.CMaProgramWriter;
import eksam3.ast.*;

public class SimpCompiler {

    public static CMaProgram compile(SimpProg prog) {
        CMaProgramWriter pw = new CMaProgramWriter();

        return pw.toProgram();
    }

}
