package br.com.webpublico.pncp.dto;

import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import com.google.common.collect.Lists;

import java.util.List;

public class EnteAutorizadoDTO {

    private EventoPncpVO eventoPncpVO;
    private String idUsuario;
    private List<String> entesAutorizados;

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public EnteAutorizadoDTO() {
        entesAutorizados = Lists.newArrayList();
    }

    public List<String> getEntesAutorizados() {
        return entesAutorizados;
    }

    public void setEntesAutorizados(List<String> entesAutorizados) {
        this.entesAutorizados = entesAutorizados;
    }

    public EventoPncpVO getEventoPncpVO() {
        return eventoPncpVO;
    }

    public void setEventoPncpVO(EventoPncpVO eventoPncpVO) {
        this.eventoPncpVO = eventoPncpVO;
    }
}
