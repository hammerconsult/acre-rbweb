package br.com.webpublico.controle.rh.comum;

import br.com.webpublico.controle.SuperControladorCRUD;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Set;


@ManagedBean
@ViewScoped
public class ComponenteEventosESocialControlador extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ComponenteEventosESocialControlador.class);

    private List<EventoESocialDTO> eventos;
    private List<OcorrenciaESocialDTO> ocorrencias;
    private String descricaoResposta;
    private String xml;


    @Override
    public AbstractFacade getFacede() {
        return null;
    }

    public void setEventos(List<EventoESocialDTO> eventos) {
        this.eventos = eventos;
    }

    public List<EventoESocialDTO> getEventos() {
        return eventos;
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(Set<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = Lists.newArrayList(ocorrencias);
    }

    public String getDescricaoResposta() {
        return descricaoResposta;
    }

    public void setDescricaoResposta(String descricaoResposta) {
        this.descricaoResposta = descricaoResposta;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }


}
