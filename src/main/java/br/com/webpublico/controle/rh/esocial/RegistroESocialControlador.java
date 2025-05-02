package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.esocial.service.RegistroESocialService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "registroESocialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-registro-esocial", pattern = "/rh/e-social/registro-esocial/novo/", viewId = "/faces/rh/esocial/registro-esocial/edita.xhtml"),
    @URLMapping(id = "editar-registro-esocial", pattern = "/rh/e-social/registro-esocial/editar/#{registroESocialControlador.id}/", viewId = "/faces/rh/esocial/registro-esocial/edita.xhtml"),
    @URLMapping(id = "listar-registro-esocial", pattern = "/rh/e-social/registro-esocial/listar/", viewId = "/faces/rh/esocial/registro-esocial/lista.xhtml"),
    @URLMapping(id = "ver-registro-esocial", pattern = "/rh/e-social/registro-esocial/ver/#{registroESocialControlador.id}/", viewId = "/faces/rh/esocial/registro-esocial/visualizar.xhtml")
})
public class RegistroESocialControlador extends PrettyControlador<RegistroESocial> implements Serializable, CRUD {

    @EJB
    private RegistroESocialFacade registroESocialFacade;
    private RegistroESocialService registroESocialService;
    private ESocialService eSocialService;
    private Object objeto;
    private List<EventoESocialDTO> eventos;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;

    public RegistroESocialControlador() {
        super(RegistroESocial.class);
    }

    @PostConstruct
    public void init() {
        registroESocialService = (RegistroESocialService) Util.getSpringBeanPeloNome("registroESocialService");
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
    }

    public String getBuscarRegistro() {
        if (objeto == null) {
            objeto = registroESocialService.carregarObjeto(selecionado);
        }
        return objeto != null ? objeto.toString() : "";
    }


    @Override
    public AbstractFacade getFacede() {
        return registroESocialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/e-social/registro-esocial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "nova-registro-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-registro-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }


    @URLAction(mappingId = "ver-registro-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarEventos();
    }

    private void buscarEventos() {
        try {
            Boolean logEsocial = configuracaoRHFacade.recuperarConfiguracaoRHVigenteDataAtual().getLogESocial();
            eventos = eSocialService.buscarEventosPorIdESocial(selecionado.getIdentificador().toString(), logEsocial);
        } catch (ExcecaoNegocioGenerica en) {
            logger.debug("sem configuracação para a base do e-social");
        }
    }

    public List<EventoESocialDTO> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoESocialDTO> eventos) {
        this.eventos = eventos;
    }

    public void enviarEvento() {
        try {
            logger.debug("enviando registro... [{}]", selecionado);
            registroESocialService.enviarEvento(selecionado);
            FacesUtil.addAtencao("Evento enviado com sucesso, aguarde o retorno da validação do evento.");
            buscarEventos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar evento: ", rax.getMessage());
            logger.error("Erro ao executar enviarEvento: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar evento: ", e.getMessage());
            logger.error("Erro ao executar enviarEvento: ", e);
        }
    }
}
