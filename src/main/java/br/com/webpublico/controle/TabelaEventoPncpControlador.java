package br.com.webpublico.controle;

import br.com.webpublico.pncp.entidade.EventoPncp;
import br.com.webpublico.pncp.facade.EventoPncpFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class TabelaEventoPncpControlador implements Serializable {

    @EJB
    private EventoPncpFacade eventoPncpFacade;
    private Map<Long, List<EventoPncp>> map = new HashMap<>();


    public void buscarTodosEventos(Long idOrigem) {
        if (idOrigem != null) {
            List<EventoPncp> eventoPncps = eventoPncpFacade.buscarPorIdOrigem(idOrigem);
            map.put(idOrigem, eventoPncps);
        }
    }

    public List<EventoPncp> getEventos(Long idOrigem) {
        return map.get(idOrigem);
    }
}
