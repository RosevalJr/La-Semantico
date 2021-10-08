package br.ufscar.dc.compiladores.la.sintatico;

import java.util.LinkedList;
import java.util.List;

// Pilha de tabelas de simbolos, utilizada para manter os diversos escopos do programa.
// Nesta implementação feita do analisador semantico, terão no maximo 2 escopos, o mais ao fundo
// sendo o escopo global do programa e o mais a frente o escopo da ultima funcao ou procedimento.
// Quando o analisador chega no corpo do programa é mantido apenas o escopo global.
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
