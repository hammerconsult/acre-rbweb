package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.contabil.reinf.FiltroReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ProcessoAdministrativoJudicial;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.enums.rh.esocial.TipoIntegracaoEsocial;
import br.com.webpublico.enums.rh.esocial.TipoProcessoAdministrativoJudicial;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.esocial.ProcessoAdministrativoJudicialFacade;
import br.com.webpublico.reinf.eventos.EventosReinfDTO;
import br.com.webpublico.reinf.eventos.domain.EventoR1070;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "r1070Service")
public class R1070Service {
    protected static final Logger logger = LoggerFactory.getLogger(R1070Service.class);
    @Autowired
    private ReinfService reinfService;
    private ProcessoAdministrativoJudicialFacade processoAdministrativoJudicialFacade;
    private EmpregadorESocial empregadorESocial;

    @PostConstruct
    public void init() {
        try {
            processoAdministrativoJudicialFacade = (ProcessoAdministrativoJudicialFacade) new InitialContext().lookup("java:module/ProcessoAdministrativoJudicialFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public List<ProcessoAdministrativoJudicial> buscarR1070(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        List<ProcessoAdministrativoJudicial> processos = processoAdministrativoJudicialFacade.buscarProcessosPorEmpregador(config, TipoIntegracaoEsocial.REINF);
        if (processos != null) {
            return processos;
        }
        return Lists.newArrayList();
    }

    public void enviarR1070(AssistenteSincronizacaoReinf assistente, EventoR1070 r1000) {
        if (assistente.getSelecionado().getUtilizarVersao2_1()) {
            reinfService.enviarEventoR1070V2(r1000);
        } else {
            reinfService.enviarEventoR1070(r1000);
        }
    }

    public List<EventoR1070> criarEventosR1070(AssistenteSincronizacaoReinf assistente, List<ProcessoAdministrativoJudicial> processos) {
        List<EventoR1070> retorno = Lists.newArrayList();
        ValidacaoException val = new ValidacaoException();
        if (processos != null) {
            this.empregadorESocial = null;
            for (ProcessoAdministrativoJudicial processo : processos) {
                EventoR1070 r1070 = criarEventoR1070(assistente, processo, val);
                if (r1070 != null) {
                    logger.info("XML " + r1070.getXml());
                    retorno.add(r1070);
                }
            }
        }
        return retorno;
    }

    public EventoR1070 criarEventoR1070(AssistenteSincronizacaoReinf assistente, ProcessoAdministrativoJudicial processo, ValidacaoException val) {
        ConfiguracaoEmpregadorESocial config = assistente.getConfiguracaoEmpregadorESocial();
        if (empregadorESocial == null) {
            empregadorESocial = reinfService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        }
        if (assistente.getSelecionado().getUtilizarVersao2_1()) {
            EventosReinfDTO.R1070V2 evento = criarEventoR1070V2(config, processo);
            return evento;
        } else {
            EventosReinfDTO.R1070 evento = criarEventoR1070V1(config, processo);
            return evento;
        }
    }

    private EventosReinfDTO.R1070V2 criarEventoR1070V2(ConfiguracaoEmpregadorESocial config, ProcessoAdministrativoJudicial processo) {
        EventosReinfDTO.R1070V2 evento = (EventosReinfDTO.R1070V2) reinfService.getEventoR1070V2(empregadorESocial);
        evento.setIdESocial(processo.getId().toString());
        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }
        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());

        evento.setIniValid(config.getDataInicioReinf());
        evento.setFimValid(config.getDataFimReinf());

        evento.setTpProc(processo.getTipoProcesso().getCodigo());
        evento.setNrProc(processo.getNumeroProcesso());
        if (processo.getIndicativoAutoria() != null) {
            evento.setIndAutoria(processo.getIndicativoAutoria().getCodigo());
        }
        criarInformacoesProcessoJudicial(processo, evento);
        criarInformacoesIndicativoMateriaTributaria(processo, evento);
        return evento;
    }

    private void criarInformacoesIndicativoMateriaTributaria(ProcessoAdministrativoJudicial processo, EventosReinfDTO.R1070V2 evento) {
        EventoR1070.InfoSup informacaoSuspensao = evento.addInfoSusp();
        if (processo.getIndicativoSuspensaoExigib() != null) {
            informacaoSuspensao.setIndSusp(processo.getIndicativoSuspensaoExigib().getCodigo());
        }
        informacaoSuspensao.setCodSusp(processo.getCodigoSuspensao());
        informacaoSuspensao.setDtDecisao(processo.getDataDecisao());
        informacaoSuspensao.setIndDeposito(processo.getDepositoMontanteIntegral());
    }

    private void criarInformacoesProcessoJudicial(ProcessoAdministrativoJudicial processo, EventosReinfDTO.R1070V2 evento) {
        if (TipoProcessoAdministrativoJudicial.JUDICIAL.equals(processo.getTipoProcesso())) {
            if (processo.getUfVara() != null) {
                evento.setUfVara(processo.getUfVara().getUf());
            }
            if (processo.getMunicipio() != null) {
                evento.setCodMunic(processo.getMunicipio().getCodigo());
            }
            evento.setIdVara(processo.getCodigoVara());
        }
    }

    private EventosReinfDTO.R1070 criarEventoR1070V1(ConfiguracaoEmpregadorESocial config, ProcessoAdministrativoJudicial processo) {
        EventosReinfDTO.R1070 evento = (EventosReinfDTO.R1070) reinfService.getEventoR1070(empregadorESocial);
        evento.setIdESocial(processo.getId().toString());
        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }
        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());

        evento.setIniValid(config.getDataInicioReinf());
        evento.setFimValid(config.getDataFimReinf());

        evento.setTpProc(processo.getTipoProcesso().getCodigo());
        evento.setNrProc(processo.getNumeroProcesso());
        if (processo.getIndicativoAutoria() != null) {
            evento.setIndAutoria(processo.getIndicativoAutoria().getCodigo());
        }
        criarInformacoesProcessoJudicial(processo, evento);
        criarInformacoesIndicativoMateriaTributaria(processo, evento);
        return evento;
    }

    private void criarInformacoesIndicativoMateriaTributaria(ProcessoAdministrativoJudicial processo, EventosReinfDTO.R1070 evento) {
        EventoR1070.InfoSup informacaoSuspensao = evento.addInfoSusp();
        if (processo.getIndicativoSuspensaoExigib() != null) {
            informacaoSuspensao.setIndSusp(processo.getIndicativoSuspensaoExigib().getCodigo());
        }
        informacaoSuspensao.setCodSusp(processo.getCodigoSuspensao());
        informacaoSuspensao.setDtDecisao(processo.getDataDecisao());
        informacaoSuspensao.setIndDeposito(processo.getDepositoMontanteIntegral());
    }

    private void criarInformacoesProcessoJudicial(ProcessoAdministrativoJudicial processo, EventosReinfDTO.R1070 evento) {
        if (processo.getUfVara() != null) {
            evento.setUfVara(processo.getUfVara().getUf());
        }
        if (processo.getMunicipio() != null) {
            evento.setCodMunic(processo.getMunicipio().getCodigo());
        }
        evento.setIdVara(processo.getCodigoVara());
    }
}
