package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public enum OperacaoMovimentoAlteracaoContratual implements EnumComDescricao {

    REDIMENSIONAMENTO_OBJETO("Redimensionamento do Objeto", "Alteração legal na quantidade adquirida (ex: o limite de 25%)", 2),
    REAJUSTE_POS_EXECUCAO("Reajuste - Pós Execução", "Reajuste legal através de índice, geralmente a correção da inflação pelo IPCA/IGPM já predefino no ata/contrato", 3),
    REAJUSTE_PRE_EXECUCAO("Reajuste - Pré Execução", "Reajuste legal através de índice, geralmente a correção da inflação pelo IPCA/IGPM já predefino no ata/contrato", 3),
    REEQUILIBRIO_POS_EXECUCAO("Reequilíbrio - Pós Execução", "Ajuste segundo um evento que não é a inflação", 3),
    REEQUILIBRIO_PRE_EXECUCAO("Reequilíbrio - Pré Execução", "Ajuste segundo um evento que não é a inflação", 3),
    DILACAO_PRAZO("Dilação do Prazo", "Prorrogação de um prazo processual", 1),
    REDUCAO_PRAZO("Redução do Prazo", "Redução de um prazo processual", 1),
    TRANSFERENCIA_FORNECEDOR("Transferência de Fornecedor", "Transferência de fornecedor e responsável pelo fornecedor do contrato", 1),
    TRANSFERENCIA_DOTACAO("Transferência de Dotação Reservada", "Transferência de dotação para processos que não são de registro de preço", 1),
    TRANSFERENCIA_UNIDADE("Transferência de Unidade", "Transferência de unidade do contrato", 1),
    OUTRO("Outro", "", 2);
    private String descricao;
    private Integer ordem;
    private String textoInformativo;

    OperacaoMovimentoAlteracaoContratual(String descricao, String textoInformativo, Integer ordem) {
        this.descricao = descricao;
        this.textoInformativo = textoInformativo;
        this.ordem = ordem;
    }

    public String getTextoInformativo() {
        return textoInformativo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public boolean isRedimensionamentoObjeto() {
        return REDIMENSIONAMENTO_OBJETO.equals(this);
    }

    public boolean isReajustePosExecucao() {
        return REAJUSTE_POS_EXECUCAO.equals(this);
    }

    public boolean isReajustePreExecucao() {
        return REAJUSTE_PRE_EXECUCAO.equals(this);
    }

    public boolean isReequilibrioPosExecucao() {
        return REEQUILIBRIO_POS_EXECUCAO.equals(this);
    }

    public boolean isReequilibrioPreExecucao() {
        return REEQUILIBRIO_PRE_EXECUCAO.equals(this);
    }

    public boolean isDilacaoPrazo() {
        return DILACAO_PRAZO.equals(this);
    }

    public boolean isReducaoPrazo() {
        return REDUCAO_PRAZO.equals(this);
    }

    public boolean isTransferenciaFornecedor() {
        return TRANSFERENCIA_FORNECEDOR.equals(this);
    }

    public boolean isTransferenciaDotacao() {
        return TRANSFERENCIA_DOTACAO.equals(this);
    }

    public boolean isTransferenciaUnidade() {
        return TRANSFERENCIA_UNIDADE.equals(this);
    }

    public boolean isOutro() {
        return OUTRO.equals(this);
    }

    public static List<OperacaoMovimentoAlteracaoContratual> getOperacoesPosExecucao(){
        List<OperacaoMovimentoAlteracaoContratual> list = Lists.newArrayList();
        list.add(OperacaoMovimentoAlteracaoContratual.REAJUSTE_POS_EXECUCAO);
        list.add(OperacaoMovimentoAlteracaoContratual.REEQUILIBRIO_POS_EXECUCAO);
        return list;
    }

    public static List<OperacaoMovimentoAlteracaoContratual> getOperacoesPreExecucao(){
        List<OperacaoMovimentoAlteracaoContratual> list = Lists.newArrayList();
        list.add(OperacaoMovimentoAlteracaoContratual.REDIMENSIONAMENTO_OBJETO);
        list.add(OperacaoMovimentoAlteracaoContratual.OUTRO);
        list.add(OperacaoMovimentoAlteracaoContratual.REAJUSTE_PRE_EXECUCAO);
        list.add(OperacaoMovimentoAlteracaoContratual.REEQUILIBRIO_PRE_EXECUCAO);
        return list;
    }

    public static List<OperacaoMovimentoAlteracaoContratual> getOperacoesValor() {
        List<OperacaoMovimentoAlteracaoContratual> list = Lists.newArrayList();
        list.add(OperacaoMovimentoAlteracaoContratual.REDIMENSIONAMENTO_OBJETO);
        list.add(OperacaoMovimentoAlteracaoContratual.REAJUSTE_POS_EXECUCAO);
        list.add(OperacaoMovimentoAlteracaoContratual.REAJUSTE_PRE_EXECUCAO);
        list.add(OperacaoMovimentoAlteracaoContratual.REEQUILIBRIO_POS_EXECUCAO);
        list.add(OperacaoMovimentoAlteracaoContratual.REEQUILIBRIO_PRE_EXECUCAO);
        list.add(OperacaoMovimentoAlteracaoContratual.OUTRO);
        return list;
    }

    public static List<OperacaoMovimentoAlteracaoContratual> getOperacoesPrazo() {
        List<OperacaoMovimentoAlteracaoContratual> list = Lists.newArrayList();
        list.add(OperacaoMovimentoAlteracaoContratual.DILACAO_PRAZO);
        list.add(OperacaoMovimentoAlteracaoContratual.REDUCAO_PRAZO);
        return list;
    }

    public static List<OperacaoMovimentoAlteracaoContratual> getOperacoesTransferencia() {
        List<OperacaoMovimentoAlteracaoContratual> list = Lists.newArrayList();
        list.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_FORNECEDOR);
        list.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_DOTACAO);
        list.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_UNIDADE);
        return list;
    }

    public static List<OperacaoMovimentoAlteracaoContratual> getOperacoesApostilamento(TipoCadastroAlteracaoContratual tipoCad) {
        List<OperacaoMovimentoAlteracaoContratual> list = Lists.newArrayList();
        list.add(OperacaoMovimentoAlteracaoContratual.REAJUSTE_PRE_EXECUCAO);
        if (tipoCad.isContrato()) {
            list.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_DOTACAO);
            list.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_UNIDADE);
            list.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_FORNECEDOR);
        }
        list.add(OperacaoMovimentoAlteracaoContratual.OUTRO);
        return list;
    }


    public static List<OperacaoMovimentoAlteracaoContratual> getOperacoesAditivo(TipoCadastroAlteracaoContratual tipoCad) {
        List<OperacaoMovimentoAlteracaoContratual> list = Lists.newArrayList();
        if (tipoCad.isContrato()){
            Collections.addAll(list, OperacaoMovimentoAlteracaoContratual.values());
            return list;
        }
        list.add(OperacaoMovimentoAlteracaoContratual.REDIMENSIONAMENTO_OBJETO);
        list.add(OperacaoMovimentoAlteracaoContratual.REEQUILIBRIO_PRE_EXECUCAO);
        list.add(OperacaoMovimentoAlteracaoContratual.REAJUSTE_PRE_EXECUCAO);
        list.add(OperacaoMovimentoAlteracaoContratual.OUTRO);
        list.add(OperacaoMovimentoAlteracaoContratual.DILACAO_PRAZO);
        return list;
    }

    public static List<OperacaoMovimentoAlteracaoContratual> getOperacoesContratoAprovado(TipoAlteracaoContratual tipoAlteracao) {
        List<OperacaoMovimentoAlteracaoContratual> list = Lists.newArrayList();
        list.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_DOTACAO);
        list.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_UNIDADE);
        list.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_FORNECEDOR);

        if (tipoAlteracao.isApostilamento()){
            list.add(OperacaoMovimentoAlteracaoContratual.OUTRO);
        }
        return list;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
