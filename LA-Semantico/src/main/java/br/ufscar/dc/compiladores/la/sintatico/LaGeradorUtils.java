package br.ufscar.dc.compiladores.la.sintatico;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

// Classe implementada para disponibilizador métodos utilizados para auxiliar na análise semântica,
// controlando erros identificado, percorrendo árvore sintática das expressões, verificando se
// os identificadores usados já foram declarados e retornando o tipo da expressã́o quando pertinente.
public class LaGeradorUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    
    // Todos os erros são adicionados assim, para printar eles na tela, necessário
    // instanciar um LaSemanticoUtils e acessar todos os errosSemanticos.
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }
    
    // Esse método checa os tipos de uma atribuição se são válidos ou não. tipo1 <- tipo2.
    //Retornando true caso sejam válidos.
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
    
    // Esse método retorna o tipo do identificador dado seu tipo na tabela de símbolos, caso não tenha sido declarado um erro é apontado.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.IdentificadorContext ctx){
        String identificador = ctx.getText(); // Texto completo do identificador. EX: vinho.Preco | vinho[i] | vinho
        
        // Tudo isso é feito por causa da existência de registros
        if(!identificador.contains("[") && !identificador.contains("]")){
            // Caso não tenha dimensão.
            String[] nomePartes = identificador.split("\\.");

            // Checa se o identificador principal já foi declarado.
            if(!tabela.existe(nomePartes[0])){
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
            }
            else{ 
                // ENTÃO vinho existe.
                // Caso seja um registro tipo: "vinho.Preco", necessário checar se o .Preco é um campo do registro do tipo vinho.
                EntradaTabelaDeSimbolos possivelRegistro = tabela.verificar(nomePartes[0]);
                if(possivelRegistro.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                    TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                    if(!camposRegistro.existe(nomePartes[1])){ 
                        // Caso não exista!
                        adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                    }
                    else{ 
                        // Então vinho.preco existe.
                        EntradaTabelaDeSimbolos entradaRegistro = camposRegistro.verificar(nomePartes[1]); // Acesso o tipo de vinho.preco
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.INTEIRO){
                            saida.append(identificador);
                            return TabelaDeSimbolos.TipoLaVariavel.INTEIRO; 
                        }
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LITERAL){
                            saida.append(identificador);
                            return TabelaDeSimbolos.TipoLaVariavel.LITERAL;
                        }
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.REAL){
                            saida.append(identificador);
                            return TabelaDeSimbolos.TipoLaVariavel.REAL;
                        }
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LOGICO){
                            saida.append(identificador);
                            return TabelaDeSimbolos.TipoLaVariavel.LOGICO;
                        }
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_INT){
                            saida.append(identificador);
                            return TabelaDeSimbolos.TipoLaVariavel.PONT_INT; 
                        }
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_LIT){
                            saida.append(identificador);
                            return TabelaDeSimbolos.TipoLaVariavel.PONT_LIT;
                        }
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_REA){
                            saida.append(identificador);
                            return TabelaDeSimbolos.TipoLaVariavel.PONT_REA;
                        }
                        if(entradaRegistro.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.PONT_LOG){
                            saida.append(identificador);
                            return TabelaDeSimbolos.TipoLaVariavel.PONT_LOG;
                        }
                    }       
                }

                // Caso seja apenas um registro.
                if(possivelRegistro.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length == 1){
                    return TabelaDeSimbolos.TipoLaVariavel.REGISTRO;
                }
                
                // Caso contrário e uma variável mesmo.
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
        else{ 
            // Caso tenha dimensão.
            
            String identificadorSemDimensao = "";
            
            for(var identCtx: ctx.IDENT()) 
                // Ignoramos a dimensão
                identificadorSemDimensao += identCtx.getText();
            
            // Verifica se o primeiro identificador já foi declarado tipo: "vinho[0]", checa se vinho já foi declarado.
            if(!tabela.existe(identificadorSemDimensao)){
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + identificadorSemDimensao + " nao declarado\n");
            }
            else{
                EntradaTabelaDeSimbolos ident = tabela.verificar(identificadorSemDimensao);
                saida.append(ctx.getText());
                if(ident.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.INTEIRO)
                    return TabelaDeSimbolos.TipoLaVariavel.INTEIRO; 
                if(ident.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LITERAL)
                    return TabelaDeSimbolos.TipoLaVariavel.LITERAL;
                if(ident.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.REAL)
                    return TabelaDeSimbolos.TipoLaVariavel.REAL;
                if(ident.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LOGICO)
                    return TabelaDeSimbolos.TipoLaVariavel.LOGICO;
                // Necessário ser do mesmo tipo.
                if(ident.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO)
                    return TabelaDeSimbolos.TipoLaVariavel.REGISTRO;
                
            }
        }
        // Caso criado para passar no teste sem apontar errado, caso esteja usando uma variável não declarada na atribuição.
        return TabelaDeSimbolos.TipoLaVariavel.NAO_DECLARADO;
    }
    
    
    // Os métodos verificarTipo tem como principal objetivo retornar o tipo da expressão, entretanto também detecta se identificadores
    // não declarados estão sendo utilizados. Diante disso, esses métodos descem na árvore sintática das expressões a fim de cumprir esses
    // dois objetivos.
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.ExpressaoContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        int contador = 0;
        for (var tl : ctx.termo_logico()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, tl);
            if(ctx.op_logico_1(contador) != null){
                saida.append(" || ");
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

    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Termo_logicoContext ctx){
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        int contador = 0;
        for(var fl : ctx.fator_logico()){
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, fl);
            if(ctx.op_logico_2(contador) != null){
                saida.append(" && ");
            }
            
            if(ret == null){
                ret = aux;
            }else if(!verificarTipo(ret,aux)){
                ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
            }
            contador++;
        }
        
        return ret;
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Fator_logicoContext ctx){
        return verificarTipo(saida, tabela, ctx.parcela_logica());
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_logicaContext ctx){
        if(ctx.exp_relacional() != null)
            return verificarTipo(saida, tabela, ctx.exp_relacional());
        else{ 
            // (falso | verdadeiro)
            return TabelaDeSimbolos.TipoLaVariavel.LOGICO;
        }
    }
    
    // VOLÁTIL 
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Exp_relacionalContext ctx){
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        int contador = 0;
        
        if(ctx.exp_aritmetica().size() == 1)
            for(var ea : ctx.exp_aritmetica()){
                TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, ea);
                if(ret == null){
                    ret = aux;
                }else if(!verificarTipo(ret,aux)){
                    ret = TabelaDeSimbolos.TipoLaVariavel.INVALIDO;
                }
            }
        else{ 
            // Caso contrário checa os se os identificadores já foram declarados e retorna o tipo lógico.
            boolean lock = false; // estava repetindo o ">";
            for(var ea: ctx.exp_aritmetica()){
                verificarTipo(saida, tabela, ea);
                if(ctx.op_relacional() != null && !lock){ // Adiciona operadores "op1".
                    if(ctx.op_relacional().getText().equals(">"))
                        saida.append(">");
                    if(ctx.op_relacional().getText().equals("="))
                        saida.append("==");
                    if(ctx.op_relacional().getText().equals("<>"))
                        saida.append("!=");
                    if(ctx.op_relacional().getText().equals("<"))
                        saida.append("<");
                    if(ctx.op_relacional().getText().equals("<="))
                        saida.append("<=");
                    if(ctx.op_relacional().getText().equals(">="))
                        saida.append(">=");
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
            if(ctx.op1(contador) != null){ 
                // Adiciona operadores "op1".
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
        int contador = 0;
        for (var fa : ctx.fator()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, fa);
            if(ctx.op2(contador) != null){ // Adiciona operadores "op2".
                if(ctx.op2(contador).getText().equals("*"))
                    saida.append("*");
                else    
                    saida.append("/");
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
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.FatorContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;
        int contador = 0;
        for (var pa : ctx.parcela()) {
            TabelaDeSimbolos.TipoLaVariavel aux = verificarTipo(saida, tabela, pa);
            if(ctx.op3(contador) != null){ // Adiciona operadores "op2".
                saida.append("%");
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
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.ParcelaContext ctx){
        
        if(ctx.parcela_unario() != null){
            return verificarTipo(saida, tabela, ctx.parcela_unario());
        }
        else{
            // Caso contrário é uma parcela não-unária.
            return verificarTipo(saida, tabela, ctx.parcela_nao_unario());
        }
    }
    
    public static TabelaDeSimbolos.TipoLaVariavel verificarTipo(StringBuilder saida, TabelaDeSimbolos tabela, LaSintaticoParser.Parcela_unarioContext ctx) {
        TabelaDeSimbolos.TipoLaVariavel ret = null;
                
        if (ctx.NUM_INT() != null){
            saida.append(ctx.NUM_INT().getText());
            return TabelaDeSimbolos.TipoLaVariavel.INTEIRO;
        }
        if(ctx.NUM_REAL() != null){
            saida.append(ctx.NUM_REAL().getText());
            return TabelaDeSimbolos.TipoLaVariavel.REAL;
        }
        if(ctx.IDENT() != null){ 
            // Caso seja uma chamada de função, é necessário checar parâmetros e setar o tipo de retorno.
            
            if(!tabela.existe(ctx.IDENT().getText())){ 
                // Caso identificador da chamada não tenha sido declarado.
                adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "identificador " + ctx.IDENT().getText() + " nao declarado\n");
            }
            
            
            // O tipo de retorno da chamada é o tipo de retorno da função.
            if(tabela.existe(ctx.IDENT().getText())){
                EntradaTabelaDeSimbolos funcao = tabela.verificar(ctx.IDENT().getText());
                switch(funcao.tipoEspecial){ 
                    // Checa o tipo de retorno da função.
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
                    default: 
                        // Se chegar aqui é um tipo não básico!
                        ret = TabelaDeSimbolos.TipoLaVariavel.REGISTRO;
                        break;
                }
                
                // Também necessário checar o número de parâmetros e seus tipos.
                String nomeFun = ctx.IDENT().getText();
                EntradaTabelaDeSimbolos funProc = tabela.verificar(nomeFun);
                
                saida.append(nomeFun + "(");
                
                boolean primeiro = true;
                for(var exp: ctx.expressao()){
                    verificarTipo(saida, tabela, exp);
                    // Adiciona todos os tipos dos parâmetros no ArrayList.
                    if(!primeiro)
                        saida.append(",");
                    else
                        primeiro = false;
                }
                
                saida.append(")");
                
                
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
        else{ 
            // Caso contrário checa se os identificadores já foram declarados e retorna o tipo
            ret = verificarTipo(saida, tabela, ctx.identificador());
            if(ctx.getText().contains("&")) 
                // Caso contenha o & é um endereço.
                return TabelaDeSimbolos.TipoLaVariavel.ENDERECO;
        }
        
        return ret;
    }    
}