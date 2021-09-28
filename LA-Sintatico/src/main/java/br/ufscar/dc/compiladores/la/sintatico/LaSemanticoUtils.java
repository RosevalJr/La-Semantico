package br.ufscar.dc.compiladores.la.sintatico;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

public class LaSemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    
    
    // Todos os erros sao adicionados assim, para printar eles na tela, necessario
    // instanciar um LaSemanticoUtils e acessar todos os errosSemanticos.
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }
    
    
//    // Vai descendo na hierarquia da expressao a fim de realizar a checagem de tipos atraves da tabela de simbolso, caso tenha um erro
//    // em alguma das partes esse erro e apontado por meio do ``errosSemanticos``.
//    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Exp_aritmeticaContext ctx) {
//        TabelaDeSimbolos.TipoLA ret = null;
//        for (var ta : ctx.termo()) {
//            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabela, ta); // Ambiguidade?
//            if (ret == null) {
//                ret = aux;
//            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLA.INVALIDO) {
//                adicionarErroSemantico(ctx.start, "Expressão " + ctx.getText() + " contém tipos incompatíveis");
//                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//            }
//        }
//
//        return ret;
//    }
//
//    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.TermoContext ctx) {
//        TabelaDeSimbolos.TipoLA ret = null;
//
//        for (var fa : ctx.fator()) {
//            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabela, fa);
//            if (ret == null) {
//                ret = aux;
//            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLA.INVALIDO) {
//                adicionarErroSemantico(ctx.start, "Termo " + ctx.getText() + " contém tipos incompatíveis");
//                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//            }
//        }
//        return ret;
//    }
//    
//    // Temporario para nao dar erros.
//    /*public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.FatorContext ctx) {
//        TabelaDeSimbolos.TipoLA ret = null;
//        return ret;
//    }*/
//    
//    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.FatorContext ctx) {
//        TabelaDeSimbolos.TipoLA ret = null;
//
//        for (var pa : ctx.parcela()) {
//            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabela, pa);
//            if (ret == null) {
//                ret = aux;
//            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLA.INVALIDO) {
//                adicionarErroSemantico(ctx.start, "Parcela " + ctx.getText() + " contém tipos incompatíveis");
//                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//            }
//        }
//        return ret;
//    }
//    
//    // Talvez errada.
//    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.ParcelaContext ctx){
//        TabelaDeSimbolos.TipoLA ret = null;
//        
//        var paUn = ctx.parcela_unario();
//            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabela, paUn);
//            if(ret == null){
//                ret = aux;
//            } else if(ret != aux && aux != TabelaDeSimbolos.TipoLA.INVALIDO){
//                adicionarErroSemantico(ctx.start, "Parcela Unaria " + ctx.getText() + " contém tipos incompatíveis");
//                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//            }
//        
//        var paNUn = ctx.parcela_nao_unario();
//            TabelaDeSimbolos.TipoLA aux1 = verificarTipo(tabela, paNUn);
//            if(ret == null){
//                ret = aux1;
//            } else if(ret != aux1 && aux1 != TabelaDeSimbolos.TipoLA.INVALIDO){
//                adicionarErroSemantico(ctx.start, "Parcela Não Unaria " + ctx.getText() + " contém tipos incompatíveis");
//                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//            }
//        
//        return ret;
//    }
//    
//    // Aqui tenho que fazer para parcelaUnaria e parcelaNaoUnaria.
//    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_unarioContext ctx) {
//        if (ctx.NUM_INT() != null){
//            return TabelaDeSimbolos.TipoLA.INTEIRO;
//        }
//        if(ctx.NUM_REAL() != null){
//            return TabelaDeSimbolos.TipoLA.REAL;
//        }
//        if(ctx.IDENT() != null){
//            String nomeVar = ctx.IDENT().getText();
//            if(!tabela.existe(nomeVar)){
//                adicionarErroSemantico(ctx.IDENT().getSymbol(), "Variável " + nomeVar + " não foi declarada antes do uso");
//                return TabelaDeSimbolos.TipoLA.INVALIDO;
//            }
//            return verificarTipo(tabela, nomeVar);
//        }
//        
//        return verificarTipo(tabela, ctx.expressao()); // Precisa verificar tipo da expressao 
//    }
//    
//    // Aqui tenho que fazer para parcelaUnaria e parcelaNaoUnaria.
//    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_nao_unarioContext ctx) {
//        TabelaDeSimbolos.TipoLA ret = null;
//
//        for (var pa : ctx.parcela()) {
//            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabela, pa);
//            if (ret == null) {
//                ret = aux;
//            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLA.INVALIDO) {
//                adicionarErroSemantico(ctx.start, "Parcela " + ctx.getText() + " contém tipos incompatíveis");
//                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//            }
//        }
//        return ret;
//    }
//    
//    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.ExpressaoContext ctx) {
//        TabelaDeSimbolos.TipoLA ret = null;
//
//        for (var te : ctx.termo_logico()) {
//            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabela, te);
//            if (ret == null) {
//                ret = aux;
//            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLA.INVALIDO) {
//                adicionarErroSemantico(ctx.start, "Termo Lógico " + ctx.getText() + " contém tipos incompatíveis");
//                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//            }
//        }
//        return ret;
//    }
//
//    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.FatorContext ctx) {
//        if (ctx.INT() != null) {
//            return TabelaDeSimbolos.TipoAlguma.INTEIRO;
//        }
//        if (ctx.NUMREAL() != null) {
//            return TabelaDeSimbolos.TipoAlguma.REAL;
//        }
//        if (ctx.VARIAVEL() != null) {
//            String nomeVar = ctx.VARIAVEL().getText();
//            if (!tabela.existe(nomeVar)) {
//                adicionarErroSemantico(ctx.VARIAVEL().getSymbol(), "Variável " + nomeVar + " não foi declarada antes do uso");
//                return TabelaDeSimbolos.TipoAlguma.INVALIDO;
//            }
//            return verificarTipo(tabela, nomeVar);
//        }
//        // se não for nenhum dos tipos acima, só pode ser uma expressão
//        // entre parêntesis
//        return verificarTipo(tabela, ctx.expressaoAritmetica());
//    }
//    
//    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, String nomeVar) {
//        return tabela.verificar(nomeVar);
//    }
    
    
}