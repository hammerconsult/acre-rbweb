/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.ImprimeRelatoriosArrecadacao;
import br.com.webpublico.controlerelatorio.RelatorioMapaArrecadacaoConsolidadoControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.BloqueioMovimentoContabilException;
import br.com.webpublico.exception.ContaReceitaException;
import br.com.webpublico.exception.SemUnidadeException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.arrecadacao.ArquivoArrecadacaoExecutor;
import br.com.webpublico.negocios.tributario.arrecadacao.DepoisDePagarQueue;
import br.com.webpublico.negocios.tributario.arrecadacao.DepoisDePagarResquest;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroEconomicoDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoISSDao;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcPessoaDAO;
import br.com.webpublico.negocios.tributario.services.ServiceIntegracaoTributarioContabil;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoLoteBaixa;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.dtos.ItemPagamentoDTO;
import br.com.webpublico.tributario.consultadebitos.dtos.PagamentoDTO;
import br.com.webpublico.tributario.consultadebitos.enums.TipoTributoDTO;
import br.com.webpublico.tributario.dto.EventoRedeSimDTO;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
@Interceptors(TransactionInterceptor.class)
public class LoteBaixaFacade extends AbstractFacade<LoteBaixa> {
    public static final long NUMEROLIMITEDAMNFSE = 500000000;
    private static final Logger logger = LoggerFactory.getLogger(LoteBaixaFacade.class);
    public static final BigDecimal MENOS_UM_CENTAVO = new BigDecimal("-0.01");
    public static final BigDecimal UM_CENTAVO = new BigDecimal("0.01");
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private final DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    private ServiceIntegracaoTributarioContabil service;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ArrecadacaoFacade arrecadacaoFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private EnquadramentoTributoExercicioFacade enquadramentoTributoExercicioFacade;
    @EJB
    private SingletonGeradorCodigoLoteBaixa singletonGeradorCodigoLoteBaixa;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private BandeiraCartaoFacade bandeiraCartaoFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private CalculaISSFacade calculaISSFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private IntegracaoRedeSimFacade integracaoRedeSimFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private DepoisDePagarQueue depoisDePagarQueue;

    private JdbcCalculoISSDao issDAO;
    private JdbcDividaAtivaDAO dividaAtivaDAO;
    private JdbcCadastroEconomicoDAO cadastroEconomicoDAO;
    private JdbcPessoaDAO pessoaDAO;
    @EJB//auto injeção para chamar async dele mesmo
    private LoteBaixaFacade loteBaixaFacade;

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        issDAO = (JdbcCalculoISSDao) ap.getBean("calculoISSDao");
        dividaAtivaDAO = (JdbcDividaAtivaDAO) ap.getBean("dividaAtivaDAO");
        cadastroEconomicoDAO = (JdbcCadastroEconomicoDAO) ap.getBean("cadastroEconomicoDAO");
        pessoaDAO = (JdbcPessoaDAO) ap.getBean("consultaPessoaDAO");
    }

    public LoteBaixaFacade() {
        super(LoteBaixa.class);
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public DAM buscarDamPeloNumero(String codigo) {
        Long numero = Long.parseLong(codigo.substring(0, codigo.indexOf("/")));
        Integer ano = Integer.parseInt(codigo.substring(codigo.indexOf("/") + 1, codigo.length()));
        Query q;
        if (numero < NUMEROLIMITEDAMNFSE) {
            q = procuraDamsWebPublico(numero, ano);
        } else {
            q = procuraDamsNfse(numero);
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            DAM dam = (DAM) resultList.get(0);
            Hibernate.initialize(dam.getItens());
            return dam;
        } else {
            codigo = codigo.replace("/", "");
            q = procuraDamsWebPublico(Long.parseLong(codigo), Calendar.getInstance().get(Calendar.YEAR));
            resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                DAM dam = (DAM) resultList.get(0);
                Hibernate.initialize(dam.getItens());
                return dam;
            }
        }
        return null;
    }

    private Query procuraDamsNfse(Long numero) {
        String sql;
        Query q;
        sql = "select dam.* from calculonfse nota " +
            "inner join valordivida vd on vd.calculo_id = nota.id " +
            "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "inner join itemdam itd on itd.parcela_id = pvd.id " +
            "inner join dam on dam.id = itd.dam_id " +
            "where nota.identificacaodaguia = :numero order by dam.numero desc, dam.sequencia desc";
        q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("numero", numero);
        return q;
    }

    private Query procuraDamsWebPublico(Long numero, Integer ano) {
        String sql = "from DAM dam where dam.numero = :numero "
            + " and dam.exercicio.ano = :ano  "
            + " order by dam.sequencia desc";
        Query q = em.createQuery(sql);
        q.setParameter("numero", numero);
        q.setParameter("ano", ano);
        return q;
    }

    public DAM recuperaDamPorCodigoBarrasSemDigitoVerificador(String barras) {
        try {
            String codigo = CarneUtil.retiraNumeroDamCodigoBarras(barras);
            return buscarDamPeloNumero(codigo);
        } catch (Exception e) {
            logger.error("Erro ao recuperaDamPorCodigoBarrasSemDigitoVerificador: {}", e);
        }
        return null;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArrecadacaoFacade getArrecadacaoFacade() {
        return arrecadacaoFacade;
    }

    public BandeiraCartaoFacade getBandeiraCartaoFacade() {
        return bandeiraCartaoFacade;
    }

    public String gerarCodigoLote(Date dataPagamento) {
        try {
            String codigoLote = singletonGeradorCodigoLoteBaixa.getProximoCodigo(dataPagamento).toString();
            if (!codigoLote.startsWith(DataUtil.getAno(dataPagamento).toString())) {
                codigoLote = StringUtil.cortaOuCompletaDireita(DataUtil.getAno(dataPagamento) + "", 9, "0").concat(codigoLote);
            }
            return codigoLote;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        throw new ExcecaoNegocioGenerica("Não foi possível gerar o código do lote de arrecadação");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public LoteBaixa encerrarLoteManual(LoteBaixa loteBaixa) {
        try {
            if (loteBaixa.getId() == null) {
                loteBaixa.setCodigoLote(gerarCodigoLote(loteBaixa.getDataPagamento()));
            }
            defineSituacaoLote(loteBaixa);
            loteBaixa = em.merge(loteBaixa);
        } catch (Exception e) {
            logger.error("Erro ao encerrar o lote de baixa: {}", e);
        }
        return loteBaixa;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public LoteBaixa atualizarItens(LoteBaixa loteBaixa) {
        loteBaixa = recuperar(loteBaixa.getId());
        for (ItemLoteBaixa item : loteBaixa.getItemLoteBaixa()) {
            BigDecimal valorTotalOriginal = BigDecimal.ZERO;
            for (ItemLoteBaixaParcela itemParcela : item.getItemParcelas()) {
                if (item.getDam() != null) {
                    valorTotalOriginal = valorTotalOriginal.add(itemParcela.getItemDam().getValorTotal());
                }
            }
            if (valorTotalOriginal.compareTo(item.getValorPago()) == 0) {
                for (ItemLoteBaixaParcela itemParcela : item.getItemParcelas()) {
                    itemParcela.setValorPago(itemParcela.getItemDam().getValorTotal());
                }
            } else {
                for (ItemLoteBaixaParcela itemParcela : item.getItemParcelas()) {
                    BigDecimal proporcao = (itemParcela.getItemDam().getValorTotal().multiply(CEM))
                        .divide(item.getValorPago(), 6, RoundingMode.HALF_UP);
                    proporcao = proporcao.divide(CEM, 6, RoundingMode.HALF_UP);
                    proporcao = proporcao.multiply(item.getValorPago());
                    itemParcela.setValorPago(proporcao.setScale(2, RoundingMode.HALF_UP));
                }
            }
        }
        return em.merge(loteBaixa);
    }

    private void defineSituacaoLote(LoteBaixa loteBaixa) {
        if (loteBaixa.getInconsistencias() != null && !loteBaixa.getInconsistencias().isEmpty()) {
            loteBaixa.setSituacaoLoteBaixa(SituacaoLoteBaixa.BAIXADO_INCONSITENTE);
        } else {
            loteBaixa.setSituacaoLoteBaixa(SituacaoLoteBaixa.BAIXADO);
        }
    }

    @Override
    public LoteBaixa recuperar(Object id) {
        LoteBaixa lb = em.find(LoteBaixa.class, id);
        Hibernate.initialize(lb.getItemLoteBaixa());
        Hibernate.initialize(lb.getInconsistencias());
        Hibernate.initialize(lb.getArquivosLoteBaixa());
        for (ItemLoteBaixa itemLoteBaixa : lb.getItemLoteBaixa()) {
            Hibernate.initialize(itemLoteBaixa.getItemParcelas());
            if (itemLoteBaixa != null && itemLoteBaixa.getPagamentoCartao() != null) {
                Hibernate.initialize(itemLoteBaixa.getPagamentoCartao().getItem());
            }
        }
        return lb;
    }

    public LoteBaixa recuperarIntegracao(Object id) {
        LoteBaixa lb = em.find(LoteBaixa.class, id);
        Hibernate.initialize(lb.getItemLoteBaixa());
        Hibernate.initialize(lb.getIntegracoes());
        Hibernate.initialize(lb.getSubConta().getSubContaFonteRecs());
        return lb;
    }

    public LoteBaixa recuperarPadrao(Object id) {
        LoteBaixa lb = em.find(LoteBaixa.class, id);
        lb.getItemLoteBaixa().size();
        return lb;
    }

    public String retornaDamCodigoBarras(String codigoDeBarras) {
        return CarneUtil.retiraNumeroDamCodigoBarras(codigoDeBarras);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public LoteBaixa salvaRetornando(LoteBaixa entity) {
        return em.merge(entity);
    }

    public LoteBaixa salvarCriarItensParcelasCasoNaoExista(LoteBaixa loteBaixa) {
        loteBaixa = em.merge(loteBaixa);
        for (ItemLoteBaixa item : loteBaixa.getItemLoteBaixa()) {
            if (item.getItemParcelas().isEmpty() && item.getDam() != null) {
                for (ItemDAM itemDam : item.getDam().getItens()) {
                    ItemLoteBaixaParcela it = new ItemLoteBaixaParcela();
                    it.setItemDam(itemDam);
                    it.setValorPago(item.getValorPago());
                    it.setItemLoteBaixa(item);
                    item.getItemParcelas().add(it);
                }
            }
        }

        return loteBaixa;
    }

    @Override
    public void salvar(LoteBaixa entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(LoteBaixa entity) {
        super.salvarNovo(entity);
    }

    public List<LoteBaixaDoArquivo> listaArquivosNaoProcessado() {
        String hql = "select distinct arquivo from LoteBaixaDoArquivo arquivo where arquivo.loteBaixa is null order by arquivo.dataPagamento desc";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public List<LoteBaixaDoArquivo> listaArquivosProcessados(ConsultaLoteBaixa consulta, Long max) {

        StringBuilder hql = new StringBuilder("select distinct arquivo from LoteBaixaDoArquivo arquivo where arquivo.arquivoLoteBaixa.situacaoArquivo = 'PROCESSADO'");
        if (consulta.situacaoLoteBaixa != null) {
            hql.append(" and arquivo.loteBaixa.situacaoLoteBaixa = :situacao");
        }
        if (consulta.subConta != null) {
            hql.append(" and arquivo.loteBaixa.subConta = :subConta");
        }
        if (consulta.numeroArquivo != null) {
            hql.append(" and arquivo.numero = :numero");
        }
        if (consulta.banco != null) {
            hql.append(" and arquivo.loteBaixa.banco = :banco");
        }
        if (consulta.dataPagamentoInicial != null) {
            hql.append(" and arquivo.dataPagamento >= :pagamentoInicial");
        }
        if (consulta.dataPagamentoFinal != null) {
            hql.append(" and arquivo.dataPagamento <= :pagamentoFinal");
        }
        if (consulta.dataMovimentoInicial != null) {
            hql.append(" and arquivo.dataMovimentacao >= :movimentacaoInicial");
        }
        if (consulta.dataMovimentoFinal != null) {
            hql.append(" and arquivo.dataMovimentacao <= :movimentacaoFinal");
        }
        hql.append(" order by arquivo.dataMovimentacao desc");
        Query q = em.createQuery(hql.toString());
        if (consulta.situacaoLoteBaixa != null) {
            q.setParameter("situacao", consulta.situacaoLoteBaixa);
        }
        if (consulta.subConta != null) {
            q.setParameter("subConta", consulta.subConta);
        }
        if (consulta.numeroArquivo != null) {
            q.setParameter("numero", consulta.numeroArquivo.intValue());
        }
        if (consulta.banco != null) {
            q.setParameter("banco", consulta.banco);
        }
        if (consulta.dataPagamentoInicial != null) {
            q.setParameter("pagamentoInicial", consulta.dataPagamentoInicial);
        }
        if (consulta.dataPagamentoFinal != null) {
            q.setParameter("pagamentoFinal", consulta.dataPagamentoFinal);
        }
        if (consulta.dataMovimentoInicial != null) {
            q.setParameter("movimentacaoInicial", consulta.dataMovimentoInicial);
        }
        if (consulta.dataMovimentoFinal != null) {
            q.setParameter("movimentacaoFinal", consulta.dataMovimentoFinal);
        }
        if (max != 0) {
            q.setMaxResults(max.intValue());
        }
        return q.getResultList();
    }

    public List<ArquivoLoteBaixa> listaTodosArquivos() {
        String hql = "from ArquivoLoteBaixa arquivo";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public ValidacaoException gerarArquivos(List<FileUploadEvent> files) throws ValidacaoException {
        ValidacaoException validacao = new ValidacaoException();
        for (FileUploadEvent event : Lists.newArrayList(files)) {
            try {
                UploadedFile arquivoRecebido = event.getFile();
                Arquivo arq = new Arquivo();
                arq.setNome(arquivoRecebido.getFileName());
                arq.setMimeType(arquivoFacade.getMimeType(arquivoRecebido.getFileName()));
                arq.setDescricao(" ");
                arq.setTamanho(arquivoRecebido.getSize());
                ArquivoLoteBaixa arquivoBaixa = new ArquivoLoteBaixa();
                arquivoBaixa.setArquivo(arq);
                arquivoBaixa.setSituacaoArquivo(SituacaoArquivo.NAO_PROCESSADO);
                arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
                arquivoRecebido.getInputstream().close();
                byte[] buffer = arquivoRecebido.getContents();
                String arqString = new String(buffer);
                new ArquivoArrecadacaoExecutor(this, bancoFacade, configuracaoTributarioFacade)
                    .lerArquivoBancario(arqString, arquivoBaixa);
                if (TipoArquivoBancarioTributario.DAF607.equals(arquivoBaixa.getTipoArquivoBancarioTributario())) {
                    ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
                    if (configuracaoTributario.getDividaSimplesNacional() == null) {
                        validacao.adicionarMensagemDeOperacaoNaoPermitida("É necessário configurar a Dívida de Simples Nacional na Configuração do Tributário.");
                        validacao.lancarException();
                    }
                }
                arquivoBaixa.setHashMd5(ArquivoUtil.stringHexa(ArquivoUtil.gerarHashMD5(arqString)));
                if (!existeArquivoComEsseHash(arquivoBaixa.getHashMd5())) {
                    em.persist(arquivoBaixa);
                    validacao.adicionarMensagemInfo(SummaryMessages.OPERACAO_REALIZADA, "O arquivo " + arq.getNome() + " foi gravado com Sucesso!");
                } else {
                    validacao.adicionarMensagemDeOperacaoNaoPermitida("O arquivo " + arq.getNome() + " já foi gravado anteriormente, verifique os arquivos já inseridos.");
                }
            } catch (IOException e) {
                validacao.adicionarMensagemDeOperacaoNaoRealizada("Não foi possível ler o arquivo de retorno bancário por problemas de IO (Input Output) no servidor");
            } catch (ParseException e) {
                validacao.adicionarMensagemDeOperacaoNaoRealizada("Não foi possível ler o arquivo de retorno bancário, o arquivo contém informações que não podem ser convertidas para Data e Número, verifique se contém caracteres alfa numéricos (ABC) nas linhas referentes a data e valor no arquivo de retorno bancário ");
            } catch (ExcecaoNegocioGenerica | ValidacaoException e) {
                validacao.adicionarMensagemDeOperacaoNaoRealizada(e.getMessage());
            } catch (Exception e) {
                logger.error("erro ao gerar lote ", e);
                validacao.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_REALIZADA, "Algo inesperado aconteceu ao gerar o arquivo de baixa, verifique se o arquivo de retorno bancário está correto antes de continuar!");
            }
        }
        return validacao;
    }

    public void criarItemLoteBaixa(LoteBaixa lote, RegistroDAF607 registroDAF607, ArquivoDAF607 arquivoDAF607,
                                   ConfiguracaoTributario configuracaoTributario,
                                   String usuarioBanco, UsuarioSistema usuarioSistema) {
        BigDecimal valorPrincipal = registroDAF607.getValorPrincipal() != null ? registroDAF607.getValorPrincipal() : BigDecimal.ZERO;
        BigDecimal valorMulta = registroDAF607.getValorMulta() != null ? registroDAF607.getValorMulta() : BigDecimal.ZERO;
        BigDecimal valorJuros = registroDAF607.getValorJuros() != null ? registroDAF607.getValorJuros() : BigDecimal.ZERO;
        BigDecimal valorTotal = valorPrincipal.add(valorJuros).add(valorMulta);

        ItemLoteBaixa itemLoteBaixa = new ItemLoteBaixa();
        itemLoteBaixa.setValorPago(valorTotal);
        itemLoteBaixa.setLoteBaixa(lote);
        itemLoteBaixa.setDamInformado(registroDAF607.getCodigoIdentificadorGuia());
        if (registroDAF607.getDataArrecadacaoDocumento() != null) {
            itemLoteBaixa.setDataPagamento(registroDAF607.getDataArrecadacaoDocumento());
        }
        if (arquivoDAF607.getDataCreditoConta() != null) {
            itemLoteBaixa.setDataCredito(arquivoDAF607.getDataCreditoConta());
        }
        lote.getItemLoteBaixa().add(itemLoteBaixa);
        ValorDivida valorDivida = loteBaixaFacade.gerarCalculosDebitos(configuracaoTributario,
            registroDAF607, itemLoteBaixa, usuarioSistema, usuarioBanco);
        loteBaixaFacade.gerarDamDaf607(usuarioSistema, registroDAF607, itemLoteBaixa, valorDivida);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public CadastroEconomico criarCadastroEconomicoDAF607(RegistroDAF607 registroDAF607, String usuarioBanco) {
        CadastroEconomico cadastroEconomico;
        cadastroEconomico = new CadastroEconomico();
        PessoaJuridica pj = pessoaFacade.buscarPessoaJuridicaPorCNPJ(registroDAF607.getCnpjContribuinte(), false);
        if (pj == null) {
            EventoRedeSimDTO eventoRedeSimDTO = null;
            try {
                eventoRedeSimDTO = integracaoRedeSimFacade.buscarRedeSimPorCnpj(registroDAF607.getCnpjContribuinte(), usuarioBanco, false);
            } catch (Exception ex) {
                logger.error("Erro ao buscar cnpj na redesim: ", ex);
            }
            pj = new PessoaJuridica();
            if (eventoRedeSimDTO != null) {
                pj.setRazaoSocial(eventoRedeSimDTO.getPessoaDTO().getNome());
                pj.setNomeFantasia(eventoRedeSimDTO.getPessoaDTO().getNome());
                pj.setNomeReduzido(eventoRedeSimDTO.getPessoaDTO().getNome());
            }
            pj.setCnpj(registroDAF607.getCnpjContribuinte());
            pj.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
            pj = (PessoaJuridica) pessoaDAO.inserir(pj);
        }
        cadastroEconomico.setPessoa(pj);
        cadastroEconomicoFacade.gerarInscricaoCadastral(cadastroEconomico);
        SituacaoCadastroEconomico situacao = new SituacaoCadastroEconomico();
        situacao.setSituacaoCadastral(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacao.setDataAlteracao(new Date());
        situacao.setDataRegistro(situacao.getDataAlteracao());
        cadastroEconomico.getSituacaoCadastroEconomico().add(situacao);
        cadastroEconomico = cadastroEconomicoFacade.criarEnquadramentoFiscal(cadastroEconomico, RegimeTributario.SIMPLES_NACIONAL, TipoIssqn.SIMPLES_NACIONAL);
        cadastroEconomico = cadastroEconomicoDAO.inserir(cadastroEconomico);
        return cadastroEconomico;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public ValorDivida gerarCalculosDebitos(ConfiguracaoTributario configuracaoTributario,
                                            RegistroDAF607 registroDAF607, ItemLoteBaixa itemLoteBaixa,
                                            UsuarioSistema usuarioSistema, String usuarioBanco) {
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpj(registroDAF607.getCnpjContribuinte());
        if (cadastroEconomico == null) {
            registroDAF607.setAtualizaPessoa(true);
            cadastroEconomico = loteBaixaFacade.criarCadastroEconomicoDAF607(registroDAF607, usuarioBanco);
        } else {
            cadastroEconomico = cadastroEconomicoFacade.recuperar(cadastroEconomico.getId());
        }
        Exercicio exercicio = exercicioFacade.recuperarExercicioPeloAno(new Integer(registroDAF607
            .getCompetenciaConstanteGuia().substring(0, 4)));
        BigDecimal valorPrincipal = registroDAF607.getValorPrincipal() != null ?
            registroDAF607.getValorPrincipal() : BigDecimal.ZERO;

        Mes mes = Mes.getMesToInt(new Integer(registroDAF607.getCompetenciaConstanteGuia().substring(4, 6)));
        ProcessoCalculoISS processoCalculoISS = calculaISSFacade.criarCalculoParaDaf607(exercicio,
            cadastroEconomico, mes, valorPrincipal, itemLoteBaixa.getDataPagamento(), usuarioSistema);
        issDAO.salvar(processoCalculoISS);
        ValorDivida valorDivida = calculaISSFacade.criarValorDivida(processoCalculoISS.getCalculos().get(0),
            registroDAF607.getDataVencimentoDocumento(), configuracaoTributario.getTributoISS());
        dividaAtivaDAO.persisteValorDivida(valorDivida);
        for (ParcelaValorDivida parcelaValorDivida : valorDivida.getParcelaValorDividas()) {
            dividaAtivaDAO.persistirSituacaoParcelaValorDividaComMesmaReferencia(parcelaValorDivida.getId(),
                SituacaoParcela.PAGO, valorDivida.getEmissao());
        }
        return valorDivida;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public void gerarDamDaf607(UsuarioSistema usuarioSistema,
                               RegistroDAF607 registroDAF607,
                               ItemLoteBaixa itemLoteBaixa,
                               ValorDivida valorDivida) {
        if (valorDivida.getParcelaValorDividas() != null
            && !valorDivida.getParcelaValorDividas().isEmpty()) {
            ParcelaValorDivida parcelaValorDivida = valorDivida.getParcelaValorDividas().get(0);
            DAM dam = damFacade.criarDAMParaRegistroDAF607(usuarioSistema, parcelaValorDivida);
            itemLoteBaixa.setCodigoBarrasInformado(dam.getCodigoBarras());
            itemLoteBaixa.setDamInformado(retornaDamCodigoBarras(dam.getCodigoBarras()));
            if (registroDAF607.getDataArrecadacaoDocumento() != null) {
                itemLoteBaixa.setDataPagamento(registroDAF607.getDataArrecadacaoDocumento());
            }
            if (registroDAF607.getArquivoDAF607().getDataCreditoConta() != null) {
                itemLoteBaixa.setDataCredito(registroDAF607.getArquivoDAF607().getDataCreditoConta());
            }
            itemLoteBaixa.setDam(dam);
            for (ItemDAM itemDam : dam.getItens()) {
                ItemLoteBaixaParcela it = new ItemLoteBaixaParcela();
                it.setItemDam(itemDam);
                it.setItemLoteBaixa(itemLoteBaixa);
                it.setValorPago(itemLoteBaixa.getValorPago());
                itemLoteBaixa.setItemParcelas(Lists.<ItemLoteBaixaParcela>newArrayList());
                itemLoteBaixa.getItemParcelas().add(it);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ArquivoLoteBaixa salvaViaArquivo(ArquivoLoteBaixa arquivoLote) {
        arquivoLote.setSituacaoArquivo(SituacaoArquivo.PROCESSADO);
        return em.merge(arquivoLote);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ProcessaArquivo salvaViaArquivo(ProcessaArquivo processaArquivo) {
        processaArquivo.setCalculados(0);
        processaArquivo.setTotal(processaArquivo.getArquivos().size());
        List<ArquivoLoteBaixa> salvos = Lists.newArrayList();
        for (ArquivoLoteBaixa arquivo : processaArquivo.getArquivos()) {
            salvos.add(salvaViaArquivo(arquivo));
            processaArquivo.conta();
        }
        processaArquivo.setArquivos(salvos);
        return processaArquivo;
    }

    public void removerArquivo(ArquivoLoteBaixa arquivo) {
        arquivo = em.find(ArquivoLoteBaixa.class, arquivo.getId());
        em.remove(arquivo);
    }


    public LoteBaixa criarNovoLoteDepoisEstorno(LoteBaixa selecionado) {

        selecionado = recuperar(selecionado.getId());
        LoteBaixa novoLote = new LoteBaixa();
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            DAM dam = item.getDam();
            ItemLoteBaixa novoItem = new ItemLoteBaixa();
            novoItem.setValorPago(item.getValorPago());
            novoItem.setDam(dam);
            novoItem.setLoteBaixa(novoLote);
            novoItem.setDamInformado(item.getDamInformado());
            novoItem.setCodigoBarrasInformado(item.getCodigoBarrasInformado());
            novoItem.setDataCredito(item.getDataCredito());
            novoItem.setDataPagamento(item.getDataPagamento());
            if (dam != null) {
                for (ItemDAM itemDam : dam.getItens()) {
                    ItemLoteBaixaParcela itemLoteBaixaParcela = new ItemLoteBaixaParcela();

                    BigDecimal proporcao = (itemDam.getValorTotal().multiply(CEM))
                        .divide(novoItem.getValorPago(), 6, RoundingMode.HALF_UP);
                    proporcao = proporcao.divide(CEM, 6, RoundingMode.HALF_UP);
                    proporcao = proporcao.multiply(novoItem.getValorPago());

                    itemLoteBaixaParcela.setValorPago(proporcao);
                    itemLoteBaixaParcela.setItemDam(itemDam);
                    itemLoteBaixaParcela.setItemLoteBaixa(novoItem);
                    novoItem.getItemParcelas().add(itemLoteBaixaParcela);
                }
            }
            novoLote.getItemLoteBaixa().add(novoItem);
        }
        novoLote.setBanco(selecionado.getBanco());
        novoLote.setIntegraContasAcrecimos(true);
        novoLote.setFormaPagamento(selecionado.getFormaPagamento());
        novoLote.setSubConta(selecionado.getSubConta());
        novoLote.setDataFinanciamento(selecionado.getDataFinanciamento());
        novoLote.setDataPagamento(selecionado.getDataPagamento());
        novoLote.setTipoDePagamentoBaixa(selecionado.getTipoDePagamentoBaixa());
        novoLote.setSituacaoLoteBaixa(SituacaoLoteBaixa.EM_ABERTO);
        novoLote.setCodigoLote(gerarCodigoLote(novoLote.getDataPagamento()));
        novoLote.setQuantidadeParcelas(selecionado.getQuantidadeParcelas());
        novoLote.setValorTotal(selecionado.getValorTotal());
        novoLote = em.merge(novoLote);

        for (LoteBaixaDoArquivo loteBaixaDoArquivo : selecionado.getArquivosLoteBaixa()) {
            LoteBaixaDoArquivo novoLoteBaixaDoArquivo = new LoteBaixaDoArquivo();
            novoLoteBaixaDoArquivo.setArquivoLoteBaixa(loteBaixaDoArquivo.getArquivoLoteBaixa());
            novoLoteBaixaDoArquivo.setBanco(loteBaixaDoArquivo.getBanco());
            novoLoteBaixaDoArquivo.setCodigoBanco(loteBaixaDoArquivo.getCodigoBanco());
            novoLoteBaixaDoArquivo.setDataMovimentacao(loteBaixaDoArquivo.getDataMovimentacao());
            novoLoteBaixaDoArquivo.setDataPagamento(loteBaixaDoArquivo.getDataPagamento());
            novoLoteBaixaDoArquivo.setLoteBaixa(novoLote);
            novoLoteBaixaDoArquivo.setNumero(loteBaixaDoArquivo.getNumero());
            novoLoteBaixaDoArquivo.setValorTotalArquivo(loteBaixaDoArquivo.getValorTotalArquivo());
            em.merge(novoLoteBaixaDoArquivo);
        }
        em.flush();

        return novoLote;

    }

    public LoteBaixa estornarLoteAntigo(LoteBaixa selecionado) {
        selecionado = recuperar(selecionado.getId());
        selecionado.getItemLoteBaixa().size();
        selecionado.setSituacaoLoteBaixa(SituacaoLoteBaixa.ESTORNADO);
        selecionado = em.merge(selecionado);
        return selecionado;
    }

    public void geraNovoArquivo(ArquivoLoteBaixa arquivo) {
        Arquivo arq = em.find(Arquivo.class, arquivo.getArquivo().getId());
        ArquivoLoteBaixa arquivoBaixa = new ArquivoLoteBaixa();
        arquivoBaixa.setArquivo(arq);
        arquivoBaixa.setSituacaoArquivo(SituacaoArquivo.NAO_PROCESSADO);
        em.persist(arquivo);
    }

    public List<BancoContaConfTributario> recuperaContasConfiguracao() {
        Query q = em.createQuery("select a from BancoContaConfTributario a where a.ativo = true");
        return q.getResultList();
    }

    public List<BancoContaConfTributario> buscarContasConfiguracaoPorBanco(String numeroBanco) {
        Query q = em.createNativeQuery("SELECT * FROM bancocontaconftributario conf "
                + " INNER JOIN subconta sub ON sub.id = conf.subconta_id"
                + " INNER JOIN contabancariaentidade conta ON conta.id = sub.contabancariaentidade_id"
                + " INNER JOIN agencia ON agencia.id = conta.agencia_id"
                + " INNER JOIN banco ON banco.id = agencia.banco_id "
                + " WHERE banco.numeroBanco = :numeroBanco "
                + " and coalesce(conf.ativo,0) = :ativo ", BancoContaConfTributario.class)
            .setParameter("numeroBanco", numeroBanco).setParameter("ativo", Boolean.TRUE);
        return q.getResultList();
    }

    public BancoContaConfTributario recuperaBancoContaConfTributarioPorSubConta(SubConta subConta) {
        if (subConta != null) {
            Query q = em.createQuery("select a from BancoContaConfTributario a where a.subConta = :subConta");
            q.setParameter("subConta", subConta);
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                return (BancoContaConfTributario) resultList.get(0);
            }
        }
        return null;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public LoteBaixa consisteLote(LoteBaixa lote) {
        BigDecimal totalInformado = lote.getValorTotal();
        BigDecimal totalCalculado = BigDecimal.ZERO;
        for (ItemLoteBaixa item : lote.getItemLoteBaixa()) {
            totalCalculado = totalCalculado.add(item.getValorPago());
            if (item.getDam() == null) {
                if (item.getDamInformadoSoNumero() != null && item.getDamInformadoSoNumero() >= LoteBaixaFacade.NUMEROLIMITEDAMNFSE) {
                    lote.addInconsistencia(new InconsistenciaArrecadacao(lote, item, null, InconsistenciaArrecadacao.Inconsistencia.DAM_NAO_ENCONTRATO_DE_NFSE));
                    //DAM NÃO ENCONTRATO NFSE
                } else {
                    lote.addInconsistencia(new InconsistenciaArrecadacao(lote, item, null, InconsistenciaArrecadacao.Inconsistencia.DAM_NAO_ENCONTRATO));
                    //DAM NÃO ENCONTRATO
                }
            }
            if (item.getValorDiferenca().compareTo(BigDecimal.ZERO) != 0) {
                lote.addInconsistencia(new InconsistenciaArrecadacao(lote, item, null, InconsistenciaArrecadacao.Inconsistencia.DIFERENCA_ENTRE_VALOR_DEVIDO_E_VALOR_PAGO));
                //DIFERENÇA ENTRE VALOR DEVIDO E VALOR PAGO
            }

        }
        if (totalInformado.compareTo(totalCalculado) != 0) {
            lote.addInconsistencia(new InconsistenciaArrecadacao(lote, null, null, InconsistenciaArrecadacao.Inconsistencia.DIFERENCA_NO_TOTAL_DO_LOTE));
            //DIFERENÇA NO TOTAL DO LOTE
        }
        if (lote.getQuantidadeParcelas().compareTo(lote.getItemLoteBaixa().size()) != 0) {
            lote.addInconsistencia(new InconsistenciaArrecadacao(lote, null, null, InconsistenciaArrecadacao.Inconsistencia.DIFERENCA_QUANTIDADE_DAMS));
            //DIFERENÇA QUANTIDADE DE DAMS
        }
        return lote;
    }

    private boolean existeArquivoComEsseHash(String hashMd5) {
        String hql = "select a.id from ArquivoLoteBaixa a " +
            " left join a.lotes lotes " +
            " left join lotes.loteBaixa lb " +
            " where to_char(a.hashMd5) = :hash " +
            " and (lb.situacaoLoteBaixa <> :situacao or a.situacaoArquivo = :situacaoArquivo)";
        return !em.createQuery(hql).setParameter("hash", hashMd5)
            .setParameter("situacao", SituacaoLoteBaixa.ESTORNADO)
            .setParameter("situacaoArquivo", SituacaoArquivo.NAO_PROCESSADO)
            .getResultList().isEmpty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ProcessaArquivo regularizarCadastrosDoDaf(ProcessaArquivo processaArquivo) {
        try {
            for (ArquivoLoteBaixa arquivoLoteBaixa : processaArquivo.getArquivos()) {
                for (LoteBaixaDoArquivo loteBaixaDoArquivo : arquivoLoteBaixa.getLotes()) {
                    if (!TipoArquivoBancarioTributario.DAF607.equals(arquivoLoteBaixa.getTipoArquivoBancarioTributario())) {
                        continue;
                    }
                    depoisDePagarQueue.enqueueProcess(buildDepoisDePagar(loteBaixaDoArquivo.getLoteBaixa()));
                    getArrecadacaoFacade().gerarIntegracaoSync(loteBaixaDoArquivo.getLoteBaixa(), TipoIntegracao.ARRECADACAO);
                }
            }
            Set<String> cadastros = Sets.newHashSet();
            for (ArquivoLoteBaixa arquivoLoteBaixa : processaArquivo.getMapArquivoBancarioTributario().keySet()) {
                if (!TipoArquivoBancarioTributario.DAF607.equals(arquivoLoteBaixa.getTipoArquivoBancarioTributario())) {
                    continue;
                }
                for (LoteBaixaDoArquivo loteBaixaDoArquivo : processaArquivo.getMapArquivoBancarioTributario().get(arquivoLoteBaixa).keySet()) {
                    ArquivoDAF607 arquivoDAF607 = (ArquivoDAF607) loteBaixaDoArquivo.getArquivoBancarioTributario();
                    for (RegistroDAF607 registroDAF607 : arquivoDAF607.getRegistrosDAF607()) {
                        if (registroDAF607.getAtualizaPessoa()) {
                            if (!cadastros.contains(registroDAF607.getCnpjContribuinte())) {
                                cadastroEconomicoFacade.atualizarDadosPessoaDaf607(registroDAF607.getCnpjContribuinte(),
                                    processaArquivo.getUsuarioBancoDados(), processaArquivo.getUsuarioSistema());
                                cadastros.add(registroDAF607.getCnpjContribuinte());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao regularizar cadastros do daf. Erro: {}", e);
            logger.debug("Detalhes do erro ao regularizar cadastros do daf.", e);
        }
        return processaArquivo;
    }


    public ArquivoLoteBaixa recuperarArquivo(ArquivoLoteBaixa arquivoLoteBaixa) {
        arquivoLoteBaixa = em.find(ArquivoLoteBaixa.class, arquivoLoteBaixa.getId());
        arquivoLoteBaixa.getLotes().size();
        return arquivoLoteBaixa;
    }

    public List<ItemLoteBaixa> itensLoteBaixaPorDam(DAM dam) {
        String hql = "select i from ItemLoteBaixa i where i.dam = :dam order by i.loteBaixa.codigoLote";
        Query q = em.createQuery(hql);
        q.setParameter("dam", dam);
        List<ItemLoteBaixa> itens = q.getResultList();
        for (ItemLoteBaixa item : itens) {
            Hibernate.initialize(item.getLoteBaixa().getArquivosLoteBaixa());
        }
        return itens;
    }

    public List<LoteBaixa> completaLotePorNumeroSituacaoBaixado(String parte) {
        try {
            String sql = "select l.* from lotebaixa l " +
                " where l.situacaolotebaixa in ('BAIXADO','BAIXADO_INCONSITENTE') " +
                " and l.codigoLote like :parte " +
                " order by l.datapagamento desc";
            Query consulta = em.createNativeQuery(sql, LoteBaixa.class);
            consulta.setParameter("parte", "%" + parte.trim() + "%");
            consulta.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<LoteBaixa> buscarLotePorNumeroSituacaoData(String parte, Date dataInicial, Date dataFinal, boolean somenteNaoIntegrados, SituacaoLoteBaixa... situacoes) {
        try {
            String sql = "select lb.* from LoteBaixa lb " +
                "where lb.situacaoLoteBaixa in (:situacoes) " +
                "and trunc(lb.dataPagamento) between to_date(:dataInicial, 'dd/MM/yyyy') " +
                "and to_date(:dataFinal, 'dd/MM/yyyy') ";
            if (somenteNaoIntegrados) {
                if (Lists.newArrayList(situacoes).contains(SituacaoLoteBaixa.ESTORNADO)) {
                    sql += " and lb.integracaoEstorno = 0 ";
                } else if (Lists.newArrayList(situacoes).contains(SituacaoLoteBaixa.BAIXADO)) {
                    sql += " and lb.integracaoArrecadacao = 0 ";
                }
            }
            sql += "and lb.codigoLote like :parte " +
                "order by lb.dataPagamento desc";
            Query consulta = em.createNativeQuery(sql, LoteBaixa.class);
            consulta.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
            consulta.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
            consulta.setParameter("parte", "%" + parte.trim() + "%");

            List<String> situacoesLote = Lists.newArrayList();
            for (SituacaoLoteBaixa sit : situacoes) {
                situacoesLote.add(sit.name());
            }
            consulta.setParameter("situacoes", situacoesLote);
            consulta.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<LoteBaixa> completaLotePorPeriodo(String parte, Date dataInicial, Date dataFinal) {
        try {
            String sql = " select lb.* from lotebaixa lb " +
                " where lb.datapagamento between to_date(:datainicial, 'dd/MM/yyyy') " +
                " and to_date(:datafinal, 'dd/MM/yyyy') " +
                " and lb.codigolote like :parte " +
                " order by lb.datapagamento desc";
            Query consulta = em.createNativeQuery(sql, LoteBaixa.class);
            consulta.setParameter("parte", "%" + parte.trim() + "%");
            consulta.setParameter("datainicial", DataUtil.getDataFormatada(dataInicial));
            consulta.setParameter("datafinal", DataUtil.getDataFormatada(dataFinal));
            consulta.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 6)
    public AssistenteMapaArrecadacao gerarMapaArrecadacaoConsolidado(AssistenteMapaArrecadacao assistente, List<LoteBaixa> lotes,
                                                                     RelatorioMapaArrecadacaoConsolidadoControlador.FiltroRelatorioMapaConsolidado filtro) {
        LoteBaixa loteBaixaDaVez = null;
        try {
            List<MapaArrecadacao> listaArrecadacao = Lists.newArrayList();
            List<MapaArrecadacao> listaAcrescimos = Lists.newArrayList();
            List<MapaArrecadacao> listaDescontos = Lists.newArrayList();

            for (LoteBaixa loteBaixa : lotes) {
                List<MapaArrecadacao> listaArrecadacaoDoLote = Lists.newArrayList();
                List<MapaArrecadacao> listaAcrescimosDoLote = Lists.newArrayList();
                List<MapaArrecadacao> listaDescontosDoLote = Lists.newArrayList();

                loteBaixaDaVez = loteBaixa;
                montarMapaDeArrecadacao(filtro, listaArrecadacaoDoLote, listaAcrescimosDoLote, listaDescontosDoLote, loteBaixa);
                assistente.conta();

                agruparListasDoMapaArrecadacao(listaArrecadacao, listaArrecadacaoDoLote);
                agruparListasDoMapaArrecadacao(listaAcrescimos, listaAcrescimosDoLote);
                agruparListasDoMapaArrecadacao(listaDescontos, listaDescontosDoLote);
            }
            Collections.sort(listaArrecadacao);
            Collections.sort(listaAcrescimos);
            Collections.sort(listaDescontos);

            assistente.setListaArrecadacao(listaArrecadacao);
            assistente.setListaAcrescimos(listaAcrescimos);
            assistente.setListaDescontos(listaDescontos);
        } catch (Exception e) {
            if (loteBaixaDaVez != null) {
                logger.error("Erro ao gerar o mapa de arrecadação do lote: " + loteBaixaDaVez.getCodigoLote() + " {}", e);
            } else {
                logger.error("Erro ao gerar o mapa de arrecadação: {}", e);
            }
        }
        return assistente;
    }

    private void agruparListasDoMapaArrecadacao(List<MapaArrecadacao> lista, List<MapaArrecadacao> listaDoLote) {
        for (MapaArrecadacao item : listaDoLote) {
            if (lista.contains(item)) {
                MapaArrecadacao daLista = lista.get(lista.indexOf(item));
                lista.remove(daLista);
                item.setValor(daLista.getValor().add(item.getValor()).setScale(6, RoundingMode.HALF_UP));
            }
            lista.add(item);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void gerarImprimirMapaArrecadacao(LoteBaixa loteBaixa,
                                             RelatorioMapaArrecadacaoConsolidadoControlador.FiltroRelatorioMapaConsolidado filtro,
                                             LoteBaixaDoArquivo arquivo) {
        try {
            List<MapaArrecadacao> listaArrecadacao = Lists.newArrayList();
            List<MapaArrecadacao> listaAcrescimos = Lists.newArrayList();
            List<MapaArrecadacao> listaDescontos = Lists.newArrayList();

            List<MapaArrecadacao> listaArrecadacaoDoLote = Lists.newArrayList();
            List<MapaArrecadacao> listaAcrescimosDoLote = Lists.newArrayList();
            List<MapaArrecadacao> listaDescontosDoLote = Lists.newArrayList();

            montarMapaDeArrecadacao(filtro, listaArrecadacaoDoLote, listaAcrescimosDoLote, listaDescontosDoLote, loteBaixa);
            agruparListasDoMapaArrecadacao(listaArrecadacao, listaArrecadacaoDoLote);
            agruparListasDoMapaArrecadacao(listaAcrescimos, listaAcrescimosDoLote);
            agruparListasDoMapaArrecadacao(listaDescontos, listaDescontosDoLote);

            Collections.sort(listaArrecadacao);
            Collections.sort(listaAcrescimos);
            Collections.sort(listaDescontos);
            new ImprimeRelatoriosArrecadacao().imprimeMapaArrecadacao(Lists.newArrayList(loteBaixa), filtro, arquivo, listaArrecadacao, listaAcrescimos, listaDescontos, "MapaArrecadacao.jasper");
        } catch (Exception e) {
            logger.error("Erro ao gerar o mapa de arrecadação: {}", e);
        }
    }

    public List<VOItemDam> buscarVOItemDamDoDam(Long idDam) {
        String sql = "select idam.id,\n" +
            "       idam.dam_id,\n" +
            "       idam.parcela_id,\n" +
            "       idam.valorOriginalDevido,\n" +
            "       idam.juros,\n" +
            "       idam.multa,\n" +
            "       idam.correcaomonetaria,\n" +
            "       idam.honorarios,\n" +
            "       idam.desconto,\n" +
            "       idam.outrosAcrescimos,\n" +
            "       ex.ano as exercicio,\n" +
            "       pvd.dividaativa,\n" +
            "       dv.entidade_id,\n" +
            "       ex.id as idExercicio\n" +
            "from itemdam idam\n" +
            "inner join dam dam on dam.id = idam.dam_id\n" +
            "inner join exercicio ex on ex.id = dam.exercicio_id\n" +
            "inner join parcelavalordivida pvd on pvd.id = idam.parcela_id\n" +
            "inner join valordivida vd on vd.id = pvd.valordivida_id\n" +
            "inner join divida dv on dv.id = vd.divida_id\n" +
            "where idam.dam_id = :idDam ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDam", idDam);
        List<Object[]> lista = q.getResultList();
        List<VOItemDam> itens = Lists.newArrayList();
        for (Object[] obj : lista) {
            VOItemDam item = new VOItemDam();
            item.setId(((BigDecimal) obj[0]).longValue());
            item.setIdDam(((BigDecimal) obj[1]).longValue());
            item.setIdParcela(((BigDecimal) obj[2]).longValue());
            item.setValorOriginalDevido(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
            item.setJuros(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
            item.setMulta(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
            item.setCorrecaoMonetaria(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
            item.setHonorarios(obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO);
            item.setDesconto(obj[8] != null ? (BigDecimal) obj[8] : BigDecimal.ZERO);
            item.setOutrosAcrescimos(obj[9] != null ? (BigDecimal) obj[9] : BigDecimal.ZERO);
            item.setExercicio(((BigDecimal) obj[10]).intValue());
            item.setDividaAtiva(((BigDecimal) obj[11]).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE);
            item.setIdEntidade(obj[12] != null ? ((BigDecimal) obj[12]).longValue() : null);
            item.setIdExercicio(obj[13] != null ? ((BigDecimal) obj[13]).longValue() : null);
            itens.add(item);
        }
        return itens;
    }

    public List<TributoDAM> buscarTributosDamDoItem(Long idItemDam) {
        String sql = "select * from TributoDam where itemdam_id = :idItemDam";
        Query q = em.createNativeQuery(sql, TributoDAM.class);
        q.setParameter("idItemDam", idItemDam);
        return q.getResultList();
    }

    public List<VOTributoDam> buscarVOTributosDamDoItem(Long idItemDam) {
        String sql = "select tdam.id,\n" +
            "       tdam.itemdam_id,\n" +
            "       tdam.valororiginal,\n" +
            "       tdam.desconto,\n" +
            "       tr.id as idTributo,\n" +
            "       tr.codigo,\n" +
            "       tr.descricao,\n" +
            "       tr.tipotributo\n" +
            "from tributoDam tdam\n" +
            "inner join tributo tr on tr.id = tdam.tributo_id\n" +
            "where tdam.itemdam_id = :idItemDam";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemDam", idItemDam);
        List<Object[]> lista = q.getResultList();
        List<VOTributoDam> itens = Lists.newArrayList();
        for (Object[] obj : lista) {
            VOTributoDam item = new VOTributoDam();
            item.setId(((BigDecimal) obj[0]).longValue());
            item.setIdItemDam(((BigDecimal) obj[1]).longValue());
            item.setValorOriginal(obj[2] != null ? (BigDecimal) obj[2] : BigDecimal.ZERO);
            item.setDesconto(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
            item.setIdTributo(obj[4] != null ? ((BigDecimal) obj[4]).longValue() : null);
            item.setCodigoTributo(obj[5] != null ? ((BigDecimal) obj[5]).longValue() : null);
            item.setDescricaoTributo(obj[6] != null ? (String) obj[6] : "");
            item.setTipoTributo(obj[7] != null ? Tributo.TipoTributo.valueOf((String) obj[7]) : Tributo.TipoTributo.IMPOSTO);
            itens.add(item);
        }
        return itens;
    }

    public List<VOItemLoteBaixa> buscarVOItensLoteBaixaDoLote(Long idLoteBaixa) {
        String sql = "select ilb.id,\n" +
            "       ilb.dam_id,\n" +
            "       ilb.valorpago,\n" +
            "       dam.valororiginal as valorOriginalDam,\n" +
            "       dam.desconto as descontoDam,\n" +
            "       dam.juros as jurosDam,\n" +
            "       dam.multa as multaDam,\n" +
            "       dam.correcaomonetaria as correcaoDam,\n" +
            "       dam.honorarios as honorariosDam,\n" +
            "       lb.datapagamento,\n" +
            "       contabancaria.numeroconta || '-' || contabancaria.digitoverificador as conta,\n" +
            "       banco.numerobanco || ' - ' || banco.descricao as banco,\n" +
            "       banco.id as idBanco,\n" +
            "       lb.codigoLote as codigoLote,\n" +
            "       lb.dataFinanciamento as dataContabilizacao\n" +
            "from itemlotebaixa ilb\n" +
            "inner join dam dam on dam.id = ilb.dam_id\n" +
            "inner join lotebaixa lb on lb.id = ilb.lotebaixa_id\n" +
            "inner join subconta sconta on sconta.id = lb.subconta_id\n" +
            "inner join contabancariaentidade contabancaria on contabancaria.id = sconta.contabancariaentidade_id\n" +
            "inner join agencia ag on ag.id = contabancaria.agencia_id\n" +
            "inner join banco banco on banco.id = ag.banco_id\n" +
            "where ilb.lotebaixa_id = :idLoteBaixa\n" +
            " and ilb.valorpago > 0";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteBaixa", idLoteBaixa);
        List<Object[]> lista = q.getResultList();
        List<VOItemLoteBaixa> itens = Lists.newArrayList();
        for (Object[] obj : lista) {
            VOItemLoteBaixa item = new VOItemLoteBaixa();
            item.setId(((BigDecimal) obj[0]).longValue());
            item.setIdDam(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
            item.setValorPago(obj[2] != null ? (BigDecimal) obj[2] : BigDecimal.ZERO);
            item.setValorOriginalDam(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
            item.setDescontoDam(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
            item.setJurosDam(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
            item.setMultaDam(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
            item.setCorrecaoDam(obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO);
            item.setHonorariosDam(obj[8] != null ? (BigDecimal) obj[8] : BigDecimal.ZERO);
            item.setDataPagamento(obj[9] != null ? (Date) obj[9] : new Date());
            item.setConta(obj[10] != null ? (String) obj[10] : "");
            item.setBanco(obj[11] != null ? (String) obj[11] : "");
            item.setIdBanco(obj[12] != null ? ((BigDecimal) obj[12]).longValue() : 0L);
            item.setCodigoLote(obj[13] != null ? (String) obj[13] : "");
            item.setDataContabilizacao(obj[14] != null ? (Date) obj[14] : new Date());
            item.setIdLoteBaixa(idLoteBaixa);
            itens.add(item);
        }
        return itens;
    }

    public void montarMapaDeArrecadacao(RelatorioMapaArrecadacaoConsolidadoControlador.FiltroRelatorioMapaConsolidado filtro, List<MapaArrecadacao> listaArrecadacao, List<MapaArrecadacao> listaAcrescimos, List<MapaArrecadacao> listaDescontos, LoteBaixa loteBaixa) {
        Date dataInicial = filtro != null ? filtro.getDataInicio() : null;
        Date dataFinal = filtro != null ? filtro.getDataFinal() : null;

        List<VOItemArrecadadoPorTributo> itens = preencherItensArrecadacao(loteBaixa.getId(), dataInicial, dataFinal, null);

        Map<Long, Tributo> mapaTributo = Maps.newHashMap();

        List<VOItemArrecadadoPorTributo> itensArrecadacao = agruparItensMapaPorTributo(itens, TipoTributoDTO.getOriginais());
        for (VOItemArrecadadoPorTributo item : itensArrecadacao) {
            preencherMapaArrecadacao(mapaTributo, item, item.getValor(), listaArrecadacao, loteBaixa);
        }

        List<VOItemArrecadadoPorTributo> itensAcrescimos = agruparItensMapaPorTributo(itens, TipoTributoDTO.getAcrescimos());
        for (VOItemArrecadadoPorTributo item : itensAcrescimos) {
            preencherMapaArrecadacao(mapaTributo, item, item.getValor(), listaAcrescimos, loteBaixa);
        }

        for (VOItemArrecadadoPorTributo item : itens) {
            preencherMapaArrecadacao(mapaTributo, item, item.getDesconto(), listaDescontos, loteBaixa);
        }

        proporcionalizarValoresComIntegracao(listaArrecadacao, listaAcrescimos, listaDescontos, loteBaixa, dataInicial, dataFinal);
    }

    public List<VOItemArrecadadoPorTributo> preencherItensArrecadacao(Long idLoteBaixa, Date dataInicial, Date dataFinal, List<String> tipos) {
        service = Util.recuperarSpringBean(ServiceIntegracaoTributarioContabil.class);
        List<VOItemArrecadadoPorTributo> itens = buscarItensParaMapaArrecadacao(idLoteBaixa, dataInicial, dataFinal, tipos, service);

        Map<Long, List<VOItemArrecadadoPorTributo>> mapaItensPorParcela = Maps.newHashMap();
        for (VOItemArrecadadoPorTributo item : itens) {
            if (mapaItensPorParcela.containsKey(item.getIdParcela())) {
                mapaItensPorParcela.get(item.getIdParcela()).add(item);
            } else {
                mapaItensPorParcela.put(item.getIdParcela(), Lists.newArrayList(item));
            }
        }

        List<ResultadoParcela> valoresDoLote = buscarValoresDosDansDoLote(idLoteBaixa, dataInicial, dataFinal);

        for (ResultadoParcela damDoLote : valoresDoLote) {
            for (Map.Entry<Long, List<VOItemArrecadadoPorTributo>> entryItem : mapaItensPorParcela.entrySet()) {
                if (entryItem.getKey().equals(damDoLote.getIdParcela())) {
                    ValoresMapaArrecadacao valorMapa = new ValoresMapaArrecadacao();

                    for (VOItemArrecadadoPorTributo item : entryItem.getValue()) {
                        valorMapa.adicionarValor(item.getValor(), item.getTipoTributo());
                    }

                    valorMapa.subtrairValor(damDoLote.getValorImposto(), TipoTributoDTO.IMPOSTO);
                    valorMapa.subtrairValor(damDoLote.getValorJuros(), TipoTributoDTO.JUROS);
                    valorMapa.subtrairValor(damDoLote.getValorMulta(), TipoTributoDTO.MULTA);
                    valorMapa.subtrairValor(damDoLote.getValorCorrecao(), TipoTributoDTO.CORRECAO);
                    valorMapa.subtrairValor(damDoLote.getValorHonorarios(), TipoTributoDTO.HONORARIOS);

                    if (valorMapa.containsValor()) {
                        for (VOItemArrecadadoPorTributo item : itens) {
                            if (item.getIdParcela().equals(entryItem.getKey())) {
                                if (item.getTipoTributo().isImpostoTaxa()) {
                                    item.setValor(item.getValor().add(valorMapa.getArrecadacao()));
                                    valorMapa.setArrecadacao(BigDecimal.ZERO);
                                } else if (TipoTributoDTO.JUROS.equals(item.getTipoTributo())) {
                                    item.setValor(item.getValor().add(valorMapa.getJuros()));
                                    valorMapa.setJuros(BigDecimal.ZERO);
                                } else if (TipoTributoDTO.MULTA.equals(item.getTipoTributo())) {
                                    item.setValor(item.getValor().add(valorMapa.getMulta()));
                                    valorMapa.setMulta(BigDecimal.ZERO);
                                } else if (TipoTributoDTO.CORRECAO.equals(item.getTipoTributo())) {
                                    item.setValor(item.getValor().add(valorMapa.getCorrecao()));
                                    valorMapa.setCorrecao(BigDecimal.ZERO);
                                } else if (TipoTributoDTO.HONORARIOS.equals(item.getTipoTributo())) {
                                    item.setValor(item.getValor().add(valorMapa.getHonorarios()));
                                    valorMapa.setHonorarios(BigDecimal.ZERO);
                                }
                            }
                        }
                    }
                }
            }
        }
        return itens;
    }

    private void proporcionalizarValoresComIntegracao(List<MapaArrecadacao> listaArrecadacao, List<MapaArrecadacao> listaAcrescimos, List<MapaArrecadacao> listaDescontos, LoteBaixa loteBaixa, Date dataInicial, Date dataFinal) {
        List<VOItemIntegracaoTribCont> itensIntegracao = buscarItensIntegracaoGerados(loteBaixa.getId(), dataInicial, dataFinal);

        Map<ContaReceita, BigDecimal> mapContaValor = Maps.newHashMap();
        for (MapaArrecadacao mapa : listaArrecadacao) {
            if (!mapContaValor.containsKey(mapa.getConta())) {
                mapContaValor.put(mapa.getConta(), mapa.getValor());
            } else {
                mapContaValor.put(mapa.getConta(), mapContaValor.get(mapa.getConta()).add(mapa.getValor()));
            }
        }

        for (MapaArrecadacao mapa : listaAcrescimos) {
            if (!mapContaValor.containsKey(mapa.getConta())) {
                mapContaValor.put(mapa.getConta(), mapa.getValor());
            } else {
                mapContaValor.put(mapa.getConta(), mapContaValor.get(mapa.getConta()).add(mapa.getValor()));
            }
        }

        for (MapaArrecadacao mapa : listaDescontos) {
            if (mapContaValor.containsKey(mapa.getConta())) {
                mapContaValor.put(mapa.getConta(), mapContaValor.get(mapa.getConta()).subtract(mapa.getValor()));
            }
        }

        for (VOItemIntegracaoTribCont integracao : itensIntegracao) {
            loopContaValor:
            for (Map.Entry<ContaReceita, BigDecimal> contaValor : mapContaValor.entrySet()) {
                if (integracao.getIdConta().equals(contaValor.getKey().getId())) {
                    if (integracao.getValor().compareTo(contaValor.getValue()) != 0) {
                        for (MapaArrecadacao mapaArrecadacao : listaArrecadacao) {
                            if (mapaArrecadacao.getConta().equals(contaValor.getKey())) {
                                mapaArrecadacao.setValor(mapaArrecadacao.getValor().add(integracao.getValor().subtract(contaValor.getValue())));
                                continue loopContaValor;
                            }
                        }

                        for (MapaArrecadacao mapaArrecadacao : listaAcrescimos) {
                            if (mapaArrecadacao.getConta().equals(contaValor.getKey())) {
                                mapaArrecadacao.setValor(mapaArrecadacao.getValor().add(integracao.getValor().subtract(contaValor.getValue())));
                                continue loopContaValor;
                            }
                        }
                    }
                }
            }
        }
    }

    private void preencherMapaArrecadacao(Map<Long, Tributo> mapaTributo, VOItemArrecadadoPorTributo item,
                                          BigDecimal valor, List<MapaArrecadacao> mapas, LoteBaixa loteBaixa) {
        if (!mapaTributo.containsKey(item.getIdTributo())) {
            mapaTributo.put(item.getIdTributo(), tributoFacade.recuperar(item.getIdTributo()));
        }

        MapaArrecadacao mapa = instanciarMapaArrecadacao(item, valor,
            mapaTributo.get(item.getIdTributo()), loteBaixa);

        adicionarMapaNaLista(mapas, mapa, valor);
    }

    public List<VOItemArrecadadoPorTributo> agruparItensMapaPorTributo(List<VOItemArrecadadoPorTributo> itens, List<TipoTributoDTO> tiposTributo) {
        List<VOItemArrecadadoPorTributo> itensPorTributo = Lists.newArrayList();
        for (VOItemArrecadadoPorTributo item : itens) {
            for (TipoTributoDTO tipoTributo : tiposTributo) {
                if (tipoTributo.equals(item.getTipoTributo())) {
                    itensPorTributo.add(item);
                }
            }
        }
        return itensPorTributo;
    }

    private void adicionarMapaNaLista(List<MapaArrecadacao> mapas, MapaArrecadacao mapa, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            if (mapas.contains(mapa)) {
                mapas.get(mapas.indexOf(mapa)).setValor(
                    mapas.get(mapas.indexOf(mapa)).getValor().add(valor));
            } else {
                mapas.add(mapa);
            }
        }
    }

    private MapaArrecadacao instanciarMapaArrecadacao(VOItemArrecadadoPorTributo item, BigDecimal valor, Tributo tributo,
                                                      LoteBaixa loteBaixa) {
        MapaArrecadacao mapa = new MapaArrecadacao();
        mapa.setValor(valor);
        mapa.setPagamento(item.getDataPagamento());
        mapa.setConta(item.getContaReceita());
        mapa.setTributo(tributo);
        mapa.setAgenteArrecadador(loteBaixa.montabancoConta(false));

        return mapa;
    }

    private List<VOItemArrecadadoPorTributo> buscarItensParaMapaArrecadacao(Long idLoteBaixa, Date dataInicial,
                                                                            Date dataFinal, List<String> tiposTributo,
                                                                            ServiceIntegracaoTributarioContabil service) {

        String sql = " select pvd.id as parcela_id, tr.id as tributo_id, lb.id as lote_id, sum(tdam.valororiginal) as original, " +
            "                 sum(tdam.desconto) as desconto, ex.ano, lb.datapagamento, coalesce(pvd.dividaativa, 0) as divida_ativa, " +
            "                 lb.codigolote, tr.tipotributo " +
            " from lotebaixa lb " +
            " inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id " +
            " inner join dam on dam.id = ilb.dam_id " +
            " inner join exercicio ex on ex.id = dam.exercicio_id " +
            " inner join itemdam idam on idam.dam_id = dam.id " +
            " inner join tributodam tdam on tdam.itemdam_id = idam.id " +
            " inner join tributo tr on tr.id = tdam.tributo_id " +
            " inner join parcelavalordivida pvd on pvd.id = idam.parcela_id " +
            " where tdam.valororiginal > 0 " +
            " and tr.tipotributo is not null " +
            ((tiposTributo != null && !tiposTributo.isEmpty()) ? " and tr.tipoTributo in (:tiposTributo) " : "") +
            ((idLoteBaixa != null) ? " and lb.id = :idLoteBaixa " :
                ((dataInicial != null && dataFinal != null) ? " and lb.datapagamento between to_date(:dataInicial, 'dd/MM/yyyy') " +
                    " and to_date(:dataFinal, 'dd/MM/yyyy') " +
                    " and lb.situacaoLoteBaixa in (:situacoes) " : "")) +
            " group by pvd.id, tr.id, lb.id, ex.ano, lb.datapagamento, coalesce(pvd.dividaativa, 0), lb.codigolote, tr.tipotributo ";


        Query q = em.createNativeQuery(sql);

        if (tiposTributo != null && !tiposTributo.isEmpty()) {
            q.setParameter("tiposTributo", tiposTributo);
        }
        if (idLoteBaixa != null) {
            q.setParameter("idLoteBaixa", idLoteBaixa);
        } else if (dataInicial != null && dataFinal != null) {
            q.setParameter("dataInicio", DataUtil.getDataFormatada(dataInicial));
            q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
            q.setParameter("situacoes", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        }

        List<Object[]> valores = q.getResultList();
        List<VOItemArrecadadoPorTributo> itens = Lists.newArrayList();

        if (valores != null && !valores.isEmpty()) {
            for (Object[] obj : valores) {
                VOItemArrecadadoPorTributo item = new VOItemArrecadadoPorTributo();

                item.setIdParcela(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
                item.setIdTributo(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
                item.setIdLoteBaixa(obj[2] != null ? ((BigDecimal) obj[2]).longValue() : null);
                item.setValor(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
                item.setDesconto(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
                item.setExercicio(obj[5] != null ? ((BigDecimal) obj[5]).intValue() : null);
                item.setDataPagamento(obj[6] != null ? (Date) obj[6] : null);
                item.setDividaAtiva(obj[7] != null && ((BigDecimal) obj[7]).intValue() == 1);
                item.setCodigoLoteBaixa(obj[8] != null ? (String) obj[8] : null);
                item.setTipoTributo(obj[9] != null ? TipoTributoDTO.valueOf((String) obj[9]) : null);

                try {
                    ContaTributoReceita contaTributo = service.getContaTributoReceitaPorTributoExercicio(item.getIdTributo(), item.getDataPagamento());
                    if (contaTributo != null) {
                        OperacaoReceita operacaoReceita = item.getDividaAtiva() ? contaTributo.getOperacaoArrecadacaoDivAtiva() : contaTributo.getOperacaoArrecadacaoExercicio();
                        item.setContaReceita(item.getDividaAtiva() ? contaTributo.getContaDividaAtiva() : contaTributo.getContaExercicio());
                        item.setOperacaoReceita(operacaoReceita);

                        itens.add(item);
                    } else {
                        logger.error("Conta nao encontrada para o tributo: {}", item.getIdTributo());
                    }
                } catch (Exception e) {
                    logger.error("Erro ao recuperar conta do tributo: {} {}", item.getIdTributo(), e);
                }
            }
        }

        return itens;
    }

    public List<ResultadoParcela> buscarValoresDosDansDoLote(Long idLoteBaixa, Date dataInicial, Date dataFinal) {
        String sql = " select pvd.id as idparcela, sum(idam.juros) as juros, sum(idam.multa) as multa, sum(idam.correcaomonetaria) as correcao, " +
            "                 sum(idam.honorarios) as honorarios, sum(idam.valororiginaldevido) as valororiginal " +
            " from lotebaixa lb " +
            " inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id " +
            " inner join dam on dam.id = ilb.dam_id " +
            " inner join exercicio ex on ex.id = dam.exercicio_id " +
            " inner join itemdam idam on idam.dam_id = dam.id " +
            " inner join parcelavalordivida pvd on pvd.id = idam.parcela_id ";

        if (idLoteBaixa != null) {
            sql += " where lb.id = :idLoteBaixa ";
        } else if (dataInicial != null && dataFinal != null) {
            sql += " where lb.datapagamento between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " +
                " and lb.situacaoLoteBaixa in (:situacoes) ";
        }

        sql += " group by lb.datapagamento, pvd.id, ex.ano, lb.id, lb.codigolote ";

        Query q = em.createNativeQuery(sql);

        if (idLoteBaixa != null) {
            q.setParameter("idLoteBaixa", idLoteBaixa);
        } else if (dataInicial != null && dataFinal != null) {
            q.setParameter("dataInicio", DataUtil.getDataFormatada(dataInicial));
            q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
            q.setParameter("situacoes", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        }

        List<Object[]> resultados = q.getResultList();
        List<ResultadoParcela> valores = Lists.newArrayList();

        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] obj : resultados) {
                ResultadoParcela valor = new ResultadoParcela();

                valor.setIdParcela(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
                valor.setValorJuros(obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);
                valor.setValorMulta(obj[2] != null ? (BigDecimal) obj[2] : BigDecimal.ZERO);
                valor.setValorCorrecao(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
                valor.setValorHonorarios(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
                valor.setValorImposto(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);

                valores.add(valor);
            }
        }

        return valores;
    }

    public void carregarValoresArrecadadosDeAcordoComIntegracao(List<VOItemIntegracaoTribCont> itensIntegracao, List<VOItemArrecadadoPorTributo> itensArrecadacao, boolean corrigirValor) throws ContaReceitaException {
        service = (ServiceIntegracaoTributarioContabil) ContextLoader.getCurrentWebApplicationContext()
            .getBean("serviceIntegracaoTributarioContabil");

        Map<AgrupadorValorConta, BigDecimal> mapaValor = Maps.newHashMap();
        Map<AgrupadorValorConta, BigDecimal> mapaValorAdicionado = Maps.newHashMap();
        for (VOItemIntegracaoTribCont valorIntegrado : itensIntegracao) {
            AgrupadorValorConta agrupador = new AgrupadorValorConta(valorIntegrado.getIdLoteBaixa(), valorIntegrado.getDataPagamento(), valorIntegrado.getIdConta());
            if (mapaValor.containsKey(agrupador)) {
                mapaValor.put(agrupador, mapaValor.get(agrupador).add(valorIntegrado.getValor()));
            } else {
                mapaValor.put(agrupador, valorIntegrado.getValor());
            }
        }

        for (VOItemArrecadadoPorTributo item : itensArrecadacao) {
            ContaTributoReceita contaTributo = service.getContaTributoReceitaPorTributoExercicio(item.getIdTributo(), item.getDataPagamento());
            boolean arrumouValor = false;
            if (contaTributo != null) {
                OperacaoReceita operacaoReceita = item.getDividaAtiva() ? contaTributo.getOperacaoArrecadacaoDivAtiva() : contaTributo.getOperacaoArrecadacaoExercicio();

                item.setContaReceita(item.getDividaAtiva() ? contaTributo.getContaDividaAtiva() : contaTributo.getContaExercicio());
                item.setOperacaoReceita(operacaoReceita);

                if (corrigirValor) {
                    for (VOItemIntegracaoTribCont integrado : itensIntegracao) {
                        if (integrado.getDataPagamento().equals(item.getDataPagamento()) && integrado.getIdConta().equals(item.getContaReceita().getId()) && integrado.getIdLoteBaixa().equals(item.getIdLoteBaixa())) {
                            AgrupadorValorConta agrupador = new AgrupadorValorConta(item.getIdLoteBaixa(), item.getDataPagamento(), item.getContaReceita().getId());
                            if (mapaValor.containsKey(agrupador)) {
                                BigDecimal valorCorrigido = item.getValor().multiply(integrado.getValor()).divide(mapaValor.get(agrupador), 2, RoundingMode.HALF_UP);
                                item.setValor(valorCorrigido);
                                arrumouValor = true;

                                if (!mapaValorAdicionado.containsKey(agrupador)) {
                                    mapaValorAdicionado.put(agrupador, valorCorrigido);
                                } else {
                                    mapaValorAdicionado.put(agrupador, mapaValorAdicionado.get(agrupador).add(valorCorrigido));
                                }
                                break;
                            } else {
                                logger.error("nao achou no mapa: " + agrupador);
                            }
                        }
                    }
                }
            } else {
                logger.error("Conta nao encontrada");
            }

            if (!arrumouValor && corrigirValor) {
                logger.error("nao arrumou o valor do Lote: " + item.getIdLoteBaixa() + " Conta: " + item.getContaReceita().getId() + " Data: " + DataUtil.getDataFormatada(item.getDataPagamento()) + " com valor de: " + item.getValor());
            }
        }

        for (AgrupadorValorConta agrupador : mapaValorAdicionado.keySet()) {
            if (mapaValorAdicionado.get(agrupador).compareTo(mapaValor.get(agrupador)) != 0) {
                BigDecimal diferenca = mapaValor.get(agrupador).subtract(mapaValorAdicionado.get(agrupador)).setScale(2, RoundingMode.HALF_UP);
                for (VOItemArrecadadoPorTributo item : itensArrecadacao) {
                    if (item.getContaReceita() != null) {
                        if (agrupador.getData().equals(item.getDataPagamento()) && agrupador.getIdConta().equals(item.getContaReceita().getId()) && agrupador.getIdLoteBaixa().equals(item.getIdLoteBaixa())) {
                            item.setValor(item.getValor().add(diferenca));
                            break;
                        }
                    } else {
                        logger.error("nao achou a conta pra arrumar a diferenca de " + diferenca);
                    }
                }
            }
        }
    }

    public List<VOItemIntegracaoTribCont> buscarItensIntegracaoGerados(Long idLoteBaixa) {
        return buscarItensIntegracaoGerados(idLoteBaixa, null, null);
    }

    public List<VOItemIntegracaoTribCont> buscarItensIntegracaoGerados(Long idLoteBaixa, Date dataInicio, Date dataFinal) {
        StringBuilder sql = new StringBuilder();
        sql.append("select item.dataPagamento, conta.id as idConta, conta.codigo, lb.id as idLoteBaixa, lb.codigoLote, sum(item.valor) as valor ")
            .append("from LoteBaixa lb ")
            .append("inner join ItemIntegracaoTribCont item on item.loteBaixa_id = lb.id ")
            .append("inner join Conta conta on conta.id = item.contaReceita_id ")
            .append("where ((item.tipo = :tipoEstorno and lb.situacaoLoteBaixa = :estornado) or (item.tipo = :tipoArrecadacao and lb.situacaoLoteBaixa != :estornado))");
        if (idLoteBaixa != null) {
            sql.append(" and item.loteBaixa_id = :idLoteBaixa ");
        } else if (dataInicio != null && dataFinal != null) {
            sql.append(" and item.dataPagamento between :dataInicio and :dataFinal ");
            sql.append(" and lb.situacaoLoteBaixa in (:situacoes) ");
        }
        sql.append(" group by item.dataPagamento, conta.id, conta.codigo, lb.id, lb.codigoLote");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("tipoEstorno", TipoIntegracao.ESTORNO_ARRECADACAO.name());
        q.setParameter("tipoArrecadacao", TipoIntegracao.ARRECADACAO.name());
        q.setParameter("estornado", SituacaoLoteBaixa.ESTORNADO.name());
        if (idLoteBaixa != null) {
            q.setParameter("idLoteBaixa", idLoteBaixa);
        } else if (dataInicio != null && dataFinal != null) {
            q.setParameter("dataInicio", dataInicio);
            q.setParameter("dataFinal", dataFinal);
            q.setParameter("situacoes", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name(), SituacaoLoteBaixa.ESTORNADO.name()));
        }
        List<Object[]> lista = q.getResultList();
        List<VOItemIntegracaoTribCont> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            VOItemIntegracaoTribCont valor = new VOItemIntegracaoTribCont();
            valor.setDataPagamento((Date) obj[0]);
            valor.setIdConta(((BigDecimal) obj[1]).longValue());
            valor.setConta((String) obj[2]);
            valor.setIdLoteBaixa(((BigDecimal) obj[3]).longValue());
            valor.setCodigoLoteBaixa((String) obj[4]);
            valor.setValor((BigDecimal) obj[5]);
            retorno.add(valor);
        }
        return retorno;
    }

    public List<VOItemArrecadadoPorTributo> buscarItensArrecadadoPorTributo(Long idLoteBaixa, List<String> tiposTributo) {
        return buscarItensArrecadadoPorTributo(null, null, idLoteBaixa, tiposTributo);
    }

    private List<VOItemArrecadadoPorTributo> buscarItensArrecadadoPorTributo(Date dataInicial, Date dataFinal, Long idLoteBaixa, List<String> tiposTributo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select lb.dataPagamento, tdam.tributo_id, pvd.dividaAtiva, ")
            .append("ex.ano, tr.tipoTributo, lb.id as idLoteBaixa, lb.codigoLote, ");
        if (tiposTributo != null && !tiposTributo.isEmpty()) {
            sql.append(" sum(tdam.valorOriginal) ");
        } else {
            sql.append(" sum(tdam.desconto) ");
        }
        sql.append("from LoteBaixa lb " +
            "inner join ItemLoteBaixa ilb on ilb.loteBaixa_id = lb.id " +
            "inner join Dam dam on dam.id = ilb.dam_id " +
            "inner join Exercicio ex on ex.id = dam.exercicio_id " +
            "inner join ItemDam idam on idam.dam_id = dam.id " +
            "inner join TributoDam tdam on tdam.itemdam_id = idam.id " +
            "inner join Tributo tr on tr.id = tdam.tributo_id " +
            "inner join ParcelaValorDivida pvd on pvd.id = idam.parcela_id " +
            "inner join ValorDivida vd on vd.id = pvd.valorDivida_id " +
            "inner join Calculo cal on cal.id = vd.calculo_id ");
        sql.append(" where tdam.valorOriginal > 0 ");
        if (tiposTributo != null && !tiposTributo.isEmpty()) {
            sql.append(" and tr.tipoTributo in (:tipoTributo) ");
        }
        if (idLoteBaixa != null) {
            sql.append(" and lb.id = :idLoteBaixa ");
        } else if (dataInicial != null && dataFinal != null) {
            sql.append(" and (lb.dataPagamento >= :dataInicio and lb.dataPagamento <= :dataFinal) ");
            sql.append(" and lb.situacaoLoteBaixa in (:situacoes) ");
        }
        sql.append(" group by lb.dataPagamento,tdam.tributo_id, pvd.dividaAtiva, ex.ano, " +
            "tr.tipoTributo, lb.id, lb.codigoLote");

        Query q = em.createNativeQuery(sql.toString());
        if (tiposTributo != null && !tiposTributo.isEmpty()) {
            q.setParameter("tipoTributo", tiposTributo);
        }
        if (idLoteBaixa != null) {
            q.setParameter("idLoteBaixa", idLoteBaixa);
        } else if (dataInicial != null && dataFinal != null) {
            q.setParameter("dataInicio", dataInicial);
            q.setParameter("dataFinal", dataFinal);
            q.setParameter("situacoes", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        }
        List<Object[]> lista = q.getResultList();
        List<VOItemArrecadadoPorTributo> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            VOItemArrecadadoPorTributo item = new VOItemArrecadadoPorTributo();
            item.setDataPagamento((Date) obj[0]);
            item.setIdTributo(((BigDecimal) obj[1]).longValue());
            item.setDividaAtiva(((BigDecimal) obj[2]).compareTo(BigDecimal.ZERO) != 0);
            item.setExercicio(((BigDecimal) obj[3]).intValue());
            item.setTipoTributo(TipoTributoDTO.valueOf((String) obj[4]));
            item.setIdLoteBaixa(((BigDecimal) obj[5]).longValue());
            item.setCodigoLoteBaixa((String) obj[6]);
            item.setValor((BigDecimal) obj[7]);
            item.setDesconto((BigDecimal) obj[8]);
            if (item.getValor().compareTo(BigDecimal.ZERO) > 0) {
                retorno.add(item);
            }
        }
        return retorno;
    }

    private void gerarImprimeMapaArrecadacaoComAcrescimosOld(RelatorioMapaArrecadacaoConsolidadoControlador.FiltroRelatorioMapaConsolidado filtro, List<MapaArrecadacao> listaArrecadacao, List<MapaArrecadacao> listaAcrescimos, List<MapaArrecadacao> listaDescontos, LoteBaixa loteBaixa) {
        Map<MapaDeContas, ContaReceita> mapaDeContas = Maps.newHashMap();

        List<VOItemLoteBaixa> itensLoteBaixa = buscarVOItensLoteBaixaDoLote(loteBaixa.getId());
        if (!itensLoteBaixa.isEmpty()) {
            Collections.sort(itensLoteBaixa);
            for (VOItemLoteBaixa item : itensLoteBaixa) {
                if (item.getIdDam() != null) {
                    List<MapaArrecadacao> listaArrecadacaoItem = Lists.newArrayList();
                    List<MapaArrecadacao> listaAcrescimosItem = Lists.newArrayList();
                    List<MapaArrecadacao> listaDescontosItem = Lists.newArrayList();

                    List<VOItemDam> itensDam = buscarVOItemDamDoDam(item.getIdDam());
                    Collections.sort(itensDam);
                    for (VOItemDam itemDam : itensDam) {
                        List<VOTributoDam> tributosDam = buscarVOTributosDamDoItem(itemDam.getId());
                        BigDecimal totalDam = BigDecimal.ZERO;
                        Collections.sort(tributosDam);
                        for (VOTributoDam tributoDAM : tributosDam) {
                            totalDam = totalDam.add(tributoDAM.getValorComDesconto());
                        }
                        BigDecimal totalItem = BigDecimal.ZERO;
                        BigDecimal valorPagoDoItem = item.getValorPago();
                        if (item.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                            valorPagoDoItem = itemDam.getValorTotal().multiply(item.getValorPago()).divide(item.getTotal(), 8, RoundingMode.HALF_UP);
                        }

                        for (VOTributoDam tributoDAM : tributosDam) {
                            if (tributoDAM.getTipoTributo().isAcrescimosSemHonorarios()
                                && item.getTotalAcrescimosDamSemHonorarios().compareTo(BigDecimal.ZERO) > 0) {

                                BigDecimal valorAcrescimos = tributoDAM.getValorOriginal();
                                if (valorPagoDoItem.compareTo(item.getValorPago()) != 0) {
                                    BigDecimal proporcaoAcrescimos = tributoDAM.getValorComDesconto().compareTo(BigDecimal.ZERO) != 0 ? tributoDAM.getValorComDesconto().divide(totalDam, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                                    valorAcrescimos = (proporcaoAcrescimos.multiply(valorPagoDoItem));
                                }
                                valorAcrescimos = valorAcrescimos.setScale(2, RoundingMode.HALF_UP);

                                if (valorAcrescimos.compareTo(BigDecimal.ZERO) > 0 || tributoDAM.getValorOriginal().compareTo(BigDecimal.ZERO) > 0) {
                                    MapaArrecadacao mapa = new MapaArrecadacao();
                                    ContaReceita contaReceita;
                                    MapaDeContas contas = new MapaDeContas(tributoDAM.getIdTributo(), itemDam.getExercicio(), ContaTributoReceita.TipoContaReceita.EXERCICIO, itemDam.getDividaAtiva());

                                    if (!mapaDeContas.containsKey(contas)) {
                                        contaReceita = enquadramentoTributoExercicioFacade.buscarContasDoTributo(tributoDAM.getIdTributo(),
                                            item.getDataPagamento(),
                                            ContaTributoReceita.TipoContaReceita.EXERCICIO, itemDam.getDividaAtiva());
                                        mapaDeContas.put(contas, contaReceita);
                                    } else {
                                        contaReceita = mapaDeContas.get(contas);
                                    }
                                    mapa.setConta(contaReceita);
                                    mapa.setTributo(new Tributo(tributoDAM.getIdTributo(), tributoDAM.getCodigoTributo(), tributoDAM.getDescricaoTributo(), tributoDAM.getTipoTributo()));
                                    totalItem = totalItem.add(valorAcrescimos);
                                    mapa.setValor(valorAcrescimos);
                                    if (filtro != null && filtro.getAgrupaPorData()) {
                                        mapa.setPagamento(item.getDataPagamento());
                                    }
                                    if (filtro != null && filtro.getAgrupaPorAgenteArrecadador()) {
                                        mapa.setAgenteArrecadador(item.getBancoConta());
                                    }
                                    if (listaAcrescimosItem.contains(mapa)) {
                                        MapaArrecadacao daLista = listaAcrescimosItem.get(listaAcrescimosItem.indexOf(mapa));
                                        listaAcrescimosItem.remove(daLista);
                                        mapa.setValor(daLista.getValor().add(mapa.getValor()));
                                    }
                                    if (podeAdicionarConta(filtro, mapa.getConta())) {
                                        listaAcrescimosItem.add(mapa);
                                    }
                                }
                            }

                            if (tributoDAM.getTipoTributo().isHonorarios()
                                && item.getHonorariosDam().compareTo(BigDecimal.ZERO) > 0) {

                                BigDecimal valorHonorarios = tributoDAM.getValorOriginal();
                                if (valorPagoDoItem.compareTo(item.getValorPago()) != 0) {
                                    BigDecimal proporcaoHonorarios = tributoDAM.getValorComDesconto().compareTo(BigDecimal.ZERO) != 0 ? tributoDAM.getValorComDesconto().divide(totalDam, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                                    valorHonorarios = (proporcaoHonorarios.multiply(valorPagoDoItem));
                                }
                                valorHonorarios = valorHonorarios.setScale(2, RoundingMode.HALF_UP);

                                if (valorHonorarios.compareTo(BigDecimal.ZERO) > 0 || tributoDAM.getValorOriginal().compareTo(BigDecimal.ZERO) > 0) {
                                    MapaArrecadacao mapa = new MapaArrecadacao();
                                    ContaReceita contaReceita;
                                    MapaDeContas contas = new MapaDeContas(tributoDAM.getIdTributo(),
                                        itemDam.getExercicio(),
                                        ContaTributoReceita.TipoContaReceita.EXERCICIO,
                                        itemDam.getDividaAtiva());

                                    if (!mapaDeContas.containsKey(contas)) {
                                        contaReceita = enquadramentoTributoExercicioFacade.buscarContasDoTributo(tributoDAM.getIdTributo(),
                                            item.getDataPagamento(),
                                            ContaTributoReceita.TipoContaReceita.EXERCICIO,
                                            itemDam.getDividaAtiva());
                                        mapaDeContas.put(contas, contaReceita);
                                    } else {
                                        contaReceita = mapaDeContas.get(contas);
                                    }
                                    mapa.setConta(contaReceita);
                                    mapa.setTributo(new Tributo(tributoDAM.getIdTributo(), tributoDAM.getCodigoTributo(), tributoDAM.getDescricaoTributo(), tributoDAM.getTipoTributo()));
                                    mapa.setValor(valorHonorarios);
                                    totalItem = totalItem.add(valorHonorarios);
                                    if (filtro != null && filtro.getAgrupaPorData()) {
                                        mapa.setPagamento(item.getDataPagamento());
                                    }
                                    if (filtro != null && filtro.getAgrupaPorAgenteArrecadador()) {
                                        mapa.setAgenteArrecadador(item.getBancoConta());
                                    }

                                    if (listaAcrescimosItem.contains(mapa)) {
                                        MapaArrecadacao daLista = listaAcrescimosItem.get(listaAcrescimosItem.indexOf(mapa));
                                        listaAcrescimosItem.remove(daLista);
                                        mapa.setValor(daLista.getValor().add(mapa.getValor()));
                                    }
                                    if (podeAdicionarConta(filtro, mapa.getConta())) {
                                        listaAcrescimosItem.add(mapa);
                                    }
                                }
                            }

                            if (tributoDAM.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                                BigDecimal valorDesconto = tributoDAM.getDesconto();
                                valorDesconto = valorDesconto.setScale(2, RoundingMode.HALF_UP);

                                MapaArrecadacao mapa = new MapaArrecadacao();
                                ContaReceita contaReceita;
                                MapaDeContas contas = new MapaDeContas(tributoDAM.getIdTributo(), itemDam.getExercicio(), ContaTributoReceita.TipoContaReceita.DESCONTO, itemDam.getDividaAtiva());
                                if (!mapaDeContas.containsKey(contas)) {
                                    contaReceita = enquadramentoTributoExercicioFacade.buscarContasDoTributo(tributoDAM.getIdTributo(),
                                        item.getDataPagamento(), ContaTributoReceita.TipoContaReceita.DESCONTO, itemDam.getDividaAtiva());
                                    mapaDeContas.put(contas, contaReceita);
                                } else {
                                    contaReceita = mapaDeContas.get(contas);
                                }
                                mapa.setConta(contaReceita);
                                mapa.setTributo(new Tributo(tributoDAM.getIdTributo(), tributoDAM.getCodigoTributo(), tributoDAM.getDescricaoTributo(), tributoDAM.getTipoTributo()));
                                mapa.setValor(valorDesconto);
                                if (filtro != null && filtro.getAgrupaPorData()) {
                                    mapa.setPagamento(item.getDataPagamento());
                                }
                                if (filtro != null && filtro.getAgrupaPorAgenteArrecadador()) {
                                    mapa.setAgenteArrecadador(item.getBancoConta());
                                }
                                if (listaDescontosItem.contains(mapa)) {
                                    MapaArrecadacao daLista = listaDescontosItem.get(listaDescontosItem.indexOf(mapa));
                                    listaDescontosItem.remove(daLista);
                                    mapa.setValor(daLista.getValor().add(mapa.getValor()));
                                }
                                if (podeAdicionarConta(filtro, mapa.getConta())) {
                                    listaDescontosItem.add(mapa);
                                }
                            }

                            if (tributoDAM.getTipoTributo().isImpostoTaxa()) {

                                BigDecimal valorArrecadacao = tributoDAM.getValorOriginal();
                                if (valorPagoDoItem.compareTo(item.getValorPago()) != 0) {
                                    BigDecimal proporcaoArrecadacao = tributoDAM.getValorComDesconto().compareTo(BigDecimal.ZERO) != 0 ? tributoDAM.getValorComDesconto().divide(totalDam, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                                    valorArrecadacao = (proporcaoArrecadacao.multiply(valorPagoDoItem));
                                }
                                valorArrecadacao = valorArrecadacao.setScale(2, RoundingMode.HALF_UP);

                                if (valorArrecadacao.compareTo(BigDecimal.ZERO) > 0 || tributoDAM.getValorOriginal().compareTo(BigDecimal.ZERO) > 0) {
                                    MapaArrecadacao mapa = new MapaArrecadacao();
                                    ContaReceita contaReceita;
                                    MapaDeContas contas = new MapaDeContas(tributoDAM.getIdTributo(), itemDam.getExercicio(), ContaTributoReceita.TipoContaReceita.EXERCICIO, itemDam.getDividaAtiva());
                                    if (!mapaDeContas.containsKey(contas)) {
                                        contaReceita = enquadramentoTributoExercicioFacade.buscarContasDoTributo(tributoDAM.getIdTributo(),
                                            item.getDataPagamento(), ContaTributoReceita.TipoContaReceita.EXERCICIO, itemDam.getDividaAtiva());
                                        mapaDeContas.put(contas, contaReceita);
                                    } else {
                                        contaReceita = mapaDeContas.get(contas);
                                    }
                                    mapa.setConta(contaReceita);
                                    mapa.setTributo(new Tributo(tributoDAM.getIdTributo(), tributoDAM.getCodigoTributo(), tributoDAM.getDescricaoTributo(), tributoDAM.getTipoTributo()));
                                    mapa.setValor(valorArrecadacao);
                                    totalItem = totalItem.add(valorArrecadacao);
                                    if (filtro != null && filtro.getAgrupaPorData()) {
                                        mapa.setPagamento(item.getDataPagamento());
                                    }
                                    if (filtro != null && filtro.getAgrupaPorAgenteArrecadador()) {
                                        mapa.setAgenteArrecadador(item.getBancoConta());
                                    }
                                    if (listaArrecadacaoItem.contains(mapa)) {
                                        MapaArrecadacao daLista = listaArrecadacaoItem.get(listaArrecadacaoItem.indexOf(mapa));
                                        listaArrecadacaoItem.remove(daLista);
                                        mapa.setValor(daLista.getValor().add(mapa.getValor()));
                                    }
                                    if (podeAdicionarConta(filtro, mapa.getConta())) {
                                        listaArrecadacaoItem.add(mapa);
                                    }
                                }
                            }
                        }
                        BigDecimal diferenca = totalItem.subtract(valorPagoDoItem);
                        if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                            arredondarDiferencaNaIntegracao(listaArrecadacaoItem, valorPagoDoItem, totalItem);
                        }
                    }
                    listaArrecadacao.addAll(listaArrecadacaoItem);
                    listaAcrescimos.addAll(listaAcrescimosItem);
                    listaDescontos.addAll(listaDescontosItem);
                }
            }
        }
        gerarMapaArrecadacaoDasListas(listaArrecadacao, listaAcrescimos, listaDescontos);
    }

    private void gerarMapaArrecadacaoDasListas(List<MapaArrecadacao> listaArrecadacao, List<MapaArrecadacao> listaAcrescimos, List<MapaArrecadacao> listaDescontos) {
        for (MapaArrecadacao desconto : listaDescontos) {
            if (listaAcrescimos.contains(desconto)) {
                listaAcrescimos.get(listaAcrescimos.indexOf(desconto)).setValor(listaAcrescimos.get(listaAcrescimos.indexOf(desconto)).getValor().add(desconto.getValor()));
            }
            if (listaArrecadacao.contains(desconto)) {
                listaArrecadacao.get(listaArrecadacao.indexOf(desconto)).setValor(listaArrecadacao.get(listaArrecadacao.indexOf(desconto)).getValor().add(desconto.getValor()));
            }
        }
    }

    private void arredondarDiferencaNaIntegracao(List<MapaArrecadacao> mapa, BigDecimal total, BigDecimal lancado) {
        if (total.compareTo(lancado) != 0 && lancado.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diferenca = total.subtract(lancado).setScale(2, RoundingMode.HALF_UP);

            while (diferenca.compareTo(BigDecimal.ZERO) != 0 && mapa.size() > 0) {
                BigDecimal valorProporcionalDeferenca = BigDecimal.ZERO;
                for (MapaArrecadacao item : mapa) {
                    if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                        valorProporcionalDeferenca = diferenca.compareTo(BigDecimal.ZERO) > 0 ? UM_CENTAVO : diferenca.compareTo(BigDecimal.ZERO) < 0 ? MENOS_UM_CENTAVO : BigDecimal.ZERO;
                        item.setValor((item.getValor().add(valorProporcionalDeferenca)));
                        lancado = lancado.add(valorProporcionalDeferenca);
                        diferenca = total.subtract(lancado).setScale(2, RoundingMode.HALF_UP);
                    }
                }
                if (valorProporcionalDeferenca.compareTo(BigDecimal.ZERO) != 0) {
                    diferenca = total.subtract(lancado).setScale(2, RoundingMode.HALF_UP);
                } else {
                    diferenca = BigDecimal.ZERO;
                }
            }
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void geraImprimeMapaHonorarios(List<LoteBaixa> lotes,
                                          RelatorioMapaArrecadacaoConsolidadoControlador.FiltroRelatorioMapaConsolidado filtro,
                                          LoteBaixaDoArquivo arquivo) {
        try {

            List<MapaArrecadacao> listaAcrescimos = Lists.newArrayList();

            for (LoteBaixa loteBaixa : lotes) {
                List<MapaArrecadacao> listaAcrescimosLote = Lists.newArrayList();
                BigDecimal totalHonorarios = BigDecimal.ZERO;
                BigDecimal lancadoHonorarios = BigDecimal.ZERO;
                Map<MapaDeContas, ContaReceita> mapaDeContas = Maps.newHashMap();
                if (!loteBaixa.getItemLoteBaixa().isEmpty()) {
                    for (ItemLoteBaixa item : loteBaixa.getItemLoteBaixa()) {
                        if (item.getDam() != null) {
                            totalHonorarios = totalHonorarios.add(item.getDam().getHonorarios());
                            BigDecimal totalDam = BigDecimal.ZERO;
                            DAM dam = damFacade.recuperar(item.getDam().getId());
                            for (ItemDAM itemDam : dam.getItens()) {
                                for (TributoDAM tributoDAM : itemDam.getTributoDAMs()) {
                                    totalDam = totalDam.add(tributoDAM.getValorOriginal());
                                }
                            }

                            for (ItemDAM itemDam : dam.getItens()) {
                                for (TributoDAM tributoDAM : itemDam.getTributoDAMs()) {

                                    if (dam.getTotalAcrescimosSemHonorarios().compareTo(BigDecimal.ZERO) > 0) {

                                        if (dam.getHonorarios().compareTo(BigDecimal.ZERO) > 0) {

                                            BigDecimal proporcaoHonorarios = (tributoDAM.getValorOriginal().multiply(CEM))
                                                .divide(totalDam, 6, RoundingMode.HALF_UP);
                                            BigDecimal valorHonorarios = (proporcaoHonorarios.multiply(dam.getHonorarios()))
                                                .divide(CEM, 6, RoundingMode.HALF_UP);

                                            valorHonorarios = valorHonorarios.setScale(2, RoundingMode.HALF_UP);
                                            lancadoHonorarios = lancadoHonorarios.add(valorHonorarios);

                                            MapaArrecadacao mapa = new MapaArrecadacao();
                                            ContaReceita contaReceita;
                                            MapaDeContas contas = new MapaDeContas(tributoDAM.getTributo().getId(),
                                                dam.getExercicio().getAno(),
                                                ContaTributoReceita.TipoContaReceita.EXERCICIO,
                                                itemDam.getParcela().getDividaAtiva());


                                            if (!mapaDeContas.containsKey(contas)) {
                                                contaReceita = enquadramentoTributoExercicioFacade
                                                    .contasDoTributo(tributoDAM.getTributo(),
                                                        loteBaixa.getDataPagamento(),
                                                        ContaTributoReceita.TipoContaReceita.EXERCICIO,
                                                        itemDam.getParcela().getDividaAtiva());
                                                mapaDeContas.put(contas, contaReceita);
                                            } else {
                                                contaReceita = mapaDeContas.get(contas);
                                            }
                                            mapa.setConta(contaReceita);
                                            mapa.setTributo(tributoDAM.getTributo());
                                            mapa.setValor(valorHonorarios);

                                            if (filtro != null && filtro.getAgrupaPorData()) {
                                                mapa.setPagamento(loteBaixa.getDataPagamento());
                                            }
                                            if (filtro != null && filtro.getAgrupaPorAgenteArrecadador()) {
                                                mapa.setAgenteArrecadador(loteBaixa.montabancoConta());
                                            }

                                            if (listaAcrescimosLote.contains(mapa)) {
                                                MapaArrecadacao daLista = listaAcrescimosLote.get(listaAcrescimosLote.indexOf(mapa));
                                                listaAcrescimosLote.remove(daLista);
                                                mapa.setValor(daLista.getValor().add(mapa.getValor()).setScale(6, RoundingMode.HALF_UP));
                                            }
                                            if (podeAdicionarConta(filtro, mapa.getConta())) {
                                                listaAcrescimosLote.add(mapa);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (totalHonorarios.compareTo(lancadoHonorarios) != 0 && !listaAcrescimosLote.isEmpty()) {
                        BigDecimal diferenca = totalHonorarios.subtract(lancadoHonorarios);
                        BigDecimal valor = listaAcrescimosLote.get(0).getValor().add(diferenca);
                        listaAcrescimosLote.get(0).setValor(valor);
                    }
                    for (MapaArrecadacao acrescimo : listaAcrescimosLote) {
                        if (listaAcrescimos.contains(acrescimo)) {
                            MapaArrecadacao daLista = listaAcrescimos.get(listaAcrescimos.indexOf(acrescimo));
                            listaAcrescimos.remove(daLista);
                            acrescimo.setValor(daLista.getValor().add(acrescimo.getValor()).setScale(6, RoundingMode.HALF_UP));
                        }
                        listaAcrescimos.add(acrescimo);
                    }
                }
            }
            Collections.sort(listaAcrescimos);
            ImprimeRelatoriosArrecadacao imprime = new ImprimeRelatoriosArrecadacao();
            imprime.imprimeMapaArrecadacao(lotes, filtro, arquivo, new ArrayList<MapaArrecadacao>(), listaAcrescimos, new ArrayList<MapaArrecadacao>(), "MapaArrecadacaoHonorarios.jasper");

        } catch (Exception e) {
            logger.error("Erro ao geraImprimeMapaHonorarios: {}", e);
        }
    }

    private boolean podeAdicionarConta(RelatorioMapaArrecadacaoConsolidadoControlador.FiltroRelatorioMapaConsolidado filtro, ContaReceita conta) {
        return filtro == null
            || filtro.getContasReceita() == null
            || filtro.getContasReceita().isEmpty()
            || filtro.getContasReceita().contains(conta);
    }

    public boolean podeApagarIntegracao(LoteBaixa lote) {
        String sql = "select lbi.id from LoteBaixaIntegracao lbi\n" +
            "inner join itemintegracaotribcont iitc on iitc.id = lbi.itemitengracao_id\n" +
            "inner join integracaotribcont itc on itc.item_id = iitc.id\n" +
            "inner join loteintegracaotribcont litc on litc.id = itc.lote_id " +
            "where lbi.lotebaixa_id = :lote" +
            "  and iitc.tipo = :tipoIntegracao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("lote", lote.getId());
        if (lote.isEstornado()) {
            q.setParameter("tipoIntegracao", TipoIntegracao.ESTORNO_ARRECADACAO.name());
        } else {
            q.setParameter("tipoIntegracao", TipoIntegracao.ARRECADACAO.name());
        }
        return q.getResultList().isEmpty();
    }

    public String retornaNumeroArquivoLote(LoteBaixa loteBaixa) {
        if (loteBaixa != null && loteBaixa.getId() != null) {
            String sql = "select arq.* from LoteBaixaDoArquivo arq where arq.loteBaixa_id = :idLoteBaixa";
            Query q = em.createNativeQuery(sql, LoteBaixaDoArquivo.class);
            q.setParameter("idLoteBaixa", loteBaixa.getId());
            List<LoteBaixaDoArquivo> retorno = q.getResultList();
            if (!retorno.isEmpty()) {
                return retorno.get(0).getNumero() + "";
            }
        }
        return "";
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<Exception> verificaContasDoLote(LoteBaixa loteBaixa) {
        Map<Tributo, List<DAM>> tributosInconsistentesPorDam = Maps.newHashMap();

        service = (ServiceIntegracaoTributarioContabil) ContextLoader.getCurrentWebApplicationContext()
            .getBean("serviceIntegracaoTributarioContabil");

        Map<Tributo, ContaTributoReceita> mapa = Maps.newHashMap();

        if (loteBaixa.getId() != null) {
            loteBaixa = em.find(LoteBaixa.class, loteBaixa.getId());
        }
        for (ItemLoteBaixa itemLoteBaixa : loteBaixa.getItemLoteBaixa()) {
            if (itemLoteBaixa.getDam() != null) {
                BigDecimal totalDam = BigDecimal.ZERO;
                DAM dam = em.find(DAM.class, itemLoteBaixa.getDam().getId());
                for (ItemDAM itemDam : dam.getItens()) {
                    for (TributoDAM tributoDAM : itemDam.getTributoDAMs()) {
                        totalDam = totalDam.add(tributoDAM.getValorComDesconto());
                    }
                }
                for (ItemDAM itemDam : dam.getItens()) {
                    for (TributoDAM tributoDAM : itemDam.getTributoDAMs()) {

                        BigDecimal valorAcrescimos = BigDecimal.ZERO;
                        if (dam.getTotalAcrescimos().compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal proporcaoAcrescimos = (tributoDAM.getValorComDesconto().multiply(CEM)).divide(totalDam, 6, RoundingMode.HALF_UP);
                            valorAcrescimos = (proporcaoAcrescimos.multiply(dam.getTotalAcrescimos())).divide(CEM, 6, RoundingMode.HALF_UP);

                        }
                        BigDecimal valorDesconto = BigDecimal.ZERO;
                        if (dam.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal proporcaoDesconto = (tributoDAM.getValorOriginal().multiply(CEM)).divide(totalDam, 6, RoundingMode.HALF_UP);
                            valorDesconto = (proporcaoDesconto.multiply(dam.getDesconto())).divide(CEM, 6, RoundingMode.HALF_UP);
                        }
                        try {
                            if (!mapa.containsKey(tributoDAM.getTributo())) {
                                ContaTributoReceita contas = service.getContaTributoReceitaPorTributoExercicio(tributoDAM.getTributo().getId(), loteBaixa.getDataPagamento());
                                mapa.put(tributoDAM.getTributo(), contas);
                            }
                            verificarConta(itemDam.getParcela(), tributoDAM.getTributo(), valorDesconto, mapa.get(tributoDAM.getTributo()), dam.getExercicio());
                        } catch (ContaReceitaException e) {
                            adicionarTributoInconsistente(tributosInconsistentesPorDam, tributoDAM);
                        }
                    }

                }
            }
        }
        int ano = Calendar.getInstance().get(Calendar.YEAR);
        List<Exception> excecoes = Lists.newArrayList();
        for (Tributo tributo : tributosInconsistentesPorDam.keySet()) {
            excecoes.add(criarContaReceitaException(tributo, ano, tributosInconsistentesPorDam.get(tributo)));
        }
        return excecoes;
    }

    public ContaReceitaException criarContaReceitaException(Tributo tributo, Integer exercicio, List<DAM> dams) {
        String msg = "O tributo " + tributo.getCodigo() + " - " + tributo.getDescricao() + " não está devidamente configurado. Insira as contas de receita corretas para o exercício " + exercicio;
        if (dams != null && !dams.isEmpty()) {
            List<String> numerosDAM = Lists.newArrayList();
            for (DAM dam : dams) {
                numerosDAM.add(dam.getNumeroDAM());
            }
            msg += " DAM's: " + StringUtils.join(numerosDAM, ", ");
        }
        return new ContaReceitaException(msg);
    }

    private void adicionarTributoInconsistente(Map<Tributo, List<DAM>> tributosInconsistentesPorDam, TributoDAM tributoDAM) {
        if (tributosInconsistentesPorDam.get(tributoDAM.getTributo()) == null) {
            tributosInconsistentesPorDam.put(tributoDAM.getTributo(), Lists.<DAM>newArrayList());
        }
        tributosInconsistentesPorDam.get(tributoDAM.getTributo()).add(tributoDAM.getItemDAM().getDAM());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private void verificarConta(ParcelaValorDivida parcela, Tributo tributo, BigDecimal descontoDebitar, ContaTributoReceita contaTributo, Exercicio ex) throws ContaReceitaException {
        if (tributo.getTipoTributo().isImpostoTaxa()) {
            if (tributo.getTributoJuros() == null || tributo.getTributoMulta() == null || tributo.getTributoCorrecaoMonetaria() == null || tributo.getTributoHonorarios() == null) {
                throw new ContaReceitaException(tributo, ex.getAno());
            }
        }
        ContaReceita contaArrecadacao = contaTributo.getContaExercicio();
        ContaReceita contaDeducao = contaTributo.getContaDescontoExercicio();
        if (parcela.getDividaAtiva()) {
            contaArrecadacao = contaTributo.getContaDividaAtiva();
            contaDeducao = contaTributo.getContaDescontoDividaAtiva();
        }
        if (contaArrecadacao == null) {
            throw new ContaReceitaException();
        }
        if (descontoDebitar.compareTo(BigDecimal.ZERO) > 0) {
            if (contaDeducao == null) {
                throw new ContaReceitaException();
            }
        }
    }

    public LoteBaixaDoArquivo arquivoDoLote(LoteBaixa loteBaixa) {
        String hql = "select a from LoteBaixaDoArquivo a where a.loteBaixa = :lote";
        List<LoteBaixaDoArquivo> arquivos = em.createQuery(hql).setParameter("lote", loteBaixa).getResultList();
        if (!arquivos.isEmpty()) {
            return arquivos.get(0);
        }
        return null;
    }

    public List<LoteBaixaDoArquivo> buscarLoteBaixaDoArquivo(LoteBaixa loteBaixa) {
        String hql = "select a from LoteBaixaDoArquivo a where a.loteBaixa = :lote";
        List<LoteBaixaDoArquivo> arquivos = em.createQuery(hql).setParameter("lote", loteBaixa).getResultList();
        return arquivos;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<LoteBaixa> buscarItensLotesPorIntervaloEContaESituacao(Date dataInicio,
                                                                       Date dataFinal,
                                                                       List<SubConta> subContas,
                                                                       List<String> situacoes) {

        List<String> idsSubContas = Lists.newArrayList();
        for (SubConta subConta : subContas) {
            idsSubContas.add(subConta.getId() + "");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("select lb.* ");
        sql.append("from LoteBaixa lb ");
        sql.append("where (lb.datapagamento >= :dataInicio and lb.datapagamento <= :dataFinal) ");
        sql.append("and lb.situacaolotebaixa in (:situacoes) ");
        if (!idsSubContas.isEmpty()) {
            sql.append("and lb.subconta_id in (:subContas) ");
        }
        sql.append(" order by lb.datapagamento asc");

        Query q = em.createNativeQuery(sql.toString(), LoteBaixa.class);
        q.setParameter("dataInicio", dataInicio);
        q.setParameter("dataFinal", dataFinal);
        q.setParameter("situacoes", situacoes);
        if (!idsSubContas.isEmpty()) {
            q.setParameter("subContas", subContas);
        }
        List<LoteBaixa> toReturn = q.getResultList();
        return toReturn;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<VOItemLoteBaixa> buscarItensLoteBaixa(Date dataPagamentoInicial,
                                                      Date dataPagamentoFinal,
                                                      List<String> situacoesLoteBaixa,
                                                      Exercicio exercicio,
                                                      List<SubConta> subContas) {

        String sql = "select distinct lb.id as idLote,\n" +
            "       ilb.id as idItem,\n" +
            "       ilb.dam_id,\n" +
            "       ilb.valorpago,\n" +
            "       dam.valororiginal as valorOriginalDam,\n" +
            "       dam.desconto as descontoDam,\n" +
            "       dam.juros as jurosDam,\n" +
            "       dam.multa as multaDam,\n" +
            "       dam.correcaomonetaria as correcaoDam,\n" +
            "       dam.honorarios as honorariosDam,\n" +
            "       lb.datapagamento,\n" +
            "       contabancaria.numeroconta || '-' || contabancaria.digitoverificador as conta,\n" +
            "       banco.numerobanco || ' - ' || banco.descricao as banco,\n" +
            "       banco.id as idBanco,\n" +
            "       lb.codigoLote as codigoLote,\n" +
            "       lb.dataFinanciamento as dataContabilizacao\n" +
            "from itemlotebaixa ilb\n" +
            "inner join dam dam on dam.id = ilb.dam_id\n" +
            "inner join itemdam idam on idam.dam_id = ilb.dam_id\n" +
            "inner join parcelavalordivida pvd on pvd.id = idam.parcela_id\n" +
            "inner join valordivida vd on vd.id = pvd.valordivida_id\n" +
            "inner join lotebaixa lb on lb.id = ilb.lotebaixa_id\n" +
            "inner join subconta sconta on sconta.id = lb.subconta_id\n" +
            "inner join contabancariaentidade contabancaria on contabancaria.id = sconta.contabancariaentidade_id\n" +
            "inner join agencia ag on ag.id = contabancaria.agencia_id\n" +
            "inner join banco banco on banco.id = ag.banco_id\n" +
            " where (lb.datapagamento >= :dataInicio and lb.datapagamento <= :dataFinal) " +
            "   and lb.situacaolotebaixa in (:situacoes) ";

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("dataInicio", dataPagamentoInicial);
        parameters.put("dataFinal", dataPagamentoFinal);
        parameters.put("situacoes", situacoesLoteBaixa);
        if (exercicio != null && exercicio.getId() != null) {
            sql += " and vd.exercicio_id = :exercicio_id ";
            parameters.put("exercicio_id", exercicio.getId());
        }
        if (subContas != null && subContas.size() > 0) {
            sql += " and lb.subconta_id in (:subContas)  ";
            List<Long> idsSubConta = Lists.newArrayList();
            for (SubConta subConta : subContas) {
                idsSubConta.add(subConta.getId());
            }
            parameters.put("subContas", idsSubConta);
        }

        Query q = em.createNativeQuery(sql);
        for (String parameter : parameters.keySet()) {
            q.setParameter(parameter, parameters.get(parameter));
        }
        List<Object[]> lista = q.getResultList();
        List<VOItemLoteBaixa> itens = Lists.newArrayList();
        for (Object[] obj : lista) {
            VOItemLoteBaixa item = new VOItemLoteBaixa();
            item.setIdLoteBaixa(((BigDecimal) obj[0]).longValue());
            item.setId(((BigDecimal) obj[1]).longValue());
            item.setIdDam(obj[2] != null ? ((BigDecimal) obj[2]).longValue() : null);
            item.setValorPago(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
            item.setValorOriginalDam(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
            item.setDescontoDam(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
            item.setJurosDam(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
            item.setMultaDam(obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO);
            item.setCorrecaoDam(obj[8] != null ? (BigDecimal) obj[8] : BigDecimal.ZERO);
            item.setHonorariosDam(obj[9] != null ? (BigDecimal) obj[9] : BigDecimal.ZERO);
            item.setDataPagamento(obj[10] != null ? (Date) obj[10] : new Date());
            item.setConta(obj[11] != null ? (String) obj[11] : "");
            item.setBanco(obj[12] != null ? (String) obj[12] : "");
            item.setIdBanco(obj[13] != null ? ((BigDecimal) obj[13]).longValue() : 0L);
            item.setCodigoLote(obj[14] != null ? (String) obj[14] : "");
            item.setDataContabilizacao(obj[15] != null ? (Date) obj[15] : new Date());
            itens.add(item);
        }
        return itens;
    }

    public List<RegistroDAF607> buscarRegistroDAF607(ArquivoDAF607 arquivo) {
        String sql = " select registro.* from RegistroDAF607 registro where arquivodaf607_id = :arquivo";
        Query q = em.createNativeQuery(sql, RegistroDAF607.class);
        q.setParameter("arquivo", arquivo.getId());
        return q.getResultList();
    }

    public void corrigirDAMsParcelamento(List<DAM> dams) {
        for (DAM dam : dams) {
            DAM corrigir = damFacade.recuperar(dam.getId());
            if (corrigir != null && corrigir.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                for (ItemDAM itemDAM : corrigir.getItens()) {
                    boolean isParcelamento = itemDAM.getParcela().getValorDivida().getDivida().getIsParcelamento();
                    ProcessoParcelamento parcelamento = null;
                    if (isParcelamento) {
                        VOProcessoParcelamento vo = processoParcelamentoFacade.recuperarProcessoParcelamento(itemDAM.getParcela().getId());
                        parcelamento = processoParcelamentoFacade.recuperar(vo.getId());
                    }
                    corrigirDescontoTributosDAM(itemDAM, itemDAM.getDesconto(), parcelamento, isParcelamento);
                    corrigirDescontoItemParcela(itemDAM.getParcela(), itemDAM.getTributoDAMs(), parcelamento);
                }
            }
        }
    }

    public void corrigirDAMsPorIds(List<Long> idsDams) {
        for (Long idDam : idsDams) {
            DAM corrigir = em.find(DAM.class, idDam);
            if (corrigir != null) {
                if (corrigir.getDesconto().compareTo(BigDecimal.ZERO) > 0 && DAM.Situacao.ABERTO.equals(corrigir.getSituacao())) {
                    for (ItemDAM itemDAM : corrigir.getItens()) {
                        boolean isParcelamento = itemDAM.getParcela().getValorDivida().getDivida().getIsParcelamento();
                        ProcessoParcelamento parcelamento = null;
                        if (isParcelamento) {
                            VOProcessoParcelamento vo = processoParcelamentoFacade.recuperarProcessoParcelamento(itemDAM.getParcela().getId());
                            parcelamento = processoParcelamentoFacade.recuperar(vo.getId());
                        }
                        corrigirDescontoTributosDAM(itemDAM, itemDAM.getDesconto(), parcelamento, isParcelamento);
                        corrigirDescontoItemParcela(itemDAM.getParcela(), itemDAM.getTributoDAMs(), parcelamento);
                    }
                }
            }
        }
    }

    private void corrigirDescontoItemParcela(ParcelaValorDivida parcelaValorDivida, List<TributoDAM> tributosDAM, ProcessoParcelamento parcelamento) {
        if (parcelamento != null) {
            for (ItemParcelaValorDivida itemParcelaValorDivida : parcelaValorDivida.getItensParcelaValorDivida()) {
                itemParcelaValorDivida.getDescontos().clear();
                for (TributoDAM tributoDAM : tributosDAM) {
                    if (tributoDAM.getDesconto().compareTo(BigDecimal.ZERO) > 0 && itemParcelaValorDivida.getTributo().equals(tributoDAM.getTributo())) {
                        DescontoItemParcela descontoItemParcela = new DescontoItemParcela();
                        descontoItemParcela.setItemParcelaValorDivida(itemParcelaValorDivida);
                        descontoItemParcela.setDesconto(tributoDAM.getDesconto());
                        descontoItemParcela.setTipo(DescontoItemParcela.Tipo.VALOR);
                        descontoItemParcela.setInicio(parcelaValorDivida.getDataRegistro());
                        descontoItemParcela.setFim(parcelaValorDivida.getVencimento());
                        descontoItemParcela.setOrigem(DescontoItemParcela.Origem.PARCELAMENTO);
                        itemParcelaValorDivida.getDescontos().add(descontoItemParcela);
                        em.merge(itemParcelaValorDivida);
                    }
                }
            }
        }
    }

    private void corrigirDescontoTributosDAM(ItemDAM itemDAM, BigDecimal valorDesconto, ProcessoParcelamento parcelamento, boolean isParcelamento) {
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (TributoDAM tributoDAM : itemDAM.getTributoDAMs()) {
            if ((isParcelamento && parcelamento != null && incideDescontoNoParcelamento(tributoDAM.getTributo().getTipoTributo(), parcelamento)) || !isParcelamento) {
                valorTotal = valorTotal.add(tributoDAM.getValorOriginal());
            }
        }

        for (TributoDAM tributoDAM : itemDAM.getTributoDAMs()) {
            if ((isParcelamento && parcelamento != null && incideDescontoNoParcelamento(tributoDAM.getTributo().getTipoTributo(), parcelamento)) || !isParcelamento) {
                tributoDAM.setDesconto((tributoDAM.getValorOriginal().multiply(valorDesconto)).divide(valorTotal, 8, RoundingMode.HALF_UP));
            } else {
                tributoDAM.setDesconto(BigDecimal.ZERO);
            }
            em.merge(tributoDAM);
        }
    }

    public void corrigirTributosDAM(DAM dam) {
        logger.debug("Corrigindo o DAM " + dam.getNumeroCompleto());
        for (ItemDAM itemDAM : dam.getItens()) {
            BigDecimal original = itemDAM.getValorOriginalDevido();
            BigDecimal juros = itemDAM.getJuros();
            BigDecimal multa = itemDAM.getMulta();
            BigDecimal correcao = itemDAM.getCorrecaoMonetaria();
            BigDecimal honorarios = itemDAM.getHonorarios();

            BigDecimal totalOriginal = BigDecimal.ZERO;
            BigDecimal totalJuros = BigDecimal.ZERO;
            BigDecimal totalMulta = BigDecimal.ZERO;
            BigDecimal totalCorrecao = BigDecimal.ZERO;
            BigDecimal totalHonorarios = BigDecimal.ZERO;

            List<TributoDAM> tributoDAMS = buscarTributosDamDoItem(itemDAM.getId());
            if (!tributoDAMS.isEmpty()) {
                for (TributoDAM tributoDAM : tributoDAMS) {
                    if (tributoDAM.getTributo().getTipoTributo().isImpostoTaxa()) {
                        totalOriginal = totalOriginal.add(tributoDAM.getValorOriginal());
                    }
                    if (Tributo.TipoTributo.JUROS.equals(tributoDAM.getTributo().getTipoTributo())) {
                        totalJuros = totalJuros.add(tributoDAM.getValorOriginal());
                    }
                    if (Tributo.TipoTributo.MULTA.equals(tributoDAM.getTributo().getTipoTributo())) {
                        totalMulta = totalMulta.add(tributoDAM.getValorOriginal());
                    }
                    if (Tributo.TipoTributo.CORRECAO.equals(tributoDAM.getTributo().getTipoTributo())) {
                        totalCorrecao = totalCorrecao.add(tributoDAM.getValorOriginal());
                    }
                    if (Tributo.TipoTributo.HONORARIOS.equals(tributoDAM.getTributo().getTipoTributo())) {
                        totalHonorarios = totalHonorarios.add(tributoDAM.getValorOriginal());
                    }
                }

                TributoDAM tributoDAMOriginal = null;
                for (TributoDAM tributoDAM : tributoDAMS) {
                    BigDecimal valor = BigDecimal.ZERO;
                    boolean alterou = false;

                    if (tributoDAM.getTributo().getTipoTributo().isImpostoTaxa()) {
                        tributoDAMOriginal = tributoDAM;
                    }

                    if (tributoDAM.getTributo().getTipoTributo().isImpostoTaxa() && totalOriginal.compareTo(original) != 0) {
                        valor = tributoDAM.getValorOriginal().multiply(original).divide(totalOriginal, 6, RoundingMode.HALF_UP);
                        tributoDAM.setValorOriginal(valor.setScale(2, RoundingMode.HALF_UP));
                        alterou = true;
                    }
                    if (Tributo.TipoTributo.JUROS.equals(tributoDAM.getTributo().getTipoTributo()) && totalJuros.compareTo(juros) != 0) {
                        valor = tributoDAM.getValorOriginal().multiply(juros).divide(totalJuros, 6, RoundingMode.HALF_UP);
                        tributoDAM.setValorOriginal(valor.setScale(2, RoundingMode.HALF_UP));
                        alterou = true;
                    }
                    if (Tributo.TipoTributo.MULTA.equals(tributoDAM.getTributo().getTipoTributo()) && totalMulta.compareTo(multa) != 0) {
                        valor = tributoDAM.getValorOriginal().multiply(multa).divide(totalMulta, 6, RoundingMode.HALF_UP);
                        tributoDAM.setValorOriginal(valor.setScale(2, RoundingMode.HALF_UP));
                        alterou = true;
                    }
                    if (Tributo.TipoTributo.CORRECAO.equals(tributoDAM.getTributo().getTipoTributo()) && totalCorrecao.compareTo(correcao) != 0) {
                        valor = tributoDAM.getValorOriginal().multiply(correcao).divide(totalCorrecao, 6, RoundingMode.HALF_UP);
                        tributoDAM.setValorOriginal(valor.setScale(2, RoundingMode.HALF_UP));
                        alterou = true;
                    }
                    if (Tributo.TipoTributo.HONORARIOS.equals(tributoDAM.getTributo().getTipoTributo()) && totalHonorarios.compareTo(honorarios) != 0) {
                        valor = tributoDAM.getValorOriginal().multiply(honorarios).divide(totalHonorarios, 6, RoundingMode.HALF_UP);
                        tributoDAM.setValorOriginal(valor.setScale(2, RoundingMode.HALF_UP));
                        alterou = true;
                    }

                    if (alterou) {
                        em.merge(tributoDAM);
                    }
                }

                totalOriginal = BigDecimal.ZERO;
                totalJuros = BigDecimal.ZERO;
                totalMulta = BigDecimal.ZERO;
                totalCorrecao = BigDecimal.ZERO;
                totalHonorarios = BigDecimal.ZERO;

                for (TributoDAM tributoDAM : tributoDAMS) {
                    if (tributoDAM.getTributo().getTipoTributo().isImpostoTaxa()) {
                        totalOriginal = totalOriginal.add(tributoDAM.getValorOriginal());
                    }
                    if (Tributo.TipoTributo.JUROS.equals(tributoDAM.getTributo().getTipoTributo())) {
                        totalJuros = totalJuros.add(tributoDAM.getValorOriginal());
                    }
                    if (Tributo.TipoTributo.MULTA.equals(tributoDAM.getTributo().getTipoTributo())) {
                        totalMulta = totalMulta.add(tributoDAM.getValorOriginal());
                    }
                    if (Tributo.TipoTributo.CORRECAO.equals(tributoDAM.getTributo().getTipoTributo())) {
                        totalCorrecao = totalCorrecao.add(tributoDAM.getValorOriginal());
                    }
                    if (Tributo.TipoTributo.HONORARIOS.equals(tributoDAM.getTributo().getTipoTributo())) {
                        totalHonorarios = totalHonorarios.add(tributoDAM.getValorOriginal());
                    }
                }

                if (tributoDAMOriginal != null) {
                    if (totalJuros.compareTo(BigDecimal.ZERO) == 0 && juros.compareTo(BigDecimal.ZERO) > 0) {
                        TributoDAM td = new TributoDAM();
                        td.setItemDAM(itemDAM);
                        td.setTributo(tributoDAMOriginal.getTributo().getTributoJuros());
                        td.setValorOriginal(juros);
                        tributoDAMS.add(td);

                        em.persist(td);
                    }
                    if (totalMulta.compareTo(BigDecimal.ZERO) == 0 && multa.compareTo(BigDecimal.ZERO) > 0) {
                        TributoDAM td = new TributoDAM();
                        td.setItemDAM(itemDAM);
                        td.setTributo(tributoDAMOriginal.getTributo().getTributoMulta());
                        td.setValorOriginal(multa);
                        tributoDAMS.add(td);

                        em.persist(td);
                    }
                    if (totalCorrecao.compareTo(BigDecimal.ZERO) == 0 && correcao.compareTo(BigDecimal.ZERO) > 0) {
                        TributoDAM td = new TributoDAM();
                        td.setItemDAM(itemDAM);
                        td.setTributo(tributoDAMOriginal.getTributo().getTributoCorrecaoMonetaria());
                        td.setValorOriginal(correcao);
                        tributoDAMS.add(td);

                        em.persist(td);
                    }
                    if (totalHonorarios.compareTo(BigDecimal.ZERO) == 0 && honorarios.compareTo(BigDecimal.ZERO) > 0) {
                        TributoDAM td = new TributoDAM();
                        td.setItemDAM(itemDAM);
                        td.setTributo(tributoDAMOriginal.getTributo().getTributoHonorarios());
                        td.setValorOriginal(honorarios);
                        tributoDAMS.add(td);

                        em.persist(td);
                    }
                }
            }

            if (totalOriginal.compareTo(original) != 0 && original.compareTo(BigDecimal.ZERO) > 0) {
                logger.debug("Deveria mas nao arrumou o tributo original no valor de: " + original + " ficou com " + totalOriginal);
            }
            if (totalJuros.compareTo(juros) != 0 && juros.compareTo(BigDecimal.ZERO) > 0) {
                logger.debug("Deveria mas nao arrumou o tributo juros no valor de: " + juros + " ficou com " + totalJuros);
            }
            if (totalMulta.compareTo(multa) != 0 && multa.compareTo(BigDecimal.ZERO) > 0) {
                logger.debug("Deveria mas nao arrumou o tributo multa no valor de: " + multa + " ficou com " + totalMulta);
            }
            if (totalCorrecao.compareTo(correcao) != 0 && correcao.compareTo(BigDecimal.ZERO) > 0) {
                logger.debug("Deveria mas nao arrumou o tributo correcao no valor de: " + correcao + " ficou com " + totalCorrecao);
            }
            if (totalHonorarios.compareTo(honorarios) != 0 && honorarios.compareTo(BigDecimal.ZERO) > 0) {
                logger.debug("Deveria mas nao arrumou o tributo honorarios no valor de: " + honorarios + " ficou com " + totalHonorarios);
            }
        }
    }

    private boolean incideDescontoNoParcelamento(Tributo.TipoTributo tipoTributo, ProcessoParcelamento parcelamento) {
        if (Tributo.TipoTributo.IMPOSTO.equals(tipoTributo) && parcelamento.getValorDescontoImposto().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        if (Tributo.TipoTributo.TAXA.equals(tipoTributo) && parcelamento.getValorDescontoTaxa().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        if (Tributo.TipoTributo.JUROS.equals(tipoTributo) && parcelamento.getValorDescontoJuros().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        if (Tributo.TipoTributo.MULTA.equals(tipoTributo) && parcelamento.getValorDescontoMulta().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        if (Tributo.TipoTributo.CORRECAO.equals(tipoTributo) && parcelamento.getValorDescontoCorrecao().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        if (Tributo.TipoTributo.HONORARIOS.equals(tipoTributo) && parcelamento.getValorDescontoHonorarios().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        return false;
    }

    public List<ItemInconsistenciaIntegracaoTribCont> buscarIntegracoesNaContabilidade(LoteBaixa loteBaixa) {
        String sql = "select contaReceita, operacao, unidade, datalancamento, dataconciliacao, sum(valor) from (\n" +
            "select distinct lanc.id, lanc.datalancamento as datalancamento, \n" +
            "       lanc.dataconciliacao as dataconciliacao, \n" +
            "       c.id as contaReceita,\n" +
            "       lanc.operacaoreceitarealizada as operacao, \n" +
            "       lanc.unidadeorganizacionaladm_id as unidade,\n" +
            "       lanc.valor as valor \n" +
            "from lancamentoreceitaorc lanc \n" +
            "inner join receitaloa rec on rec.id = lanc.receitaloa_id \n" +
            "inner join conta c on c.id = rec.contadereceita_id \n" +
            "inner join contareceita cr on cr.id = c.id \n" +
            "inner join subconta sub on sub.id = lanc.subconta_id \n" +
            "inner join loteintegracaotribcont integrador on integrador.id = lanc.integracaotribcont_id \n" +
            "where integrador.lotebaixa_id = :idLoteBaixa\n" +
            "union\n" +
            "select distinct estorno.id, estorno.dataestorno as datalancamento, \n" +
            "       estorno.dataconciliacao as dataconciliacao, \n" +
            "       c.id as contaReceita, \n" +
            "       estorno.operacaoreceitarealizada as operacao, \n" +
            "       estorno.unidadeorganizacionaladm as unidade,\n" +
            "       (estorno.valor * -1) as valor \n" +
            "from receitaorcestorno estorno\n" +
            "inner join receitaloa rec on rec.id = estorno.receitaloa_id\n" +
            "inner join conta c on c.id = rec.contadereceita_id \n" +
            "inner join contareceita cr on cr.id = c.id \n" +
            "inner join subconta sub on sub.id = estorno.contafinanceira_id\n" +
            "inner join loteintegracaotribcont integrador on integrador.id = estorno.integracaotribcont_id\n" +
            "where integrador.lotebaixa_id = :idLoteBaixa)\n" +
            "group by contaReceita, operacao, unidade, datalancamento, dataconciliacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteBaixa", loteBaixa.getId());
        List<Object[]> lista = q.getResultList();
        List<ItemInconsistenciaIntegracaoTribCont> integracoes = Lists.newArrayList();
        for (Object[] obj : lista) {
            ItemInconsistenciaIntegracaoTribCont item = new ItemInconsistenciaIntegracaoTribCont();
            item.setContaReceita(obj[0] != null ? (ContaReceita) contaFacade.recuperar(((BigDecimal) obj[0]).longValue()) : null);
            item.setOperacaoReceitaRealizada(obj[1] != null ? OperacaoReceita.valueOf((String) obj[1]) : null);
            item.setTipo(TipoIntegracao.ARRECADACAO);
            item.setEntidade(obj[2] != null ? entidadeFacade.recuperarEntidadePorUnidadeOrganizacional(((BigDecimal) obj[2]).longValue()) : null);
            item.setDataPagamento(obj[3] != null ? (Date) obj[3] : loteBaixa.getDataPagamento());
            item.setDataCredito(obj[4] != null ? (Date) obj[4] : loteBaixa.getDataFinanciamento());
            item.setValorContabil(obj[5] != null ? (BigDecimal) obj[5] : null);
            item.setOperacaoIntegracao(null);
            integracoes.add(item);
        }
        return integracoes;
    }

    public List<ItemInconsistenciaIntegracaoTribCont> buscarIntegracoesCreditoReceber(LoteBaixa loteBaixa) {
        String sql = "select distinct cr.id, cr.DATACREDITO as DATACREDITO, \n" +
            "       c.id as contaReceita,\n" +
            "       cr.OPERACAOCREDITORECEBER as operacao, \n" +
            "       cr.unidadeorganizacionaladm_id as unidade,\n" +
            "       case when cr.TIPOLANCAMENTO = 'NORMAL' then cr.valor else cr.valor * -1 end as valor\n" +
            "from creditoreceber cr\n" +
            "inner join receitaloa rec on rec.id = cr.receitaloa_id \n" +
            "inner join conta c on c.id = rec.contadereceita_id \n" +
            "where cr.lotebaixa_id = :idLoteBaixa";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteBaixa", loteBaixa.getId());
        List<Object[]> lista = q.getResultList();
        List<ItemInconsistenciaIntegracaoTribCont> integracoes = Lists.newArrayList();
        for (Object[] obj : lista) {
            ItemInconsistenciaIntegracaoTribCont item = new ItemInconsistenciaIntegracaoTribCont();
            item.setContaReceita(obj[2] != null ? (ContaReceita) contaFacade.recuperar(((BigDecimal) obj[2]).longValue()) : null);
            item.setOperacaoReceitaRealizada(OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA);
            item.setTipo(TipoIntegracao.ARRECADACAO);
            item.setEntidade(obj[4] != null ? entidadeFacade.recuperarEntidadePorUnidadeOrganizacional(((BigDecimal) obj[4]).longValue()) : null);
            item.setDataPagamento(loteBaixa.getDataPagamento());
            item.setDataCredito(obj[1] != null ? (Date) obj[1] : loteBaixa.getDataFinanciamento());
            item.setOperacaoIntegracao(OperacaoIntegracaoTribCont.MULTA_JUROS_CORRECAO);
            item.setValorContabil(obj[5] != null ? (BigDecimal) obj[5] : null);
            integracoes.add(item);
        }
        return integracoes;
    }

    public List<ItemInconsistenciaIntegracaoTribCont> buscarIntegracoesDividaAtiva(LoteBaixa loteBaixa) {
        String sql = "select distinct da.id, da.datadivida as DATACREDITO, \n" +
            "       c.id as contaReceita,\n" +
            "       da.operacaodividaativa as operacao, \n" +
            "       da.unidadeorganizacionaladm_id as unidade,\n" +
            "       case when da.TIPOLANCAMENTO = 'NORMAL' then da.valor else da.valor * -1 end as valor\n" +
            "from dividaativacontabil da\n" +
            "inner join receitaloa rec on rec.id = da.receitaloa_id \n" +
            "inner join conta c on c.id = rec.contadereceita_id \n" +
            "where da.lotebaixa_id = :idLoteBaixa";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteBaixa", loteBaixa.getId());
        List<Object[]> lista = q.getResultList();
        List<ItemInconsistenciaIntegracaoTribCont> integracoes = Lists.newArrayList();
        for (Object[] obj : lista) {
            ItemInconsistenciaIntegracaoTribCont item = new ItemInconsistenciaIntegracaoTribCont();
            item.setContaReceita(obj[2] != null ? (ContaReceita) contaFacade.recuperar(((BigDecimal) obj[2]).longValue()) : null);
            item.setOperacaoReceitaRealizada(OperacaoReceita.RECEITA_DIVIDA_ATIVA_BRUTA);
            item.setTipo(TipoIntegracao.ARRECADACAO);
            item.setEntidade(obj[4] != null ? entidadeFacade.recuperarEntidadePorUnidadeOrganizacional(((BigDecimal) obj[4]).longValue()) : null);
            item.setDataPagamento(loteBaixa.getDataPagamento());
            item.setDataCredito(obj[1] != null ? (Date) obj[1] : loteBaixa.getDataFinanciamento());
            item.setOperacaoIntegracao(OperacaoIntegracaoTribCont.MULTA_JUROS_CORRECAO);
            item.setValorContabil(obj[5] != null ? (BigDecimal) obj[5] : null);
            integracoes.add(item);
        }
        return integracoes;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<ItemInconsistenciaIntegracaoTribCont> criarItensParaInconsistenciaDaIntegracao(LoteBaixa loteBaixa) {
        service = (ServiceIntegracaoTributarioContabil) ContextLoader.getCurrentWebApplicationContext()
            .getBean("serviceIntegracaoTributarioContabil");
        return service.buscarItensParaInconsistenciaDaIntegracao(loteBaixa);
    }

    public void criarIntegracaoComAContabilidade(LoteBaixa loteBaixa, Date dataPagamento, Date dataCredito, Long idEntidade,
                                                 BigDecimal valor, Exercicio exercicio, ContaReceita conta, TipoIntegracao tipo,
                                                 OperacaoReceita operacaoReceita, FonteDeRecursos fonteDeRecursos,
                                                 OperacaoIntegracaoTribCont operacaoIntegracao,
                                                 ItemInconsistenciaIntegracaoTribCont item) throws SemUnidadeException, BloqueioMovimentoContabilException {
        service = (ServiceIntegracaoTributarioContabil) ContextLoader.getCurrentWebApplicationContext()
            .getBean("serviceIntegracaoTributarioContabil");

        ItemIntegracaoTributarioContabil itemIntegracaoTributarioContabil;
        if (item.getIntegracoes() != null && !item.getIntegracoes().isEmpty()) {
            itemIntegracaoTributarioContabil = item.getIntegracoes().get(0);
            itemIntegracaoTributarioContabil.setValor(valor);
        } else {
            itemIntegracaoTributarioContabil = service.geraItemArracadacao(loteBaixa, idEntidade, valor, conta, tipo,
                operacaoReceita, fonteDeRecursos, operacaoIntegracao);
        }
        LoteIntegracaoTributarioContabil loteIntegracao = criarLoteDoItem(itemIntegracaoTributarioContabil);

        Entidade entidade = em.find(Entidade.class, idEntidade);
        if (TipoIntegracao.ARRECADACAO.equals(tipo)) {
            LancamentoReceitaOrc lancamentoReceitaOrc = criarLancamentoReceitaOrc(loteIntegracao, loteBaixa, dataPagamento, dataCredito, entidade, valor, exercicio, conta, operacaoReceita, itemIntegracaoTributarioContabil.getFonteDeRecursos());
            lancamentoReceitaOrcFacade.gerarLancamentoIntegracao(lancamentoReceitaOrc);
            lancamentoReceitaOrcFacade.salvarNovaReceita(lancamentoReceitaOrc, null);
        } else {
            ReceitaORCEstorno receitaORCEstorno = criarEstornoLancamentoReceitaOrc(loteIntegracao, loteBaixa, dataPagamento, dataCredito, entidade, valor, exercicio, conta, operacaoReceita, itemIntegracaoTributarioContabil.getFonteDeRecursos());
            receitaORCEstornoFacade.gerarLancamentoIntegracao(receitaORCEstorno);
            receitaORCEstornoFacade.salvarNovoEstorno(receitaORCEstorno, null);
        }
        criarIntegracaoDoLoteComItem(loteIntegracao, itemIntegracaoTributarioContabil);
    }

    public CreditoReceber criarIntegracaoCreditoReceberComAContabilidade(LoteBaixa loteBaixa, Date dataCredito, Entidade entidade, BigDecimal valorIntegracao, Exercicio exercicioCorrente, ContaReceita contaReceita, TipoLancamento tipoLancamento, ConfiguracaoContabil configuracaoContabil, FonteDeRecursos fonteDeRecursos) throws SemUnidadeException, BloqueioMovimentoContabilException {
        CreditoReceber credito = new CreditoReceber();
        credito.setReceitaLOA(receitaLOAFacade.receitaPorContaUnidadeExercicio(contaReceita, entidade.getUnidadeOrganizacionalOrc(), exercicioCorrente,
            loteBaixa.getSubConta(), OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA, fonteDeRecursos));
        credito.setValor(valorIntegracao);
        credito.setPessoa(pessoaFacade.recuperaPessoaDaEntidade(entidade.getUnidadeOrganizacional(), dataCredito));
        credito.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
        credito.setUnidadeOrganizacionalAdm(entidade.getUnidadeOrganizacional());
        credito.setUnidadeOrganizacional(entidade.getUnidadeOrganizacionalOrc());
        credito.setDataCredito(dataCredito);
        credito.setDataReferencia(dataCredito);
        credito.setOperacaoCreditoReceber(OperacaoCreditoReceber.ATUALIZACAO_DE_CREDITOS_A_RECEBER);
        credito.setNaturezaCreditoReceber(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
        credito.setIntervalo(Intervalo.CURTO_PRAZO);
        credito.setContaDeDestinacao(contaFacade.recuperarContaDestinacaoPorFonte(fonteDeRecursos));
        credito.setTipoLancamento(tipoLancamento);
        credito.setIntegracaoTribCont(null);
        credito.setExercicio(exercicioCorrente);
        credito.setLoteBaixa(loteBaixa);
        credito.setIntegracao(true);
        credito.setHistorico(contaReceita + ", Lote: " + loteBaixa.getCodigoLote());
        return em.merge(credito);
    }

    public DividaAtivaContabil criarIntegracaoDividaAtivaComAContabilidade(LoteBaixa loteBaixa, Date dataCredito, Entidade entidade, BigDecimal valorIntegracao, Exercicio exercicioCorrente, ContaReceita contaReceita, TipoLancamento tipoLancamento, ConfiguracaoContabil configuracaoContabil, FonteDeRecursos fonteDeRecursos) throws SemUnidadeException, BloqueioMovimentoContabilException {
        DividaAtivaContabil lancamento = new DividaAtivaContabil();
        lancamento.setReceitaLOA(receitaLOAFacade.receitaPorContaUnidadeExercicio(contaReceita, entidade.getUnidadeOrganizacionalOrc(), exercicioCorrente,
            loteBaixa.getSubConta(), OperacaoReceita.RECEITA_DIVIDA_ATIVA_BRUTA, fonteDeRecursos));
        lancamento.setValor(valorIntegracao);
        lancamento.setPessoa(pessoaFacade.recuperaPessoaDaEntidade(entidade.getUnidadeOrganizacional(), dataCredito));
        lancamento.setClasseCredorPessoa(configuracaoContabil.getClasseTribContReceitaRea());
        lancamento.setUnidadeOrganizacionalAdm(entidade.getUnidadeOrganizacional());
        lancamento.setUnidadeOrganizacional(entidade.getUnidadeOrganizacionalOrc());
        lancamento.setTipoLancamento(tipoLancamento);
        lancamento.setDataDivida(dataCredito);
        lancamento.setDataReferencia(dataCredito);
        lancamento.setOperacaoDividaAtiva(OperacaoDividaAtiva.ATUALIZACAO);
        lancamento.setNaturezaDividaAtiva(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
        lancamento.setContaDeDestinacao(contaFacade.recuperarContaDestinacaoPorFonte(fonteDeRecursos));
        lancamento.setIntervalo(Intervalo.CURTO_PRAZO);
        lancamento.setIntegracaoTribCont(null);
        lancamento.setExercicio(exercicioCorrente);
        lancamento.setLoteBaixa(loteBaixa);
        lancamento.setIntegracao(true);
        lancamento.setHistorico(contaReceita + ", Lote: " + loteBaixa.getCodigoLote());
        return em.merge(lancamento);
    }

    private LoteIntegracaoTributarioContabil criarLoteDoItem(ItemIntegracaoTributarioContabil itemIntegracao) {
        LoteIntegracaoTributarioContabil lote = new LoteIntegracaoTributarioContabil();
        lote.setAdministrativa(itemIntegracao.getAdministrativa());
        lote.setOrcamentaria(itemIntegracao.getOrcamentaria());
        lote.setContaReceita(itemIntegracao.getContaReceita());
        lote.setSubConta(itemIntegracao.getSubConta());
        lote.setDataIntegracao(itemIntegracao.getDataPagamento());
        lote.setDataConciliacao(itemIntegracao.getDataCredito());
        lote.setLoteBaixa(itemIntegracao.getLoteBaixa());
        lote.setOperacaoReceitaRealizada(itemIntegracao.getOperacaoReceitaRealizada());
        lote.setValor(itemIntegracao.getValor());
        return em.merge(lote);
    }

    private void criarIntegracaoDoLoteComItem(LoteIntegracaoTributarioContabil loteIntegracao, ItemIntegracaoTributarioContabil itemIntegracao) {
        IntegracaoTributarioContabil integracao = new IntegracaoTributarioContabil();
        integracao.setItem(itemIntegracao);
        integracao.setLote(loteIntegracao);
        em.persist(integracao);
    }

    private LancamentoReceitaOrc criarLancamentoReceitaOrc(LoteIntegracaoTributarioContabil loteIntegracao, LoteBaixa loteBaixa, Date dataPagamento, Date dataCredito, Entidade entidade, BigDecimal valor, Exercicio exercicio, ContaReceita conta, OperacaoReceita operacaoReceita, FonteDeRecursos fonteDeRecursos) {
        LancamentoReceitaOrc lancamento = new LancamentoReceitaOrc();

        ReceitaLOA receitaLOA = receitaLOAFacade.receitaPorContaUnidadeExercicio(conta,
            entidade.getUnidadeOrganizacionalOrc(), exercicio, loteBaixa.getSubConta(), operacaoReceita, fonteDeRecursos);

        ConfiguracaoContabil configuracaoContabil = lancamentoReceitaOrcFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();

        lancamento.setPessoa(lancamentoReceitaOrcFacade.getPessoaFacade().recuperaPessoaDaEntidade(entidade.getUnidadeOrganizacionalOrc(), loteBaixa.getDataPagamento()));
        lancamento.setReceitaLOA(receitaLOA);
        lancamento.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
        lancamento.setValor(valor);
        lancamento.setSaldo(valor);
        lancamento.setUnidadeOrganizacionalAdm(entidade.getUnidadeOrganizacional());
        lancamento.setUnidadeOrganizacional(entidade.getUnidadeOrganizacionalOrc());
        lancamento.setDataLancamento(dataPagamento);
        lancamento.setDataConciliacao(dataCredito);
        lancamento.setDataReferencia(dataPagamento);
        lancamento.setSubConta(loteBaixa.getSubConta());
        lancamento.setIntegracaoTribCont(loteIntegracao);
        lancamento.setOperacaoReceitaRealizada(operacaoReceita);
        lancamento.setTipoOperacao(TipoOperacaoORC.NORMAL);
        lancamento.setLote(loteBaixa.getCodigoLote());
        lancamento.setComplemento(conta + ", Lote: " + loteBaixa.getCodigoLote());
        lancamento.setExercicio(exercicio);
        lancamento.setReceitaDeIntegracao(true);
        lancamento.setFonteDeRecursos(fonteDeRecursos);
        lancamentoReceitaOrcFacade.definirEventoReceitaRealizada(lancamento);

        return lancamento;
    }

    private ReceitaORCEstorno criarEstornoLancamentoReceitaOrc(LoteIntegracaoTributarioContabil loteIntegracao, LoteBaixa loteBaixa, Date dataPagamento, Date dataCredito, Entidade entidade, BigDecimal valor, Exercicio exercicio, ContaReceita conta, OperacaoReceita operacaoReceita, FonteDeRecursos fonteDeRecursos) {
        ReceitaORCEstorno estorno = new ReceitaORCEstorno();

        ReceitaLOA receitaLOA = receitaLOAFacade.receitaPorContaUnidadeExercicio(conta,
            entidade.getUnidadeOrganizacionalOrc(), exercicio, loteBaixa.getSubConta(), operacaoReceita, fonteDeRecursos);

        ConfiguracaoContabil configuracaoContabil = lancamentoReceitaOrcFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();

        estorno.setPessoa(lancamentoReceitaOrcFacade.getPessoaFacade().recuperaPessoaDaEntidade(entidade.getUnidadeOrganizacionalOrc(), loteBaixa.getDataPagamento()));
        estorno.setReceitaLOA(receitaLOA);
        estorno.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
        estorno.setValor(valor);
        estorno.setUnidadeOrganizacionalAdm(entidade.getUnidadeOrganizacional());
        estorno.setUnidadeOrganizacionalOrc(entidade.getUnidadeOrganizacionalOrc());
        estorno.setDataEstorno(dataPagamento);
        estorno.setDataConciliacao(dataCredito);
        estorno.setDataReferencia(dataPagamento);
        estorno.setContaFinanceira(loteBaixa.getSubConta());
        estorno.setIntegracaoTribCont(loteIntegracao);
        estorno.setOperacaoReceitaRealizada(operacaoReceita);
        estorno.setLote(loteBaixa.getCodigoLote());
        estorno.setComplementoHistorico(conta + ", Lote: " + loteBaixa.getCodigoLote());
        estorno.setExercicio(exercicio);
        estorno.setIntegracao(true);
        receitaORCEstornoFacade.definirEventoContabil(estorno);

        return estorno;
    }

    public DepoisDePagarResquest buildDepoisDePagar(LoteBaixa loteBaixa) {
        DepoisDePagarResquest build = DepoisDePagarResquest.build().nome(loteBaixa.getCodigoLote());
        for (ItemLoteBaixa item : loteBaixa.getItemLoteBaixa()) {
            try {
                if (item.getDam() != null) {
                    DAM dam = damFacade.recuperar(item.getDam().getId());
                    if (dam != null) {
                        for (ItemDAM itemDAM : dam.getItens()) {
                            build.addParcelas(itemDAM.getParcela());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Erro ao recuperar DAM para pós pagamento", e);
            }
        }
        return build;
    }

    public LoteBaixa criarPagamentoInternetBanking(PagamentoDTO pagamentoDTO) {
        Banco banco = bancoFacade.buscarBancoPorCNPJ(pagamentoDTO.getCodigoBanco());
        if (banco == null) {
            throw new ExcecaoNegocioGenerica("Banco não encontrado para o ISPB " + pagamentoDTO.getCodigoBanco());
        }
        Agencia agencia = agenciaFacade.buscarAgenciaPeloNumeroDaAgenciaAndIspbBanco(pagamentoDTO.getCodigoAgencia(), banco.getIspb());
        if (agencia == null) {
            throw new ExcecaoNegocioGenerica("Agência não encontrada com o código " + pagamentoDTO.getCodigoAgencia() + " para o banco " + banco.getIspb() + " - " + banco.getDescricao());
        }
        SubConta subConta = buscarSubContaContaArrecadacaoPorBanco(banco);
        if (subConta == null) {
            throw new ExcecaoNegocioGenerica("Conta não encontrado para o banco " + banco);
        }
        Map<ItemPagamentoDTO, DAM> dams = Maps.newHashMap();
        for (ItemPagamentoDTO itemPagamentoDTO : pagamentoDTO.getItens()) {
            DAM dam = recuperaDamPorCodigoBarrasSemDigitoVerificador(itemPagamentoDTO.getLinhaDigitavel());
            if (dam == null) {
                throw new ExcecaoNegocioGenerica("DAM não encontrado com o código de barras " + itemPagamentoDTO.getLinhaDigitavel());
            }
            dams.put(itemPagamentoDTO, dam);
        }
        List<ParcelaValorDivida> parcelas = Lists.newArrayList();
        LoteBaixa lotebaixa = new LoteBaixa();
        lotebaixa.setIntegraContasAcrecimos(true);
        lotebaixa.setSituacaoLoteBaixa(SituacaoLoteBaixa.EM_ABERTO);
        lotebaixa.setTipoDePagamentoBaixa(TipoDePagamentoBaixa.INTERNET_BANKING);
        lotebaixa.setDataPagamento(pagamentoDTO.getDataPagamento());
        lotebaixa.setDataFinanciamento(pagamentoDTO.getDataCredito());
        lotebaixa.setBanco(banco);
        lotebaixa.setFormaPagamento(LoteBaixa.FormaPagamento.NORMAL);
        lotebaixa.setCodigoLote(gerarCodigoLote(lotebaixa.getDataPagamento()));
        lotebaixa.setSubConta(subConta);
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemPagamentoDTO itemPagamentoDTO : dams.keySet()) {
            DAM dam = dams.get(itemPagamentoDTO);
            ItemLoteBaixa itemLoteBaixa = new ItemLoteBaixa();
            itemLoteBaixa.setValorPago(itemPagamentoDTO.getValorPago());
            itemLoteBaixa.setCodigoBarrasInformado(CarneUtil.montaLinhaDigitavel(itemPagamentoDTO.getLinhaDigitavel()));
            itemLoteBaixa.setDamInformado(retornaDamCodigoBarras(itemPagamentoDTO.getLinhaDigitavel()));
            itemLoteBaixa.setDataPagamento(pagamentoDTO.getDataPagamento());
            itemLoteBaixa.setDataCredito(pagamentoDTO.getDataCredito());
            itemLoteBaixa.setAutenticacao(itemPagamentoDTO.getAutenticacao());
            valorTotal = valorTotal.add(itemPagamentoDTO.getValorPago());
            itemLoteBaixa.setLoteBaixa(lotebaixa);
            if (dam != null) {
                dam = damFacade.recuperar(dam.getId());
                itemLoteBaixa.setDam(dam);
                for (ItemDAM itemDam : dam.getItens()) {
                    ItemLoteBaixaParcela it = new ItemLoteBaixaParcela();
                    it.setItemDam(itemDam);
                    it.setItemLoteBaixa(itemLoteBaixa);
                    BigDecimal proporcao = (itemDam.getValorTotal().multiply(CEM)
                        .divide(itemLoteBaixa.getValorPago(), 6, RoundingMode.HALF_UP));
                    proporcao = proporcao.divide(CEM, 6, RoundingMode.HALF_UP);
                    proporcao = proporcao.multiply(itemLoteBaixa.getValorPago());
                    it.setValorPago(proporcao);
                    itemLoteBaixa.getItemParcelas().add(it);
                    parcelas.add(itemDam.getParcela());
                }
            }
            lotebaixa.getItemLoteBaixa().add(itemLoteBaixa);
        }
        lotebaixa.setValorTotal(valorTotal);
        lotebaixa.setQuantidadeParcelas(lotebaixa.getItemLoteBaixa().size());
        lotebaixa = encerrarLoteManual(lotebaixa);
        ArrecadacaoFacade.AssistenteArrecadacao assistenteArrecadacao = new ArrecadacaoFacade.AssistenteArrecadacao(null, lotebaixa, lotebaixa.getItemLoteBaixa().size());
        arrecadacaoFacade.efetuaPagamento(assistenteArrecadacao, null);
        depoisDePagarQueue.enqueueProcess(DepoisDePagarResquest.build().nome("Internet Banking").parcelas(parcelas));
        arrecadacaoFacade.gerarIntegracao(lotebaixa, TipoIntegracao.ARRECADACAO);
        return lotebaixa;
    }

    private SubConta buscarSubContaContaArrecadacaoPorBanco(Banco banco) {
        Query q = em.createNativeQuery("SELECT conf.* FROM BancoContaConfTributario conf "
                + " INNER JOIN subconta sub ON sub.id = conf.subconta_id"
                + " INNER JOIN contabancariaentidade conta ON conta.id = sub.contabancariaentidade_id"
                + " INNER JOIN agencia ON agencia.id = conta.agencia_id"
                + " INNER JOIN banco ON banco.id = agencia.banco_id "
                + " WHERE banco.id = :banco "
                + "and sub.TIPOCONTAFINANCEIRA = :tipoArrecadacao"
                + "and sub.INTEGRACAO = 1"
                + " and coalesce(conf.ativo,0) = :ativo ", BancoContaConfTributario.class)
            .setParameter("banco", banco.getId()).setParameter("ativo", Boolean.TRUE)
            .setParameter("tipoArrecadacao", TipoContaFinanceira.ARRECADACAO.name())
            .setParameter("integracao", Boolean.TRUE);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return ((BancoContaConfTributario) resultList.get(0)).getSubConta();
        }
        return null;
    }

    public void estornarPagamentoInternetBanking(PagamentoDTO pagamentoDTO) {
        Set<LoteBaixa> lotesEstornar = Sets.newHashSet();
        for (ItemPagamentoDTO itemPagamentoDTO : pagamentoDTO.getItens()) {
            DAM dam = recuperaDamPorCodigoBarrasSemDigitoVerificador(itemPagamentoDTO.getLinhaDigitavel());
            if (dam == null) {
                throw new ExcecaoNegocioGenerica("DAM não encontrado com o código de barras " + itemPagamentoDTO.getLinhaDigitavel());
            }
            List<LoteBaixa> lotes = buscarLotesInternetBankingBaixaPorDam(dam);
            if (lotes.isEmpty()) {
                throw new ExcecaoNegocioGenerica("Arrecadação não encontrada para o código de barras " + itemPagamentoDTO.getLinhaDigitavel());
            }
            lotesEstornar.addAll(lotes);
        }
        for (LoteBaixa loteBaixa : lotesEstornar) {
            loteBaixa = recuperar(loteBaixa.getId());
            estornarLoteAntigo(loteBaixa);
            ArrecadacaoFacade.AssistenteArrecadacao assistenteArrecadacao = new ArrecadacaoFacade.AssistenteArrecadacao(null, loteBaixa, loteBaixa.getItemLoteBaixa().size());
            arrecadacaoFacade.estornarPagamento(assistenteArrecadacao, null);
            arrecadacaoFacade.executarDependenciasEstorno(loteBaixa);
        }
    }

    public List<LoteBaixa> buscarLotesInternetBankingBaixaPorDam(DAM dam) {
        String hql = "select i.loteBaixa from ItemLoteBaixa i " +
            " where i.dam = :dam and i.loteBaixa.tipoDePagamentoBaixa = :tipo";
        Query q = em.createQuery(hql);
        q.setParameter("dam", dam);
        q.setParameter("tipo", TipoDePagamentoBaixa.INTERNET_BANKING);
        return q.getResultList();
    }

    public Calculo recuperarCalculo(Calculo calculo) {
        Calculo recupera = em.find(Calculo.class, calculo.getId());
        initializeAndUnproxy(recupera);
        return recupera;
    }

    public Calculo recuperarCalculo(ValorDivida valorDivida) {
        valorDivida = em.find(ValorDivida.class, valorDivida.getId());
        Calculo recupera = valorDivida.getCalculo();
        initializeAndUnproxy(recupera);
        return recupera;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void salvarLoteBaixaDoArquivo(LoteBaixaDoArquivo loteBaixaDoArquivo) {
        em.merge(loteBaixaDoArquivo);
    }

    public static class ProcessaArquivo extends AssistenteBarraProgresso {
        private List<ArquivoLoteBaixa> arquivos;
        private ValorDivida valorDivida;
        private Map<ArquivoLoteBaixa, Map<LoteBaixaDoArquivo, ArquivoBancarioTributario>> mapArquivoBancarioTributario;

        public ProcessaArquivo(UsuarioSistema usuarioSistema,
                               Date dataAtual,
                               String usuarioBancoDeDados) {
            super(usuarioSistema, "Processamento de arquivo de arrecadação", 0);
            setDataAtual(dataAtual);
            setUsuarioBancoDados(usuarioBancoDeDados);
            this.arquivos = Lists.newArrayList();
            this.mapArquivoBancarioTributario = Maps.newHashMap();
        }

        public List<ArquivoLoteBaixa> getArquivos() {
            return arquivos;
        }

        public void setArquivos(List<ArquivoLoteBaixa> arquivos) {
            this.arquivos = arquivos;
        }

        public ValorDivida getValorDivida() {
            return valorDivida;
        }

        public void setValorDivida(ValorDivida valorDivida) {
            this.valorDivida = valorDivida;
        }

        public void addArquivo(ArquivoLoteBaixa arquivoLoteBaixa) {
            arquivos.add(arquivoLoteBaixa);
        }

        public Map<ArquivoLoteBaixa, Map<LoteBaixaDoArquivo, ArquivoBancarioTributario>> getMapArquivoBancarioTributario() {
            return mapArquivoBancarioTributario;
        }
    }

    public static class ConsultaLoteBaixa {
        public Long numeroArquivo;
        public Date dataPagamentoInicial;
        public Date dataPagamentoFinal;
        public Date dataMovimentoInicial;
        public Date dataMovimentoFinal;
        public SituacaoLoteBaixa situacaoLoteBaixa;
        public SubConta subConta;
        public Banco banco;

        public ConsultaLoteBaixa() {

        }
    }

    private class MapaDeContas {
        public Long idTributo;
        public Integer ano;
        public ContaTributoReceita.TipoContaReceita tipoContaReceita;
        public Boolean dividaAtiva;

        private MapaDeContas(Long idTributo, Integer ano, ContaTributoReceita.TipoContaReceita tipoContaReceita, Boolean dividaAtiva) {
            this.idTributo = idTributo;
            this.ano = ano;
            this.tipoContaReceita = tipoContaReceita;
            this.dividaAtiva = dividaAtiva;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MapaDeContas that = (MapaDeContas) o;

            if (!dividaAtiva.equals(that.dividaAtiva)) return false;
            if (!ano.equals(that.ano)) return false;
            if (!idTributo.equals(that.idTributo)) return false;
            if (tipoContaReceita != that.tipoContaReceita) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = idTributo.hashCode();
            result = 31 * result + ano.hashCode();
            result = 31 * result + tipoContaReceita.hashCode();
            result = 31 * result + dividaAtiva.hashCode();
            return result;
        }
    }

    public static class AssistenteMapaArrecadacao extends AssistenteBarraProgresso {
        private List<MapaArrecadacao> listaArrecadacao;
        private List<MapaArrecadacao> listaAcrescimos;
        private List<MapaArrecadacao> listaDescontos;

        public AssistenteMapaArrecadacao(UsuarioSistema usuarioSistema, String descricaoProcesso, int total) {
            super(usuarioSistema, descricaoProcesso, total);
            this.listaArrecadacao = Lists.newArrayList();
            this.listaAcrescimos = Lists.newArrayList();
            this.listaDescontos = Lists.newArrayList();
        }

        public List<MapaArrecadacao> getListaArrecadacao() {
            return listaArrecadacao;
        }

        public void setListaArrecadacao(List<MapaArrecadacao> listaArrecadacao) {
            this.listaArrecadacao = listaArrecadacao;
        }

        public List<MapaArrecadacao> getListaAcrescimos() {
            return listaAcrescimos;
        }

        public void setListaAcrescimos(List<MapaArrecadacao> listaAcrescimos) {
            this.listaAcrescimos = listaAcrescimos;
        }

        public List<MapaArrecadacao> getListaDescontos() {
            return listaDescontos;
        }

        public void setListaDescontos(List<MapaArrecadacao> listaDescontos) {
            this.listaDescontos = listaDescontos;
        }
    }

    public List<UnidadeOrganizacional> buscarUnidadesOrcamentariasPorLote(LoteBaixa selecionado) {
        String sql = " select distinct und.* " +
            "  from ITEMINTEGRACAOTRIBCONT integracao " +
            " inner join unidadeorganizacional und on und.id = integracao.orcamentaria_id " +
            " where integracao.loteBaixa_id = :loteId ";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("loteId", selecionado.getId());
        return q.getResultList();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public List<String> buscarLotesOndeODamJaFoiPago(String numeroDam) {
        String sql = " select lb.codigolote || ' em ' || to_char(lb.dataPagamento,'dd/MM/yyyy') " +
            " from dam " +
            " inner join itemdam item on dam.id = item.dam_id " +
            " inner join itemlotebaixa ilb on dam.id = ilb.dam_id " +
            " inner join lotebaixa lb on ilb.lotebaixa_id = lb.id " +
            " where dam.situacao = :pago " +
            " and lb.situacaolotebaixa in (:baixados) " +
            " and dam.numeroDam = :numeroDam ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pago", DAM.Situacao.PAGO.name());
        q.setParameter("baixados", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        q.setParameter("numeroDam", numeroDam.trim());
        List<String> lotes = q.getResultList();
        return lotes != null ? lotes : Lists.<String>newArrayList();
    }

    public List<String> buscarLotesOndeODamJaFoiPago(Long idDam, Long idItemLoteBaixa) {
        String sql = " select lb.codigolote from dam " +
            " inner join itemdam item on dam.id = item.dam_id " +
            " inner join itemlotebaixa ilb on dam.id = ilb.dam_id " +
            " inner join lotebaixa lb on ilb.lotebaixa_id = lb.id " +
            " where dam.situacao = :pago " +
            " and lb.situacaolotebaixa in (:baixados) " +
            " and dam.id = :idDam ";
        if (idItemLoteBaixa != null) {
            sql += " and ilb.id <> :idItemLoteBaixa ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("pago", DAM.Situacao.PAGO.name());
        q.setParameter("baixados", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        q.setParameter("idDam", idDam);
        if (idItemLoteBaixa != null) {
            q.setParameter("idItemLoteBaixa", idItemLoteBaixa);
        }

        List<String> lotes = q.getResultList();
        return lotes != null ? lotes : Lists.<String>newArrayList();
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public LoteBaixa criarLoteBaixaDAF607(LoteBaixaFacade.ProcessaArquivo processaArquivo,
                                          ArquivoLoteBaixa arquivoLoteBaixa,
                                          LoteBaixaDoArquivo loteBaixaDoArquivo,
                                          ConfiguracaoTributario configuracaoTributario) {
        LoteBaixa loteBaixa = new LoteBaixa();
        loteBaixa.setIntegraContasAcrecimos(true);
        loteBaixa.setSituacaoLoteBaixa(SituacaoLoteBaixa.BAIXADO);
        loteBaixa.setTipoDePagamentoBaixa(TipoDePagamentoBaixa.BAIXA_ARQUIVO_RETORNO);
        loteBaixa.setDataPagamento(loteBaixaDoArquivo.getDataPagamento());
        loteBaixa.setDataFinanciamento(loteBaixaDoArquivo.getDataMovimentacao());
        loteBaixa.setValorTotal(loteBaixaDoArquivo.getValorTotalArquivo());
        loteBaixa.setFormaPagamento(LoteBaixa.FormaPagamento.NORMAL);
        ArquivoDAF607 arquivoDAF607 = (ArquivoDAF607) loteBaixaDoArquivo.getArquivoBancarioTributario();
        for (RegistroDAF607 registro : arquivoDAF607.getRegistrosDAF607()) {
            criarItemLoteBaixa(loteBaixa, registro, arquivoDAF607,
                configuracaoTributario, processaArquivo.getUsuarioBancoDados(),
                processaArquivo.getUsuarioSistema());
            processaArquivo.conta();
        }
        loteBaixa.setQuantidadeParcelas(arquivoDAF607.getRegistrosDAF607().size());
        loteBaixa.setBanco(bancoFacade.retornaBancoPorNumero(arquivoDAF607.getHeaderDAF607().getCodigoBancoArrecadador()));
        loteBaixa.setCodigoLote(loteBaixaFacade.gerarCodigoLote(loteBaixa.getDataPagamento()));
        loteBaixa.setSubConta(arquivoLoteBaixa.getTransientSubConta());
        return em.merge(loteBaixa);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public LoteBaixa criarLoteBaixaRBC001(LoteBaixaFacade.ProcessaArquivo processaArquivo,
                                          ArquivoLoteBaixa arquivoLoteBaixa,
                                          LoteBaixaDoArquivo loteBaixaDoArquivo) {
        LoteBaixa loteBaixa = new LoteBaixa();
        loteBaixa.setIntegraContasAcrecimos(true);
        loteBaixa.setSituacaoLoteBaixa(SituacaoLoteBaixa.EM_ABERTO);
        loteBaixa.setTipoDePagamentoBaixa(TipoDePagamentoBaixa.BAIXA_ARQUIVO_RETORNO);
        loteBaixa.setDataPagamento(loteBaixaDoArquivo.getDataPagamento());
        loteBaixa.setDataFinanciamento(loteBaixaDoArquivo.getDataMovimentacao());
        loteBaixa.setValorTotal(loteBaixaDoArquivo.getValorTotalArquivo());
        ArquivoRCB001 arquivoRCB001 = (ArquivoRCB001) loteBaixaDoArquivo.getArquivoBancarioTributario();
        for (RegistroRCB001 registro : arquivoRCB001.getRegistros()) {
            geraItensDoLote(registro, loteBaixa);
            processaArquivo.conta();
        }
        loteBaixa.setQuantidadeParcelas(arquivoRCB001.getRegistros().size());
        loteBaixa.setBanco(bancoFacade.retornaBancoPorNumero(arquivoRCB001.getHeader().getCodigoBanco()));
        loteBaixa.setCodigoLote(loteBaixaFacade.gerarCodigoLote(loteBaixa.getDataPagamento()));
        loteBaixa.setSubConta(arquivoLoteBaixa.getTransientSubConta());
        return em.merge(loteBaixa);
    }

    private void geraItensDoLote(RegistroRCB001 registroRCB001, LoteBaixa lote) {
        try {
            DAM dam = loteBaixaFacade.recuperaDamPorCodigoBarrasSemDigitoVerificador(registroRCB001.getCodigoBarras());
            ItemLoteBaixa itemLoteBaixa = new ItemLoteBaixa();
            itemLoteBaixa.setValorPago(registroRCB001.getValorRecebido());
            itemLoteBaixa.setCodigoBarrasInformado(CarneUtil.montaLinhaDigitavel(registroRCB001.getCodigoBarras()));
            itemLoteBaixa.setDamInformado(loteBaixaFacade.retornaDamCodigoBarras(registroRCB001.getCodigoBarras()));
            try {
                if (registroRCB001.getDataPagamento() != null && !"".equals(registroRCB001.getDataPagamento())) {
                    itemLoteBaixa.setDataPagamento(formatter.parse(registroRCB001.getDataPagamento()));
                }
                if (registroRCB001.getDataCredito() != null && !"".equals(registroRCB001.getDataCredito())) {
                    itemLoteBaixa.setDataCredito(formatter.parse(registroRCB001.getDataCredito()));
                }
            } catch (Exception e) {
                logger.error("erro ao definir data de pagamento ou credito ", e);
            }
            itemLoteBaixa.setLoteBaixa(lote);
            if (dam != null) {
                itemLoteBaixa.setDam(dam);
                for (ItemDAM itemDam : dam.getItens()) {
                    ItemLoteBaixaParcela it = new ItemLoteBaixaParcela();
                    it.setItemDam(itemDam);
                    it.setItemLoteBaixa(itemLoteBaixa);

                    BigDecimal proporcao = (itemDam.getValorTotal().multiply(CEM))
                        .divide(itemLoteBaixa.getValorPago(), 6, RoundingMode.HALF_UP);
                    proporcao = proporcao.divide(CEM, 6, RoundingMode.HALF_UP);
                    proporcao = proporcao.multiply(itemLoteBaixa.getValorPago());
                    it.setValorPago(proporcao);
                    itemLoteBaixa.getItemParcelas().add(it);
                }
            }
            lote.getItemLoteBaixa().add(itemLoteBaixa);
        } catch (Exception e) {
            logger.error("Erro ao geraItensDoLote: ", e);
        }
    }

}
