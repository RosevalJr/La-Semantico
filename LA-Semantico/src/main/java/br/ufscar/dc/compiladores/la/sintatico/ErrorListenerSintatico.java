package br.ufscar.dc.compiladores.la.sintatico;


import java.util.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener; // cuidado para importar a versão 4
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token; // Vamos também precisar de Token
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.ParseCancellationException;
// Outros imports vão ser necessários aqui. O NetBeans ou IntelliJ fazem isso automaticamente

public class ErrorListenerSintatico implements ANTLRErrorListener {
    @Override
    public void	reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        // Não será necessário para o T2, pode deixar vazio
    }
    
    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
        // Não será necessário para o T2, pode deixar vazio
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
        // Não será necessário para o T2, pode deixar vazio
    }

    @Override
    public void	syntaxError(Recognizer<?,?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        // Aqui vamos colocar o tratamento de erro customizado
        
        Token t = (Token) offendingSymbol;
        
        // Erro que envolve EOF e retornado como "<EOF>", necessario retirar
        // o "<" e ">".
        if(t.getText().equals("<EOF>")){
            StringBuffer simbolo = new StringBuffer(t.getText());
        // Deleta ultima caracter
            simbolo.delete(t.getText().length() - 1, t.getText().length());
        // Deleta primeiro caracter
            simbolo.delete(0, 1);
            throw new ParseCancellationException("Linha "+line+": erro sintatico proximo a "+simbolo.toString() + "\n");
        }
        else
            throw new ParseCancellationException("Linha "+line+": erro sintatico proximo a "+t.getText() + "\n");
    }
}
