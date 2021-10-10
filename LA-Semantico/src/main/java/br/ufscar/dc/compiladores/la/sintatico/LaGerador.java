package br.ufscar.dc.compiladores.la.sintatico;

//import java.io.FileWriter;

import java.util.ArrayList;
import java.util.List;


public class LaGerador extends LaSintaticoBaseVisitor<Void>{
    StringBuilder saida;
    TabelaDeSimbolos tabela;
    
    Escopos escoposAninhados = new Escopos(); // Inicializa o escopo global.
    LaSemanticoUtils utils = new LaSemanticoUtils(); // Verificador de tipos e modulos que auxiliam na analise semantica.
    LaGeradorUtils utilsGerador = new LaGeradorUtils();
    
    public LaGerador(){
        saida = new StringBuilder();
        this.tabela = new TabelaDeSimbolos();
    }
    
    public StringBuilder getSaida(){
        return this.saida;
    }
    /* Override do visitPrograma, utilizado para checar se o cmdRetorne esta sendo usado, onde nao deve ser usado. */
    @Override
    public Void visitPrograma(LaSintaticoParser.ProgramaContext ctx) { 
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        saida.append("\n");
        
        // Aqui jaz codigo futuro ...
        ctx.declaracoes().decl_local_global().forEach(dec -> visitDecl_local_global(dec));
        
        // Para cada funcao ou reigstro que aparece executa ela e seu cmd. dentro da instanciação da funcao ou precedimento.
        
        for(var decLocGlo: ctx.declaracoes().decl_local_global()){ // Para cada funcao e procedimento.
            if(decLocGlo.declaracao_global() != null){
                visitDeclaracao_global(decLocGlo.declaracao_global());
                decLocGlo.declaracao_global().cmd().forEach(cmd -> visitCmd(cmd));
                saida.append("}\n");
            }
        }
        
        List<TabelaDeSimbolos> escopos = escoposAninhados.percorrerEscoposAninhados();
        if(escopos.size() > 1){
                escoposAninhados.abandonarEscopo(); // Tira o escopo da funcao ou precedimento anteriores.
        }
        
        saida.append("\n");
        saida.append("int main(){\n");
        ctx.corpo().declaracao_local().forEach(dec -> visitDeclaracao_local(dec));
        ctx.corpo().cmd().forEach(cmd -> visitCmd(cmd));
        saida.append("    return 0;\n");
        saida.append("}\n");
        return null;
    }
    
    @Override
    public Void visitDecl_local_global(LaSintaticoParser.Decl_local_globalContext ctx){
        //'constante' IDENT ':' tipo_basico '=' valor_constante | 'tipo' IDENT ':' tipo
        if(ctx.declaracao_local() != null){
            if(ctx.declaracao_local().IDENT() != null){
                String identificador = ctx.declaracao_local().IDENT().getText();
                TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();

                // Caso seja declaracao de constante.
                if(ctx.declaracao_local().tipo_basico() != null){ // 'constante' IDENT ':' tipo_basico '=' valor_constante 
                    // Identificada e armazena na tabela de simbolos a declaração de constante.
                    if(escopoAtual.existe(identificador))
                        utils.adicionarErroSemantico(ctx.declaracao_local().IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
                    else{
                        String tipoDoConstante = ctx.declaracao_local().tipo_basico().getText();
                        saida.append("#define " + identificador + " " + ctx.declaracao_local().valor_constante().getText());
                        switch(tipoDoConstante){
                            case "inteiro":
                                escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                break;
                            case "literal":
                                escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                break;
                            case "real":
                                escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                break;
                            case "logico":
                                escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                break;
                            default: // Nunca chega
                                break;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /* Override do visitDeclaracao_global para realizar a detecção de procedimentos e funçoes,
     * armazenado seu identificador na tabela de simbolos dos escopoGlobal. Tambem é inserido
     * o escopo da função e precedimento na pilha de escopos, porém essa pilha nunca tera um tamanho
     * maior que 2, tendo normalmente, o escopo global no fundo da pilha e o escopo da funcao ou precedimento
     * anteriormente declarada, quando uma nova funcao ou procedimento for declarada, o escopo anterior é
     * retirado da pilha. Antes de entrar no corpo do programa, é mantido apenas o escopo global.*/
    @Override 
    public Void visitDeclaracao_global(LaSintaticoParser.Declaracao_globalContext ctx){
        // Pega identificador da funcao ou procedimento.
        String identificador = ctx.IDENT().getText();
        
        // Retorna lista de escopos (tamanho nunca maior que 2), retira o escopo no topo
        // caso tenha 2 escopos na pilha.
        List<TabelaDeSimbolos> escopos = escoposAninhados.percorrerEscoposAninhados();
        if(escopos.size() > 1){
                escoposAninhados.abandonarEscopo(); // Tira o escopo da funcao ou precedimento anteriores.
        }
        
        TabelaDeSimbolos escopoGlobal = escoposAninhados.obterEscopoAtual(); // Pega o escopo global.
        
        // Caso seja uma função
        if(ctx.tipo_estendido() != null){ 
            // Cria o escopo da funcao e coloca na pilha.
            escoposAninhados.criarNovoEscopo();
            TabelaDeSimbolos escopoDaFuncao = escoposAninhados.obterEscopoAtual();
            // Coloca o apontador para o escopo global no escopo da funcao para acessar tipos ja declarados.
            escopoDaFuncao.setGlobao(escopoGlobal);
            
            if(escopoGlobal.existe(identificador)) // Identificador da funcao ja declarado no escopo global? 
                utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
            else{
                String tipoRetorno = ctx.tipo_estendido().getText();
                // Cria o novo tipo na tabela de simbolos, inserindo com uma tabela de simbolos aninhada.
                // Essa parametros sera usada para armazenar os parametros da funcao.
                TabelaDeSimbolos parametros = new TabelaDeSimbolos();
                // Adiciona identificador da funcao no escopo global.
                escopoGlobal.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.FUNCAO, null, parametros, tipoRetorno);
                
                switch(tipoRetorno){ // Sempre insere no escopo da funcao, e na tabela de simbolos que armazena os parametros da funcao.
                    case "inteiro":
                        saida.append("int " + identificador + "(");
                        break;
                    case "literal":
                        saida.append("char* " + identificador + "(");
                        break;
                    case "real":
                        saida.append("float " + identificador + "(");
                        break;
                    case "logico":
                        saida.append("boolean " + identificador + "(");
                        break;
                    case "^logico":
                        saida.append("boolean* " + identificador + "(");
                        break;
                    case "^real":
                        saida.append("float* " + identificador + "(");
                        break;
                    case "^literal":
                        saida.append("char** " + identificador + "(");
                        break;
                    case "^inteiro":
                        saida.append("int* " + identificador + "(");
                        break;                       
                    default:
                        break;
                }
                
                boolean primeiro = true;
                for(var parametro: ctx.parametros().parametro()){ // Cada parametro é um conjunto de variaveis com um tipo.
                    String tipoDaVariavel = parametro.tipo_estendido().getText(); // Tipo pode ser um tipo especial definido
                    
                    for(var ident: parametro.identificador()){ // Escopo usado aqui é a variavel "parametros".                    
                        String nomeParametro = ident.getText();
                        
                        
                        if(escopoDaFuncao.existe(nomeParametro)) // Caso o identificador da variavel ja esteja sendo usado.
                            utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),"identificador " + nomeParametro + " ja declarado anteriormente\n");
                        else{ // Caso contrario pode ser declarado.

                            switch(tipoDaVariavel){ // Sempre insere no escopo da funcao, e na tabela de simbolos que armazena os parametros da funcao.
                                case "inteiro":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                    if(primeiro){
                                        saida.append("int " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",int" + " " + nomeParametro);
                                    break;
                                case "literal":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                    if(primeiro){
                                        saida.append("char* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",char*"  + " " + nomeParametro);
                                    break;
                                case "real":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                    if(primeiro){
                                        saida.append("float " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",float " + nomeParametro);
                                    break;
                                case "logico":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                    if(primeiro){
                                        saida.append("boolean " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",boolean " + nomeParametro);
                                    break;
                                case "^logico":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                    if(primeiro){
                                        saida.append("boolean* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",boolean* " + nomeParametro);
                                    break;
                                case "^real":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                    if(primeiro){
                                        saida.append("float* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",float* " + nomeParametro);
                                    break;
                                case "^literal":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                    if(primeiro){
                                        saida.append("boolean* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",boolean* " + nomeParametro);
                                    break;
                                case "^inteiro":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
                                    if(primeiro){
                                        saida.append("int* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",int* " + nomeParametro);
                                    break;                       
                                default: // Se chegar aqui e um tipo nao basico!
                                    // Checa se o identificador tipo da variavel existe na tabela de simbolos, caso ele existe e seja um tipo este sendo declarado um registro. 
                                    if(escopoGlobal.existe(tipoDaVariavel) && escopoGlobal.verificar(tipoDaVariavel).tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.TIPO){

                                        if(escopoDaFuncao.existe(nomeParametro)) // Caso ja existe aponta erro.
                                            utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),"identificador " + nomeParametro + " ja declarado anteriormente\n");
                                        else{ // Caso contrario o registro pode ser declarado.

                                            // Acessando a tabelaDeSimbolos aninhada interna ao TIPO, a fim de criar o registro com os "campos" corretos.
                                            EntradaTabelaDeSimbolos campos = escopoGlobal.verificar(tipoDaVariavel);
                                            TabelaDeSimbolos tabelaAninhadaTipo = campos.argsRegFunc;

                                            escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, TabelaDeSimbolos.TipoLaVariavel.REGISTRO, tabelaAninhadaTipo, tipoDaVariavel);
                                            parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, TabelaDeSimbolos.TipoLaVariavel.REGISTRO, tabelaAninhadaTipo, tipoDaVariavel);
                                            if(primeiro){
                                                saida.append(tipoDaVariavel + " " + nomeParametro);
                                                primeiro = false;
                                            }
                                            else
                                                saida.append("," + tipoDaVariavel + " " + nomeParametro);
                                        }
                                    }

                                    if(!escopoGlobal.existe(tipoDaVariavel)){ // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                                        utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); 
                                        // Identificador é "declarado", porém com tipo INVALIDO.
                                        escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                        parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                    }
                                    break;
                            }
                        }
                    }
                }
                saida.append(") {\n");
            }
        }
        else{ // Caso contrario é um procedimento
            // Cria o escopo do procedimento e coloca na pilha.
            escoposAninhados.criarNovoEscopo();
            TabelaDeSimbolos escopoDaFuncao = escoposAninhados.obterEscopoAtual();
            // Coloca o apontador para o escopo global no escopo do precedimento para acessar tipos ja declarados.
            escopoDaFuncao.setGlobao(escopoGlobal);
            
            saida.append("void " + identificador + "(");
            
            boolean primeiro = true;
            if(escopoGlobal.existe(identificador)) // Identificador do procedimento ja declarado no escopo global? 
                utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
            else{
                // Cria o novo tipo na tabela de simbolos, inserindo com uma tabela de simbolos aninhada.
                // Essa parametros sera usada para armazenar os parametros do procedimento.
                TabelaDeSimbolos parametros = new TabelaDeSimbolos();
                // Adiciona identificador do procedimento no escopo global. (PROCEDIMENTO NAO TEM RETORNO!)
                escopoGlobal.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.PROCEDIMENTO, null, parametros);
                    
                for(var parametro: ctx.parametros().parametro()){ // Cada parametro é um conjunto de variaveis com um tipo.
                    String tipoDaVariavel = parametro.tipo_estendido().getText(); // Tipo pode ser um tipo especial definido
                    
                    for(var ident: parametro.identificador()){ // Escopo usado aqui é a variavel "parametros".
                                             
                        String nomeParametro = ident.getText();
                        if(escopoDaFuncao.existe(nomeParametro)) // Caso o identificador da variavel ja esteja sendo usada.
                            utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),"identificador " + nomeParametro + " ja declarado anteriormente\n");
                        else{ // Caso contrario pode ser declarado.

                            switch(tipoDaVariavel){ // Sempre insere no escopo da funcao, e na tabela de simbolos que armazena os parametros da funcao.
                                case "inteiro":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                    if(primeiro){
                                        saida.append("int " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",int" + " " + nomeParametro);
                                    break;
                                case "literal":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                    if(primeiro){
                                        saida.append("char* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",char*"  + " " + nomeParametro);
                                    break;
                                case "real":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                    if(primeiro){
                                        saida.append("float " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",float " + nomeParametro);
                                    break;
                                case "logico":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                    if(primeiro){
                                        saida.append("boolean " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",boolean " + nomeParametro);
                                    break;
                                case "^logico":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                    if(primeiro){
                                        saida.append("boolean* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",boolean* " + nomeParametro);
                                    break;
                                case "^real":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                    if(primeiro){
                                        saida.append("float* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",float* " + nomeParametro);
                                    break;
                                case "^literal":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                    if(primeiro){
                                        saida.append("boolean* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",boolean* " + nomeParametro);
                                    break;
                                case "^inteiro":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
                                    if(primeiro){
                                        saida.append("int* " + nomeParametro);
                                        primeiro = false;
                                    }
                                    else
                                        saida.append(",int* " + nomeParametro);
                                    break;                       
                                default: // Se chegar aqui e um tipo nao basico!
                                    // Checa se o identificador tipo da variavel existe na tabela de simbolos, caso ele existe e seja um tipo este sendo declarado um registro. 
                                    if(escopoGlobal.existe(tipoDaVariavel) && escopoGlobal.verificar(tipoDaVariavel).tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.TIPO){

                                        if(escopoDaFuncao.existe(nomeParametro)) // Caso ja existe aponta erro.
                                            utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),"identificador " + nomeParametro + " ja declarado anteriormente\n");
                                        else{ // Caso contrario o registro pode ser declarado.

                                            // Acessando a tabelaDeSimbolos aninhada interna ao TIPO, a fim de criar o registro com os "campos" corretos.
                                            EntradaTabelaDeSimbolos campos = escopoGlobal.verificar(tipoDaVariavel);
                                            TabelaDeSimbolos tabelaAninhadaTipo = campos.argsRegFunc;

                                            escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, TabelaDeSimbolos.TipoLaVariavel.REGISTRO, tabelaAninhadaTipo, tipoDaVariavel);
                                            parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, TabelaDeSimbolos.TipoLaVariavel.REGISTRO, tabelaAninhadaTipo, tipoDaVariavel);
                                            if(primeiro){
                                                saida.append(tipoDaVariavel + " " + nomeParametro);
                                                primeiro = false;
                                            }
                                            else
                                                saida.append("," + tipoDaVariavel + " " + nomeParametro);
                                        }
                                    }

                                    if(!escopoGlobal.existe(tipoDaVariavel)){ // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                                        utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); 
                                        // Identificador é "declarado", porém com tipo INVALIDO.
                                        escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                        parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                    }
                                    break;
                            }
                        }
                    }
                }
                saida.append(") {\n");
            }
        }
        
        return null; // visita os filhos.
    }
    
    
    @Override 
    public Void visitDeclaracao_local(LaSintaticoParser.Declaracao_localContext ctx){

        //'constante' IDENT ':' tipo_basico '=' valor_constante | 'tipo' IDENT ':' tipo
        if(ctx.IDENT() != null){
            String identificador = ctx.IDENT().getText();
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            
            // Caso seja declaracao de constante.
            if(ctx.tipo_basico() != null){ // 'constante' IDENT ':' tipo_basico '=' valor_constante 
                // Identificada e armazena na tabela de simbolos a declaração de constante.
                /*if(escopoAtual.existe(identificador))
                    utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
                else{
                    String tipoDoConstante = ctx.tipo_basico().getText();
                    switch(tipoDoConstante){
                        case "inteiro":
                            escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                            break;
                        case "literal":
                            escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                            break;
                        case "real":
                            escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TabelaDeSimbolos.TipoLaVariavel.REAL);
                            break;
                        case "logico":
                            escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                            break;
                        default: // Nunca chega
                            break;
                    }
                }*/
            } // Caso contrario e a declaracao de um tipo.
            else{ // 'tipo' IDENT ':' tipo  || Typedef.
                // Aqui é realizado a declaração de um novo tipo, usado para declarar registros posteriormente.
                if(escopoAtual.existe(identificador)) 
                    utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
                else{ // Caso o identificador ja nao esteja sendo usado!
                    
                    // Cria o novo tipo na tabela de simbolos, inserindo com uma tabela de simbolos aninhada.
                    // Essa parametros sera usada para armazenas as variaveis do tipo.
                    TabelaDeSimbolos camposTipo = new TabelaDeSimbolos();
                    escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.TIPO, null, camposTipo);
                    
                    saida.append("    typedef struct {\n");
                    
                    for(var variavel : ctx.tipo().registro().variavel()){

                        for(var ctxIdentVariavel: variavel.identificador()){ // Cada variavel tem um tipo
                            
                            String identificadorVariavel = ctxIdentVariavel.getText();
                            
                            // Nao pode repetir o nomes dos campos do registros ...
                            if(camposTipo.existe(identificadorVariavel))
                                utils.adicionarErroSemantico(ctxIdentVariavel.IDENT(0).getSymbol(),"identificador " + identificadorVariavel + " ja declarado anteriormente\n");
                            else{
                                // Pega o tipo da variavel.
                                String tipoDaVariavel = variavel.tipo().getText();
                                
                                switch(tipoDaVariavel){
                                    case "inteiro":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                        saida.append("        int " + identificadorVariavel + ";\n");
                                        break;
                                    case "literal":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                        saida.append("        char " + identificadorVariavel + "[80];\n");
                                        break;
                                    case "real":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                        saida.append("        float " + identificadorVariavel + ";\n");
                                        break;
                                    case "logico":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                        saida.append("        boolean " + identificadorVariavel + ";\n");
                                        break;
                                    case "^logico":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                        saida.append("        boolean* " + identificadorVariavel + ";\n");
                                        break;
                                    case "^real":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                        saida.append("        float* " + identificadorVariavel + ";\n");
                                        break;
                                    case "^literal":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                        saida.append("        char* " + identificadorVariavel + "[80];\n");
                                        break;
                                    case "^inteiro":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
                                        saida.append("        int* " + identificadorVariavel + ";\n");
                                        break;                       
                                    default: // Nao estou tratando se tiver usando um tipo declarado na criação de um novo tipo.
                                        break;
                                }
                            }
                        }
                    }
                    saida.append("    } " + identificador + ";\n");
                }
            }
        } // Caso seja a declaracao de uma "variavel"
        else{// 'declare' variavel 
            
            // Caso nao seja uma declaracao de registro direta !
            if(ctx.variavel().tipo().registro() == null){
                for(var ctxIdentVariavel: ctx.variavel().identificador()){ // Cada variavel tem um tipo.
                    // Nao é necessario concatenar.
                    String identificadorVariavel = "";

                    for(var ident: ctxIdentVariavel.IDENT())
                        identificadorVariavel += ident.getText();
                    
                    TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
                    
                    // Checa se tiver dimencao as expressoes deve utilizar variaveis ja declaradas! EX: valor[i]
                    if(ctxIdentVariavel.dimensao() != null)
                        for(var expDim: ctxIdentVariavel.dimensao().exp_aritmetica())
                            utils.verificarTipo(escopoAtual, expDim); // VerificarTipo retorna tipo da expressao, mas tambem checa se identificadores ja foram declarados.
                    
                     // Caso o identificador da variavel ja esteja sendo usada.
                    if(escopoAtual.existe(identificadorVariavel))
                        utils.adicionarErroSemantico(ctxIdentVariavel.IDENT(0).getSymbol(),"identificador " + identificadorVariavel + " ja declarado anteriormente\n");
                    else{ // Caso contrario pode ser declarado.
                        
                        String tipoDaVariavel = ctx.variavel().tipo().getText();
                        
                        switch(tipoDaVariavel){
                            case "inteiro":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                saida.append("    int "+ ctxIdentVariavel.getText() + ";\n");
                                break;
                            case "literal":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                saida.append("    char "+ identificadorVariavel + "[80];\n");
                                break;
                            case "real":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                saida.append("    float "+ ctxIdentVariavel.getText() + ";\n");
                                break;
                            case "logico":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                break;
                            case "^logico":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                break;
                            case "^real":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                saida.append("    float* "+ ctxIdentVariavel.getText() + ";\n");
                                break;
                            case "^literal":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                saida.append("    char* "+ identificadorVariavel + "[80];\n");
                                break;
                            case "^inteiro":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
                                saida.append("    int* "+ ctxIdentVariavel.getText() + ";\n");
                                break;                       
                            default: // Se chegar aqui e um tipo nao basico!
                                // Checa se o identificador tipo da variavel existe na tabela de simbolos, caso ele existe e seja um tipo esta sendo declarado um registro. 
                                if(escopoAtual.existe(tipoDaVariavel) && escopoAtual.verificar(tipoDaVariavel).tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.TIPO){
                                    
                                    if(escopoAtual.existe(identificadorVariavel)) // Caso a variavel ja existe aponta erro.
                                        utils.adicionarErroSemantico(ctxIdentVariavel.IDENT(0).getSymbol(),"identificador " + identificadorVariavel + " ja declarado anteriormente\n");
                                    else{ // Caso contrario o registro pode ser declarado.
                                        
                                        // Acessando a tabelaDeSimbolos aninhada interna ao TIPO, a fim de criar o registro com os "campos" corretos.
                                        EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(tipoDaVariavel);
                                        TabelaDeSimbolos camposTipo = entrada.argsRegFunc;
                                        escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, null, camposTipo, tipoDaVariavel);
                                        saida.append("    " + tipoDaVariavel + " " + ctxIdentVariavel.getText() + ";\n");
                                    }
                                }
                                
                                if(!escopoAtual.existe(tipoDaVariavel)){ // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                                    utils.adicionarErroSemantico(ctxIdentVariavel.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); 
                                    // Coloca a variavel na tabela de simbolos com tipo invalido.
                                    escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                }
                                break;
                        }
                    }
                }
            }
            else{// caso seja feita a declaracao de um registro sem declarar o tipo antes !
                // Neste caso nao sera criado um "tipo" antes para ser usado depois, todas as instancias do registro serao definidas aqui.
                saida.append("    struct {\n");
                ArrayList<String> identsRegistros = new ArrayList<>(); // Armazenar os identificadores dos registros a serem declarados.
                // Primeira é inserido na tabela de simbolos os identificadores dos registros.
                for(var ctxIdentReg: ctx.variavel().identificador()){
                    String nomeIdentificador = ctxIdentReg.getText();
                    
                    TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
                    
                    if(escopoAtual.existe(nomeIdentificador)) // Identificador nao pode repetir.
                        utils.adicionarErroSemantico(ctxIdentReg.IDENT(0).getSymbol(),"identificador " + nomeIdentificador + " ja declarado anteriormente\n");
                    else{
                        TabelaDeSimbolos campos = new TabelaDeSimbolos();
                        escopoAtual.adicionar(nomeIdentificador, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, null, campos, null); // Neste caso nao tem o identificadorEspecial.
                        identsRegistros.add(nomeIdentificador); //  Guardando todos os identificadores dos registros que foram declarados.
                    }
                }
                
                boolean lock = false;
                // Agora sera identificado cada campo dos registros e inserido na tabela de simbolos aninhada da declaracao do identificador do registro.
                for(var ctxVariavelRegistro: ctx.variavel().tipo().registro().variavel()){ // Cada "campo" dos registros tem um tipo.
                    for(var ctxVariavelRegistroIdent: ctxVariavelRegistro.identificador()){
                        lock = false;
                        String nomeCampoRegistro = ctxVariavelRegistroIdent.getText();

                        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();

                        for(String identRegistro: identsRegistros){ // Para cada um dos registros, inserir o "campo" em sua tabela de simbolos interna.
                            EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(identRegistro);
                            TabelaDeSimbolos camposRegistro = entrada.argsRegFunc;
                            
                           if(camposRegistro.existe(nomeCampoRegistro))// Nao pode repetir o identificador dentro da declaração dos registros.
                               utils.adicionarErroSemantico(ctxVariavelRegistroIdent.IDENT(0).getSymbol(),"identificador " + nomeCampoRegistro + " ja declarado anteriormente\n");
                           else{ // Checa que tipo é a variavel.
                               String tipoDaVariavel = ctxVariavelRegistro.tipo().getText();
                               
                               switch(tipoDaVariavel){
                                   case "inteiro":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                       if(!lock)
                                           saida.append("    int "+ nomeCampoRegistro + ";\n");
                                       break;
                                   case "literal":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                       if(!lock)
                                           saida.append("    char "+ nomeCampoRegistro + "[80];\n");
                                       break;
                                   case "real":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                       if(!lock)
                                           saida.append("    float "+ nomeCampoRegistro + ";\n");
                                       break;
                                   case "logico":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                       break;
                                   case "^logico":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                       break;
                                   case "^real":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                        if(!lock)
                                           saida.append("    float* "+ nomeCampoRegistro + ";\n");
                                       break;
                                   case "^literal":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                        if(!lock)
                                           saida.append("    char* "+ nomeCampoRegistro + "[80];\n");
                                       break;
                                   case "^inteiro":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
                                        if(!lock)
                                           saida.append("    int* "+ nomeCampoRegistro + ";\n");
                                       break;                       
                                   default: // Se chegar aqui e um tipo nao basico.
                                       // Nao checo se esta sendo usado um tipo criado antes "registro dentro de registro".
                                       if(!escopoAtual.existe(tipoDaVariavel)){ // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                                           utils.adicionarErroSemantico(ctxVariavelRegistroIdent.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); // Aqui esta errado !
                                           escopoAtual.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                       }
                                       break;
                               }
                           }
                        }
                        lock = true;
                    }
                }
                saida.append("    }");
                boolean primeiro = true;
                for(var identRegistro: identsRegistros){
                    if(primeiro)
                        saida.append(identRegistro);
                    else
                        saida.append("," + identRegistro);
                }
                saida.append(";\n");
            }
        }
        
        return null; // visita os filhos.
    }
    
    @Override 
    public Void visitCmdLeia(LaSintaticoParser.CmdLeiaContext ctx){
        for(var identificador: ctx.identificador()){
            String nomeIdentificador = "";
            
            for(var parteNome: identificador.IDENT())
                nomeIdentificador += parteNome.getText();
            
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            EntradaTabelaDeSimbolos variavel = escopoAtual.verificar(nomeIdentificador);
            
            if(variavel.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.INTEIRO)
                saida.append("    scanf(\"%d\", &" + nomeIdentificador + ");\n");
            if(variavel.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.REAL)
                saida.append("    scanf(\"%f\", &" + nomeIdentificador + ");\n");
            if(variavel.tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LITERAL)
                saida.append("    gets(" + nomeIdentificador + ");\n");
        }
        return null;
    }
    
    @Override 
    public Void visitCmdEscreva(LaSintaticoParser.CmdEscrevaContext ctx){
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        
        saida.append("    printf(\"");
        StringBuilder bufferConteudoExpressao = new StringBuilder();
        ArrayList<String> nomeVariaveis = new ArrayList<>();
        
        for(var expressao: ctx.expressao()){
            TabelaDeSimbolos.TipoLaVariavel tipoVariavel = utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, expressao);
            // Checa o que pegou.
            System.out.println("Tipo: "+ tipoVariavel + "\n" + "Conteudo: " + bufferConteudoExpressao.toString());
            
            if(tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LITERAL && bufferConteudoExpressao.toString().contains("\"")) // Literal puro.
                saida.append(bufferConteudoExpressao.toString().replaceAll("\"", ""));
            if(tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.INTEIRO){
                saida.append("%d");
                nomeVariaveis.add(bufferConteudoExpressao.toString());
            }
            if(tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.REAL){
                saida.append("%f");
                nomeVariaveis.add(bufferConteudoExpressao.toString());
            }
            if(tipoVariavel == TabelaDeSimbolos.TipoLaVariavel.LITERAL  && !bufferConteudoExpressao.toString().contains("\"")){
                saida.append("%s");
                nomeVariaveis.add(bufferConteudoExpressao.toString());
            }
                
            
            bufferConteudoExpressao.setLength(0); //Reseta o buffer...
        }
        saida.append("\"");
        for(var nomeVariavel: nomeVariaveis){
            saida.append("," + nomeVariavel);
        }
            
        saida.append(");\n");
        return null;
    }
    
    @Override 
    public Void visitCmdAtribuicao(LaSintaticoParser.CmdAtribuicaoContext ctx){
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        StringBuilder bufferNaoUsado = new StringBuilder();
        
        String[] atribuicao = ctx.getText().split("<-");
        String nomeIdentEsq = ctx.identificador().getText();
        if(!(utilsGerador.verificarTipo(bufferNaoUsado, escopoAtual, ctx.identificador()) == TabelaDeSimbolos.TipoLaVariavel.LITERAL)){
            if(atribuicao[0].contains("^")){
                saida.append("    *" + nomeIdentEsq + " = ");
            }
            else{
                saida.append("    " + nomeIdentEsq + " = ");
            }

            StringBuilder bufferConteudoExpressao = new StringBuilder();

            if(atribuicao[1].contains("&")){
                saida.append("&");
            }
            utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, ctx.expressao());

            saida.append(bufferConteudoExpressao + ";\n");
        }
        else{
            saida.append("    strcpy(" + nomeIdentEsq + ",");
            
            StringBuilder bufferConteudoExpressao = new StringBuilder();
            utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, ctx.expressao());

            saida.append(bufferConteudoExpressao + ");\n");
        }
        return null;
    }
    
    @Override 
    public Void visitCmdSe(LaSintaticoParser.CmdSeContext ctx){
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        StringBuilder bufferConteudoExpressao = new StringBuilder();
        
        saida.append("    if(");
        
        utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, ctx.expressao());
        saida.append(bufferConteudoExpressao);
        saida.append("){\n");
        
        ctx.cmdIf.forEach(cmd -> visitCmd(cmd));
        
        saida.append("    }\n");
        
        if(ctx.getText().contains("senao")){
            saida.append("    else{\n");
            ctx.cmdElse.forEach(cmd -> visitCmd(cmd));
            saida.append("    }\n");
        }
        
        
        return null;
    }
    
    @Override 
    public Void visitCmdCaso(LaSintaticoParser.CmdCasoContext ctx){
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        StringBuilder bufferConteudoExpressao = new StringBuilder();
        
        saida.append("    switch(");
        
        utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, ctx.exp_aritmetica());
        saida.append(bufferConteudoExpressao);
        saida.append("){\n");
        
        // Selecao ...
        for(var item: ctx.selecao().item_selecao()){
            for(var num_intervalo: item.constantes().numero_intervalo()){
                String comecoNum = "";
                String finalNum = "";
                
                /* Definindo o intervalo ... */
                if(num_intervalo.op_unarioPrimeiro != null)
                    comecoNum += num_intervalo.op_unarioPrimeiro.getText();
                comecoNum += num_intervalo.numeroPrimeiro.getText();
                
                if(num_intervalo.op_unariosSegundo != null)
                    finalNum += num_intervalo.op_unariosSegundo.getText();
                if(num_intervalo.numeroSegundo != null)
                    finalNum += num_intervalo.numeroSegundo.getText();
                
                if(!finalNum.equals(""))
                    for(int i = Integer. parseInt(comecoNum); i <= Integer. parseInt(finalNum); i++ )
                        saida.append("case " + String.valueOf(i) + ":\n");
                else
                    saida.append("case " + comecoNum + ":\n");
            }
            
            item.cmd().forEach(cmd -> visitCmd(cmd));
            saida.append("break;\n");
        }
        //ctx.cmdIf.forEach(cmd -> visitCmd(cmd));
        
        if(ctx.getText().contains("senao")){
            saida.append("    default:\n");
            ctx.cmd().forEach(cmd -> visitCmd(cmd));
            saida.append("    }\n");
        }
        
        
        return null;
    }
    
    @Override
    public Void visitCmdPara(LaSintaticoParser.CmdParaContext ctx){
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        StringBuilder bufferConteudoExpressao = new StringBuilder();     
        
        saida.append("for(");
        saida.append(ctx.IDENT().getText()); 
        saida.append(" = ");
        
        utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, ctx.exp_aritmetica(0));
        saida.append(bufferConteudoExpressao);
        saida.append("; "); 
        
        saida.append(ctx.IDENT().getText()); 
        saida.append(" <= ");
        bufferConteudoExpressao = new StringBuilder(); // limpa o buffer
        utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, ctx.exp_aritmetica(1));
        saida.append(bufferConteudoExpressao);
        saida.append("; "); 
        saida.append(ctx.IDENT().getText()); 
        saida.append("++){\n");
        
        ctx.cmd().forEach(cmd ->visitCmd(cmd));
        saida.append("}\n"); 
       
        return null;
    }
    @Override
    public Void visitCmdEnquanto(LaSintaticoParser.CmdEnquantoContext ctx){
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        StringBuilder bufferConteudoExpressao = new StringBuilder();     
        
        saida.append("while(");
        utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, ctx.expressao());
        saida.append(bufferConteudoExpressao);
        saida.append("){\n");
        
        ctx.cmd().forEach(cmd ->visitCmd(cmd));
        saida.append("}\n");
        
        return null;
    }
    
    @Override
    public Void visitCmdFaca(LaSintaticoParser.CmdFacaContext ctx){
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        StringBuilder bufferConteudoExpressao = new StringBuilder();     
        Boolean isFalse = false;
        
        saida.append("do{\n"); 
        ctx.cmd().forEach(cmd ->visitCmd(cmd));
        
        saida.append("}while(");
        
        if(ctx.expressao().termo_logico(0).getText().contains("nao")){
            isFalse = true;
            saida.append("!(");
        }
        utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, ctx.expressao());
        saida.append(bufferConteudoExpressao);
        if(isFalse){
            saida.append(")");
        }
        saida.append(");\n");
       
        return null;
    }
    
    @Override
    public Void visitCmdRetorne(LaSintaticoParser.CmdRetorneContext ctx){
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        StringBuilder bufferConteudoExpressao = new StringBuilder();
        
        utilsGerador.verificarTipo(bufferConteudoExpressao, escopoAtual, ctx.expressao());
        saida.append("    return " + bufferConteudoExpressao + ";\n");
        //saida.append("}\n");
        return null;
    }
    
    @Override
    public Void visitCmdChamada(LaSintaticoParser.CmdChamadaContext ctx){
        saida.append("    " + ctx.getText() + ";\n");
        
        return null;
    }
    
}
