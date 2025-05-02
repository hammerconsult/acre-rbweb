package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.contabil.reinf.RegistroEventoExclusaoReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.negocios.contabil.reinf.ReinfFacade;
import br.com.webpublico.reinf.eventos.EventosReinfDTO;
import br.com.webpublico.reinf.eventos.domain.EventoR9000;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "r9000Service")
public class R9000Service {

    protected static final Logger logger = LoggerFactory.getLogger(R9000Service.class);

    @Autowired
    private ReinfService reinfService;
    private ReinfFacade reinfFacade;
    private ConfiguracaoContabilFacade configuracaoContabilFacade;

    private EmpregadorESocial empregadorESocial;


    @PostConstruct
    public void init() {
        try {
            reinfFacade = (ReinfFacade) new InitialContext().lookup("java:module/ReinfFacade");
            configuracaoContabilFacade = (ConfiguracaoContabilFacade) new InitialContext().lookup("java:module/ConfiguracaoContabilFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }


    public void enviar(AssistenteSincronizacaoReinf assistente, EventoR9000 r9000) {
        if(assistente.getSelecionado().getUtilizarVersao2_1()) {
            reinfService.enviarEventoR9000V2(r9000);
        }else {
            reinfService.enviarEventoR9000(r9000);
        }
    }

    public EventoR9000 criarEventosR9000(AssistenteSincronizacaoReinf assistente, ConfiguracaoEmpregadorESocial config, RegistroEventoExclusaoReinf obj) {
        ValidacaoException val = new ValidacaoException();
        this.empregadorESocial = null;
        EventoR9000 r9000 = criarEventoR9000(assistente, config, obj, val);
        if (r9000 != null) {
            logger.info("XML " + r9000.getXml());
            reinfFacade.salvarRegistroEventoExclusao(obj);
            return r9000;
        }
        return null;
    }

    private EventoR9000 criarEventoR9000(AssistenteSincronizacaoReinf assistente, ConfiguracaoEmpregadorESocial config, RegistroEventoExclusaoReinf reg, ValidacaoException val) {
        if (empregadorESocial == null) {
            empregadorESocial = reinfService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        }
        if(assistente.getSelecionado().getUtilizarVersao2_1()) {
            EventosReinfDTO.R9000V2 evento = criarEventoR9000V2(config, reg, val);
            return evento;
        }else {
            EventosReinfDTO.R9000 evento = criarEventoR9000V1(config, reg, val);
            return  evento;
        }
    }

    private EventosReinfDTO.R9000 criarEventoR9000V1(ConfiguracaoEmpregadorESocial config, RegistroEventoExclusaoReinf reg, ValidacaoException val) {
        EventosReinfDTO.R9000 evento = (EventosReinfDTO.R9000) reinfService.getEventoR9000(empregadorESocial);

        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }
        Integer mes = reg.getMes().getNumeroMes();
        Integer ano = reg.getExercicio().getAno();
        evento.setIdESocial(ano.toString().concat(mes.toString()));

        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());

        evento.setPerApur(ano, mes);
        evento.setTpEvento(reg.getTipoArquivo().getCodigo());
        evento.setNrRecEvt(reg.getNumeroRecibo());
        return evento;
    }

    private EventosReinfDTO.R9000V2 criarEventoR9000V2(ConfiguracaoEmpregadorESocial config, RegistroEventoExclusaoReinf reg, ValidacaoException val) {
        EventosReinfDTO.R9000V2 evento = (EventosReinfDTO.R9000V2) reinfService.getEventoR9000V2(empregadorESocial);

        Integer mes = reg.getMes().getNumeroMes();
        Integer ano = reg.getExercicio().getAno();
        evento.setIdESocial(ano.toString().concat(mes.toString()));

        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }else{
            evento.setTpAmb(1);
        }
        evento.setProcEmi(1);
        evento.setVerProc("2.1.1");
        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());

        evento.setPerApur(ano, mes);

        evento.setTpEvento(reg.getTipoArquivo().getCodigo().substring(0,6));
        evento.setNrRecEvt(reg.getNumeroRecibo());
        return evento;
    }
}
