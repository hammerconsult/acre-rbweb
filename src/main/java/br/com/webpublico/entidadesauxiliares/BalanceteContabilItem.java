package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 03/04/14
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class BalanceteContabilItem {
    private Long id;
    private String descricao;
    private String codigo;
    private BigDecimal saldoAnterior;
    private BigDecimal movimentoCredito;
    private BigDecimal movimentoDebito;
    private BigDecimal saldoFinal;
    private BigDecimal nivel;
    private String unidadeGestora;
    private String orgao;
    private String unidade;
    private Long unidadeGestoraId;
    private Long orgaoId;
    private Long unidadeId;
    private List<BalanceteContabilItem> contasAuxiliares;

    public BalanceteContabilItem() {
        contasAuxiliares = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getMovimentoCredito() {
        return movimentoCredito;
    }

    public void setMovimentoCredito(BigDecimal movimentoCredito) {
        this.movimentoCredito = movimentoCredito;
    }

    public BigDecimal getMovimentoDebito() {
        return movimentoDebito;
    }

    public void setMovimentoDebito(BigDecimal movimentoDebito) {
        this.movimentoDebito = movimentoDebito;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public BigDecimal getNivel() {
        return nivel;
    }

    public void setNivel(BigDecimal nivel) {
        this.nivel = nivel;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Long getUnidadeGestoraId() {
        return unidadeGestoraId;
    }

    public void setUnidadeGestoraId(Long unidadeGestoraId) {
        this.unidadeGestoraId = unidadeGestoraId;
    }

    public Long getOrgaoId() {
        return orgaoId;
    }

    public void setOrgaoId(Long orgaoId) {
        this.orgaoId = orgaoId;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public List<BalanceteContabilItem> getContasAuxiliares() {
        return contasAuxiliares;
    }

    public void setContasAuxiliares(List<BalanceteContabilItem> contasAuxiliares) {
        this.contasAuxiliares = contasAuxiliares;
    }
}
