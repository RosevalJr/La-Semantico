package br.ufscar.dc.compiladores.la.sintatico;

import java.util.LinkedList;
import java.util.List;

// Pilha de tabelas de simbolos, utilizada para manter os diversos escopos do programa.
// Ainda estou estudando para ver se ela é necessaria neste trabalho.
public class Escopos {
    private LinkedList<TabelaDeSimbolos> pilhaDeTabelas;
    
    public Escopos(){
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo(); // Comeca com o escopo global.
    }
    
    public void criarNovoEscopo(){
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }
    
    public TabelaDeSimbolos obterEscopoAtual(){
        return pilhaDeTabelas.peek();
    }
    
    // Tabalha com list por que talvez nos queremos alguma variaveis que esta la
    // dentro do escopo global.
    public List<TabelaDeSimbolos> percorrerEscoposAninhados(){
        return pilhaDeTabelas;
    }
    
    public void abandonarEscopo(){
        pilhaDeTabelas.pop();
    }
}
