package br.ufscar.dc.compiladores.la.sintatico;

import br.ufscar.dc.compiladores.la.sintatico.TabelaDeSimbolos.TipoLaVariavel;
import java.util.ArrayList;
import java.util.List;

public class LaSemantico extends LaSintaticoBaseVisitor<Void> {
    
    Escopos escoposAninhados = new Escopos(); // Inicializa o escopo global.
    LaSemanticoUtils utils = new LaSemanticoUtils(); // Verificador de tipos e modulos que auxiliam na analise semantica.

    /* Override do visitPrograma, utilizado para checar se o cmdRetorne esta sendo usado, onde nao deve ser usado. */
    @Override
    public Void visitPrograma(LaSintaticoParser.ProgramaContext ctx) { // (procedimento nao tem retorno? necessario checar funcao?)
        
        // O Corpo do programa nao deve ter RETORNE!
        for(var ctxCmd: ctx.corpo().cmd()){
            if(ctxCmd.cmdRetorne() != null){
                utils.adicionarErroSemantico(ctxCmd.cmdRetorne().getStart(),"comando retorne nao permitido nesse escopo\n");
            }
        }
        
        // Procedimentos nao devem ter RETORNE!
        for(var ctxDec : ctx.declaracoes().decl_local_global()){
            if(ctxDec.declaracao_global() != null){
                if( ctxDec.declaracao_global().tipo_estendido() == null){
                    for(var ctxCmd: ctxDec.declaracao_global().cmd()){
                        if(ctxCmd.cmdRetorne() != null)
                            utils.adicionarErroSemantico(ctxCmd.cmdRetorne().getStart(),"comando retorne nao permitido nesse escopo\n");
                    }
                }
            }
        }
        
        return super.visitPrograma(ctx);  // visita os filhos.
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
                // Cria o novo tipo na tabela de simbolos, inserindo com uma tabela de simbolos aninhada.
                // Essa parametros sera usada para armazenar os parametros da funcao.
                TabelaDeSimbolos parametros = new TabelaDeSimbolos();
                // Adiciona identificador da funcao no escopo global.
                escopoGlobal.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.FUNCAO, null, parametros, ctx.tipo_estendido().getText());
                    
                for(var parametro: ctx.parametros().parametro()){ // Cada parametro é um conjunto de variaveis com um tipo.
                    String tipoDaVariavel = parametro.tipo_estendido().getText(); // Tipo pode ser um tipo especial definido
                    
                    for(var ident: parametro.identificador()){ // Escopo usado aqui é a variavel "parametros".
                                             
                        String nomeParametro = ident.getText();
                        if(escopoDaFuncao.existe(nomeParametro)) // Caso o identificador da variavel ja esteja sendo usado.
                            utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),"identificador " + nomeParametro + " ja declarado anteriormente\n");
                        else{ // Caso contrario pode ser declarado.

                            switch(tipoDaVariavel){ // Sempre insere no escopo da funcao, e na tabela de simbolos que armazena os parametros da funcao.
                                case "inteiro":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                    break;
                                case "literal":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                    break;
                                case "real":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                    break;
                                case "logico":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                    break;
                                case "^logico":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                    break;
                                case "^real":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                    break;
                                case "^literal":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                    break;
                                case "^inteiro":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
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
            }
        }
        else{ // Caso contrario é um procedimento
            // Cria o escopo do procedimento e coloca na pilha.
            escoposAninhados.criarNovoEscopo();
            TabelaDeSimbolos escopoDaFuncao = escoposAninhados.obterEscopoAtual();
            // Coloca o apontador para o escopo global no escopo do precedimento para acessar tipos ja declarados.
            escopoDaFuncao.setGlobao(escopoGlobal);
            
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

                            switch(tipoDaVariavel){ // Sempre insere no escopo do procedimento, e na tabela de simbolos que armazena os parametros do procedimento.
                                case "inteiro":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                    break;
                                case "literal":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                    break;
                                case "real":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                    break;
                                case "logico":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                    break;
                                case "^logico":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                    break;
                                case "^real":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                    break;
                                case "^literal":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                    break;
                                case "^inteiro":
                                    escopoDaFuncao.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
                                    parametros.adicionar(nomeParametro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
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
            }
        }
        
        return super.visitDeclaracao_global(ctx); // visita os filhos.
    }
    
    // Override do visitante do corpo do programa, neste visitante apenas é retirado os escopos
    // das funções e procedimentos, mantendo apenas o escopo global na lista de escopos.
    @Override
    public Void visitCorpo(LaSintaticoParser.CorpoContext ctx){
        
        
        List<TabelaDeSimbolos> escopos = escoposAninhados.percorrerEscoposAninhados();
        if(escopos.size() > 1){
                escoposAninhados.abandonarEscopo(); // Tira o escopo da funcao ou precedimento anteriores.
        }
        
        return super.visitCorpo(ctx); // visita os filhos.
    }
    
    /* Override do visitDeclaracao_local para deteccar todo tipo de declaração e fazer 
     * os devidos armazenamentos na tabela de simbolos do escopo atual.
     * (Quando tentar declarar algo com um tipo que nao existe, esse algo sera inserido
     * na tabela de simbolos com tipo invalido)*/
    @Override 
    public Void visitDeclaracao_local(LaSintaticoParser.Declaracao_localContext ctx){

        //'constante' IDENT ':' tipo_basico '=' valor_constante | 'tipo' IDENT ':' tipo
        if(ctx.IDENT() != null){
            String identificador = ctx.IDENT().getText();
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            
            // Caso seja declaracao de constante.
            if(ctx.tipo_basico() != null){ // 'constante' IDENT ':' tipo_basico '=' valor_constante 
                // Identificada e armazena na tabela de simbolos a declaração de constante.
                if(escopoAtual.existe(identificador))
                    utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
                else{
                    String tipoDoConstante = ctx.tipo_basico().getText();
                    switch(tipoDoConstante){
                        case "inteiro":
                            escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TipoLaVariavel.INTEIRO);
                            break;
                        case "literal":
                            escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TipoLaVariavel.LITERAL);
                            break;
                        case "real":
                            escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TipoLaVariavel.REAL);
                            break;
                        case "logico":
                            escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.CONSTANTE, TipoLaVariavel.LOGICO);
                            break;
                        default: // Nunca chega
                            break;
                    }
                }
            } // Caso contrario e a declaracao de um tipo.
            else{ // 'tipo' IDENT ':' tipo 
                // Aqui é realizado a declaração de um novo tipo, usado para declarar registros posteriormente.
                if(escopoAtual.existe(identificador)) 
                    utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
                else{ // Caso o identificador ja nao esteja sendo usado!
                    
                    // Cria o novo tipo na tabela de simbolos, inserindo com uma tabela de simbolos aninhada.
                    // Essa parametros sera usada para armazenas as variaveis do tipo.
                    TabelaDeSimbolos camposTipo = new TabelaDeSimbolos();
                    escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.TIPO, null, camposTipo);
                    
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
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                        break;
                                    case "literal":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                        break;
                                    case "real":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                        break;
                                    case "logico":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                        break;
                                    case "^logico":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                        break;
                                    case "^real":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                        break;
                                    case "^literal":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                        break;
                                    case "^inteiro":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
                                        break;                       
                                    default: // Nao estou tratando se tiver usando um tipo declarado na criação de um novo tipo.
                                        break;
                                }
                            }
                        }
                    }
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
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                break;
                            case "literal":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                break;
                            case "real":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                break;
                            case "logico":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                break;
                            case "^logico":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                break;
                            case "^real":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                break;
                            case "^literal":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                break;
                            case "^inteiro":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
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
                
                // Agora sera identificado cada campo dos registros e inserido na tabela de simbolos aninhada da declaracao do identificador do registro.
                for(var ctxVariavelRegistro: ctx.variavel().tipo().registro().variavel()){ // Cada "campo" dos registros tem um tipo.
                    for(var ctxVariavelRegistroIdent: ctxVariavelRegistro.identificador()){
                        
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
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                       break;
                                   case "literal":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                       break;
                                   case "real":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                       break;
                                   case "logico":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                       break;
                                   case "^logico":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                       break;
                                   case "^real":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                       break;
                                   case "^literal":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                       break;
                                   case "^inteiro":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
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
                    }
                }
            }
        }
        
        return super.visitDeclaracao_local(ctx); // visita os filhos.
    }
    
    /* Override do visitCmd para checar se os identificador ja foram declarados quando eles sao utilizados,
     * checar a tipagem de uma atribuição e checar o numero e tipos dos parametros de uma funcao.*/
    @Override 
    public Void visitCmd(LaSintaticoParser.CmdContext ctx){
        
        /* utils.verificarTipo(tabela, expressao), sempre retornara um tipo da expressao ou identificador,
         * entretanto também serve para checar se um identificador ja foi declarado ou nao antes de seu usp. */
        
        // Checando utilizacao dos identificador dentro do cmdLeia.
        if(ctx.cmdLeia() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            for(var ident: ctx.cmdLeia().identificador()){
                utils.verificarTipo(escopoAtual, ident);
            }
        }
        
        // Checa utilização dos identificador dentro de cmdEscreva.
        if(ctx.cmdEscreva() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            for(var exp: ctx.cmdEscreva().expressao()){
                utils.verificarTipo(escopoAtual, exp);
            }
        }
        
        // Checa utilização dos identificador dentro de cmdEnquanto.
        if(ctx.cmdEnquanto() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            utils.verificarTipo(escopoAtual, ctx.cmdEnquanto().expressao());
        }
        
        // Checa utilização dos identificador dentro de cmdAtribuicao.
        if(ctx.cmdAtribuicao() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            
            TabelaDeSimbolos.TipoLaVariavel identEsq = utils.verificarTipo(escopoAtual, ctx.cmdAtribuicao().identificador());
            
            // Verificar se as variaveis da expressao da atribuicao foram declaradas.
            TabelaDeSimbolos.TipoLaVariavel identDir = utils.verificarTipo(escopoAtual, ctx.cmdAtribuicao().expressao());
            
            /* Checa a questao dos ponteiros também. */
            String[] atribuicao = ctx.cmdAtribuicao().getText().split("<-");
            
            // Caso atribuicao seja invalida (tipos invalidos) e nao seja uma atribuicao de ponteiro.
            if(!utils.verificarTipo(identEsq,identDir) && !atribuicao[0].contains("^"))
                utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(),"atribuicao nao compativel para " + ctx.cmdAtribuicao().identificador().getText() + "\n");
            
            // Caso seja uma atribuica de ponteiro a checagem de tipos é feita aqui!
            if(atribuicao[0].contains("^")) // Isso pode ser colocado dentro do verificarTipo talvez.
                if(identEsq == TabelaDeSimbolos.TipoLaVariavel.PONT_INT && identDir != TabelaDeSimbolos.TipoLaVariavel.INTEIRO)
                    utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(),"atribuicao nao compativel para " + atribuicao[0] + "\n");
                if(identEsq == TabelaDeSimbolos.TipoLaVariavel.PONT_LOG && identDir != TabelaDeSimbolos.TipoLaVariavel.LOGICO)
                    utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(),"atribuicao nao compativel para " + atribuicao[0] + "\n");
                if(identEsq == TabelaDeSimbolos.TipoLaVariavel.PONT_REA && identDir != TabelaDeSimbolos.TipoLaVariavel.REAL)
                    utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(),"atribuicao nao compativel para " + atribuicao[0] + "\n");
                if(identEsq == TabelaDeSimbolos.TipoLaVariavel.PONT_LIT && identDir != TabelaDeSimbolos.TipoLaVariavel.LITERAL)
                    utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(),"atribuicao nao compativel para " + atribuicao[0] + "\n");
        }
        
        // Checa utilização dos identificador dentro de cmdSe.
        if(ctx.cmdSe() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            utils.verificarTipo(escopoAtual, ctx.cmdSe().expressao());
        }
        
        // Checa a uilização dos identificadores dentro de cmdChamada e checa numero de parametros e tipos.
        if(ctx.cmdChamada() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            String nomeFunProc = ctx.cmdChamada().IDENT().getText();
            if(!escopoAtual.existe(nomeFunProc))
                utils.adicionarErroSemantico(ctx.cmdChamada().IDENT().getSymbol(),"identificador " + nomeFunProc + " nao declarado\n");
            else{
                EntradaTabelaDeSimbolos funProc = escopoAtual.verificar(nomeFunProc);
                // Estrutura para armazenar os tipos de todos os parametros da chamada.
                ArrayList<TabelaDeSimbolos.TipoLaVariavel> tiposParametros = new ArrayList<>();
                
                for(var exp: ctx.cmdChamada().expressao()){ // Adiciona todos os tipos dos parametros no ArrayList.
                    tiposParametros.add(utils.verificarTipo(escopoAtual, exp));
                }
                
                // Caso o numero de parametros esteja diferente, ou os tipos nao batam, deve ser apontado um erro!
                if(!funProc.argsRegFunc.tipoValido(tiposParametros))
                    utils.adicionarErroSemantico(ctx.cmdChamada().IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nomeFunProc + "\n");
            }
        }
        
        return super.visitCmd(ctx);
    }
    
}