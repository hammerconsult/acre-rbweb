/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.util.Util;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AgrupadorReservaSolicitacaoCompraVO implements Serializable, Comparable<AgrupadorReservaSolicitacaoCompraVO> {

    private Exercicio exercicio;
    private TipoObjetoCompra tipoObjetoCompra;
    private List<DotacaoSolicitacaoMaterialItemFonteVO> dotacoesFonte;

    public AgrupadorReservaSolicitacaoCompraVO(TipoObjetoCompra tipoObjetoCompra, Exercicio exercicio) {
        this.tipoObjetoCompra = tipoObjetoCompra;
        this.exercicio = exercicio;
        this.dotacoesFonte = Lists.newArrayList();
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<DotacaoSolicitacaoMaterialItemFonteVO> getDotacoesFonte() {
        return dotacoesFonte;
    }

    public void setDotacoesFonte(List<DotacaoSolicitacaoMaterialItemFonteVO> dotacoesFonte) {
        this.dotacoesFonte = dotacoesFonte;
    }

    public BigDecimal getValorEstornoReservado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasDotacaoesFonte()) {
            for (DotacaoSolicitacaoMaterialItemFonteVO dot : dotacoesFonte) {
                valor = valor.add(dot.getValorEstornoReservado());
            }
        }
        return valor;
    }

    public BigDecimal getValorExecutado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasDotacaoesFonte()) {
            for (DotacaoSolicitacaoMaterialItemFonteVO dot : dotacoesFonte) {
                valor = valor.add(dot.getValorExecutado().subtract(dot.getValorEstornoExecutado()));
            }
        }
        return valor;
    }

    public BigDecimal getValorReservado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasDotacaoesFonte()) {
            for (DotacaoSolicitacaoMaterialItemFonteVO dot : dotacoesFonte) {
                if (dot.getTipoOperacao().isNormal()) {
                    valor = valor.add(dot.getValorReservado());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorReservadoOutro() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasDotacaoesFonte()) {
            for (DotacaoSolicitacaoMaterialItemFonteVO dot : dotacoesFonte) {
                if (dot.getTipoReserva().isExecucaoContrato() || dot.getTipoReserva().isExecucaoProcesso()) {
                    valor = dot.getTipoOperacao().isNormal() ? valor.add(dot.getValorReservado()) : valor.subtract(dot.getValorReservado());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorReservadoOriginal() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasDotacaoesFonte()) {
            for (DotacaoSolicitacaoMaterialItemFonteVO dot : dotacoesFonte) {
                if (dot.getTipoReserva().isSolicitacaoCompra()) {
                    valor = dot.getTipoOperacao().isNormal() ? valor.add(dot.getValorReservado()) : valor.subtract(dot.getValorReservado());
                }
            }
        }
        return valor;
    }

    public BigDecimal getSaldoDoExercicio() {
        return getValorReservado().subtract(getValorEstornoReservado()).subtract(getValorExecutado());
    }

    public boolean hasDotacaoesFonte() {
        return !Util.isListNullOrEmpty(dotacoesFonte);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgrupadorReservaSolicitacaoCompraVO that = (AgrupadorReservaSolicitacaoCompraVO) o;
        return Objects.equals(exercicio, that.exercicio) && tipoObjetoCompra == that.tipoObjetoCompra;
    }

    @Override
    public int hashCode() {
        return Objects.hash(exercicio, tipoObjetoCompra);
    }

    @Override
    public int compareTo(AgrupadorReservaSolicitacaoCompraVO dotacao) {
        if (dotacao.getExercicio() != null && getExercicio() != null) {
            return ComparisonChain.start().compare(getExercicio(), dotacao.getExercicio()).result();
        }
        return 0;
    }
}
