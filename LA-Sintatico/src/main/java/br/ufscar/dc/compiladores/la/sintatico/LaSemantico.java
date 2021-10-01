package br.ufscar.dc.compiladores.la.sintatico;

import br.ufscar.dc.compiladores.la.sintatico.TabelaDeSimbolos.TipoLaVariavel;

public class LaSemantico extends LaSintaticoBaseVisitor<Void> {

    /*1. Identificador (variável, constante, procedimento, função, tipo) já declarado anteriormente no escopo. [X]
      2. Tipo não declarado. [X]
      3. Identificador (variável, constante, procedimento, função) não declarado []*/
    TabelaDeSimbolos tabela;
    
    Escopos escoposAninhados = new Escopos(); // Inicializa o escopo global.
    LaSemanticoUtils utils = new LaSemanticoUtils(); // Verificador de tipos.

    @Override
    public Void visitPrograma(LaSintaticoParser.ProgramaContext ctx) {
        // tabela = new TabelaDeSimbolos(); // Cria o escopo global.
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
    @Override 
    public Void visitDeclaracao_local(LaSintaticoParser.Declaracao_localContext ctx){
        // checar primeiro se esta no contexto atual !
        // Caso ja esteja apontar o erro de ja estar sido declarada antes.
        // Caso contrario inserir no contexto atual.

        //'constante' IDENT ':' tipo_basico '=' valor_constante | 'tipo' IDENT ':' tipo
        if(ctx.IDENT() != null){
            String identificador = ctx.IDENT().getText();
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            
            
            if(ctx.tipo_basico() != null){ // 'constante' IDENT ':' tipo_basico '=' valor_constante
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
            else{ // 'tipo' IDENT ':' tipo
                // TIPO | Neste caso pode existir por ser a declaracao de uma variavel do tipo declarado aqui ai vai ter dois mesmo.
                if(escopoAtual.existe(identificador)) 
                    utils.adicionarErroSemantico(ctx.IDENT().getSymbol(),"identificador " + identificador + " ja declarado anteriormente\n");
                else
                    escopoAtual.adicionar(identificador, TabelaDeSimbolos.TipoLaIdentificador.TIPO, null);
            }
        }
        // --- 'declare' variavel -> necessário acessar a variavel. (Caso com multiplas declaracoes...) ---.

        
        return super.visitDeclaracao_local(ctx); // visita os filhos.
    }
    
    // Necessario checar se os nomes das variaveis repetem no escopo atual e checar os tipos também.
    // VARIAVEL
    @Override 
    public Void visitVariavel(LaSintaticoParser.VariavelContext ctx){
        for(var test: ctx.identificador()){ // Cada um tem um tipo.
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
                String tipoDaVariavel = ctx.tipo().getText();
                System.out.println("---" + conca + ":" + tipoDaVariavel + "---");
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
                            else // Caso contrario ela pode entrar no escopo
                                escopoAtual.adicionar(conca, TabelaDeSimbolos.TipoLaIdentificador.REGISTRO, null);
                        }
                        if(!escopoAtual.existe(tipoDaVariavel)) // Caso o tipo nao exista mesmo, entao e um tipo nao declarado!
                            utils.adicionarErroSemantico(test.IDENT(0).getSymbol(), "tipo " + tipoDaVariavel + " nao declarado\n"); // Aqui esta errado !
                        break;
                }
            }
        }
        
        // Vendo se esta colocando na tabela de simbolos corretamente.
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        escopoAtual.print();
        System.out.println();
        
        return super.visitVariavel(ctx); // visita os filhos.
    }
    
    // Testando se os identificadores ja foram declarados !
    @Override 
    public Void visitCmd(LaSintaticoParser.CmdContext ctx){
        
        
        // Pode pegar direto o identificador.
        if(ctx.cmdLeia() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            for(var ident: ctx.cmdLeia().identificador()){
                if(!escopoAtual.existe(ident.getText()))
                    utils.adicionarErroSemantico(ident.IDENT(0).getSymbol(), "identificador " + ident.getText() + " nao declarado\n");
            }
        }
        
        // Nao pega direto
        if(ctx.cmdEscreva() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            for(var exp: ctx.cmdEscreva().expressao()){
                System.out.println("HAHA");
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
            String conca = "";
            for(var ident: ctx.cmdAtribuicao().identificador().IDENT()){
                conca += ident.getText();
            }
            if(!escopoAtual.existe(conca))
                    utils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(), "identificador " + conca + " nao declarado\n");
        }
        
        if(ctx.cmdSe() != null){
            TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
            utils.verificarTipo(escopoAtual, ctx.cmdSe().expressao());
        }
        
        return super.visitCmd(ctx);
    }
}