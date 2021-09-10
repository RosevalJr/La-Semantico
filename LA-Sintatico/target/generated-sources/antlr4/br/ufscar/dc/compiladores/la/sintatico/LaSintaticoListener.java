// Generated from br/ufscar/dc/compiladores/la/sintatico/LaSintatico.g4 by ANTLR 4.7.2
package br.ufscar.dc.compiladores.la.sintatico;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LaSintaticoParser}.
 */
public interface LaSintaticoListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#programa}.
	 * @param ctx the parse tree
	 */
	void enterPrograma(LaSintaticoParser.ProgramaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#programa}.
	 * @param ctx the parse tree
	 */
	void exitPrograma(LaSintaticoParser.ProgramaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#declaracoes}.
	 * @param ctx the parse tree
	 */
	void enterDeclaracoes(LaSintaticoParser.DeclaracoesContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#declaracoes}.
	 * @param ctx the parse tree
	 */
	void exitDeclaracoes(LaSintaticoParser.DeclaracoesContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#decl_local_global}.
	 * @param ctx the parse tree
	 */
	void enterDecl_local_global(LaSintaticoParser.Decl_local_globalContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#decl_local_global}.
	 * @param ctx the parse tree
	 */
	void exitDecl_local_global(LaSintaticoParser.Decl_local_globalContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#declaracao_local}.
	 * @param ctx the parse tree
	 */
	void enterDeclaracao_local(LaSintaticoParser.Declaracao_localContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#declaracao_local}.
	 * @param ctx the parse tree
	 */
	void exitDeclaracao_local(LaSintaticoParser.Declaracao_localContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#variavel}.
	 * @param ctx the parse tree
	 */
	void enterVariavel(LaSintaticoParser.VariavelContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#variavel}.
	 * @param ctx the parse tree
	 */
	void exitVariavel(LaSintaticoParser.VariavelContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#identificador}.
	 * @param ctx the parse tree
	 */
	void enterIdentificador(LaSintaticoParser.IdentificadorContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#identificador}.
	 * @param ctx the parse tree
	 */
	void exitIdentificador(LaSintaticoParser.IdentificadorContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#dimensao}.
	 * @param ctx the parse tree
	 */
	void enterDimensao(LaSintaticoParser.DimensaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#dimensao}.
	 * @param ctx the parse tree
	 */
	void exitDimensao(LaSintaticoParser.DimensaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#tipo}.
	 * @param ctx the parse tree
	 */
	void enterTipo(LaSintaticoParser.TipoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#tipo}.
	 * @param ctx the parse tree
	 */
	void exitTipo(LaSintaticoParser.TipoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#tipo_basico}.
	 * @param ctx the parse tree
	 */
	void enterTipo_basico(LaSintaticoParser.Tipo_basicoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#tipo_basico}.
	 * @param ctx the parse tree
	 */
	void exitTipo_basico(LaSintaticoParser.Tipo_basicoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#tipo_basico_ident}.
	 * @param ctx the parse tree
	 */
	void enterTipo_basico_ident(LaSintaticoParser.Tipo_basico_identContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#tipo_basico_ident}.
	 * @param ctx the parse tree
	 */
	void exitTipo_basico_ident(LaSintaticoParser.Tipo_basico_identContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#tipo_estendido}.
	 * @param ctx the parse tree
	 */
	void enterTipo_estendido(LaSintaticoParser.Tipo_estendidoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#tipo_estendido}.
	 * @param ctx the parse tree
	 */
	void exitTipo_estendido(LaSintaticoParser.Tipo_estendidoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#valor_constante}.
	 * @param ctx the parse tree
	 */
	void enterValor_constante(LaSintaticoParser.Valor_constanteContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#valor_constante}.
	 * @param ctx the parse tree
	 */
	void exitValor_constante(LaSintaticoParser.Valor_constanteContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#registro}.
	 * @param ctx the parse tree
	 */
	void enterRegistro(LaSintaticoParser.RegistroContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#registro}.
	 * @param ctx the parse tree
	 */
	void exitRegistro(LaSintaticoParser.RegistroContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#declaracao_global}.
	 * @param ctx the parse tree
	 */
	void enterDeclaracao_global(LaSintaticoParser.Declaracao_globalContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#declaracao_global}.
	 * @param ctx the parse tree
	 */
	void exitDeclaracao_global(LaSintaticoParser.Declaracao_globalContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#parametro}.
	 * @param ctx the parse tree
	 */
	void enterParametro(LaSintaticoParser.ParametroContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#parametro}.
	 * @param ctx the parse tree
	 */
	void exitParametro(LaSintaticoParser.ParametroContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#parametros}.
	 * @param ctx the parse tree
	 */
	void enterParametros(LaSintaticoParser.ParametrosContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#parametros}.
	 * @param ctx the parse tree
	 */
	void exitParametros(LaSintaticoParser.ParametrosContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#corpo}.
	 * @param ctx the parse tree
	 */
	void enterCorpo(LaSintaticoParser.CorpoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#corpo}.
	 * @param ctx the parse tree
	 */
	void exitCorpo(LaSintaticoParser.CorpoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmd}.
	 * @param ctx the parse tree
	 */
	void enterCmd(LaSintaticoParser.CmdContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmd}.
	 * @param ctx the parse tree
	 */
	void exitCmd(LaSintaticoParser.CmdContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdLeia}.
	 * @param ctx the parse tree
	 */
	void enterCmdLeia(LaSintaticoParser.CmdLeiaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdLeia}.
	 * @param ctx the parse tree
	 */
	void exitCmdLeia(LaSintaticoParser.CmdLeiaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdEscreva}.
	 * @param ctx the parse tree
	 */
	void enterCmdEscreva(LaSintaticoParser.CmdEscrevaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdEscreva}.
	 * @param ctx the parse tree
	 */
	void exitCmdEscreva(LaSintaticoParser.CmdEscrevaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdSe}.
	 * @param ctx the parse tree
	 */
	void enterCmdSe(LaSintaticoParser.CmdSeContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdSe}.
	 * @param ctx the parse tree
	 */
	void exitCmdSe(LaSintaticoParser.CmdSeContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdCaso}.
	 * @param ctx the parse tree
	 */
	void enterCmdCaso(LaSintaticoParser.CmdCasoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdCaso}.
	 * @param ctx the parse tree
	 */
	void exitCmdCaso(LaSintaticoParser.CmdCasoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdPara}.
	 * @param ctx the parse tree
	 */
	void enterCmdPara(LaSintaticoParser.CmdParaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdPara}.
	 * @param ctx the parse tree
	 */
	void exitCmdPara(LaSintaticoParser.CmdParaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdEnquanto}.
	 * @param ctx the parse tree
	 */
	void enterCmdEnquanto(LaSintaticoParser.CmdEnquantoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdEnquanto}.
	 * @param ctx the parse tree
	 */
	void exitCmdEnquanto(LaSintaticoParser.CmdEnquantoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdFaca}.
	 * @param ctx the parse tree
	 */
	void enterCmdFaca(LaSintaticoParser.CmdFacaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdFaca}.
	 * @param ctx the parse tree
	 */
	void exitCmdFaca(LaSintaticoParser.CmdFacaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdAtribuicao}.
	 * @param ctx the parse tree
	 */
	void enterCmdAtribuicao(LaSintaticoParser.CmdAtribuicaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdAtribuicao}.
	 * @param ctx the parse tree
	 */
	void exitCmdAtribuicao(LaSintaticoParser.CmdAtribuicaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdChamada}.
	 * @param ctx the parse tree
	 */
	void enterCmdChamada(LaSintaticoParser.CmdChamadaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdChamada}.
	 * @param ctx the parse tree
	 */
	void exitCmdChamada(LaSintaticoParser.CmdChamadaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#cmdRetorne}.
	 * @param ctx the parse tree
	 */
	void enterCmdRetorne(LaSintaticoParser.CmdRetorneContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#cmdRetorne}.
	 * @param ctx the parse tree
	 */
	void exitCmdRetorne(LaSintaticoParser.CmdRetorneContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#selecao}.
	 * @param ctx the parse tree
	 */
	void enterSelecao(LaSintaticoParser.SelecaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#selecao}.
	 * @param ctx the parse tree
	 */
	void exitSelecao(LaSintaticoParser.SelecaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#item_selecao}.
	 * @param ctx the parse tree
	 */
	void enterItem_selecao(LaSintaticoParser.Item_selecaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#item_selecao}.
	 * @param ctx the parse tree
	 */
	void exitItem_selecao(LaSintaticoParser.Item_selecaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#constantes}.
	 * @param ctx the parse tree
	 */
	void enterConstantes(LaSintaticoParser.ConstantesContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#constantes}.
	 * @param ctx the parse tree
	 */
	void exitConstantes(LaSintaticoParser.ConstantesContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#numero_intervalo}.
	 * @param ctx the parse tree
	 */
	void enterNumero_intervalo(LaSintaticoParser.Numero_intervaloContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#numero_intervalo}.
	 * @param ctx the parse tree
	 */
	void exitNumero_intervalo(LaSintaticoParser.Numero_intervaloContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#op_unario}.
	 * @param ctx the parse tree
	 */
	void enterOp_unario(LaSintaticoParser.Op_unarioContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#op_unario}.
	 * @param ctx the parse tree
	 */
	void exitOp_unario(LaSintaticoParser.Op_unarioContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#exp_unario}.
	 * @param ctx the parse tree
	 */
	void enterExp_unario(LaSintaticoParser.Exp_unarioContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#exp_unario}.
	 * @param ctx the parse tree
	 */
	void exitExp_unario(LaSintaticoParser.Exp_unarioContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#exp_aritmetica}.
	 * @param ctx the parse tree
	 */
	void enterExp_aritmetica(LaSintaticoParser.Exp_aritmeticaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#exp_aritmetica}.
	 * @param ctx the parse tree
	 */
	void exitExp_aritmetica(LaSintaticoParser.Exp_aritmeticaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#termo}.
	 * @param ctx the parse tree
	 */
	void enterTermo(LaSintaticoParser.TermoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#termo}.
	 * @param ctx the parse tree
	 */
	void exitTermo(LaSintaticoParser.TermoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#fator}.
	 * @param ctx the parse tree
	 */
	void enterFator(LaSintaticoParser.FatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#fator}.
	 * @param ctx the parse tree
	 */
	void exitFator(LaSintaticoParser.FatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#op1}.
	 * @param ctx the parse tree
	 */
	void enterOp1(LaSintaticoParser.Op1Context ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#op1}.
	 * @param ctx the parse tree
	 */
	void exitOp1(LaSintaticoParser.Op1Context ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#op2}.
	 * @param ctx the parse tree
	 */
	void enterOp2(LaSintaticoParser.Op2Context ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#op2}.
	 * @param ctx the parse tree
	 */
	void exitOp2(LaSintaticoParser.Op2Context ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#op3}.
	 * @param ctx the parse tree
	 */
	void enterOp3(LaSintaticoParser.Op3Context ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#op3}.
	 * @param ctx the parse tree
	 */
	void exitOp3(LaSintaticoParser.Op3Context ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#parcela}.
	 * @param ctx the parse tree
	 */
	void enterParcela(LaSintaticoParser.ParcelaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#parcela}.
	 * @param ctx the parse tree
	 */
	void exitParcela(LaSintaticoParser.ParcelaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#parcela_unario}.
	 * @param ctx the parse tree
	 */
	void enterParcela_unario(LaSintaticoParser.Parcela_unarioContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#parcela_unario}.
	 * @param ctx the parse tree
	 */
	void exitParcela_unario(LaSintaticoParser.Parcela_unarioContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#parcela_nao_unario}.
	 * @param ctx the parse tree
	 */
	void enterParcela_nao_unario(LaSintaticoParser.Parcela_nao_unarioContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#parcela_nao_unario}.
	 * @param ctx the parse tree
	 */
	void exitParcela_nao_unario(LaSintaticoParser.Parcela_nao_unarioContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#exp_relacional}.
	 * @param ctx the parse tree
	 */
	void enterExp_relacional(LaSintaticoParser.Exp_relacionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#exp_relacional}.
	 * @param ctx the parse tree
	 */
	void exitExp_relacional(LaSintaticoParser.Exp_relacionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#op_relacional}.
	 * @param ctx the parse tree
	 */
	void enterOp_relacional(LaSintaticoParser.Op_relacionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#op_relacional}.
	 * @param ctx the parse tree
	 */
	void exitOp_relacional(LaSintaticoParser.Op_relacionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#expressao}.
	 * @param ctx the parse tree
	 */
	void enterExpressao(LaSintaticoParser.ExpressaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#expressao}.
	 * @param ctx the parse tree
	 */
	void exitExpressao(LaSintaticoParser.ExpressaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#termo_logico}.
	 * @param ctx the parse tree
	 */
	void enterTermo_logico(LaSintaticoParser.Termo_logicoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#termo_logico}.
	 * @param ctx the parse tree
	 */
	void exitTermo_logico(LaSintaticoParser.Termo_logicoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#fator_logico}.
	 * @param ctx the parse tree
	 */
	void enterFator_logico(LaSintaticoParser.Fator_logicoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#fator_logico}.
	 * @param ctx the parse tree
	 */
	void exitFator_logico(LaSintaticoParser.Fator_logicoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#parcela_logica}.
	 * @param ctx the parse tree
	 */
	void enterParcela_logica(LaSintaticoParser.Parcela_logicaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#parcela_logica}.
	 * @param ctx the parse tree
	 */
	void exitParcela_logica(LaSintaticoParser.Parcela_logicaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#op_logico_1}.
	 * @param ctx the parse tree
	 */
	void enterOp_logico_1(LaSintaticoParser.Op_logico_1Context ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#op_logico_1}.
	 * @param ctx the parse tree
	 */
	void exitOp_logico_1(LaSintaticoParser.Op_logico_1Context ctx);
	/**
	 * Enter a parse tree produced by {@link LaSintaticoParser#op_logico_2}.
	 * @param ctx the parse tree
	 */
	void enterOp_logico_2(LaSintaticoParser.Op_logico_2Context ctx);
	/**
	 * Exit a parse tree produced by {@link LaSintaticoParser#op_logico_2}.
	 * @param ctx the parse tree
	 */
	void exitOp_logico_2(LaSintaticoParser.Op_logico_2Context ctx);
}