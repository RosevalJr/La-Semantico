package br.ufscar.dc.compiladores.la.sintatico;

// Conteudo do hashMap da TabelaDeSimbolos, aqui é armazenado o nome do identificador, tipo do identificador, tipo da variavel
// caso seja uma variavel e uma tabelaDeSimbolos utilizada para armazenar parametros de funcoes e procedimentos e variaveis de 
// registros.
class EntradaTabelaDeSimbolos {
        String nome;
        TabelaDeSimbolos.TipoLaIdentificador tipoIdentificador;
        TabelaDeSimbolos.TipoLaVariavel tipoVariavel;
        // Tabela de simbolos aninhada para armazenar parametros de funcao e procedimento ou campos de registro.
        TabelaDeSimbolos argsRegFunc;
        // Tipo de retorna de funcao, ou TIPO de registro.
        String tipoEspecial;

        /* Tres contrutores caso nao seja necessarios inserir a tabela de simbolos aninhada ou o TipoEspecial(tipo de retorno da funcao ou String do TIPO do registro). */
        public EntradaTabelaDeSimbolos(String nome, TabelaDeSimbolos.TipoLaIdentificador tipoIdentificador, TabelaDeSimbolos.TipoLaVariavel tipoVariavel, TabelaDeSimbolos argsRegFunc) {
            this.nome = nome;
            this.tipoVariavel = tipoVariavel;
            this.tipoIdentificador = tipoIdentificador;
            this.argsRegFunc = argsRegFunc;
        }
        
        public EntradaTabelaDeSimbolos(String nome, TabelaDeSimbolos.TipoLaIdentificador tipoIdentificador, TabelaDeSimbolos.TipoLaVariavel tipoVariavel, TabelaDeSimbolos argsRegFunc, String tipoEspecial) {
            this.nome = nome;
            this.tipoVariavel = tipoVariavel;
            this.tipoIdentificador = tipoIdentificador;
            this.argsRegFunc = argsRegFunc;
            this.tipoEspecial = tipoEspecial;
        }
        
        public EntradaTabelaDeSimbolos(String nome, TabelaDeSimbolos.TipoLaIdentificador tipoIdentificador, TabelaDeSimbolos.TipoLaVariavel tipoVariavel) {
            this.nome = nome;
            this.tipoVariavel = tipoVariavel;
            this.tipoIdentificador = tipoIdentificador;
        }
    }
