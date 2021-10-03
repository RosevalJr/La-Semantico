package br.ufscar.dc.compiladores.la.sintatico;

import java.util.HashMap;
import java.util.Map;

// Classe de tabela de simbolos, utilizada para manter o controle de tipos de identificadores, variaveis
// paramentrosde funcoes e procedimento e variaveis de registros. A tabela sera um hashMap tendo como chave
// o nome do identificador e como conteudo uma EntradaTabelaDeSimbolos.
public class TabelaDeSimbolos {
    
    // Tipo do identificador.
    public enum TipoLaIdentificador{
        FUNCAO,
        PROCEDIMENTO,
        TIPO,
        VARIAVEL,
        REGISTRO,
        CONSTANTE
    }
    
    // Tipo da variavel.
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
    
    // Esse adicionar é utilizado para inserir uma tabela de simbolos dentro da entrada de tabela de simbolos, dessa forma é possivel
    // manter o controle dos tipos das variaveis de cada registro e futuramente parametros de funcoes e procedimentos.
    public void adicionar(String nome, TipoLaIdentificador tipoIdentificador, TipoLaVariavel tipoVariavel, TabelaDeSimbolos argsRegFunc){
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipoIdentificador, tipoVariavel, argsRegFunc));
    }
            
    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }
    
    public EntradaTabelaDeSimbolos verificar(String nome) {
        return tabela.get(nome);
    }
    
    // Metodo utilizado para debuging, visualizando o que tem na tabela.
    public void print(){
        for(var hehe: tabela.values()){
            System.out.println("-----\nNome: " + hehe.nome);
            System.out.println("TipoIdentificador: " + hehe.tipoIdentificador);
            if(hehe.tipoVariavel != null)
                System.out.println("TipoVariavel: " + hehe.tipoVariavel);
        }
    }
}