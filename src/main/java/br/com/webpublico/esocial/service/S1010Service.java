package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.NaturezaJuridicaEntidade;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.DetalheLogErroEnvioEvento;
import br.com.webpublico.entidades.rh.esocial.EventoFPEmpregador;
import br.com.webpublico.entidades.rh.esocial.LogErroEnvioEvento;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.EventoS1010;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.rh.esocial.LogErroEnvioEventoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1010Service")
public class S1010Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1010Service.class);

    @Autowired
    private ESocialService eSocialService;

    private PessoaFisicaFacade pessoaFisicaFacade;
    private EventoFPFacade eventoFPFacade;

    private LogErroEnvioEventoFacade logErroEnvioEventoFacade;


    @PostConstruct
    public void init() {
        try {
            pessoaFisicaFacade = (PessoaFisicaFacade) new InitialContext().lookup("java:module/PessoaFisicaFacade");
            eventoFPFacade = (EventoFPFacade) new InitialContext().lookup("java:module/EventoFPFacade");
            logErroEnvioEventoFacade = (LogErroEnvioEventoFacade) new InitialContext().lookup("java:module/LogErroEnvioEventoFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }


    public void enviarS1010(ConfiguracaoEmpregadorESocial config, List<EventoFP> eventos, List<DetalheLogErroEnvioEvento> itemLog) {
        ValidacaoException val = new ValidacaoException();
        int quantidadeErros = 0;
        if (eventos == null) {
            eventos = eventoFPFacade.getEventoFPPorEmpregador(config.getEntidade());
        }
        if (eventos != null) {
            for (EventoFP eventofp : eventos) {
                try {
                    enviar1010(config, val, eventofp);
                } catch (ValidacaoException ve) {
                    quantidadeErros++;
                    if (itemLog != null) {
                        LogErroEnvioEvento erro = logErroEnvioEventoFacade.criarLogErro("EventoFP", eventofp.getId(), config, TipoArquivoESocial.S1010, ve);
                        itemLog.addAll(erro.getItemDetalheLog());
                    }
                }
            }
            if (quantidadeErros > 0) {
                FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + eventos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
            }
        }
    }

    private void removerRegistroLog(List<EventoFP> eventos, ConfiguracaoEmpregadorESocial config) {
        List<Long> idsEvento = Lists.newArrayList();
        for (EventoFP evento : eventos) {
            idsEvento.add(evento.getId());
        }
        logErroEnvioEventoFacade.removerLogErroEnvioEvento(idsEvento, config);
    }


    public void enviar1010(ConfiguracaoEmpregadorESocial config, ValidacaoException val, EventoFP eventofp) {
        EventoS1010 s1010 = criarEventoS1010(config, eventofp, val);
        logger.error("Antes de Enviar: " + s1010.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1010(s1010);
    }

    private EventoS1010 criarEventoS1010(ConfiguracaoEmpregadorESocial config, EventoFP
        eventoFP, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1010 eventoS1010 = (EventosESocialDTO.S1010) eSocialService.getEventoS1010(empregador);
        eventoS1010.setIdentificadorWP(eventoFP.getId().toString());
        eventoS1010.setClasseWP(ClasseWP.EVENTOFP);
        eventoS1010.setDescricao(eventoFP.getCodigoAndDescricao());
        adicionarDadosRubrica(config, eventoS1010, eventoFP, val);
        return eventoS1010;
    }

    private void adicionarDadosRubrica(ConfiguracaoEmpregadorESocial config, EventosESocialDTO.S1010
        eventoS1010, EventoFP evento, ValidacaoException val) {
        EventoFPEmpregador eventoFPEmpregador = eventoFPFacade.getEventoFPEmpregador(evento, new Date(), config.getEntidade());
        eventoS1010.setIdESocial(evento.getId().toString().concat(evento.getDebitoCreditoDecimo()));
        eventoS1010.setCodRubr(evento.getCodigo());

        if (!Strings.isNullOrEmpty(eventoFPEmpregador.getIdentificacaoTabela())) {
            eventoS1010.setIdeTabRubr(eventoFPEmpregador.getIdentificacaoTabela());
        } else {
            val.adicionarMensagemDeCampoObrigatorio("A Identificação da Tabela do evento " + evento + " não foi informada.");
        }
        eventoS1010.setIniValid(config.getInicioVigencia());
        preencherNaturezaRubrica(eventoS1010, evento, val, eventoFPEmpregador);
        eventoS1010.setTpRubr(evento.getTipoEventoFP().getCodigoEsocial());
        preencherCodigoRubricaPrevidenciaSocial(eventoS1010, evento, val, eventoFPEmpregador);
        preencherCodigoRubricaIRRF(eventoS1010, evento, val, eventoFPEmpregador);
        preencherCodigoRubricaFGTS(eventoS1010, evento, val, eventoFPEmpregador);
        preencherIncidenciaSindical(eventoS1010, evento, val, eventoFPEmpregador);
        preencherTetoRemuneratorio(eventoS1010, config.getEntidade(), eventoFPEmpregador);
    }

    private void preencherCodigoRubricaPrevidenciaSocial(EventosESocialDTO.S1010 eventoS1010, EventoFP
        evento, ValidacaoException val, EventoFPEmpregador eventoFPEmpregador) {
        if (eventoFPEmpregador.getIncidenciaPrevidencia() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O evento " + evento + " não possui Incidência Tributária para a Previdência Social.");
        } else {
            eventoS1010.setCodIncCP(eventoFPEmpregador.getIncidenciaPrevidencia().getCodigo());
        }
    }

    private void preencherCodigoRubricaFGTS(EventosESocialDTO.S1010 eventoS1010, EventoFP
        evento, ValidacaoException val, EventoFPEmpregador eventoFPEmpregador) {
        if (eventoFPEmpregador.getIncidenciaTributariaFGTS() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O evento " + evento + " não possui Incidência Tributária para FGTS.");
        } else {
            eventoS1010.setCodIncFGTS(eventoFPEmpregador.getIncidenciaTributariaFGTS().getCodigo());
        }
    }

    private void preencherCodigoRubricaIRRF(EventosESocialDTO.S1010 eventoS1010, EventoFP
        evento, ValidacaoException val, EventoFPEmpregador eventoFPEmpregador) {
        if (eventoFPEmpregador.getIncidenciaTributariaIRRF() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O evento " + evento + " não possui Incidência Tributária para IRRF.");
        } else {
            eventoS1010.setCodIncIRRF(Integer.valueOf(eventoFPEmpregador.getIncidenciaTributariaIRRF().getCodigo()));
        }
    }

    private void preencherNaturezaRubrica(EventosESocialDTO.S1010 eventoS1010, EventoFP evento, ValidacaoException
        val, EventoFPEmpregador eventoFPEmpregador) {
        if (eventoFPEmpregador.getNaturezaRubrica() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O evento " + evento + " não possui Natureza Rubrica.");
        } else {
            eventoS1010.setDscRubr(evento.getDescricao());
            eventoS1010.setNatRubr(Integer.parseInt(eventoFPEmpregador.getNaturezaRubrica().getCodigo()));
        }

        if (evento.getTipoOperacaoESocial() != null) {
            eventoS1010.setTipoOperacaoESocial(TipoOperacaoESocial.valueOf(evento.getTipoOperacaoESocial().name()));
        }
    }

    private void preencherIncidenciaSindical(EventosESocialDTO.S1010 eventoS1010, EventoFP
        evento, ValidacaoException coe, EventoFPEmpregador eventoFPEmpregador) {
        if (eventoFPEmpregador.getIncidenciaTributariaRPPS() == null) {
            coe.adicionarMensagemDeCampoObrigatorio("O evento: " + evento + " deve possuir o campo Incidência Tributária para o RPPS informado.");
        } else {
            eventoS1010.setCodIncCPRP(eventoFPEmpregador.getIncidenciaTributariaRPPS().getCodigo());
        }
    }

    private void preencherTetoRemuneratorio(EventosESocialDTO.S1010 eventoS1010, Entidade
        entidade, EventoFPEmpregador eventoFPEmpregador) {
        NaturezaJuridicaEntidade naturezaJuridicaEntidade = entidade.getNaturezaJuridicaEntidade();
        if (naturezaJuridicaEntidade != null && naturezaJuridicaEntidade.isNaturezaAdministracao()) {
            eventoS1010.setTetoRemun(eventoFPEmpregador.getTetoRemuneratorio());
        }

    }

}
