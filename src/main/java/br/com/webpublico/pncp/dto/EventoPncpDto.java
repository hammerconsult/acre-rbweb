package br.com.webpublico.pncp.dto;

import br.com.webpublico.pncp.entidade.EventoPncp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventoPncpDto {

    private List<Long> idEventoPncp;

    public EventoPncpDto(EventoPncp evento) {
        this.idEventoPncp = new ArrayList<>();
        this.idEventoPncp.add(evento.getId());
    }

    public EventoPncpDto(List<EventoPncp> eventos) {
        this.idEventoPncp = new ArrayList<>();
        this.idEventoPncp.addAll(eventos.stream().map(EventoPncp::getId).collect(Collectors.toSet()));
    }

    public List<Long> getIdEventoPncp() {
        return idEventoPncp;
    }

    public void setIdEventoPncp(List<Long> idEventoPncp) {
        this.idEventoPncp = idEventoPncp;
    }
}
