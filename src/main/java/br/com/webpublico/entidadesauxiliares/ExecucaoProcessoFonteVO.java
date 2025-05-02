package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DespesaORC;
import br.com.webpublico.entidades.ExecucaoProcessoItem;
import br.com.webpublico.entidades.ExecucaoProcessoReserva;
import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ExecucaoProcessoFonteVO implements Serializable {

    private ExecucaoProcessoReserva execucaoProcessoReserva;
    private FonteDespesaORC fonteDespesaORC;
    private DespesaORC despesaORC;
    private BigDecimal valor;
    private BigDecimal saldoFonteDespesaORC;
    private BigDecimal valorReservado;
    private BigDecimal valorEstornoReservado;
    private BigDecimal valorExecutado;
    private BigDecimal valorEstornoExecutado;
    private BigDecimal valorReservaExecucao;
    private List<ExecucaoProcessoFonteItemVO> itens;
    private List<ExecucaoProcessoFonteItemVO> itensQuantidade;
    private List<ExecucaoProcessoFonteItemVO> itensValor;
    private Boolean geraReserva;
    private Long criadoEm;

    public ExecucaoProcessoFonteVO() {
        criadoEm = System.nanoTime();
        saldoFonteDespesaORC = BigDecimal.ZERO;
        valor = BigDecimal.ZERO;
        valorExecutado = BigDecimal.ZERO;
        valorReservado = BigDecimal.ZERO;
        valorEstornoReservado = BigDecimal.ZERO;
        valorEstornoExecutado = BigDecimal.ZERO;
        valorReservaExecucao = BigDecimal.ZERO;
        geraReserva = Boolean.FALSE;
        itens = Lists.newArrayList();
        itensQuantidade = Lists.newArrayList();
        itensValor = Lists.newArrayList();
    }

    public ExecucaoProcessoReserva getExecucaoProcessoReserva() {
        return execucaoProcessoReserva;
    }

    public void setExecucaoProcessoReserva(ExecucaoProcessoReserva execucaoProcessoReserva) {
        this.execucaoProcessoReserva = execucaoProcessoReserva;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldoFonteDespesaORC() {
        return saldoFonteDespesaORC;
    }

    public void setSaldoFonteDespesaORC(BigDecimal saldoFonteDespesaORC) {
        this.saldoFonteDespesaORC = saldoFonteDespesaORC;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }

    public BigDecimal getValorEstornoExecutado() {
        return valorEstornoExecutado;
    }

    public void setValorEstornoExecutado(BigDecimal valorEstornoExecutado) {
        this.valorEstornoExecutado = valorEstornoExecutado;
    }

    public BigDecimal getValorExecutado() {
        return valorExecutado;
    }

    public void setValorExecutado(BigDecimal valorExecutado) {
        this.valorExecutado = valorExecutado;
    }

    public BigDecimal getValorReservaExecucao() {
        return valorReservaExecucao;
    }

    public void setValorReservaExecucao(BigDecimal valorReservaExecucao) {
        this.valorReservaExecucao = valorReservaExecucao;
    }

    public BigDecimal getValorEstornoReservado() {
        return valorEstornoReservado;
    }

    public void setValorEstornoReservado(BigDecimal valorEstornoReservado) {
        this.valorEstornoReservado = valorEstornoReservado;
    }

    public List<ExecucaoProcessoFonteItemVO> getItens() {
        return itens;
    }

    public void setItens(List<ExecucaoProcessoFonteItemVO> itens) {
        this.itens = itens;
    }

    public List<ExecucaoProcessoFonteItemVO> getItensQuantidade() {
        return itensQuantidade;
    }

    public void setItensQuantidade(List<ExecucaoProcessoFonteItemVO> itensQuantidade) {
        this.itensQuantidade = itensQuantidade;
    }

    public List<ExecucaoProcessoFonteItemVO> getItensValor() {
        return itensValor;
    }

    public void setItensValor(List<ExecucaoProcessoFonteItemVO> itensValor) {
        this.itensValor = itensValor;
    }

    public Boolean getGeraReserva() {
        return geraReserva;
    }

    public void setGeraReserva(Boolean geraReserva) {
        this.geraReserva = geraReserva;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getSaldoDisponivel() {
        try {
            return getValorReservado().subtract(getValorExecutado()).subtract(getValorEstornoReservado()).add(getValorEstornoExecutado());
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getQuantidadeReservadaItemDotacao(ExecucaoProcessoItem execItem) {
        if (hasItensQuantidade()) {
            for (ExecucaoProcessoFonteItemVO item : itensQuantidade) {
                if (execItem.equals(item.getExecucaoProcessoItem())) {
                    return item.getQuantidade();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorReservadoItemDotacao(ExecucaoProcessoItem execItem) {
        if (hasItensValor()) {
            for (ExecucaoProcessoFonteItemVO item : itensValor) {
                if (execItem.equals(item.getExecucaoProcessoItem())) {
                    return item.getValorTotal();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorTotalItemQuantidade() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ExecucaoProcessoFonteItemVO item : getItensQuantidade()) {
                total = total.add(item.getQuantidade().multiply(item.getValorUnitario()));
            }
            return total;
        } catch (NullPointerException e) {
            return total;
        }
    }

    public BigDecimal getValorTotalItemValor() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ExecucaoProcessoFonteItemVO item : getItensValor()) {
                total = total.add(item.getValorTotal());
            }
            return total;
        } catch (NullPointerException e) {
            return total;
        }
    }

    public void calcularValorTotal() {
        setValor(getValorTotalItemQuantidade().add(getValorTotalItemValor()));
    }

    public boolean hasSaldoDisponivel() {
        return getSaldoDisponivel() != null && getSaldoDisponivel().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorReservaNaExecucao() {
        return getValorReservaExecucao() != null && getValorReservaExecucao().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasItensQuantidade() {
        return itensQuantidade != null && !itensQuantidade.isEmpty();
    }

    public boolean hasItensValor() {
        return itensValor != null && !itensValor.isEmpty();
    }

    public String getDescricaoExercicioComFonteDespesa() {
        try {
            return fonteDespesaORC.getDespesaORC().getExercicio().toString() + " - " + fonteDespesaORC.toString();
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
            return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecucaoProcessoFonteVO that = (ExecucaoProcessoFonteVO) o;
        return Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criadoEm);
    }

}
