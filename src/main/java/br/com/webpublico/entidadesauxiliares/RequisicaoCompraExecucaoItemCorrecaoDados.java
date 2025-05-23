package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class RequisicaoCompraExecucaoItemCorrecaoDados {

    Long idItemRequisicao;
    Long idItemExecucao;
    BigDecimal quantidade;
    BigDecimal valorUnitario;
    BigDecimal valorTotal;
    Long idGrupo;
    Long idEmpenho;
    String numeroEmpenho;
    String codigoGrupo;
    String codigoConta;
    CorrecaoDadosAdministrativoVO.TipoAlteracaoDados tipoAlteracaoDados;

    public Long getIdItemRequisicao() {
        return idItemRequisicao;
    }

    public void setIdItemRequisicao(Long idItemRequisicao) {
        this.idItemRequisicao = idItemRequisicao;
    }

    public Long getIdItemExecucao() {
        return idItemExecucao;
    }

    public void setIdItemExecucao(Long idItemExecucao) {
        this.idItemExecucao = idItemExecucao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Long getIdEmpenho() {
        return idEmpenho;
    }

    public void setIdEmpenho(Long idEmpenho) {
        this.idEmpenho = idEmpenho;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public String getCodigoConta() {
        return codigoConta;
    }

    public void setCodigoConta(String codigoConta) {
        this.codigoConta = codigoConta;
    }

    public CorrecaoDadosAdministrativoVO.TipoAlteracaoDados getTipoAlteracaoDados() {
        return tipoAlteracaoDados;
    }

    public void setTipoAlteracaoDados(CorrecaoDadosAdministrativoVO.TipoAlteracaoDados tipoAlteracaoDados) {
        this.tipoAlteracaoDados = tipoAlteracaoDados;
    }
}
