package br.ufscar.dc.compiladores.la.sintatico;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

// Classe implementada para disponibilizados métodos utilizados para auxiliar na analise semantica,
// controlando erros identificado, percorrendo a arvore sintatica das expressoes, verificando se
// os identificadores usados ja foram declarados e futuramente checando os tipos.
public class LaSemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    
    // Todos os erros sao adicionados assim, para printar eles na tela, necessario
    // instanciar um LaSemanticoUtils e acessar todos os errosSemanticos.
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }
    
    // Os métodos verificarTipo tem como principal objetivo detectar erros de tipos, entretanto também detecta se identificadores
    // nao declarados estao sendo utilizados. Diante disso, esses metodos descem na arvore sintatica das expressoes a fim de detectar
    // esses dois tipos de erros.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.ExpressaoContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        for (var tl : ctx.termo_logico()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(tabela, tl); 
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLaVariavel.INVALIDO) {
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
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.ParcelaContext ctx){
        
        if(ctx.parcela_unario() != null){
            return verificarTipo(tabela, ctx.parcela_unario());
        }
        else{ // Caso contrario é uma parcela nao unaria.
            return verificarTipo(tabela, ctx.parcela_nao_unario());
        }
    }
    
    // Aqui tenho que fazer para parcelaUnaria e parcelaUnaria.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_unarioContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;

        if (ctx.NUM_INT() != null){
            return TabelaDeSimbolos.TipoLaVariavel.INTEIRO;
        }
        if(ctx.NUM_REAL() != null){
            return TabelaDeSimbolos.TipoLaVariavel.REAL;
        }
        if(ctx.IDENT() != null){ 
            if(!tabela.existe(ctx.IDENT().getText())){
                adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + ctx.IDENT().getText() + " nao declarado\n");
            }
            for(var exp: ctx.expressao()){
                verificarTipo(tabela, exp);
            }
        }
        if(ctx.identificador() != null){ 
            if(!ctx.identificador().getText().contains("[") && ctx.identificador().getText().contains("]")){ // Caso tenha dimensao.
                String identificador = ctx.identificador().getText();
                String[] nomePartes = identificador.split("\\.");

                if(!tabela.existe(nomePartes[0])){
                    adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                }
                else{
                    EntradaTabelaDeSimbolos possivelRegistro = tabela.verificar(nomePartes[0]);
                    if(possivelRegistro.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                        TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                        if(!camposRegistro.existe(nomePartes[1])){
                            adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                        }
                    }
                }
            }
            else{
                // Verifica se as variaveis usadas dentro da dimensao foram ja declaradas.
                for(var xp: ctx.identificador().dimensao().exp_aritmetica())
                    verificarTipo(tabela, xp);
                if(!tabela.existe(ctx.identificador().IDENT(0).getText())){
                    adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + ctx.identificador().IDENT(0).getText() + " nao declarado\n");
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
    
    // Aqui tenho que fazer para parcelaNaoUnaria.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_nao_unarioContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        if(ctx.CADEIA() != null){
            ret = TabelaDeSimbolos.TipoLaVariavel.LITERAL;
        }
        else{
            /* Tudo isso e feito por causa da existencia de registros ... */
            if(!ctx.identificador().getText().contains("[") && ctx.identificador().getText().contains("]")){ // Caso tenha dimensao.
                String identificador = ctx.identificador().getText();
                String[] nomePartes = identificador.split("\\.");

                // Checa se o identificador principal foi declarado ja.
                if(!tabela.existe(nomePartes[0])){
                    adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                }
                else{
                    // Caso seja um registro tipo: "vinho.Preco", necessario checar se o .Preco é um campo do registro do tipo vinho.
                    EntradaTabelaDeSimbolos possivelRegistro = tabela.verificar(nomePartes[0]);
                    if(possivelRegistro.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                        TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                        if(!camposRegistro.existe(nomePartes[1])){
                            adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                        }
                    }
                }
            }
            else{ // Caso tenha dimensao
                // Verifica se as expressoes usadas dentro da dimensao usam variaveis ja declaradas.
                for(var xp: ctx.identificador().dimensao().exp_aritmetica())
                    verificarTipo(tabela, xp);
                // Verifica se o primeiro identificador ja foi declarado tipo: "vinho[0]", checa se vinho ja foi declarado.
                if(!tabela.existe(ctx.identificador().IDENT(0).getText())){
                    adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + ctx.identificador().IDENT(0).getText() + " nao declarado\n");
                }
            }
        }

        return ret;
    }    
}