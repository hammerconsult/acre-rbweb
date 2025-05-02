package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.*;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabiteseClassesConstrucaoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ItemServicoConstrucaoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ParametroRegularizacaoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ServicoConstrucaoFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EmailService;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
public class AlvaraConstrucaoFacade extends CalculoExecutorDepoisDePagar<AlvaraConstrucao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProcRegularizaConstrucaoFacade procRegularizaConstrucaoFacade;
    @EJB
    private ParametroRegularizacaoFacade parametroRegularizacaoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private HabiteseClassesConstrucaoFacade habiteseClassesConstrucaoFacade;
    @EJB
    private GeraValorDividaAlvaraConstrucao geraValorDividaAlvaraConstrucao;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private ConstrucaoFacade construcaoFacade;
    @EJB
    private ItemServicoConstrucaoFacade itemServicoConstrucaoFacade;
    @EJB
    private ServicoConstrucaoFacade servicoConstrucaoFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;
    @EJB
    private HabiteseConstrucaoFacade habiteseConstrucaoFacade;
    @Resource
    private SessionContext sessionContext;

    public AlvaraConstrucaoFacade() {
        super(AlvaraConstrucao.class);
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    @Override
    public AlvaraConstrucao recuperar(Object id) {
        return inicializar(super.recuperar(id));
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return inicializar((AlvaraConstrucao) super.recuperar(entidade, id));
    }

    private AlvaraConstrucao inicializar(AlvaraConstrucao alvaraConstrucao) {
        em.refresh(alvaraConstrucao);
        Hibernate.initialize(alvaraConstrucao.getServicos());
        for (ServicosAlvaraConstrucao servicoConstrucao : alvaraConstrucao.getServicos()) {
            Hibernate.initialize(servicoConstrucao.getServicoConstrucao());
        }
        Hibernate.initialize(alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getConstrucoes());
        Hibernate.initialize(alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario());
        Hibernate.initialize(alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getPropriedade());
        Hibernate.initialize(alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getLote().getTestadas());
        Hibernate.initialize(alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getLote().getTestadaPrincipal().getFace());
        Hibernate.initialize(alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getLote().getTestadaPrincipal().getFace().getLogradouroBairro());
        Hibernate.initialize(alvaraConstrucao.getProcessoCalcAlvaConstHabi());
        if (alvaraConstrucao.getProcessoCalcAlvaConstHabi() != null) {
            Hibernate.initialize(alvaraConstrucao.getProcessoCalcAlvaConstHabi().getCalculos());
            for (CalculoAlvaraConstrucaoHabitese calculo : alvaraConstrucao.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese()) {
                Hibernate.initialize(calculo);
                Hibernate.initialize(calculo.getItensCalculo());
            }
        }
        Hibernate.initialize(alvaraConstrucao.getProcessoCalcAlvaConstVisto());
        if (alvaraConstrucao.getProcessoCalcAlvaConstVisto() != null) {
            Hibernate.initialize(alvaraConstrucao.getProcessoCalcAlvaConstVisto().getCalculos());
            for (CalculoAlvaraConstrucaoHabitese calculo : alvaraConstrucao.getProcessoCalcAlvaConstVisto().getCalculosAlvaraConstrucaoHabitese()) {
                Hibernate.initialize(calculo);
                Hibernate.initialize(calculo.getItensCalculo());
            }
        }
        if (alvaraConstrucao.getConstrucaoAlvara() != null) {
            Hibernate.initialize(alvaraConstrucao.getConstrucaoAlvara().getCaracteristicas());
        }
        return alvaraConstrucao;
    }

    public void enviarEmail(AlvaraConstrucao alvara, String mensagemEmail, String... emailsSeparados) throws ValidacaoException {
        try {
            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            String assunto = "Prefeitura Municipal de " + configuracaoTributario.getCidade().getNome() + " / " + configuracaoTributario.getCidade().getUf() + " - ";
            if (AlvaraConstrucao.Situacao.FINALIZADO.equals(alvara.getSituacao())) {
                assunto += "Alvará de Construção";
                atualizarDocumentoOficial(alvara);
                documentoOficialFacade.enviarEmailDocumentoOficial(emailsSeparados, alvara.getDocumentoOficial(), assunto, mensagemEmail);
            } else {
                assunto += "DAM do Alvará de Construção";
                List<DAM> dams = recuperarDAMS(alvara);
                if (dams.isEmpty()) {
                    ValidacaoException ve = new ValidacaoException();
                    ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi possível gerar um DAM para esse Alvará de Construção!");
                    ve.lancarException();
                } else {
                    ImprimeDAM imprimeDAM = new ImprimeDAM();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(imprimeDAM.gerarByteImpressaoDamUnicoViaApi(dams));
                    EmailService.getInstance().enviarEmail(emailsSeparados, assunto, mensagemEmail, outputStream);
                }
            }
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception e) {
            logger.error("Ocorreu um erro ao enviar o e-mail: {}", e);
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoRealizada(e.getMessage());
            ve.lancarException();
        }
    }

    public void lancarNotificacoesVencidos() {
        for (AlvaraConstrucao alvaraConstrucao : buscarAlvarasVencidos()) {
            criarNotificacao(alvaraConstrucao, TipoNotificacao.AVISO_ALVARA_CONSTRUCAO_VENCIDO);
        }
    }

    public void lancarNotificacoesAVencer() {
        for (AlvaraConstrucao alvaraConstrucao : buscarAlvarasAVencer()) {
            criarNotificacao(alvaraConstrucao, TipoNotificacao.AVISO_ALVARA_CONSTRUCAO_PARA_VENCER);
        }
    }

    private void criarNotificacao(AlvaraConstrucao alvaraConstrucao, TipoNotificacao tipoNotificacao) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(tipoNotificacao.getDescricao() + ": " + alvaraConstrucao.getCodigo());
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo("Alvará de Construção");
        notificacao.setTipoNotificacao(tipoNotificacao);
        notificacao.setUsuarioSistema(alvaraConstrucao.getUsuarioIncluiu());
        notificacao.setLink("/alvara-construcao/ver/" + alvaraConstrucao.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void atualizarDocumentoOficial(AlvaraConstrucao alvara) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        ParametroRegularizacao parametro = parametroRegularizacaoFacade.buscarParametroRegularizacaoPorExercicio(alvara.getExercicio());
        SolicitacaoAlvaraImediato solicitacaoAlvaraImediato = buscarSolicitacaoImediato(alvara.getProcRegularizaConstrucao());
        if (parametro == null) {
            parametro = parametroRegularizacaoFacade.buscarParametroRegularizacaoPorExercicio(sistemaFacade.getExercicioCorrente());
        }
        if (parametro == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum parâmetro de regularização encontrado para o exercício " + alvara.getExercicio().getAno());
            ve.lancarException();
        } else {
            if (solicitacaoAlvaraImediato != null && parametro.getTipoDoctoOficialAlvaraImediato() == null) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("O tipo do documento oficial do alvará imediato não está configurado no parametro de regularização");
                ve.lancarException();
            } else if (solicitacaoAlvaraImediato == null && parametro.getTipoDoctoOficial() == null) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("O tipo do documento oficial do alvará não está configurado no parametro de regularização");
                ve.lancarException();
            } else {
                try {
                    TipoDoctoOficial tipoDoc = solicitacaoAlvaraImediato != null ? parametro.getTipoDoctoOficialAlvaraImediato() : parametro.getTipoDoctoOficial();
                    alvara.setDocumentoOficial(documentoOficialFacade.gerarDocumentoAlvaraConstrucao(sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).recarregarEmNovaTransacao(alvara), tipoDoc));
                    em.merge(alvara);
                } catch (UFMException | AtributosNulosException e) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("Erro ao emitir o alvará: " + e.getMessage());
                }
            }
        }
    }

    public byte[] gerarBytesAlvara(AlvaraConstrucao alvaraConstrucao) {
        if (alvaraConstrucao.getDocumentoOficial() == null) {
            atualizarDocumentoOficial(alvaraConstrucao);
        }
        String conteudo = documentoOficialFacade.geraConteudoDocumento(alvaraConstrucao.getDocumentoOficial());
        return conteudo.getBytes(StandardCharsets.UTF_8);
    }


    public void emitirAlvara(AlvaraConstrucao alvara) throws ValidacaoException {
        if (alvara.getDocumentoOficial() == null) {
            atualizarDocumentoOficial(alvara);
        }
        documentoOficialFacade.emiteDocumentoOficial(alvara.getDocumentoOficial());
    }

    public List<AlvaraConstrucao> buscarAlvarasVencidos() {
        Query query = em.createQuery("from AlvaraConstrucao alva where dataVencimentoCartaz is not null and trunc(dataVencimentoCartaz) < :dataAtual and alva.situacao <> :situa", AlvaraConstrucao.class);
        query.setParameter("situa", AlvaraConstrucao.Situacao.FINALIZADO);
        query.setParameter("dataAtual", new Date());
        return query.getResultList();
    }

    public List<AlvaraConstrucao> buscarAlvarasAVencer() {
        Query query = em.createNativeQuery("select * from AlvaraConstrucao alva where dataVencimentoCartaz is not null and trunc(dataVencimentoCartaz) < :dataAtual + (select PRAZOCOMUNICACAOCONTRIBUINTE from PARAMETROREGULARIZACAO parametro where parametro.EXERCICIO_ID = alva.EXERCICIO_ID and ROWNUM = 1) and alva.situacao <> :situa", AlvaraConstrucao.class);
        query.setParameter("situa", AlvaraConstrucao.Situacao.FINALIZADO);
        query.setParameter("dataAtual", new Date());
        return query.getResultList();
    }

    public List<AlvaraConstrucao> buscarAlvarasParaEnvioSisObra() {
        TypedQuery<AlvaraConstrucao> query = em.createQuery("from AlvaraConstrucao alva where alva.envioSisObra = false", AlvaraConstrucao.class);
        List<AlvaraConstrucao> alvaras = query.getResultList();
        for (AlvaraConstrucao proc : alvaras) {
            inicializar(proc);
        }
        return alvaras;
    }

    public AlvaraConstrucao buscarUltimoAlvaraParaProcesso(boolean considerarVencidos, ProcRegularizaConstrucao proc, AlvaraConstrucao.Situacao... situacoes) {
        TypedQuery<AlvaraConstrucao> query = em.createQuery("from AlvaraConstrucao alva where " + "alva.procRegularizaConstrucao = :proc " + "and alva.situacao in (:situa) " + "order by dataExpedicao desc", AlvaraConstrucao.class);
        query.setParameter("proc", proc);
        if (situacoes != null && situacoes.length > 0) {
            query.setParameter("situa", Arrays.asList(situacoes));
        } else {
            query.setParameter("situa", Arrays.asList(AlvaraConstrucao.Situacao.values()));
        }
        for (AlvaraConstrucao alvara : query.getResultList()) {
            if (alvara.isVencido() && !considerarVencidos) {
                continue;
            }
            return alvara;
        }
        return null;
    }

    public List<AlvaraConstrucao> buscarAlvaras(CriteriaQuery<AlvaraConstrucao> criteria) {
        TypedQuery<AlvaraConstrucao> typedQuery = em.createQuery(criteria);
        List<AlvaraConstrucao> processos = typedQuery.getResultList();
        for (AlvaraConstrucao proc : processos) {
            inicializar(proc);
        }
        return processos;
    }

    public Long buscarUltimoCodigoMaisUmPorExercicio(Exercicio exercicio) {
        return singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(AlvaraConstrucao.class,
            "codigo", "exercicio", exercicio);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ResultadoParcela> buscarParcelas(ProcessoCalculoAlvaraConstrucaoHabitese processo) {
        if (processo != null && !processo.getCalculosAlvaraConstrucaoHabitese().isEmpty() && processo.getCalculosAlvaraConstrucaoHabitese().get(0).getId() != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, processo.getCalculosAlvaraConstrucaoHabitese().get(0).getId());
            return consultaParcela.executaConsulta().getResultados();
        } else {
            return Lists.newArrayList();
        }
    }

    public List<ResultadoParcela> buscarParcelas(AlvaraConstrucao alvaraConstrucao) {
        return buscarParcelas(alvaraConstrucao.getProcessoCalcAlvaConstHabi());
    }

    public List<ResultadoParcela> recuperarParcelas(AlvaraConstrucao alvaraConstrucao) throws ValidacaoException {
        alvaraConstrucao = sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).recarregarEmNovaTransacao(alvaraConstrucao);
        try {
            switch (alvaraConstrucao.getProcRegularizaConstrucao().getSituacao()) {
                case AGUARDANDO_ALVARA_CONSTRUCAO:
                    if (alvaraConstrucao.getProcessoCalcAlvaConstHabi() == null) {
                        alvaraConstrucao = sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).gerarCalculoAlvaraConstrucao(alvaraConstrucao, true);
                    }
                    return processoParcelamentoFacade.buscarArvoreParcelamentoPartindoDoCalculo(alvaraConstrucao.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese().get(0).getId());
                case ALVARA_CONSTRUCAO:
                    if (alvaraConstrucao.getProcessoCalcAlvaConstVisto() == null) {
                        alvaraConstrucao = sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).gerarCalculoTaxaVistoria(alvaraConstrucao, true);
                    }
                case AGUARDANDO_TAXA_VISTORIA:
                    return processoParcelamentoFacade.buscarArvoreParcelamentoPartindoDoCalculo(alvaraConstrucao.getProcessoCalcAlvaConstVisto().getCalculosAlvaraConstrucaoHabitese().get(0).getId());
            }
        } catch (Exception e) {
            e = Util.getRootCauseEJBException(e);
            if (e instanceof ValidacaoException) {
                ((ValidacaoException) e).lancarException();
            }
            new ValidacaoException().adicionarMensagemDeOperacaoNaoRealizada("Erro ao recuperar parcelas: " + e.getMessage()).lancarException();
        }
        return null;
    }


    public List<DAM> recuperarDAMS(AlvaraConstrucao alvaraConstrucao) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        List<ResultadoParcela> resultadoParcelas = recuperarParcelas(alvaraConstrucao);
        List<DAM> dams = Lists.newArrayList();
        for (ResultadoParcela parcela : resultadoParcelas) {
            DAM[] dam = {consultaDebitoFacade.getDamFacade().recuperaDAMPeloIdParcela(parcela.getIdParcela())};
            if (dam[0] != null) {
                dams.add(dam[0]);
            } else {
                ParcelaValorDivida p = new ParcelaValorDivida();
                p.setId(parcela.getIdParcela());
                try {
                    dam[0] = consultaDebitoFacade.getDamFacade().gerarDAM(p, parcela.getVencimento(), true);
                    dams.add(dam[0]);
                } catch (Exception e) {
                    logger.error("{}", e);
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Ocorreu um erro ao gerar o DAM!: " + e.getMessage());
                    ve.lancarException();
                }
            }
        }
        return dams;
    }

    public byte[] gerarBytesDAM(AlvaraConstrucao alvaraConstrucao) throws Exception {
        List<DAM> dams = recuperarDAMS(alvaraConstrucao);
        ImprimeDAM imprimeDAM = new ImprimeDAM();
        return imprimeDAM.gerarByteImpressaoDamUnicoViaApi(dams);
    }

    public void emitirDAM(AlvaraConstrucao alvara) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        List<DAM> dams = recuperarDAMS(alvara);
        if (!dams.isEmpty()) {
            try {
                ImprimeDAM imprimeDAM = new ImprimeDAM();
                imprimeDAM.setGeraNoDialog(true);
                imprimeDAM.imprimirDamUnicoViaApi(dams);
            } catch (Exception e) {
                logger.error("{}", e);
                ve.adicionarMensagemDeOperacaoNaoPermitida("Ocorreu um erro ao gerar o DAM!: " + e.getMessage());
                ve.lancarException();
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi possível gerar um DAM para esse Alvará de Construção!");
            ve.lancarException();
        }
    }

    private CalculoAlvaraConstrucaoHabitese criarCalculoAlvaraConstrucao(ParametroRegularizacao parametroRegularizacao,
                                                                         ProcessoCalculoAlvaraConstrucaoHabitese processoCalculo) {
        CalculoAlvaraConstrucaoHabitese calculo = new CalculoAlvaraConstrucaoHabitese();
        calculo.setProcCalcAlvaraConstruHabit(processoCalculo);
        calculo.setExercicio(processoCalculo.getExercicio());
        calculo.setValorUFM(BigDecimal.ZERO);
        calculo.setAreaTotal(BigDecimal.ZERO);
        calculo.setVencimento(processoCalculo.getAlvaraConstrucao().getDataVencimentoDebito());
        calculo.setCadastro(processoCalculo.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario());
        criarItensCalculo(parametroRegularizacao, calculo);
        converterValorUFMCalculo(calculo);
        criarCalculoPessoa(calculo);
        return calculo;
    }

    private CalculoAlvaraConstrucaoHabitese criarCalculoTaxaVistoria(ParametroRegularizacao parametroRegularizacao,
                                                                     ProcessoCalculoAlvaraConstrucaoHabitese processoCalculo) {
        CalculoAlvaraConstrucaoHabitese calculo = new CalculoAlvaraConstrucaoHabitese();
        calculo.setProcCalcAlvaraConstruHabit(processoCalculo);
        calculo.setExercicio(processoCalculo.getExercicio());
        calculo.setValorUFM(BigDecimal.ZERO);
        calculo.setAreaTotal(BigDecimal.ZERO);
        calculo.setVencimento(DataUtil.ajustarDataUtil(DataUtil.adicionaDias(new Date(), parametroRegularizacao.getPrazoTaxaVistoria()), feriadoFacade));
        calculo.setSubTipoCalculo(CalculoAlvaraConstrucaoHabitese.SubTipoCalculo.VISTORIA);
        calculo.setCadastro(processoCalculo.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario());
        calculo.setAreaTotal(calculo.getProcCalcAlvaraConstruHabit().getAlvaraConstrucao().getTotalAreaServicos());

        ServicoConstrucao servicoConstrucao = parametroRegularizacao.getServicoConstrucao();
        VinculoServicoTributo vinculoServicoTributo = parametroRegularizacao.getVinculoPorServico(servicoConstrucao);
        Tributo tributo = vinculoServicoTributo != null ? vinculoServicoTributo.getTributo() :
            parametroRegularizacao.getTributoTaxaVistoria();
        calculo.setValorUFM(calculo.getValorUFM().add(lancarItemCalculo(calculo, servicoConstrucao,
            tributo, calculo.getAreaTotal())));

        converterValorUFMCalculo(calculo);
        criarCalculoPessoa(calculo);
        return calculo;

    }

    public ProcessoCalculoAlvaraConstrucaoHabitese criarProcessoCalculoAlvaraConstrucao(ParametroRegularizacao parametroRegularizacao,
                                                                                        AlvaraConstrucao alvaraConstrucao) {
        ProcessoCalculoAlvaraConstrucaoHabitese processoCalculo = new ProcessoCalculoAlvaraConstrucaoHabitese();
        processoCalculo.setDivida(parametroRegularizacao.getDividaAlvara());
        processoCalculo.setExercicio(alvaraConstrucao.getExercicio());
        processoCalculo.setAlvaraConstrucao(alvaraConstrucao);
        return processoCalculo;
    }

    private void criarItensCalculo(ParametroRegularizacao parametroRegularizacao, CalculoAlvaraConstrucaoHabitese calculo) {
        for (ServicosAlvaraConstrucao servicosAlvaraConstrucao : calculo.getProcCalcAlvaraConstruHabit().getAlvaraConstrucao().getServicos()) {
            VinculoServicoTributo vinculoServicoTributo = parametroRegularizacao.getVinculoPorServico(
                servicosAlvaraConstrucao.getServicoConstrucao());
            calculo.setAreaTotal(calculo.getAreaTotal().add(servicosAlvaraConstrucao.getArea()));
            calculo.setValorUFM(calculo.getValorUFM().add(lancarItemCalculo(calculo, servicosAlvaraConstrucao.getServicoConstrucao(),
                vinculoServicoTributo.getTributo(), servicosAlvaraConstrucao.getArea())));
        }
    }

    public void criarCalculoPessoa(CalculoAlvaraConstrucaoHabitese calculo) {
        List<CalculoPessoa> calculoPessoas = Lists.newArrayList();
        CadastroImobiliario cadastroImobiliario = (CadastroImobiliario) calculo.getCadastro();
        for (Propriedade propriedade : cadastroImobiliario.getPropriedadeVigente()) {
            Pessoa pessoa = propriedade.getPessoa();
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setPessoa(pessoa);
            calculoPessoa.setCalculo(calculo);
            calculoPessoas.add(calculoPessoa);
        }
        calculo.setPessoas(calculoPessoas);
    }

    public void converterValorUFMCalculo(CalculoAlvaraConstrucaoHabitese calculo) {
        try {
            BigDecimal valorReal = moedaFacade.converterToRealPorExercicio(calculo.getValorUFM(), calculo.getExercicio());
            calculo.setValorEfetivo(valorReal);
            calculo.setValorReal(valorReal);
        } catch (UFMException e) {
            throw new ValidacaoException("Falha ao converter de UFM para o valor real: " + e.getMessage());
        }
    }

    private void validarCalculoAlvaraConstrucao(ParametroRegularizacao parametroRegularizacao,
                                                AlvaraConstrucao alvaraConstrucao) {
        if (parametroRegularizacao == null) {
            throw new ValidacaoException("Nenhum parâmetro de regularização de construção foi encontrado " +
                "para o exercício de " + alvaraConstrucao.getExercicio().getAno() + ". ");
        }
        if (parametroRegularizacao.getDividaAlvara() == null) {
            throw new ValidacaoException("Não existe uma divida configurada para o alvará no parametro " +
                "de regularização do exercício " + alvaraConstrucao.getExercicio().getAno());
        }
        ValidacaoException ve = new ValidacaoException();
        if (alvaraConstrucao.getServicos() == null || alvaraConstrucao.getServicos().isEmpty()) {
            throw new ValidacaoException("Informe ao menos um serviço.");
        }
        for (ServicosAlvaraConstrucao servicosAlvaraConstrucao : alvaraConstrucao.getServicos()) {
            VinculoServicoTributo vinculoServicoTributo = parametroRegularizacao
                .getVinculoPorServico(servicosAlvaraConstrucao.getServicoConstrucao());
            if (vinculoServicoTributo == null) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("Não existe vinculo de serviço com tributo de: " +
                    servicosAlvaraConstrucao.getServicoConstrucao() + " no parametro de regularização do exercício " +
                    alvaraConstrucao.getExercicio().getAno());
            }
        }
        ve.lancarException();
    }


    private void validarCalculoAlvaraConstrucaoTaxaVistoria(ParametroRegularizacao parametroRegularizacao,
                                                            AlvaraConstrucao alvaraConstrucao) {
        if (parametroRegularizacao == null) {
            throw new ValidacaoException("Nenhum parâmetro de regularização de construção foi encontrado " +
                "para o exercício de " + alvaraConstrucao.getExercicio().getAno() + ". ");
        }
        if (parametroRegularizacao.getServicoConstrucao() == null) {
            throw new ValidacaoException("Não existe um serviço de construção configurado para a taxa de vistoria no parametro de regularização do exercício " + alvaraConstrucao.getExercicio().getAno());
        }
        if (parametroRegularizacao.getDividaTaxaVistoria() == null) {
            throw new ValidacaoException("Não existe uma divida configurada para a taxa de vistoria no parametro de regularização do exercício " + alvaraConstrucao.getExercicio().getAno());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public AlvaraConstrucao gerarCalculoAlvaraConstrucao(AlvaraConstrucao alvaraConstrucao, boolean salvar) throws ValidacaoException {
        alvaraConstrucao = sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).recarregarEmNovaTransacao(alvaraConstrucao);

        ParametroRegularizacao parametroRegularizacao = parametroRegularizacaoFacade
            .buscarParametroRegularizacaoPorExercicio(alvaraConstrucao.getExercicio());

        validarCalculoAlvaraConstrucao(parametroRegularizacao, alvaraConstrucao);

        ProcessoCalculoAlvaraConstrucaoHabitese processoCalculo = criarProcessoCalculoAlvaraConstrucao(parametroRegularizacao,
            alvaraConstrucao);

        processoCalculo.getCalculosAlvaraConstrucaoHabitese().add(criarCalculoAlvaraConstrucao(parametroRegularizacao, processoCalculo));

        alvaraConstrucao.setProcessoCalcAlvaConstHabi(processoCalculo);
        alvaraConstrucao.setDataVencimentoCartaz(DataUtil.ajustarDataUtil(DataUtil.adicionaDias(new Date(),
            parametroRegularizacao.getPrazoCartaz()), feriadoFacade));
        alvaraConstrucao.setDataVencimentoDebito(DataUtil.ajustarDataUtil(DataUtil.adicionaDias(new Date(),
            parametroRegularizacao.getPrazoTaxaAlvara()), feriadoFacade));

        if (salvar) {
            alvaraConstrucao = sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).salvarRetornandoEmNovaTransacao(alvaraConstrucao);
            try {
                alvaraConstrucao.setSituacao(AlvaraConstrucao.Situacao.EFETIVADO);
                geraValorDividaAlvaraConstrucao.gerarDebito(alvaraConstrucao.getProcessoCalcAlvaConstHabi(), true);
                alvaraConstrucao = sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).salvarRetornandoEmNovaTransacao(alvaraConstrucao);
            } catch (Exception e) {
                new ValidacaoException().adicionarMensagemDeOperacaoNaoRealizada("Falha ao gerar débito: " + e.getMessage()).lancarException();
                logger.error("Erro ao gerarCalculo{} ", e.getMessage());
            }
        }
        return alvaraConstrucao;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public AlvaraConstrucao gerarCalculoTaxaVistoria(AlvaraConstrucao alvaraConstrucao, boolean salvar) throws ValidacaoException {
        alvaraConstrucao = sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).recarregarEmNovaTransacao(alvaraConstrucao);

        ParametroRegularizacao parametroRegularizacao = parametroRegularizacaoFacade
            .buscarParametroRegularizacaoPorExercicio(alvaraConstrucao.getExercicio());

        validarCalculoAlvaraConstrucaoTaxaVistoria(parametroRegularizacao, alvaraConstrucao);

        ProcessoCalculoAlvaraConstrucaoHabitese processoCalculo = criarProcessoCalculoAlvaraConstrucao(parametroRegularizacao,
            alvaraConstrucao);

        processoCalculo.getCalculosAlvaraConstrucaoHabitese().add(criarCalculoTaxaVistoria(parametroRegularizacao, processoCalculo));

        alvaraConstrucao.setProcessoCalcAlvaConstVisto(processoCalculo);

        if (salvar) {
            alvaraConstrucao = sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).salvarRetornandoEmNovaTransacao(alvaraConstrucao);
            try {
                geraValorDividaAlvaraConstrucao.gerarDebito(alvaraConstrucao.getProcessoCalcAlvaConstVisto(), true);
                alvaraConstrucao = sessionContext.getBusinessObject(AlvaraConstrucaoFacade.class).salvarRetornandoEmNovaTransacao(alvaraConstrucao);
                alvaraConstrucao.getProcRegularizaConstrucao().setSituacao(ProcRegularizaConstrucao.Situacao.AGUARDANDO_TAXA_VISTORIA);
                procRegularizaConstrucaoFacade.salvar(alvaraConstrucao.getProcRegularizaConstrucao());
            } catch (Exception e) {
                new ValidacaoException().adicionarMensagemDeOperacaoNaoRealizada("Falha ao gerar débito: " + e.getMessage()).lancarException();
                logger.error("Erro ao gerarCalculo{} ", e.getMessage());
            }
        }
        return alvaraConstrucao;
    }

    private BigDecimal validarFaixaValoresSCL(BigDecimal faixa) {
        BigDecimal faixaValor = new BigDecimal(999999);
        if (faixa == null) {
            return faixaValor;
        }
        return faixa;
    }

    public BigDecimal lancarItemCalculo(CalculoAlvaraConstrucaoHabitese calculoAlvaraConstrucaoHabitese,
                                        ServicoConstrucao servicoConstrucao, Tributo tributo, BigDecimal area) throws ValidacaoException {
        BigDecimal ufmTotal = BigDecimal.ZERO;

        ValidacaoException ve = new ValidacaoException();
        List<ItemServicoConstrucao> itens = itemServicoConstrucaoFacade.buscarItemServicoPorServico(servicoConstrucao);
        if (!itens.isEmpty()) {
            boolean encontrou = false;
            for (FaixaDeValoresSCL faixaDeValoresSCL : itens.get(0).getListaFaixaDeValoresSCL()) {
                if ((faixaDeValoresSCL.getAreaInicial() != null && faixaDeValoresSCL.getAreaInicial().compareTo(area) <= 0)
                    && faixaDeValoresSCL.getAreaInicial() != null
                    && validarFaixaValoresSCL(faixaDeValoresSCL.getAreaFinal()).compareTo(area) >= 0) {
                    encontrou = true;
                    ItemCalculoAlvaraConstrucaoHabitese itemCalculoAlvaraConstrucaoHabitese = new ItemCalculoAlvaraConstrucaoHabitese();
                    itemCalculoAlvaraConstrucaoHabitese.setCalcAlvaraConstrucHabitese(calculoAlvaraConstrucaoHabitese);
                    itemCalculoAlvaraConstrucaoHabitese.setValorUFM(faixaDeValoresSCL.getValorUFM());
                    itemCalculoAlvaraConstrucaoHabitese.setTributo(tributo);
                    ufmTotal = ufmTotal.add(faixaDeValoresSCL.getValorUFM());
                    try {
                        itemCalculoAlvaraConstrucaoHabitese.setValorReal(moedaFacade
                            .converterToRealPorExercicio(faixaDeValoresSCL.getValorUFM(),
                                calculoAlvaraConstrucaoHabitese.getExercicio()));
                    } catch (UFMException e) {
                        ve.adicionarMensagemDeOperacaoNaoRealizada("Falha ao converter de UFM para o valor real: "
                            + e.getMessage());
                        ve.lancarException();
                    }
                    calculoAlvaraConstrucaoHabitese.getItensCalculo().add(itemCalculoAlvaraConstrucaoHabitese);
                    break;
                }
            }
            if (!encontrou) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhuma faixa de valor encontrada para o serviço " +
                    servicoConstrucao.toString() + ", no exercício de " + calculoAlvaraConstrucaoHabitese.getExercicio() +
                    " com área " + area.toString());
                ve.lancarException();
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum item de serviço encontrado para o serviço " +
                servicoConstrucao.toString() + " no exercício " + calculoAlvaraConstrucaoHabitese.getExercicio().getAno());
            ve.lancarException();
        }
        return ufmTotal;
    }

    public ProcRegularizaConstrucaoFacade getProcRegularizaConstrucaoFacade() {
        return procRegularizaConstrucaoFacade;
    }

    public ParametroRegularizacaoFacade getParametroRegularizacaoFacade() {
        return parametroRegularizacaoFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public HabiteseClassesConstrucaoFacade getHabiteseClassesConstrucaoFacade() {
        return habiteseClassesConstrucaoFacade;
    }

    public GeraValorDividaAlvaraConstrucao getGeraValorDividaAlvaraConstrucao() {
        return geraValorDividaAlvaraConstrucao;
    }

    public AtributoFacade getAtributoFacade() {
        return atributoFacade;
    }

    public ConstrucaoFacade getConstrucaoFacade() {
        return construcaoFacade;
    }

    public ItemServicoConstrucaoFacade getItemServicoConstrucaoFacade() {
        return itemServicoConstrucaoFacade;
    }

    public ServicoConstrucaoFacade getServicoConstrucaoFacade() {
        return servicoConstrucaoFacade;
    }

    @Override
    public AlvaraConstrucao salvarRetornando(AlvaraConstrucao entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(buscarUltimoCodigoMaisUmPorExercicio(entity.getExercicio()));
        }
        return super.salvarRetornando(entity);
    }

    @Override
    public void depoisDePagar(Calculo calculo) {
        CalculoAlvaraConstrucaoHabitese calcAlvara = em.find(CalculoAlvaraConstrucaoHabitese.class, calculo.getId());
        AlvaraConstrucao alvaraConstrucao = calcAlvara.getProcCalcAlvaraConstruHabit().getAlvaraConstrucao();
        Habitese habitese = calcAlvara.getProcCalcAlvaraConstruHabit().getHabitese();
        if (alvaraConstrucao == null) {
            alvaraConstrucao = habitese.getAlvaraConstrucao();
        }
        ProcRegularizaConstrucao procRegularizaConstrucao = alvaraConstrucao.getProcRegularizaConstrucao();
        switch (calcAlvara.getSubTipoCalculo()) {
            case ALVARA:
                if (ProcRegularizaConstrucao.Situacao.AGUARDANDO_ALVARA_CONSTRUCAO.equals(procRegularizaConstrucao.getSituacao())) {
                    atualizarSituacaoAlvara(alvaraConstrucao.getId(), AlvaraConstrucao.Situacao.FINALIZADO);
                    if (alvaraConstrucao.getDocumentoOficial() != null) {
                        removerDocumentoOficial(alvaraConstrucao.getId(), alvaraConstrucao.getDocumentoOficial().getId());
                    }
                    getProcRegularizaConstrucaoFacade().atualizarSituacaoProcesso(procRegularizaConstrucao.getId(), ProcRegularizaConstrucao.Situacao.ALVARA_CONSTRUCAO);
                }
                break;
            case VISTORIA:
                if (ProcRegularizaConstrucao.Situacao.AGUARDANDO_TAXA_VISTORIA.equals(procRegularizaConstrucao.getSituacao())) {
                    procRegularizaConstrucaoFacade.atualizarSituacaoProcesso(procRegularizaConstrucao.getId(), ProcRegularizaConstrucao.Situacao.TAXA_VISTORIA);
                }
                break;
            case HABITESE:
                if (ProcRegularizaConstrucao.Situacao.AGUARDANDO_HABITESE.equals(procRegularizaConstrucao.getSituacao())) {
                    habiteseConstrucaoFacade.atualizarSituacaoHabitese(habitese.getId(), Habitese.Situacao.FINALIZADO);
                    if (alvaraConstrucao.isTodosPavimentosComHabitesePago()) {
                        procRegularizaConstrucaoFacade.atualizarSituacaoProcesso(procRegularizaConstrucao.getId(), ProcRegularizaConstrucao.Situacao.HABITESE);
                    }
                }
                break;
        }
    }

    private void atualizarSituacaoAlvara(Long idAlvara, AlvaraConstrucao.Situacao situacao) {
        if (idAlvara == null || situacao == null) return;
        Query query = em.createNativeQuery("update AlvaraConstrucao set situacao = :situacao where id = :idAlvara");
        query.setParameter("idAlvara", idAlvara);
        query.setParameter("situacao", situacao.name());
        query.executeUpdate();
    }

    private void removerDocumentoOficial(Long idAlvara, Long idDocumento) {
        Query query = em.createNativeQuery("update AlvaraConstrucao set documentooficial_id = null where id = :idAlvara");
        query.setParameter("idAlvara", idAlvara);
        query.executeUpdate();

        Query q = em.createNativeQuery("delete documentooficial where id = :idDocumento");
        q.setParameter("idDocumento", idDocumento);
        q.executeUpdate();
    }

    public UsuarioSistema recuperarUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public SolicitacaoAlvaraImediato buscarSolicitacaoImediato(ProcRegularizaConstrucao procRegularizaConstrucao) {
        String sql = "select s.* from SolicitacaoAlvaraImediato s where s.procRegularizaConstrucao_id = :idProcesso";
        Query q = em.createNativeQuery(sql, SolicitacaoAlvaraImediato.class);
        q.setParameter("idProcesso", procRegularizaConstrucao.getId());
        List resultList = q.getResultList();
        return !resultList.isEmpty() ? (SolicitacaoAlvaraImediato) resultList.get(0) : null;
    }

    public AlvaraConstrucao gerarCalculo(AlvaraConstrucao alvaraConstrucao, boolean salvar) {
        switch (alvaraConstrucao.getProcRegularizaConstrucao().getSituacao()) {
            case AGUARDANDO_ALVARA_CONSTRUCAO:
                return gerarCalculoAlvaraConstrucao(alvaraConstrucao, salvar);
            case ALVARA_CONSTRUCAO:
                return gerarCalculoTaxaVistoria(alvaraConstrucao, salvar);
        }
        return null;
    }

    public List<AlvaraConstrucao> buscarAlvarasConstrucaoPorCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        return em.createQuery("from AlvaraConstrucao ac " +
                " where ac.procRegularizaConstrucao.cadastroImobiliario  = :cadastroImobiliario " +
                " order by ac.id ")
            .setParameter("cadastroImobiliario", cadastroImobiliario)
            .getResultList();
    }
}
