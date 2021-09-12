/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufscar.dc.compiladores.la.sintatico;

import java.io.FileWriter;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class Principal {

    public static void main(String args[]) throws IOException, ParseCancellationException {
        // Definindo o CharStream para ler do arquivo de entrada e tambem
        // o LaLexer criado para fazer a analise lexica desse arquivo
        CharStream cs = CharStreams.fromFileName(args[0]);
        LaSintaticoLexer lex = new LaSintaticoLexer(cs);

        // FileWriter para criar o arquivo de saida (conjunto de tokens) do  
        // codigo de entrada
        FileWriter myWriter = new FileWriter(args[1]);
        try {
            lex.removeErrorListeners();
            lex.addErrorListener(ErrorListenerLexer.INSTANCE);
            
            CommonTokenStream tokens = new CommonTokenStream(lex);
            LaSintaticoParser parser = new LaSintaticoParser(tokens);
            
            ErrorListenerSintatico mcel = new ErrorListenerSintatico();
            parser.addErrorListener(mcel);
            
            parser.programa();

        } catch (ParseCancellationException e) {
            myWriter.write(e.getMessage());
        }
        myWriter.write("Fim da compilacao\n");
        myWriter.close();
    }

}
