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
    
    /* Na verdade comeca na expressao relacional. */
    
    // Vai descendo na hierarquia da expressao a fim de realizar a checagem de tipos atraves da tabela de simbolso, caso tenha um erro
    // em alguma das partes esse erro e apontado por meio do ``errosSemanticos``.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.ExpressaoContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        for (var tl : ctx.termo_logico()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(tabela, tl); // Ambiguidade?
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLaVariavel.INVALIDO) {
                // adicionarErroSemantico(ctx.start, "Expressão " + ctx.getText() + " contém tipos incompatíveis");
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }

        return ret;
    }

    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Termo_logicoContext ctx){
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        for(var fl : ctx.fator_logico()){
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(tabela, fl);
            if(ret == null){
                ret = aux;
            }else if(ret != aux && aux != TabelaDeSimbolos.TipoLaVariavel.INVALIDO){
                // adicionar erro do termo_logico
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }
        
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Fator_logicoContext ctx){
        return verificarTipo(tabela, ctx.parcela_logica());
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_logicaContext ctx){
        return verificarTipo(tabela, ctx.exp_relacional());
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Exp_relacionalContext ctx){
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        for(var ea : ctx.exp_aritmetica()){
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(tabela, ea);
            if(ret == null){
                ret = aux;
            }else if(ret != aux && aux != TabelaDeSimbolos.TipoLaVariavel.INVALIDO){
                // adicionar erro do termo_logico
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }
        
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Exp_aritmeticaContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;

        for (var te : ctx.termo()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(tabela, te);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLaVariavel.INVALIDO) {
                //adicionarErroSemantico(ctx.start, "Termo " + ctx.getText() + " contém tipos incompatíveis");
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.TermoContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;

        for (var fa : ctx.fator()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(tabela, fa);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLaVariavel.INVALIDO) {
                //adicionarErroSemantico(ctx.start, "Termo " + ctx.getText() + " contém tipos incompatíveis");
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.FatorContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;

        for (var pa : ctx.parcela()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(tabela, pa);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLaVariavel.INVALIDO) {
                //adicionarErroSemantico(ctx.start, "Parcela " + ctx.getText() + " contém tipos incompatíveis");
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }
        return ret;
    }
    
    // Talvez errada.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.ParcelaContext ctx){
        
        if(ctx.parcela_unario() != null){
            return verificarTipo(tabela, ctx.parcela_unario());
        }
        else{ // Caso contrario é uma parcela nao unaria.
            return verificarTipo(tabela, ctx.parcela_nao_unario());
        }
    }
    
    
    //////// PAREI AQUI !!!!!!!!!!!!!!!!!
    // Aqui tenho que fazer para parcelaUnaria e parcelaNaoUnaria.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_unarioContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;

        if (ctx.NUM_INT() != null){
            return TabelaDeSimbolos.TipoLaVariavel.INTEIRO;
        }
        if(ctx.NUM_REAL() != null){
            return TabelaDeSimbolos.TipoLaVariavel.REAL;
        }
        if(ctx.IDENT() != null){ // OK
            if(!tabela.existe(ctx.IDENT().getText())){
                adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + ctx.IDENT().getText() + " nao declarado\n");
            }
            for(var exp: ctx.expressao()){
                verificarTipo(tabela, exp);
            }
        }
        if(ctx.identificador() != null){ // OK
            String identificador = ctx.identificador().getText();
            String[] nomePartes = identificador.split("\\.");
            
            if(!tabela.existe(nomePartes[0])){
                adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
            }
            else{
                EntradaTabelaDeSimbolos possivelRegistro = tabela.verificar(nomePartes[0]);
                if(possivelRegistro.tipoIdentificaor == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                    TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                    if(!camposRegistro.existe(nomePartes[1])){
                        adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                    }
                }
            }
        }
        
        if(ctx.IDENT() == null && ctx.expressao() != null){
            for(var exp: ctx.expressao()){
                return verificarTipo(tabela,ctx.expressao(0));
            }
        }
        
        return ret;
    }
    
    // Aqui tenho que fazer para parcelaUnaria e parcelaNaoUnaria.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_nao_unarioContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        if(ctx.CADEIA() != null){
            ret = TabelaDeSimbolos.TipoLaVariavel.LITERAL;
        }
        else{ // identificador: IDENT ("." IDENT)* dimensao
            String identificador = ctx.identificador().getText();
            String[] nomePartes = identificador.split("\\.");
            
            if(!tabela.existe(nomePartes[0])){
                adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
            }
            else{
                EntradaTabelaDeSimbolos possivelRegistro = tabela.verificar(nomePartes[0]);
                if(possivelRegistro.tipoIdentificaor == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                    TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                    if(!camposRegistro.existe(nomePartes[1])){
                        adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                    }
                }
            }
        }

        return ret;
    }    
}