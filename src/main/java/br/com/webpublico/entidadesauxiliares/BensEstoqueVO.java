package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;

import java.math.BigDecimal;
import java.util.Date;

public class BensEstoqueVO {

    private Date dataBem;
    private Exercicio exercicio;
    private TipoLancamento tipoLancamento;
    private GrupoMaterial grupoMaterial;
    private TipoEstoque tipoEstoque;
    private TipoOperacaoBensEstoque operacoesBensEstoque;
    private UnidadeOrganizacional unidadeOrganizacional;
    private TipoIngresso tipoIngresso;
    private TipoBaixaBens tipoBaixaBens;
    private String historico;
    private BigDecimal valor;

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataBem() {
        return dataBem;
    }

    public void setDataBem(Date dataBem) {
        this.dataBem = dataBem;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public TipoOperacaoBensEstoque getOperacoesBensEstoque() {
        return operacoesBensEstoque;
    }

    public void setOperacoesBensEstoque(TipoOperacaoBensEstoque operacoesBensEstoque) {
        this.operacoesBensEstoque = operacoesBensEstoque;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoIngresso getTipoIngresso() {
        return tipoIngresso;
    }

    public void setTipoIngresso(TipoIngresso tipoIngresso) {
        this.tipoIngresso = tipoIngresso;
    }

    public TipoBaixaBens getTipoBaixaBens() {
        return tipoBaixaBens;
    }

    public void setTipoBaixaBens(TipoBaixaBens tipoBaixaBens) {
        this.tipoBaixaBens = tipoBaixaBens;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
