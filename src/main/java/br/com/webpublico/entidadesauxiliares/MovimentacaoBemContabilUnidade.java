package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class MovimentacaoBemContabilUnidade {

    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private Boolean expandirGrupo;
    private BigDecimal valorOriginalContabil;
    private BigDecimal valorAjusteContabil;
    private BigDecimal valorOriginalAdm;
    private BigDecimal valorAjusteAdm;
    private BigDecimal valorOriginalBalancete;
    private BigDecimal valorAjusteBalancete;
    private List<MovimentacaoBemContabilGrupo> grupos;

    public MovimentacaoBemContabilUnidade() {
        grupos = Lists.newArrayList();
        valorOriginalContabil = BigDecimal.ZERO;
        valorAjusteContabil = BigDecimal.ZERO;
        valorOriginalAdm = BigDecimal.ZERO;
        valorAjusteAdm = BigDecimal.ZERO;
        valorOriginalBalancete = BigDecimal.ZERO;
        valorAjusteBalancete = BigDecimal.ZERO;
        expandirGrupo = false;
    }

    public List<MovimentacaoBemContabilGrupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<MovimentacaoBemContabilGrupo> grupos) {
        this.grupos = grupos;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public BigDecimal getValorOriginalContabil() {
        return valorOriginalContabil;
    }

    public void setValorOriginalContabil(BigDecimal valorOriginalContabil) {
        this.valorOriginalContabil = valorOriginalContabil;
    }

    public BigDecimal getValorAjusteContabil() {
        return valorAjusteContabil;
    }

    public void setValorAjusteContabil(BigDecimal valorAjusteContabil) {
        this.valorAjusteContabil = valorAjusteContabil;
    }

    public BigDecimal getValorAtualContabil() {
        return getValorOriginalContabil().subtract(getValorAjusteContabil());
    }


    public BigDecimal getValorOriginalAdm() {
        return valorOriginalAdm;
    }

    public void setValorOriginalAdm(BigDecimal valorOriginalAdm) {
        this.valorOriginalAdm = valorOriginalAdm;
    }

    public BigDecimal getValorAjusteAdm() {
        return valorAjusteAdm;
    }

    public void setValorAjusteAdm(BigDecimal valorAjusteAdm) {
        this.valorAjusteAdm = valorAjusteAdm;
    }

    public BigDecimal getValorAtualAdm() {
        return getValorOriginalAdm().subtract(getValorAjusteAdm());
    }

    public BigDecimal getValorOriginalConciliacao() {
        return getValorOriginalContabil().subtract(getValorOriginalAdm());
    }

    public BigDecimal getValorAjusteConciliacao() {
        return getValorAjusteContabil().subtract(getValorAjusteAdm());
    }

    public BigDecimal getValorAtualConciliacao() {
        return getValorAtualContabil().subtract(getValorAtualAdm());
    }

    public boolean isUnidadeConciliada() {
        return getValorAtualConciliacao().compareTo(BigDecimal.ZERO) == 0;
    }

    public Boolean getExpandirGrupo() {
        return expandirGrupo;
    }

    public void setExpandirGrupo(Boolean expandirGrupo) {
        this.expandirGrupo = expandirGrupo;
    }

    public BigDecimal getValorOriginalBalancete() {
        return valorOriginalBalancete;
    }

    public void setValorOriginalBalancete(BigDecimal valorOriginalBalancete) {
        this.valorOriginalBalancete = valorOriginalBalancete;
    }

    public BigDecimal getValorAjusteBalancete() {
        return valorAjusteBalancete;
    }

    public void setValorAjusteBalancete(BigDecimal valorAjusteBalancete) {
        this.valorAjusteBalancete = valorAjusteBalancete;
    }

    public BigDecimal getValorAtualBalancete() {
        return getValorOriginalBalancete().subtract(getValorAjusteBalancete());
    }

    public String getStyleRowConciliado() {
        if (isUnidadeConciliada()) {
            return "verdeescuro";
        }
        return "vermelhoescuro";
    }

    public boolean hasGrupos() {
        return !Util.isListNullOrEmpty(grupos);
    }
}
