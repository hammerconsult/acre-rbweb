package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.Atributo;
import br.com.webpublico.entidades.Construcao;
import br.com.webpublico.entidades.EventoCalculoConstrucao;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class WSConstrucao implements Serializable {

    private String descricao;
    private BigDecimal area;
    private String qualidadeConstrucao;
    private List<WSEventoCalculadoImovel> eventos;

    public WSConstrucao() {
    }

    public WSConstrucao(Construcao construcao) {
        eventos = Lists.newArrayList();
        descricao = construcao.getDescricao();
        area = BigDecimal.valueOf(construcao.getAreaConstruida());
        for (EventoCalculoConstrucao ev : construcao.getEventos()) {
            WSEventoCalculadoImovel wsEv = new WSEventoCalculadoImovel();
            wsEv.setDescricao(ev.getEventoCalculo().getEventoCalculo().getDescricao());
            wsEv.setIdentificacao(ev.getEventoCalculo().getEventoCalculo().getIdentificacao());
            wsEv.setValor(ev.getValorFormatado());
            eventos.add(wsEv);
        }
        for (Atributo atributo : construcao.getAtributos().keySet()) {
            if (atributo.getIdentificacao().equals("qualidade_construcao")) {
                qualidadeConstrucao = construcao.getAtributos().get(atributo).getValorDiscreto().getDescricao();
            }
        }
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public List<WSEventoCalculadoImovel> getEventos() {
        return eventos;
    }

    public void setEventos(List<WSEventoCalculadoImovel> eventos) {
        this.eventos = eventos;
    }

    public String getQualidadeConstrucao() {
        return qualidadeConstrucao;
    }

    public void setQualidadeConstrucao(String qualidadeConstrucao) {
        this.qualidadeConstrucao = qualidadeConstrucao;
    }
}
