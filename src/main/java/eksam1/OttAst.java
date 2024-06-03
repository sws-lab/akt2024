package eksam1;

import eksam1.ast.OttNode;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.ExceptionErrorListener;

import static eksam1.ast.OttNode.*;

public class OttAst {

    public static OttNode makeOttAst(String input) {
        OttLexer lexer = new OttLexer(CharStreams.fromString(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ExceptionErrorListener());

        OttParser parser = new OttParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());

        ParseTree tree = parser.init();
        return parseTreeToAst(tree);
    }

    private static OttNode parseTreeToAst(ParseTree parseTree) {
        throw new UnsupportedOperationException();
    }
}
