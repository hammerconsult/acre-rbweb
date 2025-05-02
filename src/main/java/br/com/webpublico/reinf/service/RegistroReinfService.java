package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.contabil.reinf.RegistroReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.domain.OcorrenciaESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.reinf.eventos.EventoReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.seguranca.service.AbstractCadastroService;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class RegistroReinfService extends AbstractCadastroService<RegistroReinf> {
    private final Logger log = LoggerFactory.getLogger(RegistroReinfService.class);


    @Autowired
    private ReinfService reinfService;
    private ConfiguracaoRHFacade configuracaoRHFacade;

    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    public static RegistroReinfService getService() {
        return (RegistroReinfService) Util.getSpringBeanPeloNome("registroESocialService");
    }

    @PostConstruct
    public void init() {
        try {
            configuracaoRHFacade = (ConfiguracaoRHFacade) new InitialContext().lookup("java:module/ConfiguracaoRHFacade");
            configuracaoEmpregadorESocialFacade = (ConfiguracaoEmpregadorESocialFacade) new InitialContext().lookup("java:module/ConfiguracaoEmpregadorESocialFacade");
        } catch (NamingException e) {
            e.printStackTrace();
            log.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            log.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    public void atualizarStatusRegistros() {
        try {
            log.error("integracao efd-reinf");
            Boolean logESocial = configuracaoRHFacade.recuperarConfiguracaoRHVigenteDataAtual().getLogESocial();
            List<EventoReinfDTO> eventosParaIntegrar = Lists.newArrayList();
            for (ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial : configuracaoEmpregadorESocialFacade.recuperarConfiguracaoEmpregadorVigente()) {
                log.error("empregador recuperado " + configuracaoEmpregadorESocial.getEntidade());
                List<EventoReinfDTO> eventos = reinfService.buscarEventosESocialNaoSincronizados(logESocial, configuracaoEmpregadorESocial.getEntidade().getPessoaJuridica().getCnpj());
                log.error("quantidade de eventos para integrar " + eventos.size());
                for (EventoReinfDTO evento : eventos) {
                    if (SituacaoESocial.PROCESSADO_COM_SUCESSO.equals(evento.getSituacao()) || SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.equals(evento.getSituacao())
                        || SituacaoESocial.PROCESSADO_COM_ERRO.equals(evento.getSituacao())) {
                        criarEventoReinf(evento, configuracaoEmpregadorESocial);
                        eventosParaIntegrar.add(evento);
                    }
                }
            }
            log.error("Quantidade de eventos para Integrar >  " + eventosParaIntegrar.size());
            marcarEventosIntegrados(eventosParaIntegrar);
        } catch (Exception e) {
            logger.error("Erro ao atualizar registros", e);
        }
    }

    private void marcarEventosIntegrados(List<EventoReinfDTO> eventos) {
        List<Long> idsEventosRecuperados = Lists.newArrayList();
        for (EventoReinfDTO evento : eventos) {
            idsEventosRecuperados.add(evento.getId());
        }
        if (!idsEventosRecuperados.isEmpty()) {
            reinfService.enviarEventosIntegrados(idsEventosRecuperados);
        }
    }

    public RegistroReinf criarEventoReinf(EventoReinfDTO evento, ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial) {
        RegistroReinf reinf = new RegistroReinf();
        reinf.setSituacao(evento.getSituacao());
        reinf.setDataRegistro(evento.getDataRegistro());
        reinf.setEmpregador(configuracaoEmpregadorESocial);
        reinf.setTipoArquivo(evento.getTipoArquivo());
        reinf.setXml(evento.getXML());
        reinf.setIdXmlEvento(evento.getIdXMLEvento());
        reinf.setIdentificador(evento.getId());
        reinf.setRecibo(evento.getReciboEntrega());
        reinf.setCodigoResposta(evento.getCodigoResposta());
        reinf.setDescricaoResposta(evento.getDescricaoResposta());
        reinf.setOperacao(evento.getOperacao());
        reinf.setDataIntegracao(evento.getDataOperacao());
        if (!Strings.isNullOrEmpty(evento.getIdESocial())) {
            reinf.setIdESocial(StringUtil.removerEspacoEmBranco(evento.getIdESocial()));
        }
        if (evento.getOcorrencias() != null) {
            for (OcorrenciaESocial ocorrencia : evento.getOcorrencias()) {
                reinf.setObservacao(ocorrencia.getDescricao() + "<br />");
                reinf.setCodigoOcorrencia(ocorrencia.getCodigo());
                reinf.setLocalizacao(ocorrencia.getLocalizacao());
            }
        }
        return save(reinf);
    }

    @Override
    public RegistroReinf save(RegistroReinf entity) {
        verificarRegistrosJaSalvos(entity);
        RegistroReinf save = super.save(entity);
        return save;
    }

    private void verificarRegistrosJaSalvos(RegistroReinf entity) {
        if (entity != null && entity.getIdentificador() != null) {
            List<RegistroReinf> registroESocials = buscarRegistrosPorIdentificador(entity.getIdentificador(), entity.getTipoArquivo());
            if (!registroESocials.isEmpty()) {
                for (RegistroReinf remover : registroESocials) {
                    entityManager.remove(remover);
                }
            }
        }
    }

    public List<RegistroReinf> buscarRegistrosPorIdentificador(Long identificador, TipoArquivoReinf tipoArquivoESocial) {
        String sql = "select ev.* from RegistroReinf ev " +
            " where ev.IDENTIFICADOR = :identificador " +
            "       and ev.tipoArquivo = :tipoArquivo";
        Query query = entityManager.createNativeQuery(sql, RegistroReinf.class);
        query.setParameter("identificador", identificador);
        query.setParameter("tipoArquivo", tipoArquivoESocial.name());
        return query.getResultList();
    }
}
