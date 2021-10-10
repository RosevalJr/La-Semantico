package br.ufscar.dc.compiladores.la.sintatico;

//import java.io.FileWriter;

import java.util.ArrayList;


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
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                        break;
                                    case "literal":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                        break;
                                    case "real":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                        break;
                                    case "logico":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                        break;
                                    case "^logico":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                        break;
                                    case "^real":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                        break;
                                    case "^literal":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                        break;
                                    case "^inteiro":
                                        camposTipo.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
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
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                saida.append("    int "+ identificadorVariavel + ";\n");
                                break;
                            case "literal":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                saida.append("    char "+ identificadorVariavel + "[80];\n");
                                break;
                            case "real":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                saida.append("    float "+ identificadorVariavel + ";\n");
                                break;
                            case "logico":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                break;
                            case "^logico":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                break;
                            case "^real":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                saida.append("    float* "+ identificadorVariavel + ";\n");
                                break;
                            case "^literal":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                saida.append("    char* "+ identificadorVariavel + "[80];\n");
                                break;
                            case "^inteiro":
                                escopoAtual.adicionar(identificadorVariavel, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
                                saida.append("    int* "+ identificadorVariavel + ";\n");
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
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.INTEIRO);
                                       break;
                                   case "literal":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LITERAL);
                                       break;
                                   case "real":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.REAL);
                                       break;
                                   case "logico":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.LOGICO);
                                       break;
                                   case "^logico":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LOG);
                                       break;
                                   case "^real":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_REA);
                                       break;
                                   case "^literal":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_LIT);
                                       break;
                                   case "^inteiro":
                                       camposRegistro.adicionar(nomeCampoRegistro, TabelaDeSimbolos.TipoLaIdentificador.VARIAVEL, TabelaDeSimbolos.TipoLaVariavel.PONT_INT);
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
        
        String[] atribuicao = ctx.getText().split("<-");
        String nomeIdentEsq = ctx.identificador().getText();
        
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
    public Void visitRegistro(LaSintaticoParser.RegistroContext ctx){
        saida.append("struct {\n"); 
        
        /*ctx.variavel().forEach(var -> visitDeclaracao_local(var));
        saida.append(";\n");
        
        saida.append("}");
        */
        return null;
    }
    
}
