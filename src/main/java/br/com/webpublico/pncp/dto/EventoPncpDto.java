package br.com.webpublico.pncp.dto;

import br.com.webpublico.pncp.entidade.EventoPncp;

import java.util.ArrayList;
import java.util.List;

public class EventoPncpDTO {

    private List<Long> idEventoPncp;

    public EventoPncpDTO(EventoPncp evento) {
        this.idEventoPncp = new ArrayList<>();
        this.idEventoPncp.add(evento.getId());
    }

    public List<Long> getIdEventoPncp() {
        return idEventoPncp;
    }

    public void setIdEventoPncp(List<Long> idEventoPncp) {
        this.idEventoPncp = idEventoPncp;
    }
}
