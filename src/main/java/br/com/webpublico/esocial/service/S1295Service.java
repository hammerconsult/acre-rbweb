package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroS1295;
import br.com.webpublico.enums.TipoIndicativoPeriodoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.EventoS1295;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1295Service")
public class S1295Service {
    protected static final Logger logger = LoggerFactory.getLogger(S1295Service.class);

    @Autowired
    private ESocialService eSocialService;
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    @PostConstruct
    public void init() {
        try {
            configuracaoEmpregadorESocialFacade = (ConfiguracaoEmpregadorESocialFacade)
                Util.getFacadeViaLookup("java:module/ConfiguracaoEmpregadorESocialFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    public void enviarEventoS1295(RegistroS1295 registroS1295) {
        ValidacaoException ve = new ValidacaoException();
        ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(registroS1295.getEntidade());

        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(registroS1295.getEntidade().getPessoaJuridica().getCnpj()));

        EventoS1295 eventoS1295 = criarEventoS1295(registroS1295, ve, config, empregador);
        logger.debug("Antes de Enviar: " + eventoS1295.getXml());
        ve.lancarException();
        eSocialService.enviarEventoS1295(eventoS1295);
    }

    private EventoS1295 criarEventoS1295(RegistroS1295 registroS1295, ValidacaoException ve, ConfiguracaoEmpregadorESocial config, EmpregadorESocial empregador) {
        EventosESocialDTO.S1295 eventoS1295 = (EventosESocialDTO.S1295) eSocialService.getEventoS1295(empregador);

        eventoS1295.setIndApuracao(registroS1295.getTipoIndicativoPeriodoESocial().getCodigo());

        if (TipoIndicativoPeriodoESocial.ANUAL.equals(registroS1295.getTipoIndicativoPeriodoESocial())) {
            eventoS1295.setPerApur(registroS1295.getExercicio().getAno());
        } else {
            eventoS1295.setPerApur(registroS1295.getExercicio().getAno(), registroS1295.getMes().getNumeroMes());
        }

        eventoS1295.setNmResp(config.getResponsavel() != null ? config.getResponsavel().getNome() : "");
        eventoS1295.setCpfResp(config.getResponsavel() != null ? retornaApenasNumeros(config.getResponsavel().getCpf()) : "");

        String telefone = (config.getResponsavel() != null && config.getResponsavel().getTelefonePrincipal() != null) ?
            config.getResponsavel().getTelefonePrincipal().getTelefone() : "";
        eventoS1295.setTelefone(retornaApenasNumeros(telefone));

        return eventoS1295;
    }
}
