/*
 * Codigo gerado automaticamente em Tue Apr 17 08:35:43 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.ProcessoParcelamentoControlador;
import br.com.webpublico.controlerelatorio.TermoProcessoParcelamento;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.eventos.NovoParcelamentoEvento;
import br.com.webpublico.negocios.tributario.SingletonConcorrenciaParcela;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorAcrescimos;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorHonorarios;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class ProcessoParcelamentoFacade extends CalculoExecutorDepoisDePagar<ProcessoParcelamento> {

    private static final Logger log = LoggerFactory.getLogger(ProcessoParcelamentoFacade.class);
    public static final String MENSAGEM_CANCELAMENTO = "Cancelamento efetuado de forma automática pelo sistema.";
    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private GeraValorDividaCancelamentoParcelamento geraValorDividaCancelamentoParcelamento;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private CancelamentoParcelamentoFacade cancelamentoParcelamentoFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private SingletonConcorrenciaParcela concorrenciaParcela;
    @EJB
    private ArquivoFacade arquivoFacade;
    @Inject
    private Event<NovoParcelamentoEvento> novoParcelamentoEvento;

    private JdbcDividaAtivaDAO dividaAtivaDAO;
    private JdbcProcessoParcelamentoDAO processoParcelamentoDAO;
    private JdbcCadastroImobiliarioDAO cadastroImobiliarioDAO;
    private JdbcCadastroRuralDAO cadastroRuralDAO;

    public ProcessoParcelamentoFacade() {
        super(ProcessoParcelamento.class);
    }

    @PostConstruct
    public void init() {
        dividaAtivaDAO = (JdbcDividaAtivaDAO) Util.getSpringBeanPeloNome("dividaAtivaDAO");
        processoParcelamentoDAO = (JdbcProcessoParcelamentoDAO) Util.getSpringBeanPeloNome("processoParcelamentoDAO");
        cadastroImobiliarioDAO = (JdbcCadastroImobiliarioDAO) Util.getSpringBeanPeloNome("cadastroImobiliarioJDBCDAO");
        cadastroRuralDAO = (JdbcCadastroRuralDAO) Util.getSpringBeanPeloNome("cadastroRuralDAO");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    @Override
    public List<ProcessoParcelamento> lista() {
        return em.createQuery("from ProcessoParcelamento order by numero desc").getResultList();
    }

    public InscricaoDividaAtivaFacade getInscricaoDividaAtivaFacade() {
        return inscricaoDividaAtivaFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public void setCadastroEconomicoFacade(CadastroEconomicoFacade cadastroEconomicoFacade) {
        this.cadastroEconomicoFacade = cadastroEconomicoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public void setCadastroImobiliarioFacade(CadastroImobiliarioFacade cadastroImobiliarioFacade) {
        this.cadastroImobiliarioFacade = cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public void setCadastroRuralFacade(CadastroRuralFacade cadastroRuralFacade) {
        this.cadastroRuralFacade = cadastroRuralFacade;
    }

    public ComunicaSofPlanFacade getComunicaSofPlanFacade() {
        return comunicaSofPlanFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ParametroParcelamentoFacade getParametroParcelamentoFacade() {
        return parametroParcelamentoFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public CancelamentoParcelamentoFacade getCancelamentoParcelamentoFacade() {
        return cancelamentoParcelamentoFacade;
    }

    public SingletonConcorrenciaParcela getConcorrenciaParcela() {
        return concorrenciaParcela;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public ProcessoParcelamento salvarParcelamento(ProcessoParcelamento processoParcelamento) {
        if (processoParcelamento.getId() == null) {
            gerarPessoasCalculo(processoParcelamento, processoParcelamento);
            ProcessoCalcParcelamento pc = new ProcessoCalcParcelamento();
            pc = em.merge(pc);
            processoParcelamento.setProcessoCalculo(pc);
        }
        if (processoParcelamento.getFaixaDesconto() != null && processoParcelamento.getFaixaDesconto().getId() == null) {
            processoParcelamento.setFaixaDesconto(null);
        }
        return em.merge(processoParcelamento);
    }

    public AssistenteParcelamento gerarParcelasParcelamento(AssistenteBarraProgresso assistenteBarraProgresso, AssistenteParcelamento assistente) {
        ProcessoParcelamento selecionado = (ProcessoParcelamento) assistente.getSelecionado();
        try {
            assistenteBarraProgresso.setDescricaoProcesso("Efetivando parcelamento " + selecionado.getNumeroCompostoComAno());
            JdbcParcelamentoDAO parcelamentoDAO = Util.recuperarSpringBean(JdbcParcelamentoDAO.class);
            parcelamentoDAO.inicializarAtributos();
            assistenteBarraProgresso.setTotal(assistente.getParcelas().size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            assistente.setDescricaoProcesso("Gerando Parcela(s)...");
            carregarParcelasOriginadas(selecionado.getId());
            assistente.setSelecionado(parcelamentoDAO.gerarParcelamento(assistenteBarraProgresso, assistente));
            selecionado.setSituacaoParcelamento(SituacaoParcelamento.FINALIZADO);
            liberarParcelas(selecionado);
            salvarParcelamento(selecionado);
            novoParcelamentoEvento.fire(new NovoParcelamentoEvento(selecionado));
            return assistente;
        } catch (Exception e) {
            logger.error("Erro ao gerar parcelas do parcelamento. Erro: ", e.getMessage());
            logger.debug("Detalhes do erro ao gerar parcelas do parcelamento.", e);
            assistenteBarraProgresso.setExecutando(false);
            throw new ExcecaoNegocioGenerica("Erro ao gerar parcelas do parcelamento");
        }
    }

    private void liberarParcelas(ProcessoParcelamento selecionado) {
        if (selecionado != null) {
            if (selecionado.getItensProcessoParcelamento() != null && !selecionado.getItensProcessoParcelamento().isEmpty()) {
                for (ItemProcessoParcelamento item : selecionado.getItensProcessoParcelamento()) {
                    getConcorrenciaParcela().unLock(item.getParcelaValorDivida());
                }
            }
        }
    }

    private List<CalculoPessoa> gerarPessoasCalculo(ProcessoParcelamento calculoParcelamento, Calculo calculoDaPessoa) {
        List<CalculoPessoa> pessoas = new ArrayList<>();
        if (calculoParcelamento.getCadastro() != null) {
            if (calculoParcelamento.getCadastro() instanceof CadastroImobiliario) {
                CadastroImobiliario cad = em.find(CadastroImobiliario.class, calculoParcelamento.getCadastro().getId());
                for (Propriedade prop : cad.getPropriedadeVigente()) {
                    CalculoPessoa calculoPessoa = new CalculoPessoa();
                    calculoPessoa.setCalculo(calculoDaPessoa);
                    calculoPessoa.setPessoa(prop.getPessoa());
                    pessoas.add(calculoPessoa);
                }
            } else if (calculoParcelamento.getCadastro() instanceof CadastroEconomico) {
                CalculoPessoa calculoPessoa = new CalculoPessoa();
                calculoPessoa.setCalculo(calculoDaPessoa);
                calculoPessoa.setPessoa(((CadastroEconomico) calculoParcelamento.getCadastro()).getPessoa());
                pessoas.add(calculoPessoa);
            } else if (calculoParcelamento.getCadastro() instanceof CadastroRural) {
                CadastroRural cad = em.find(CadastroRural.class, calculoParcelamento.getCadastro().getId());
                for (PropriedadeRural prop : cad.getPropriedade()) {
                    CalculoPessoa calculoPessoa = new CalculoPessoa();
                    calculoPessoa.setCalculo(calculoDaPessoa);
                    calculoPessoa.setPessoa(prop.getPessoa());
                    pessoas.add(calculoPessoa);
                }
            }
        } else if (calculoParcelamento.getPessoa() != null) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(calculoDaPessoa);
            calculoPessoa.setPessoa(calculoParcelamento.getPessoa());
            pessoas.add(calculoPessoa);
        }

        for (CalculoPessoa pessoa : pessoas) {
            calculoDaPessoa.getPessoas().add(pessoa);
        }

        return pessoas;
    }

    public ProcessoParcelamento alterar(ProcessoParcelamento entity) {
        try {
            return em.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Houve um erro ao salvar!");
        }
    }

    public List<ParamParcelamentoFaixa> getTodasFaixaProcesso(ParamParcelamento paramParcelamento) {
        Query q = em.createQuery("from ParamParcelamentoFaixa ppf where ppf.paramParcelamento = :paramParcelamento");
        q.setParameter("paramParcelamento", paramParcelamento);
        List resultado = q.getResultList();
        return (List<ParamParcelamentoFaixa>) resultado;
    }

    public VOPessoa getPessoaDoProcessoParcelamento(ProcessoParcelamento processo) {
        Cadastro cadastro = processo.getCadastro();
        Pessoa retorno = null;
        if (cadastro != null) {
            if (cadastro instanceof CadastroImobiliario) {
                CadastroImobiliario ci = em.find(CadastroImobiliario.class, cadastro.getId());
                retorno = ci.getPropriedadeVigente().get(0).getPessoa();
            } else if (cadastro instanceof CadastroEconomico) {
                retorno = ((CadastroEconomico) cadastro).getPessoa();
            } else if (cadastro instanceof CadastroRural) {
                CadastroRural cr = em.find(CadastroRural.class, cadastro.getId());
                retorno = cr.getPropriedade().get(0).getPessoa();
            }
        } else if (processo.getPessoa() != null) {
            VOPessoa vo = new VOPessoa();
            vo.setId(processo.getPessoa().getId());
            vo.setCpfCnpj(processo.getPessoa().getCpf_Cnpj());
            vo.setNome(processo.getPessoa().getNome());
            vo.setTipoPessoa(processo.getPessoa() instanceof PessoaFisica ? TipoPessoa.FISICA.getSigla() : TipoPessoa.JURIDICA.getSigla());
            return vo;
        }
        VOPessoa vo = new VOPessoa();
        vo.setId(retorno.getId());
        vo.setCpfCnpj(retorno.getCpf_Cnpj());
        vo.setNome(retorno.getNome());
        vo.setTipoPessoa(retorno instanceof PessoaFisica ? TipoPessoa.FISICA.getSigla() : TipoPessoa.JURIDICA.getSigla());
        return vo;
    }

    public ItemProcessoParcelamento addItem(ProcessoParcelamento parcelamento, ResultadoParcela parcela) {
        ItemProcessoParcelamento item = new ItemProcessoParcelamento();
        item.setProcessoParcelamento(parcelamento);
        ParcelaValorDivida parcelaValorDivida = em.find(ParcelaValorDivida.class, parcela.getIdParcela());

        item.setSituacaoAnterior(Enum.valueOf(SituacaoParcela.class, parcela.getSituacaoNameEnum()));
        item.setParcelaValorDivida(parcelaValorDivida);
        item.setImposto(parcela.getValorImposto());
        item.setTaxa(parcela.getValorTaxa());
        item.setMulta(parcela.getValorMulta());
        item.setCorrecao(parcela.getValorCorrecao());
        item.setJuros(parcela.getValorJuros());
        item.setHonorarios(parcela.getValorHonorarios());
        parcelamento.getItensProcessoParcelamento().add(item);
        return item;
    }

    public void removeItem(ItemProcessoParcelamento itemProcesso) {
        if (itemProcesso == null || itemProcesso.getProcessoParcelamento() == null || itemProcesso.getProcessoParcelamento().getItensProcessoParcelamento() == null) {
            return;
        }
        itemProcesso.getProcessoParcelamento().getItensProcessoParcelamento().remove(itemProcesso);
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        String hql = "select distinct ci from CadastroImobiliario ci left join ci.propriedade p where ci.inscricaoCadastral like :parte or (p.pessoa in "
            + " (select pf from PessoaFisica pf where lower(pf.nome) like :parte or pf.cpf like :parte)) or (p"
            + ".pessoa in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like :parte or pj.cnpj like "
            + ":parte))";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        String hql = "select distinct bce from CadastroEconomico bce left join bce.sociedadeCadastroEconomico sce where"
            + " bce.inscricaoCadastral like :parte or "
            + "(sce.socio in (select pf from PessoaFisica pf where lower(pf.nome) like :parte or "
            + "pf.cpf like :parte)) or (sce.socio in (select pj from PessoaJuridica pj where "
            + "lower(pj.razaoSocial) like :parte or pj.cnpj like :parte))";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        String hql = "select distinct cr from CadastroRural cr "
            + " where lower(cr.nomePropriedade) like :parte"
            + " or lower(cr.codigo) like :parte"
            + " or lower(cr.numeroIncra) like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }

    @Override
    public ProcessoParcelamento recuperar(Object id) {
        ProcessoParcelamento p = em.find(ProcessoParcelamento.class, id);
        Hibernate.initialize(p.getPessoas());
        Hibernate.initialize(p.getItensProcessoParcelamento());
        Hibernate.initialize(p.getParcelasProcessoParcelamento());
        if (p.getDetentorArquivoComposicao() != null)
            Hibernate.initialize(p.getDetentorArquivoComposicao().getArquivosComposicao());
        for (ItemProcessoParcelamento item : p.getItensProcessoParcelamento()) {
            Hibernate.initialize(item.getParcelaValorDivida().getItensParcelaValorDivida());
        }
        if (p.getCancelamentoParcelamento() != null) {
            Hibernate.initialize(p.getCancelamentoParcelamento().getParcelas());
            Hibernate.initialize(p.getCancelamentoParcelamento().getItens());
        }
        Collections.sort(p.getItensProcessoParcelamento());
        return p;
    }

    public VOProcessoParcelamento recuperarIdNumeroSituacao(Long id) {
        String sql = "select pp.id, pp.numero, pp.situacaoparcelamento, ex.ano " +
            " from processoparcelamento pp " +
            " inner join exercicio ex on ex.id = pp.exercicio_id " +
            " where pp.id = :idParcelamento";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcelamento", id);
        Object[] obj = (Object[]) q.getResultList().get(0);
        VOProcessoParcelamento pp = new VOProcessoParcelamento();
        pp.setId(((BigDecimal) obj[0]).longValue());
        pp.setNumero(((BigDecimal) obj[1]).longValue());
        pp.setSituacao(SituacaoParcelamento.valueOf((String) obj[2]));
        pp.setAno(((BigDecimal) obj[3]).longValue());
        return pp;
    }

    public ProcessoParcelamento recuperarSimples(Object id) {
        ProcessoParcelamento p = em.find(ProcessoParcelamento.class, id);
        Hibernate.initialize(p.getPessoas());
        Hibernate.initialize(p.getItensProcessoParcelamento());
        Hibernate.initialize(p.getParcelasProcessoParcelamento());
        Collections.sort(p.getItensProcessoParcelamento());
        return p;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void cancelarProcessamento(ProcessoParcelamento processo, CancelamentoParcelamento cancelamentoParcelamento) {
        try {
            processo = recuperar(processo.getId());
            cancelarParcelasOriginadas(processo);
            cancelamentoParcelamento.setProcessoParcelamento(processo);

            processo.setCancelamentoParcelamento(cancelamentoParcelamento);
            processo = em.merge(processo);
            apagarDescontosConcedidos(processo);

            abaterValoresPagosDoParcelamentoCancelado(cancelamentoParcelamento, processo);

            ProcessoCalculoCancelamentoParcelamento processoCancelamento = buscarProcessoCancelamentoPorParcelamento(processo);
            if (processoCancelamento != null) {
                ParcelasCancelamentoParcelamento novaParcelaAberta = cancelamentoParcelamento.getNovaParcelaAbertoComValorResidual();
                gerarDebitoPorCancelamento(processoCancelamento, novaParcelaAberta.getParcela());
            }
            cancelarParcelamentosAnteriores(processo);
            reabrirParcelasOriginais(cancelamentoParcelamento);
            comunicaSofPlanAlteracaoParcelamento(processo);
        } catch (Exception ex) {
            logger.error("Erro ao cancelar o parcelamento " + processo.getNumeroCompostoComAno() + ":", ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cancelarParcelamentosAnteriores(ProcessoParcelamento processo) {
        try {
            Set<Long> parcelamentoAnteriores = Sets.newHashSet();
            List<Long> idsParcelas = Lists.newArrayList();
            for (ItemProcessoParcelamento itemProcessoParcelamento : processo.getItensProcessoParcelamento()) {
                idsParcelas.add(itemProcessoParcelamento.getParcelaValorDivida().getId());
            }
            parcelamentoAnteriores.addAll(buscarParcelamentoOriginaisDasParcelas(idsParcelas));

            if (!parcelamentoAnteriores.isEmpty()) {
                for (Long anterior : parcelamentoAnteriores) {
                    ProcessoParcelamento pp = recuperarSimples(anterior);
                    pp.setSituacaoParcelamento(SituacaoParcelamento.CANCELADO);
                    CancelamentoParcelamento cp = new CancelamentoParcelamento();
                    cp.setExercicio(processo.getCancelamentoParcelamento().getExercicio());
                    cp.setCodigo(cancelamentoParcelamentoFacade.buscarProximoCodigoPorExercicio(cp.getExercicio()));
                    cp.setProcessoParcelamento(pp);
                    cp.setDataCancelamento(new Date());
                    cp.setUsuario(processo.getCancelamentoParcelamento().getUsuario());
                    cp.setMotivo("Cancelado automaticamente pelo cancelamento do parcelamento " + processo.getNumeroCompostoComAno());
                    pp.setCancelamentoParcelamento(cp);
                    pp = em.merge(pp);
                    comunicaSofPlanAlteracaoParcelamento(pp);
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao cancelar o parcelamento como anterior " + processo.getNumeroCompostoComAno() + ":", ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void apagarDescontosConcedidos(ProcessoParcelamento processo) {
        Query query = em.createQuery("select mov from MovimentoValorParcelamento mov where mov.parcelamento = :parcelamento and mov.tipo = :tipo");
        query.setParameter("parcelamento", processo);
        query.setParameter("tipo", MovimentoValorParcelamento.Tipo.DESCONTO);
        List<MovimentoValorParcelamento> movimentos = query.getResultList();
        for (MovimentoValorParcelamento movimento : movimentos) {
            em.remove(movimento);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void reabrirParcelasOriginais(CancelamentoParcelamento cancelamentoParcelamento) {
        List<ParcelasCancelamentoParcelamento> abatidas = cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ABATIDAS);
        List<ParcelasCancelamentoParcelamento> originais = cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ORIGINAIS);

        for (ParcelasCancelamentoParcelamento parcelaCancelamento : originais) {
            if (!hasParcelaNaLista(abatidas, parcelaCancelamento)) {
                ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, parcelaCancelamento.getParcela().getId());
                SituacaoParcelaValorDivida ultimaSituacao = parcela.getSituacaoAtual();
                if (ultimaSituacao.getSituacaoParcela().isParcelado()) {
                    em.persist(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcela, ultimaSituacao.getSaldo()));
                }
            }
        }
    }

    private boolean hasParcelaNaLista(List<ParcelasCancelamentoParcelamento> abatidas, ParcelasCancelamentoParcelamento parcelaCancelamento) {
        for (ParcelasCancelamentoParcelamento abatida : abatidas) {
            if (abatida.getParcela().getId().equals(parcelaCancelamento.getParcela().getId())) {
                return true;
            }
        }
        return false;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void abaterValoresPagosDoParcelamentoCancelado(CancelamentoParcelamento cancelamentoParcelamento, ProcessoParcelamento parcelamento) {
        for (ParcelasCancelamentoParcelamento parcelaCancelamento : cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ABATIDAS)) {

            ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, parcelaCancelamento.getParcela().getId());
            em.persist(new SituacaoParcelaValorDivida(SituacaoParcela.PAGO_PARCELAMENTO, parcela, BigDecimal.ZERO, false, parcelaCancelamento.getReferencia()));

            PagamentoPorMovimentacaoTributaria pagamento = new PagamentoPorMovimentacaoTributaria();
            pagamento.setParcela(parcelaCancelamento.getParcela());
            pagamento.setImposto(parcelaCancelamento.getValorImposto());
            pagamento.setTaxa(parcelaCancelamento.getValorTaxa());
            pagamento.setJuros(parcelaCancelamento.getValorJuros());
            pagamento.setMulta(parcelaCancelamento.getValorMulta());
            pagamento.setCorrecao(parcelaCancelamento.getValorCorrecao());
            pagamento.setHonorarios(parcelaCancelamento.getValorHonorarios());
            em.persist(pagamento);
        }

        ParcelasCancelamentoParcelamento novaParcelaAberta = cancelamentoParcelamento.getNovaParcelaAbertoComValorResidual();
        if (novaParcelaAberta != null) {
            criarCalculoComParcelaNaoPagaIntegralmente(novaParcelaAberta, parcelamento);
        }
    }

    private void criarCalculoComParcelaNaoPagaIntegralmente(ParcelasCancelamentoParcelamento parcelaCancelamento, ProcessoParcelamento parcelamento) {
        ParcelaValorDivida parcelaValorDivida = em.find(ParcelaValorDivida.class, parcelaCancelamento.getParcela().getId());
        CancelamentoParcelamento cancelamento = parcelaCancelamento.getCancelamentoParcelamento();
        ProcessoCalculoCancelamentoParcelamento processoCalculo = new ProcessoCalculoCancelamentoParcelamento();
        processoCalculo.setExercicio(parcelaValorDivida.getValorDivida().getExercicio());
        processoCalculo.setDataLancamento(cancelamento.getDataCancelamento());
        processoCalculo.setDivida(parcelaValorDivida.getValorDivida().getDivida());

        cancelamento.setValorEfetivo(parcelaCancelamento.getValorTotal());
        cancelamento.setValorReal(parcelaCancelamento.getValorTotal());
        cancelamento.setParcelaValorDivida(parcelaCancelamento.getParcela());
        cancelamento.setVencimento(parcelaCancelamento.getVencimento());
        if ("".equals(cancelamento.getReferencia())) {
            String referencia = "";
            Collections.sort(cancelamento.getParcelas());
            for (ParcelasCancelamentoParcelamento abatida : cancelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO)) {
                if ("".equals(referencia)) {
                    referencia = abatida.getReferencia();
                }
            }
            if (!"".equals(referencia)) {
                cancelamento.setReferencia(referencia);
            }
        }

        Map<Tributo.TipoTributo, BigDecimal> valorResidualPorTipoTributo = criarMapaDeValoresResiduaisPorTipoTributo(parcelaCancelamento);
        Map<Tributo, BigDecimal> valorPorTributo = criarMapaValoresOriginaisPorTributo(parcelaValorDivida);
        criarAndAdicionarItensCancelamento(cancelamento, valorPorTributo, valorResidualPorTipoTributo);

        cancelamento.setDividaAtivaAjuizada(parcelaValorDivida.getDividaAtivaAjuizada());
        cancelamento.setDividaAtiva(parcelaValorDivida.getDividaAtiva());
        cancelamento.setQuantidadeParcela(consultaDebitoFacade.getQuantidadeParcelasValorDivida(parcelaValorDivida.getValorDivida(), parcelaValorDivida.getOpcaoPagamento()));
        cancelamento.setSequenciaParcela(parcelaValorDivida.getSequenciaParcela());

        cancelamento.setProcessoCalculo(processoCalculo);
        cancelamento.setCadastro(parcelamento.getCadastro());
        gerarPessoasCalculo(parcelamento, cancelamento);

        processoCalculo.getCancelamentos().add(cancelamento);
        processoCalculo.setDividaAtiva(parcelaValorDivida.isDividaAtiva());
        processoCalculo.setDividaAtivaAjuizada(parcelaValorDivida.isDividaAtivaAjuizada());
        processoCalculo.setReferencia(cancelamento.getReferencia());
        processoCalculo = em.merge(processoCalculo);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gerarDebitoPorCancelamento(ProcessoCalculoCancelamentoParcelamento processoCalculo, ParcelaValorDivida parcelaValorDivida) {
        try {
            processoCalculo = em.find(ProcessoCalculoCancelamentoParcelamento.class, processoCalculo.getId());
            parcelaValorDivida = em.find(ParcelaValorDivida.class, parcelaValorDivida.getId());

            geraValorDividaCancelamentoParcelamento.setOpcaoPagamentoParcela(parcelaValorDivida.getOpcaoPagamento());
            geraValorDividaCancelamentoParcelamento.geraDebito(processoCalculo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ProcessoCalculoCancelamentoParcelamento buscarProcessoCancelamentoPorParcelamento(ProcessoParcelamento parcelamento) {
        Query q = em.createQuery("select cancelamento.processoCalculo from CancelamentoParcelamento cancelamento " +
            "where cancelamento.processoParcelamento = :processoParcelamento");
        q.setParameter("processoParcelamento", parcelamento);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            ProcessoCalculoCancelamentoParcelamento processoCalulo = (ProcessoCalculoCancelamentoParcelamento) resultList.get(0);
            Hibernate.initialize(processoCalulo.getCancelamentos());
            return processoCalulo;
        }
        return null;
    }

    private Map<Tributo, BigDecimal> criarMapaValoresOriginaisPorTributo(ParcelaValorDivida parcelaValorDivida) {
        Map<Tributo, BigDecimal> valorPorTributo = Maps.newHashMap();
        for (ItemParcelaValorDivida itemParcela : parcelaValorDivida.getItensParcelaValorDivida()) {
            Tributo tributo = itemParcela.getItemValorDivida().getTributo();
            if (valorPorTributo.containsKey(tributo)) {
                valorPorTributo.put(tributo, valorPorTributo.get(tributo).add(itemParcela.getValor()));
            } else {
                valorPorTributo.put(tributo, itemParcela.getValor());
            }
        }
        return valorPorTributo;
    }

    private Map<Tributo.TipoTributo, BigDecimal> criarMapaDeValoresResiduaisPorTipoTributo(ParcelasCancelamentoParcelamento parcelaCancelamento) {
        Map<Tributo.TipoTributo, BigDecimal> valorResidualPorTipoTributo = Maps.newHashMap();
        valorResidualPorTipoTributo.put(Tributo.TipoTributo.IMPOSTO, parcelaCancelamento.getValorImposto());
        valorResidualPorTipoTributo.put(Tributo.TipoTributo.TAXA, parcelaCancelamento.getValorTaxa());
        valorResidualPorTipoTributo.put(Tributo.TipoTributo.JUROS, parcelaCancelamento.getValorJuros());
        valorResidualPorTipoTributo.put(Tributo.TipoTributo.MULTA, parcelaCancelamento.getValorMulta());
        valorResidualPorTipoTributo.put(Tributo.TipoTributo.CORRECAO, parcelaCancelamento.getValorCorrecao());
        valorResidualPorTipoTributo.put(Tributo.TipoTributo.HONORARIOS, parcelaCancelamento.getValorHonorarios());
        return valorResidualPorTipoTributo;
    }

    private void criarAndAdicionarItensCancelamento(CancelamentoParcelamento cancelamento, Map<Tributo, BigDecimal> valorPorTributo, Map<Tributo.TipoTributo, BigDecimal> valorResidualPorTipoTributo) {
        for (Tributo tributo : valorPorTributo.keySet()) {
            if (tributo.getTipoTributo().equals(Tributo.TipoTributo.IMPOSTO) ||
                tributo.getTipoTributo().equals(Tributo.TipoTributo.TAXA)) {

                BigDecimal porcentagem = porcentagemDoValorTotal(valorPorTributo.get(tributo), valorPorTributo.get(tributo));
                if (porcentagem.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal residual = valorResidualPorTipoTributo.get(tributo.getTipoTributo());
                    criarItemCancelamento(cancelamento, tributo, residual);

                    criarItensCancelamentoParaAcrescimos(cancelamento, valorResidualPorTipoTributo, tributo, porcentagem, Tributo.TipoTributo.JUROS);
                    criarItensCancelamentoParaAcrescimos(cancelamento, valorResidualPorTipoTributo, tributo, porcentagem, Tributo.TipoTributo.MULTA);
                    criarItensCancelamentoParaAcrescimos(cancelamento, valorResidualPorTipoTributo, tributo, porcentagem, Tributo.TipoTributo.CORRECAO);
                    criarItensCancelamentoParaAcrescimos(cancelamento, valorResidualPorTipoTributo, tributo, porcentagem, Tributo.TipoTributo.HONORARIOS);
                }
            }
        }
    }

    private void criarItensCancelamentoParaAcrescimos(CancelamentoParcelamento cancelamento,
                                                      Map<Tributo.TipoTributo, BigDecimal> valorResidualPorTipoTributo,
                                                      Tributo tributo, BigDecimal porcentagem, Tributo.TipoTributo tipoTributo) {
        BigDecimal residual;
        if (valorResidualPorTipoTributo.get(tipoTributo) != null) {
            residual = valorResidualPorTipoTributo.get(tipoTributo);
            criarItemCancelamento(cancelamento, tributo.getTributoPorTipo(tipoTributo), porcentagem.multiply(residual));
        }
    }

    private void criarItemCancelamento(CancelamentoParcelamento cancelamento, Tributo tributo, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            ItemCancelamentoParcelamento itemCancelamento = new ItemCancelamentoParcelamento();
            itemCancelamento.setTributo(tributo);
            itemCancelamento.setValor(valor);
            itemCancelamento.setCancelamentoParcelamento(cancelamento);
            cancelamento.getItens().add(itemCancelamento);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cancelarParcelasOriginadas(ProcessoParcelamento processo) {
        List<ResultadoParcela> resultados = recuperaParcelasOriginadasParcelamento(processo.getId());

        for (ResultadoParcela resultado : resultados) {
            if (SituacaoParcela.EM_ABERTO.equals(resultado.getSituacaoEnumValue())) {
                ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, resultado.getIdParcela());
                em.persist(new SituacaoParcelaValorDivida(SituacaoParcela.PARCELAMENTO_CANCELADO, parcela, parcela.getUltimaSituacao().getSaldo()));
            } else if (SituacaoParcela.PAGO_PARCELAMENTO.equals(resultado.getSituacaoEnumValue())) {
                ParcelaValorDivida parcela = consultaDebitoFacade.buscarParcelaDoValorResidualDaParcela(em.find(ParcelaValorDivida.class, resultado.getIdParcela()));
                if (parcela != null) {
                    em.persist(new SituacaoParcelaValorDivida(SituacaoParcela.PARCELAMENTO_CANCELADO, parcela, parcela.getUltimaSituacao().getSaldo()));
                }
            }
        }
    }

    public String buscarSituacaoParcelaPorParcelaAndParcelamento(ParcelaValorDivida pvd, String numeroProcessoParcelamento) {
        String sql = "select coalesce(situacao.referencia, cal.referencia) as referencia " +
            " from situacaoparcelavalordivida situacao " +
            " inner join parcelavalordivida pvd on situacao.parcela_id = pvd.id " +
            " inner join itemprocessoparcelamento item on item.parcelavalordivida_id = pvd.id " +
            " inner join valordivida vd on vd.id = pvd.valordivida_id " +
            " inner join calculo cal on cal.id = vd.calculo_id " +
            " where pvd.id = :pvd and situacaoparcela = :situacao order by situacao.datalancamento desc, situacao.id desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pvd", pvd.getId());
        q.setParameter("situacao", SituacaoParcela.EM_ABERTO.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            String situacao = (String) resultList.get(0);
            return removerPalavraParcelamentoCasoExistaNaString(numeroProcessoParcelamento, situacao);
        }
        try {
            pvd = em.find(ParcelaValorDivida.class, pvd.getId());
            if (!pvd.getValorDivida().getCalculo().getTipoCalculo().equals(Calculo.TipoCalculo.PARCELAMENTO)) {
                String situacao = pvd.getValorDivida().getCalculo().getTipoCalculo()
                    .getGeradorReferenciaParcela().geraReferencia(pvd.getId());
                return removerPalavraParcelamentoCasoExistaNaString(numeroProcessoParcelamento, situacao);
            }
        } catch (Exception e) {
            log.debug("Não foi possível encontrar ou gerar a situação da parcela {}, {}", pvd.getId(), e.getMessage());
        }
        return " ";
    }

    private String removerPalavraParcelamentoCasoExistaNaString(String numeroProcessoParcelamento, String situacao) {
        if (situacao.contains("Parcelamento: " + numeroProcessoParcelamento)) {
            if (!"".equals(numeroProcessoParcelamento)) {
                situacao = situacao.replace("Parcelamento: " + numeroProcessoParcelamento, "");
            } else {
                situacao = situacao.replace(situacao.substring(situacao.indexOf("Parcelamento:")), "");
            }
        }
        if (situacao.contains("Reparcelamento: " + numeroProcessoParcelamento)) {
            if (!"".equals(numeroProcessoParcelamento)) {
                situacao = situacao.replace("Reparcelamento: " + numeroProcessoParcelamento, "");
            } else {
                situacao = situacao.replace(situacao.substring(situacao.indexOf("Reparcelamento:")), "");
            }

        }
        return situacao;
    }

    public List<ParcelasCancelamentoParcelamento> atualizarValorComIndiceUfm(List<ParcelasCancelamentoParcelamento> parcelas) {
        List<ParcelasCancelamentoParcelamento> parcelasPagasAtualizadas = Lists.newArrayList();
        for (ParcelasCancelamentoParcelamento parcela : parcelas) {
            ParcelasCancelamentoParcelamento atualizada = new ParcelasCancelamentoParcelamento(parcela);
            BigDecimal indice = BigDecimal.ONE;
            try {
                indice = getMoedaFacade().calcularIndiceAtualizaoDataAteHoje(parcela.getPagamento() != null ? parcela.getPagamento() : parcela.getVencimento());
            } catch (Exception e) {
                logger.error("Erro atualizarValorComIndiceUfm:", e);
            }
            BigDecimal diferencaImpostoAtualizado = (parcela.getValorImposto().multiply(indice)).subtract(parcela.getValorImposto());
            BigDecimal diferencaTaxaAtualizado = (parcela.getValorTaxa().multiply(indice)).subtract(parcela.getValorTaxa());

            atualizada.setValorImposto(parcela.getValorImposto());
            atualizada.setValorTaxa(parcela.getValorTaxa());
            atualizada.setValorJuros(parcela.getValorJuros().multiply(indice));
            atualizada.setValorMulta(parcela.getValorMulta().multiply(indice));
            atualizada.setValorCorrecao((parcela.getValorCorrecao().multiply(indice)).add(diferencaImpostoAtualizado).add(diferencaTaxaAtualizado));
            atualizada.setValorHonorarios(parcela.getValorHonorarios().multiply(indice));
            atualizada.setValorDesconto(parcela.getValorDesconto().multiply(indice));
            atualizada.setIndiceAtualizacao(indice);
            atualizada.setValorPago(atualizada.getValorTotal());
            atualizada.setTipoParcelaCancelamento(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_PAGAS_ATUALIZADAS);
            parcelasPagasAtualizadas.add(atualizada);
        }
        return parcelasPagasAtualizadas;
    }

    public List<ParcelasCancelamentoParcelamento> calcularParcelasAbatidasComValorTotalPagoAtualizado(CancelamentoParcelamento cancelamentoParcelamento, List<ParcelasCancelamentoParcelamento> parcelasOriginaisAtualizadas) {
        List<ParcelasCancelamentoParcelamento> abatidas = Lists.newArrayList();
        BigDecimal valorAbatido = BigDecimal.ZERO;
        BigDecimal valorTotalPagoAtualizado = getValorTotalPago(cancelamentoParcelamento);

        for (ParcelasCancelamentoParcelamento parcela : parcelasOriginaisAtualizadas) {
            ParcelasCancelamentoParcelamento parcelaAbatida = new ParcelasCancelamentoParcelamento(parcela);
            parcelaAbatida.setTipoParcelaCancelamento(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ABATIDAS);
            parcelaAbatida.setSituacaoParcela(SituacaoParcela.PAGO_PARCELAMENTO);

            if (valorAbatido.compareTo(valorTotalPagoAtualizado) < 0) {
                BigDecimal valorPago = BigDecimal.ZERO;
                if (valorAbatido.add(parcelaAbatida.getValorTotalAtualizado()).compareTo(valorTotalPagoAtualizado) < 0) {
                    valorPago = parcelaAbatida.getValorTotalAtualizado();
                } else {
                    valorPago = valorTotalPagoAtualizado.subtract(valorAbatido);
                }
                parcelaAbatida.setValorPago(valorPago);
                valorAbatido = valorAbatido.add(valorPago);

                corrigirValoresProporcionalmentoDeAcordoComValorPago(cancelamentoParcelamento, parcelaAbatida);
                abatidas.add(parcelaAbatida);
            }
        }
        return abatidas;
    }

    private void corrigirValoresProporcionalmentoDeAcordoComValorPago(CancelamentoParcelamento cancelamentoParcelamento, ParcelasCancelamentoParcelamento parcela) {
        if (parcela.getValorPago().compareTo(BigDecimal.ZERO) > 0 && parcela.getValorTotal().compareTo(parcela.getValorPago()) != 0) {

            BigDecimal valorImposto = parcela.getValorImpostoAtualizado();
            BigDecimal valorTaxa = parcela.getValorTaxaAtualizado();
            BigDecimal valorJuros = parcela.getValorJurosAtualizado();
            BigDecimal valorMulta = parcela.getValorMultaAtualizado();
            BigDecimal valorCorrecao = parcela.getValorCorrecaoAtualizado();
            BigDecimal valorHonorarios = parcela.getValorHonorariosAtualizado();


            BigDecimal porcentagemDeImpostoDoValorTotal = valorImposto.multiply(BigDecimal.valueOf(1L)).divide(parcela.getValorTotalAtualizado(), 8, RoundingMode.HALF_UP);
            BigDecimal porcentagemDeTaxaDoValorTotal = valorTaxa.multiply(BigDecimal.valueOf(1L)).divide(parcela.getValorTotalAtualizado(), 8, RoundingMode.HALF_UP);
            BigDecimal porcentagemDeJurosDoValorTotal = valorJuros.multiply(BigDecimal.valueOf(1L)).divide(parcela.getValorTotalAtualizado(), 8, RoundingMode.HALF_UP);
            BigDecimal porcentagemDeMultaDoValorTotal = valorMulta.multiply(BigDecimal.valueOf(1L)).divide(parcela.getValorTotalAtualizado(), 8, RoundingMode.HALF_UP);
            BigDecimal porcentagemDeCorrecaoDoValorTotal = valorCorrecao.multiply(BigDecimal.valueOf(1L)).divide(parcela.getValorTotalAtualizado(), 8, RoundingMode.HALF_UP);
            BigDecimal porcentagemDeHonorariosDoValorTotal = valorHonorarios.multiply(BigDecimal.valueOf(1L)).divide(parcela.getValorTotalAtualizado(), 8, RoundingMode.HALF_UP);


            parcela.setValorImposto(parcela.getValorPago().multiply(porcentagemDeImpostoDoValorTotal));
            parcela.setValorTaxa(parcela.getValorPago().multiply(porcentagemDeTaxaDoValorTotal));
            parcela.setValorJuros(parcela.getValorPago().multiply(porcentagemDeJurosDoValorTotal));
            parcela.setValorMulta(parcela.getValorPago().multiply(porcentagemDeMultaDoValorTotal));
            parcela.setValorCorrecao(parcela.getValorPago().multiply(porcentagemDeCorrecaoDoValorTotal));
            parcela.setValorHonorarios(parcela.getValorPago().multiply(porcentagemDeHonorariosDoValorTotal));

        }
    }

    public List<ParcelasCancelamentoParcelamento> calcularParcelasEmAbertoComValorTotalPagoAtualizado(List<ParcelasCancelamentoParcelamento> parcelasOriginais, List<ParcelasCancelamentoParcelamento> parcelasAbatidas, CancelamentoParcelamento cancelamentoParcelamento) {
        List<ParcelasCancelamentoParcelamento> parcelasAbertas = Lists.newArrayList();
        for (ParcelasCancelamentoParcelamento parcela : parcelasOriginais) {
            ParcelasCancelamentoParcelamento aberta = new ParcelasCancelamentoParcelamento(parcela);
            aberta.setTipoParcelaCancelamento(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO);

            aberta.setReferencia(removerPalavraParcelamentoCasoExistaNaString("", parcela.getReferencia()));
            aberta.setValorPago(BigDecimal.ZERO);

            for (ParcelasCancelamentoParcelamento abatida : parcelasAbatidas) {
                cancelamentoParcelamento.setReferencia(aberta.getReferencia());
                if (aberta.getParcela().getId().equals(abatida.getParcela().getId()) && abatida.getValorTotalAtualizado().compareTo(aberta.getValorTotalAtualizado()) != 0) {
                    aberta.setValorImposto(aberta.getValorImpostoAtualizado().subtract(abatida.getValorImpostoAtualizado()));
                    aberta.setValorTaxa(aberta.getValorTaxaAtualizado().subtract(abatida.getValorTaxaAtualizado()));
                    aberta.setValorJuros(aberta.getValorJurosAtualizado().subtract(abatida.getValorJurosAtualizado()));
                    aberta.setValorMulta(aberta.getValorMultaAtualizado().subtract(abatida.getValorMultaAtualizado()));
                    aberta.setValorCorrecao(aberta.getValorCorrecaoAtualizado().subtract(abatida.getValorCorrecaoAtualizado()));
                    aberta.setValorHonorarios(aberta.getValorHonorariosAtualizado().subtract(abatida.getValorHonorariosAtualizado()));
                    parcelasAbertas.add(aberta);
                }
            }
            if (!verificarSeContemNaLista(parcelasAbatidas, aberta)) {
                parcelasAbertas.add(aberta);
            }
        }
        return parcelasAbertas;
    }

    private boolean verificarSeContemNaLista(List<ParcelasCancelamentoParcelamento> parcelasAbatidas, ParcelasCancelamentoParcelamento parcela) {
        boolean contem = false;
        for (ParcelasCancelamentoParcelamento parcelasDaLista : parcelasAbatidas) {
            if (parcela.getParcela().getId().equals(parcelasDaLista.getParcela().getId())) {
                contem = true;
                break;
            }
        }
        return contem;
    }

    public List<ProcessoParcelamento> buscarParcelamentosFinalizadosPorNumero(String parte) {
        String sql = "select p.id, p.numero, p.numeroreparcelamento, p.exercicio_id, p.dataprocessoparcelamento " +
            " from processoparcelamento p " +
            " where cast(p.numero as varchar(50)) = :numero" +
            "   and p.situacaoparcelamento in (:situacao)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numero", parte);
        q.setParameter("situacao", Lists.newArrayList(SituacaoParcelamento.FINALIZADO.name(), SituacaoParcelamento.REATIVADO.name()));
        List<ProcessoParcelamento> parcelamentos = Lists.newArrayList();

        List<Object[]> lista = q.getResultList();
        for (Object[] obj : lista) {
            ProcessoParcelamento pp = new ProcessoParcelamento();
            pp.setId(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
            pp.setNumero(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
            pp.setNumeroReparcelamento(obj[2] != null ? ((BigDecimal) obj[2]).intValue() : null);
            pp.setExercicio(obj[3] != null ? exercicioFacade.recuperar(((BigDecimal) obj[3]).longValue()) : null);
            pp.setDataProcessoParcelamento(obj[4] != null ? (Date) obj[4] : null);
            pp.setSituacaoParcelamento(SituacaoParcelamento.FINALIZADO);
            parcelamentos.add(pp);
        }
        return parcelamentos;
    }

    public List<ResultadoParcela> buscarParcelaDoParcelamentoDaDividaAtiva(Long idParcela) {
        List<ProcessoParcelamento> parcelamentos = buscarParcelamentosDaParcela(idParcela);
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ProcessoParcelamento processoParcelamento : parcelamentos) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.CALCULO_ID,
                br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL,
                processoParcelamento.getId());
            consultaParcela.executaConsulta();
            for (ResultadoParcela resultado : consultaParcela.getResultados()) {
                if (!resultado.isCancelado() && !SituacaoParcela.PARCELAMENTO_CANCELADO.equals(resultado.getSituacaoEnumValue())) {
                    parcelas.add(resultado);
                }
            }
        }
        return parcelas;
    }

    public void reativarProcessoParcelamento(ProcessoParcelamento processoParcelamento, CancelamentoParcelamento cancelamentoParcelamento) {
        processoParcelamentoDAO.updateCancelarResidualCancelamentoParcelamento(cancelamentoParcelamento);
        processoParcelamentoDAO.deleteParcelasCancelamentoParcelamento(cancelamentoParcelamento);
        processoParcelamentoDAO.deleteItensCancelamentoParcelamento(cancelamentoParcelamento);
        processoParcelamentoDAO.deleteCancelamentoParcelamento(processoParcelamento);
        processoParcelamentoDAO.updateParcelasProcessoParcelamento(processoParcelamento);
        processoParcelamentoDAO.updateOrigemProcessoParcelamento(processoParcelamento);
        processoParcelamentoDAO.updateDadosReativacaoProcessoParcelamento(processoParcelamento);
        processoParcelamentoDAO.updateSituacaoProcessoParcelamento(processoParcelamento, SituacaoParcelamento.REATIVADO);
        processoParcelamentoDAO.updateAtivarDAMsCanceladosNaoVencidos(processoParcelamento);
    }

    public BloqueioParcelamento hasBloqueioParcelamento(ProcessoParcelamento processoParcelamento) {
        Cadastro cadastro = processoParcelamento.getCadastro();
        List<Long> idPessoas = Lists.newArrayList();
        if (cadastro != null) {
            if (cadastro instanceof CadastroImobiliario) {
                CadastroImobiliario ci = em.find(CadastroImobiliario.class, cadastro.getId());
                for (Propriedade propriedade : ci.getPropriedadeVigente()) {
                    idPessoas.add(propriedade.getPessoa().getId());
                }
            } else if (cadastro instanceof CadastroEconomico) {
                idPessoas.add(((CadastroEconomico) cadastro).getPessoa().getId());
            } else if (cadastro instanceof CadastroRural) {
                CadastroRural cr = em.find(CadastroRural.class, cadastro.getId());
                for (PropriedadeRural propriedadeRural : cr.getPropriedade()) {
                    idPessoas.add(propriedadeRural.getPessoa().getId());
                }
            }
        } else if (processoParcelamento.getPessoa() != null) {
            idPessoas.add(processoParcelamento.getPessoa().getId());
        }
        String sql = "select b.* from BloqueioParcelamento b where b.pessoa_id in (:idPessoas)" +
            " and trunc(:dataAtual) between trunc(b.dataInicial) and trunc(coalesce(b.dataFinal, :dataAtual))";
        Query q = em.createNativeQuery(sql, BloqueioParcelamento.class);
        q.setParameter("idPessoas", idPessoas);
        q.setParameter("dataAtual", new Date());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (BloqueioParcelamento) resultList.get(0);
    }

    public void salvarPermissaoCancelamento(ProcessoParcelamento processoParcelamento) {
        String update = "update ProcessoParcelamento set permitirCancelarParcelamento = :bloquear, faixaDesconto_id = null where id = :idParcelamento";
        Query q = em.createNativeQuery(update);
        q.setParameter("idParcelamento", processoParcelamento.getId());
        q.setParameter("bloquear", processoParcelamento.getPermitirCancelarParcelamento());
        q.executeUpdate();
    }

    public void comunicarProcuradoria(ProcessoParcelamento processoParcelamento,
                                      List<ResultadoParcela> parcelasOriginadas) {
        int qtdePagas = 0;
        for (ResultadoParcela originada : parcelasOriginadas) {
            if (originada.isPago()) {
                qtdePagas++;
            }
        }
        if (qtdePagas >= 1 && !processoParcelamento.getParamParcelamento().getSituacaoDebito().equals(SituacaoDebito.EXERCICIO)) {
            comunicaInsercaoAlteracaoParcelamento(processoParcelamento);
        } else {
            throw new ValidacaoException("Não existem parcelas pagas suficientes para comunicar o parcelamento.");
        }
    }

    public Arquivo gerarTermoAssinado(ProcessoParcelamento processoParcelamento) {
        processoParcelamento = recuperar(processoParcelamento.getId());

        validarEmissaoTermoAssinado(processoParcelamento);
        return processoParcelamento.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo();
    }

    private void validarEmissaoTermoAssinado(ProcessoParcelamento processoParcelamento) {
        if (processoParcelamento.getDetentorArquivoComposicao() == null
            || processoParcelamento.getDetentorArquivoComposicao().getArquivoComposicao() == null
            || processoParcelamento.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo() == null) {
            throw new ValidacaoException("Não existe termo assinado anexo a esse processo de parcelamento");
        }
    }

    public static class ValoresParcela {
        public BigDecimal imposto, taxa, juros, multa, correcao, honorarios, desconto;

        public ValoresParcela() {
            imposto = BigDecimal.ZERO;
            taxa = BigDecimal.ZERO;
            juros = BigDecimal.ZERO;
            multa = BigDecimal.ZERO;
            correcao = BigDecimal.ZERO;
            honorarios = BigDecimal.ZERO;
            desconto = BigDecimal.ZERO;
        }

        public BigDecimal getTotalComDesconto() {
            return (imposto.add(taxa).add(juros).add(multa).add(correcao).add(honorarios)).subtract(desconto);
        }

        public BigDecimal getTotalComDescontoSemHonorarios() {
            return (imposto.add(taxa).add(juros).add(multa).add(correcao)).subtract(desconto);
        }

        public static ValoresParcela calcularValores(ProcessoParcelamento parcelamento,
                                                     BigDecimal valorParcela, ConfiguracaoAcrescimos configuracaoAcrescimos) {
            ValoresParcela valores = new ValoresParcela();
            BigDecimal percentualImposto = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalImposto());
            if (percentualImposto.compareTo(BigDecimal.ZERO) > 0) {
                valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
                if (valores.imposto.compareTo(BigDecimal.ZERO) <= 0) {
                    percentualImposto = percentualImpostoAcrescidoDe(parcelamento, parcelamento.getValorTotalTaxa());
                    valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
                    if (valores.imposto.compareTo(BigDecimal.ZERO) <= 0) {
                        percentualImposto = percentualImpostoAcrescidoDe(parcelamento, parcelamento.getValorTotalTaxa().add(parcelamento.getValorTotalJuros()));
                        valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
                    }
                    if (valores.imposto.compareTo(BigDecimal.ZERO) <= 0) {
                        percentualImposto = percentualImpostoAcrescidoDe(parcelamento,
                            parcelamento.getValorTotalTaxa().add(parcelamento.getValorTotalJuros().add(parcelamento.getValorTotalMulta())));
                        valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
                    }
                    if (valores.imposto.compareTo(BigDecimal.ZERO) <= 0) {
                        percentualImposto = percentualImpostoAcrescidoDe(parcelamento,
                            parcelamento.getValorTotalTaxa()
                                .add(parcelamento.getValorTotalJuros().add(parcelamento.getValorTotalMulta().add(parcelamento.getValorTotalCorrecao()))));
                        valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
                    }
                }
            }
            BigDecimal percentualTaxa = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalTaxa());
            if (percentualTaxa.compareTo(BigDecimal.ZERO) > 0) {
                valores.taxa = multiplicaPercentual(valorParcela, percentualTaxa);
            }
            BigDecimal percentualJuros = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalJuros());
            if (percentualJuros.compareTo(BigDecimal.ZERO) > 0) {
                valores.juros = multiplicaPercentual(valorParcela, percentualJuros);
            }
            BigDecimal percentualMulta = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalMulta());
            if (percentualMulta.compareTo(BigDecimal.ZERO) > 0) {
                valores.multa = multiplicaPercentual(valorParcela, percentualMulta);
            }
            BigDecimal percentualCorrecao = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalCorrecao());
            if (percentualCorrecao.compareTo(BigDecimal.ZERO) > 0) {
                valores.correcao = multiplicaPercentual(valorParcela, percentualCorrecao);
            }
            BigDecimal percentualDesconto = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalDesconto());
            if (percentualDesconto.compareTo(BigDecimal.ZERO) > 0) {
                valores.desconto = multiplicaPercentual(valorParcela, percentualDesconto);
            }
            if (parcelamento.getValorTotalHonorarios() != null && parcelamento.getValorTotalHonorarios().compareTo(BigDecimal.ZERO) > 0) {
                valores.honorarios = CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), parcelamento.getDataProcessoParcelamento(),
                    valores.juros.add(valores.multa.add(valores.correcao.add(valores.imposto.add(valores.taxa)))).subtract(valores.desconto));
            }
            ajustarDiferencaItensParcela(valorParcela, valores);
            return valores;
        }

        private static BigDecimal multiplicaPercentual(BigDecimal valorParcela, BigDecimal percentualImposto) {
            return valorParcela.multiply(percentualImposto).setScale(2, RoundingMode.HALF_UP);
        }

        private static BigDecimal percentualImpostoAcrescidoDe(ProcessoParcelamento selecionado, BigDecimal somar) {
            return descobrirPercentual(selecionado.getTotalGeral(),
                selecionado.getValorTotalImposto().add(somar));
        }

        public static ValoresParcela calcularValoresParcela(ProcessoParcelamentoControlador.Valores valoresParcelar,
                                                            BigDecimal valorParcela, ConfiguracaoAcrescimos configuracaoAcrescimos) {
            ValoresParcela valores = new ValoresParcela();
            if (valoresParcelar.total.compareTo(BigDecimal.ZERO) > 0) {
                valores.imposto = (valoresParcelar.imposto.multiply(valorParcela)).divide(valoresParcelar.total, 8, RoundingMode.HALF_UP);
                valores.taxa = (valoresParcelar.taxa.multiply(valorParcela)).divide(valoresParcelar.total, 8, RoundingMode.HALF_UP);
                valores.juros = (valoresParcelar.juros.multiply(valorParcela)).divide(valoresParcelar.total, 8, RoundingMode.HALF_UP);
                valores.multa = (valoresParcelar.multa.multiply(valorParcela)).divide(valoresParcelar.total, 8, RoundingMode.HALF_UP);
                valores.correcao = (valoresParcelar.correcao.multiply(valorParcela)).divide(valoresParcelar.total, 8, RoundingMode.HALF_UP);

                if (valoresParcelar.honorarios != null && valoresParcelar.honorarios.compareTo(BigDecimal.ZERO) > 0) {
                    valores.honorarios = CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), new Date(),
                        valores.juros.add(valores.multa.add(valores.correcao.add(valores.imposto.add(valores.taxa)))).subtract(valores.desconto));
                }
            }
            ajustarDiferencaItensParcela(valorParcela, valores);
            return valores;
        }

        private static BigDecimal descobrirPercentual(BigDecimal total, BigDecimal descobrir) {
            return (descobrir.multiply(CEM).divide(total, 12, RoundingMode.HALF_UP)).divide(CEM, 8, RoundingMode.HALF_UP);
        }

        private static void ajustarDiferencaItensParcela(BigDecimal valorParcela, ValoresParcela valores) {
            BigDecimal total = valores.imposto
                .add(valores.taxa)
                .add(valores.juros)
                .add(valores.multa)
                .add(valores.correcao)
                .add(valores.honorarios)
                .subtract(valores.desconto);
            if (total.compareTo(valorParcela) != 0) {
                BigDecimal diferenca = valorParcela.subtract(total);
                valorParcela = valorParcela.compareTo(BigDecimal.ZERO) != 0 ? valorParcela : total;
                try {
                    BigDecimal diferencaAbatida = BigDecimal.ZERO;
                    if (valores.juros != null && valores.juros.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal diferencaJuros = calcularProporcaoDiferencaParaAbater(valorParcela, valores.juros, diferenca);
                        valores.juros = valores.juros.add(diferencaJuros);
                        diferencaAbatida = diferencaAbatida.add(diferencaJuros);
                    }
                    if (diferencaAbatida.compareTo(diferenca) != 0 && valores.multa != null && valores.multa.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal diferencaMulta = calcularProporcaoDiferencaParaAbater(valorParcela, valores.multa, diferenca);
                        valores.multa = valores.multa.add(diferencaMulta);
                        diferencaAbatida = diferencaAbatida.add(diferencaMulta);
                    }
                    if (diferencaAbatida.compareTo(diferenca) != 0 && valores.correcao != null && valores.correcao.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal diferencaCorrecao = calcularProporcaoDiferencaParaAbater(valorParcela, valores.correcao, diferenca);
                        valores.correcao = valores.correcao.add(diferencaCorrecao);
                        diferencaAbatida = diferencaAbatida.add(diferencaCorrecao);
                    }
                    if (diferencaAbatida.compareTo(diferenca) != 0 && valores.honorarios != null && valores.honorarios.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal diferencaHonorarios = calcularProporcaoDiferencaParaAbater(valorParcela, valores.honorarios, diferenca);
                        valores.honorarios = valores.honorarios.add(diferencaHonorarios);
                        diferencaAbatida = diferencaAbatida.add(diferencaHonorarios);
                    }
                    if (diferencaAbatida.compareTo(diferenca) != 0 && valores.imposto != null && valores.imposto.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal diferencaImposto = calcularProporcaoDiferencaParaAbater(valorParcela, valores.imposto, diferenca);
                        valores.imposto = valores.imposto.add(diferencaImposto);
                        diferencaAbatida = diferencaAbatida.add(diferencaImposto);
                    }
                    if (diferencaAbatida.compareTo(diferenca) != 0 && valores.taxa != null && valores.taxa.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal diferencaTaxa = calcularProporcaoDiferencaParaAbater(valorParcela, valores.taxa, diferenca);
                        valores.taxa = valores.taxa.add(diferencaTaxa);
                    }
                } catch (Exception e) {
                    logger.error("Não foi possível arredondar a parcela! ", e);
                }
            }
        }

        private static BigDecimal calcularProporcaoDiferencaParaAbater(BigDecimal valorParcela, BigDecimal valor, BigDecimal diferencaParcela) {
            return (valor.multiply(diferencaParcela)).divide(valorParcela, 8, RoundingMode.HALF_UP);
        }

        @Override
        public String toString() {
            return "ValoresParcela{" +
                "imposto=" + imposto +
                ", taxa=" + taxa +
                ", juros=" + juros +
                ", multa=" + multa +
                ", correcao=" + correcao +
                ", honorarios=" + honorarios +
                ", desconto=" + desconto +
                '}';
        }
    }

    private BigDecimal porcentagemDoValorTotal(BigDecimal aplicar, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            return (aplicar.multiply(CEM).divide(total, 6, RoundingMode.HALF_UP)).divide(CEM, 6, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public Integer getNumeroReparcelamento(Long idParcela) {
        String hql = "select coalesce(p.numeroReparcelamento,0)+1 from ProcessoParcelamento p where p.id = "
            + " (select parcela.valorDivida.calculo.id from ParcelaValorDivida parcela where parcela.id = :idParcela)";
        Query consulta = em.createQuery(hql).setParameter("idParcela", idParcela);
        if (consulta.getResultList().isEmpty()) {
            return 0;
        } else {
            Integer retorno = (Integer) consulta.getResultList().get(0);
            return retorno;
        }
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public List<ProcessoParcelamento> listaFiltrando(String parte) {
        String hql = "select p from ProcessoParcelamento p where "
            + " (p.cadastro in (select c from CadastroImobiliario c where c.inscricaoCadastral like :parte))"
            + " or "
            + " (p.cadastro in (select c from CadastroEconomico c where c.inscricaoCadastral like :parte))"
            + " or "
            + " (p.cadastro in (select c from CadastroRural c where to_char(c.codigo) like :parte))"
            + " or "
            + " (p.numero || '/' || p.exercicio.ano like :parte)"
            + " or "
            + " (to_char(p.numero) like  :parte )";

        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<ResultadoParcela> recuperaParcelasOriginadasParcelamento(Long idProcessoParcelamento) {
        return new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, idProcessoParcelamento)
            .addOrdem(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Ordem.TipoOrdem.DESC)
            .executaConsulta().getResultados();
    }

    public List<ResultadoParcela> recuperaParcelasPagasParcelamento(ProcessoParcelamento processoParcelamento) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, processoParcelamento.getId());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Arrays.asList(SituacaoParcela.PAGO, SituacaoParcela.PAGO_SUBVENCAO));
        return consulta.executaConsulta().getResultados();
    }

    public List<ResultadoParcela> recuperaParcelasDiferenteEmAbertoDoParcelamento(ProcessoParcelamento processoParcelamento) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, processoParcelamento.getId());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.DIFERENTE, SituacaoParcela.EM_ABERTO);
        return consulta.executaConsulta().getResultados();
    }

    public List<Long> buscarIdsParcelasOriginaisParcelamento(Long idProcessoParcelamento) {
        List<Long> idsParcelas = em.createQuery("select i.parcelaValorDivida.id from ItemProcessoParcelamento i where i.processoParcelamento.id = :processo")
            .setParameter("processo", idProcessoParcelamento).getResultList();
        return idsParcelas;
    }

    public List<ResultadoParcela> buscarResultadoParcelaOriginalParcelamento(ProcessoParcelamento processo) {
        List<Long> ids = Lists.newArrayList();
        for (ItemProcessoParcelamento item : processo.getItensProcessoParcelamento()) {
            ids.add(item.getParcelaValorDivida().getId());
        }
        if (!ids.isEmpty()) {
            return new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, ids)
                .addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC)
                .addOrdem(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Ordem.TipoOrdem.ASC)
                .executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    public List<ResultadoParcela> buscarResultadoParcelaOriginalParcelamentoCancelado(CancelamentoParcelamento cancelamentoParcelamento) {
        List<Long> ids = Lists.newArrayList();
        for (ParcelasCancelamentoParcelamento item : cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ORIGINAIS)) {
            ids.add(item.getParcela().getId());
        }
        if (!ids.isEmpty()) {
            return new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, ids)
                .addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC)
                .addOrdem(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Ordem.TipoOrdem.ASC)
                .executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void depoisDePagar(Calculo calculo) {
        pagarParcelamento(recuperar(calculo.getId()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void pagarParcelamento(ProcessoParcelamento parcelamento) {
        if (!SituacaoParcelamento.PAGO.equals(parcelamento.getSituacaoParcelamento())) {
            try {
                List<ResultadoParcela> parcelasPagas = recuperaParcelasPagasParcelamento(parcelamento);
                if (parcelasPagas.size() == parcelamento.getNumeroParcelas()) {
                    parcelamento.setSituacaoParcelamento(SituacaoParcelamento.PAGO);
                    parcelamento = em.merge(parcelamento);
                    em.flush();
                    definirSituacaoPagoParcelasOriginais(parcelamento);
                    certidaoDividaAtivaFacade.pagaCertidoesDoPacelamento(parcelamento);
                }
                if (!parcelasPagas.isEmpty() && !parcelamento.getParamParcelamento().getSituacaoDebito().equals(SituacaoDebito.EXERCICIO)) {
                    comunicaInsercaoAlteracaoParcelamento(parcelamento);
                }
                salvarDataDePrescricaoParcelaValorDivida(parcelamento);
            } catch (Exception e) {
                logger.error("Erro ao pagar o parcelamento: {}", e);
            }
        }
    }

    public void salvarDataDePrescricaoParcelaValorDivida(ProcessoParcelamento parcelamento) {
        List<ResultadoParcela> parcelasOriginais = Lists.newArrayList();
        if (parcelamento.getCancelamentoParcelamento() != null) {
            parcelasOriginais = buscarResultadoParcelaOriginalParcelamentoCancelado(parcelamento.getCancelamentoParcelamento());
        } else {
            parcelasOriginais = buscarResultadoParcelaOriginalParcelamento(parcelamento);
        }
        for (ResultadoParcela resultadoParcela : parcelasOriginais) {
            dividaAtivaDAO.mergeDataPrescicaoParcelaValorDivida(resultadoParcela.getIdParcela(), resultadoParcela.getPrescricao());
        }
    }

    public void depoisDeEstornarPagamentoParcelamento(ProcessoParcelamento parcelamento) {
        if (parcelamento.getSituacaoParcelamento().equals(SituacaoParcelamento.PAGO)) {
            try {
                List<ResultadoParcela> parcelas = recuperaParcelasDiferenteEmAbertoDoParcelamento(parcelamento);
                if (parcelas.isEmpty()) {
                    parcelamento.setSituacaoParcelamento(SituacaoParcelamento.FINALIZADO);
                    em.merge(parcelamento);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BigDecimal calcularPercentualDesconto(BigDecimal percentual, BigDecimal aplicavelSobre) {
        BigDecimal valorAplicavel = BigDecimal.ZERO;
        if (percentual != null && percentual.compareTo(BigDecimal.ZERO) > 0) {
            valorAplicavel = percentual.multiply(aplicavelSobre);
            valorAplicavel = valorAplicavel.divide(CEM);
        }

        return valorAplicavel;
    }

    public void definirSituacaoPagoParcelasOriginais(ProcessoParcelamento parcelamento) {
        BigDecimal valorPago = consultaDebitoFacade.valorPagoPoCalculo(parcelamento);
        Map<ParcelaValorDivida, BigDecimal> valorPagoPorParcela = Maps.newHashMap();
        BigDecimal totalLancado = BigDecimal.ZERO;
        if (valorPago.compareTo(parcelamento.getTotalGeral()) == 0) {
            List<ResultadoParcela> originais = buscarResultadoParcelaOriginalParcelamento(parcelamento);
            for (ResultadoParcela original : originais) {
                ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, original.getIdParcela());
                totalLancado = totalLancado.add(original.getValorTotal());
                valorPagoPorParcela.put(parcela, original.getValorTotal());
            }
        } else {
            ConfiguracaoAcrescimos configuracaoAcrescimos = dividaFacade.configuracaoVigente(parcelamento.getParamParcelamento().getDividaParcelamento());
            BigDecimal valorParcela = BigDecimal.ZERO;
            for (ItemProcessoParcelamento item : parcelamento.getItensProcessoParcelamento()) {
                BigDecimal imposto = item.getImposto();
                BigDecimal taxa = item.getTaxa();
                BigDecimal juros = item.getJuros();
                BigDecimal multa = item.getMulta();
                BigDecimal correcao = item.getCorrecao();
                ParamParcelamentoTributo faixaDesconto = parcelamento.getFaixaDesconto();
                if (faixaDesconto != null) {
                    imposto = imposto.subtract(calcularPercentualDesconto(faixaDesconto.getPercDescontoImposto(), imposto));
                    taxa = taxa.subtract(calcularPercentualDesconto(faixaDesconto.getPercDescontoTaxa(), taxa));
                    juros = juros.subtract(calcularPercentualDesconto(faixaDesconto.getPercentualJuros(), juros));
                    multa = multa.subtract(calcularPercentualDesconto(faixaDesconto.getPercentualMulta(), multa));
                    correcao = correcao.subtract(calcularPercentualDesconto(faixaDesconto.getPercentualCorrecaoMonetaria(), correcao));
                    valorParcela = imposto.add(taxa.add(juros.add(multa.add(correcao))));
                    valorParcela = valorParcela.add(CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), parcelamento.getDataProcessoParcelamento(), valorParcela));
                } else {
                    valorParcela = imposto.add(taxa.add(juros.add(multa.add(correcao).add(item.getHonorarios()))));
                }
                BigDecimal proporcao = valorParcela.multiply(BigDecimal.valueOf(100)).divide(valorPago, 8, RoundingMode.HALF_UP);
                BigDecimal valorPagoParcela = proporcao.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP).multiply(valorPago);
                totalLancado = totalLancado.add(valorPagoParcela);
                valorPagoPorParcela.put(item.getParcelaValorDivida(), valorPagoParcela);
            }
        }
        if (totalLancado.compareTo(valorPago) != 0) {
            BigDecimal diferenca = valorPago.subtract(totalLancado);
            ParcelaValorDivida parcela = parcelamento.getItensProcessoParcelamento().get(parcelamento.getItensProcessoParcelamento().size() - 1).getParcelaValorDivida();
            BigDecimal valor = valorPagoPorParcela.get(parcela).add(diferenca);
            valorPagoPorParcela.put(parcela, valor);
        }
    }

    public void comunicaInsercaoAlteracaoParcelamento(ProcessoParcelamento parcelamento) {
        comunicaSofPlanFacade.comunicarParcelamento(parcelamento, false);

        if (isTemParcelaPaga(parcelamento) && isNaoTemParcelaEmAberto(parcelamento)) {
            comunicaSofPlanFacade.alterarSituacaoParcelamento(parcelamento);
        }
    }

    private boolean isTemParcelaPaga(ProcessoParcelamento parcelamento) {
        String sql = "select pvd.id from parcelavalordivida pvd " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "where vd.calculo_id = :idCalculo " +
            "  and spvd.situacaoparcela in (:situacoesPagas)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", parcelamento.getId());
        q.setParameter("situacoesPagas", SituacaoParcela.getsituacoesPago());
        return !q.getResultList().isEmpty();
    }

    private boolean isNaoTemParcelaEmAberto(ProcessoParcelamento parcelamento) {
        String sql = "select pvd.id from parcelavalordivida pvd " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "where vd.calculo_id = :idCalculo " +
            "  and spvd.situacaoparcela = :situacaoAberto";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", parcelamento.getId());
        q.setParameter("situacaoAberto", SituacaoParcela.EM_ABERTO.name());
        return q.getResultList().isEmpty();
    }

    public BigDecimal quantidadeParcelasDoParcelamentoPorSituacao(ProcessoParcelamento parcelamento, SituacaoParcela situacaoParcela) {
        String sql = " select count(pvd.id) from parcelavalordivida pvd " +
            " inner join valordivida vd on vd.id = pvd.valordivida_id " +
            " inner join calculo on calculo.id = vd.calculo_id " +
            " inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            " where calculo.id = :id " +
            " and spvd.situacaoparcela = :situacao ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("id", parcelamento.getId());
        q.setParameter("situacao", situacaoParcela.name());

        List<BigDecimal> qtdParcelas = q.getResultList();
        return (qtdParcelas != null && !qtdParcelas.isEmpty() && qtdParcelas.get(0) != null) ? qtdParcelas.get(0) : BigDecimal.ZERO;
    }


    public Date buscarDataUltimoPagamento(ProcessoParcelamento parcelamento) {
        Query q = em.createNativeQuery("select max(lb.datapagamento) " +
                " from parcelavalordivida pvd " +
                "inner join valordivida vd on vd.id = pvd.valordivida_id " +
                "inner join calculo on calculo.id = vd.calculo_id " +
                "inner join itemdam on itemdam.parcela_id = pvd.id " +
                "inner join dam on dam.id = itemdam.dam_id " +
                "inner join itemlotebaixa ilb on ilb.dam_id = dam.id " +
                "inner join lotebaixa lb on lb.id = ilb.lotebaixa_id " +
                "where calculo.id = :id " +
                "and lb.situacaolotebaixa in (:situacoes)")
            .setParameter("id", parcelamento.getId())
            .setParameter("situacoes", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(),
                SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        List resultList = q.getResultList();
        return !resultList.isEmpty() ? (Date) resultList.get(0) : null;
    }

    private Exercicio getExercicioCorrente() {
        Calendar c = GregorianCalendar.getInstance();
        return exercicioFacade.getExercicioPorAno(c.get(Calendar.YEAR));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void comunicaSofPlanAlteracaoParcelamento(ProcessoParcelamento parcelamento) {
        try {
            comunicaSofPlanFacade.alterarSituacaoParcelamento(parcelamento);
        } catch (Exception e) {
            logger.error("Falha ao comunicar com a procuradoria: ", e);
        }
    }

    public void pagarParcelamento(CertidaoDividaAtiva certidao) {
        List<ProcessoParcelamento> parcelamentos = recuperaParcelamentoDaCda(certidao);
        if (parcelamentos.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhum parcelamento encontrado para essa certidão");
        }
        for (ProcessoParcelamento parcelamento : parcelamentos) {
            pagarParcelamento(parcelamento);
        }
    }

    public List<ProcessoParcelamento> recuperaParcelamentoDaCda(CertidaoDividaAtiva cda) {
        Query query = em.createNativeQuery("select distinct calculo.*,  pp.* from parcelavalordivida pvd\n" +
            "inner join valordivida vd on vd.id = pvd.valordivida_id\n" +
            "inner join iteminscricaodividaativa iida on iida.id = vd.calculo_id\n" +
            "inner join itemcertidaodividaativa icda on icda.iteminscricaodividaativa_id = iida.id\n" +
            "inner join certidaodividaativa cda on cda.id = icda.certidao_id\n" +
            "inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = pvd.id\n" +
            "inner join processoparcelamento pp on pp.id = ipp.processoparcelamento_id\n" +
            "inner join calculo on calculo.id = pp.id " +
            "where cda.id = :id ", ProcessoParcelamento.class).setParameter("id", cda.getId());
        return query.getResultList();
    }

    private List<Long> buscarParcelamentoOriginaisDasParcelas(List<Long> idParcelas) {
        List<Long> idParcelamentos = Lists.newArrayList();
        if (idParcelas != null && !idParcelas.isEmpty()) {
            String sql = "select distinct pp.id as idparcelamento, ipp.parcelavalordivida_id as idoriginal " +
                " from processoparcelamento pp " +
                "inner join itemprocessoparcelamento ipp on ipp.processoparcelamento_id = pp.id " +
                "inner join valordivida vd on vd.calculo_id = pp.id " +
                "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                "where pvd.id in(:parcelas)";
            Query q = em.createNativeQuery(sql);
            q.setParameter("parcelas", idParcelas);

            List<Long> originadas = Lists.newArrayList();
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                List<Object[]> lista = resultList;
                for (Object[] obj : lista) {
                    idParcelamentos.add(((BigDecimal) obj[0]).longValue());
                    originadas.add(((BigDecimal) obj[1]).longValue());
                }
            }
            if (!originadas.isEmpty()) {
                idParcelamentos.addAll(buscarParcelamentoOriginaisDasParcelas(originadas));
            }
        }
        return idParcelamentos;
    }

    private List<Long> buscarParcelamentoDasParcelas(List<Long> idParcelas) {
        if (!idParcelas.isEmpty()) {
            final List<Long> idParcelamentos = Lists.newArrayList();
            final List<Long> originadas = Lists.newArrayList();
            String sql = "select distinct pp.id as idparcelamento, pvd.id as idoriginada " +
                " from processoparcelamento pp " +
                "inner join itemprocessoparcelamento ipp on ipp.processoparcelamento_id = pp.id " +
                "inner join valordivida vd on vd.calculo_id = pp.id " +
                "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                "where ipp.parcelavalordivida_id in(:ids)";

            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dividaAtivaDAO.getJdbcTemplate());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("ids", idParcelas);
            namedParameterJdbcTemplate.query(sql, parameters, new ResultSetExtractor<Object>() {
                @Override
                public Object extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                    while (resultSet.next()) {
                        if (!idParcelamentos.contains(resultSet.getBigDecimal(1).longValue())) {
                            idParcelamentos.add(resultSet.getBigDecimal(1).longValue());
                        }
                        if (!originadas.contains(resultSet.getBigDecimal(2).longValue())) {
                            originadas.add(resultSet.getBigDecimal(2).longValue());
                        }
                    }
                    return null;
                }
            });
            if (!originadas.isEmpty()) {
                for (Long id : buscarParcelamentoDasParcelas(originadas)) {
                    if (!idParcelamentos.contains(id)) {
                        idParcelamentos.add(id);
                    }
                }
            }
            return idParcelamentos;
        }
        return Lists.newArrayList();
    }

    public List<ProcessoParcelamento> buscarTodosParcelamentosComParcelaPaga(List<Long> idParcelas) {
        List<Long> idParcelamentos = buscarParcelamentoDasParcelas(idParcelas);
        if (!idParcelamentos.isEmpty()) {
            Query query = em.createNativeQuery("select calculo.*, pp.* from processoparcelamento pp " +
                "inner join calculo on calculo.id = pp.id " +
                "inner join exercicio ex on ex.id = pp.exercicio_id " +
                "where pp.id in (:parcelamentos) " +
                "  and exists (select pvd.id from parcelavalordivida pvd " +
                "               inner join valordivida vd on vd.id = pvd.valordivida_id " +
                "               inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
                "               where vd.calculo_id = calculo.id " +
                "                 and spvd.situacaoparcela in (:situacoes)) " +
                "order by ex.ano, pp.dataprocessoparcelamento, pp.numero, pp.numeroreparcelamento", ProcessoParcelamento.class);
            query.setParameter("parcelamentos", idParcelamentos);
            List<String> situacoes = Lists.newArrayList(SituacaoParcela.PAGO.name(),
                SituacaoParcela.BAIXADO.name(),
                SituacaoParcela.PAGO_PARCELAMENTO.name(),
                SituacaoParcela.PAGO_REFIS.name(),
                SituacaoParcela.PAGO_SUBVENCAO.name(),
                SituacaoParcela.PAGO_BLOQUEIO_JUDICIAL.name(),
                SituacaoParcela.BAIXADO_OUTRA_OPCAO.name());
            query.setParameter("situacoes", situacoes);
            return query.getResultList();
        }
        return Lists.newArrayList();
    }

    private List<ResultadoParcela> montarListaDeResultadoParcelaDasParcelasOriginadasDoParcelamento(ProcessoParcelamento processoParcelamento) {
        ConsultaParcela consultaParcela = new ConsultaParcela(DataUtil.adicionaHoras(processoParcelamento.getDataProcessoParcelamento(), 23));
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, processoParcelamento.getId());
        consultaParcela.executaConsulta();
        return consultaParcela.getResultados();
    }

    public String buscarSituacaoDaParcelaDoParcelamento(Long idParcela) {
        ParcelaValorDivida parcelaDoParcelamento = em.find(ParcelaValorDivida.class, idParcela);
        if (SituacaoParcela.PAGO_PARCELAMENTO.equals(parcelaDoParcelamento.getSituacaoAtual().getSituacaoParcela())) {
            ParcelaValorDivida parcelaDoValorResidual = consultaDebitoFacade.buscarParcelaDoValorResidualDaParcela(parcelaDoParcelamento);
            if (parcelaDoValorResidual != null) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcelaDoValorResidual.getId());
                ResultadoParcela resultadoParcelaDoValorResidual = consultaParcela.executaConsulta().getResultados().get(0);
                if (SituacaoParcela.EM_ABERTO.equals(resultadoParcelaDoValorResidual.getSituacaoEnumValue())) {
                    return SituacaoParcela.SUBSTITUIDA_POR_CANCELAMENTO_PARCELAMENTO.name();
                } else {
                    return resultadoParcelaDoValorResidual.getSituacaoEnumValue().name();
                }
            }
        }
        return parcelaDoParcelamento.getSituacaoAtual().getSituacaoParcela().name();
    }

    public List<ResultadoParcela> recuperarParcelasOriginadasSemAcrescimos(ProcessoParcelamento parcelamento) {
        List<ResultadoParcela> parcelasOriginadasDoParcelamento = montarListaDeResultadoParcelaDasParcelasOriginadasDoParcelamento(parcelamento);
        if (!parcelasOriginadasDoParcelamento.isEmpty()) {
            for (ResultadoParcela resultadoParcela : parcelasOriginadasDoParcelamento) {
                if (resultadoParcela.getValorDesconto().compareTo(BigDecimal.ZERO) <= 0) {
                    corrigirValorTotalDaParcelaComDesconto(resultadoParcela, parcelamento);
                }
            }
            corrigirArredondamentoTotalParcela(parcelasOriginadasDoParcelamento, parcelamento.getTotalGeral());
            for (ResultadoParcela resultadoParcela : parcelasOriginadasDoParcelamento) {
                if (resultadoParcela.getValorDesconto().compareTo(BigDecimal.ZERO) <= 0 && temValor(parcelamento.getTotalGeral())) {
                    aplicarDescontoResultadoParcela(resultadoParcela, parcelamento.getTotalGeral(), parcelamento.getValorDesconto());
                }
            }
            corrigirArredondamentoDesconto(parcelasOriginadasDoParcelamento, parcelamento.getValorDesconto());
            for (ResultadoParcela resultadoParcela : parcelasOriginadasDoParcelamento) {
                resultadoParcela.setSituacao(buscarSituacaoDaParcelaDoParcelamento(resultadoParcela.getIdParcela()));
            }
        }
        return parcelasOriginadasDoParcelamento;
    }

    private void corrigirArredondamentoTotalParcela(List<ResultadoParcela> parcelas, BigDecimal totalGeral) {
        BigDecimal somaTotal = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            somaTotal = somaTotal.add(parcela.getTotal().compareTo(BigDecimal.ZERO) > 0 ? parcela.getTotal() : parcela.getValorTotalSemDesconto());
        }
        BigDecimal diferenca = somaTotal.subtract(totalGeral).setScale(2, RoundingMode.HALF_UP);
        ResultadoParcela ultimaParcela = parcelas.get(parcelas.size() - 1);
        if (ultimaParcela.getTotal().compareTo(BigDecimal.ZERO) > 0) {
            ultimaParcela.setTotal(ultimaParcela.getTotal().subtract(diferenca));
        }
    }

    private void corrigirArredondamentoDesconto(List<ResultadoParcela> parcelas, BigDecimal totalDesconto) {
        BigDecimal somaDesconto = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            somaDesconto = somaDesconto.add(parcela.getValorDesconto());
        }
        BigDecimal diferenca = somaDesconto.subtract(totalDesconto).setScale(2, RoundingMode.HALF_UP);
        parcelas.get(parcelas.size() - 1).setValorDesconto(parcelas.get(parcelas.size() - 1).getValorDesconto().subtract(diferenca));
    }

    private BigDecimal corrigirValorTotalDaParcelaComDesconto(ResultadoParcela parcela, ProcessoParcelamento parcelamento) {
        BigDecimal valorTotalComDesconto = parcelamento.getTotalGeral().subtract(parcelamento.getValorDesconto().compareTo(BigDecimal.ZERO) > 0 ? parcelamento.getValorDesconto() : BigDecimal.ZERO);
        BigDecimal proporcao = parcela.getValorTotalSemDesconto().multiply(parcelamento.getTotalGeral());
        if (temValor(valorTotalComDesconto)) {
            parcela.setTotal(proporcao.divide(valorTotalComDesconto, 8, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
        } else {
            parcela.setTotal(BigDecimal.ZERO);
        }
        return parcela.getTotal();
    }

    private void aplicarDescontoResultadoParcela(ResultadoParcela parcela, BigDecimal total, BigDecimal desconto) {
        BigDecimal porcentagem = parcela.getTotal().multiply(CEM);
        porcentagem = porcentagem.divide(total, 8, RoundingMode.HALF_UP);
        porcentagem = porcentagem.divide(CEM, 8, RoundingMode.HALF_UP);
        parcela.setValorDesconto(desconto.multiply(porcentagem).setScale(2, RoundingMode.HALF_UP));
    }

    public List<Long> recuperarIDDoParcelamentoDaParcelaOriginal(Long idParcelaOriginal) {
        String sql = " select ipp.processoparcelamento_id\n" +
            "   from itemprocessoparcelamento ipp\n" +
            "  where ipp.parcelavalordivida_id = :idParcela\n" +
            " union \n" +
            " select cp.id\n" +
            "    from cancelamentoparcelamento cp\n" +
            "where cp.parcelavalordivida_id = :idParcela";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcelaOriginal);
        List<Long> ids = Lists.newArrayList();
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) resultList) {
                ids.add(new Long(id.longValue()));
            }
        }
        return ids;
    }

    public boolean parcelasOriginaisDoParcelamentoEstaoEntreOsExercicios(Long idOriginada, Integer exercicioInicial, Integer exercicioFinal) {
        String sql = "select 1 from parcelavalordivida pvd " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join processoparcelamento pp on pp.id = vd.calculo_id " +
            "inner join itemprocessoparcelamento ipp on ipp.processoparcelamento_id = pp.id " +
            "inner join parcelavalordivida opvd on opvd.id = ipp.parcelavalordivida_id " +
            "inner join valordivida ovd on ovd.id = opvd.valordivida_id " +
            "inner join exercicio ex on ex.id = ovd.exercicio_id " +
            "where pvd.id = :idParcela and " +
            " ( (ex.ano >= :inicio and ex.ano <= :final ) or pp.numeroreparcelamento > 0)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idOriginada);
        q.setParameter("inicio", exercicioInicial);
        q.setParameter("final", exercicioFinal);
        return !q.getResultList().isEmpty();
    }

    public VOProcessoParcelamento recuperarProcessoParcelamento(Long idParcela) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select pp.id," +
            "pp.numero," +
            "ex.ano," +
            "pp.situacaoParcelamento " +
            "from ProcessoParcelamento pp");
        sb.append(" inner join Exercicio ex on ex.id = pp.exercicio_id ");
        sb.append(" inner join ValorDivida vd on vd.calculo_id = pp.id ");
        sb.append(" inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id ");
        sb.append(" where pvd.id = :idParcela");

        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idParcela", idParcela);

        List<Object[]> lista = q.getResultList();
        for (Object[] obj : lista) {
            VOProcessoParcelamento vo = new VOProcessoParcelamento();
            vo.setId(((BigDecimal) obj[0]).longValue());
            vo.setNumero(((BigDecimal) obj[1]).longValue());
            vo.setAno(((BigDecimal) obj[2]).longValue());
            vo.setSituacao((SituacaoParcelamento.valueOf((String) obj[3])));
            return vo;
        }
        return null;

    }

    public List<ResultadoParcela> buscarParcelasOriginaisDaArvoreDosParcelamentos(ProcessoParcelamento parcelamento) {
        LinkedList<ResultadoParcela> parcelasOriginais = Lists.newLinkedList();
        List<Long> idsParcelas = Lists.newArrayList();
        for (ItemProcessoParcelamento itemProcessoParcelamento : parcelamento.getItensProcessoParcelamento()) {
            idsParcelas.add(itemProcessoParcelamento.getParcelaValorDivida().getId());
        }
        parcelasOriginais.addAll(buscarArvoreParcelamentoPartindoDoCalculo(idsParcelas, parcelamento.getId()));

        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelasOriginais) {
            if (!parcelas.contains(parcela)) {
                parcelas.add(parcela);
            }
        }
        return parcelas;
    }

    public List<ResultadoParcela> buscarArvoreParcelamentoPartindoDoCalculo(Long idCalculo) {
        String arvoreParcelamentos = "select * from (with cteparcelamento(originada, original, calculo) as (\n" +
            "select coalesce(pvd.id,pvdoriginal.id) as originada, pvdoriginal.id as original, vdoriginal.calculo_id as calculo\n" +
            "from parcelavalordivida pvdoriginal\n" +
            "inner join valordivida vdoriginal on vdoriginal.id = pvdoriginal.valordivida_id\n" +
            "left join inscricaodividaparcela idp on idp.parcelavalordivida_id = pvdoriginal.id\n" +
            "left join iteminscricaodividaativa ida on ida.id = idp.iteminscricaodividaativa_id\n" +
            "left join valordivida vdoriginado on vdoriginado.calculo_id = ida.id\n" +
            "left join parcelavalordivida pvd on pvd.valordivida_id = vdoriginado.id\n" +
            "left join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id\n" +
            "where vdoriginal.calculo_id = :calculo \n" +
            "and spvd.situacaoparcela <> :situacaoCancelada\n" +
            "union all\n" +
            "select originada.id as originada, pvd.id as original, pp.id as calculo\n" +
            "from parcelavalordivida pvd \n" +
            "inner join cteparcelamento cte on cte.originada = pvd.id\n" +
            "inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = pvd.id\n" +
            "inner join processoparcelamento pp on pp.id = ipp.processoparcelamento_id\n" +
            "inner join valordivida vd on vd.calculo_id = pp.id\n" +
            "inner join parcelavalordivida originada on originada.valordivida_id = vd.id)\n" +
            "select pvd.id\n" +
            "from parcelavalordivida pvd\n" +
            "inner join cteparcelamento cte on cte.originada = pvd.id\n" +
            "order by pvd.id desc)";
        Query q = em.createNativeQuery(arvoreParcelamentos);
        q.setParameter("calculo", idCalculo);
        q.setParameter("situacaoCancelada", SituacaoParcela.CANCELAMENTO.name());
        List<Long> idsParcelasRecuperadas = Lists.newArrayList();
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            int count = 1;
            for (BigDecimal id : (List<BigDecimal>) resultList) {
                if (count <= 1000) {
                    idsParcelasRecuperadas.add(id.longValue());
                } else {
                    break;
                }
                count++;
            }
        }
        if (!idsParcelasRecuperadas.isEmpty()) {
            return buscarParcelasDaListaDeIds(idsParcelasRecuperadas);
        }
        return new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, idCalculo)
            .executaConsulta().getResultados();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<ResultadoParcela> buscarArvoreParcelamentoPartindoDoCalculo(List<Long> idsParcela, Long idParcelamentoAtual) {
        List<Long> idParcelamentos = buscarParcelamentoOriginaisDasParcelas(idsParcela);
        idParcelamentos.add(idParcelamentoAtual);

        List<Long> parcelamentos = Lists.newArrayList();
        for (Long idParcelamento : idParcelamentos) {
            if (!parcelamentos.contains(idParcelamento)) {
                parcelamentos.add(idParcelamento);
            }
        }

        List<ResultadoParcela> parcelas = Lists.newArrayList();
        if (!parcelamentos.isEmpty()) {
            parcelas.addAll(buscarParcelasOriginadasDosParcelamento(parcelamentos));
        }
        List<Long> idsParcelas = buscarParcelasOriginaisDaListaDeParcelamentos(parcelamentos);
        if (!idsParcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (idsParcelas.contains(parcela.getIdParcela())) {
                    idsParcelas.remove(parcela.getIdParcela());
                }
            }
            parcelas.addAll(buscarParcelasDaListaDeIds(idsParcelas));
        }

        return parcelas;
    }

    public List<ResultadoParcela> buscarParcelasDaListaDeIds(List<Long> idsParcelas) {
        return new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcelas)
            .executaConsulta().getResultados();
    }

    private List<ResultadoParcela> buscarParcelasOriginadasDosParcelamento(List<Long> idsParcelamentos) {
        return new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsParcelamentos)
            .executaConsulta().getResultados();
    }

    private List<Long> buscarParcelasOriginaisDaListaDeParcelamentos(List<Long> idsParcelamentos) {
        String sql = "select distinct item.parcelavalordivida_id from itemprocessoparcelamento item " +
            " where item.processoparcelamento_id in (:idsParcelamentos)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idsParcelamentos", idsParcelamentos);
        List<Long> idsParcelas = Lists.newArrayList();
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) resultList) {
                idsParcelas.add(id.longValue());
            }
        }
        return idsParcelas;
    }

    private List<ParcelasCancelamentoParcelamento> converterResultadoParcelaEmParcelasCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento, List<ResultadoParcela> parcelas, ParcelasCancelamentoParcelamento.TipoParcelaCancelamento tipoParcela) {
        List<ParcelasCancelamentoParcelamento> retorno = Lists.newArrayList();
        BigDecimal indice = null;
        for (ResultadoParcela parcela : parcelas) {
            try {
                if (tipoParcela.isCalcularAtualizacao()) {
                    indice = getMoedaFacade().calcularIndiceAtualizaoDataAteHoje(parcela.getPagamento() != null ? parcela.getPagamento() : parcela.getVencimento());
                }
            } catch (Exception ex) {
                indice = null;
            }
            retorno.add(new ParcelasCancelamentoParcelamento(cancelamentoParcelamento, parcela, tipoParcela, indice));
        }
        return retorno;
    }

    private List<ParcelasCancelamentoParcelamento> converterResultadoParcelaEmParcelasCancelamentoParcelamentoOriginaisAtualizadas(CancelamentoParcelamento cancelamentoParcelamento, List<ResultadoParcela> parcelas) {
        List<ParcelasCancelamentoParcelamento> retorno = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelas) {
            ResultadoParcela novaParcela = new ResultadoParcela(parcela);
            novaParcela.setSituacao(SituacaoParcela.EM_ABERTO.name());
            novaParcela = new CalculadorAcrescimos(new ConsultaParcela().getServiceConsultaDebitos())
                .preencherValoresAcrescimosImpostoTaxaResultadoParcela(novaParcela, new Date(), null, null, null, null, null);
            retorno.add(new ParcelasCancelamentoParcelamento(cancelamentoParcelamento, novaParcela, ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ORIGINAIS_ATUALIZADAS, null));
        }
        return retorno;
    }

    private List<ResultadoParcela> separarParcelasOriginais(List<ResultadoParcela> parcelasOriginais) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelasOriginais) {
            if (!Calculo.TipoCalculo.PARCELAMENTO.equals(Calculo.TipoCalculo.fromDto(parcela.getTipoCalculoEnumValue()))) {
                parcelas.add(parcela);
            }
        }
        return parcelas;
    }

    private List<ResultadoParcela> separarParcelasOriginadasPagas(List<ResultadoParcela> parcelasOriginais) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelasOriginais) {
            if (Calculo.TipoCalculo.PARCELAMENTO.equals(Calculo.TipoCalculo.fromDto(parcela.getTipoCalculoEnumValue())) &&
                parcela.isPago()) {
                parcelas.add(parcela);
            }
        }
        return parcelas;
    }

    private void ordenarParcelasDoCancelamento(List<ParcelasCancelamentoParcelamento> parcelas) {
        if (parcelas != null) {
            Collections.sort(parcelas, new Comparator<ParcelasCancelamentoParcelamento>() {
                @Override
                public int compare(ParcelasCancelamentoParcelamento o1, ParcelasCancelamentoParcelamento o2) {
                    int ordem = o2.getTipoParcelaCancelamento().getOrdem().compareTo(o1.getTipoParcelaCancelamento().getOrdem());
                    if (ordem == 0) {
                        ordem = o1.getReferencia().compareTo(o2.getReferencia());
                    }
                    return ordem == 0 ? o1.getParcela().getId().compareTo(o2.getParcela().getId()) : ordem;
                }
            });
        }
    }

    public List<ParcelasCancelamentoParcelamento> getOriginais(CancelamentoParcelamento cancelamentoParcelamento) {
        return cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ORIGINAIS);
    }

    public List<ParcelasCancelamentoParcelamento> getOriginaisAtualizadas(CancelamentoParcelamento cancelamentoParcelamento) {
        return cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ORIGINAIS_ATUALIZADAS);
    }

    public List<ParcelasCancelamentoParcelamento> getOriginadasPagas(CancelamentoParcelamento cancelamentoParcelamento) {
        return cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_PAGAS);
    }

    public List<ParcelasCancelamentoParcelamento> getOriginadasPagasAtualizadas(CancelamentoParcelamento cancelamentoParcelamento) {
        return cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_PAGAS_ATUALIZADAS);
    }

    public List<ParcelasCancelamentoParcelamento> getParcelasAbatidas(CancelamentoParcelamento cancelamentoParcelamento) {
        return cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ABATIDAS);
    }

    public List<ParcelasCancelamentoParcelamento> atualizarValorNovaParcelaAberta(CancelamentoParcelamento cancelamentoParcelamento) {
        List<ParcelasCancelamentoParcelamento> parcelas = cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO);
        List<ParcelasCancelamentoParcelamento> abatidas = getParcelasAbatidas(cancelamentoParcelamento);

        ordenarParcelasDoCancelamento(cancelamentoParcelamento.getParcelas());

        for (ParcelasCancelamentoParcelamento parcela : parcelas) {
            for (ParcelasCancelamentoParcelamento abatida : abatidas) {
                if (abatida.getParcela() != null && parcela.getParcela() != null &&
                    abatida.getParcela().getId().equals(parcela.getParcela().getId())) {
                    ConsultaParcela consultaParcela = new ConsultaParcela(DataUtil.adicionaHoras(cancelamentoParcelamento.getDataCancelamento(), 23));

                    consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, cancelamentoParcelamento.getId());
                    consultaParcela.executaConsulta();

                    List<ResultadoParcela> resultadoConsulta = consultaParcela.getResultados();

                    if (!resultadoConsulta.isEmpty()) {
                        ResultadoParcela resultadoParcela = resultadoConsulta.get(0);
                        if (SituacaoParcela.EM_ABERTO.equals(resultadoParcela.getSituacaoDescricaoEnum())) {
                            parcela.setValorImposto(resultadoParcela.getValorImposto());
                            parcela.setValorTaxa(resultadoParcela.getValorTaxa());
                            parcela.setValorJuros(resultadoParcela.getValorJuros());
                            parcela.setValorMulta(resultadoParcela.getValorMulta());
                            parcela.setValorCorrecao(resultadoParcela.getValorCorrecao());
                            parcela.setValorHonorarios(resultadoParcela.getValorHonorarios());
                        }
                    }
                }
            }
        }

        return parcelas;
    }

    public void inicializarCancelametoDoParcelamento(CancelamentoParcelamento cancelamentoParcelamento, UsuarioSistema usuarioSistema, Exercicio exercicio) {
        cancelamentoParcelamento.setParcelas(Lists.newArrayList());
        cancelamentoParcelamento.setUsuario(usuarioSistema);
        cancelamentoParcelamento.setDataCancelamento(new Date());
        cancelamentoParcelamento.setExercicio(exercicio);

        List<ResultadoParcela> parcelasOriginaisDaArvoreDosParcelamentos = buscarParcelasOriginaisDaArvoreDosParcelamentos(cancelamentoParcelamento.getProcessoParcelamento());

        cancelamentoParcelamento.getParcelas().addAll(
            converterResultadoParcelaEmParcelasCancelamentoParcelamento(cancelamentoParcelamento,
                separarParcelasOriginais(parcelasOriginaisDaArvoreDosParcelamentos),
                ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ORIGINAIS));

        cancelamentoParcelamento.getParcelas().addAll(
            converterResultadoParcelaEmParcelasCancelamentoParcelamento(cancelamentoParcelamento,
                separarParcelasOriginadasPagas(parcelasOriginaisDaArvoreDosParcelamentos),
                ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_PAGAS));

        cancelamentoParcelamento.getParcelas().addAll(
            converterResultadoParcelaEmParcelasCancelamentoParcelamentoOriginaisAtualizadas(cancelamentoParcelamento,
                separarParcelasOriginais(parcelasOriginaisDaArvoreDosParcelamentos)));

        cancelamentoParcelamento.getParcelas().addAll(
            atualizarValorComIndiceUfm(cancelamentoParcelamento.getParcelasPorTipo(
                ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_PAGAS)));

        ordenarParcelasDoCancelamento(cancelamentoParcelamento.getParcelas());

        cancelamentoParcelamento.getParcelas().addAll(
            calcularParcelasAbatidasComValorTotalPagoAtualizado(cancelamentoParcelamento, getOriginaisAtualizadas(cancelamentoParcelamento)));

        cancelamentoParcelamento.getParcelas().addAll(
            calcularParcelasEmAbertoComValorTotalPagoAtualizado(getOriginaisAtualizadas(cancelamentoParcelamento),
                getParcelasAbatidas(cancelamentoParcelamento), cancelamentoParcelamento));
    }

    public BigDecimal getValorTotalPago(CancelamentoParcelamento cancelamentoParcelamento) {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagasAtualizadas(cancelamentoParcelamento)) {
            total = total.add(parcela.getValorPago());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public List<ParcelamentoCancelar> buscarProcessosParcelamentoParaCancelamentoAutomatico(int inicio, int max) {
        String sql = "select distinct " +
            "  pp.id, " +
            "  (select listagg(pvd.sequenciaparcela, ', ') " +
            "  within group ( " +
            "    order by to_number(pvd.sequenciaparcela)) " +
            "   from parcelavalordivida pvd " +
            "     inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "   where pvd.valordivida_id = vd.id " +
            "         and spvd.situacaoparcela = :situacaoParcela " +
            "         and (trunc(pvd.vencimento) + coalesce(param.diasvencidoscancelamento, 0)) < to_date(:hoje, 'dd/MM/yyyy')) as parcelasvencidas, " +
            "  coalesce(param.tipoinadimplimento, 'SUCESSIVO') as tipoinadimplimento, " +
            "  coalesce(param.parcelasinadimplencia,0) as parcelasinadimplencia " +
            " from processoparcelamento pp " +
            " inner join valordivida vd on vd.calculo_id = pp.id " +
            " inner join paramparcelamento param on param.id = pp.paramparcelamento_id " +
            " where pp.situacaoparcelamento in (:situacaoParcelamento) " +
            " and param.verificacancelamentoautomatico = :verificaCancelamento " +
            "  and exists (select item.id from itemprocessoparcelamento item where item.processoparcelamento_id = pp.id) " +
            "  and (exists (select pvd.id from parcelavalordivida pvd " +
            "               inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "               where pvd.valordivida_id = vd.id " +
            "                 and spvd.situacaoparcela = :situacaoParcela " +
            "                 and pvd.sequenciaparcela = :numeroPrimeiroParcela " +
            "                 and (trunc(pvd.vencimento) + coalesce(param.diasvencidoscancelamento, 0)) < to_date(:hoje, 'dd/MM/yyyy')) " +
            "  or (select count(pvd.id) from parcelavalordivida pvd " +
            "               inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "               where pvd.valordivida_id = vd.id " +
            "                 and spvd.situacaoparcela = :situacaoParcela " +
            "                 and (trunc(pvd.vencimento) + coalesce(param.diasvencidoscancelamento, 0)) < to_date(:hoje, 'dd/MM/yyyy')) >= param.parcelasinadimplencia) " +
            " order by pp.id desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacaoParcelamento", Lists.newArrayList(SituacaoParcelamento.FINALIZADO.name(), SituacaoParcelamento.REATIVADO.name()));
        q.setParameter("situacaoParcela", SituacaoParcela.EM_ABERTO.name());
        q.setParameter("numeroPrimeiroParcela", "1");
        q.setParameter("hoje", DataUtil.getDataFormatada(new Date()));
        q.setParameter("verificaCancelamento", ParamParcelamento.TipoVerificacaoCancelamentoAutomatico.SIM.name());
        q.setFirstResult(inicio);
        q.setMaxResults(max);
        List<Object[]> objs = q.getResultList();
        List<ParcelamentoCancelar> retorno = Lists.newArrayList();
        for (Object[] obj : objs) {
            retorno.add(new ParcelamentoCancelar(
                obj[0] != null ? ((BigDecimal) obj[0]).longValue() : 0L,
                obj[1] != null ? (String) obj[1] : "",
                obj[2] != null ? (String) obj[2] : "",
                obj[3] != null ? ((BigDecimal) obj[3]).intValue() : 0)
            );
        }
        logger.debug("Encontrou " + retorno.size() + " parcelamentos para cancelar");
        return retorno;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ProcessoParcelamento cancelarProcessoParcelamento(ProcessoParcelamento parcelamento) throws Exception {
        try {
            parcelamento.setSituacaoParcelamento(SituacaoParcelamento.CANCELADO);
            parcelamento = salvarParcelamento(parcelamento);
            cancelarProcessamento(parcelamento, parcelamento.getCancelamentoParcelamento());
        } catch (Exception e) {
            throw e;
        }
        return parcelamento;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void corrigirReferenciaDasParcelas(CancelamentoParcelamento cancelamentoParcelamento) {
        String referencia = "";
        Collections.sort(cancelamentoParcelamento.getParcelas());
        for (ParcelasCancelamentoParcelamento abatida : cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO)) {
            if ("".equals(referencia)) {
                referencia = abatida.getReferencia();
                ParcelaValorDivida parcela = consultaDebitoFacade.recuperaParcela(abatida.getParcela().getId());
                cancelamentoParcelamento.setReferencia(referencia);
                dividaAtivaDAO.mergeReferenciaSituacaoParcelaValorDivida(parcela.getSituacaoAtual().getId(), referencia);
            }
        }
        ParcelaValorDivida parcelaCancelamento = cancelamentoParcelamentoFacade.buscarParcelaDoCancelamento(cancelamentoParcelamento);
        if (parcelaCancelamento != null) {
            parcelaCancelamento.getSituacaoAtual().setReferencia(referencia);
            dividaAtivaDAO.mergeReferenciaSituacaoParcelaValorDivida(parcelaCancelamento.getSituacaoAtual().getId(), referencia);
        }
    }

    private List<Long> buscarParcelamentosSemParcelas() {
        String sql = "select id from (select pp.id from processoparcelamento pp " +
            "where not exists (select parc.id from parcelaparcelamento parc where parc.processoparcelamento_id = pp.id) " +
            "and pp.situacaoparcelamento <> 'CANCELADO' " +
            "order by pp.id desc) where rownum <= 5000";
        Query q = em.createNativeQuery(sql);
        List<BigDecimal> ids = q.getResultList();
        List<Long> parcelamentos = Lists.newArrayList();
        for (BigDecimal id : ids) {
            parcelamentos.add(id.longValue());
        }
        return parcelamentos;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void cancelarDescontosParcelamentos() {
        List<Long> parcelamentos = buscarParcelamentosSemParcelas();
        int qtde = parcelamentos.size();
        log.debug("Parcelamentos para carregar: " + qtde);
        int i = 0;
        for (Long idParcelamento : parcelamentos) {
            carregarParcelasOriginadas(idParcelamento);
            i++;
            log.debug("Carregou o parcelamento " + idParcelamento + ": " + i + "/" + qtde);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ProcessoParcelamento carregarParcelasOriginadas(Long idParcelamento) {
        ProcessoParcelamento processoParcelamento = recuperarSimples(idParcelamento);
        try {
            List<ResultadoParcela> originadas = recuperarParcelasOriginadasSemAcrescimos(processoParcelamento);
            processoParcelamento.getParcelasProcessoParcelamento().clear();

            BigDecimal totalAplicadoDesconto = getTotalAplicadoDesconto(processoParcelamento);

            for (ResultadoParcela originada : originadas) {
                ParcelaProcessoParcelamento parcela = new ParcelaProcessoParcelamento();
                parcela.setProcessoParcelamento(processoParcelamento);
                parcela.setParcelaValorDivida(new ParcelaValorDivida(originada.getIdParcela()));

                parcela.setImposto(originada.getValorImposto());
                parcela.setTaxa(originada.getValorTaxa());
                parcela.setJuros(originada.getValorJuros());
                parcela.setMulta(originada.getValorMulta());
                parcela.setCorrecao(originada.getValorCorrecao());
                parcela.setHonorarios(originada.getValorHonorarios());

                BigDecimal valorParcelaSemDesconto = BigDecimal.ZERO;
                if (temValor(processoParcelamento.getValorTotalDesconto()) && temValor(processoParcelamento.getValorTotalDesconto())) {
                    valorParcelaSemDesconto = totalAplicadoDesconto.multiply(originada.getValorDesconto()).divide(processoParcelamento.getValorTotalDesconto(), 8, RoundingMode.HALF_UP);
                }

                if (temValor(processoParcelamento.getValorDescontoImposto())) {
                    parcela.setDescontoImposto(calcularValorDesconto(valorParcelaSemDesconto, processoParcelamento.getTotalGeral(), processoParcelamento.getValorDescontoImposto()));
                } else {
                    parcela.setDescontoImposto(BigDecimal.ZERO);
                }
                if (temValor(processoParcelamento.getValorDescontoTaxa())) {
                    parcela.setDescontoTaxa(calcularValorDesconto(valorParcelaSemDesconto, processoParcelamento.getTotalGeral(), processoParcelamento.getValorDescontoTaxa()));
                } else {
                    parcela.setDescontoTaxa(BigDecimal.ZERO);
                }
                if (temValor(processoParcelamento.getValorDescontoJuros())) {
                    parcela.setDescontoJuros(calcularValorDesconto(valorParcelaSemDesconto, processoParcelamento.getTotalGeral(), processoParcelamento.getValorDescontoJuros()));
                } else {
                    parcela.setDescontoJuros(BigDecimal.ZERO);
                }
                if (temValor(processoParcelamento.getValorDescontoMulta())) {
                    parcela.setDescontoMulta(calcularValorDesconto(valorParcelaSemDesconto, processoParcelamento.getTotalGeral(), processoParcelamento.getValorDescontoMulta()));
                } else {
                    parcela.setDescontoMulta(BigDecimal.ZERO);
                }
                if (temValor(processoParcelamento.getValorDescontoCorrecao())) {
                    parcela.setDescontoCorrecao(calcularValorDesconto(valorParcelaSemDesconto, processoParcelamento.getTotalGeral(), processoParcelamento.getValorDescontoCorrecao()));
                } else {
                    parcela.setDescontoCorrecao(BigDecimal.ZERO);
                }
                if (temValor(processoParcelamento.getValorDescontoHonorarios())) {
                    parcela.setDescontoHonorarios(calcularValorDesconto(valorParcelaSemDesconto, processoParcelamento.getTotalGeral(), processoParcelamento.getValorDescontoHonorarios()));
                } else {
                    parcela.setDescontoHonorarios(BigDecimal.ZERO);
                }

                BigDecimal diferenca = parcela.getDesconto().subtract(originada.getValorDesconto());
                if (temValor(diferenca)) {
                    if (temValor(parcela.getDescontoImposto())) {
                        parcela.setDescontoImposto(parcela.getDescontoImposto().subtract(diferenca));
                    } else if (temValor(parcela.getDescontoTaxa())) {
                        parcela.setDescontoTaxa(parcela.getDescontoTaxa().subtract(diferenca));
                    } else if (temValor(parcela.getDescontoJuros())) {
                        parcela.setDescontoJuros(parcela.getDescontoJuros().subtract(diferenca));
                    } else if (temValor(parcela.getDescontoMulta())) {
                        parcela.setDescontoMulta(parcela.getDescontoMulta().subtract(diferenca));
                    } else if (temValor(parcela.getDescontoCorrecao())) {
                        parcela.setDescontoCorrecao(parcela.getDescontoCorrecao().subtract(diferenca));
                    } else if (temValor(parcela.getDescontoHonorarios())) {
                        parcela.setDescontoHonorarios(parcela.getDescontoHonorarios().subtract(diferenca));
                    }
                }

                processoParcelamento.getParcelasProcessoParcelamento().add(parcela);
            }
            return em.merge(processoParcelamento);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return processoParcelamento;
    }

    private BigDecimal calcularValorDesconto(BigDecimal valorParcelaSemDesconto, BigDecimal valorParcelamentoSemDesconto, BigDecimal descontoColuna) {
        try {
            return valorParcelaSemDesconto.multiply(descontoColuna).divide(valorParcelamentoSemDesconto, 2, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            return descontoColuna;
        }
    }

    private BigDecimal getTotalAplicadoDesconto(ProcessoParcelamento processoParcelamento) {
        BigDecimal totalAplicadoDesconto = BigDecimal.ZERO;
        if (temValor(processoParcelamento.getValorDescontoImposto())) {
            totalAplicadoDesconto = totalAplicadoDesconto.add(processoParcelamento.getValorTotalImposto());
        }
        if (temValor(processoParcelamento.getValorDescontoTaxa())) {
            totalAplicadoDesconto = totalAplicadoDesconto.add(processoParcelamento.getValorTotalTaxa());
        }
        if (temValor(processoParcelamento.getValorDescontoJuros())) {
            totalAplicadoDesconto = totalAplicadoDesconto.add(processoParcelamento.getValorTotalJuros());
        }
        if (temValor(processoParcelamento.getValorDescontoMulta())) {
            totalAplicadoDesconto = totalAplicadoDesconto.add(processoParcelamento.getValorTotalMulta());
        }
        if (temValor(processoParcelamento.getValorDescontoCorrecao())) {
            totalAplicadoDesconto = totalAplicadoDesconto.add(processoParcelamento.getValorTotalCorrecao());
        }
        if (temValor(processoParcelamento.getValorDescontoHonorarios())) {
            totalAplicadoDesconto = totalAplicadoDesconto.add(processoParcelamento.getValorTotalHonorarios());
        }
        return totalAplicadoDesconto;
    }

    private boolean temValor(BigDecimal valor) {
        return BigDecimal.ZERO.compareTo(valor) != 0;
    }

    public List<ProcessoParcelamento> buscarParcelamentosDaParcela(Long idParcela) {
        List<Long> idParcelamentos = buscarParcelamentoDasParcelas(Lists.newArrayList(idParcela));
        List<ProcessoParcelamento> parcelamentos = Lists.newArrayList();
        if (!idParcelamentos.isEmpty()) {
            String sql = "select distinct pp.id, pp.dataprocessoparcelamento, coalesce(pp.situacaoparcelamento,'ABERTO'), " +
                "coalesce (pp.numero,0), coalesce (pp.valortotaldesconto,0)" +
                "from processoparcelamento pp " +
                "where pp.id in (:ids) " +
                " order by pp.dataprocessoparcelamento desc";
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dividaAtivaDAO.getJdbcTemplate());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("ids", idParcelamentos);
            parcelamentos.addAll(namedParameterJdbcTemplate.query(sql, parameters, new ResultSetExtractor<List<ProcessoParcelamento>>() {
                @Override
                public List<ProcessoParcelamento> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                    List<ProcessoParcelamento> processos = Lists.newArrayList();
                    while (resultSet.next()) {
                        ProcessoParcelamento pp = new ProcessoParcelamento();
                        pp.setId(resultSet.getLong(1));
                        pp.setDataProcessoParcelamento(resultSet.getDate(2));
                        pp.setSituacaoParcelamento(SituacaoParcelamento.valueOf(resultSet.getString(3)));
                        pp.setNumero(resultSet.getLong(4));
                        pp.setValorTotalDesconto(resultSet.getBigDecimal(5));
                        processos.add(pp);
                    }
                    return processos;
                }
            }));
        }
        return parcelamentos;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void buscarParcelamentosCanceladosErrados() {
        String sql = "select pp.id as idparcelamento, pvd.id as idparcela, pp.numero, pp.numeroreparcelamento, ex.ano, " +
            "pp.dataprocessoparcelamento, cp.datacancelamento, pvd.sequenciaparcela, pvd.vencimento, spvd.situacaoparcela, spvd.datalancamento,\n" +
            "(select max(lb.datapagamento) from lotebaixa lb \n" +
            "  inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id\n" +
            "  inner join itemdam idam on idam.dam_id = ilb.dam_id\n" +
            "  where idam.parcela_id = pvd.id\n" +
            "    and lb.situacaolotebaixa in ('BAIXADO','BAIXADO_INCONSITENTE')) as datapagamento\n" +
            "from processoparcelamento pp\n" +
            "inner join valordivida vd on vd.calculo_id = pp.id\n" +
            "inner join exercicio ex on ex.id = pp.exercicio_id\n" +
            "inner join cancelamentoparcelamento cp on cp.processoparcelamento_id = pp.id\n" +
            "inner join calculo cal on cal.id = cp.id\n" +
            "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id\n" +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id\n" +
            "where pp.situacaoparcelamento = :situacao\n" +
            "  and cp.motivo = :motivo\n" +
            "  and ex.ano > 2016\n" +
            "order by pp.id, pvd.sequenciaparcela";

        Query q = em.createNativeQuery(sql);
        q.setParameter("motivo", MENSAGEM_CANCELAMENTO);
        q.setParameter("situacao", SituacaoParcelamento.CANCELADO.name());
        List<Object[]> parcelas = q.getResultList();
        List<VOParcelaParcelamentoCancelamento> vos = Lists.newArrayList();
        for (Object[] parcela : parcelas) {
            VOParcelaParcelamentoCancelamento vo = new VOParcelaParcelamentoCancelamento();
            vo.setIdParcelamento(((Number) parcela[0]).longValue());
            vo.setIdParcela(((Number) parcela[1]).longValue());
            vo.setNumeroParcelamento(((Number) parcela[2]).longValue());
            vo.setNumeroReparcelamento(((Number) parcela[3]).intValue());
            vo.setExercicio(((Number) parcela[4]).intValue());
            vo.setDataParcelamento((Date) parcela[5]);
            vo.setDataCancelamento(parcela[6] != null ? (Date) parcela[6] : null);
            vo.setSequenciaParcela(parcela[7] != null ? (String) parcela[7] : "");
            vo.setVencimentoParcela(parcela[8] != null ? (Date) parcela[8] : null);
            vo.setSituacaoParcela(parcela[9] != null ? (String) parcela[9] : null);
            vo.setDataSituacao(parcela[10] != null ? (Date) parcela[10] : null);
            vo.setDataPagamento(parcela[11] != null ? (Date) parcela[11] : null);
            vos.add(vo);
        }

        Map<Long, List<VOParcelaParcelamentoCancelamento>> mapa = Maps.newHashMap();
        for (VOParcelaParcelamentoCancelamento parcela : vos) {
            if (!mapa.containsKey(parcela.getIdParcelamento())) {
                mapa.put(parcela.getIdParcelamento(), Lists.newArrayList(parcela));
            } else {
                mapa.get(parcela.getIdParcelamento()).add(parcela);
            }
        }

        for (Long idParcelamento : mapa.keySet()) {
            int vencidas = 0;
            boolean primeiroParcelaVencida = false;
            for (VOParcelaParcelamentoCancelamento parcela : mapa.get(idParcelamento)) {
                Date dataBaixa = parcela.getDataPagamento();
                if (parcela.isPago()) {
                    dataBaixa = parcela.getDataSituacao();
                }

                if ((!parcela.isPago() && parcela.isVencido(parcela.getDataCancelamento()))
                    || (parcela.isPago() && dataBaixa != null && dataBaixa.after(parcela.getDataCancelamento()))) {
                    if (mapa.get(idParcelamento).size() > 1) {
                        vencidas++;
                    }
                }
                if ("1".equals(parcela.getSequenciaParcela()) && !parcela.isPago() && parcela.isVencido(parcela.getDataCancelamento())) {
                    primeiroParcelaVencida = true;
                }
            }

            if ((vencidas > 0 && vencidas < 3) && !primeiroParcelaVencida) {
                logger.error("Parcelamento: " + mapa.get(idParcelamento).get(0).getNumeroCompostoComAno() + " com " + vencidas + " parcelas vencidas.");
            }
        }
    }

    public boolean isDiaNaoUtil(Date data) {
        return DataUtil.ehDiaNaoUtil(data, feriadoFacade);
    }

    public List<CadastroImobiliario> buscarCadastrosImobiliarios(String parte, boolean hasAtivos) {
        return cadastroImobiliarioDAO.lista(parte.trim(), hasAtivos);
    }

    public List<CadastroRural> buscarCadastrosRurais(String parte, boolean hasAtivos) {
        return cadastroRuralDAO.lista(parte.trim(), hasAtivos);
    }

    public ConfiguracaoAcrescimos buscarConfiguracaoDeAcrescimosVigente(Divida dividaParcelamento) {
        return dividaFacade.configuracaoVigente(dividaParcelamento);
    }

    public Divida recuperarDivida(Long idDivida) {
        return dividaFacade.recuperar(idDivida);
    }

    public DocumentoOficial gerarTermoParcelamento(ProcessoParcelamento parcelamento, ParcelamentoJudicial parcelamentoJudicial, DocumentoOficial documentoOficial) throws AtributosNulosException, UFMException {
        return documentoOficialFacade.geraDocumentoTermoParcelamento(parcelamento,parcelamentoJudicial, documentoOficial);
    }

    public List<ComunicacaoSoftPlan> recuperarComunicacoesParcelamento(ProcessoParcelamento parcelamento) {
        return certidaoDividaAtivaFacade.recuperarComunicacoesParcelamento(parcelamento);
    }

    public Exercicio buscarExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public UsuarioSistema buscarUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public String obterIpUsuario() {
        return Util.obterIpUsuario();
    }

    public static class ParcelamentoCancelar {
        Long id;
        String parcelasVencidas;
        ParamParcelamento.TipoInadimplimento tipoInadimplimento;
        Integer parcelasInadiplencia;

        public ParcelamentoCancelar(Long id, String parcelasVencidas, String tipoInadimplimento, Integer parcelasInadiplencia) {
            this.id = id;
            this.parcelasVencidas = parcelasVencidas;
            this.tipoInadimplimento = ParamParcelamento.TipoInadimplimento.valueOf(tipoInadimplimento);
            this.parcelasInadiplencia = parcelasInadiplencia;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getParcelasVencidas() {
            return parcelasVencidas;
        }

        public List<Integer> getSequenciaParcelas() {
            List<String> items = Arrays.asList(parcelasVencidas.split("\\s*,\\s*"));
            List<Integer> retorno = Lists.newArrayList();
            for (String s : items) retorno.add(Integer.valueOf(s));
            return retorno;
        }

        public void setParcelasVencidas(String parcelasVencidas) {
            this.parcelasVencidas = parcelasVencidas;
        }

        public boolean isInadimplenciaSucessiva() {
            return ParamParcelamento.TipoInadimplimento.SUCESSIVO.equals(this.tipoInadimplimento);
        }

        public boolean isInadimplenciaIntercadala() {
            return ParamParcelamento.TipoInadimplimento.INTERCALADO.equals(this.tipoInadimplimento);
        }

        public Integer getParcelasInadiplencia() {
            return parcelasInadiplencia;
        }

        public void setParcelasInadiplencia(Integer parcelasInadiplencia) {
            this.parcelasInadiplencia = parcelasInadiplencia;
        }
    }

    public byte[] gerarBytesDemonstrativo(ProcessoParcelamento processoParcelamento,
                                          List<ResultadoParcela> parcelasOriginais,
                                          List<ResultadoParcela> parcelasOriginadas,
                                          ProcessoParcelamentoControlador.Valores valores) throws JRException, IOException {
        if (processoParcelamento.getFaixaDesconto() == null && processoParcelamento.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
            criarFaixarDescontoParaDemonstrar(processoParcelamento);
        }
        if (processoParcelamento.getParamParcelamento() != null) {
            processoParcelamento.setParamParcelamento(parametroParcelamentoFacade
                .recuperar(processoParcelamento.getParamParcelamento().getId()));
        }
        DemonstrativoParcelamento demonstrativo = new DemonstrativoParcelamento();
        demonstrativo.setParcelamento(processoParcelamento);
        demonstrativo.setOriginais(parcelasOriginais);
        demonstrativo.setDividasOriginais(processoParcelamento.getParamParcelamento().getDividas());
        demonstrativo.setOriginadas(parcelasOriginadas);
        atribuirValorHonorariosAtualizadosParaDemonstrativo(demonstrativo);
        demonstrativo.setContribuinte(getPessoaDoProcessoParcelamento(processoParcelamento));
        demonstrativo.setEnderecoFiador(buscarEnderecosFiador(processoParcelamento).size() > 0 ?
            buscarEnderecosFiador(processoParcelamento).get(0) : null);
        demonstrativo.setEnderecoProcurador(buscarEnderecosProcurador(processoParcelamento).size() > 0 ?
            buscarEnderecosProcurador(processoParcelamento).get(0) : null);

        demonstrativo.setEnderecoContribuinte("");
        TipoCadastroTributario tipoCadastro = processoParcelamento.getParamParcelamento().getTipoCadastroTributario();
        if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastro)) {
            CadastroImobiliario cadastroImobiliario = cadastroImobiliarioFacade.recuperar(processoParcelamento.getCadastro().getId());
            demonstrativo.setEnderecoContribuinte(cadastroImobiliario.getLote().getEndereco());
        } else if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastro)) {
            CadastroEconomico ce = cadastroEconomicoFacade.recuperar(processoParcelamento.getCadastro().getId());
            EnderecoCorreio localizacao = ce.getLocalizacao().converterEnderecoCadastroEconomicoPrincipalParaEnderecoCorreio();
            demonstrativo.setEnderecoContribuinte(localizacao.toString());
        } else if (TipoCadastroTributario.RURAL.equals(tipoCadastro)) {
            demonstrativo.setEnderecoContribuinte(((CadastroRural)
                processoParcelamento.getCadastro()).getEnderecoFiscal());
        } else {
            EnderecoCorreio enderecoCorreio = getEnderecoFacade().retornaPrimeiroEnderecoCorreioValido(demonstrativo.getContribuinte().getId());
            if (enderecoCorreio != null) {
                demonstrativo.setEnderecoContribuinte(enderecoCorreio.getEnderecoCompleto());
            }
        }

        demonstrativo.setValores(valores);
        demonstrativo.setParamDesconto(processoParcelamento.getFaixaDesconto());
        demonstrativo.setMostraDesconto(true);
        return new TermoProcessoParcelamento().gerarBytesDemonstrativo(demonstrativo);
    }

    public List<EnderecoCorreio> buscarEnderecosProcurador(ProcessoParcelamento processoParcelamento) {
        return processoParcelamento.getProcurador() != null ? getEnderecoFacade().enderecoPorPessoa(processoParcelamento.getProcurador())
            : Lists.<EnderecoCorreio>newArrayList();
    }

    public List<EnderecoCorreio> buscarEnderecosFiador(ProcessoParcelamento processoParcelamento) {
        return processoParcelamento.getFiador() != null ? getEnderecoFacade().enderecoPorPessoa(processoParcelamento.getFiador())
            : Lists.<EnderecoCorreio>newArrayList();
    }

    private void atribuirValorHonorariosAtualizadosParaDemonstrativo(DemonstrativoParcelamento demonstrativo) {
        BigDecimal honorariosAtualizado = BigDecimal.ZERO;
        for (ResultadoParcela originada : demonstrativo.getOriginadas()) {
            honorariosAtualizado = honorariosAtualizado.add(originada.getValorHonorarios());
        }
        demonstrativo.getParcelamento().setValorTotalHonorariosAtualizado(honorariosAtualizado);
    }

    public void criarFaixarDescontoParaDemonstrar(ProcessoParcelamento processoParcelamento) {
        ParamParcelamentoTributo faixa = new ParamParcelamentoTributo();
        faixa.setPercentualJuros(atribuirPorcentagemDesconto(processoParcelamento.getValorTotalJuros(),
            processoParcelamento.getValorDescontoJuros()));
        faixa.setPercentualMulta(atribuirPorcentagemDesconto(processoParcelamento.getValorTotalMulta(),
            processoParcelamento.getValorDescontoMulta()));
        faixa.setPercentualCorrecaoMonetaria(atribuirPorcentagemDesconto(processoParcelamento.getValorTotalCorrecao(),
            processoParcelamento.getValorDescontoCorrecao()));
        processoParcelamento.setFaixaDesconto(faixa);
    }

    public BigDecimal atribuirPorcentagemDesconto(BigDecimal valor, BigDecimal desconto) {
        if (desconto.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal divide = desconto.multiply(CEM).divide(valor, 2, RoundingMode.HALF_UP);
            return divide.compareTo(BigDecimal.ZERO) > 0 ? divide : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    public List<ResultadoParcela> buscarParcelasOriginaisParcelamento(Map<Long, String> ajuizamentoPorValorDivida,
                                                                      ProcessoParcelamento processoParcelamento) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ItemProcessoParcelamento item : processoParcelamento.getItensProcessoParcelamento()) {
            ResultadoParcela resultado = new ResultadoParcela();
            resultado.setDivida(item.getParcelaValorDivida().getValorDivida().getDivida().getDescricao());
            resultado.setExercicio(item.getParcelaValorDivida().getValorDivida().getExercicio().getAno());
            resultado.setVencimento(item.getParcelaValorDivida().getVencimento());
            resultado.setValorImposto(item.getImposto());
            resultado.setValorTaxa(item.getTaxa());
            resultado.setValorCorrecao(item.getCorrecao());
            resultado.setValorJuros(item.getJuros());
            resultado.setValorMulta(item.getMulta());
            resultado.setValorHonorarios(item.getHonorarios());
            resultado.setSituacao(SituacaoParcela.EM_ABERTO.name());
            resultado.setCadastro(TipoCadastroTributario.IMOBILIARIO.name());
            resultado.setTipoCalculo(Calculo.TipoCalculo.PARCELAMENTO.name());
            resultado.setIdValorDivida(item.getParcelaValorDivida().getValorDivida().getId());
            resultado.setIdParcela(item.getParcelaValorDivida().getId());
            if (SituacaoDebito.AJUIZADA.equals(processoParcelamento.getParamParcelamento().getSituacaoDebito())) {
                resultado.setNumeroProcessoAjuizamento(getNumeroAjuizamento(ajuizamentoPorValorDivida, resultado));
                item.setNumeroProcessoAjuizamento(resultado.getNumeroProcessoAjuizamento());
            }
            parcelas.add(resultado);
        }
        return parcelas;
    }

    public String getNumeroAjuizamento(Map<Long, String> ajuizamentoPorValorDivida, ResultadoParcela parcela) {
        if (ajuizamentoPorValorDivida == null) {
            ajuizamentoPorValorDivida = Maps.newHashMap();
        }
        if (ajuizamentoPorValorDivida.containsKey(parcela.getIdValorDivida())) {
            return ajuizamentoPorValorDivida.get(parcela.getIdValorDivida());
        }

        String numero = getInscricaoDividaAtivaFacade().numeroProcessoJudicialCda(parcela.getIdParcela());
        ajuizamentoPorValorDivida.put(parcela.getIdValorDivida(), numero);
        return ajuizamentoPorValorDivida.get(parcela.getIdValorDivida());
    }

    public byte[] gerarBytesDemonstrativo(ProcessoParcelamento processoParcelamento) throws JRException, IOException {
        processoParcelamento = recuperar(processoParcelamento.getId());
        List<ResultadoParcela> parcelasOriginais = buscarParcelasOriginaisParcelamento(Maps.newHashMap(),
            processoParcelamento);
        List<ResultadoParcela> parcelasOriginadas = recuperarParcelasOriginadasSemAcrescimos(processoParcelamento);
        ConfiguracaoAcrescimos configuracaoAcrescimos =
            buscarConfiguracaoDeAcrescimosVigente(processoParcelamento.getParamParcelamento().getDividaParcelamento());
        ProcessoParcelamentoControlador.Valores valores = new ProcessoParcelamentoControlador.Valores();
        for (ItemProcessoParcelamento item : processoParcelamento.getItensProcessoParcelamento()) {
            valores.calcular(item, configuracaoAcrescimos);
        }
        for (ResultadoParcela parcela : parcelasOriginadas) {
            valores.calcular(parcela, configuracaoAcrescimos, processoParcelamento.getParamParcelamento());
        }
        return gerarBytesDemonstrativo(processoParcelamento, parcelasOriginais,
            parcelasOriginadas, null);
    }
}
