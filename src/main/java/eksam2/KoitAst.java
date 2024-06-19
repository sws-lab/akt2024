package eksam2;

import eksam2.ast.KoitStmt;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.ExceptionErrorListener;

import static eksam2.ast.KoitNode.*;

public class KoitAst {

    public static KoitStmt makeKoitAst(String input) {
        KoitLexer lexer = new KoitLexer(CharStreams.fromString(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ExceptionErrorListener());

        KoitParser parser = new KoitParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());

        ParseTree tree = parser.init();
        return parseTreeToAst(tree);
    }

    private static KoitStmt parseTreeToAst(ParseTree parseTree) {
        throw new UnsupportedOperationException();
    }
}
