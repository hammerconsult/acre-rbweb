package br.com.webpublico.entidadesauxiliares.administrativo.relatorio.demonstrativocompras;

import br.com.webpublico.enums.TipoHierarquiaOrganizacional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 10/02/2017.
 */
public class LevantamentoGrupoMaterial implements Serializable {

    private String codigoOrgao;
    private String descricaoOrgao;
    private String codigoUnidade;
    private String descricaoUnidade;
    private String codigoGrupoMaterial;
    private String grupoMaterial;
    private BigDecimal quantidade;
    private BigDecimal valorTotal;
    private BigDecimal valorUnitario;
    private BigDecimal valorAjuste;

    public LevantamentoGrupoMaterial() {

    }

    public static List<LevantamentoGrupoMaterial> preencherDados(List<Object[]> objetos, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        List<LevantamentoGrupoMaterial> toReturn = new ArrayList<>();
        for (Object[] obj : objetos) {
            LevantamentoGrupoMaterial levantamento = new LevantamentoGrupoMaterial();
            if (TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(tipoHierarquiaOrganizacional)) {
                levantamento.setCodigoOrgao((String) obj[0]);
                levantamento.setDescricaoOrgao((String) obj[1]);
                levantamento.setCodigoUnidade((String) obj[2]);
                levantamento.setDescricaoUnidade((String) obj[3]);
            } else {
                levantamento.setCodigoOrgao((String) obj[4]);
                levantamento.setDescricaoOrgao((String) obj[5]);
                levantamento.setCodigoUnidade((String) obj[6]);
                levantamento.setDescricaoUnidade((String) obj[7]);
            }
            levantamento.setCodigoGrupoMaterial((String) obj[8]);
            levantamento.setGrupoMaterial((String) obj[9]);
            levantamento.setQuantidade((BigDecimal) obj[10]);
            levantamento.setValorTotal((BigDecimal) obj[11]);
            levantamento.setValorUnitario((BigDecimal) obj[12]);
            levantamento.setValorAjuste((BigDecimal) obj[13]);
            toReturn.add(levantamento);
        }
        return toReturn;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getCodigoGrupoMaterial() {
        return codigoGrupoMaterial;
    }

    public void setCodigoGrupoMaterial(String codigoGrupoMaterial) {
        this.codigoGrupoMaterial = codigoGrupoMaterial;
    }

    public String getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(String grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }
}
