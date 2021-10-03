package br.ufscar.dc.compiladores.la.sintatico;

// Conteudo do hashMap da TabelaDeSimbolos, aqui é armazenado o nome do identificador, tipo do identificador, tipo da variavel
// caso seja uma variavel e uma tabelaDeSimbolos utilizada para armazenar parametros de funcoes e procedimentos e variaveis de 
// registros.
class EntradaTabelaDeSimbolos {
        String nome;
        TabelaDeSimbolos.TipoLaIdentificador tipoIdentificador;
        TabelaDeSimbolos.TipoLaVariavel tipoVariavel;
        TabelaDeSimbolos argsRegFunc;

        /* Dois contrutores caso nao seja necessarios inserir a tabela de simbolos aninhada. */
        public EntradaTabelaDeSimbolos(String nome, TabelaDeSimbolos.TipoLaIdentificador tipoIdentificador, TabelaDeSimbolos.TipoLaVariavel tipoVariavel, TabelaDeSimbolos argsRegFunc) {
            this.nome = nome;
            this.tipoVariavel = tipoVariavel;
            this.tipoIdentificador = tipoIdentificador;
            this.argsRegFunc = argsRegFunc;
        }
        
        public EntradaTabelaDeSimbolos(String nome, TabelaDeSimbolos.TipoLaIdentificador tipoIdentificador, TabelaDeSimbolos.TipoLaVariavel tipoVariavel) {
            this.nome = nome;
            this.tipoVariavel = tipoVariavel;
            this.tipoIdentificador = tipoIdentificador;
        }
    }
