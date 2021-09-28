package br.ufscar.dc.compiladores.la.sintatico;

class EntradaTabelaDeSimbolos {
        String nome;
        TabelaDeSimbolos.TipoLaIdentificador tipoIdentificaor;
        TabelaDeSimbolos.TipoLaVariavel tipoVariavel;

        public EntradaTabelaDeSimbolos(String nome, TabelaDeSimbolos.TipoLaIdentificador tipoIdentificador, TabelaDeSimbolos.TipoLaVariavel tipoVariavel) {
            this.nome = nome;
            this.tipoVariavel = tipoVariavel;
            this.tipoIdentificaor = tipoIdentificador;
        }
    }
