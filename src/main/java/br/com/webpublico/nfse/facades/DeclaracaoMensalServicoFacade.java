/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCalculoISS;
import br.com.webpublico.enums.TipoSituacaoCalculoISS;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoISSDao;
import br.com.webpublico.negocios.tributario.dao.JdbcDeclaracaoMensalServicoDao;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonLoteEncerramentoMensalServico;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.domain.dtos.enums.IssqnFmTipoLancamentoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.LancadoPorNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoDeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoMovimentoMensalNfseDTO;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.enums.TipoDeclaracaoMensalServico;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.enums.TipoServicoDeclarado;
import br.com.webpublico.nfse.exceptions.NfseOperacaoNaoPermitidaException;
import br.com.webpublico.nfse.util.ImprimeRelatorioNfse;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class DeclaracaoMensalServicoFacade extends CalculoExecutorDepoisDePagar<DeclaracaoMensalServico> {
    private DecimalFormat decimalFormat;

    private static final String SQL_BASE = "select dec.id,  dec.codigo,  dec.tipo,  dec.mes, dec.situacao,  dec.qtdNotas, " +
            " dec.totalServicos, dec.totalIss, ex.ano, calculo.id as calculo_id,  dec.abertura, dec.encerramento, dec.tipomovimento " +
            " from DeclaracaoMensalServico dec " +
            " inner join exercicio ex on ex.id = dec.exercicio_id " +
            " left join processocalculo proc on proc.id = dec.processocalculo_id" +
            " left join calculo on calculo.processocalculo_id = proc.id " +
            " where dec.prestador_id = :empresaId ";

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private CalculaISSFacade calculaISSFacade;
    @EJB
    private LivroFiscalFacade livroFiscalFacade;
    @EJB
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private LoteDeclaracaoMensalServicoFacade loteDeclaracaoMensalServicoFacade;
    @EJB
    private SingletonLoteEncerramentoMensalServico singletonLoteEncerramentoMensalServico;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private JdbcDeclaracaoMensalServicoDao jdbcDeclaracaoMensalServicoDao;

    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;

    public DeclaracaoMensalServicoFacade() {
        super(DeclaracaoMensalServico.class);
    }

    @PostConstruct
    public void init() {
        this.jdbcDeclaracaoMensalServicoDao = (JdbcDeclaracaoMensalServicoDao) Util.getSpringBeanPeloNome("declaracaoMensalServicoDao");
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public NotaFiscalFacade getNotaFiscalFacade() {
        return notaFiscalFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public ConfiguracaoNfseFacade getConfiguracaoNfseFacade() {
        return configuracaoNfseFacade;
    }

    @Override
    public DeclaracaoMensalServico recuperar(Object id) {
        DeclaracaoMensalServico dms = em.find(DeclaracaoMensalServico.class, id);
        Hibernate.initialize(dms.getNotas());
        for (NotaDeclarada nota : dms.getNotas()) {
            Hibernate.initialize(nota.getDeclaracaoPrestacaoServico().getItens());
        }
        if (dms.getProcessoCalculo() != null) {
            Hibernate.initialize(dms.getProcessoCalculo().getCalculos());
        }
        return dms;
    }

    public List<DeclaracaoMensalServicoNfseDTO> criarDeclaracoesMensalServico(LoteDeclaracaoMensalServico lancamento,
                                                                              List<NotaFiscalSearchDTO> notas) {
        return criarDeclaracoesMensalServico(null, lancamento, notas);
    }

    public List<DeclaracaoMensalServicoNfseDTO> criarDeclaracoesMensalServico(AssistenteBarraProgresso assistente,
                                                                              LoteDeclaracaoMensalServico lancamento,
                                                                              List<NotaFiscalSearchDTO> notas) {

        if (assistente != null)
            assistente.setDescricaoProcesso("Preparando as notas para declarar ...");

        singletonLoteEncerramentoMensalServico.registrar(assistente, lancamento);
        Map<Long, List<NotaFiscalSearchDTO>> mapa = Maps.newHashMap();
        for (NotaFiscalSearchDTO nota : notas) {
            if (assistente != null)
                assistente.conta();
            if (!mapa.containsKey(nota.getIdPrestador())) {
                mapa.put(nota.getIdPrestador(), new ArrayList<NotaFiscalSearchDTO>());
            }
            mapa.get(nota.getIdPrestador()).add(nota);
        }
        List<DeclaracaoMensalServicoNfseDTO> declaracoes = Lists.newArrayList();
        if (assistente != null) {
            assistente.zerarContadoresProcesso();
            assistente.setTotal(mapa.keySet().size());
            assistente.setDescricaoProcesso("Preparando as declarações ...");
        }

        for (Long idPrestador : mapa.keySet()) {
            if (assistente != null)
                assistente.conta();
            DeclaracaoMensalServicoNfseDTO declaracao = new DeclaracaoMensalServicoNfseDTO();
            declaracao.setExercicio(lancamento.getExercicio().getAno());
            declaracao.setMes(lancamento.getMes().getNumeroMes());
            declaracao.setTipo(TipoDeclaracaoMensalServicoNfseDTO.PRINCIPAL);
            declaracao.setTipoMovimento(lancamento.getTipoMovimento().toDto());
            declaracao.setNotas(Lists.<NotaFiscalSearchDTO>newArrayList());
            for (NotaFiscalSearchDTO notaDTO : mapa.get(idPrestador)) {
                NotaFiscalSearchDTO notaDeclarar = new NotaFiscalSearchDTO();
                notaDeclarar.setId(notaDTO.getId());
                declaracao.getNotas().add(notaDeclarar);
                declaracao.setTotalServicos(declaracao.getTotalServicos().add(notaDTO.getTotalServicos()));
                declaracao.setTotalIss(declaracao.getTotalIss().add(notaDTO.getIss()));
            }
            declaracao.setQtdNotas(mapa.get(idPrestador).size());
            declaracao.setAbertura(new Date());
            declaracao.setEncerramento(new Date());
            declaracao.setUsuarioDeclaracao(lancamento.getUsuarioSistema().getNome());
            declaracao.setLancadoPor(LancadoPorNfseDTO.WEBPUBLICO);
            PrestadorServicoNfseDTO prestador = new PrestadorServicoNfseDTO();
            prestador.setId(idPrestador);
            declaracao.setPrestador(prestador);
            lancamento.setTotalServicos(lancamento.getTotalServicos().add(declaracao.getTotalServicos()));
            lancamento.setTotalIss(lancamento.getTotalIss().add(declaracao.getTotalIss()));
            lancamento.setQtdDeclaracoes(lancamento.getQtdDeclaracoes() + 1);
            declaracoes.add(declaracao);
        }
        singletonLoteEncerramentoMensalServico.finalizar(lancamento);
        return declaracoes;
    }

    @Asynchronous
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Future<List<DeclaracaoMensalServicoNfseDTO>> criarDeclaracoesMensalServicoAsync(AssistenteBarraProgresso assistente, LoteDeclaracaoMensalServico lancamento, List<NotaFiscalSearchDTO> notas) {
        return new AsyncResult<>(criarDeclaracoesMensalServico(assistente, lancamento, notas));
    }

    public List<DeclaracaoMensalServico> declarar(List<DeclaracaoMensalServicoNfseDTO> declaracoes) {
        return declarar(null, null, declaracoes);
    }

    public List<DeclaracaoMensalServico> declarar(AssistenteBarraProgresso assistente, LoteDeclaracaoMensalServico lote, List<DeclaracaoMensalServicoNfseDTO> declaracoes) {
        singletonLoteEncerramentoMensalServico.registrar(assistente, lote);
        List<DeclaracaoMensalServico> retorno = Lists.newArrayList();
        if (assistente != null)
            assistente.setDescricaoProcesso("Salvando Declarações");
        for (DeclaracaoMensalServicoNfseDTO declaracao : declaracoes) {
            if (assistente != null)
                assistente.conta();
            DeclaracaoMensalServico declaracaoMensalServico = declaracaoMensalServicoFacade.createAndSaveNewTransactional(declaracao);
            if (declaracaoMensalServico != null) {
                retorno.add(declaracaoMensalServico);
                if (lote != null) {
                    LoteDeclaracaoMensalServicoItem loteItem = new LoteDeclaracaoMensalServicoItem();
                    loteItem.setLote(lote);
                    loteItem.setDeclaracaoMensalServico(declaracaoMensalServico);
                    loteDeclaracaoMensalServicoFacade.persistirItem(loteItem);
                }
            }
        }
        singletonLoteEncerramentoMensalServico.finalizar(lote);
        return retorno;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DeclaracaoMensalServico createAndSaveNewTransactional(DeclaracaoMensalServicoNfseDTO declaracaoMensalServico) {
        try {
            return createAndSave(declaracaoMensalServico);
        } catch (Exception e) {
            logger.error("Erro ao salvar declaracao {} - {}", declaracaoMensalServico.getPrestador().getInscricaoMunicipal(),
                    declaracaoMensalServico.getExercicio() + "/" + declaracaoMensalServico.getMes());
        }
        return null;
    }

    @Asynchronous
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Future<List<DeclaracaoMensalServico>> declararAsync(AssistenteBarraProgresso assistente, LoteDeclaracaoMensalServico lote,
                                                               List<DeclaracaoMensalServicoNfseDTO> declaracoes) {
        List<DeclaracaoMensalServico> result = declarar(assistente, lote, declaracoes);
        lote.setSituacao(LoteDeclaracaoMensalServico.Situacao.ENCERRADO);
        lote.setFim(new Date());
        loteDeclaracaoMensalServicoFacade.salvar(lote);
        return new AsyncResult<>(result);
    }

    public void validarDeclaracao(Integer mes, Integer exercicio, Long prestadorId,
                                  boolean isAusenciaMovimento, TipoMovimentoMensalNfseDTO tipoMovimento) {
        if (isAusenciaMovimento) {
            if (TipoMovimentoMensalNfseDTO.NORMAL.equals(tipoMovimento)) {
                List<NotaFiscalSearchDTO> notasDoMes = notaFiscalFacade.buscarNotasSemDeclararPorCompetencia(prestadorId,
                        mes, exercicio, TipoMovimentoMensal.NORMAL, true);
                if (!notasDoMes.isEmpty()) {
                    throw new NfseOperacaoNaoPermitidaException("Não é possível declarar ausência de movimentos para um periodo que existem notas emitidas");
                }
            } else if (TipoMovimentoMensalNfseDTO.RETENCAO.equals(tipoMovimento)) {
                List<NotaFiscalSearchDTO> notasDoMes = notaFiscalFacade.buscarNotasSemDeclararPorCompetencia(prestadorId,
                        mes, exercicio, TipoMovimentoMensal.RETENCAO, false);
                if (!notasDoMes.isEmpty()) {
                    throw new NfseOperacaoNaoPermitidaException("Não é posspível declarar ausência de movimentos para um periodo que existem notas com retenção recebidas");
                }
            }
        }
        Calendar dataAtual = Calendar.getInstance();
        dataAtual.setTime(new Date());
        if (exercicio >= dataAtual.get(Calendar.YEAR) &&
                mes > dataAtual.get(Calendar.MONTH) + 1) {
            throw new NfseOperacaoNaoPermitidaException("Não é possível encerrar uma competência futura.");
        }
    }

    public DeclaracaoMensalServico createAndSave(DeclaracaoMensalServicoNfseDTO dto) {
        try {
            validarDeclaracao(dto.getMes(), dto.getExercicio(), dto.getPrestador().getId(),
                    dto.getNotas().isEmpty(), dto.getTipoMovimento());
            DeclaracaoMensalServico declaracao;
            if (dto.getId() == null) {
                declaracao = new DeclaracaoMensalServico();
                declaracao.setCodigo(getUltimoNumeroDeDeclaracaoDoPrestador(dto.getPrestador().getId()) + 1);
            } else {
                declaracao = recuperar(dto.getId());
            }
            declaracao.setMes(Mes.getMesToInt(dto.getMes()));
            declaracao.setExercicio(exercicioFacade.getExercicioPorAno(dto.getExercicio()));
            declaracao.setPrestador(new CadastroEconomico(dto.getPrestador().getId()));
            if (hasDeclaracaoNaCompetencia(declaracao.getPrestador().getId(), declaracao.getMes(), declaracao.getExercicio().getAno(), TipoMovimentoMensal.valueOf(dto.getTipoMovimento().name()))) {
                declaracao.setTipo(TipoDeclaracaoMensalServico.COMPLEMENTAR);
            } else {
                declaracao.setTipo(TipoDeclaracaoMensalServico.PRINCIPAL);
            }
            declaracao.setTipoMovimento(TipoMovimentoMensal.valueOf(dto.getTipoMovimento().name()));
            declaracao.setNotas(Lists.<NotaDeclarada>newArrayList());
            declaracao.setSituacao(DeclaracaoMensalServico.Situacao.ENCERRADO);
            declaracao.setQtdNotas(dto.getQtdNotas());
            declaracao.setTotalServicos(dto.getTotalServicos());
            declaracao.setTotalIss(dto.getTotalIss());
            declaracao.setAbertura(dto.getAbertura());
            declaracao.setEncerramento(dto.getAbertura());
            declaracao.setReferencia(DataUtil.primeiroDiaMes(dto.getMes(), dto.getExercicio()));
            declaracao.setUsuarioDeclaracao(dto.getUsuarioDeclaracao());
            if (dto.getLancadoPor() != null)
                declaracao.setLancadoPor(DeclaracaoMensalServico.LancadoPor.valueOf(dto.getLancadoPor().name()));
            for (NotaFiscalSearchDTO notaFiscalSearchDTO : dto.getNotas()) {
                validarNotaDeclarada(declaracao, notaFiscalSearchDTO);
                NotaDeclarada nota = new NotaDeclarada();
                nota.setDeclaracaoMensalServico(declaracao);
                nota.setDeclaracaoPrestacaoServico(new DeclaracaoPrestacaoServico(notaFiscalSearchDTO.getId()));
                declaracao.getNotas().add(nota);
            }
            declaracao = jdbcDeclaracaoMensalServicoDao.inserir(declaracao);
            livroFiscalFacade.criarLivroParaDeclaracaoMensal(declaracao);
            criarCalculoDms(declaracao);
            return declaracao;
        } catch (Exception e) {
            throw e;
        }
    }

    private void validarNotaDeclarada(DeclaracaoMensalServico declaracao, NotaFiscalSearchDTO notaFiscalSearchDTO) {
        if (hasDeclaracaoMensalServico(declaracao.getTipoMovimento(), notaFiscalSearchDTO.getId())) {
            if (declaracao.getTipoMovimento().equals(TipoMovimentoMensal.NORMAL)) {
                throw new NfseOperacaoNaoPermitidaException("A Nota Fiscal " + notaFiscalSearchDTO.getNumero() + " do Tomador "
                        + (StringUtils.isEmpty(notaFiscalSearchDTO.getNomeTomador()) ? "Não Identificado" : notaFiscalSearchDTO.getNomeTomador())
                        + " já encontra-se em outro encerramento mensal.");
            } else {
                throw new NfseOperacaoNaoPermitidaException("A Nota Fiscal " + notaFiscalSearchDTO.getNumero() + " do Prestador "
                        + notaFiscalSearchDTO.getNomePrestador() + " já encontra-se em outro encerramento mensal.");
            }
        }
    }

    public Boolean hasDeclaracaoMensalServico(TipoMovimentoMensal tipoMovimentoMensal, Long idDeclaracao) {
        String sql = " select 1 from notadeclarada nd " +
                " inner join declaracaomensalservico dms on dms.id = nd.declaracaomensalservico_id " +
                " where dms.situacao <> :cancelado " +
                "   and dms.tipomovimento = :tipo_movimento " +
                "   and nd.declaracaoprestacaoservico_id = :id_declaracao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cancelado", DeclaracaoMensalServico.Situacao.CANCELADO.name());
        q.setParameter("tipo_movimento", tipoMovimentoMensal.name());
        q.setParameter("id_declaracao", idDeclaracao);
        List resultList = q.getResultList();
        return resultList != null && !resultList.isEmpty();
    }

    public void criarCalculoDms(DeclaracaoMensalServico dms) {
        ProcessoCalculoISS processoCalculoISS = criarCalculoIss(dms);
        dms.setProcessoCalculo(processoCalculoISS);
        jdbcDeclaracaoMensalServicoDao.update(dms);
    }

    public Long contarCalculosIssMesPrestador(Long idPrestador, Mes mes) {
        String sql = "select count(iss.id) from CalculoIss iss " +
                " inner join calculo calc on calc.id = iss.id " +
                " inner join processocalculoiss proc on proc.id = calc.processocalculo_id " +
                " where calc.cadastro_id = :idPrestador and proc.mesreferencia = :mes";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idPrestador", idPrestador);
        q.setParameter("mes", mes.getNumeroMes());
        if (!q.getResultList().isEmpty()) {
            return ((Number) q.getResultList().get(0)).longValue();
        }

        return 0L;
    }

    public ProcessoCalculoISS criarCalculoIss(DeclaracaoMensalServico declaracao) {
        Map<Long, List<ServicoDeclaradoDTO>> itens = Maps.newHashMap();
        for (NotaDeclarada notaDeclarada : declaracao.getNotasSemIssRetido()) {
            for (ServicoDeclaradoDTO itemDeclaracaoServico : buscarServicosDaDeclaracao(notaDeclarada.getDeclaracaoPrestacaoServico().getId())) {
                if (!itens.containsKey(itemDeclaracaoServico.getIdServico())) {
                    itens.put(itemDeclaracaoServico.getIdServico(), Lists.<ServicoDeclaradoDTO>newArrayList());
                }
                itens.get(itemDeclaracaoServico.getIdServico()).add(itemDeclaracaoServico);
            }
        }
        ConfiguracaoNfse configuracao = configuracaoNfseFacade.recuperarConfiguracao();
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(declaracao.getPrestador().getId());

        ProcessoCalculoISS processo = new ProcessoCalculoISS();
        processo.setDataLancamento(new Date());
        processo.setExercicio(declaracao.getExercicio());
        processo.setMesReferencia(declaracao.getMes().getNumeroMes());
        processo.setDescricao("Nota Fiscal Eletronica " + cadastroEconomico.getInscricaoCadastral() + " " + processo.getMesReferencia() + "/" + processo.getExercicio().getAno());
        processo.setDivida(configuracaoNfseFacade.buscarDividaNfse(declaracao.getTipoMovimento(), declaracao.getTipo()));
        processo.setCalculos(new ArrayList<CalculoISS>());

        CalculoISS calculo = new CalculoISS();
        calculo.setSubDivida(contarCalculosIssMesPrestador(cadastroEconomico.getId(), declaracao.getMes()) + 1);
        calculo.setSequenciaLancamento(calculaISSFacade.buscarSequenciaDeLancamentoDoCalculoIssEstimadoMensalOuHomologado(cadastroEconomico, declaracao.getExercicio(), processo.getMesReferencia(), TipoCalculoISS.MENSAL));
        calculo.setAusenciaMovimento(declaracao.getTotalIss().compareTo(BigDecimal.ZERO) <= 0);
        calculo.setTaxaSobreIss(BigDecimal.ZERO);
        calculo.setCadastroEconomico(cadastroEconomico);
        calculo.setCadastro(cadastroEconomico);
        calculo.setDataCalculo(new Date());
        calculo.setProcessoCalculoISS(processo);
        calculo.setSimulacao(Boolean.TRUE);
        calculo.setTipoCalculoISS(TipoCalculoISS.MENSAL);
        calculo.setTipoSituacaoCalculoISS(TipoSituacaoCalculoISS.LANCADO);
        calculo.setMesdeReferencia(Mes.getMesToInt(DataUtil.getMes(declaracao.getReferencia())));
        calculo.setBaseCalculo(BigDecimal.ZERO);
        calculo.setFaturamento(BigDecimal.ZERO);
        calculo.setValorCalculado(BigDecimal.ZERO);
        if (TipoMovimentoMensal.NORMAL.equals(declaracao.getTipoMovimento()) ||
                TipoMovimentoMensal.INSTITUICAO_FINANCEIRA.equals(declaracao.getTipoMovimento())) {
            calculo.setIssqnFmTipoLancamentoNfse(IssqnFmTipoLancamentoNfseDTO.PROPRIO);
        } else {
            calculo.setIssqnFmTipoLancamentoNfse(IssqnFmTipoLancamentoNfseDTO.SUBSTITUTO);
        }
        if (declaracao.getNotas() != null && declaracao.getNotas().size() == 1) {
            NotaDeclarada notaDeclarada = declaracao.getNotas().get(0);
            NotaFiscal notaFiscal = notaFiscalFacade.buscarNotaPorDeclaracaoPrestacaoServico(notaDeclarada.getDeclaracaoPrestacaoServico());
            if (notaFiscal != null) {
                calculo.setObservacao("Prestador: " + notaFiscal.getPrestador().getPessoa().getNomeCpfCnpj() + " Nota Fiscal: " + notaFiscal.getNumero());
            }
        }
        if (itens.isEmpty()) {
            ItemCalculoIss item = new ItemCalculoIss();
            item.setCalculo(calculo);
            item.setAliquota(BigDecimal.ZERO);
            item.setBaseCalculo(BigDecimal.ZERO);
            item.setFaturamento(BigDecimal.ZERO);
            item.setValorCalculado(BigDecimal.ZERO);
            item.setTributo(configuracaoNfseFacade.buscarTributoNfse(declaracao.getTipoMovimento(), declaracao.getTipo()));
            calculo.getItemCalculoIsss().add(item);
        } else {
            for (Long idServico : itens.keySet()) {
                ItemCalculoIss item = new ItemCalculoIss();
                item.setCalculo(calculo);
                item.setAliquota(itens.get(idServico).get(0).getAliquotaServico());
                item.setBaseCalculo(BigDecimal.ZERO);
                item.setFaturamento(BigDecimal.ZERO);
                item.setValorCalculado(BigDecimal.ZERO);
                for (ServicoDeclaradoDTO itemDeclaracaoServico : itens.get(idServico)) {
                    item.setBaseCalculo(item.getBaseCalculo().add(itemDeclaracaoServico.getBaseCalculo()));
                    item.setFaturamento(item.getFaturamento().add(itemDeclaracaoServico.getValorServico()));
                    if (TipoMovimentoMensal.RETENCAO.equals(declaracao.getTipoMovimento()) || TipoMovimentoMensal.ISS_RETIDO.equals(declaracao.getTipoMovimento())) {
                        item.setValorCalculado(item.getValorCalculado().add(itemDeclaracaoServico.getIssCalculado()));
                    } else {
                        if (!itemDeclaracaoServico.getIssRetido()) {
                            item.setValorCalculado(item.getValorCalculado().add(itemDeclaracaoServico.getIssCalculado()));
                        }
                    }
                }

                item.setServico(new Servico(idServico));
                item.setTributo(configuracaoNfseFacade.buscarTributoNfse(declaracao.getTipoMovimento(), declaracao.getTipo()));
                calculo.getItemCalculoIsss().add(item);
            }
        }

        calculo.setBaseCalculo(declaracao.getTotalServicos());
        calculo.setFaturamento(declaracao.getTotalServicos());
        calculo.setValorCalculado(declaracao.getTotalIss());
        calculo.setValorEfetivo(calculo.getValorCalculado());
        calculo.setValorReal(calculo.getValorCalculado());
        calculo.setNotaEletronica(true);

        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setPessoa(cadastroEconomico.getPessoa());
        calculoPessoa.setCalculo(calculo);
        calculo.setPessoas(Lists.<CalculoPessoa>newArrayList());
        calculo.getPessoas().add(calculoPessoa);
        processo.getCalculos().add(calculo);
        processo = ((JdbcCalculoISSDao) Util.getSpringBeanPeloNome("calculoISSDao")).salvar(processo);
        gerarDebito(calculo, configuracao, declaracao.getTipoMovimento(), declaracao.getTipo());
        return processo;
    }

    public List<ServicoDeclaradoDTO> buscarServicosDaDeclaracao(Long id) {
        String sql = "select item.id as itemId, " +
                " servico.id as servicoId, " +
                " servico.aliquotaisshomologado," +
                " item.basecalculo," +
                " item.valorservico," +
                " item.iss," +
                " coalesce(dec.issretido, 0)" +
                " from ItemDeclaracaoServico item" +
                " inner join servico on servico.id = item.servico_id" +
                " inner join declaracaoprestacaoservico dec on dec.id = item.declaracaoprestacaoservico_id" +
                " where dec.id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        List<ServicoDeclaradoDTO> retorno = Lists.newArrayList();
        for (Object[] objects : ((List<Object[]>) q.getResultList())) {
            retorno.add(new ServicoDeclaradoDTO(objects));
        }
        return retorno;
    }

    public void gerarDebito(CalculoISS calculoISS, ConfiguracaoNfse configuracao, TipoMovimentoMensal tipoMovimentoMensal,
                            TipoDeclaracaoMensalServico tipoDeclaracaoMensalServico) {
        if (calculoISS.getValorCalculado().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        OpcaoPagamento opcaoPagamento = recuperarOpcoesPagamento(calculoISS.getProcessoCalculoISS().getDivida(), new Date());
        Date dataVencimento = buscarVencimentoIss(opcaoPagamento,
                calculoISS.getProcessoCalculoISS().getExercicio().getAno(),
                calculoISS.getMesdeReferencia().getNumeroMes());
        while (DataUtil.ehDiaNaoUtil(dataVencimento, feriadoFacade)) {
            dataVencimento = DataUtil.adicionaDias(dataVencimento, 1);
        }
        ValorDivida valorDivida = new ValorDivida();
        valorDivida.setOcorrenciaValorDivida(new ArrayList<OcorrenciaValorDivida>());
        valorDivida.setCalculo(calculoISS);
        valorDivida.setItemValorDividas(new ArrayList<ItemValorDivida>());
        valorDivida.setParcelaValorDividas(new ArrayList<ParcelaValorDivida>());
        valorDivida.setDivida(calculoISS.getProcessoCalculo().getDivida());
        valorDivida.setEmissao(new Date());
        valorDivida.setExercicio(calculoISS.getProcessoCalculo().getExercicio());
        valorDivida.setValor(calculoISS.getValorEfetivo());
        ParcelaValorDivida parcelaValorDivida = criarParcela(calculoISS, valorDivida, dataVencimento, opcaoPagamento);

        Tributo tributoNfseIssqn = configuracaoNfseFacade.buscarTributoNfse(tipoMovimentoMensal, tipoDeclaracaoMensalServico);
        criarItensValorDividaParcela(calculoISS, valorDivida, parcelaValorDivida, tributoNfseIssqn);

        ((JdbcDividaAtivaDAO) Util.getSpringBeanPeloNome("dividaAtivaDAO")).persisteValorDivida(valorDivida);
    }

    private ParcelaValorDivida criarParcela(CalculoISS calculo, ValorDivida valorDivida, Date vencimento, OpcaoPagamento op) {
        ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
        parcelaValorDivida.setDividaAtiva(Boolean.FALSE);
        parcelaValorDivida.setSequenciaParcela("1");
        parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
        parcelaValorDivida.setOpcaoPagamento(op);
        parcelaValorDivida.setValorDivida(valorDivida);
        parcelaValorDivida.setValor(calculo.getValorEfetivo());
        parcelaValorDivida.setVencimento(vencimento);
        parcelaValorDivida.setPercentualValorTotal(BigDecimal.valueOf(100));
        if (parcelaValorDivida.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            parcelaValorDivida.setSituacaoAtual(new SituacaoParcelaValorDivida(SituacaoParcela.SEM_MOVIMENTO, parcelaValorDivida, BigDecimal.ZERO));
        } else {
            parcelaValorDivida.setSituacaoAtual(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
        }
        valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
        return parcelaValorDivida;
    }

    private void criarItensValorDividaParcela(CalculoISS calculo, ValorDivida valorDivida, ParcelaValorDivida parcelaValorDivida, Tributo tributo) {
        ItemValorDivida ivd = new ItemValorDivida();
        ivd.setTributo(tributo);
        ivd.setValor(calculo.getValorEfetivo());
        ivd.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(ivd);
        ItemParcelaValorDivida ipvd = new ItemParcelaValorDivida();
        ipvd.setItemValorDivida(ivd);
        ipvd.setParcelaValorDivida(parcelaValorDivida);
        ipvd.setValor(ivd.getValor());
        parcelaValorDivida.getItensParcelaValorDivida().add(ipvd);
    }

    public OpcaoPagamento recuperarOpcoesPagamento(Divida divida, Date dataBase) throws IllegalArgumentException {
        Query q = em.createQuery(" from OpcaoPagamentoDivida op "
                + " where op.divida = :divida "
                + " and :vigencia between coalesce(op.inicioVigencia, current_date) "
                + " and coalesce(op.finalVigencia, current_date)");
        q.setParameter("divida", divida);
        q.setParameter("vigencia", DataUtil.dataSemHorario(dataBase));
        if (q.getResultList().isEmpty()) {
            throw new IllegalArgumentException("Nenhuma opção de pagamento válida para a dívida selecionada: " + divida.getDescricao());
        }
        return ((OpcaoPagamentoDivida) q.getResultList().get(0)).getOpcaoPagamento();
    }

    private Integer getUltimoNumeroDeDeclaracaoDoPrestador(Long prestadorId) {
        Query q = em.createNativeQuery("select coalesce(max(a.codigo),0) from DeclaracaoMensalServico a where a.prestador_id = :empresaId");
        q.setParameter("empresaId", prestadorId);
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        }
        return 0;
    }

    private boolean hasDeclaracaoNaCompetencia(Long prestadorId, Mes mes, Integer exercicio, TipoMovimentoMensal tipoMovimento) {
        Query q = em.createNativeQuery("select a.id from " +
                " DeclaracaoMensalServico a " +
                " inner join exercicio ex on ex.id = a.exercicio_id " +
                " where a.prestador_id =:empresaId and a.mes = :mes and ex.ano = :exercicio and a.tipoMovimento = :tipoMovimento");
        q.setParameter("empresaId", prestadorId);
        q.setParameter("mes", mes.name());
        q.setParameter("exercicio", exercicio);
        q.setParameter("tipoMovimento", tipoMovimento.name());
        return !q.getResultList().isEmpty();
    }

    public List<ResultadoParcela> recuperarDebitosDaDecalaracao(Long id, Long prestadorId) {
        String hql = SQL_BASE + " and dec.id = :id";
        Query q = em.createNativeQuery(hql);
        q.setParameter("empresaId", prestadorId);
        q.setParameter("id", id);
        q.setMaxResults(1);
        Object[] resultado = (Object[]) q.getSingleResult();
        List<ResultadoParcela> cp = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, ((BigDecimal) resultado[9]).longValue())
                .executaConsulta().getResultados();
        return cp;

    }

    private DeclaracaoMensalServicoNfseDTO popularDtoPeloSqlBase(Object[] obj) {
        DeclaracaoMensalServicoNfseDTO toAdd = new DeclaracaoMensalServicoNfseDTO();
        toAdd.setId(((BigDecimal) obj[0]).longValue());
        toAdd.setCodigo(((BigDecimal) obj[1]).intValue());
        toAdd.setTipo(TipoDeclaracaoMensalServico.valueOf((String) obj[2]).toDto());
        toAdd.setMes(Mes.valueOf((String) obj[3]).getNumeroMes());
        toAdd.setSituacao(DeclaracaoMensalServico.Situacao.valueOf((String) obj[4]).toDto());
        toAdd.setQtdNotas(((BigDecimal) obj[5]).intValue());
        toAdd.setTotalServicos(((BigDecimal) obj[6]));
        toAdd.setTotalIss(((BigDecimal) obj[7]));
        toAdd.setExercicio(((BigDecimal) obj[8]).intValue());
        if (obj[9] != null) {
            List<ResultadoParcela> cp = new ConsultaParcela()
                    .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, ((BigDecimal) obj[9]).longValue())
                    .executaConsulta().getResultados();
            for (ResultadoParcela parcela : cp) {
                toAdd.setSituacaoDebito(parcela.getSituacao());
                toAdd.setTotalJuros(toAdd.getTotalJuros().add(parcela.getValorJuros()));
                toAdd.setTotalMulta(toAdd.getTotalMulta().add(parcela.getValorMulta()));
                toAdd.setTotalCorrecao(toAdd.getTotalCorrecao().add(parcela.getValorCorrecao()));
            }
        }
        toAdd.setAbertura(((Date) obj[10]));
        toAdd.setEncerramento(((Date) obj[11]));
        toAdd.setTipoMovimento(TipoMovimentoMensal.valueOf((String) obj[12]).toDto());
        return toAdd;
    }

    public List<DeclaracaoMensalServicoNfseDTO> buscarDeclaracoesNoPeriodoSemLivro(Mes inicio, Mes fim, Integer exercicio, Long prestadorId, TipoMovimentoMensal tipoMovimentoMensal) {
        String where = " and dec.mes in (:meses) and ex.ano = :exercicio " +
                " and dec.tipoMovimento = :tipoMovimento" +
                " and not exists (select id from itemlivrofiscal where declaracaomensalservico_id = dec.id) ";

        Query q = em.createNativeQuery(SQL_BASE + where);
        q.setParameter("meses", Mes.getTodosMesesComoStringNoIntevalo(inicio, fim));
        q.setParameter("exercicio", exercicio);
        q.setParameter("empresaId", prestadorId);
        q.setParameter("tipoMovimento", tipoMovimentoMensal.name());
        List<Object[]> resultado = q.getResultList();
        List<DeclaracaoMensalServicoNfseDTO> retorno = Lists.newArrayList();
        for (Object[] obj : resultado) {
            retorno.add(popularDtoPeloSqlBase(obj));
        }
        return retorno;
    }

    public List<EstatisticaMensalNfseDTO> buscarServicosPrestadosNaReferencia(Date referenciaInicial, Date referenciaFinal, Long cadastroId) {
        List<EstatisticaMensalNfseDTO> estatisticaPorMes = Lists.newArrayList();
        LocalDate inicio = LocalDate.fromDateFields(referenciaInicial);
        while (inicio.isBefore(LocalDate.fromDateFields(referenciaFinal))) {
            inicio = inicio.plus(Period.months(1));
            estatisticaPorMes.add(new EstatisticaMensalNfseDTO(inicio.getMonthOfYear(), inicio.getYear()));
        }
        String sql = "select extract(month from dps.COMPETENCIA) mes, " +
                "       extract(YEAR from dps.COMPETENCIA) ano," +
                "       coalesce(dps.ISSRETIDO,0), " +
                "       dps.SITUACAO, " +
                "       count(dps.id) count_id, " +
                "       sum(dps.totalservicos - dps.descontosincondicionais - dps.deducoeslegais) totalServicos, " +
                "       sum(dps.isscalculado) totalIss " +
                "  from DECLARACAOPRESTACAOSERVICO dps " +
                " left join NOTAFISCAL nota on nota.declaracaoprestacaoservico_id = dps.id " +
                " left join servicodeclarado sd on sd.declaracaoprestacaoservico_id = dps.id" +
                "                              and sd.tiposervicodeclarado = :servico_prestado " +
                " inner join CADASTROECONOMICO cmc on cmc.id = coalesce(nota.prestador_id, sd.cadastroeconomico_id) " +
                " where cmc.ID = :cadastroId " +
                "   and dps.competencia between :referenciaInicial and :referenciaFinal " +
                " group by coalesce(dps.issretido, 0), " +
                "         dps.SITUACAO, " +
                "         dps.TIPODOCUMENTO, " +
                "         extract(month from dps.COMPETENCIA), extract(year from dps.COMPETENCIA), extract(YEAR from dps.COMPETENCIA) " +
                " order by extract(YEAR from dps.COMPETENCIA), extract(year from dps.COMPETENCIA), extract(month from dps.COMPETENCIA) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastroId", cadastroId);
        q.setParameter("referenciaInicial", referenciaInicial);
        q.setParameter("referenciaFinal", referenciaFinal);
        q.setParameter("servico_prestado", TipoServicoDeclarado.SERVICO_PRESTADO.name());
        List<Object[]> resultado = q.getResultList();
        for (Object[] obj : resultado) {
            int mes = ((Number) obj[0]).intValue();
            int ano = ((Number) obj[1]).intValue();
            boolean retido = ((BigDecimal) obj[2]).compareTo(BigDecimal.ZERO) > 0;
            String situacao = (String) obj[3];
            int quantidade = ((Number) obj[4]).intValue();
            EstatisticaMensalNfseDTO estatistica = new EstatisticaMensalNfseDTO(mes, ano);
            if (!estatisticaPorMes.contains(estatistica)) {
                estatisticaPorMes.add(estatistica);
            }
            int index = estatisticaPorMes.indexOf(estatistica);
            estatisticaPorMes.get(index).somarEmitidas(quantidade);
            if (retido) {
                estatisticaPorMes.get(index).somarRetidas(quantidade);
            }
            if (SituacaoNota.CANCELADA.name().equalsIgnoreCase(situacao)) {
                estatisticaPorMes.get(index).somarCanceladas(quantidade);
            } else {
                estatisticaPorMes.get(index).somarServico(((BigDecimal) obj[5]));
                estatisticaPorMes.get(index).somarIss(((BigDecimal) obj[6]));
            }
        }
        return estatisticaPorMes;
    }

    public List<EstatisticaMensalPorServicoNfseDTO> buscarEstatisticasServicosPrestadosNaReferencia(Date referenciaInicial, Date referenciaFinal, Long cadastroId) {
        String sql =
                "select    extract(month from dps.COMPETENCIA), " +
                        "      extract(year from dps.COMPETENCIA)," +
                        "       serv.codigo, " +
                        "       serv.nome, " +
                        "       sum(dps.totalservicos - dps.descontosincondicionais - dps.deducoeslegais) " +
                        "   from DECLARACAOPRESTACAOSERVICO dps " +
                        "  inner join itemdeclaracaoservico item on dps.id = item.declaracaoprestacaoservico_id " +
                        "  inner join servico serv on item.servico_id = serv.id " +
                        "  left join notafiscal nf on nf.declaracaoprestacaoservico_id = dps.id " +
                        "  left join servicodeclarado sd on sd.declaracaoprestacaoservico_id = dps.id " +
                        "                               and sd.tiposervicodeclarado = :servico_prestado " +
                        "  inner join cadastroeconomico cmc on cmc.id = coalesce(nf.prestador_id, sd.cadastroeconomico_id) " +
                        "where cmc.ID = :cadastroId " +
                        "  and dps.competencia between :referenciaInicial and :referenciaFinal" +
                        "  and dps.situacao != :cancelada " +
                        "group by extract(month from dps.COMPETENCIA), extract(year from dps.COMPETENCIA), serv.codigo, serv.nome " +
                        "order by extract(month from dps.COMPETENCIA), extract(year from dps.COMPETENCIA),serv.codigo, serv.nome";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastroId", cadastroId);
        q.setParameter("referenciaInicial", referenciaInicial);
        q.setParameter("referenciaFinal", referenciaFinal);
        q.setParameter("cancelada", SituacaoNota.CANCELADA.name());
        q.setParameter("servico_prestado", TipoServicoDeclarado.SERVICO_PRESTADO.name());
        List<Object[]> resultado = q.getResultList();
        List<EstatisticaMensalPorServicoNfseDTO> retorno = Lists.newArrayList();
        for (Object[] obj : resultado) {
            int mes = ((Number) obj[0]).intValue();
            int ano = ((Number) obj[1]).intValue();
            EstatisticaMensalPorServicoNfseDTO item = new EstatisticaMensalPorServicoNfseDTO();
            item.setMes(mes);
            item.setAno(ano);
            item.setCodigo((String) obj[2]);
            item.setNome((String) obj[3]);
            item.setValor((BigDecimal) obj[4]);
            retorno.add(item);
        }
        return retorno;
    }

    public List<EstatisticaMensalPorTomadorNfseDTO> buscarEstatisticasTomadorPrestadosNaReferencia(Date referenciaInicial, Date referenciaFinal, Long cadastroId) {
        String sql =
                "select     extract(month from dps.COMPETENCIA), " +
                        "       extract(year from dps.COMPETENCIA), " +
                        "       tomador.nomerazaosocial, " +
                        "       tomador.cpfcnpj, " +
                        "       sum(dps.totalservicos - dps.descontosincondicionais - dps.deducoeslegais) totalServicos, " +
                        "       sum(dps.isscalculado) totalIss " +
                        "   from DECLARACAOPRESTACAOSERVICO dps " +
                        "  inner join DadosPessoaisNfse tomador on dps.dadospessoaistomador_id = tomador.id " +
                        "  left join notafiscal nf on nf.declaracaoprestacaoservico_id = dps.id " +
                        "  left join servicodeclarado sd on sd.declaracaoprestacaoservico_id = dps.id " +
                        "                               and sd.tiposervicodeclarado = :servico_prestado " +
                        "  inner join cadastroeconomico cmc on cmc.id = coalesce(nf.prestador_id, sd.cadastroeconomico_id) " +
                        "where cmc.ID = :cadastroId " +
                        "  and dps.competencia between :referenciaInicial and :referenciaFinal " +
                        "  and dps.situacao != :cancelada " +
                        "group by extract(month from dps.COMPETENCIA), extract(year from dps.COMPETENCIA), " +
                        "         tomador.nomerazaosocial, " +
                        "         tomador.cpfcnpj " +
                        "order by extract(month from dps.COMPETENCIA), extract(year from dps.COMPETENCIA), " +
                        "         tomador.nomerazaosocial, " +
                        "         tomador.cpfcnpj";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastroId", cadastroId);
        q.setParameter("referenciaInicial", referenciaInicial);
        q.setParameter("referenciaFinal", referenciaFinal);
        q.setParameter("cancelada", SituacaoNota.CANCELADA.name());
        q.setParameter("servico_prestado", TipoServicoDeclarado.SERVICO_PRESTADO.name());
        List<Object[]> resultado = q.getResultList();
        List<EstatisticaMensalPorTomadorNfseDTO> retorno = Lists.newArrayList();
        for (Object[] obj : resultado) {
            int mes = ((Number) obj[0]).intValue();
            int ano = ((Number) obj[1]).intValue();
            EstatisticaMensalPorTomadorNfseDTO item = new EstatisticaMensalPorTomadorNfseDTO();
            item.setMes(mes);
            item.setAno(ano);
            item.setNome((String) obj[2]);
            item.setCnpj((String) obj[3]);
            item.setTotalServico((BigDecimal) obj[4]);
            item.setTotalPagar((BigDecimal) obj[5]);
            retorno.add(item);
        }
        return retorno;
    }

    public DeclaracaoMensalServico cancelarDeclaracaoMensalServico(DeclaracaoMensalServico declaracao) {
        declaracao.setDataCancelamento(new Date());
        declaracao.setUsuarioCancelamentoWp(Util.recuperarUsuarioCorrente());
        cancelarDebitosAndAtribuirSituacaoCancelamento(declaracao);
        return cancelarSemValidar(declaracao);
    }

    private void cancelarDebitosAndAtribuirSituacaoCancelamento(DeclaracaoMensalServico declaracao) {
        cancelarDebitos(declaracao);
        declaracao.setSituacao(DeclaracaoMensalServico.Situacao.CANCELADO);
    }

    private DeclaracaoMensalServico cancelarSemValidar(DeclaracaoMensalServico declaracao) throws NfseOperacaoNaoPermitidaException {
        salvar(declaracao);
        return recarregar(declaracao);
    }

    private void cancelarDebitos(DeclaracaoMensalServico declaracao) {
        if (declaracao.getProcessoCalculo() == null)
            return;
        Calculo calculo = declaracao.getProcessoCalculo().getCalculos().get(0);
        List<ResultadoParcela> resultados = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId())
                .executaConsulta()
                .getResultados();
        for (ResultadoParcela resultado : resultados) {
            ParcelaValorDivida parcelaValorDivida = em.find(ParcelaValorDivida.class, resultado.getId());
            SituacaoParcelaValorDivida situacaoParcelaValorDivida = new SituacaoParcelaValorDivida(SituacaoParcela.CANCELAMENTO, parcelaValorDivida, parcelaValorDivida.getSituacaoAtual().getSaldo());
            parcelaValorDivida.getSituacoes().add(situacaoParcelaValorDivida);
            em.merge(parcelaValorDivida);
        }
    }

    public Date buscarVencimentoIss(OpcaoPagamento opcaoPagamento, Integer ano, Integer mes) {
        Calendar dataVencimento = Calendar.getInstance();
        dataVencimento.setTime(new Date());
        dataVencimento.set(Calendar.DAY_OF_MONTH, ((ParcelaFixaPeriodica) opcaoPagamento.getParcelas().get(0)).getDiaVencimento());
        dataVencimento.set(Calendar.MONTH, mes);
        dataVencimento.set(Calendar.YEAR, ano);
        return dataVencimento.getTime();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<List<LivroFiscalCompetenciaNfseDTO>> buscarCompetenciasLivroFiscalAsync(Long prestadorId,
                                                                                          Integer exercicioInicial,
                                                                                          Integer exercicioFinal,
                                                                                          Integer mesInicial,
                                                                                          Integer mesFinal,
                                                                                          TipoMovimentoMensal tipoMovimentoMensal) {
        return new AsyncResult<>(buscarCompetenciasLivroFiscal(prestadorId, exercicioInicial, exercicioFinal,
                mesInicial, mesFinal, tipoMovimentoMensal));
    }

    public List<LivroFiscalCompetenciaNfseDTO> buscarCompetenciasLivroFiscal(Long prestadorId,
                                                                             Integer exercicioInicial,
                                                                             Integer exercicioFinal,
                                                                             Integer mesInicial,
                                                                             Integer mesFinal,
                                                                             TipoMovimentoMensal tipoMovimentoMensal) {
        Date competenciaInicial = DataUtil.getDia(1, mesInicial, exercicioInicial);
        Date competenciaFinal = DataUtil.getDia(1, mesFinal, exercicioFinal);
        competenciaFinal = DataUtil.ultimoDiaMes(competenciaFinal).getTime();

        competenciaInicial = DataUtil.dataSemHorario(competenciaInicial);
        competenciaFinal = DataUtil.dataSemHorario(competenciaFinal);

        List<LivroFiscalCompetenciaNfseDTO> competencias =
                buscarCompetenciasLivroFiscalComMovimentos(prestadorId, tipoMovimentoMensal, competenciaInicial, competenciaFinal);

        Date competenciaAtual = competenciaInicial;

        while (competenciaAtual.before(competenciaFinal)) {
            if (!hasCompetenciaNoLivroFiscal(competenciaAtual, competencias)) {
                competencias.add(criarCompetenciaLivroFiscalDefault(prestadorId, competenciaAtual, tipoMovimentoMensal.toDto()));
            }

            competenciaAtual = DataUtil.adicionarMeses(competenciaAtual, 1);
        }

        Collections.sort(competencias, new Comparator<LivroFiscalCompetenciaNfseDTO>() {
            @Override
            public int compare(LivroFiscalCompetenciaNfseDTO o1, LivroFiscalCompetenciaNfseDTO o2) {
                int i = o1.getExercicio().compareTo(o2.getExercicio());
                if (i != 0)
                    return i;
                i = o1.getMes().compareTo(o2.getMes());
                return i;
            }
        });

        return competencias;
    }

    private List<LivroFiscalCompetenciaNfseDTO> buscarCompetenciasLivroFiscalComMovimentos(Long prestadorId,
                                                                                           TipoMovimentoMensal tipoMovimentoMensal,
                                                                                           Date competenciaInicial,
                                                                                           Date competenciaFinal) {
        switch (tipoMovimentoMensal) {
            case NORMAL: {
                return buscarCompetenciasLivroFiscalComMovimentoServicosPrestados(prestadorId, competenciaInicial, competenciaFinal);
            }
            case RETENCAO: {
                return buscarCompetenciasLivroFiscalComMovimentoServicosTomados(prestadorId, competenciaInicial, competenciaFinal);
            }
            case INSTITUICAO_FINANCEIRA: {
                return buscarCompetenciasLivroFiscalComMovimentoDesif(prestadorId, competenciaInicial, competenciaFinal);
            }
        }
        return Lists.newArrayList();
    }

    private List<LivroFiscalCompetenciaNfseDTO> buscarCompetenciasLivroFiscalComMovimentoServicosPrestados(Long prestadorId,
                                                                                                           Date competenciaInicial,
                                                                                                           Date competenciaFinal) {
        List<LivroFiscalCompetenciaNfseDTO> competencias = Lists.newArrayList();
        String sql = " SELECT " +
                "       EXTRACT(YEAR FROM DEC.COMPETENCIA) AS EXERCICIO, " +
                "       EXTRACT(MONTH FROM DEC.COMPETENCIA) AS MES, " +
                "       COUNT(1) AS QUANTIDADE, " +
                "       SUM(CASE  " +
                "              WHEN DEC.ISSRETIDO != 1 THEN DEC.ISSCALCULADO " +
                "              ELSE 0 " +
                "           END) AS ISSQN_PROPRIO, " +
                "       SUM(CASE  " +
                "              WHEN DEC.ISSRETIDO = 1 THEN DEC.ISSCALCULADO " +
                "              ELSE 0  " +
                "           END) AS ISSQN_RETIDO, " +
                "       SUM(CASE  " +
                "              WHEN DEC.ISSRETIDO != 1 AND DEC.SITUACAO = :PAGA THEN DEC.ISSCALCULADO  " +
                "              ELSE 0 " +
                "           END) AS ISSQN_PAGO," +
                "       SUM(DEC.TOTALSERVICOS) AS VALOR_SERVICO " +
                "     FROM DECLARACAOPRESTACAOSERVICO DEC " +
                "    LEFT JOIN NOTAFISCAL NF ON NF.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID  " +
                "    LEFT JOIN SERVICODECLARADO SD ON SD.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID AND SD.TIPOSERVICODECLARADO = :TIPO_SERVICO_PRESTADO " +
                "    INNER JOIN CADASTROECONOMICO CE ON CE.ID = COALESCE(NF.PRESTADOR_ID, SD.CADASTROECONOMICO_ID) " +
                " WHERE (NF.ID IS NOT NULL OR SD.ID IS NOT NULL)" +
                "   AND CE.ID = :PRESTADOR_ID " +
                "   AND DEC.SITUACAO != :CANCELADA " +
                "   AND trunc(DEC.COMPETENCIA) BETWEEN :COMPETENCIA_INICIAL AND :COMPETENCIA_FINAL " +
                " GROUP BY EXTRACT(YEAR FROM DEC.COMPETENCIA), EXTRACT(MONTH FROM DEC.COMPETENCIA) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("PRESTADOR_ID", prestadorId);
        q.setParameter("COMPETENCIA_INICIAL", competenciaInicial);
        q.setParameter("COMPETENCIA_FINAL", competenciaFinal);
        q.setParameter("CANCELADA", SituacaoNota.CANCELADA.name());
        q.setParameter("PAGA", SituacaoNota.PAGA.name());
        q.setParameter("TIPO_SERVICO_PRESTADO", TipoServicoDeclarado.SERVICO_PRESTADO.name());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                LivroFiscalCompetenciaNfseDTO competencia = new LivroFiscalCompetenciaNfseDTO();
                competencia.setPrestadorId(prestadorId);
                competencia.setTipoMovimento(TipoMovimentoMensal.NORMAL.toDto());
                competencia.setExercicio(((Number) obj[0]).intValue());
                competencia.setMes(((Number) obj[1]).intValue());
                competencia.setQuantidade(((BigDecimal) obj[2]).intValue());
                competencia.setIssqnProprio(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
                competencia.setIssqnRetido(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
                competencia.setIssqnPago(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
                competencia.setValorServico(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
                competencia.setSaldoIssqn(competencia.getIssqnProprio().subtract(competencia.getIssqnPago()));
                competencias.add(competencia);
            }
        }
        return competencias;
    }

    private List<LivroFiscalCompetenciaNfseDTO> buscarCompetenciasLivroFiscalComMovimentoServicosTomados(Long prestadorId,
                                                                                                         Date competenciaInicial,
                                                                                                         Date competenciaFinal) {
        List<LivroFiscalCompetenciaNfseDTO> competencias = Lists.newArrayList();
        String sql = " SELECT " +
                "       EXTRACT(YEAR FROM DEC.COMPETENCIA) AS EXERCICIO, " +
                "       EXTRACT(MONTH FROM DEC.COMPETENCIA) AS MES, " +
                "       COUNT(1) AS QUANTIDADE, " +
                "       SUM(CASE  " +
                "              WHEN COALESCE(DEC.ISSRETIDO, 0) = 0 THEN DEC.ISSCALCULADO " +
                "              ELSE 0 " +
                "           END) AS ISSQN_PROPRIO, " +
                "       SUM(CASE  " +
                "              WHEN DEC.ISSRETIDO = 1 THEN DEC.ISSCALCULADO " +
                "              ELSE 0  " +
                "           END) AS ISSQN_RETIDO, " +
                "       SUM(CASE  " +
                "              WHEN DEC.ISSRETIDO = 1 AND DEC.SITUACAO = :PAGA THEN DEC.ISSCALCULADO  " +
                "              ELSE 0 " +
                "           END) AS ISSQN_PAGO," +
                "       SUM(DEC.TOTALSERVICOS) AS VALOR_SERVICO " +
                "   FROM DECLARACAOPRESTACAOSERVICO DEC " +
                "  LEFT JOIN NOTAFISCAL NF ON NF.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID  " +
                "  LEFT JOIN SERVICODECLARADO SD ON SD.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID AND SD.TIPOSERVICODECLARADO = :TIPO_SERVICO_TOMADO " +
                "  LEFT JOIN DADOSPESSOAISNFSE DPT ON DPT.ID = DEC.DADOSPESSOAISTOMADOR_ID " +
                "  LEFT JOIN PESSOAJURIDICA PJT ON PJT.CNPJ = DPT.CPFCNPJ " +
                "  INNER JOIN CADASTROECONOMICO CE ON CE.PESSOA_ID = PJT.ID " +
                " WHERE (NF.ID IS NOT NULL OR SD.ID IS NOT NULL) " +
                "   AND CE.ID = :PRESTADOR_ID  " +
                "   AND DEC.SITUACAO != :CANCELADA " +
                "   AND DEC.COMPETENCIA BETWEEN :COMPETENCIA_INICIAL AND :COMPETENCIA_FINAL " +
                " GROUP BY EXTRACT(YEAR FROM DEC.COMPETENCIA), EXTRACT(MONTH FROM DEC.COMPETENCIA) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("PRESTADOR_ID", prestadorId);
        q.setParameter("COMPETENCIA_INICIAL", competenciaInicial);
        q.setParameter("COMPETENCIA_FINAL", competenciaFinal);
        q.setParameter("CANCELADA", SituacaoNota.CANCELADA.name());
        q.setParameter("PAGA", SituacaoNota.PAGA.name());
        q.setParameter("TIPO_SERVICO_TOMADO", TipoServicoDeclarado.SERVICO_TOMADO.name());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                LivroFiscalCompetenciaNfseDTO competencia = new LivroFiscalCompetenciaNfseDTO();
                competencia.setPrestadorId(prestadorId);
                competencia.setTipoMovimento(TipoMovimentoMensal.RETENCAO.toDto());
                competencia.setExercicio(((Number) obj[0]).intValue());
                competencia.setMes(((Number) obj[1]).intValue());
                competencia.setQuantidade(((BigDecimal) obj[2]).intValue());
                competencia.setIssqnProprio(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
                competencia.setIssqnRetido(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
                competencia.setIssqnPago(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
                competencia.setValorServico(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
                competencia.setSaldoIssqn(competencia.getIssqnProprio().subtract(competencia.getIssqnPago()));
                competencias.add(competencia);
            }
        }
        return competencias;
    }

    private List<LivroFiscalCompetenciaNfseDTO> buscarCompetenciasLivroFiscalComMovimentoDesif(Long prestadorId,
                                                                                               Date competenciaInicial,
                                                                                               Date competenciaFinal) {
        List<LivroFiscalCompetenciaNfseDTO> competencias = Lists.newArrayList();
        String sql = " SELECT " +
                "    E.ANO AS EXERCICIO, " +
                "    FUNCMESTONUMERO(DMS.MES) AS MES, " +
                "    COUNT(DISTINCT IDS.ID) AS QUANTIDADE, " +
                "    SUM(IDS.ISS) AS ISSQN_PROPRIO, " +
                "    SUM(CASE " +
                "            WHEN DEC.SITUACAO = :PAGA THEN IDS.ISS " +
                "            ELSE 0 " +
                "        END) AS ISSQN_PAGO, " +
                "    SUM(IDS.VALORSERVICO) AS VALOR_SERVICO " +
                "   FROM DECLARACAOMENSALSERVICO DMS " +
                "  INNER JOIN EXERCICIO E ON E.ID = DMS.EXERCICIO_ID " +
                "  INNER JOIN NOTADECLARADA ND ON ND.DECLARACAOMENSALSERVICO_ID = DMS.ID " +
                "  INNER JOIN DECLARACAOPRESTACAOSERVICO DEC ON DEC.ID = ND.DECLARACAOPRESTACAOSERVICO_ID " +
                "  INNER JOIN ITEMDECLARACAOSERVICO IDS ON IDS.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID " +
                "  INNER JOIN CADASTROECONOMICO CE ON CE.ID = DMS.PRESTADOR_ID " +
                "WHERE CE.ID = :PRESTADOR_ID " +
                "  AND DMS.SITUACAO != :CANCELADO " +
                "  AND DMS.TIPOMOVIMENTO = :INSTITUICAO_FINANCEIRA " +
                "  AND TO_DATE('01/'||LPAD(FUNCMESTONUMERO(DMS.MES), 2, 0)||'/'||E.ANO, 'dd/MM/yyyy') BETWEEN :COMPETENCIA_INICIAL AND :COMPETENCIA_FINAL " +
                "GROUP BY E.ANO, FUNCMESTONUMERO(DMS.MES) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("PRESTADOR_ID", prestadorId);
        q.setParameter("CANCELADO", DeclaracaoMensalServico.Situacao.CANCELADO.name());
        q.setParameter("INSTITUICAO_FINANCEIRA", TipoMovimentoMensal.INSTITUICAO_FINANCEIRA.name());
        q.setParameter("COMPETENCIA_INICIAL", competenciaInicial);
        q.setParameter("COMPETENCIA_FINAL", competenciaFinal);
        q.setParameter("PAGA", SituacaoNota.PAGA.name());

        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                LivroFiscalCompetenciaNfseDTO competencia = new LivroFiscalCompetenciaNfseDTO();
                competencia.setPrestadorId(prestadorId);
                competencia.setTipoMovimento(TipoMovimentoMensal.INSTITUICAO_FINANCEIRA.toDto());
                competencia.setExercicio(((Number) obj[0]).intValue());
                competencia.setMes(((Number) obj[1]).intValue());
                competencia.setQuantidade(((BigDecimal) obj[2]).intValue());
                competencia.setIssqnProprio(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
                competencia.setIssqnRetido(BigDecimal.ZERO);
                competencia.setIssqnPago(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
                competencia.setValorServico(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
                competencia.setSaldoIssqn(competencia.getIssqnProprio().subtract(competencia.getIssqnPago()));
                competencias.add(competencia);
            }
        }
        return competencias;
    }

    private LivroFiscalCompetenciaNfseDTO criarCompetenciaLivroFiscalDefault(Long prestadorId,
                                                                             Date competencia,
                                                                             TipoMovimentoMensalNfseDTO tipoMovimento) {
        Integer exercicio = DataUtil.getAno(competencia);
        Integer mes = DataUtil.getMes(competencia);

        LivroFiscalCompetenciaNfseDTO competenciaDefault = new LivroFiscalCompetenciaNfseDTO();
        competenciaDefault.setPrestadorId(prestadorId);
        competenciaDefault.setExercicio(exercicio);
        competenciaDefault.setMes(mes);
        competenciaDefault.setTipoMovimento(tipoMovimento);
        competenciaDefault.setIssqnProprio(BigDecimal.ZERO);
        competenciaDefault.setIssqnRetido(BigDecimal.ZERO);
        competenciaDefault.setIssqnPago(BigDecimal.ZERO);
        return competenciaDefault;
    }

    public boolean hasCompetenciaNoLivroFiscal(Date competencia, List<LivroFiscalCompetenciaNfseDTO> competencias) {
        if (competencias == null || competencias.isEmpty()) {
            return false;
        }
        Integer exercicio = DataUtil.getAno(competencia);
        Integer mes = DataUtil.getMes(competencia);
        for (LivroFiscalCompetenciaNfseDTO competenciaExtratoContaCorrenteNfseDTO : competencias) {
            if (competenciaExtratoContaCorrenteNfseDTO.getExercicio().equals(exercicio) &&
                    competenciaExtratoContaCorrenteNfseDTO.getMes().equals(mes)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void depoisDePagar(Calculo calculo) {
        List<DeclaracaoMensalServico> declaracoes = buscarDeclaracaoMensalServicoPorCalculo(calculo);
        for (DeclaracaoMensalServico dms : declaracoes) {
            for (NotaDeclarada nota : dms.getNotas()) {
                if (nota.getDeclaracaoPrestacaoServico().getIssRetido() &&
                        TipoMovimentoMensal.NORMAL.equals(dms.getTipoMovimento())) {
                    continue;
                }
                if (SituacaoNota.EMITIDA.equals(nota.getDeclaracaoPrestacaoServico().getSituacao())) {
                    DeclaracaoPrestacaoServico declaracaoPrestacaoServico = nota.getDeclaracaoPrestacaoServico();
                    declaracaoPrestacaoServico.setSituacao(SituacaoNota.PAGA);
                    em.merge(declaracaoPrestacaoServico);
                    notaFiscalFacade.removerNaNota(declaracaoPrestacaoServico);
                }
            }
            dms.setSituacao(DeclaracaoMensalServico.Situacao.ENCERRADO);
            em.merge(dms);
        }

    }

    public boolean hasDeclaracaoMensalServico(Long idDeclaracaoPrestacaoServico) {
        Query q = em.createNativeQuery(" select 1 from declaracaomensalservico dms " +
                "  inner join notadeclarada nd on nd.declaracaomensalservico_id = dms.id " +
                "  where dms.situacao != :situacao and nd.declaracaoprestacaoservico_id =:id_declaracao ");
        q.setParameter("situacao", DeclaracaoMensalServico.Situacao.CANCELADO.name());
        q.setParameter("id_declaracao", idDeclaracaoPrestacaoServico);
        return !q.getResultList().isEmpty();
    }

    public List<DeclaracaoMensalServico> buscarDeclaracaoMensalServicoPorCalculo(Calculo calculo) {
        Query q = em.createNativeQuery("select dec.* " +
                " from DeclaracaoMensalServico dec " +
                " inner join exercicio ex on ex.id = dec.exercicio_id " +
                " left join processocalculo proc on proc.id = dec.processocalculo_id" +
                " left join calculo on calculo.processocalculo_id = proc.id " +
                " where calculo.id = :calculoId", DeclaracaoMensalServico.class);

        q.setParameter("calculoId", calculo.getId());
        return q.getResultList();
    }

    public LoteDeclaracaoMensalServico salvarLote(LoteDeclaracaoMensalServico loteDeclaracaoMensalServico) {
        return em.merge(loteDeclaracaoMensalServico);
    }

    public List<DeclaracaoMensalServico> buscarDMSdaNota(Long idNota) {
        String sql = "select dms.* " +
                "from DECLARACAOMENSALSERVICO dms " +
                "         inner join NOTADECLARADA nd " +
                "                    on dms.ID = nd.DECLARACAOMENSALSERVICO_ID " +
                "         inner join DECLARACAOPRESTACAOSERVICO " +
                "                    on nd.DECLARACAOPRESTACAOSERVICO_ID = DECLARACAOPRESTACAOSERVICO.ID " +
                "         inner join notafiscal on DECLARACAOPRESTACAOSERVICO.ID = NOTAFISCAL.DECLARACAOPRESTACAOSERVICO_ID " +
                "where NOTAFISCAL.ID = :idNota";

        Query q = em.createNativeQuery(sql, DeclaracaoMensalServico.class);
        q.setParameter("idNota", idNota);
        return q.getResultList();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<JasperPrint> gerarLivroFiscal(LivroFiscalCompetenciaNfseDTO dto, CadastroEconomico cadastroEconomico, String usuario) throws JRException {
        ImprimeRelatorioNfse imprimeRelatorioNfse = gerarImprimeRelatorioLivroFiscal(dto, cadastroEconomico);
        String jasper = getJasperLivroFiscal(dto);
        JasperPrint jasperPrint = imprimeRelatorioNfse.
                gerarJasperPrintPdf(jasper, Lists.newArrayList(dto), usuario);
        return new AsyncResult<>(jasperPrint);
    }

    public String getJasperLivroFiscal(LivroFiscalCompetenciaNfseDTO dto) {
        String jasper = "RelatorioLivroFiscal.jasper";
        if (TipoMovimentoMensalNfseDTO.INSTITUICAO_FINANCEIRA.equals(dto.getTipoMovimento())) {
            jasper = "RelatorioLivroFiscalBanco.jasper";
        }
        return jasper;
    }

    public ImprimeRelatorioNfse gerarImprimeRelatorioLivroFiscal(LivroFiscalCompetenciaNfseDTO dto, CadastroEconomico cadastroEconomico) throws JRException {
        TipoMovimentoMensal tipoMovimentoMensal = TipoMovimentoMensal.valueOf(dto.getTipoMovimento().name());
        if (TipoMovimentoMensal.INSTITUICAO_FINANCEIRA.equals(tipoMovimentoMensal)) {
            dto.setContasDesif(notaFiscalFacade.buscarContasDesif(cadastroEconomico.getId(), dto.getExercicio(),
                    dto.getMes()));
        } else {
            dto.setDocumentos(notaFiscalFacade.buscarNotasPorCompetenciaAndTipo(cadastroEconomico.getId(),
                    dto.getExercicio(), dto.getMes(), tipoMovimentoMensal, null));
            dto.setTotalPorNaturezaSituacao(notaFiscalFacade.agruparPorNaturezaOperacaoAndSituacao(
                    dto.getTotalPorNaturezaSituacao(), dto.getDocumentos()));
        }

        return new ImprimeRelatorioNfse().
                adicionarParametro("DETALHADO", dto.getDetalhado()).
                adicionarParametro("CONTRIBUINTE", cadastroEconomico.getPessoa().getNomeAutoComplete()).
                adicionarParametro("PERIODO", "De: " + dto.getMes() + "/" +
                        dto.getExercicio() + " até: " + dto.getMes() + "/" + dto.getExercicio() + ".");
    }

    public void removerNotaFiscalEncerramento(DeclaracaoMensalServico declaracaoMensalServico,
                                              NotaFiscalSearchDTO notaFiscal) {
        declaracaoMensalServico = recuperar(declaracaoMensalServico.getId());
        NotaDeclarada notaDeclarada = null;
        for (NotaDeclarada item : declaracaoMensalServico.getNotas()) {
            if (item.getDeclaracaoPrestacaoServico().getId().equals(notaFiscal.getId())) {
                notaDeclarada = item;
                break;
            }
        }
        notaFiscalFacade.alterarSituacaoNotaFiscal(notaDeclarada.getDeclaracaoPrestacaoServico(), SituacaoNota.EMITIDA);
        declaracaoMensalServico.getNotas().remove(notaDeclarada);
        em.merge(declaracaoMensalServico);
        atualizarTotaisEncerramento(declaracaoMensalServico);
    }

    private void atualizarTotaisEncerramento(DeclaracaoMensalServico declaracaoMensalServico) {
        declaracaoMensalServico = recuperar(declaracaoMensalServico.getId());
        declaracaoMensalServico.setQtdNotas(0);
        declaracaoMensalServico.setTotalServicos(BigDecimal.ZERO);
        declaracaoMensalServico.setTotalIss(BigDecimal.ZERO);

        for (NotaDeclarada notaDeclarada : declaracaoMensalServico.getNotas()) {
            if (!notaDeclarada.getDeclaracaoPrestacaoServico().getIssRetido() ||
                    !TipoMovimentoMensal.NORMAL.equals(declaracaoMensalServico.getTipoMovimento())) {
                declaracaoMensalServico.setQtdNotas(declaracaoMensalServico.getQtdNotas() + 1);
                declaracaoMensalServico.setTotalServicos(declaracaoMensalServico.getTotalServicos()
                        .add(notaDeclarada.getDeclaracaoPrestacaoServico().getTotalServicos()));
                declaracaoMensalServico.setTotalIss(declaracaoMensalServico.getTotalIss()
                        .add(notaDeclarada.getDeclaracaoPrestacaoServico().getIssCalculado()));
            }
        }
        em.merge(declaracaoMensalServico);
    }

    public void adicionarNotasFiscais(DeclaracaoMensalServico declaracaoMensalServico,
                                      List<NotaFiscal> notasFiscais) {
        if (declaracaoMensalServico.getNotas() == null) {
            declaracaoMensalServico.setNotas(new ArrayList());
        }
        if (notasFiscais != null && !notasFiscais.isEmpty()) {
            Boolean dmsPaga = isDeclaracaoMensalServicoPaga(declaracaoMensalServico);
            for (NotaFiscal notaFiscal : notasFiscais) {
                NotaDeclarada notaDeclarada = new NotaDeclarada();
                notaDeclarada.setDeclaracaoMensalServico(declaracaoMensalServico);
                notaDeclarada.setDeclaracaoPrestacaoServico(notaFiscal.getDeclaracaoPrestacaoServico());
                declaracaoMensalServico.getNotas().add(notaDeclarada);
                if (dmsPaga) {
                    notaFiscalFacade.alterarSituacaoNotaFiscal(notaDeclarada.getDeclaracaoPrestacaoServico(), SituacaoNota.PAGA);
                }
            }
            em.merge(declaracaoMensalServico);
            atualizarTotaisEncerramento(declaracaoMensalServico);
        }
    }

    private Boolean isDeclaracaoMensalServicoPaga(DeclaracaoMensalServico declaracaoMensalServico) {
        List<ResultadoParcela> parcelas = recuperarDebitosDaDecalaracao(declaracaoMensalServico.getId(), declaracaoMensalServico.getPrestador().getId());
        if (parcelas != null && parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isPago()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<ResultadoParcela> buscarDebitosReferenteEncerramento(DeclaracaoMensalServico declaracaoMensalServico) {
        ConfiguracaoNfse configuracaoNfse = configuracaoNfseFacade.recuperarConfiguracao();
        ConfiguracaoNfseDivida configuracaoNfseDivida = configuracaoNfse.buscarConfiguracaoDivida(declaracaoMensalServico.getTipoMovimento(), declaracaoMensalServico.getTipo());
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addComplementoJoin(" inner join calculo c on c.id = vw.calculo_id " +
                " inner join processocalculoiss pciss on pciss.id = c.processocalculo_id ");
        consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, declaracaoMensalServico.getExercicio().getAno());
        consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, configuracaoNfseDivida.getDividaNfse().getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, declaracaoMensalServico.getPrestador().getId());
        consultaParcela.addComplementoDoWhere(" and pciss.mesreferencia = " + declaracaoMensalServico.getMes().getNumeroMes());
        return consultaParcela.executaConsulta().getResultados();
    }

    public void removerDebitoEncerramento(DeclaracaoMensalServico declaracaoMensalServico) {
        declaracaoMensalServico.setProcessoCalculo(null);
        em.merge(declaracaoMensalServico);
    }

    public void vincularDebitoEncerramento(DeclaracaoMensalServico declaracaoMensalServico, ResultadoParcela resultadoParcela) {
        ParcelaValorDivida parcelaValorDivida = em.find(ParcelaValorDivida.class, resultadoParcela.getIdParcela());
        declaracaoMensalServico.setProcessoCalculo(parcelaValorDivida.getValorDivida().getCalculo().getProcessoCalculo());
        em.merge(declaracaoMensalServico);
    }

    public BigDecimal buscarValorDeclarado(CadastroEconomico cadastroEconomico, TipoMovimentoMensal tipoMovimentoMensal,
                                           Integer ano, Integer mes) {
        String sql = " select " +
            "     sum(dms.totalservicos) as valor_declarado_nfse " +
            "    from declaracaomensalservico dms " +
            "   inner join exercicio e on e.id = dms.exercicio_id " +
            " where dms.situacao != :cancelada " +
            "   and dms.tipomovimento = :tipo_movimento" +
            "   and dms.prestador_id = :prestador " +
            "   and e.ano = :ano " +
            "   and dms.mes = :mes ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cancelada", DeclaracaoMensalServico.Situacao.CANCELADO.name());
        q.setParameter("tipo_movimento", tipoMovimentoMensal.name());
        q.setParameter("prestador", cadastroEconomico.getId());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (BigDecimal) resultList.get(0);
        }
        return BigDecimal.ZERO;
    }

    public boolean hasDebitoDiferenteEmAberto(DeclaracaoMensalServico declaracaoMensalServico) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.PARCELA_SITUACAO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.DIFERENTE,
            SituacaoParcela.EM_ABERTO.name());
        consultaParcela.addComplementoDoWhere(" and vw.calculo_id in (select c.id " +
            " from declaracaomensalservico d " +
            " inner join processocalculo pc on pc.id = d.processocalculo_id " +
            " inner join calculo c on c.processocalculo_id = pc.id " +
            " where d.id = " + declaracaoMensalServico.getId() + ") ");
        return !consultaParcela.executaConsulta().getResultados().isEmpty();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<List<RBT12NfseDTO>> buscarRegistrosRBT12(Long prestadorId,
                                                           Date competenciaInicial,
                                                           Date competenciaFinal) {
        List<RBT12NfseDTO> registros = Lists.newArrayList();

        String sql = " with competencias as ( " +
                "    select to_date(to_char(add_months(:competencia_final,  - level + 1), 'MMyyyy'), 'MMyyyy') as competencia " +
                "       from dual " +
                "    connect by level <= round(months_between(:competencia_final, :competencia_inicial))), " +
                "    valores as (select " +
                "                      to_date(to_char(dec.competencia, 'MMyyyy'), 'MMyyyy') as competencia, " +
                "                      count(1) as total_notas, " +
                "                      sum(dec.totalservicos) as total_servicos, " +
                "                      sum(case " +
                "                              when dec.issretido != 1 then dec.isscalculado " +
                "                              else 0 " +
                "                          end) as issqn_proprio, " +
                "                      sum(case " +
                "                              when dec.issretido = 1 then dec.isscalculado " +
                "                              else 0 " +
                "                          end) as issqn_retido " +
            "                  from notafiscal nf " +
            "                 inner join declaracaoprestacaoservico dec on dec.id = nf.declaracaoprestacaoservico_id " +
            "                 inner join cadastroeconomico ce on ce.id = nf.prestador_id " +
            "                where dec.situacao != :cancelada " +
            "                  and ce.id = :prestador_id " +
                "                group by to_date(to_char(dec.competencia, 'MMyyyy'), 'MMyyyy')) " +
                " select extract(year from c.competencia) as exercicio, " +
                "        extract(month from c.competencia) as mes, " +
                "        coalesce(valores.total_notas, 0) as total_notas, " +
                "        coalesce(valores.total_servicos, 0) as total_servicos, " +
                "        coalesce(valores.issqn_proprio, 0) as issqn_proprio, " +
                "        coalesce(valores.issqn_retido, 0) as issqn_retido, " +
            "            func_rbt12(c.competencia, :prestador_id) as rbt12 " +
                "    from competencias c " +
                "   left join valores on valores.competencia = c.competencia " +
                " order by c.competencia ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("prestador_id", prestadorId);
        q.setParameter("competencia_inicial", competenciaInicial);
        q.setParameter("competencia_final", competenciaFinal);
        q.setParameter("cancelada", SituacaoNota.CANCELADA.name());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RBT12NfseDTO rbt12 = new RBT12NfseDTO();
                rbt12.setPrestadorId(prestadorId);
                rbt12.setExercicio(((Number) obj[0]).intValue());
                rbt12.setMes(((Number) obj[1]).intValue());
                rbt12.setTotalNotas(((BigDecimal) obj[2]).intValue());
                rbt12.setTotalServicos(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
                rbt12.setIssqnProprio(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
                rbt12.setIssqnRetido(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
                rbt12.setRbt12(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
                registros.add(rbt12);
            }
        }

        return new AsyncResult<>(registros);
    }

}
