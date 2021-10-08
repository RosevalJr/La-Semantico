package br.ufscar.dc.compiladores.la.sintatico;

import java.util.ArrayList;
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
        PONT_LIT,
        REGISTRO,
        ENDERECO,
        NAO_DECLARADO
    }
    
    // Tabela de simbolos e um hashMap com chave sendo o identificador e conteudo uma EntradaTabelaDeSimbolos, que contém as informações
    // necessarias do identificador dentro do contexto semantico do analisador.
    private final Map<String, EntradaTabelaDeSimbolos> tabela;
    
    // Possivel TabelaDeSimbolos aninhada, caso a tabela de simbolos instanciada seja um escopo mais interno do programa, como uma funcao ou registro.
    // Sendo uma funcao ou registro o atributo "global" é usado para facilmente acessar o escopo global do programa.
    private TabelaDeSimbolos global;
    
    public TabelaDeSimbolos() {
        this.tabela = new HashMap<>();
        this.global = null;
    }
    
    // Seta o escopo global, caso a this. é um escopo de funcao ou procedimento.
    public void setGlobao(TabelaDeSimbolos global){
        this.global = global;
    }
    
    // adiciona novo identificador na tabela de simbolos (variavel).
    public void adicionar(String nome, TipoLaIdentificador tipoIdentificador, TipoLaVariavel tipoVariavel) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipoIdentificador, tipoVariavel));
    }
    
    // Esse adicionar é utilizado para inserir uma tabela de simbolos dentro da entrada de tabela de simbolos, dessa forma é possivel
    // manter o controle dos tipos das variaveis de cada registro, parametros de funcoes e procedimentos.
    public void adicionar(String nome, TipoLaIdentificador tipoIdentificador, TipoLaVariavel tipoVariavel, TabelaDeSimbolos argsRegFunc){
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipoIdentificador, tipoVariavel, argsRegFunc));
    }
    
    // Tipo especial pode ser o TIPO declarado de registros, ou o tipo de retorno para funcao.
    public void adicionar(String nome, TipoLaIdentificador tipoIdentificador, TipoLaVariavel tipoVariavel, TabelaDeSimbolos argsRegFunc, String TipoEspecial){
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipoIdentificador, tipoVariavel, argsRegFunc, TipoEspecial));
    }
            
    // Esse metodo retorna true caso o identificador exista na tabela, caso contrario retorna false.
    // Entretanto, caso a tabela de simbolos tenha um escopo global para acessar, é necessario procurar
    // no escopo global também.
    public boolean existe(String nome) {
        if(global == null)
            return tabela.containsKey(nome);
        else{
            return tabela.containsKey(nome) || global.existe(nome);
        }
    }
    
    // Esse metodo retorna o a entrada da tabela de simbolos, dado o identificador passado como entrada.
    // Entretanto, caso o a tabela de simbolos tenha um escopo global para acessar, é necessario procurar 
    // também no escopo global.
    public EntradaTabelaDeSimbolos verificar(String nome) {
        if(global == null)
            return tabela.get(nome);
        else{
            if(tabela.containsKey(nome))
                return tabela.get(nome);
            else
                return global.verificar(nome);
        }
    }
    
    // Esse metodo é utilizado caso a tabela de simbolos seja uma tabela de simbolos aninhada para uma funcao
    // ou procedimento, armazenando seus parametros. Nesse método é feito a checagem dos parametros, sendo checado
    // se o numero de parametros é igual ao numero de tipos passados e checando caso os tipos sejam iguais.
    // Caso algum erro seja detectado é retornado false.
    public boolean tipoValido(ArrayList<TabelaDeSimbolos.TipoLaVariavel> tipos){
        int contador = 0;
        
        // Caso o numero de parametros esteje errado.
        if(tabela.size() != tipos.size())
            return false;
        
        for(var entrada: tabela.values()){
            
            // Caso os tipos nao batam.
            if(tipos.get(contador) != entrada.tipoVariavel){
                return false;
            }
            contador++;
        }
        
        return true;
    }
    
    // Metodo utilizado para debuging, visualizando o que tem na tabela.
    public void print(){
        for(var valor: tabela.values()){
            System.out.println("-----\nNome: " + valor.nome);
            System.out.println("TipoIdentificador: " + valor.tipoIdentificador);
            if(valor.tipoVariavel != null)
                System.out.println("TipoVariavel: " + valor.tipoVariavel);
            if(valor.tipoEspecial != null)
                System.out.println("TipoEspecial: " + valor.tipoEspecial);
        }
    }
}