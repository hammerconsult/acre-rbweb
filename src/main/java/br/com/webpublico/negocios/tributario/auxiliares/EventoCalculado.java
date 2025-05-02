package br.com.webpublico.negocios.tributario.auxiliares;

import br.com.webpublico.entidades.EventoCalculo;

import java.math.BigDecimal;

public class EventoCalculado {

    private EventoCalculo eventoCalculo;
    private BigDecimal valor;

    public EventoCalculado() {
    }

    public EventoCalculado(EventoCalculo eventoCalculo, BigDecimal valor) {
        this.eventoCalculo = eventoCalculo;
        this.valor = valor;
    }

    public EventoCalculo getEventoCalculo() {
        return eventoCalculo;
    }

    public void setEventoCalculo(EventoCalculo eventoCalculo) {
        this.eventoCalculo = eventoCalculo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
