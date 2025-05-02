package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EventoCalculo;

import java.io.Serializable;
import java.util.List;

public class GrupoEvento implements Serializable {

    List<EventoCalculo> eventos;

    public List<EventoCalculo> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoCalculo> eventos) {
        this.eventos = eventos;
    }
}
