package br.ufscar.dc.compiladores.la.sintatico;

import br.ufscar.dc.compiladores.la.sintatico.TabelaDeSimbolos.TipoLaVariavel;
import java.util.ArrayList;

public class LaSemantico extends LaSintaticoBaseVisitor<Void> {

    /*1. Identificador (variável, constante, procedimento, função, tipo) já declarado anteriormente no escopo. [X]
      2. Tipo não declarado. [X]
      3. Identificador (variável, constante, procedimento, função) não declarado [X] -  Fiz cobrir os casos que estao nos casos de teste. [X]*/
    
    Escopos escoposAninhados = new Escopos(); // Inicializa o escopo global.
    LaSemanticoUtils utils = new LaSemanticoUtils(); // Verificador de tipos e modulos que auxiliam na analise semantica.

    @Override
    public Void visitPrograma(LaSintaticoParser.ProgramaContext ctx) { // Faz checagem de retorno invalido em funcoes e main. (procedimento nao tem retorno?)
        
        // O Corpo do programa nao deve ter RETORNE !
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
    
    @Override // PROCEDIMENTO E FUNCAO
    public Void visitDeclaracao_global(LaSintaticoParser.Declaracao_globalContext ctx){
        String identificador = ctx.IDENT().getText();
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        
        // FUNCAO
        if(ctx.tipo_estendido() != null){
            if(escopoAtual.existe(identificador))
                utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
            else
                escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.FUNCAO, null);
        }
        else{ // Procedimento
            if(escopoAtual.existe(identificador))
                utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
            else
                escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.PROCEDIMENTO, null);
        }
        return super.visitDeclaracao_global(ctx); // visita os filhos.
    }
    
    
    /*declaracao_local:'declare' variavel |
                      'constante' IDENT ':' tipo_basico '=' valor_constante |
                      'tipo' IDENT ':' tipo;*/
    /* Quando tentar declarar algo com um tipo que nao existe, esse algo sera inserido
     * na tabela de simbolos com tipo invalido...*/
    @Override 
    public Void visitDeclaracao_local(LaSintaticoParser.Declaracao_localContext ctx){
        // checar primeiro se esta no contexto atual !
        // Caso ja esteja apontar o erro de ja estar sido declarada antes.
        // Caso contrario inserir no contexto atual.

        //'constante' IDENT ':' tipo_basico '=' valor_constante | 'tipo' IDENT ':' tipo
        if(ctx.IDENT() != null){
            String identificador = ctx.IDENT().getText();
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            
            
            if(ctx.tipo_basico() != null){ // --- 'constante' IDENT ':' tipo_basico '=' valor_constante ---
                // CONSTANTE
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
            }
            else{ // --- 'tipo' IDENT ':' tipo ---
                // TIPO | Neste caso pode existir por ser a declaracao de uma variavel do tipo declarado aqui ai vai ter dois mesmo.
                System.out.println("Entrei");
                if(escopoAtual.existe(identificador)) 
                    utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
                else{ // Caso o tipo nao seja repetido !
                    TabelaDeSimbolos tabela = new TabelaDeSimbolos();
                    escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.TIPO, null, tabela);
                    
                    // Acessando a tabelaDeSimbolos interna ao TIPO.
                    EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(identificador);
                    TabelaDeSimbolos tabelinha = entrada.argsRegFunc;
                    for(var variavel : ctx.tipo().registro().variavel()){

                        for(var test: variavel.identificador()){ // Cada um tem um tipo.
                            // Aqui nao vai ter "." mas e necessario concatenar ainda.
                            String conca = "";
                            for(var ident: test.IDENT()){
                                conca += ident;
                            }

                            // Nao pode repetir o nomes dos campos do registros ...
                            if(tabelinha.existe(conca))
                                utils.adicionarErroSemantico(test.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                            else{
                                // Pega o tipo da variavel.
                                String tipoDaVariavel = variavel.tipo().getText();

                                switch(tipoDaVariavel){
                                    case "inteiro":
                                        tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                        break;
                                    case "literal":
                                        tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                        break;
                                    case "real":
                                        tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                        break;
                                    case "logico":
                                        tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                        break;
                                    case "^logico":
                                        tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                        break;
                                    case "^real":
                                        tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                        break;
                                    case "^literal":
                                        tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                        break;
                                    case "^inteiro":
                                        tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
                                        break;                       
                                    default: // Nao estou tratando se tiver usando um tipo criado aqui na criação de um registro.
                                        // Checa se o tipo da variavel existe.
                                        //if(!escopoAtual.existe(tipoDaVariavel)) // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                                        //    utils.adicionarErroSemantico(test.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); // Aqui esta errado !
                                        break;
                                }
                            }
                        }
                    }
                    System.out.println();
                    System.out.println("Escopo interno de "+ identificador);
                    tabelinha.print();
                    System.out.println();
                }
            }
        }
        else{// --- 'declare' variavel --- ATENÇÃO NO CASO 11 ! aqui pode entrar também para fazer uma declaracao ja do registro...
            if(ctx.variavel().tipo().registro() == null){
                for(var test: ctx.variavel().identificador()){ // Cada um tem um tipo.
                    // Peculiar por que aqui tem o ponto
                    String conca = "";

                    TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();

                    for(var ident: test.IDENT()){
                        conca += ident;
                    }
                     // Para causar erro tem que ser uma variavel sendo declarada.
                    if(escopoAtual.existe(conca))// Aqui pode dar erro por que estou pegando só o token Inicial do IDENT.
                        utils.adicionarErroSemantico(test.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                    else{ // Checa que tipo é a variavel.
                        String tipoDaVariavel = ctx.variavel().tipo().getText();
                        //System.out.println("---" + conca + ":" + tipoDaVariavel + "---");
                        switch(tipoDaVariavel){
                            case "inteiro":
                                escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                break;
                            case "literal":
                                escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                break;
                            case "real":
                                escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                break;
                            case "logico":
                                escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                break;
                            case "^logico":
                                escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                break;
                            case "^real":
                                escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                break;
                            case "^literal":
                                escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                break;
                            case "^inteiro":
                                escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
                                break;                       
                            default: // Se chegar aqui e um tipo nao basico.
                                // Caso o tipo foi declarado no escopoAtual !!! Na verdade ele tem que ser global?
                                // TipoDaVariavel esta null!
                                if(escopoAtual.existe(tipoDaVariavel) && escopoAtual.verificar(tipoDaVariavel).tipoIdentificaor == TabelaDeSimbolos.TipoLaIdentificador.TIPO){
                                    // Caso a variavel ja existe e ela for do tipo registro aponta erro
                                    if(escopoAtual.existe(conca))
                                        utils.adicionarErroSemantico(test.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                                    else{ // Caso contrario ela pode entrar no escopo
                                        // Acessando a tabelaDeSimbolos interna ao TIPO.
                                        EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(tipoDaVariavel);
                                        TabelaDeSimbolos tabelinha = entrada.argsRegFunc;
                                        escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, null, tabelinha);

                                        // Para cada argumento do tipo...
                                        // Na verdade posso só enfiar o ponteiro no registro. [PODE DAR PAU!]
                                    }
                                }
                                if(!escopoAtual.existe(tipoDaVariavel)){ // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                                    utils.adicionarErroSemantico(test.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); // Aqui esta errado !
                                    escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                }
                                break;
                        }
                    }
                }

                // Vendo se esta colocando na tabela de simbolos corretamente.
                System.out.println();
                TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
                escopoAtual.print();
                System.out.println();
            }
            else{// caso for um ""declare" variavel com registro". Neste caso nao sera criado um "tipo" antes para ser usado depois, todas as instancias do registro serao definidas antes.
                ArrayList<String> identRegistros = new ArrayList<>();
                for(var identReg: ctx.variavel().identificador()){
                    String conca = "";
                    
                    TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
                    
                    for(var ident: identReg.IDENT()){
                        conca += ident;
                    }
                    // Para causar erro tem que ser uma variavel sendo declarada.
                    if(escopoAtual.existe(conca))// Aqui pode dar erro por que estou pegando só o token Inicial do IDENT.
                        utils.adicionarErroSemantico(identReg.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                    else{
                        TabelaDeSimbolos tabela = new TabelaDeSimbolos();
                        escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, null, tabela);
                        identRegistros.add(conca); //  Guardando todos os identificadores do registro que foram declarados.
                    }
                }
                for(var test: ctx.variavel().tipo().registro().variavel()){ // Cada um tem um tipo.
                    for(var testAux: test.identificador()){
                        // Peculiar por que aqui tem o ponto
                        String conca = "";

                        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();

                        for(var ident: testAux.IDENT()){
                            conca += ident;
                        }
                        for(String identRegistro: identRegistros){
                            EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(identRegistro);
                            TabelaDeSimbolos tabelinha = entrada.argsRegFunc;
                            
                            // Para causar erro tem que ser uma variavel sendo declarada.
                           if(tabelinha.existe(conca))// Aqui pode dar erro por que estou pegando só o token Inicial do IDENT.
                               utils.adicionarErroSemantico(testAux.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                           else{ // Checa que tipo é a variavel.
                               String tipoDaVariavel = test.tipo().getText();
                               //System.out.println("---" + conca + ":" + tipoDaVariavel + "---");
                               switch(tipoDaVariavel){
                                   case "inteiro":
                                       tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                       break;
                                   case "literal":
                                       tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                       break;
                                   case "real":
                                       tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                       break;
                                   case "logico":
                                       tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                       break;
                                   case "^logico":
                                       tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                       break;
                                   case "^real":
                                       tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                       break;
                                   case "^literal":
                                       tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                       break;
                                   case "^inteiro":
                                       tabelinha.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
                                       break;                       
                                   default: // Se chegar aqui e um tipo nao basico.
                                       // Caso o tipo foi declarado no escopoAtual !!! Na verdade ele tem que ser global?
                                       // TipoDaVariavel esta null!
                                       if(!escopoAtual.existe(tipoDaVariavel)){ // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                                           utils.adicionarErroSemantico(testAux.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); // Aqui esta errado !
                                           escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                       }
                                       break;
                               }
                           }
                        }
                    }

                    // Vendo se esta colocando na tabela de simbolos corretamente.
                    System.out.println();
                    TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
                    escopoAtual.print();
                    System.out.println();
                }
            }
        }
        
        return super.visitDeclaracao_local(ctx); // visita os filhos.
    }
    
    // Testando se os identificadores ja foram declarados !
    @Override 
    public Void visitCmd(LaSintaticoParser.CmdContext ctx){
        
        
        // Pode pegar direto o identificador.
        if(ctx.cmdLeia() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            for(var ident: ctx.cmdLeia().identificador()){
                //System.out.println(ident.getText()); // Carrega o ponto ja !
                
                /* Tudo isso e feito por causa da existencia de registros ... */
                if(!ident.getText().contains("[") && !ident.getText().contains("]")){ // Caso tenha dimensao.
                    String[] nomePartes = ident.getText().split("\\.");

                    if(!escopoAtual.existe(nomePartes[0])){
                        utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(), "identificador " + ident.getText() + " nao declarado\n");
                    }
                    else{
                        EntradaTabelaDeSimbolos possivelRegistro = escopoAtual.verificar(nomePartes[0]);
                        if(possivelRegistro.tipoIdentificaor == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                            TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                            if(!camposRegistro.existe(nomePartes[1])){
                                utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(), "identificador " + ident.getText() + " nao declarado\n");
                            }
                        }
                    }
                }
                else{
                    // Verifica se as variaveis usadas dentro da dimensao foram ja declaradas.
                    for(var xp: ident.dimensao().exp_aritmetica())
                        utils.verificarTipo(escopoAtual, xp);
                    if(!escopoAtual.existe(ident.IDENT(0).getText())){
                        utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(), "identificador " + ident.IDENT(0).getText() + " nao declarado\n");
                    }
                }
                
            }
        }
        
        // Nao pega direto
        if(ctx.cmdEscreva() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            for(var exp: ctx.cmdEscreva().expressao()){
                if(exp != null)
                    utils.verificarTipo(escopoAtual, exp);
                else
                    System.out.println("MAS ORAS!");
            }
        }
        
        if(ctx.cmdEnquanto() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            utils.verificarTipo(escopoAtual, ctx.cmdEnquanto().expressao());
        }
        
        if(ctx.cmdAtribuicao() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            if(!ctx.cmdAtribuicao().identificador().getText().contains("[") && ctx.cmdAtribuicao().identificador().getText().contains("]")){ // Caso tenha dimensao.
                String identificador = ctx.cmdAtribuicao().identificador().getText();
                String[] nomePartes = identificador.split("\\.");

                if(!escopoAtual.existe(nomePartes[0])){
                    utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                }
                else{
                    EntradaTabelaDeSimbolos possivelRegistro = escopoAtual.verificar(nomePartes[0]);
                    if(possivelRegistro.tipoIdentificaor == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                        TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                        if(!camposRegistro.existe(nomePartes[1])){
                            utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                        }
                    }
                }
            }
            else{
                // Verifica se as variaveis usadas dentro da dimensao foram ja declaradas.
                for(var xp: ctx.cmdAtribuicao().identificador().dimensao().exp_aritmetica())
                    utils.verificarTipo(escopoAtual, xp);
                if(!escopoAtual.existe(ctx.cmdAtribuicao().identificador().IDENT(0).getText())){
                    utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(), "identificador " + ctx.cmdAtribuicao().identificador().IDENT(0).getText() + " nao declarado\n");
                }
            }
        }
        
        if(ctx.cmdSe() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            utils.verificarTipo(escopoAtual, ctx.cmdSe().expressao());
        }
        
        return super.visitCmd(ctx);
    }
    
}