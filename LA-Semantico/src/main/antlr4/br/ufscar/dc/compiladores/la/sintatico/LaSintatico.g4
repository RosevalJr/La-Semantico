/*
Nesse arquivo, definimos a gramática da Linguagem Algoritmica (LA) criada pelo 
professor Jander Moreira, DC - UFSCar.
*/

grammar LaSintatico;

/* 
Aqui sao definidas as palavras chaves da LA, elas sao posicionadas no ínicio para
nao termos problemas com a abordagem gulosa do analisador. Essas palavras sao
reservadas para uma tarefa especifica dentro da linguagem.
*/ 
PALAVRA_CHAVE: 'algoritmo' | 'declare' | 'literal' | 'inteiro' | 'fim_algoritmo' |
 'leia' | 'escreva' | 'real' | 'e' | 'ou' | 'nao' | 'logico' | 'se' | 'senao' |
 'fim_se' | 'entao' | 'caso' | 'seja' | '..' | 'fim_caso' | 'para' | 'faca' |
 'ate' | 'fim_para' | 'enquanto' | 'fim_enquanto' | 'registro' | 'fim_registro' |
 'tipo' | 'procedimento' | 'var' | 'fim_procedimento' | 'retorne' | 'fim_funcao' |
 'funcao' | 'constante' |'falso' | 'verdadeiro';

// Definicao dos numeros, o sinal eh analisado separadamente
NUM_INT: ('0'..'9')+;

NUM_REAL: ('0'..'9')+ '.' ('0'..'9')+;

// Nome das variaveis
IDENT: ('a'..'z' | 'A'..'Z')('a'..'z' | 'A'..'Z'|'0'..'9' | '_')*;

/* Cadeia de caracteres, eh tudo aquilo entre dentre aspas, exceto por quebra 
de linha, \r e a própria aspas */
CADEIA: '"' (~('\n' | '\r' | '"'))* '"';


// Comentario eh tudo aquilo dentre chaves, com exceçoes analogas as da CADEIA.
// Todo COMENTARIO sera ignorado (função do -> skip)
COMENTARIO: '{' (~('}' | '\n' | '\r'))* '}' -> skip;

// Ignorando whitespaces (espaco, quebra de linha, tab, \r)
WS: (' ' | '\t' | '\r' | '\n' ) -> skip;

// Operacoes aritmeticas (soma, subtracao, multiplicacao, divisao e resto de div.)
OP_ARIT: '+' | '-' | '*' | '/' | '%';

// Operacoes relacionais (maior, maior igual, menor, menor igual, diferente, igual)
OP_REL: '>' | '>=' | '<' | '<=' | '<>' | '=';


OP_PON: '^' | '&' | '.' | '[' | ']';

// O delimitador (:) eh utilizado para definir o tipo das variaveis
DELIM: ':';

// Usado para atribuir os valores das variaveis
ATRIB: '<-';

// Virgula
VIRGU: ',';

// Abre arenteses
ABREPAR: '(';

// Fecha parenteses
FECHAPAR: ')';

/* Regras sintaticas */

// Estrutura principal do programa
programa: declaracoes 'algoritmo' corpo 'fim_algoritmo';

// Multiplas de declaracoes
declaracoes: (decl_local_global)*;

// Declaracoes globais e locais
decl_local_global: declaracao_local | declaracao_global;

// Diferentes tipos de declarações locais
declaracao_local: 'declare' variavel |
                  'constante' IDENT ':' tipo_basico '=' valor_constante |
                  'tipo' IDENT ':' tipo;

// Declaracao de variaveis
variavel: identificador (',' identificador)* ':' tipo;

// Identificadores concatenados com ponto e que possuem dimensao
identificador: IDENT ('.' IDENT)* dimensao;

// Dimencao que corresponde a vetores de mais de uma dimensao
dimensao: ('[' exp_aritmetica ']')*;

// Definicao dos tipos (registro ou tipo estendido)
tipo: registro | tipo_estendido;

// Definicao dos tipos basicos (literal, inteiro, real e logico)
tipo_basico: 'literal' | 'inteiro' | 'real' | 'logico';

// Tipo basico ou identificador
tipo_basico_ident: tipo_basico | IDENT;

// Tipo estendido
tipo_estendido: ('^')? tipo_basico_ident;

// Definicao dos valores que podem ser constantes
valor_constante: CADEIA | NUM_INT | NUM_REAL | 'verdadeiro' | 'falso';

// Definicao de registro
registro: 'registro' (variavel)* 'fim_registro';

// Definicao de estrutura de declaracao global
declaracao_global: 'procedimento' IDENT '(' (parametros)? ')' (declaracao_local)* (cmd)* 'fim_procedimento' |
                   'funcao' IDENT '(' (parametros)? ')' ':' tipo_estendido (declaracao_local)* (cmd)* 'fim_funcao';

// Definição da estrutura de um parametro de procedimento ou funcao
parametro: ('var')? identificador (',' identificador)* ':' tipo_estendido;

// Definicao de multiplos parametros em uma funcao
parametros: parametro (',' parametro)*;

// Definicao da estrutura do corpo do programa (conjunto de declaracoes locais e
// de comandos)
corpo: (declaracao_local)* (cmd)*;

// Diferentes tipos de comandos
cmd: cmdLeia | cmdEscreva | cmdSe | cmdCaso | cmdPara | cmdEnquanto |
     cmdFaca | cmdAtribuicao | cmdChamada | cmdRetorne;

// Definicao da estrutura do comando 'leia'
cmdLeia: 'leia' '(' ('^')? identificador (',' ('^')? identificador)* ')';

// Definicao da estrutura do comando 'escreva'
cmdEscreva: 'escreva' '(' expressao (',' expressao)* ')';

// Definicao da estrutura do comando 'se... entao'
cmdSe: 'se'  expressao 'entao' (cmdIf+=cmd)* ('senao' (cmdElse+=cmd)*)? 'fim_se';

// Definicao da estrutura do comando 'caso'
cmdCaso: 'caso' exp_aritmetica 'seja' selecao ('senao' (cmd)*)? 'fim_caso';

// Definicao da estrutura do comando 'para'
cmdPara: 'para' IDENT '<-' exp_aritmetica 'ate' exp_aritmetica 'faca' (cmd)* 'fim_para';

// Definicao da estrutura do comando 'enquanto'
cmdEnquanto: 'enquanto' expressao 'faca' (cmd)* 'fim_enquanto';

// Definicao da estrutura do comando 'faca'
cmdFaca: 'faca' (cmd)* 'ate' expressao;

// Definicao da estrutura de atribuicao
cmdAtribuicao: ('^')? identificador '<-' expressao;

// Definicao da chamada de funcao ou procedimento
cmdChamada: IDENT '(' expressao (',' expressao)* ')';

// Definicao da estrutura do comando de retorno de funcao ou procedimento
cmdRetorne: 'retorne' expressao;

// Definicao de estrutura de selecao
selecao: (item_selecao)*;

// Definicao de estrutura do item da selecao
item_selecao: constantes ':' (cmd)*;

// Definicao de constantes
constantes: numero_intervalo (',' numero_intervalo)*;

// Definicao da estrutura de um intervalo numerico
numero_intervalo: (op_unario)? NUM_INT ('..' (op_unario)? NUM_INT)?;

//Definicao de operador unario
op_unario: '-';

// Definicao de expressao aritmetica de soma
exp_aritmetica: termo (op1 termo)*;

// Definicao de termo por fatores de multiplicao
termo: fator (op2 fator)*;

// Definicao de fator por operacao de resto de divisao em parcelas
fator: parcela (op3 parcela)*;

// Operadores de adicao e subtracaoFatorContext ctx
op1: '+' | '-';

// Operadores de multiplicacao e divisao
op2: '*' | '/';

// Operador de resto de divisao inteira
op3: '%';

// Definicao de parcelas unarias e nao-unarias
parcela: (op_unario)? parcela_unario | parcela_nao_unario;

//Definicao de diferentes parcelas com operador unario
parcela_unario: ('^')? identificador | IDENT '(' expressao (',' expressao)* ')'|
                NUM_INT | NUM_REAL | '(' expressao ')';

// Definicao de parcela sem nao unaria
parcela_nao_unario: '&' identificador | CADEIA;

// Expressao relacional
exp_relacional: exp_aritmetica (op_relacional exp_aritmetica)?;

// Operadores relacionais = <> >= <= > e <
op_relacional: '=' | '<>' | '>=' | '<=' | '>' | '<';

// Expressao logica correspondente a dois termos logicos unidos por um 'ou'
expressao: termo_logico (op_logico_1 termo_logico)*;

// Termo logico correspondente a dois fatores lógicos unidos por um 'e'
termo_logico: fator_logico (op_logico_2 fator_logico)*;

// Fator logico que corresponde a uma parcela logica seguida opcionalmente de um 'nao'
fator_logico: ('nao')? parcela_logica;

// Parcela lógica que corresponde a valores verdade ou expressão relacional
parcela_logica: ('verdadeiro' | 'falso') | exp_relacional;

// Operador logico 'ou'
op_logico_1: 'ou';

// Operador logico 'e'
op_logico_2: 'e';
