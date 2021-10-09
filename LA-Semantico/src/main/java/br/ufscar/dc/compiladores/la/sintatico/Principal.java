package br.ufscar.dc.compiladores.la.sintatico;

import br.ufscar.dc.compiladores.la.sintatico.LaSintaticoParser.ProgramaContext;
import java.io.FileWriter;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class Principal {

    public static void main(String args[]) throws IOException, ParseCancellationException {
        // Definindo o CharStream para ler do arquivo de entrada e tambem
        // o LaSintaticoLexer criado para fazer a analise lexica e sintática desse arquivo
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
            
            ProgramaContext arvore = parser.programa(); // Gera a arvore sintatica.
            LaSemantico sem = new LaSemantico();
            sem.visitPrograma(arvore); // Comeca visitando pelo no mais acima
            
            // Printa todos os erros semanticos encontrados.
            LaSemanticoUtils utils = new LaSemanticoUtils();
            if(!utils.errosSemanticos.isEmpty()){ // Caso ocorra erro semantico.
                for(var s: utils.errosSemanticos){
                    myWriter.write(s);
                }
                myWriter.write("Fim da compilacao\n");
            }
            else{ // Caso nenhum erro ocorra, gerar codigo intermediario em C.
                LaGerador gerador = new LaGerador();
                gerador.visitPrograma(arvore);
                StringBuilder saida = gerador.getSaida();
                myWriter.write(saida.toString());
            }

        } catch (ParseCancellationException e) { // Caso ocorra erro lexico ou sintatico.
            myWriter.write(e.getMessage());
            myWriter.write("Fim da compilacao\n");
        }
        
        myWriter.close();
    }

}
