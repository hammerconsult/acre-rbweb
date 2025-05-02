package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ParametroRegularizacao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.VinculoServicoTributo;
import br.com.webpublico.entidadesauxiliares.VOHabitese;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.EmailService;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class HabiteseConstrucaoFacade extends AbstractFacade<Habitese> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    @EJB
    private HabiteseFaixaDeValoresFacade habiteseFaixaDeValoresFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HabiteseClassesConstrucaoFacade habiteseClassesConstrucaoFacade;
    @EJB
    private HabitesePadroesConstrucaoFacade habitesePadroesConstrucaoFacade;
    @EJB
    private ParametroRegularizacaoFacade parametroRegularizacaoFacade;
    @EJB
    private GeraValorDividaAlvaraConstrucao geraValorDividaAlvaraConstrucao;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private ServicoConstrucaoFacade servicoConstrucaoFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @Resource
    private SessionContext sessionContext;

    public HabiteseConstrucaoFacade() {
        super(Habitese.class);
    }

    @Override
    public Habitese recuperar(Object id) {
        return inicializar(super.recuperar(id));
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return inicializar((Habitese) super.recuperar(entidade, id));
    }

    private Habitese inicializar(Habitese habitese) {
        em.refresh(habitese);
        Hibernate.initialize(habitese.getAlvaraConstrucao());
        Hibernate.initialize(habitese.getAlvaraConstrucao().getHabiteses());
        Hibernate.initialize(habitese.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario());
        Hibernate.initialize(habitese.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario().getConstrucoes());
        Hibernate.initialize(habitese.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario().getPropriedade());
        Hibernate.initialize(habitese.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario().getLote().getTestadas());
        Hibernate.initialize(habitese.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario().getLote().getTestadaPrincipal().getFace());
        Hibernate.initialize(habitese.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario().getLote().getTestadaPrincipal().getFace().getLogradouroBairro());
        Hibernate.initialize(habitese.getAlvaraConstrucao().getProcRegularizaConstrucao().getAlvarasDeConstrucao());
        Hibernate.initialize(habitese.getProcessoCalcAlvaConstHabi());
        if (habitese.getProcessoCalcAlvaConstHabi() != null) {
            Hibernate.initialize(habitese.getProcessoCalcAlvaConstHabi().getCalculos());
            for (CalculoAlvaraConstrucaoHabitese calculo : habitese.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese()) {
                Hibernate.initialize(calculo);
                Hibernate.initialize(calculo.getItensCalculo());
            }
        }
        Hibernate.initialize(habitese.getDeducoes());
        Hibernate.initialize(habitese.getCaracteristica());
        return habitese;
    }

    private Long buscarUltimoCodigoMaisUm() {
        Query q = em.createNativeQuery("select nvl(max(codigo),0)+1 from HABITESE");
        return ((Number) q.getResultList().get(0)).longValue();
    }

    public void emitirTermo(Habitese habitese) throws ValidacaoException {
        if (habitese.getDocumentoOficial() == null) {
            atualizarDocumentoOficial(habitese);
        }
        alvaraConstrucaoFacade.getDocumentoOficialFacade().emiteDocumentoOficial(habitese.getDocumentoOficial());
    }

    public void atualizarDocumentoOficial(Habitese habitese) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        ParametroRegularizacao parametro = parametroRegularizacaoFacade.buscarParametroRegularizacaoPorExercicio(habitese.getExercicio());
        if(parametro == null) {
            parametro = parametroRegularizacaoFacade.buscarParametroRegularizacaoPorExercicio(sistemaFacade.getExercicioCorrente());
        }
        if (parametro == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum parâmetro de regularização encontrado para o exercício " + habitese.getExercicio().getAno());
            ve.lancarException();
        } else if (parametro.getTipoDoctoOficialHabitese() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O tipo do documento oficial do habite-se não está configurado no parametro de regularização");
            ve.lancarException();
        } else {
            try {
                habitese.setDocumentoOficial(alvaraConstrucaoFacade.getDocumentoOficialFacade()
                    .gerarDocumentoHabiteseConstrucao(sessionContext.getBusinessObject(HabiteseConstrucaoFacade.class)
                        .recarregarEmNovaTransacao(habitese), parametro.getTipoDoctoOficialHabitese()));
                habitese.setDataExpedicaoTermo(new Date());
                em.merge(habitese);
            } catch (UFMException | AtributosNulosException e) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("Erro ao emitir o alvará: " + e.getMessage());
            }
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(Habitese entity) {
        entity.setCodigo(buscarUltimoCodigoMaisUm());
        super.salvarNovo(entity);
        ProcRegularizaConstrucao procRegularizaConstrucao = alvaraConstrucaoFacade.getProcRegularizaConstrucaoFacade().recarregar(entity.getAlvaraConstrucao().getProcRegularizaConstrucao());
        procRegularizaConstrucao.setSituacao(ProcRegularizaConstrucao.Situacao.AGUARDANDO_HABITESE);
        em.merge(procRegularizaConstrucao);
    }

    @Override
    public void remover(Habitese entity) {
        super.remover(entity);
        ProcRegularizaConstrucao procRegularizaConstrucao = alvaraConstrucaoFacade.getProcRegularizaConstrucaoFacade().recarregar(entity.getAlvaraConstrucao().getProcRegularizaConstrucao());
        procRegularizaConstrucao.setSituacao(ProcRegularizaConstrucao.Situacao.TAXA_VISTORIA);
        em.merge(procRegularizaConstrucao);
    }

    public void enviarEmail(Habitese habitese, String mensagemEmail, String... emailsSeparados) throws ValidacaoException {
        try {
            ConfiguracaoTributario configuracaoTributario = alvaraConstrucaoFacade.getConfiguracaoTributarioFacade().retornaUltimo();
            String assunto = "Prefeitura Municipal de " + configuracaoTributario.getCidade().getNome() + " / " + configuracaoTributario.getCidade().getUf() + " - ";
            if (Habitese.Situacao.FINALIZADO.equals(habitese.getSituacao())) {
                assunto += "Habite-se de Construção";
                atualizarDocumentoOficial(habitese);
                alvaraConstrucaoFacade.getDocumentoOficialFacade().enviarEmailDocumentoOficial(emailsSeparados, habitese.getDocumentoOficial(), assunto, mensagemEmail);
            } else {
                assunto += "DAM do Habite-se de Construção";
                List<DAM> dams = recuperarDAMS(habitese);
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
        } catch (ValidacaoException e) {
            throw e;
        } catch (Exception e) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoRealizada(e.getMessage());
            ve.lancarException();
        }
    }

    private void validarCalculoHabitese(ParametroRegularizacao parametroRegularizacao,
                                        Habitese habitese) {
        if (parametroRegularizacao == null) {
            throw new ValidacaoException("Nenhum parâmetro de regularização de construção foi encontrado " +
                "para o exercício de " + habitese.getExercicio().getAno() + ". ");
        }
        if (habitese.getCaracteristica().getServicos().isEmpty()) {
            throw new ValidacaoException("Informe ao menos um serviço.");
        }
        if (parametroRegularizacao.getDividaHabitese() == null) {
            throw new ValidacaoException("Não existe uma divida configurada para o habite-se no parametro de regularização do exercício " + habitese.getExercicio().getAno());
        }
        if (parametroRegularizacao.getTributoHabitese() == null) {
            throw new ValidacaoException("Não existe um tributo configurado para o habite-se no parametro de regularização do exercício " + habitese.getExercicio().getAno());
        }
    }

    private ProcessoCalculoAlvaraConstrucaoHabitese criarProcessoCalculoHabitese(ParametroRegularizacao parametroRegularizacao,
                                                                                 Habitese habitese) {
        ProcessoCalculoAlvaraConstrucaoHabitese processoCalculo = new ProcessoCalculoAlvaraConstrucaoHabitese();
        processoCalculo.setDivida(parametroRegularizacao.getDividaAlvara());
        processoCalculo.setExercicio(habitese.getExercicio());
        processoCalculo.setHabitese(habitese);
        processoCalculo.setAlvaraConstrucao(habitese.getAlvaraConstrucao());
        return processoCalculo;
    }

    private void criarItensCalculoHabitese(ParametroRegularizacao parametroRegularizacao, CalculoAlvaraConstrucaoHabitese calculo) {
        for (ServicosAlvaraConstrucao servicosAlvaraConstrucao : calculo.getProcCalcAlvaraConstruHabit().getHabitese().getCaracteristica().getServicos()) {
            VinculoServicoTributo vinculoServicoTributo = parametroRegularizacao
                .getVinculoPorServico(servicosAlvaraConstrucao.getServicoConstrucao());
            calculo.setAreaTotal(calculo.getAreaTotal().add(servicosAlvaraConstrucao.getArea()));
            calculo.setValorUFM(calculo.getValorUFM().add(alvaraConstrucaoFacade.lancarItemCalculo(calculo, servicosAlvaraConstrucao.getServicoConstrucao(),
                vinculoServicoTributo.getTributo(), servicosAlvaraConstrucao.getArea())));
        }
    }


    private CalculoAlvaraConstrucaoHabitese criarCalculoHabite(ParametroRegularizacao parametroRegularizacao,
                                                               ProcessoCalculoAlvaraConstrucaoHabitese processoCalculo) {
        CalculoAlvaraConstrucaoHabitese calculo = new CalculoAlvaraConstrucaoHabitese();
        calculo.setProcCalcAlvaraConstruHabit(processoCalculo);
        calculo.setExercicio(processoCalculo.getExercicio());
        calculo.setValorUFM(BigDecimal.ZERO);
        calculo.setAreaTotal(BigDecimal.ZERO);
        calculo.setCadastro(processoCalculo.getHabitese().getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario());
        calculo.setSubTipoCalculo(CalculoAlvaraConstrucaoHabitese.SubTipoCalculo.HABITESE);
        criarItensCalculoHabitese(parametroRegularizacao, calculo);
        alvaraConstrucaoFacade.converterValorUFMCalculo(calculo);
        alvaraConstrucaoFacade.criarCalculoPessoa(calculo);
        return calculo;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Habitese gerarCalculoHabitese(Habitese habitese, boolean salvar) throws ValidacaoException {
        habitese = sessionContext.getBusinessObject(HabiteseConstrucaoFacade.class).recarregarEmNovaTransacao(habitese);

        ParametroRegularizacao parametroRegularizacao = parametroRegularizacaoFacade
            .buscarParametroRegularizacaoPorExercicio(habitese.getExercicio());

        validarCalculoHabitese(parametroRegularizacao, habitese);

        ProcessoCalculoAlvaraConstrucaoHabitese processoCalculo = criarProcessoCalculoHabitese(parametroRegularizacao,
            habitese);

        processoCalculo.getCalculosAlvaraConstrucaoHabitese().add(criarCalculoHabite(parametroRegularizacao, processoCalculo));

        habitese.setProcessoCalcAlvaConstHabi(processoCalculo);

        if (salvar) {
            habitese = sessionContext.getBusinessObject(HabiteseConstrucaoFacade.class).salvarRetornandoEmNovaTransacao(habitese);
            try {
                geraValorDividaAlvaraConstrucao.gerarDebito(habitese.getProcessoCalcAlvaConstHabi(), true);
                habitese.setSituacao(Habitese.Situacao.EFETIVADO);
                habitese.setDataVencimentoISS(habitese.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese().get(0).getVencimento());
            } catch (Exception e) {
                new ValidacaoException().adicionarMensagemDeOperacaoNaoRealizada("Falha ao gerar débito: " + e.getMessage()).lancarException();
            }
            habitese = sessionContext.getBusinessObject(HabiteseConstrucaoFacade.class).salvarRetornandoEmNovaTransacao(habitese);
        }
        return habitese;
    }

    public List<ResultadoParcela> recuperarParcelas(Habitese habitese) throws ValidacaoException {
        habitese = sessionContext.getBusinessObject(HabiteseConstrucaoFacade.class).recarregarEmNovaTransacao(habitese);
        try {
            return processoParcelamentoFacade.buscarArvoreParcelamentoPartindoDoCalculo(habitese.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese().get(0).getId());
        } catch (Exception e) {
            new ValidacaoException().adicionarMensagemDeOperacaoNaoRealizada("Erro ao recuperar parcelas: " + e.getMessage()).lancarException();
        }
        return null;
    }

    public DAM recuperarDAM(Long id) {
        List<DAM> dams = em.createQuery("select dam " +
            "                  from ItemDAM " +
            "               item join item.DAM dam " +
            "               where item.parcela.id = :id ").setParameter("id", id).getResultList();
        if (dams.isEmpty()) {
            return null;
        } else {
            return dams.get(0);
        }
    }

    public List<ResultadoParcela> buscarParcelas(ProcessoCalculoAlvaraConstrucaoHabitese processo) {
        if (processo != null &&
            !processo.getCalculosAlvaraConstrucaoHabitese().isEmpty()
            && processo.getCalculosAlvaraConstrucaoHabitese().get(0).getId() != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL,
                processo.getCalculosAlvaraConstrucaoHabitese().get(0).getId());
            return consultaParcela.executaConsulta().getResultados();
        } else {
            return Lists.newArrayList();
        }
    }


    public List<DAM> recuperarDAMS(Habitese habitese) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        List<ResultadoParcela> resultadoParcelas = recuperarParcelas(habitese);
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

    public void emitirDAM(Habitese habitese) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        List<DAM> dams = recuperarDAMS(habitese);
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

    public CriteriaQuery<Habitese> criarCriteriaConsulta(Map<String, Object> parametros) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Habitese> query = builder.createQuery(Habitese.class);
        Root<Habitese> root = query.from(Habitese.class);
        Join<CaracteristicaConstrucaoHabitese, Habitese> construcaoHabiteseHabitese = root.join("caracteristica");
        Join<ProcessoCalculoAlvaraConstrucaoHabitese, Habitese> processoCalculo = root.join("processoCalcAlvaConstHabi", JoinType.LEFT);
        Join<CalculoAlvaraConstrucaoHabitese, ProcessoCalculoAlvaraConstrucaoHabitese> calculo = processoCalculo.join("calculosAlvaraConstrucaoHabitese", JoinType.LEFT);
        Join<AlvaraConstrucao, Habitese> alvaraConstrucao = root.join("alvaraConstrucao");
        Join<ProcRegularizaConstrucao, AlvaraConstrucao> procRegularizaConstrucao = alvaraConstrucao.join("procRegularizaConstrucao");
        Join<Exercicio, ProcRegularizaConstrucao> exercicioProcesso = procRegularizaConstrucao.join("exercicio");
        Join<CadastroImobiliario, ProcRegularizaConstrucao> cadastroImobiliario = procRegularizaConstrucao.join("cadastroImobiliario");
        Join<CadastroImobiliario, Lote> lote = cadastroImobiliario.join("lote");
        Join<Lote, Testada> testada = lote.join("testadas");
        Join<Testada, Face> face = testada.join("face");
        Join<Face, LogradouroBairro> logradouroBairro = face.join("logradouroBairro");
        Join<Propriedade, CadastroImobiliario> propriedade = cadastroImobiliario.join("propriedade");
        List<Predicate> predicates = Lists.newArrayList();
        if (parametros.containsKey("codigoProcesso")) {
            predicates.add(builder.equal(procRegularizaConstrucao.get("codigo"), parametros.get("codigoProcesso")));
        }
        if (parametros.containsKey("exercicioProcesso")) {
            predicates.add(builder.equal(exercicioProcesso.get("ano"), parametros.get("exercicioProcesso")));
        }
        if (parametros.containsKey("requerente")) {
            predicates.add(builder.equal(propriedade.get("pessoa"), parametros.get("requerente")));
        }
        if (parametros.containsKey("cadastroImobiliario")) {
            predicates.add(builder.equal(procRegularizaConstrucao.get("cadastroImobiliario"), parametros.get("cadastroImobiliario")));
        }
        if (parametros.containsKey("bairro")) {
            predicates.add(builder.equal(logradouroBairro.get("bairro"), parametros.get("bairro")));
        }
        if (parametros.containsKey("logradouro")) {
            predicates.add(builder.equal(logradouroBairro.get("logradouro"), parametros.get("logradouro")));
        }
        if (parametros.containsKey("tipoConstrucao")) {
            predicates.add(builder.equal(construcaoHabiteseHabitese.get("tipoConstrucao"), parametros.get("tipoConstrucao")));
        }
        if (parametros.containsKey("tipoHabitese")) {
            predicates.add(builder.equal(construcaoHabiteseHabitese.get("tipoHabitese"), parametros.get("tipoHabitese")));
        }
        if (parametros.containsKey("areaInicial")) {
            predicates.add(builder.between(construcaoHabiteseHabitese.<Comparable>get("areaConstruida"), ((Comparable) parametros.get("areaInicial")), ((Comparable) parametros.get("areaFinal"))));
        }
        if (parametros.containsKey("codigoHabiteseInicial")) {
            predicates.add(builder.between(root.<Comparable>get("codigo"), ((Comparable) parametros.get("codigoHabiteseInicial")), ((Comparable) parametros.get("codigoHabiteseFinal"))));
        }
        if (parametros.containsKey("dataLancamentoCalculoInicial")) {
            predicates.add(builder.between(calculo.<Comparable>get("dataCalculo"), ((Comparable) parametros.get("dataLancamentoCalculoInicial")), ((Comparable) parametros.get("dataLancamentoCalculoFinal"))));
        }
        if (parametros.containsKey("dataExpedicaoInicial")) {
            predicates.add(builder.between(root.<Comparable>get("dataExpedicaoTermo"), ((Comparable) parametros.get("dataExpedicaoInicial")), ((Comparable) parametros.get("dataExpedicaoFinal"))));
        }
        if (parametros.containsKey("dataVencimentoInicial")) {
            predicates.add(builder.between(root.<Comparable>get("dataVencimentoISS"), ((Comparable) parametros.get("dataVencimentoInicial")), ((Comparable) parametros.get("dataVencimentoFinal"))));
        }
        if (parametros.containsKey("dataVencimentoDebitoInicial")) {
            predicates.add(builder.between(root.<Comparable>get("dataVencimentoISS"), ((Comparable) parametros.get("dataVencimentoDebitoInicial")), ((Comparable) parametros.get("dataVencimentoDebitoFinal"))));
        }
        if (parametros.containsKey("responsavelServico")) {
            predicates.add(builder.equal(alvaraConstrucao.get("responsavelServico"), parametros.get("responsavelServico")));
        }
        if (parametros.containsKey("CEI")) {
            predicates.add(builder.equal(procRegularizaConstrucao.get("matriculaINSS"), parametros.get("CEI")));
        }
        query.distinct(true).select(root);
        if (predicates != null && !predicates.isEmpty()) {
            query.where(builder.and(predicates.toArray(new Predicate[0])));
        }
        query.orderBy(builder.asc(root.get("codigo")));
        return query;
    }

    public List<Habitese> buscarHabitese(CriteriaQuery<Habitese> criteria) {
        TypedQuery<Habitese> typedQuery = em.createQuery(criteria);
        List<Habitese> habiteses = typedQuery.getResultList();
        for (Habitese proc : habiteses) {
            inicializar(proc);
        }
        return habiteses;
    }

    public CaracteristicaConstrucaoHabitese recuperarCaracteristicaConstrucaoHabitese(Long idProcRegularizacao) {
        List<CaracteristicaConstrucaoHabitese> caracteristica = em.createQuery(" select cch " +
            " from Habitese h" +
            "         JOIN h.caracteristica cch " +
            "         JOIN h.alvaraConstrucao ac " +
            "         JOIN ac.procRegularizaConstrucao prc " +
            "               where prc.id = :id and h.situacao = 'FINALIZADO' order by h.id desc ").setParameter("id", idProcRegularizacao).getResultList();
        if (caracteristica.isEmpty()) {
            return null;
        } else {
            return caracteristica.get(0);
        }
    }

    public List<VOHabitese> buscarDadosRelatorio(CriteriaQuery<Habitese> criteria) {
        List<Habitese> habiteses = buscarHabitese(criteria);
        List<VOHabitese> habitesesVO = Lists.newArrayList();
        for (Habitese habitese : habiteses) {
            habitesesVO.add(new VOHabitese(habitese));
        }
        return habitesesVO;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    public AlvaraConstrucaoFacade getAlvaraConstrucaoFacade() {
        return alvaraConstrucaoFacade;
    }

    public HabiteseFaixaDeValoresFacade getHabiteseFaixaDeValoresFacade() {
        return habiteseFaixaDeValoresFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public HabiteseClassesConstrucaoFacade getHabiteseClassesConstrucaoFacade() {
        return habiteseClassesConstrucaoFacade;
    }

    public HabitesePadroesConstrucaoFacade getHabitesePadroesConstrucaoFacade() {
        return habitesePadroesConstrucaoFacade;
    }

    public ParametroRegularizacaoFacade getParametroRegularizacaoFacade() {
        return parametroRegularizacaoFacade;
    }

    public ServicoConstrucaoFacade getServicoConstrucaoFacade() {
        return servicoConstrucaoFacade;
    }

    public List<Habitese> buscarHabitesConstrucaoPorCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        return em.createQuery("from Habitese h " +
                " where h.alvaraConstrucao.procRegularizaConstrucao.cadastroImobiliario  = :cadastroImobiliario " +
                " order by h.id ")
            .setParameter("cadastroImobiliario", cadastroImobiliario)
            .getResultList();
    }

    public void atualizarSituacaoHabitese(Long idHabitese, Habitese.Situacao situacao) {
        if (idHabitese == null || situacao == null) return;
        Query query = em.createNativeQuery("update habitese set situacao = :situacao where id = :idHabitese");
        query.setParameter("idHabitese", idHabitese);
        query.setParameter("situacao", situacao.name());
        query.executeUpdate();
    }
}
