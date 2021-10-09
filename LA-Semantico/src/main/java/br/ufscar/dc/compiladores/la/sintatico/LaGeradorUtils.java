package br.ufscar.dc.compiladores.la.sintatico;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

// Classe implementada para disponibilizador métodos utilizados para auxiliar na analise semantica,
// controlando erros identificado, percorrendo a arvore sintatica das expressoes, verificando se
// os identificadores usados ja foram declarados e retornando o tipo da expressao quando pertinente.
public class LaGeradorUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    
    // Todos os erros sao adicionados assim, para printar eles na tela, necessario
    // instanciar um LaSemanticoUtils e acessar todos os errosSemanticos.
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }
    
    // Esse metodo checa os tipos de uma atribuicao se sao validos ou nao. tipo1 <- tipo2. Retornando true caso sejam validos.
    public static boolean verificarTipo(TabelaDeSimbolos.TipoLaVariavel tipo1, TabelaDeSimbolos.TipoLaVariavel tipo2){
        if(tipo1 == tipo2)
            return true;
        if(tipo1 == TabelaDeSimbolos.TipoLaVariavel.NAO_DECLARADO || tipo2 == TabelaDeSimbolos.TipoLaVariavel.NAO_DECLARADO)
            return true;
        if(tipo1 == TabelaDeSimbolos.TipoLaVariavel.INVALIDO || tipo2 == TabelaDeSimbolos.TipoLaVariavel.INVALIDO)
            return false;
        if((tipo1 == TabelaDeSimbolos.TipoLaVariavel.INTEIRO || tipo1 == TabelaDeSimbolos.TipoLaVariavel.REAL) && (tipo2 == TabelaDeSimbolos.TipoLaVariavel.INTEIRO || tipo2 == TabelaDeSimbolos.TipoLaVariavel.REAL))
            return true;
        if((tipo1 == TabelaDeSimbolos.TipoLaVariavel.PONT_INT || tipo1 == TabelaDeSimbolos.TipoLaVariavel.PONT_REA || tipo1 == TabelaDeSimbolos.TipoLaVariavel.PONT_LOG || tipo1 == TabelaDeSimbolos.TipoLaVariavel.PONT_LIT ) && (tipo2 == TabelaDeSimbolos.TipoLaVariavel.ENDERECO))
            return true;
        if(tipo1 != tipo2)
            return false;
        
        return true;
    }
    
    // Esse metodo retorna o tipo do identificador dado seu tipo na tabela de simbolos, caso nao tenha sido declarado um erro é aponado.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.IdentificadorContext ctx){
        String identificador = ctx.getText(); // Texto completo do identificador. EX: vinho.Preco | vinho[i] | vinho
        
        /* Tudo isso e feito por causa da existencia de registros ... */
        if(!identificador.contains("[") && !identificador.contains("]")){ // Caso nao tenha dimensao.
            String[] nomePartes = identificador.split("\\.");

            // Checa se o identificador principal foi declarado ja.
            if(!tabela.existe(nomePartes[0])){
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
            }
            else{ // ENTAO vinho existe.
                // Caso seja um registro tipo: "vinho.Preco", necessario checar se o .Preco é um campo do registro do tipo vinho.
                EntradaTabelaDeSimbolos possivelRegistro = tabela.verificar(nomePartes[0]);
                if(possivelRegistro.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                    TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                    if(!camposRegistro.existe(nomePartes[1])){ // Caso nao exista!
                        adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                    }
                    else{ // Entao vinho.preco existe.
                        EntradaTabelaDeSimbolos entradaRegistro = camposRegistro.verificar(nomePartes[1]); // Acesso o tipo de vinho.preco
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.INTEIRO)
                            return TabelaDeSimbolos.TipoLaVariavel.INTEIRO; 
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LITERAL)
                            return TabelaDeSimbolos.TipoLaVariavel.LITERAL;
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.REAL)
                            return TabelaDeSimbolos.TipoLaVariavel.REAL;
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LOGICO)
                            return TabelaDeSimbolos.TipoLaVariavel.LOGICO;
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_INT)
                            return TabelaDeSimbolos.TipoLaVariavel.PONT_INT; 
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_LIT)
                            return TabelaDeSimbolos.TipoLaVariavel.PONT_LIT;
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_REA)
                            return TabelaDeSimbolos.TipoLaVariavel.PONT_REA;
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_LOG)
                            return TabelaDeSimbolos.TipoLaVariavel.PONT_LOG;
                    }       
                }

                // Caso seja apenas um registro.
                if(possivelRegistro.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length == 1){
                    return TabelaDeSimbolos.TipoLaVariavel.REGISTRO;
                }
                
                // Caso contrario e uma variavel mesmo.
                if(possivelRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.INTEIRO){
                    saida.append(possivelRegistro.nome);
                    return TabelaDeSimbolos.TipoLaVariavel.INTEIRO; 
                }
                if(possivelRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LITERAL){
                    saida.append(possivelRegistro.nome);
                    return TabelaDeSimbolos.TipoLaVariavel.LITERAL;
                }
                if(possivelRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.REAL){
                    saida.append(possivelRegistro.nome);
                    return TabelaDeSimbolos.TipoLaVariavel.REAL;
                }
                if(possivelRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LOGICO)
                    return TabelaDeSimbolos.TipoLaVariavel.LOGICO;
                if(possivelRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_INT)
                    return TabelaDeSimbolos.TipoLaVariavel.PONT_INT; 
                if(possivelRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_LIT)
                    return TabelaDeSimbolos.TipoLaVariavel.PONT_LIT;
                if(possivelRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_REA)
                    return TabelaDeSimbolos.TipoLaVariavel.PONT_REA;
                if(possivelRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_LOG)
                    return TabelaDeSimbolos.TipoLaVariavel.PONT_LOG;
            }
        }
        else{ // Caso tenha dimensao.
            
            String identificadorSemDimensao = "";
            
            for(var identCtx: ctx.IDENT()) // Ignoramos a dimensao
                identificadorSemDimensao += identCtx.getText();
            
            // Verifica se as expressoes usadas dentro da dimensao usam variaveis ja declaradas.
            for(var xp: ctx.dimensao().exp_aritmetica()) 
                verificarTipo(saida, tabela, xp); // So pode ser inteiro, mas nao precisa checar!
            
            // Verifica se o primeiro identificador ja foi declarado tipo: "vinho[0]", checa se vinho ja foi declarado.
            if(!tabela.existe(identificadorSemDimensao)){
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + identificadorSemDimensao + " nao declarado\n");
            }
            else{
                EntradaTabelaDeSimbolos ident = tabela.verificar(identificadorSemDimensao);
                if(ident.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.INTEIRO)
                    return TabelaDeSimbolos.TipoLaVariavel.INTEIRO; 
                if(ident.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LITERAL)
                    return TabelaDeSimbolos.TipoLaVariavel.LITERAL;
                if(ident.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.REAL)
                    return TabelaDeSimbolos.TipoLaVariavel.REAL;
                if(ident.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LOGICO)
                    return TabelaDeSimbolos.TipoLaVariavel.LOGICO;
                
                if(ident.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO) // Tem que ser do mesmo tipo.
                    return TabelaDeSimbolos.TipoLaVariavel.REGISTRO;
                
            }
        }
        
        return TabelaDeSimbolos.TipoLaVariavel.NAO_DECLARADO; // Caso criado para passar no teste sem apontar errado, caso esteja usando uma variavel nao declarada na atribuição.
    }
    
    
    // Os métodos verificarTipo tem como principal objetivo retornar o tipo da expressao, entretanto também detecta se identificadores
    // nao declarados estao sendo utilizados. Diante disso, esses metodos descem na arvore sintatica das expressoes a fim de cumprir esses
    // dois objetivos.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.ExpressaoContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        for (var tl : ctx.termo_logico()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, tl); 
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret,aux)) {
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }

        return ret;
    }

    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Termo_logicoContext ctx){
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        for(var fl : ctx.fator_logico()){
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, fl);
            if(ret == null){
                ret = aux;
            }else if(!verificarTipo(ret,aux)){
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }
        
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Fator_logicoContext ctx){
        return verificarTipo(saida, tabela, ctx.parcela_logica());
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_logicaContext ctx){
        if(ctx.exp_relacional() != null)
            return verificarTipo(saida, tabela, ctx.exp_relacional());
        else{ // (falso | verdadeiro)
            return TabelaDeSimbolos.TipoLaVariavel.LOGICO;
        }
    }
    
    // VOLATIL !
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Exp_relacionalContext ctx){
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        int contador = 0;
        
        if(ctx.exp_aritmetica().size() == 1)
            for(var ea : ctx.exp_aritmetica()){ // AQUI
                TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, ea);
                if(ret == null){
                    ret = aux;
                }else if(!verificarTipo(ret,aux)){
                    ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
                }
            }
        else{ // Caso contrario checa os se os identificadores ja foram declarados e retorna o tipo logico.
            boolean lock = false; // estava repetindo o ">";
            for(var ea: ctx.exp_aritmetica()){
                verificarTipo(saida, tabela, ea);
                if(ctx.op_relacional() != null && !lock){ // Adiciona operadores "op1".
                    if(ctx.op_relacional().getText().equals(">"))
                        saida.append(">");
                    lock = true;
                }
            }
            return TabelaDeSimbolos.TipoLaVariavel.LOGICO;
        }
        
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Exp_aritmeticaContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;

        int contador = 0;
        for (var te : ctx.termo()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, te);
            if(ctx.op1(contador) != null){ // Adiciona operadores "op1".
                if(ctx.op1(contador).getText().equals("+"))
                    saida.append("+");
                else    
                    saida.append("-");
            }
                
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret,aux)) {
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
            contador++;
        }
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.TermoContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;

        for (var fa : ctx.fator()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, fa);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret,aux)) {
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.FatorContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;

        for (var pa : ctx.parcela()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, pa);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret,aux)) {
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
        }
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.ParcelaContext ctx){
        
        if(ctx.parcela_unario() != null){
            return verificarTipo(saida, tabela, ctx.parcela_unario());
        }
        else{ // Caso contrario é uma parcela nao unaria.
            return verificarTipo(saida, tabela, ctx.parcela_nao_unario());
        }
    }
    
    // Aqui tenho que fazer para parcelaUnaria e parcelaUnaria.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_unarioContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;;
                
        if (ctx.NUM_INT() != null){
            saida.append(ctx.NUM_INT().getText());
            return TabelaDeSimbolos.TipoLaVariavel.INTEIRO;
        }
        if(ctx.NUM_REAL() != null){
            saida.append(ctx.NUM_REAL().getText());
            return TabelaDeSimbolos.TipoLaVariavel.REAL;
        }
        if(ctx.IDENT() != null){ // Caso seja uma chamada de funcao, necessario checar parametros e setar o tipo de retorno.
            
            if(!tabela.existe(ctx.IDENT().getText())){ // Caso identificador da chamada nao tenha sido declarado.
                adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + ctx.IDENT().getText() + " nao declarado\n");
            }
            
            // Checa se todos os identificadores foram declarados.
            for(var exp: ctx.expressao()){
                TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, exp);
                if (ret == null) {
                    ret = aux;
                } else if (!verificarTipo(ret,aux)) {
                    ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
                }
            }
            
            // O tipo de retorno da chamada é o tipo de retorno da funcao.
            if(tabela.existe(ctx.IDENT().getText())){
                EntradaTabelaDeSimbolos funcao = tabela.verificar(ctx.IDENT().getText());
                switch(funcao.tipoEspecial){ // Checa o tipo de retorno da funcao.
                    case "inteiro":
                        ret = TabelaDeSimbolos.TipoLaVariavel.INTEIRO;
                        break;
                    case "literal":
                        ret = TabelaDeSimbolos.TipoLaVariavel.LITERAL;
                        break;
                    case "real":
                        ret = TabelaDeSimbolos.TipoLaVariavel.REAL;
                        break;
                    case "logico":
                        ret = TabelaDeSimbolos.TipoLaVariavel.LOGICO;
                        break;
                    case "^logico":
                        ret = TabelaDeSimbolos.TipoLaVariavel.PONT_LOG;
                        break;
                    case "^real":
                        ret = TabelaDeSimbolos.TipoLaVariavel.PONT_REA;
                        break;
                    case "^literal":
                        ret = TabelaDeSimbolos.TipoLaVariavel.PONT_LIT;
                        break;
                    case "^inteiro":
                        ret = TabelaDeSimbolos.TipoLaVariavel.PONT_INT;
                        break;                       
                    default: // Se chegar aqui e um tipo nao basico!
                        ret = TabelaDeSimbolos.TipoLaVariavel.REGISTRO;
                        break;
                }
                
                /* Tambem necessario checar o numero de parametros e seus tipos. */
                String nomeFun = ctx.IDENT().getText();
                EntradaTabelaDeSimbolos funProc = tabela.verificar(nomeFun);
                
                // Estrutura para armazenar os tipos de todos os parametros da chamada.
                ArrayList<TabelaDeSimbolos.TipoLaVariavel> tiposParametros = new ArrayList<>();
                
                for(var exp: ctx.expressao()){
                    tiposParametros.add(verificarTipo(saida, tabela, exp)); // Adiciona todos os tipos dos parametros no ArrayList.
                }
                
                // Caso o numero de parametros esteja diferente, ou os tipos nao batam, deve ser apontado um erro!
                if(!funProc.argsRegFunc.tipoValido(tiposParametros))
                    adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nomeFun + "\n");
                
            }
            
        }
        
        if(ctx.identificador() != null){ 
            return verificarTipo(saida, tabela, ctx.identificador());
        }
        
        if(ctx.IDENT() == null && ctx.expressao() != null){
            for(var exp: ctx.expressao()){
                return verificarTipo(saida, tabela,ctx.expressao(0));
            }
        }
        
        return ret;
    }
    
    // Aqui tenho que fazer para parcelaNaoUnaria.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_nao_unarioContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        
        if(ctx.CADEIA() != null){
            saida.append(ctx.CADEIA().getText());
            ret = TabelaDeSimbolos.TipoLaVariavel.LITERAL;
        }
        else{ // Caso contrario checa se os identificadores ja foram declarados e retorna o tipo
            ret = verificarTipo(saida, tabela, ctx.identificador());
            if(ctx.getText().contains("&")) // Caso contenha o & é um endereco.
                return TabelaDeSimbolos.TipoLaVariavel.ENDERECO;
        }
        
        return ret;
    }    
}