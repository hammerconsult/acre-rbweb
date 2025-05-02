package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.entidades.comum.TemplateEmail;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.entidades.comum.trocatag.TrocaTagRegistroExigenciaAlvaraImediato;
import br.com.webpublico.enums.SituacaoAlvaraImediato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.comum.FormularioFacade;
import br.com.webpublico.negocios.comum.TemplateEmailFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ParametroRegularizacaoFacade;
import br.com.webpublico.util.EmailService;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class SolicitacaoAlvaraImediatoFacade extends AbstractFacade<SolicitacaoAlvaraImediato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ProcRegularizaConstrucaoFacade procRegularizaConstrucaoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private ParametroAlvaraImediatoFacade parametroAlvaraImediatoFacade;
    @EJB
    private ValorPossivelFacade valorPossivelFacade;
    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    @EJB
    private TemplateEmailFacade templateEmailFacade;
    @EJB
    private FormularioFacade formularioFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private Boolean enviandoAlvarasImediatos = Boolean.FALSE;
    @EJB
    private ParametroRegularizacaoFacade parametroRegularizacaoFacade;

    public SolicitacaoAlvaraImediatoFacade() {
        super(SolicitacaoAlvaraImediato.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public FormularioFacade getFormularioFacade() {
        return formularioFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public ParametroRegularizacaoFacade getParametroRegularizacaoFacade() {
        return parametroRegularizacaoFacade;
    }

    @Override
    public SolicitacaoAlvaraImediato recuperar(Object id) {
        SolicitacaoAlvaraImediato solicitacao = super.recuperar(id);
        Hibernate.initialize(solicitacao.getHistoricos());
        if (solicitacao.getRespostaFormulario() != null) {
            Hibernate.initialize(solicitacao.getRespostaFormulario().getRespostaFormularioCampoList());
            Hibernate.initialize(solicitacao.getRespostaFormulario().getFormulario().getFormularioCampoList());
            for (FormularioCampo formularioCampo : solicitacao.getRespostaFormulario().getFormulario().getFormularioCampoList()) {
                Hibernate.initialize(formularioCampo.getFormularioCampoOpcaoList());
            }
        }
        Hibernate.initialize(solicitacao.getCadastroImobiliario().getLote().getTestadas());
        Hibernate.initialize(solicitacao.getExigenciasAlvaraImediato());
        if (solicitacao.getExigenciasAlvaraImediato() != null) {
            for (ExigenciaAlvaraImediato exigenciaAlvaraImediato : solicitacao.getExigenciasAlvaraImediato()) {
                Hibernate.initialize(exigenciaAlvaraImediato.getCampos());
                if (exigenciaAlvaraImediato.getRespostaFormulario() != null) {
                    Hibernate.initialize(exigenciaAlvaraImediato.getRespostaFormulario().getRespostaFormularioCampoList());
                }
            }
        }
        return solicitacao;
    }

    public AlvaraConstrucao aprovarSolicitacao(SolicitacaoAlvaraImediato solicitacao, UsuarioSistema usuarioAprovacao) {
        solicitacao.setUsuarioEfetivacao(usuarioAprovacao);
        solicitacao.setDataEfetivacao(new Date());
        solicitacao.adicionarHistorico(new Date(), SituacaoAlvaraImediato.APROVADO);
        ProcRegularizaConstrucao procRegularizaConstrucao = registrarProcRegularizaConstrucao(solicitacao, usuarioAprovacao);
        solicitacao.setProcRegularizaConstrucao(procRegularizaConstrucao);
        AlvaraConstrucao alvaraConstrucao = registrarAlvaraConstrucao(solicitacao, procRegularizaConstrucao);
        salvar(solicitacao);
        return alvaraConstrucao;
    }

    @Asynchronous
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Future<AlvaraConstrucao> aprovarSolicitacaoAsync(SolicitacaoAlvaraImediato solicitacao, UsuarioSistema usuarioAprovacao) {
        return new AsyncResult(aprovarSolicitacao(solicitacao, usuarioAprovacao));
    }

    public AlvaraConstrucao gerarCalculoAlvaraConstrucao(AlvaraConstrucao alvaraConstrucao) {
        return alvaraConstrucaoFacade.gerarCalculo(alvaraConstrucao, true);
    }

    public void enviarDAMsPorEmail(String email,
                                   AlvaraConstrucao alvaraConstrucao) {
        alvaraConstrucaoFacade.enviarEmail(alvaraConstrucao, "Em anexo", email);
    }

    @Asynchronous
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Future<List<FacesMessage>> gerarCalculoAlvaraConstrucaoAsync(AlvaraConstrucao alvaraConstrucao) {
        try {
            gerarCalculoAlvaraConstrucao(alvaraConstrucao);
        } catch (ValidacaoException ve) {
            return new AsyncResult<>(ve.getAllMensagens());
        } catch (Exception e) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoRealizada(e.getMessage());
            return new AsyncResult<>(ve.getAllMensagens());
        }
        return new AsyncResult(Lists.newArrayList());
    }

    private AlvaraConstrucao registrarAlvaraConstrucao(SolicitacaoAlvaraImediato solicitacao,
                                                       ProcRegularizaConstrucao procRegularizaConstrucao) {
        ParametroAlvaraImediato parametroAlvaraImediato = parametroAlvaraImediatoFacade
                .findByExercicio(solicitacao.getExercicio());
        AlvaraConstrucao alvaraConstrucao = new AlvaraConstrucao();
        alvaraConstrucao.setProcRegularizaConstrucao(procRegularizaConstrucao);
        alvaraConstrucao.setExercicio(procRegularizaConstrucao.getExercicio());
        alvaraConstrucao.setCodigo(alvaraConstrucaoFacade.buscarUltimoCodigoMaisUmPorExercicio(alvaraConstrucao.getExercicio()));
        alvaraConstrucao.setSituacao(AlvaraConstrucao.Situacao.EM_ABERTO);
        alvaraConstrucao.setUsuarioIncluiu(procRegularizaConstrucao.getUsuarioIncluiu());
        alvaraConstrucao.setDataExpedicao(new Date());
        alvaraConstrucao.setResponsavelServico(procRegularizaConstrucao.getResponsavelProjeto());
        alvaraConstrucao.setConstrucaoAlvara(criarConstrucaoAlvara(parametroAlvaraImediato, solicitacao));
        addServicoAlvaraConstrucao(solicitacao, parametroAlvaraImediato, alvaraConstrucao);
        return alvaraConstrucaoFacade.salvarRetornando(alvaraConstrucao);
    }

    private void addServicoAlvaraConstrucao(SolicitacaoAlvaraImediato solicitacao,
                                            ParametroAlvaraImediato parametroAlvaraImediato,
                                            AlvaraConstrucao alvaraConstrucao) {
        ServicosAlvaraConstrucao servicosAlvaraConstrucao = new ServicosAlvaraConstrucao();
        servicosAlvaraConstrucao.setAlvaraConstrucao(alvaraConstrucao);
        servicosAlvaraConstrucao.setServicoConstrucao(parametroAlvaraImediato.getServicoConstrucao());
        servicosAlvaraConstrucao.setItemServicoConstrucao(solicitacao.getTipoObra());
        servicosAlvaraConstrucao.setArea(solicitacao.getAreaTotal());
        alvaraConstrucao.getServicos().add(servicosAlvaraConstrucao);
    }

    private ConstrucaoAlvara criarConstrucaoAlvara(ParametroAlvaraImediato parametroAlvaraImediato,
                                                   SolicitacaoAlvaraImediato solicitacaoAlvaraImediato) {
        CadastroImobiliario cadastroImobiliario = cadastroImobiliarioFacade.recuperar(solicitacaoAlvaraImediato.getCadastroImobiliario().getId());
        ConstrucaoAlvara construcaoAlvara = null;
        if (cadastroImobiliario.getConstrucoesAtivas().size() == 1) {
            Construcao construcao = cadastroImobiliario.getConstrucoesAtivas().get(0);
            construcaoAlvara = new ConstrucaoAlvara(construcao);
        } else if (cadastroImobiliario.getConstrucoesAtivas().isEmpty()) {
            construcaoAlvara = new ConstrucaoAlvara();
            construcaoAlvara.setDescricao(solicitacaoAlvaraImediato.getDescricaoArea());
            construcaoAlvara.setAreaConstruida(solicitacaoAlvaraImediato.getAreaConstruir());
            construcaoAlvara.setQuantidadePavimentos(solicitacaoAlvaraImediato.getNumeroPavimentos());
            construcaoAlvara.setCaracteristicas(new ArrayList<>());
            addCaracteristicasAlvaraConstrucao(construcaoAlvara, parametroAlvaraImediato.getTipoImovel(),
                    solicitacaoAlvaraImediato.getTipoImovel());
            addCaracteristicasAlvaraConstrucao(construcaoAlvara, parametroAlvaraImediato.getUtilizacaoImovel(),
                    solicitacaoAlvaraImediato.getUtilizacaoImovel());
        }
        return construcaoAlvara;
    }

    private void addCaracteristicasAlvaraConstrucao(ConstrucaoAlvara construcaoAlvara,
                                                    Atributo atributo,
                                                    String valor) {
        CaracteristicasAlvaraConstrucao caracteristicasAlvaraConstrucao = new CaracteristicasAlvaraConstrucao();
        caracteristicasAlvaraConstrucao.setConstrucaoAlvara(construcaoAlvara);
        caracteristicasAlvaraConstrucao.setAtributo(atributo);
        ValorAtributo valorAtributo = new ValorAtributo();
        valorAtributo.setAtributo(atributo);
        valorAtributo.setValorDiscreto(valorPossivelFacade.findByAtributoAndValor(atributo, valor));
        caracteristicasAlvaraConstrucao.setValorAtributo(valorAtributo);
        if (construcaoAlvara.getCaracteristicas() == null) {
            construcaoAlvara.setCaracteristicas(new ArrayList<>());
        }
        construcaoAlvara.getCaracteristicas().add(caracteristicasAlvaraConstrucao);
    }

    private ProcRegularizaConstrucao registrarProcRegularizaConstrucao(SolicitacaoAlvaraImediato solicitacao,
                                                                       UsuarioSistema usuarioSistema) {
        ProcRegularizaConstrucao procRegularizaConstrucao = new ProcRegularizaConstrucao();
        procRegularizaConstrucao.setExercicio(solicitacao.getExercicio());
        procRegularizaConstrucao.setCadastroImobiliario(solicitacao.getCadastroImobiliario());
        procRegularizaConstrucao.setDataCriacao(new Date());
        procRegularizaConstrucao.setUsuarioIncluiu(usuarioSistema);
        procRegularizaConstrucao.setDataInicioObra(solicitacao.getDataInicio());
        procRegularizaConstrucao.setDataFimObra(solicitacao.getDataConclusao());
        procRegularizaConstrucao.setMatriculaINSS(solicitacao.getCei());
        procRegularizaConstrucao.setAnoProtocolo(solicitacao.getAnoProtocolo() != null ? solicitacao.getAnoProtocolo().toString() : null);
        procRegularizaConstrucao.setNumeroProtocolo(solicitacao.getNumeroProtocolo());
        procRegularizaConstrucao.setResponsavelExecucao(solicitacao.getResponsavelExecucao());
        procRegularizaConstrucao.setArtRrtResponsavelExecucao(solicitacao.getArtRrtResponsavelExecucao());
        procRegularizaConstrucao.setAreaExistente(solicitacao.getAreaExistente());
        procRegularizaConstrucao.setResponsavelProjeto(solicitacao.getAutorProjeto());
        procRegularizaConstrucao.setArtRrtAutorProjeto(solicitacao.getArtRrtAutorProjeto());
        procRegularizaConstrucao.setSituacao(ProcRegularizaConstrucao.Situacao.AGUARDANDO_ALVARA_CONSTRUCAO);
        procRegularizaConstrucao.setObservacao("Processo gerado automáticamente pela aprovação da solicitação de alvará imediato " +
                solicitacao.getCodigo() + "/" + solicitacao.getExercicio().getAno() + ".");
        return procRegularizaConstrucaoFacade.salvarRetornando(procRegularizaConstrucao);
    }

    public AlvaraConstrucao recuperarAlvaraConstrucao(SolicitacaoAlvaraImediato solicitacao) {
        return alvaraConstrucaoFacade.buscarUltimoAlvaraParaProcesso(true,
                solicitacao.getProcRegularizaConstrucao(),
                AlvaraConstrucao.Situacao.values());
    }

    public void registrarExigencia(ExigenciaAlvaraImediato exigenciaAlvaraImediato) {
        exigenciaAlvaraImediato = em.merge(exigenciaAlvaraImediato);
        exigenciaAlvaraImediato.setSolicitacao(recuperar(exigenciaAlvaraImediato.getSolicitacao().getId()));
        exigenciaAlvaraImediato.getSolicitacao().adicionarHistorico(new Date(), SituacaoAlvaraImediato.EM_EXIGENCIA);
        exigenciaAlvaraImediato.setSolicitacao(em.merge(exigenciaAlvaraImediato.getSolicitacao()));
        enviarEmailRegistroExigencia(exigenciaAlvaraImediato);

    }

    public void enviarEmailRegistroExigencia(ExigenciaAlvaraImediato exigenciaAlvaraImediato) {
        TemplateEmail templateEmail = templateEmailFacade.findByTipoTemplateEmail(TipoTemplateEmail.REGISTRO_EXIGENCIA_ALVARA_IMEDIATO);
        if (templateEmail == null) {
            logger.error("Template de e-mail não configurado. {}", TipoTemplateEmail.REGISTRO_EXIGENCIA_ALVARA_IMEDIATO);
            return;
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        ConfiguracaoEmail configuracaoEmail = configuracaoEmailFacade.recuperarUtilmo();
        EmailService.getInstance().enviarEmail(exigenciaAlvaraImediato.getSolicitacao().getEmail(),
                templateEmail.getAssunto(),
                new TrocaTagRegistroExigenciaAlvaraImediato(configuracaoTributario, exigenciaAlvaraImediato)
                        .trocarTags(templateEmail.getConteudo()));
    }

    public byte[] gerarDAMs(AlvaraConstrucao alvaraConstrucao) throws Exception {
        List<DAM> dams = alvaraConstrucaoFacade.recuperarDAMS(alvaraConstrucao);
        ImprimeDAM imprimeDAM = new ImprimeDAM();
        return imprimeDAM.gerarByteImpressaoDamUnicoViaApi(dams);
    }

    public SolicitacaoAlvaraImediato findByProcRegularizaConstrucao(ProcRegularizaConstrucao procRegularizaConstrucao) {
        List resultList = em.createQuery(" select s from SolicitacaoAlvaraImediato s " +
                        " where s.procRegularizaConstrucao = :processo ")
                .setParameter("processo", procRegularizaConstrucao)
                .setMaxResults(1)
                .getResultList();
        return !resultList.isEmpty() ? (SolicitacaoAlvaraImediato) resultList.get(0) : null;
    }

    public List<AlvaraConstrucao> buscarAlvaraImediatoAguardandoEnvio() {
        return em.createQuery(" select a from SolicitacaoAlvaraImediato sa " +
                        " inner join sa.procRegularizaConstrucao pr " +
                        " inner join pr.alvarasDeConstrucao a " +
                        " where a.situacao = :finalizado " +
                        "   and coalesce(sa.alvaraEnviadoPorEmail, 0) = :nao_enviado ")
                .setParameter("finalizado", AlvaraConstrucao.Situacao.FINALIZADO)
                .setParameter("nao_enviado", Boolean.FALSE)
                .getResultList();
    }

    public void enviarAlvarasImediatoPorEmail() {
        if (enviandoAlvarasImediatos) {
            return;
        }
        try {
            enviandoAlvarasImediatos = Boolean.TRUE;
            List<AlvaraConstrucao> alvaras = buscarAlvaraImediatoAguardandoEnvio();
            for (AlvaraConstrucao alvaraConstrucao : alvaras) {
                SolicitacaoAlvaraImediato solicitacao = findByProcRegularizaConstrucao(alvaraConstrucao.getProcRegularizaConstrucao());
                if (solicitacao != null) {
                    alvaraConstrucaoFacade.enviarEmail(alvaraConstrucao, "Em anexo", solicitacao.getEmail());
                    solicitacao.setAlvaraEnviadoPorEmail(Boolean.TRUE);
                    salvar(solicitacao);
                }
            }
        } catch (Exception e) {
            logger.debug("Erro ao enviar alvara imediato aguardando envio.", e);
            logger.error("Erro ao enviar alvara imediato aguardando envio.");
        } finally {
            enviandoAlvarasImediatos = Boolean.FALSE;
        }
    }

    public void designar(SolicitacaoAlvaraImediato solicitacaoAlvaraImediato, UsuarioSistema usuarioCorrente) {
        solicitacaoAlvaraImediato.setAnalistaResponsavel(usuarioCorrente);
        solicitacaoAlvaraImediato.setSituacao(SituacaoAlvaraImediato.DESIGNADO);
        solicitacaoAlvaraImediato.adicionarHistorico(new Date(), SituacaoAlvaraImediato.DESIGNADO);
        salvar(solicitacaoAlvaraImediato);
    }

    public void deferir(SolicitacaoAlvaraImediato solicitacaoAlvaraImediato) {
        solicitacaoAlvaraImediato.setSituacao(SituacaoAlvaraImediato.DEFERIDO);
        solicitacaoAlvaraImediato.adicionarHistorico(new Date(), SituacaoAlvaraImediato.DEFERIDO);
        salvar(solicitacaoAlvaraImediato);
    }
}
