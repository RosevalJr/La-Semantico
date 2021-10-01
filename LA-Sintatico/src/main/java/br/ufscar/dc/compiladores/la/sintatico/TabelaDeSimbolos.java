package br.ufscar.dc.compiladores.la.sintatico;

import java.util.HashMap;
import java.util.Map;

// Talvez seja suficiente ter uma tabela de simbolos global, e uma local pra cada escopo.
public class TabelaDeSimbolos {
    
    // O que é o identificador?
    public enum TipoLaIdentificador{
        FUNCAO,
        PROCEDIMENTO,
        TIPO,
        VARIAVEL,
        REGISTRO,
        CONSTANTE
    }
    
    // Existem diversos tipos para as variaveis.
    public enum TipoLaVariavel {
        INTEIRO,
        REAL,
        LITERAL,
        LOGICO,
        INVALIDO,
        PONT_INT,
        PONT_LOG,
        PONT_REA,
        PONT_LIT
    }
    
    private final Map<String, EntradaTabelaDeSimbolos> tabela;
    
    public TabelaDeSimbolos() {
        this.tabela = new HashMap<>();
    }
    
    public void adicionar(String nome, TipoLaIdentificador tipoIdentificador, TipoLaVariavel tipoVariavel) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipoIdentificador, tipoVariavel));
    }
    
    // Isso e utilizado em novos tipos/registros e funcoes.
    public void adicionar(String nome, TipoLaIdentificador tipoIdentificador, TipoLaVariavel tipoVariavel, TabelaDeSimbolos argsRegFunc){
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipoIdentificador, tipoVariavel, argsRegFunc));
    }
            
    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }
    
    public EntradaTabelaDeSimbolos verificar(String nome) {
        return tabela.get(nome);
    }
    
    public void print(){
        for(var hehe: tabela.values()){
            System.out.println("-----\nNome: " + hehe.nome);
            System.out.println("TipoIdentificador: " + hehe.tipoIdentificaor);
            if(hehe.tipoVariavel != null)
                System.out.println("TipoVariavel: " + hehe.tipoVariavel);
        }
    }
}