package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.contabil.reinf.FiltroReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.reinf.eventos.EventosReinfDTO;
import br.com.webpublico.reinf.eventos.domain.EventoR2098;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "r2098Service")
public class R2098Service {

    protected static final Logger logger = LoggerFactory.getLogger(R2098Service.class);

    @Autowired
    private ReinfService reinfService;

    private ConfiguracaoContabilFacade configuracaoContabilFacade;

    private EmpregadorESocial empregadorESocial;


    @PostConstruct
    public void init() {
        try {
            configuracaoContabilFacade = (ConfiguracaoContabilFacade) new InitialContext().lookup("java:module/ConfiguracaoContabilFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public void enviar(AssistenteSincronizacaoReinf assistente, EventoR2098 r2098) {
        if (assistente.getSelecionado().getUtilizarVersao2_1()) {
            reinfService.enviarEventoR2098V2(r2098);
        } else {
            reinfService.enviarEventoR2098(r2098);
        }

    }

    public EventoR2098 criarEventosR2098(AssistenteSincronizacaoReinf assistente, ConfiguracaoEmpregadorESocial config, FiltroReinf reg) {
        ValidacaoException val = new ValidacaoException();
        this.empregadorESocial = null;
        EventoR2098 r2098 = criarEventoR2098(assistente, config, reg, val);
        if (r2098 != null) {
            logger.info("XML " + r2098.getXml());
            return r2098;
        }
        return null;
    }

    private EventoR2098 criarEventoR2098(AssistenteSincronizacaoReinf assistente, ConfiguracaoEmpregadorESocial config, FiltroReinf reg, ValidacaoException val) {
        if (empregadorESocial == null) {
            empregadorESocial = reinfService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        }
        if (assistente.getSelecionado().getUtilizarVersao2_1()) {
            EventosReinfDTO.R2098V2 evento = criarEventoR2098V2(config, reg, val);
            return evento;
        } else {
            EventosReinfDTO.R2098 evento = criarEventoR2098V1(config, reg, val);
            return evento;
        }
    }

    private EventosReinfDTO.R2098 criarEventoR2098V1(ConfiguracaoEmpregadorESocial config, FiltroReinf reg, ValidacaoException val) {
        EventosReinfDTO.R2098 evento = (EventosReinfDTO.R2098) reinfService.getEventoR2098(empregadorESocial);

        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }
        Integer mes = reg.getMes().getNumeroMes();
        Integer ano = reg.getExercicio().getAno();
        evento.setIdESocial(config.getId().toString().concat(ano.toString().concat(mes.toString())));
        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());
        evento.setPerApur(ano, mes);
        return evento;
    }

    private EventosReinfDTO.R2098V2 criarEventoR2098V2(ConfiguracaoEmpregadorESocial config, FiltroReinf reg, ValidacaoException val) {

        EventosReinfDTO.R2098V2 evento = (EventosReinfDTO.R2098V2) reinfService.getEventoR2098V2(empregadorESocial);

        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }

        Integer mes = reg.getMes().getNumeroMes();
        Integer ano = reg.getExercicio().getAno();
        evento.setIdESocial(config.getId().toString().concat(ano.toString().concat(mes.toString())));
        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());
        evento.setPerApur(ano, mes);
        evento.setProcEmi(1);
        evento.setVerProc("2.1.2");
        return evento;
    }
}
