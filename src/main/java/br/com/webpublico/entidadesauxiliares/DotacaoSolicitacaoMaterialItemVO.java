/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.enums.TipoObjetoCompra;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class DotacaoSolicitacaoMaterialItemVO implements Serializable {

    private TipoObjetoCompra tipoObjetoCompra;
    private SubTipoObjetoCompra subTipoObjetoCompra;
    private BigDecimal valor;
    private Long criadoEm;
    private List<AgrupadorReservaSolicitacaoCompraVO> agrupadorFontes;

    public DotacaoSolicitacaoMaterialItemVO() {
        valor = BigDecimal.ZERO;
        agrupadorFontes = Lists.newArrayList();
        criadoEm = System.nanoTime();
    }

    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        return subTipoObjetoCompra;
    }

    public void setSubTipoObjetoCompra(SubTipoObjetoCompra subTipoObjetoCompra) {
        this.subTipoObjetoCompra = subTipoObjetoCompra;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public List<AgrupadorReservaSolicitacaoCompraVO> getAgrupadorFontes() {
        return agrupadorFontes;
    }

    public void setAgrupadorFontes(List<AgrupadorReservaSolicitacaoCompraVO> agrupadorFontes) {
        this.agrupadorFontes = agrupadorFontes;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public BigDecimal getValorProcessoOriginal() {
        return getValor().subtract(getValorProcessoOutro());
    }

    public BigDecimal getValorReservadoOriginal() {
        BigDecimal total = BigDecimal.ZERO;
        if (this.agrupadorFontes != null) {
            for (AgrupadorReservaSolicitacaoCompraVO agrup : agrupadorFontes) {
                total = total.add(agrup.getValorReservadoOriginal());
            }
        }
        return total;
    }

    public BigDecimal getValorAReservarOriginal() {
        return getValorProcessoOriginal().subtract(getValorReservadoOriginal());
    }

    public BigDecimal getValorProcessoOutro() {
        BigDecimal total = BigDecimal.ZERO;
        if (this.agrupadorFontes != null) {
            for (AgrupadorReservaSolicitacaoCompraVO agrup : agrupadorFontes) {
                total = total.add(agrup.getValorReservadoOutro());
            }
        }
        return total;
    }

    public BigDecimal getValorOutroAReservar() {
        return getValorProcessoOutro().subtract(getValorProcessoOutro());
    }

    public boolean isReservadoTotalmente() {
        return getValorAReservarOriginal().compareTo(BigDecimal.ZERO) == 0;
    }

    public String toStringTipoObjetoCompra() {
        try {
            return tipoObjetoCompra.getDescricao() + " (" + subTipoObjetoCompra.getDescricao() + ")";
        } catch (NullPointerException ex) {
            return tipoObjetoCompra.getDescricao();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DotacaoSolicitacaoMaterialItemVO that = (DotacaoSolicitacaoMaterialItemVO) object;
        return tipoObjetoCompra == that.tipoObjetoCompra;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoObjetoCompra);
    }
}
