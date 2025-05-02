package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Buzatto on 29/10/2015.
 */
public class OrgaoRelatorioTotalVerbasPorOrgao {

    private String codigo2nivel;
    private String descricao;
    private BigDecimal totalBruto;
    private BigDecimal totalDesconto;
    private String codigoOrgao;

    private List<EventoRelatorioTotalVerbasPorOrgao> eventos;

    public OrgaoRelatorioTotalVerbasPorOrgao() {
        this.totalBruto = BigDecimal.ZERO;
        this.totalDesconto = BigDecimal.ZERO;
        this.eventos = Lists.newArrayList();
    }

    public String getCodigo2nivel() {
        return codigo2nivel;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(BigDecimal totalBruto) {
        this.totalBruto = totalBruto;
    }

    public BigDecimal getTotalDesconto() {
        return totalDesconto;
    }

    public void setTotalDesconto(BigDecimal totalDesconto) {
        this.totalDesconto = totalDesconto;
    }

    public void setCodigo2nivel(String codigo2nivel) {
        this.codigo2nivel = codigo2nivel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<EventoRelatorioTotalVerbasPorOrgao> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoRelatorioTotalVerbasPorOrgao> eventos) {
        this.eventos = eventos;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!(obj instanceof OrgaoRelatorioTotalVerbasPorOrgao)) {
            return false;
        }
        OrgaoRelatorioTotalVerbasPorOrgao relatorio = (OrgaoRelatorioTotalVerbasPorOrgao) obj;
        return Objects.equal(getCodigoOrgao(), relatorio.getCodigoOrgao());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCodigoOrgao());
    }
}
