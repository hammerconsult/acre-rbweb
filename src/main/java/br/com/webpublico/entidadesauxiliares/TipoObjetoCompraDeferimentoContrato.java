package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoObjetoCompra;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class TipoObjetoCompraDeferimentoContrato {

    private TipoObjetoCompra tipoObjetoCompra;
    private BigDecimal valorContratado;
    private BigDecimal valorExecutado;
    private BigDecimal valorEmpenhado;
    private BigDecimal valorEstornado;
    private List<String> mensagens;

    public TipoObjetoCompraDeferimentoContrato() {
        valorContratado = BigDecimal.ZERO;
        valorExecutado = BigDecimal.ZERO;
        valorEmpenhado = BigDecimal.ZERO;
        valorEstornado = BigDecimal.ZERO;
        mensagens = Lists.newArrayList();
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public BigDecimal getValorContratado() {
        return valorContratado;
    }

    public void setValorContratado(BigDecimal valorContratoItem) {
        this.valorContratado = valorContratoItem;
    }

    public BigDecimal getValorExecutado() {
        return valorExecutado;
    }

    public void setValorExecutado(BigDecimal valorExecutado) {
        this.valorExecutado = valorExecutado;
    }

    public BigDecimal getValorEmpenhado() {
        return valorEmpenhado;
    }

    public void setValorEmpenhado(BigDecimal valorEmpenhado) {
        this.valorEmpenhado = valorEmpenhado;
    }

    public BigDecimal getValorEstornado() {
        return valorEstornado;
    }

    public void setValorEstornado(BigDecimal valorEstornado) {
        this.valorEstornado = valorEstornado;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public BigDecimal getSaldoRestante() {
        try {
            return getValorContratado().subtract(getValorExecutado()).add(getValorEstornado());
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public boolean hasDiferenca() {
        return getSaldoRestante().compareTo(BigDecimal.ZERO) != 0;
    }
}
