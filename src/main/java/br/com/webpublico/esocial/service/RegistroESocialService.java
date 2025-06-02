package br.com.webpublico.esocial.service;

import br.com.webpublico.agendamentotarefas.job.AtualizarRegistrosESocialJob;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.entidades.rh.esocial.*;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.enums.rh.esocial.TipoAfastamentoESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.domain.OcorrenciaESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.PrestadorServicosFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.seguranca.service.AbstractCadastroService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RegistroESocialService extends AbstractCadastroService<RegistroESocial> {
    public static final String EVENTO_S1010 = "- Evento(s) S-1010 para envio ao E-social";
    public static final String EVENTO_S2200 = "- Evento(s) S-2200 para envio ao E-social";
    public static final String PRODUCAO = "WPPRODUCAO";
    private final Logger log = LoggerFactory.getLogger(RegistroESocialService.class);

    @Autowired
    private ESocialService eSocialService;
    @Autowired
    private S1000Service s1000Service;
    @Autowired
    private S1005Service s1005Service;
    @Autowired
    private S1010Service s1010Service;
    @Autowired
    private S1020Service s1020Service;
    @Autowired
    private S1030Service s1030Service;
    @Autowired
    private S1035Service s1035Service;
    @Autowired
    private S1040Service s1040Service;
    @Autowired
    private S1050Service s1050Service;
    @Autowired
    private S1070Service s1070Service;
    @Autowired
    private S2190Service s2190Service;
    @Autowired
    private S2200Service s2200Service;
    @Autowired
    private S2205Service s2205Service;
    @Autowired
    private S2206Service s2206Service;
    @Autowired
    private S2230Service s2230Service;
    @Autowired
    private S2250Service s2250Service;
    @Autowired
    private S2299Service s2299Service;
    @Autowired
    private S2298Service s2298Service;
    @Autowired
    private S2300Service s2300Service;
    @Autowired
    private S2306Service s2306Service;
    @Autowired
    private S2399Service s2399Service;
    private boolean integrou = true;

    private ConfiguracaoRHFacade configuracaoRHFacade;

    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    private PrestadorServicosFacade prestadorServicosFacade;


    public static RegistroESocialService getService() {
        return (RegistroESocialService) Util.getSpringBeanPeloNome("registroESocialService");
    }

    @PostConstruct
    public void init() {
        try {
            configuracaoRHFacade = (ConfiguracaoRHFacade) new InitialContext().lookup("java:module/ConfiguracaoRHFacade");
            configuracaoEmpregadorESocialFacade = (ConfiguracaoEmpregadorESocialFacade) new InitialContext().lookup("java:module/ConfiguracaoEmpregadorESocialFacade");
            prestadorServicosFacade = (PrestadorServicosFacade) new InitialContext().lookup("java:module/PrestadorServicosFacade");
        } catch (NamingException e) {
            e.printStackTrace();
            log.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            log.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    public void atualizarStatusRegistros() {
        try {
            log.debug("integracao e-social");
            Boolean logESocial = configuracaoRHFacade.recuperarConfiguracaoRHVigenteDataAtual().getLogESocial();
            List<EventoESocialDTO> eventosParaIntegrar = Lists.newArrayList();
            for (ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial : configuracaoEmpregadorESocialFacade.recuperarConfiguracaoEmpregadorVigente()) {
                try {
                    List<EventoESocialDTO> eventos = eSocialService.buscarEventosESocialNaoSincronizados(logESocial, configuracaoEmpregadorESocial.getEntidade().getPessoaJuridica().getCnpj());
                    if (eventos != null) {
                        log.debug("Quantidade de eventos para integrar " + eventos.size());
                        List<RegistroESocial> registros = Lists.newArrayList();
                        for (EventoESocialDTO evento : eventos) {
                            if (SituacaoESocial.PROCESSADO_COM_SUCESSO.equals(evento.getSituacao()) || SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.equals(evento.getSituacao())
                                || SituacaoESocial.PROCESSADO_COM_ERRO.equals(evento.getSituacao()) || SituacaoESocial.REJEITADO.equals(evento.getSituacao())) {
                                RegistroESocial registro = criarEventoESocial(evento, configuracaoEmpregadorESocial);
                                registros.add(registro);
                                eventosParaIntegrar.add(evento);
                                if (TipoArquivoESocial.S3000.equals(evento.getTipoArquivo()) &&
                                    SituacaoESocial.PROCESSADO_COM_SUCESSO.equals(evento.getSituacao())) {
                                    processarEventosS3000(registro);
                                }
                            }
                        }
                        salvar(registros, configuracaoEmpregadorESocial);
                    }
                } catch (Exception e) {
                    log.debug("Integrar eventos do E-Social - Não foi possível estabelecer conexão");
                    break;
                }
            }
            log.debug("Quantidade de eventos integrados e marcados no e-social >  " + eventosParaIntegrar.size());
            marcarEventosIntegrados(eventosParaIntegrar);
        } catch (Exception e) {
            logger.error("Erro ao atualizar registros", e);
        }
    }


    private void salvar(List<RegistroESocial> registroESocial, ConfiguracaoEmpregadorESocial config) {
        registroESocial.forEach(registro -> {
            configuracaoEmpregadorESocialFacade.getJdbcRegistroESocialDAO().insert(registro, config.getId());
        });
    }

    private void marcarEventosIntegrados(List<EventoESocialDTO> eventos) {
        List<Long> idsEventosRecuperados = Lists.newArrayList();
        for (EventoESocialDTO evento : eventos) {
            idsEventosRecuperados.add(evento.getId());
        }
        if (!idsEventosRecuperados.isEmpty()) {
            eSocialService.enviarEventosIntegrados(idsEventosRecuperados);
        }
    }

    private RegistroESocial criarEventoESocial(EventoESocialDTO evento, ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial) {
        RegistroESocial registroESocial = new RegistroESocial();
        registroESocial.setSituacao(evento.getSituacao());
        registroESocial.setDataRegistro(evento.getDataRegistro());
        registroESocial.setEmpregador(configuracaoEmpregadorESocial);
        registroESocial.setTipoArquivoESocial(evento.getTipoArquivo());
        registroESocial.setXml(evento.getXML());
        registroESocial.setIdentificador(evento.getId());
        registroESocial.setRecibo(evento.getReciboEntrega());
        registroESocial.setCodigoResposta(evento.getCodigoResposta());
        registroESocial.setDescricaoResposta(evento.getDescricaoResposta());
        registroESocial.setOperacao(evento.getOperacao());
        registroESocial.setDataIntegracao(new Date());
        registroESocial.setMesApuracao(evento.getMesApur());
        registroESocial.setAnoApuracao(evento.getAnoApur());
        registroESocial.setIdentificadorWP(evento.getIdentificadorWP());
        registroESocial.setClasseWP(evento.getClasseWP());
        registroESocial.setDescricao(evento.getDescricao());
        if (!Strings.isNullOrEmpty(evento.getIdESocial())) {
            registroESocial.setIdESocial(StringUtil.removerEspacoEmBranco(evento.getIdESocial()));
        }
        for (OcorrenciaESocial ocorrencia : evento.getOcorrencias()) {
            registroESocial.setObservacao(ocorrencia.getDescricao() + "<br />");
            registroESocial.setCodigoOcorrencia(ocorrencia.getCodigo());
            registroESocial.setLocalizacao(ocorrencia.getLocalizacao());
        }
        return registroESocial;
    }

    public void agendarAtualizacao(int minutos) throws SchedulerException {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        try {
            agendarAtualizacaoRegistros(scheduler, minutos);
        } catch (Exception e) {
            logger.error("Não foi possível agendar a atualização dos registros");
        } finally {
            scheduler.start();
        }
    }

    private void agendarAtualizacaoRegistros(Scheduler scheduler, int minutos) throws SchedulerException {

        JobDetail job = criaJob();

        Trigger trigger = criarTriggerAtualizar(minutos);
        logger.debug("Agendando AtualizarRegistrosESocialJob para " + minutos + " minutos ");
        scheduler.scheduleJob(job, trigger);

    }

    private Trigger criarTriggerAtualizar(int minutosASomar) {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusMinutes(minutosASomar);
        return TriggerBuilder
            .newTrigger().withIdentity("ESocial", "GrupoEsocial")
            .startAt(dateTime.toDate())
            .withSchedule(SimpleScheduleBuilder.repeatMinutelyForTotalCount(3))
            .build();
    }

    private JobDetail criaJob() {
        return JobBuilder.newJob(AtualizarRegistrosESocialJob.class).withIdentity("ESocial", "GrupoEsocial").build();
    }

    @Override
    public RegistroESocial save(RegistroESocial entity) {
        verificarRegistrosJaSalvos(entity);
        RegistroESocial save = super.save(entity);
        criarNotificacao(save);
        return save;
    }

    public List<RegistroESocial> buscarRegistrosPorIdentificadorAndSituacao(Long identificador, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao) {
        Query query = entityManager.createNativeQuery("select ev.* from registroesocial ev " +
            " where ev.identificador = :identificador " +
            "       and ev.situacao = :situacao" +
            "       and ev.tipoarquivoesocial = :tipoarquivo", RegistroESocial.class);
        query.setParameter("identificador", identificador);
        query.setParameter("situacao", situacao.name());
        query.setParameter("tipoarquivo", tipoArquivoESocial.name());
        return query.getResultList();
    }

    public List<RegistroESocial> buscarRegistrosPorEmpregadorAndSituacao(Long idEmpregador, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao) {
        Query query = entityManager.createNativeQuery("select ev.* from registroesocial ev " +
            " where ev.empregador_id= :empregador " +
            "       and ev.situacao = :situacao" +
            "       and ev.tipoarquivoesocial = :tipoarquivo", RegistroESocial.class);
        query.setParameter("empregador", idEmpregador);
        query.setParameter("situacao", situacao.name());
        query.setParameter("tipoarquivo", tipoArquivoESocial.name());
        return query.getResultList();
    }

    public List<RegistroESocial> buscarRegistrosPorSituacao(SituacaoESocial situacao) {
        Query query = entityManager.createNativeQuery("select ev.* from registroesocial ev " +
            " where ev.situacao = :situacao", RegistroESocial.class);
        query.setParameter("situacao", situacao.name());
        return query.getResultList();
    }

    private void verificarRegistrosJaSalvos(RegistroESocial entity) {
        if (entity != null && entity.getIdentificador() != null) {
            List<RegistroESocial> registroESocials = buscarRegistrosPorIdentificadorAndSituacao(entity.getIdentificador(), entity.getTipoArquivoESocial(), SituacaoESocial.NAO_ENVIADO);
            if (!registroESocials.isEmpty()) {
                for (RegistroESocial remover : registroESocials) {
                    NotificacaoService.getService().removerNotificacaoPorIdLink(remover.getId());
                    entityManager.remove(remover);
                }
            }
        }
    }

    private void criarNotificacao(RegistroESocial registro) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(montarDescricao(registro));
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTipoNotificacao(TipoNotificacao.ENVIO_EVENTO_ESOCIAL);
        notificacao.setTitulo(StringUtil.cortaDireita(70, "Envio de Evento Pendente: " + registro.getTipoArquivoESocial().getCodigo() + " - " + registro.getTipoArquivoESocial().getDescricao()));
        notificacao.setLink("/rh/e-social/registro-esocial/ver/" + registro.getId());
        NotificacaoService.getService().notificar(notificacao);
    }

    private String montarDescricao(RegistroESocial registro) {
        StringBuilder descricao = new StringBuilder();
        descricao.append("Evento Aguardando Envio para o E-Social " + registro.getTipoArquivoESocial().getCodigo() + " - " + registro.getTipoArquivoESocial().getDescricao());
        descricao.append(" Empregador: " + registro.getEmpregador());
        return descricao.toString();
    }

    public void enviarEvento(RegistroESocial registroESocial) throws ValidacaoException {
        ValidacaoException val = new ValidacaoException();
        PrestadorServicos prestadorServicos = null;
        switch (registroESocial.getTipoArquivoESocial()) {
            case S1000:
                s1000Service.enviarS1000(registroESocial.getEmpregador());
                break;
            case S1005:
                s1005Service.enviarS1005(registroESocial.getEmpregador());
                break;
            case S1010:
                Object o = carregarObjeto(registroESocial);
                s1010Service.enviar1010(registroESocial.getEmpregador(), val, (EventoFP) o);
                break;
            case S1020:
                s1020Service.enviarS1020(registroESocial.getEmpregador(), (Entidade) carregarObjeto(registroESocial));
                break;
            case S1030:
                s1030Service.enviarS1030(registroESocial.getEmpregador(), (Cargo) carregarObjeto(registroESocial));
                break;
            case S1035:
                s1035Service.enviarS1035(registroESocial.getEmpregador(), (Cargo) carregarObjeto(registroESocial));
                break;
            case S1040:
                s1040Service.enviarS1040(registroESocial.getEmpregador(), (Cargo) carregarObjeto(registroESocial));
                break;
            case S1050:
                s1050Service.enviarS1050(registroESocial.getEmpregador(), (HorarioDeTrabalho) carregarObjeto(registroESocial));
                break;
            case S1070:
                s1070Service.enviarS1070(registroESocial.getEmpregador(), (ProcessoAdministrativoJudicial) carregarObjeto(registroESocial));
                break;
            case S2200:
                s2200Service.enviarS2200(registroESocial.getEmpregador(), (ContratoFP) carregarObjeto(registroESocial), configuracaoRHFacade.getSistemaFacade().getDataOperacao(), null);
                break;
            case S2190:
                prestadorServicos = prestadorServicosFacade
                    .buscarPrestadorServicosPorIdPessoa(
                        Optional.ofNullable((ContratoFP) carregarObjeto(registroESocial))
                            .map(ContratoFP::getMatriculaFP)
                            .map(MatriculaFP::getPessoa)
                            .map(Pessoa::getId)
                            .orElse(null)
                    );
                s2190Service.enviarS2190(registroESocial.getEmpregador(), (ContratoFP) carregarObjeto(registroESocial), prestadorServicos);
                break;
            case S2205:
                s2205Service.enviarS2205(registroESocial.getEmpregador(), (ContratoFP) carregarObjeto(registroESocial));
                break;
            case S2206:
                s2206Service.enviarS2206(registroESocial.getEmpregador(), (ContratoFP) carregarObjeto(registroESocial), configuracaoRHFacade.getSistemaFacade().getDataOperacao(), configuracaoRHFacade.getSistemaFacade().getDataOperacao());
                break;
            case S2230:
                Object instanciaS2230 = carregarObjetoEspecifico(registroESocial, Afastamento.class, ConcessaoFeriasLicenca.class);
                Afastamento afastamento;
                if (instanciaS2230 instanceof ConcessaoFeriasLicenca) {
                    afastamento = montarAfastamento((ConcessaoFeriasLicenca) instanciaS2230);
                } else {
                    afastamento = (Afastamento) instanciaS2230;
                }
                prestadorServicos = prestadorServicosFacade
                    .buscarPrestadorServicosPorIdPessoa(
                        Optional.ofNullable(afastamento)
                            .map(Afastamento::getContratoFP)
                            .map(ContratoFP::getMatriculaFP)
                            .map(MatriculaFP::getPessoa)
                            .map(Pessoa::getId)
                            .orElse(null));
                s2230Service.enviarS2230(registroESocial.getEmpregador(), afastamento, prestadorServicos);
                break;
            case S2250:
                s2250Service.enviarS2250(registroESocial.getEmpregador(), (AvisoPrevio) carregarObjeto(registroESocial));
                break;
            case S2298:
                s2298Service.enviarS2298(registroESocial.getEmpregador(), (Reintegracao) carregarObjeto(registroESocial));
                break;
            case S2299:
                s2299Service.enviarS2299(registroESocial.getEmpregador(), (ExoneracaoRescisao) carregarObjeto(registroESocial));
                break;
            case S2300:
                s2300Service.enviarS2300(registroESocial.getEmpregador(), (PrestadorServicos) carregarObjeto(registroESocial));
                break;
            case S2399:
                prestadorServicos = prestadorServicosFacade
                    .buscarPrestadorServicosPorIdPessoa(
                        Optional.ofNullable((ContratoFP) carregarObjeto(registroESocial))
                            .map(ContratoFP::getMatriculaFP)
                            .map(MatriculaFP::getPessoa)
                            .map(Pessoa::getId)
                            .orElse(null)
                    );
                s2399Service.enviarS2399(registroESocial.getEmpregador(), (ExoneracaoRescisao) carregarObjeto(registroESocial), prestadorServicos);
                break;
            default:
        }
        try {
            atualizarStatusAndSalvar(registroESocial);
            NotificacaoService.getService().marcarNotificaoComoLidaPorIdDoLink(registroESocial.getId());
            agendarAtualizacao(2);
        } catch (SchedulerException e) {
            logger.error("erro ao agendar atualização: ", e);
        }
    }


    private Afastamento montarAfastamento(ConcessaoFeriasLicenca ferias) {
        Afastamento afastamento = new Afastamento();
        afastamento.setId(ferias.getId());
        afastamento.setContratoFP(ferias.getPeriodoAquisitivoFL().getContratoFP());
        afastamento.setInicio(ferias.getInicioVigencia());
        afastamento.setTermino(ferias.getFimVigencia());
        afastamento.setTotalDias(ferias.getDias());
        afastamento.setTipoAfastamentoESocial(TipoAfastamentoESocial.GOZO_FERIAS_OU_RECESSO);
        return afastamento;
    }


    private void atualizarStatusAndSalvar(RegistroESocial registroESocial) {
        registroESocial.setSituacao(SituacaoESocial.ENVIADO_AGUARDANDO_PROCESSAMENTO);
        super.save(registroESocial);
    }

    public Object carregarObjeto(RegistroESocial registroESocial) {
        return entityManager.find(registroESocial.getTipoClasse().getClasse(), registroESocial.getIdentificador());
    }

    private Object carregarObjetoEspecifico(RegistroESocial registroESocial, Class... classes) {
        Object retorno;
        for (Class aClass : classes) {
            try {
                retorno = entityManager.find(aClass, registroESocial.getIdentificador());
                if (retorno != null) {
                    return retorno;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug("Classe não encontrada ");
            }
        }
        return null;
    }

    public void prepararEnvioEmailsPrimeiraFase() {
        logger.info("Notificação por E-mail Primeira Fase E-social");

        String banco = configuracaoEmpregadorESocialFacade.getSistemaFacade().getUsuarioBancoDeDados();

        if (PRODUCAO.equals(banco)) {
            List<NotificacaoEmailEsocial> notificacoes = getNotificacoesEmail();
            if (notificacoes != null && !notificacoes.isEmpty()) {
                for (NotificacaoEmailEsocial notificacaoEmailEsocial : notificacoes) {
                    if (notificacaoEmailEsocial.getEventoS1010()) {
                        logger.info("Buscando inconsistências da primeira fase do e-social para " + notificacaoEmailEsocial.getEmpregador());
                        String emails = "";
                        emails = recuperarEmailsParaEnvio(notificacaoEmailEsocial, emails);
                        if (emails != null) {
                            enviarEmailS1010(notificacaoEmailEsocial, emails);
                        }
                    }
                }
            }
        }
    }


    @Transactional(timeout = 70000, propagation = Propagation.REQUIRES_NEW)
    public void prepararEnvioEmailsSegundaFase() {
        logger.info("Notificação por E-mail Segunda Fase E-social");
        String banco = configuracaoEmpregadorESocialFacade.getSistemaFacade().getUsuarioBancoDeDados();

        if (PRODUCAO.equals(banco)) {
            List<NotificacaoEmailEsocial> notificacoes = getNotificacoesEmail();
            if (notificacoes != null && !notificacoes.isEmpty()) {
                for (NotificacaoEmailEsocial notificacaoEmailEsocial : notificacoes) {
                    if (notificacaoEmailEsocial.getEventoS2200()) {
                        logger.info("Buscando inconsistências da segunda fase do e-social para " + notificacaoEmailEsocial.getEmpregador());
                        String emails = "";
                        emails = recuperarEmailsParaEnvio(notificacaoEmailEsocial, emails);
                        if (emails != null) {
                            enviarEmailS2200(notificacaoEmailEsocial, emails);
                        }
                    }
                }
            }
        }
    }

    private List<NotificacaoEmailEsocial> getNotificacoesEmail() {
        return configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().getNotificacaoEmailEsocialFacade().lista();
    }

    private static String recuperarEmailsParaEnvio(NotificacaoEmailEsocial notificacaoEmailEsocial, String emails) {
        StringBuilder emailsBuilder = new StringBuilder(emails);
        if (notificacaoEmailEsocial.getUsuarios() != null && !notificacaoEmailEsocial.getUsuarios().isEmpty()) {
            for (UsuarioEmailEsocial usuario : notificacaoEmailEsocial.getUsuarios()) {
                if (!Strings.isNullOrEmpty(usuario.getUsuarioSistema().getPessoaFisica().getEmail()) && !usuario.getUsuarioSistema().getExpira()) {
                    emailsBuilder.append(usuario.getUsuarioSistema().getPessoaFisica().getEmail());
                    emailsBuilder.append(", ");
                }
            }
            return emailsBuilder.toString();
        } else {
            return null;
        }
    }

    private void enviarEmailS1010(NotificacaoEmailEsocial notificacaoEmailEsocial, String email) {
        List<EventoFP> eventos = configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().
            buscarEventoFPParaEnvioEsocial(notificacaoEmailEsocial.getEmpregador(), null,
                true, new Date(), null);
        Set<EventoFP> eventosSemConfiguracaoNecessaria = configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().
            getNotificacaoEmailEsocialFacade().buscarEventoFPSemconfiguracaoEmpregador(notificacaoEmailEsocial.getEmpregador());
        StringBuilder conteudo = new StringBuilder();
        String assunto = montarAssuntoEmail(notificacaoEmailEsocial, EVENTO_S1010);
        if (eventos != null && !eventos.isEmpty()) {

            conteudo.append("<p>Os seguintes eventos<strong> n&atilde;o foram enviados </strong>para a base do e-social, fa&ccedil;a os envios para que n&atilde;o haja inconsistências futuras.</p>\n" +
                "\n" +
                "<p>S&atilde;o eles:</p>\n" +
                "\n" +
                "<ul>\n");

            for (EventoFP evento : eventos) {
                conteudo.append("\t<li>").append(evento.toString()).append("</li>\n");
            }
            conteudo.append("</ul>\n ");

        }
        if (eventosSemConfiguracaoNecessaria != null && !eventosSemConfiguracaoNecessaria.isEmpty()) {
            conteudo.append("<p>Os seguintes eventos <strong>NÃO possuem</strong> configuração para o empregador, realize a configuração do EventoFP e envie ao e-social.</p>\n");
            for (EventoFP eventoSemConfiguracao : eventosSemConfiguracaoNecessaria) {
                conteudo.append("\t<li>").append(eventoSemConfiguracao.toString()).append("</li>\n");
            }
        }
        conteudo.append("<p>Os n&atilde;o envios desses eventos podem ocorrer <span style=\"color:#e74c3c\"><strong>problemas</strong></span> nos eventos de pagamento (S-1200, S-1202, S-1210)</p>");

        getRodapeEmail(conteudo);

        if ((eventos != null && !eventos.isEmpty()) || (eventosSemConfiguracaoNecessaria != null && !eventosSemConfiguracaoNecessaria.isEmpty())) {
            configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().getNotificacaoEmailEsocialFacade().enviarEmail(conteudo.toString(), assunto, email); //TODO descomentar
            logger.info("ENVIANDO EMAIL S1010 " + notificacaoEmailEsocial.getEmpregador());
        }
    }

    private void enviarEmailS2200(NotificacaoEmailEsocial notificacaoEmailEsocial, String email) {
        List<ContratoFP> contratos = buscarContratoFPNaoEnviadoAoEsocial(notificacaoEmailEsocial);
        StringBuilder conteudo = new StringBuilder();
        String assunto = montarAssuntoEmail(notificacaoEmailEsocial, EVENTO_S2200);

        if (contratos != null && !contratos.isEmpty()) {

            conteudo.append("<p style=\"text-align:justify\">Os seguintes servidores <span style=\"color:#e74c3c\"><strong>n&atilde;o " +
                "foram enviados</strong></span> ao e-social, realize os envios para o evento S-2200. Caso o servidor j&aacute; tenha sido exonerado" +
                " &eacute; necess&aacute;rio realizar o seu envio e logo ap&oacute;s o <strong>processamento com sucesso </strong>" +
                "enviar o evento de desligamento (S-2299).</p>");

            conteudo.append("<table border=\"1\" cellpadding=\"0\" cellspacing=\"1\" style=\"width:800px\">\n" +
                "<thead>\n" +
                "<tr>\n" +
                "<th scope=\"col\" style=\"width:530px\">Servidor</th>\n" +
                "<th scope=\"col\" style=\"width:117px\">Inicio da vigência</th>\n" +
                "<th scope=\"col\" style=\"width:117px\">Final da vigência</th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "<tbody>\n");

            for (ContratoFP contrato : contratos) {
                conteudo.append("<tr>\n" +
                    "<td style=\"width:600px\"> ".concat(contrato.toString()).concat("</td>\n") +
                    "<td style=\"text-align:center; width:117px\"> ".concat(DataUtil.getDataFormatada(contrato.getInicioVigencia())).concat("</td>\n") +
                    "<td style=\"text-align:center; width:117px\"> ".concat(DataUtil.getDataFormatada(contrato.getFinalVigencia())).concat("</td>\n") +
                    "</tr>");
            }
            conteudo.append("</tbody>\n" +
                "</table> ");

            getRodapeEmail(conteudo);

        }

        if (contratos != null && !contratos.isEmpty()) {
            logger.info("ENVIANDO EMAIL S2200 " + notificacaoEmailEsocial.getEmpregador());
            configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().getNotificacaoEmailEsocialFacade().enviarEmail(conteudo.toString(), assunto, email);
        }
    }

    private static void getRodapeEmail(StringBuilder conteudo) {
        conteudo.append("<p>&nbsp;</p>\n" +
            "\n" +
            "<p style=\"text-align: center;\">Sistema RBWeb</p>\n" +
            "\n" +
            "<p style=\"text-align: center;\">Município de Rio Branco</p>");
    }

    private List<ContratoFP> buscarContratoFPNaoEnviadoAoEsocial(NotificacaoEmailEsocial notificacaoEmailEsocial) {
        if (notificacaoEmailEsocial.getEmpregador().getItemConfiguracaoEmpregadorESocial() == null
            || notificacaoEmailEsocial.getEmpregador().getItemConfiguracaoEmpregadorESocial().isEmpty()) {
            return null;

        }
        return configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().
            buscarContratoFPPorTipoArquivoSemEnvioEsocial(notificacaoEmailEsocial.getEmpregador(), true,
                TipoArquivoESocial.S2200, notificacaoEmailEsocial.getEmpregador().getDataInicioObrigatoriedadeFase2());
    }

    private static String montarAssuntoEmail(NotificacaoEmailEsocial notificacaoEmailEsocial, String tipoEvento) {
        return notificacaoEmailEsocial.getEmpregador() + tipoEvento;
    }

    private void processarEventosS3000(RegistroESocial registroExclusao) {
        configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().removerRegistroEsocial(registroExclusao);
    }


    public String getDescricaoEventoPagamento(String identificadorWP) {
        VinculoFP vinculoFp = configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().getVinculoFPFichaFinanceiraFP(identificadorWP);
        if (vinculoFp != null)
            return vinculoFp.toString();

        PrestadorServicos prestador = configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().getPrestadorServicosFichaRPA(identificadorWP);
        if (prestador != null)
            return prestador.toString();

        return "";
    }

    public String getDescricaoEventoEsocialVinculoFP(String identificadorWP, String tabela, String coluna) {
        VinculoFP vinculoFp = configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().
            getVinculoFpPorTabelaAndColuna(identificadorWP, tabela, coluna);
        if (vinculoFp != null) {
            return vinculoFp.toString();
        }
        return "";
    }

    public String getDescricaoEventoFP(String identificadorWP) {
        EventoFP eventoFP = configuracaoEmpregadorESocialFacade.getEventoFPFacade().recuperar(Long.parseLong(identificadorWP));
        if (eventoFP != null)
            return eventoFP.getCodigoAndDescricao();

        return "";
    }

    public void atualizarDescricoesEventosEsocial() {
        log.debug("Atualizar Descrição e-social eventos pagamento");
        try {
            List<RegistroESocial> eventos = configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().getRegistroEsocialPagamentosSemDescricao();
            List<EventoESocialDTO> eventosDTO = Lists.newArrayList();
            if (eventos != null) {
                log.debug("Quantidade de eventos de pagamentos para atualizar " + eventos.size());
                for (RegistroESocial evento : eventos) {
                    evento.setDescricao(getDescricaoEventoPagamento(evento.getIdentificadorWP()));
                    configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().atualizarDescricaoRegistroEsocial(evento);
                    eventosDTO.add(RegistroESocial.registroESocialToEventoESocialDTOSomenteID(evento));
                }
                eSocialService.enviarEventosAtualizarDescricao(eventosDTO);
            }
        } catch (Exception e) {
            log.debug("Integrar eventos do E-Social - Não foi possível estabelecer conexão");
        }
    }

    public void atualizarDescricoesEventosEsocialVinculoFP() {
        log.debug("Atualizar Descrição e-social eventos com VinculoFP");
        try {
            List<RegistroESocial> eventos = configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().getRegistroEsocialComVinculoFP();
            List<EventoESocialDTO> eventosDTO = Lists.newArrayList();
            if (eventos != null) {
                log.debug("Quantidade de eventos vinculofp para atualizar " + eventos.size());
                for (RegistroESocial evento : eventos) {
                    String tabela = getTabelaEvento(evento);
                    String coluna = getColunaEvento(evento);
                    evento.setDescricao(getDescricaoEventoEsocialVinculoFP(evento.getIdentificadorWP(), tabela, coluna));
                    configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().atualizarDescricaoRegistroEsocial(evento);
                    eventosDTO.add(RegistroESocial.registroESocialToEventoESocialDTOSomenteID(evento));
                }
                eSocialService.enviarEventosAtualizarDescricao(eventosDTO);
            }
        } catch (Exception e) {
            log.debug("Integrar eventos do E-Social - Não foi possível estabelecer conexão");
        }
    }


    public void atualizarDescricoesEventosEsocialS1010() {
        log.debug("Atualizar Descrição e-social evento S1010");
        try {
            List<RegistroESocial> eventos = configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().getRegistroEsocialEventoFPSemDescricao();
            List<EventoESocialDTO> eventosDTO = Lists.newArrayList();
            if (eventos != null) {
                log.debug("Quantidade de eventos para atualizar " + eventos.size());
                for (RegistroESocial evento : eventos) {
                    evento.setDescricao(getDescricaoEventoFP(evento.getIdentificadorWP()));
                    configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().salvar(evento);
                    eventosDTO.add(RegistroESocial.registroESocialToEventoESocialDTOSomenteID(evento));
                }
                eSocialService.enviarEventosAtualizarDescricao(eventosDTO);
            }
        } catch (Exception e) {
            log.debug("Integrar eventos do E-Social - Não foi possível estabelecer conexão");
        }
    }

    private String getTabelaEvento(RegistroESocial registro) {
        switch (registro.getTipoArquivoESocial()) {
            case S2200:
                return "VinculoFP";
            case S2299:
                return "ExoneracaoRescisao";
            default:
                return null;
        }
    }

    private String getColunaEvento(RegistroESocial registro) {
        switch (registro.getTipoArquivoESocial()) {
            case S2200:
                return "id";
            case S2299:
                return "vinculofp_id";
            default:
                return null;
        }
    }
}
