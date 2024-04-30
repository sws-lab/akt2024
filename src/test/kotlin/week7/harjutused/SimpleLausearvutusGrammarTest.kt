package week7.harjutused

import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.fail
import org.junit.Test
import utils.ExceptionErrorListener

class SimpleLausearvutusGrammarTest {
    @Test
    fun `lihtsad legaalsed sisendid`() {
        legal("X")
        legal("Y")
        legal("Z")

        // TODO: võimalik, et ANTLR ei saa nende operaatorite sümbolitega hakkama
        legal("¬X")

        legal("(X&Y)")
        legal("(X∨Y)")
        legal("(X→Y)")
        legal("(X↔Y)")
    }

    @Test
    fun `suuremad legaalsed sisendid`() {
        // TODO: võimalik, et ANTLR ei saa nende operaatorite sümbolitega hakkama
        legal("(¬(X&Y)→(X∨Z))")
        legal("(¬(X&(Y↔Y))→(X∨Z))")
    }

    @Test
    fun `illegaalsed sisendid`() {
        illegal("X&Y")
        illegal("X∨Y")
        illegal("X→Y")
        illegal("X↔Y")

        illegal("XvY")
        illegal("X->Y")
        illegal("X<->Y")
    }

    private fun legal(input: String) {
        parse(input)
    }

    private fun illegal(input: String) {
        runCatching { parse(input) }
            .onSuccess { fail("expected parse error: $input") }
    }

    private fun parse(input: String) {
        val lexer = SimpleLausearvutusLexer(CharStreams.fromString(input)).apply {
                removeErrorListeners()
                addErrorListener(ExceptionErrorListener())
            }

        val parser = SimpleLausearvutusParser(CommonTokenStream(lexer)).apply {
            removeErrorListeners()
            errorHandler = BailErrorStrategy()
        }

        parser.init()
    }
}
