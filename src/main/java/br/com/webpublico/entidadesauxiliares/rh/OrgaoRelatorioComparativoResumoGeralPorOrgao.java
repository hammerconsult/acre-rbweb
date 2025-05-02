package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Tharlyson on 03/04/19.
 */
public class OrgaoRelatorioComparativoResumoGeralPorOrgao {

    private String codigo2nivel;
    private String descricao;
    private String codigoOrgao;
    private BigDecimal totalBruto;
    private BigDecimal totalDesconto;

    private List<EventoRelatorioComparativoResumoGeralPorOrgao> eventos;

    public OrgaoRelatorioComparativoResumoGeralPorOrgao() { }

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

    public List<EventoRelatorioComparativoResumoGeralPorOrgao> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoRelatorioComparativoResumoGeralPorOrgao> eventos) {
        this.eventos = eventos;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }
}
