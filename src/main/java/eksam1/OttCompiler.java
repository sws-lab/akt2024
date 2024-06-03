package eksam1;

import cma.CMaProgram;
import cma.CMaProgramWriter;
import eksam1.ast.*;

import java.util.List;

public class OttCompiler {

    /**
     * Oraakli indeksi muutuja nimi.
     */
    public static final String ORACLE_NAME = "oracle";

    public static CMaProgram compile(OttNode node, List<String> variables) {
        CMaProgramWriter pw = new CMaProgramWriter();

        return pw.toProgram();
    }
}
