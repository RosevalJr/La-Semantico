package br.ufscar.dc.compiladores.la.sintatico;

import br.ufscar.dc.compiladores.la.sintatico.TabelaDeSimbolos.TipoLaVariavel;
import java.util.ArrayList;

public class LaSemantico extends LaSintaticoBaseVisitor<Void> {

    /*1. Identificador (variável, constante, procedimento, função, tipo) já declarado anteriormente no escopo. [X]
      2. Tipo não declarado. [X]
      3. Identificador (variável, constante, procedimento, função) não declarado [X] -  Fiz cobrir os casos que estao nos casos de teste. [X]*/
    
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
     * armazenado seu identificador na tabela de simbolos dos escopoAtual. [Futuramente inserir
     * parametros e retorno.] */
    @Override 
    public Void visitDeclaracao_global(LaSintaticoParser.Declaracao_globalContext ctx){
        // Pega identificador e escopo atual.
        String identificador = ctx.IDENT().getText();
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        
        // Caso seja uma função
        if(ctx.tipo_estendido() != null){
            if(escopoAtual.existe(identificador)) 
                utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
            else
                escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.FUNCAO, null);
        }
        else{ // Caso contrario é um procedimento
            if(escopoAtual.existe(identificador))
                utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
            else
                escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.PROCEDIMENTO, null);
        }
        
        return super.visitDeclaracao_global(ctx); // visita os filhos.
    }
    
    
    /*declaracao_local:'declare' variavel |
                      'constante' IDENT ':' tipo_basico '=' valor_constante |
                      'tipo' IDENT ':' tipo; */
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
            }
            else{ // 'tipo' IDENT ':' tipo 
                // Aqui é realizado a declaração de um novo tipo, usado para declarar registros posteriormente.
                if(escopoAtual.existe(identificador)) 
                    utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
                else{ // Caso o identificador ja nao esteja sendo usado!
                    
                    // Cria o novo tipo na tabela de simbolos, inserindo com uma tabela de simbolos aninhada.
                    // Essa tabela sera usada para armazenas as variaveis do tipo.
                    TabelaDeSimbolos tabela = new TabelaDeSimbolos();
                    escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.TIPO, null, tabela);
                    
                    // Acessando a tabelaDeSimbolos interna ao TIPO. [Posso simplesmente usar o "tabela" instanciado acima]
                    EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(identificador);
                    TabelaDeSimbolos tabelaAninhadaTipo = entrada.argsRegFunc;
                    
                    for(var variavel : ctx.tipo().registro().variavel()){

                        for(var test: variavel.identificador()){ // Cada variavel te um tipo
                            
                            // Aqui nao vai ter "." mas e necessario concatenar ainda.
                            String conca = "";
                            for(var ident: test.IDENT()){
                                conca += ident;
                            }

                            // Nao pode repetir o nomes dos campos do registros ...
                            if(tabelaAninhadaTipo.existe(conca))
                                utils.adicionarErroSemantico(test.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                            else{
                                // Pega o tipo da variavel.
                                String tipoDaVariavel = variavel.tipo().getText();

                                switch(tipoDaVariavel){
                                    case "inteiro":
                                        tabelaAninhadaTipo.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                        break;
                                    case "literal":
                                        tabelaAninhadaTipo.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                        break;
                                    case "real":
                                        tabelaAninhadaTipo.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                        break;
                                    case "logico":
                                        tabelaAninhadaTipo.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                        break;
                                    case "^logico":
                                        tabelaAninhadaTipo.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                        break;
                                    case "^real":
                                        tabelaAninhadaTipo.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                        break;
                                    case "^literal":
                                        tabelaAninhadaTipo.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                        break;
                                    case "^inteiro":
                                        tabelaAninhadaTipo.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
                                        break;                       
                                    default: // Nao estou tratando se tiver usando um tipo criado aqui na criação de um registro.
                                        break;
                                }
                            }
                        }
                    }
                    // Checa o estado atual da tabela aninhada do tipo. [visualizar se esta correto]
                    System.out.println();
                    System.out.println("Escopo interno de "+ identificador);
                    tabelaAninhadaTipo.print();
                    System.out.println();
                }
            }
        }
        else{// 'declare' variavel 
            // Caso nao seja uma declaracao de registro direta !
            if(ctx.variavel().tipo().registro() == null){
                for(var test: ctx.variavel().identificador()){ // Cada variavel tem um tipo.
                    // Nao é necessario concatenar.
                    String conca = "";

                    TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();

                    for(var ident: test.IDENT()){
                        conca += ident;
                    }
                     // Caso o identificador da variavel ja esteja sendo usada.
                    if(escopoAtual.existe(conca))
                        utils.adicionarErroSemantico(test.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                    else{ // Caso contrario pode ser declarado.
                        
                        String tipoDaVariavel = ctx.variavel().tipo().getText();
                        
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
                            default: // Se chegar aqui e um tipo nao basico!
                                // Checa se o identificador tipo da variavel existe na tabela de simbolos, caso ele existe e seja um tipo este sendo declarado um registro. 
                                if(escopoAtual.existe(tipoDaVariavel) && escopoAtual.verificar(tipoDaVariavel).tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.TIPO){
                                    
                                    if(escopoAtual.existe(conca)) // Caso a variavel ja existe aponta erro.
                                        utils.adicionarErroSemantico(test.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                                    else{ // Caso contrario o registro pode ser declarado.
                                        
                                        // Acessando a tabelaDeSimbolos aninhada interna ao TIPO, a fim de criar o registro com os "campos" corretos.
                                        EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(tipoDaVariavel);
                                        TabelaDeSimbolos tabelaAninhadaTIpo = entrada.argsRegFunc;
                                        escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, null, tabelaAninhadaTIpo);
                                    }
                                }
                                
                                if(!escopoAtual.existe(tipoDaVariavel)){ // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                                    utils.adicionarErroSemantico(test.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); 
                                    // Coloca a variavel na tabela de simbolos com tipo invalido.
                                    escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                }
                                break;
                        }
                    }
                }

                // Checa o estado atual da tabela de simbolos para verificar se esta correto.
                System.out.println();
                TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
                escopoAtual.print();
                System.out.println();
            }
            else{// caso seja feita a declaracao de um registro sem declarar o tipo antes. 
                // Neste caso nao sera criado um "tipo" antes para ser usado depois, todas as instancias do registro serao definidas antes.
                
                ArrayList<String> identRegistros = new ArrayList<>(); // Armazenar os identificadores dos registros a serem declarados.
                for(var identReg: ctx.variavel().identificador()){
                    String conca = "";
                    
                    TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
                    
                    for(var ident: identReg.IDENT()){
                        conca += ident;
                    }
                    
                    if(escopoAtual.existe(conca)) // Identificador nao pode repetir.
                        utils.adicionarErroSemantico(identReg.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                    else{
                        TabelaDeSimbolos tabela = new TabelaDeSimbolos();
                        escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, null, tabela);
                        identRegistros.add(conca); //  Guardando todos os identificadores do registro que foram declarados.
                    }
                }
                for(var ctxVariavelRegistro: ctx.variavel().tipo().registro().variavel()){ // Cada "campo" dos registros tem um tipo.
                    for(var variavelIdent: ctxVariavelRegistro.identificador()){
                        
                        String conca = "";

                        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();

                        for(var ident: variavelIdent.IDENT()){
                            conca += ident;
                        }
                        for(String identRegistro: identRegistros){ // Para cada um dos registros, inserir o "campo" em sua tabela de simbolos interna.
                            EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(identRegistro);
                            TabelaDeSimbolos tabelaAninhadaRegistro = entrada.argsRegFunc;
                            
                           if(tabelaAninhadaRegistro.existe(conca))// Nao pode repetir o identificador dentro da declaração dos registros.
                               utils.adicionarErroSemantico(variavelIdent.IDENT(0).getSymbol(),"identificador " + conca + " ja declarado anteriormente\n");
                           else{ // Checa que tipo é a variavel.
                               String tipoDaVariavel = ctxVariavelRegistro.tipo().getText();
                               
                               switch(tipoDaVariavel){
                                   case "inteiro":
                                       tabelaAninhadaRegistro.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.INTEIRO);
                                       break;
                                   case "literal":
                                       tabelaAninhadaRegistro.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LITERAL);
                                       break;
                                   case "real":
                                       tabelaAninhadaRegistro.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.REAL);
                                       break;
                                   case "logico":
                                       tabelaAninhadaRegistro.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.LOGICO);
                                       break;
                                   case "^logico":
                                       tabelaAninhadaRegistro.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LOG);
                                       break;
                                   case "^real":
                                       tabelaAninhadaRegistro.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_REA);
                                       break;
                                   case "^literal":
                                       tabelaAninhadaRegistro.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_LIT);
                                       break;
                                   case "^inteiro":
                                       tabelaAninhadaRegistro.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TipoLaVariavel.PONT_INT);
                                       break;                       
                                   default: // Se chegar aqui e um tipo nao basico.
                                       // Nao checo se esta sendo usado um tipo criado antes "registro dentro de registro".
                                       if(!escopoAtual.existe(tipoDaVariavel)){ // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                                           utils.adicionarErroSemantico(variavelIdent.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); // Aqui esta errado !
                                           escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INVALIDO);
                                       }
                                       break;
                               }
                           }
                        }
                    }

                    // Checando o estado da tabela aninhada do registro para checar se esta correto.
                    System.out.println();
                    TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
                    escopoAtual.print();
                    System.out.println();
                }
            }
        }
        
        return super.visitDeclaracao_local(ctx); // visita os filhos.
    }
    
    /* Override do visitCmd para checar se os identificador ja foram declarados quando eles sao utilizados! */
    @Override 
    public Void visitCmd(LaSintaticoParser.CmdContext ctx){
        
        // Checando utilizacao dos identificador dentro do cmdLeia.
        if(ctx.cmdLeia() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            for(var ident: ctx.cmdLeia().identificador()){
                
                /* Tudo isso e feito por causa da existencia de registros ... */
                if(!ident.getText().contains("[") && !ident.getText().contains("]")){ // Caso nao tenha dimensao.
                    String[] nomePartes = ident.getText().split("\\.");
                    
                    // Checa se o identificador principal foi declarado ja.
                    if(!escopoAtual.existe(nomePartes[0])){
                        utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(), "identificador " + ident.getText() + " nao declarado\n");
                    }
                    else{
                        // Caso seja um registro tipo: "vinho.Preco", necessario checar se o .Preco é um campo do registro do tipo vinho.
                        EntradaTabelaDeSimbolos possivelRegistro = escopoAtual.verificar(nomePartes[0]);
                        if(possivelRegistro.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                            TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                            if(!camposRegistro.existe(nomePartes[1])){
                                utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(), "identificador " + ident.getText() + " nao declarado\n");
                            }
                        }
                    }
                }
                else{ // Caso tenha dimensão
                    // Verifica se as expressoes usadas dentro da dimensao usam variaveis ja declaradas.
                    for(var exp: ident.dimensao().exp_aritmetica())
                        utils.verificarTipo(escopoAtual, exp);
                    // Verifica se o primeiro identificador ja foi declarado tipo: "vinho[0]", checa se vinho ja foi declarado.
                    if(!escopoAtual.existe(ident.IDENT(0).getText())){
                        utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(), "identificador " + ident.IDENT(0).getText() + " nao declarado\n");
                    }
                }
                
            }
        }
        
        // Checa utilização dos identificador dentro de cmdEscreva.
        if(ctx.cmdEscreva() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            for(var exp: ctx.cmdEscreva().expressao()){
                // Verifica se as expressoes usadas dentro do cmdEscreva usam variaveis ja declaradas. 
                utils.verificarTipo(escopoAtual, exp);
            }
        }
        
        // Checa utilização dos identificador dentro de cmdEnquanto.
        if(ctx.cmdEnquanto() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            // Verifica se as expressoes usadas dentro do cmdEnquanto usam variaveis ja declaradas. 
            utils.verificarTipo(escopoAtual, ctx.cmdEnquanto().expressao());
        }
        
        // Checa utilização dos identificador dentro de cmdAtribuicao.
        if(ctx.cmdAtribuicao() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            
            /* Tudo isso e feito por causa da existencia de registros ... */
            if(!ctx.cmdAtribuicao().identificador().getText().contains("[") && ctx.cmdAtribuicao().identificador().getText().contains("]")){ // Caso tenha dimensao.
                String identificador = ctx.cmdAtribuicao().identificador().getText();
                String[] nomePartes = identificador.split("\\.");
                
                // Checa se o identificador principal foi declarado ja.
                if(!escopoAtual.existe(nomePartes[0])){
                    utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                }
                else{
                    // Caso seja um registro tipo: "vinho.Preco", necessario checar se o .Preco é um campo do registro do tipo vinho.
                    EntradaTabelaDeSimbolos possivelRegistro = escopoAtual.verificar(nomePartes[0]);
                    if(possivelRegistro.tipoIdentificador == TabelaDeSimbolos.TipoLaIdentificador.REGISTRO && nomePartes.length > 1){
                        TabelaDeSimbolos camposRegistro = possivelRegistro.argsRegFunc;
                        if(!camposRegistro.existe(nomePartes[1])){
                            utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(), "identificador " + identificador + " nao declarado\n");
                        }
                    }
                }
            }
            else{ // Caso tenha dimensao
                // Verifica se as expressoes usadas dentro da dimensao usam variaveis ja declaradas.
                for(var xp: ctx.cmdAtribuicao().identificador().dimensao().exp_aritmetica())
                    utils.verificarTipo(escopoAtual, xp);
                // Verifica se o primeiro identificador ja foi declarado tipo: "vinho[0]", checa se vinho ja foi declarado.
                if(!escopoAtual.existe(ctx.cmdAtribuicao().identificador().IDENT(0).getText())){
                    utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(), "identificador " + ctx.cmdAtribuicao().identificador().IDENT(0).getText() + " nao declarado\n");
                }
            }
        }
        
        // Checa utilização dos identificador dentro de cmdSe.
        if(ctx.cmdSe() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            utils.verificarTipo(escopoAtual, ctx.cmdSe().expressao());
        }
        
        return super.visitCmd(ctx);
    }
    
}