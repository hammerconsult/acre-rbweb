package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.enums.TipoEventoFP;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 25/01/14
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class ItensResultado implements Serializable, Comparable<Object> {
    private EventoFP evento;
    private TipoEventoFP tipoEventoFP;
    private BigDecimal valor;
    private BigDecimal valorweb;
    private BigDecimal valorTurma;
    private Integer ordenacao;

    public ItensResultado() {
        ordenacao = 3;
    }

    public EventoFP getEvento() {
        return evento;
    }

    public void setEvento(EventoFP evento) {
        this.evento = evento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Integer ordenacao) {
        this.ordenacao = ordenacao;
    }

    public BigDecimal getValorweb() {
        return valorweb;
    }

    public void setValorweb(BigDecimal valorweb) {
        this.valorweb = valorweb;
    }

    public TipoEventoFP getTipoEventoFP() {
        return tipoEventoFP;
    }

    public void setTipoEventoFP(TipoEventoFP tipoEventoFP) {
        this.tipoEventoFP = tipoEventoFP;
    }

    public BigDecimal getValorTurma() {
        return valorTurma;
    }

    public void setValorTurma(BigDecimal valorTurma) {
        this.valorTurma = valorTurma;
    }

    @Override
    public String toString() {
        return evento.getCodigo() + "-" + evento.getDescricao() + ", valorweb=" + valorweb +
                ", valorTurma=" + valorTurma + System.getProperty("line.separator");
    }

    public String getDescricao() {
        return evento.getCodigo() + "-" + evento.getDescricao() + ", valorweb=" + valorweb +
                ", valorTurma=" + valorTurma + System.getProperty("line.separator");
    }

    @Override
    public int compareTo(Object o) {
        return ordenacao.compareTo(((ItensResultado) o).getOrdenacao());  //To change body of implemented methods use File | Settings | File Templates.
    }
}
