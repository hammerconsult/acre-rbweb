/*
 * Codigo gerado automaticamente em Tue Nov 29 17:31:17 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.LiquidacaoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DocumentoFiscalIntegracao;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentariaDetalhamento;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentariaDocumentoComprobatorio;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reinf.NaturezaRendimentoReinfFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.webreportdto.dto.contabil.TipoEmpenhoDTO;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class LiquidacaoFacade extends SuperFacadeContabil<Liquidacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private LiquidacaoEstornoFacade liquidacaoEstornoFacade;
    @EJB
    private ContaFacade contaDespesaDesdobradaFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private PagamentoEstornoFacade pagamentoEstornoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private UFFacade ufFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigLiquidacaoFacade configLiquidacaoFacade;
    @EJB
    private ConfigLiquidacaoResPagarFacade configLiquidacaoResPagarFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private DoctoFiscalLiquidacaoFacade doctoFiscalLiquidacaoFacade;
    @EJB
    private ConfigTipoViagemContaDespesaFacade configTipoViagemContaDespesaFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private SaldoGrupoMaterialFacade saldoGrupoMaterialFacade;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemFacade;
    @EJB
    private SaldoGrupoBemImovelFacade saldoGrupoBemImovelFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private ObrigacaoAPagarFacade obrigacaoAPagarFacade;
    @EJB
    private DesdobramentoObrigacaoAPagarFacade desdobramentoObrigacaoAPagarFacade;
    @EJB
    private DesdobramentoEmpenhoFacade desdobramentoEmpenhoFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private ConfigContaDespesaTipoDocumentoFacade configContaDespesaTipoDocumentoFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    @EJB
    private BensEstoqueFacade bensEstoqueFacade;
    @EJB
    private NaturezaRendimentoReinfFacade naturezaRendimentoReinfFacade;
    @EJB
    private IntegradorDocumentoFiscalLiquidacaoFacade integradorDocumentoFiscalLiquidacaoFacade;
    @EJB
    private ConvenioDespesaFacade convenioDespesaFacade;
    @EJB
    private ConfiguracaoExcecaoDespesaContratoFacade configuracaoExcecaoDespesaContratoFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private TransfBensMoveisFacade transfBensMoveisFacade;
    @EJB
    private TransferenciaBensEstoqueFacade transferenciaBensEstoqueFacade;

    public LiquidacaoFacade() {
        super(Liquidacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public ConvenioDespesaFacade getConvenioDespesaFacade() {
        return convenioDespesaFacade;
    }

    @Override
    public Liquidacao recuperar(Object id) {
        Liquidacao entity = em.find(Liquidacao.class, id);
        entity.getDesdobramentos().size();
        entity.getDoctoFiscais().size();
        for (LiquidacaoDoctoFiscal liqDoctoFiscal : entity.getDoctoFiscais()) {
            liqDoctoFiscal.getTransferenciasBens().size();
        }
        entity.getLiquidacaoEstornos().size();
        entity.getObrigacoesPagar().size();
        entity.getPagamentos().size();
        if (!entity.getObrigacoesPagar().isEmpty()) {
            for (LiquidacaoObrigacaoPagar liqOp : entity.getObrigacoesPagar()) {
                liqOp.getObrigacaoAPagar().getDesdobramentos().size();
                liqOp.getObrigacaoAPagar().getDocumentosFiscais().size();
            }
        }
        entity.getEmpenho().getEmpenhoEstornos().size();
        entity.getEmpenho().getDesdobramentos().size();
        return entity;
    }

    @Override
    public Liquidacao recuperar(Class classe, Object id) {
        Liquidacao entity = em.find(Liquidacao.class, id);
        entity.getDesdobramentos().size();
        entity.getDoctoFiscais().size();
        entity.getLiquidacaoEstornos().size();
        if (!entity.getObrigacoesPagar().isEmpty()) {
            for (LiquidacaoObrigacaoPagar liqOp : entity.getObrigacoesPagar()) {
                liqOp.getObrigacaoAPagar().getDesdobramentos().size();
                liqOp.getObrigacaoAPagar().getDocumentosFiscais().size();
            }
        }
        entity.getPagamentos().size();
        return entity;
    }

    public void recuperarTransferenciasLiquidacaoDoctoFiscal(Liquidacao entity) {
        entity.getDoctoFiscais().forEach(ldf -> {
            ldf.getTransferenciasBens().forEach(transferencia -> {
                switch (transferencia.getTipoContaDespesa()) {
                    case BEM_MOVEL:
                        transferencia.setTransfBensMoveis(em.find(TransfBensMoveis.class, transferencia.getIdTransferencia()));
                        break;

                    case BEM_ESTOQUE:
                        transferencia.setTransferenciaBensEstoque(em.find(TransferenciaBensEstoque.class, transferencia.getIdTransferencia()));
                        break;
                }
            });
        });
    }

    public List<Liquidacao> buscarPorPessoa(String parte, Integer ano, UnidadeOrganizacional uo) {
        String sql = "SELECT * FROM(SELECT L.* FROM LIQUIDACAO L "
            + " INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID "
            + " INNER JOIN PESSOAFISICA PF ON PF.ID = E.FORNECEDOR_ID "
            + " AND ((LOWER(PF.NOME) LIKE :PARAM) OR (PF.CPF LIKE :PARAM) OR (E.NUMERO LIKE :PARAM) OR L.NUMERO LIKE :PARAM) "
            + " WHERE L.SALDO > 0 AND extract(year from E.DATAEMPENHO) = :ANO AND L.CATEGORIAORCAMENTARIA='NORMAL' AND L.UNIDADEORGANIZACIONAL_ID = :uo AND  ROWNUM <=10 "
            + " UNION ALL "
            + " SELECT L.* FROM LIQUIDACAO L "
            + " INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID "
            + " INNER JOIN PESSOAJURIDICA PJ ON PJ.ID = E.FORNECEDOR_ID "
            + " AND ((LOWER(PJ.NOMEFANTASIA) LIKE :PARAM) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :PARAM) OR (PJ.CNPJ LIKE :PARAM) OR L.NUMERO LIKE :PARAM)"
            + " WHERE L.SALDO > 0 AND extract(year from E.DATAEMPENHO) = :ANO AND L.CATEGORIAORCAMENTARIA='NORMAL' AND L.UNIDADEORGANIZACIONAL_ID = :uo AND ROWNUM <=10 ) X"
            + " ORDER BY X.ID DESC";
        Query q = em.createNativeQuery(sql, Liquidacao.class);
        q.setParameter("PARAM", "%" + parte.toLowerCase() + "%");
        q.setParameter("ANO", ano);
        q.setParameter("uo", uo.getId());
        q.setMaxResults(30);
        return q.getResultList();
    }

    public List<Liquidacao> buscarFiltrandoLiquidacaRestoPagarPorPessoa(String parte, Integer ano, UnidadeOrganizacional uo) {
        String sql = "SELECT * FROM(SELECT L.* FROM LIQUIDACAO L "
            + " INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID "
            + " INNER JOIN PESSOAFISICA PF ON PF.ID = E.FORNECEDOR_ID "
            + " WHERE L.SALDO > 0 "
            + "  AND extract(year from E.DATAEMPENHO) = :ANO "
            + "  AND L.CATEGORIAORCAMENTARIA = 'RESTO' "
            + "  AND L.UNIDADEORGANIZACIONAL_ID = :uo "
            + "  AND ((LOWER(PF.NOME) LIKE :PARAM) OR (PF.CPF LIKE :PARAM) OR (E.NUMERO LIKE :PARAM or e.numerooriginal like :PARAM ) "
            + "   OR (L.NUMERO LIKE :PARAM or l.numerooriginal like :PARAM)) "
            + " UNION ALL "
            + " SELECT L.* FROM LIQUIDACAO L "
            + " INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID "
            + " INNER JOIN PESSOAJURIDICA PJ ON PJ.ID = E.FORNECEDOR_ID "
            + " WHERE L.SALDO > 0 "
            + "  AND extract(year from E.DATAEMPENHO) = :ANO "
            + "  AND L.CATEGORIAORCAMENTARIA='RESTO' "
            + "  AND ((LOWER(PJ.NOMEFANTASIA) LIKE :PARAM) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :PARAM) OR (PJ.CNPJ LIKE :PARAM) "
            + "   OR (L.NUMERO LIKE :PARAM or l.numerooriginal like :PARAM) )"
            + "  AND L.UNIDADEORGANIZACIONAL_ID = :uo ) X"
            + " ORDER BY X.ID DESC";
        Query q = em.createNativeQuery(sql, Liquidacao.class);
        q.setParameter("PARAM", "%" + parte.toLowerCase() + "%");
        q.setParameter("ANO", ano);
        q.setParameter("uo", uo.getId());
        return q.getResultList();
    }

    public List<Liquidacao> buscarLiquidacaRestoPagarPorPessoaNaoTransportada(String parte, Integer ano, UnidadeOrganizacional uo) {
        String sql = "SELECT * FROM(SELECT L.* FROM LIQUIDACAO L "
            + " INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID "
            + " INNER JOIN PESSOAFISICA PF ON PF.ID = E.FORNECEDOR_ID "
            + " WHERE L.SALDO > 0 "
            + "  AND extract(year from E.DATAEMPENHO) = :ANO "
            + "  AND L.CATEGORIAORCAMENTARIA = '" + CategoriaOrcamentaria.RESTO.name() + "' "
            + "  AND L.UNIDADEORGANIZACIONAL_ID = :uo "
            + "  AND ((LOWER(PF.NOME) LIKE :PARAM) OR (PF.CPF LIKE :PARAM) OR (E.NUMERO LIKE :PARAM or e.numerooriginal like :PARAM ) "
            + "   OR (L.NUMERO LIKE :PARAM or l.numerooriginal like :PARAM)) "
            + " UNION ALL "
            + " SELECT L.* FROM LIQUIDACAO L "
            + " INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID "
            + " INNER JOIN PESSOAJURIDICA PJ ON PJ.ID = E.FORNECEDOR_ID "
            + " WHERE L.SALDO > 0 "
            + "  AND extract(year from E.DATAEMPENHO) = :ANO "
            + "  AND L.CATEGORIAORCAMENTARIA='" + CategoriaOrcamentaria.RESTO.name() + "' "
            + "  AND ((LOWER(PJ.NOMEFANTASIA) LIKE :PARAM) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :PARAM) OR (PJ.CNPJ LIKE :PARAM) "
            + "   OR (L.NUMERO LIKE :PARAM or l.numerooriginal like :PARAM) )"
            + "  AND L.UNIDADEORGANIZACIONAL_ID = :uo ) X"
            + " ORDER BY X.ID DESC";
        Query q = em.createNativeQuery(sql, Liquidacao.class);
        q.setParameter("PARAM", "%" + parte.toLowerCase() + "%");
        q.setParameter("ANO", ano);
        q.setParameter("uo", uo.getId());
        q.setMaxResults(30);
        return q.getResultList();
    }

    public String retornaUltimoNumeroLiquidacao(UnidadeOrganizacional uni) {
        String hql = "select li from Liquidacao li "
            + " inner join li.empenho e"
            + " where li.categoriaOrcamentaria = :categoria "
            + "   and e.unidadeOrganizacional = :uni "
            + " order by to_number(li.numero) desc";

        Query q = em.createQuery(hql);
        q.setParameter("categoria", CategoriaOrcamentaria.NORMAL);
        q.setParameter("uni", uni);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return ((Liquidacao) q.getSingleResult()).getNumero();
        } else {
            return "0";
        }
    }

    public Liquidacao salvarRetornando(Liquidacao entity) {
        return em.merge(entity);
    }

    public void geraNumeroLiquidacao(Liquidacao entity) {
        entity.setNumero(singletonGeradorCodigoContabil.getNumeroLiquidacao(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataLiquidacao()));
    }

    private void gerarMovimentoDespesaORC(Liquidacao entity) {
        if (entity.isLiquidacaoNormal()) {
            MovimentoDespesaORC mdorc = gerarMovimentoDespesaOrc(entity, OperacaoORC.LIQUIDACAO);
            entity.setMovimentoDespesaORC(mdorc);
        }
    }

    private MovimentoDespesaORC gerarMovimentoDespesaOrc(Liquidacao entity, OperacaoORC operacaoORC) {
        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
            entity.getEmpenho().getFonteDespesaORC(),
            operacaoORC,
            TipoOperacaoORC.NORMAL,
            entity.getValor(),
            entity.getDataLiquidacao(),
            entity.getUnidadeOrganizacional(),
            entity.getId().toString(),
            entity.getClass().getSimpleName(),
            entity.getNumero(),
            entity.getHistoricoRazao());
        return saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
    }

    private void gerarSaldoGrupoPatrimonialAndGrupoMaterial(Liquidacao liquidacao) {
        Empenho empenho = liquidacao.getEmpenho();
        switch (empenho.getTipoContaDespesa()) {
            case BEM_MOVEL:
                gerarSaldoBensMoveis(liquidacao);
                break;
            case BEM_IMOVEL:
                gerarSaldoBensImoveis(liquidacao);
                break;
            case BEM_ESTOQUE:
                gerarSaldoBensEstoque(liquidacao);
                break;
        }
    }

    private void gerarSaldoBensMoveis(Liquidacao liquidacao) {

        for (Desdobramento desdobramento : liquidacao.getDesdobramentos()) {
            List<HashMap> gruposBens = grupoBemFacade.recuperaGrupoBemETipoGrupoBemPorContaDespesa(desdobramento.getConta(), liquidacao.getDataLiquidacao());
            for (HashMap has : gruposBens) {
                GrupoBem grupoBem = (GrupoBem) has.get(1);
                TipoGrupo tipoGrupoBem = (TipoGrupo) has.get(2);
                saldoGrupoBemFacade.geraSaldoGrupoBemMoveis(
                    liquidacao.getUnidadeOrganizacional(),
                    grupoBem,
                    desdobramento.getValor(),
                    tipoGrupoBem,
                    liquidacao.getDataLiquidacao(),
                    TipoOperacaoBensMoveis.AQUISICAO_BENS_MOVEIS,
                    TipoLancamento.NORMAL,
                    TipoOperacao.DEBITO,
                    true);
            }
        }
    }

    private void gerarSaldoBensImoveis(Liquidacao liquidacao) {
        for (Desdobramento desdobramento : liquidacao.getDesdobramentos()) {
            List<HashMap> gruposBens = grupoBemFacade.recuperaGrupoBemETipoGrupoBemPorContaDespesa(desdobramento.getConta(), liquidacao.getDataLiquidacao());
            for (HashMap has : gruposBens) {
                GrupoBem grupoBem = (GrupoBem) has.get(1);
                TipoGrupo tipoGrupoBem = (TipoGrupo) has.get(2);
                saldoGrupoBemImovelFacade.geraSaldoGrupoBemImoveis(
                    liquidacao.getUnidadeOrganizacional(),
                    grupoBem,
                    desdobramento.getValor(),
                    tipoGrupoBem,
                    liquidacao.getDataLiquidacao(),
                    TipoOperacaoBensImoveis.AQUISICAO_BENS_IMOVEIS,
                    TipoLancamento.NORMAL,
                    TipoOperacao.DEBITO,
                    true);
            }
        }
    }


    private void gerarSaldoBensEstoque(Liquidacao liquidacao) {
        for (Desdobramento desdobramento : liquidacao.getDesdobramentos()) {
            List<HashMap> gruposBens = grupoMaterialFacade.recuperaGrupoMateriaETipoEstoquePorContaDespesa(desdobramento.getConta(), liquidacao.getDataLiquidacao());
            for (HashMap has : gruposBens) {
                GrupoMaterial grupoMaterial = (GrupoMaterial) has.get(1);
                TipoEstoque tipoEstoque = (TipoEstoque) has.get(2);
                saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
                    liquidacao.getUnidadeOrganizacional(),
                    grupoMaterial,
                    desdobramento.getValor(),
                    tipoEstoque,
                    liquidacao.getDataLiquidacao(),
                    TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE,
                    TipoLancamento.NORMAL,
                    TipoOperacao.DEBITO,
                    liquidacao.getId(),
                    true);

                gerarBensEstoqueEmpenhoReajustePosExecucao(liquidacao, desdobramento.getValor(), grupoMaterial, tipoEstoque);
            }
        }
    }

    private void gerarBensEstoqueEmpenhoReajustePosExecucao(Liquidacao liquidacao, BigDecimal valor, GrupoMaterial grupoMaterial, TipoEstoque tipoEstoque) {
        if (empenhoFacade.getExecucaoContratoFacade().isEmpenhoReajustePosExecucao(liquidacao.getEmpenho())) {
            BensEstoque bensEstoque = new BensEstoque();
            bensEstoque.setDataBem(liquidacao.getDataLiquidacao());
            bensEstoque.setExercicio(liquidacao.getExercicio());
            bensEstoque.setOperacoesBensEstoque(TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_CONSUMO);
            bensEstoque.setUnidadeOrganizacional(liquidacao.getUnidadeOrganizacional());
            bensEstoque.setTipoBaixaBens(bensEstoqueFacade.getTipoBaixaBensFacade().recuperarTipoIngressoBaixa(TipoBem.ESTOQUE, TipoIngressoBaixa.CONSUMO));
            bensEstoque.setGrupoMaterial(grupoMaterial);
            bensEstoque.setTipoEstoque(tipoEstoque);
            bensEstoque.setTipoLancamento(TipoLancamento.NORMAL);
            bensEstoque.setHistorico(liquidacao.getComplemento());
            bensEstoque.setValor(valor);
            bensEstoqueFacade.salvarBensEstoque(bensEstoque);
        }
    }

    @Override
    public void salvar(Liquidacao entity) {
        entity.gerarHistoricos();
        em.merge(entity);
        empenhoFacade.getPortalTransparenciaNovoFacade().salvarPortal(new LiquidacaoPortal(entity));
    }


    public void salvarEncerramento(List<Liquidacao> liquidacoes, Empenho empenho) {
        for (Liquidacao liqui : liquidacoes) {
            liqui.setEmpenho(empenho);
            liqui.gerarHistoricos();
            em.persist(liqui);
            empenhoFacade.getPortalTransparenciaNovoFacade().salvarPortal(new LiquidacaoPortal(liqui));
        }
    }

    public List<LiquidacaoDoctoFiscal> getDocumentosLiquidados(DoctoFiscalLiquidacao docto) {
        if (docto != null) {
            return buscarDocumentosFiscaisPorDocumentoFiscal(docto);
        }
        return new ArrayList<>();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Liquidacao salvarNovaLiquidacao(Liquidacao entity, List<DocumentoFiscalIntegracao> documentosIntegracao) {
        try {
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                if (empenhoFacade.getSingletonConcorrenciaContabil().isDisponivel(entity.getEmpenho())) {
                    entity.setEmpenho(empenhoFacade.recuperar(entity.getEmpenho().getId()));
                    atualizarDesdobramentoEmpenhoDoDesdobramentoDaLiquidacao(entity);
                    HashMap<Long, List<LiquidacaoDoctoFiscalTransferenciaBens>> mapTransferenciasBensPorIdDoctFiscal = montarMapTransferencias(entity);
                    empenhoFacade.getSingletonConcorrenciaContabil().bloquear(entity.getEmpenho());
                    hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataLiquidacao());
                    gerarNumeroLiquidacao(entity);
                    integradorDocumentoFiscalLiquidacaoFacade.liquidarDocumentoFiscalIntegracao(entity, documentosIntegracao);
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                    gerarMovimentoDespesaORC(entity);
                    movimentarEmpenho(entity);
                    gerarSaldoGrupoPatrimonialAndGrupoMaterial(entity);
                    recuperarTransferenciasNaoSalvasDoMap(entity, mapTransferenciasBensPorIdDoctFiscal);
                    entity = integradorDocumentoFiscalLiquidacaoFacade.gerarTransferenciasDeBensEmGruposDesdobradosDiferentes(entity);
                    contabilizarLiquidacao(entity);
                    empenhoFacade.getPortalTransparenciaNovoFacade().salvarPortal(new LiquidacaoPortal(entity));
                    empenhoFacade.getSingletonConcorrenciaContabil().desbloquear(entity.getEmpenho());
                } else {
                    throw new ExcecaoNegocioGenerica("O Empenho " + entity.getEmpenho() + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente o Empenho.");
                }

            }
            return entity;
        } catch (Exception ex) {
            logger.debug("Erro ao salvar liquidação. " + ex.getMessage());
            Empenho e = entity.getEmpenho();
            e.setSaldo(e.getSaldo().add(entity.getValor()));
            empenhoFacade.getSingletonConcorrenciaContabil().desbloquear(entity.getEmpenho());
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }


    private void atualizarDesdobramentoEmpenhoDoDesdobramentoDaLiquidacao(Liquidacao entity) {
        for (Desdobramento desdobramento : entity.getDesdobramentos()) {
            for (DesdobramentoEmpenho desdobramentoEmpenho : entity.getEmpenho().getDesdobramentos()) {
                if (desdobramento.getDesdobramentoEmpenho().equals(desdobramentoEmpenho)) {
                    desdobramento.setDesdobramentoEmpenho(desdobramentoEmpenho);
                }
            }
        }
    }

    private HashMap<Long, List<LiquidacaoDoctoFiscalTransferenciaBens>> montarMapTransferencias(Liquidacao entity) {
        HashMap<Long, List<LiquidacaoDoctoFiscalTransferenciaBens>> retorno = new HashMap<>();
        if (entity.getEmpenho().getTipoContaDespesa().isBemMovel() || entity.getEmpenho().getTipoContaDespesa().isEstoque()) {
            entity.getDoctoFiscais().forEach(documentoFiscal -> retorno.put(documentoFiscal.getDoctoFiscalLiquidacao().getId(), documentoFiscal.getTransferenciasBens()));
        }
        return retorno;
    }

    private void recuperarTransferenciasNaoSalvasDoMap(Liquidacao entity, HashMap<Long, List<LiquidacaoDoctoFiscalTransferenciaBens>> mapTransferenciasBensPorIdDoctFiscal) {
        if (!mapTransferenciasBensPorIdDoctFiscal.isEmpty()) {
            entity.getDoctoFiscais().forEach(documentoFiscal -> {
                int contador = 0;
                for (LiquidacaoDoctoFiscalTransferenciaBens transferenciaDocumentoFiscal : documentoFiscal.getTransferenciasBens()) {
                    LiquidacaoDoctoFiscalTransferenciaBens transferenciaNaoSalva = mapTransferenciasBensPorIdDoctFiscal.get(documentoFiscal.getDoctoFiscalLiquidacao().getId()).get(contador);
                    transferenciaDocumentoFiscal.setTransfBensMoveis(transferenciaNaoSalva.getTransfBensMoveis());
                    transferenciaDocumentoFiscal.setTransferenciaBensEstoque(transferenciaNaoSalva.getTransferenciaBensEstoque());
                    contador++;
                }
            });
        }
    }

    private Empenho movimentarSaldoObrigacaoPagar(Liquidacao entity, Empenho empenho) {
        if (entity.isLiquidacaoPossuiObrigacoesPagar()) {
            for (LiquidacaoObrigacaoPagar obrigadacaoLiquidacao : entity.getObrigacoesPagar()) {
                ObrigacaoAPagar obrigacaoPagar = obrigacaoAPagarFacade.recuperar(obrigadacaoLiquidacao.getObrigacaoAPagar().getId());
                if (obrigacaoPagar.isObrigacaoPagarAntesEmpenho()) {
                    obrigacaoPagar = movimentarSaldoDesdobramentoObrigacaoPagarAntesEmpenho(entity, empenho, obrigacaoPagar);
                } else {
                    obrigacaoPagar = movimentarSaldoDesdobramentoObrigacaoPagarDepoisEmpenho(entity, empenho, obrigacaoPagar);
                }
                obrigacaoPagar = movimentarSaldoDocumentoFiscalObrigacaoPagar(entity, obrigacaoPagar);
                em.merge(obrigacaoPagar);
            }
        }
        return empenho;
    }

    private ObrigacaoAPagar movimentarSaldoDocumentoFiscalObrigacaoPagar(Liquidacao entity, ObrigacaoAPagar
        obrigacaoPagar) {
        for (LiquidacaoDoctoFiscal doctoLiquidacao : entity.getDoctoFiscais()) {
            for (ObrigacaoAPagarDoctoFiscal doctoObrigacao : obrigacaoPagar.getDocumentosFiscais()) {
                if (doctoLiquidacao.getDoctoFiscalLiquidacao().equals(doctoObrigacao.getDocumentoFiscal())) {
                    doctoObrigacao.setSaldo(doctoObrigacao.getSaldo().subtract(doctoLiquidacao.getValorLiquidado()));
                }
            }
        }
        return obrigacaoPagar;
    }

    private ObrigacaoAPagar movimentarSaldoDesdobramentoObrigacaoPagarAntesEmpenho(Liquidacao entity, Empenho
        empenho, ObrigacaoAPagar obrigacaoPagar) {

        for (Desdobramento desdLiquidacao : entity.getDesdobramentos()) {
            if (desdLiquidacao.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().getEmpenho() == null) {

                for (DesdobramentoEmpenho desdEmpenho : empenho.getDesdobramentos()) {

                    if (desdLiquidacao.getConta().equals(desdEmpenho.getConta())
                        && desdLiquidacao.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoPagar)
                        && desdEmpenho.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoPagar)) {

                        desdEmpenho.setSaldo(desdEmpenho.getSaldo().subtract(desdLiquidacao.getValor()));
                        obrigacaoPagar.setSaldo(obrigacaoPagar.getSaldo().subtract(desdLiquidacao.getValor()));
                        empenho.setSaldoObrigacaoPagarAntesEmp(empenho.getSaldoObrigacaoPagarAntesEmp().subtract(desdLiquidacao.getValor()));
                    }
                }
            }
        }
        return obrigacaoPagar;
    }


    private ObrigacaoAPagar movimentarSaldoDesdobramentoObrigacaoPagarDepoisEmpenho(Liquidacao entity, Empenho
        empenho, ObrigacaoAPagar obrigacaoPagar) {
        for (Desdobramento desdLiquidacao : entity.getDesdobramentos()) {

            for (DesdobramentoObrigacaoPagar desdObrigacao : obrigacaoPagar.getDesdobramentos()) {
                if (desdLiquidacao.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoPagar)
                    && desdLiquidacao.getConta().equals(desdObrigacao.getConta())) {

                    desdObrigacao.setSaldo(desdObrigacao.getSaldo().subtract(desdLiquidacao.getValor()));
                    obrigacaoPagar.setSaldo(obrigacaoPagar.getSaldo().subtract(desdLiquidacao.getValor()));
                    empenho.setSaldoObrigacaoPagarDepoisEmp(empenho.getSaldoObrigacaoPagarDepoisEmp().subtract(desdLiquidacao.getValor()));
                }
            }
        }
        return obrigacaoPagar;
    }

    private void gerarNumeroLiquidacao(Liquidacao entity) {
        entity.setSaldo(entity.getValor());
        geraNumeroLiquidacao(entity);
    }

    private void movimentarEmpenho(Liquidacao entity) {
        Empenho empenho = entity.getEmpenho();
        if (!entity.isLiquidacaoPossuiObrigacoesPagar()
            && entity.getEmpenho().getSaldo().compareTo(entity.getValor()) < 0) {
            throw new ValidacaoException("O movimento já foi salvo por outro usuário! Atualize a página e tente novamente.");
        }
        empenho.setSaldo(empenho.getSaldo().subtract(entity.getValor()));
        empenho = movimentarSaldoObrigacaoPagar(entity, empenho);
        if (!empenho.getDesdobramentos().isEmpty()) {
            for (DesdobramentoEmpenho desdobramentoEmpenho : empenho.getDesdobramentos()) {
                for (Desdobramento desdobramento : entity.getDesdobramentos()) {
                    if (desdobramento.getDesdobramentoEmpenho().equals(desdobramentoEmpenho)) {
                        desdobramentoEmpenho.setSaldo(desdobramentoEmpenho.getSaldo().subtract(desdobramento.getValor()));
                    }
                }
            }
        }
        em.merge(empenho);
    }

    public List<Liquidacao> buscarLiquidacaoNormalPorExercicioRelatorio(String parte, Integer ano) {

        String sql = "SELECT l.* FROM liquidacao l "
            + " inner join empenho e on l.empenho_id = e.id "
            + " inner join pessoa p on e.fornecedor_id = p.id "
            + " left join  pessoafisica pf ON pf.id = p.id "
            + " left join pessoajuridica pj on pj.id = p.id "
            + " where ((lower(pf.nome) LIKE :parte) "
            + " OR (pf.cpf LIKE :parte) "
            + " or (lower(pj.nomefantasia) LIKE :parte) "
            + " OR (lower(pj.nomereduzido) LIKE :parte) "
            + " OR (pj.cnpj LIKE :parte) "
            + " OR (l.numero LIKE :parte)) "
            + " and extract(year from l.dataliquidacao) = :exer "
            + " AND l.categoriaorcamentaria = 'NORMAL'";
        Query q = em.createNativeQuery(sql, Liquidacao.class);
        q.setParameter("exer", ano.toString());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<Liquidacao>();
        } else {
            return lista;
        }
    }

    public List<Liquidacao> listaPorEmpenho(Empenho emp) {
        String sql = "SELECT li.* FROM liquidacao li WHERE li.empenho_id =:emp AND li.saldo != 0";
        Query q = em.createNativeQuery(sql, Liquidacao.class);
        q.setParameter("emp", emp.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<Liquidacao>();
        } else {
            return q.getResultList();
        }
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    public void setConfiguracaoContabilFacade(ConfiguracaoContabilFacade configuracaoContabilFacade) {
        this.configuracaoContabilFacade = configuracaoContabilFacade;
    }

    public ContaFacade getContaDespesaDesdobradaFacade() {
        return contaDespesaDesdobradaFacade;
    }

    public void setContaDespesaDesdobradaFacade(ContaFacade contaDespesaDesdobradaFacade) {
        this.contaDespesaDesdobradaFacade = contaDespesaDesdobradaFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public void setEmpenhoFacade(EmpenhoFacade empenhoFacade) {
        this.empenhoFacade = empenhoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public void setExercicioFacade(ExercicioFacade exercicioFacade) {
        this.exercicioFacade = exercicioFacade;
    }

    public HistoricoContabilFacade getHistoricoContabilFacade() {
        return historicoContabilFacade;
    }

    public void setHistoricoContabilFacade(HistoricoContabilFacade historicoContabilFacade) {
        this.historicoContabilFacade = historicoContabilFacade;
    }

    public LiquidacaoEstornoFacade getLiquidacaoEstornoFacade() {
        return liquidacaoEstornoFacade;
    }

    public void setLiquidacaoEstornoFacade(LiquidacaoEstornoFacade liquidacaoEstornoFacade) {
        this.liquidacaoEstornoFacade = liquidacaoEstornoFacade;
    }

    public PagamentoEstornoFacade getPagamentoEstornoFacade() {
        return pagamentoEstornoFacade;
    }

    public void setPagamentoEstornoFacade(PagamentoEstornoFacade pagamentoEstornoFacade) {
        this.pagamentoEstornoFacade = pagamentoEstornoFacade;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }

    public void setPagamentoFacade(PagamentoFacade pagamentoFacade) {
        this.pagamentoFacade = pagamentoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public TipoDocumentoFiscalFacade getTipoDocumentoFiscalFacade() {
        return tipoDocumentoFiscalFacade;
    }

    public void setTipoDocumentoFiscalFacade(TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade) {
        this.tipoDocumentoFiscalFacade = tipoDocumentoFiscalFacade;
    }

    public UFFacade getUfFacade() {
        return ufFacade;
    }


    public Liquidacao recuperaLiquidacaoConstrutorSimples(Long id) {
        String sql = "select obj from Liquidacao obj where obj.id = :id";

        Query q = em.createQuery(sql);
        q.setParameter("id", id);
        try {
            Liquidacao liquidacao = (Liquidacao) q.getSingleResult();
            liquidacao.setEmpenho(empenhoFacade.recuperaEmpenhoConstrutorSimples(liquidacao.getEmpenho().getId()));
            return liquidacao;
        } catch (NoResultException e) {
            return null;
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfigLiquidacaoFacade getConfigLiquidacaoFacade() {
        return configLiquidacaoFacade;
    }

    public EventoContabil buscarEventoContabil(Liquidacao entity, ContaDespesa conta) {
        try {
            EventoContabil eventoContabil = null;
            if (entity.isLiquidacaoNormal()) {
                ConfigLiquidacao configLiquidacao = configLiquidacaoFacade.buscarCDELiquidacao(
                    conta, TipoLancamento.NORMAL,
                    entity.getDataLiquidacao(),
                    entity.getEmpenho().getSubTipoDespesa(),
                    entity.getTipoReconhecimento());
                if (configLiquidacao != null) {
                    eventoContabil = configLiquidacao.getEventoContabil();
                }
            } else {
                if (entity.isLiquidacaoRestoNaoProcessada()) {
                    ConfigLiquidacaoResPagar configLiquidacaoResto = configLiquidacaoResPagarFacade.recuperaEventoPorContaDespesa(
                        conta,
                        TipoLancamento.NORMAL,
                        entity.getDataLiquidacao(),
                        entity.getEmpenho().getSubTipoDespesa(),
                        entity.getTipoReconhecimento());
                    if (configLiquidacaoResto != null) {
                        eventoContabil = configLiquidacaoResto.getEventoContabil();
                    }
                }
            }
            return eventoContabil;
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void contabilizarLiquidacao(Liquidacao entity) {
        for (Desdobramento desdobramento : entity.getDesdobramentos()) {
            if (entity.isLiquidacaoNormal()) {
                contabilizarDesdobramento(desdobramento);
            } else {
                contabilizarDesdobramentoResto(desdobramento);
            }
        }
    }

    private void contabilizarDesdobramento(Desdobramento desdobramento) {
        if (!Hibernate.isInitialized(desdobramento.getLiquidacao().getDesdobramentos())) {
            desdobramento.setLiquidacao(recuperar(desdobramento.getLiquidacao().getId()));
        }
        EventoContabil eventoContabil = buscarEventoContabil(desdobramento.getLiquidacao(), (ContaDespesa) desdobramento.getConta());
        if (eventoContabil != null) {
            desdobramento.setEventoContabil(eventoContabil);
            desdobramento.getLiquidacao().gerarHistoricos();
            ParametroEvento parametroEvento = criarParametroEvento(desdobramento);
            contabilizacaoFacade.contabilizar(parametroEvento);
        }
    }

    public ParametroEvento criarParametroEvento(Desdobramento desdobramento) {
        try {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(getHistoricoRazao(desdobramento));
            parametroEvento.setDataEvento(desdobramento.getLiquidacao().getDataLiquidacao());
            parametroEvento.setUnidadeOrganizacional(desdobramento.getLiquidacao().getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(desdobramento.getEventoContabil());
            parametroEvento.setExercicio(desdobramento.getConta().getExercicio());
            parametroEvento.setClasseOrigem(Desdobramento.class.getSimpleName());
            parametroEvento.setIdOrigem(desdobramento.getId() == null ? null : desdobramento.getId().toString());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(desdobramento.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            if (desdobramento.getId() != null) {
                objetos.add(new ObjetoParametro(desdobramento.getId().toString(), Desdobramento.class.getSimpleName(), item));
            }
            objetos.add(new ObjetoParametro(desdobramento.getConta().getId().toString(), ContaDespesa.class.getSimpleName(), item));
            Preconditions.checkNotNull(desdobramento.getLiquidacao().getEmpenho().getClasseCredor(), "A Classe credor do empenho não foi preenchida.");
            objetos.add(new ObjetoParametro(desdobramento.getLiquidacao().getEmpenho().getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
            if (desdobramento.getLiquidacao().getEmpenho().getDividaPublica() != null) {
                objetos.add(new ObjetoParametro(desdobramento.getLiquidacao().getEmpenho().getDividaPublica().getCategoriaDividaPublica().getId().toString(), CategoriaDividaPublica.class.getSimpleName(), item));
            }
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void contabilizarDesdobramentoResto(Desdobramento desdobramento) {
        if (desdobramento.getLiquidacao().isLiquidacaoRestoNaoProcessada()) {
            Conta conta = desdobramento.getConta();
            if (!Hibernate.isInitialized(desdobramento.getLiquidacao().getDesdobramentos())) {
                desdobramento.setLiquidacao(recuperar(desdobramento.getLiquidacao().getId()));
            }
            ConfigLiquidacaoResPagar configuracao = configLiquidacaoResPagarFacade.recuperaEventoPorContaDespesa(
                conta,
                TipoLancamento.NORMAL,
                desdobramento.getLiquidacao().getDataLiquidacao(),
                desdobramento.getLiquidacao().getEmpenho().getSubTipoDespesa(),
                desdobramento.getLiquidacao().getTipoReconhecimento());
            if (configuracao != null && configuracao.getEventoContabil() != null) {
                desdobramento.setEventoContabil(configuracao.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Liquidação de Resto a Pagar.");
            }
            desdobramento.getLiquidacao().gerarHistoricos();
            ParametroEvento parametroEvento = criarParametroEvento(desdobramento);
            contabilizacaoFacade.contabilizar(parametroEvento);
        }
    }

    private String getHistoricoRazao(Desdobramento desdobramento) {
        Liquidacao liquidacao = desdobramento.getLiquidacao();
        String historico = liquidacao.getHistoricoRazao() != null ? liquidacao.getHistoricoRazao() : "";
        if (liquidacao.getEmpenho().getTipoContaDespesa().isEstoque()
            && liquidacao.isEmpenhoIntegracaoContratoOrReconhecimentoDividaOrAta()) {
            historico += UtilBeanContabil.SEPARADOR_HISTORICO + getHistoricoRazaoEntradaPorCompra(liquidacao);
        }
        return historico;
    }

    private String getHistoricoRazaoEntradaPorCompra(Liquidacao liquidacao) {
        DoctoFiscalLiquidacao documento = liquidacao.getDoctoFiscais().get(0).getDoctoFiscalLiquidacao();
        EntradaCompraMaterial entradaCompra = entradaMaterialFacade.recuperarEntradaMaterialPorDocumentoFiscal(documento);
        if (entradaCompra != null) {
            return entradaCompra.getHistoricoRazao();
        }
        return "";
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public DoctoFiscalLiquidacaoFacade getDoctoFiscalLiquidacaoFacade() {
        return doctoFiscalLiquidacaoFacade;
    }

    public ConfigTipoViagemContaDespesaFacade getConfigTipoViagemContaDespesaFacade() {
        return configTipoViagemContaDespesaFacade;
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        Desdobramento desdobramento = (Desdobramento) entidadeContabil;
        if (desdobramento.getLiquidacao().isLiquidacaoNormal()) {
            contabilizarDesdobramento(desdobramento);
        } else if (desdobramento.getLiquidacao().isLiquidacaoRestoPagar()) {
            contabilizarDesdobramentoResto(desdobramento);
        }
    }


    public List<LiquidacaoDoctoFiscal> buscarDocumentosFiscaisPorDocumentoFiscal(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        String sql = " select d.* from liquidacaodoctofiscal d " +
            "          where d.doctofiscalliquidacao_id = :idDoctoFiscal ";
        Query q = em.createNativeQuery(sql, LiquidacaoDoctoFiscal.class);
        q.setParameter("idDoctoFiscal", doctoFiscalLiquidacao.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public BigDecimal getValorTotalDocumentosFicaisLiquidado(DoctoFiscalLiquidacao documentoFical) {
        return doctoFiscalLiquidacaoFacade.buscarValorLiquidado(documentoFical);
    }

    public List<Liquidacao> buscarLiquidacaoPorUnidadesExercicio(Exercicio
                                                                     ano, List<HierarquiaOrganizacional> listaUnidades, CategoriaOrcamentaria categoria) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }
        String sql = " select liq from Liquidacao liq" +
            " where liq.exercicio.id = :exercicio" +
            " and liq.unidadeOrganizacional in ( :unidades )" +
            " and liq.categoriaOrcamentaria = :categoria";
        Query q = em.createQuery(sql, Liquidacao.class);
        q.setParameter("exercicio", ano.getId());
        q.setParameter("unidades", unidades);
        q.setParameter("categoria", categoria);

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<LiquidacaoDoctoFiscal> buscarDocumentoFiscalNaoIntegradoComLiquidacao(DoctoFiscalLiquidacao documentoFiscal) {
        String sql = "" +
            "  select ldf.* from doctofiscalliquidacao documento " +
            "  inner join liquidacaodoctofiscal ldf on ldf.doctofiscalliquidacao_id = documento.id " +
            "    where documento.tipodocumentofiscal_id = :tipoDocto " +
            "    and documento.datadocto = to_date(:dataDocto, 'dd/MM/yyyy')" +
            "    and documento.numero like :numeroDocto" +
            "    and documento.id not in (select docto.doctofiscalliquidacao_id from doctofiscalentradacompra docto where documento.id = docto.doctofiscalliquidacao_id) ";
        Query q = em.createNativeQuery(sql, LiquidacaoDoctoFiscal.class);
        q.setParameter("numeroDocto", "%" + documentoFiscal.getNumero() + "%");
        q.setParameter("dataDocto", DataUtil.getDataFormatada(documentoFiscal.getDataDocto()));
        q.setParameter("tipoDocto", documentoFiscal.getTipoDocumentoFiscal().getId());
        return q.getResultList();
    }

    public LiquidacaoDoctoFiscal buscarLiquidacaoDoctoFiscalPorDocumentoFiscal(DoctoFiscalLiquidacao documentoFiscal) {
        String sql = " select ldf.* from liquidacaodoctofiscal ldf " +
            "          where ldf.doctofiscalliquidacao_id = :idDocumento ";
        Query q = em.createNativeQuery(sql, LiquidacaoDoctoFiscal.class);
        q.setParameter("idDocumento", documentoFiscal.getId());
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return null;
        }
        return (LiquidacaoDoctoFiscal) q.getSingleResult();
    }

    public Desdobramento buscarDesdobramentoPorDocumentoFiscal(DoctoFiscalLiquidacao docto) {
        String sql = "" +
            "  select desd.* from desdobramento desd " +
            "   inner join liquidacao liq on liq.id = desd.liquidacao_id " +
            "   inner join liquidacaodoctofiscal ldf on ldf.liquidacao_id = liq.id " +
            "  where ldf.doctofiscalliquidacao_id = :idDocto  " +
            "  order by liq.id desc ";
        Query q = em.createNativeQuery(sql, Desdobramento.class);
        q.setParameter("idDocto", docto.getId());
        q.setMaxResults(1);
        try {
            return (Desdobramento) q.getSingleResult();
        } catch (NoResultException r) {
            return null;
        }
    }

    public ObrigacaoAPagarFacade getObrigacaoAPagarFacade() {
        return obrigacaoAPagarFacade;
    }

    public DesdobramentoObrigacaoAPagarFacade getDesdobramentoObrigacaoAPagarFacade() {
        return desdobramentoObrigacaoAPagarFacade;
    }

    public DesdobramentoEmpenhoFacade getDesdobramentoEmpenhoFacade() {
        return desdobramentoEmpenhoFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(Desdobramento.class, filtros);
        consulta.incluirJoinsComplementar(" inner join liquidacao liq on liq.id = obj.liquidacao_id ");
        consulta.incluirJoinsComplementar(" inner join empenho emp on liq.empenho_id = emp.id ");
        consulta.incluirJoinsOrcamentoDespesa(" emp.fontedespesaorc_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(liq.dataliquidacao)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(liq.dataliquidacao)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "liq.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "liq.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CATEGORIA_ORCAMENTARIA, "obj.categoriaorcamentaria");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "emp.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "emp.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "liq.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "liq.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "liq.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_DESPESA, "obj.conta_id");
        return Arrays.asList(consulta);
    }


    public BigDecimal buscarValorPorFonteDespesaOrc(FonteDespesaORC fonteDespesaORC, Date dataInicial, Date dataFinal) {
        String sql = "select coalesce(sum(valor),0) from ( " +
            " select coalesce(liq.valor, 0) as valor " +
            "  FROM liquidacao liq  " +
            " inner join empenho emp on liq.empenho_id = emp.id  " +
            " INNER JOIN FONTEDESPESAORC fontdesp ON emp.FONTEDESPESAORC_ID = fontdesp.ID  " +
            " where liq.categoriaorcamentaria = :categoria  " +
            " and trunc(liq.dataliquidacao) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')  " +
            " and fontdesp.id = :fontedespesaorc " +
            " union all " +
            "  " +
            " select coalesce(liq.valor, 0)*-1 as valor " +
            "  FROM liquidacaoestorno est  " +
            " inner join liquidacao liq on est.liquidacao_id = liq.id  " +
            " inner join empenho emp on liq.empenho_id = emp.id  " +
            " INNER JOIN FONTEDESPESAORC fontdesp ON emp.FONTEDESPESAORC_ID = fontdesp.ID  " +
            " where liq.categoriaorcamentaria = :categoria " +
            " and trunc(est.dataEstorno) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            " and fontdesp.id = :fontedespesaorc " +
            ") ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fontedespesaorc", fonteDespesaORC.getId());
        q.setParameter("categoria", CategoriaOrcamentaria.NORMAL.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public List<Object[]> buscarLiquidacoes(Exercicio exercicio, Date dataOperacao) {
        String sql = " select ano,  " +
            "       mes, " +
            "       dia, " +
            "       contaCodigo, " +
            "       orgao, " +
            "       funcaoCodigo, " +
            "       programaCodigo, " +
            "       subFuncaoCodigo, " +
            "       tipoAcaoCodigo, " +
            "       acaoCodigo, " +
            "       fonteCodigo, " +
            "       subFonteCodigo, " +
            "       fornecedor, " +
            "       coalesce(numerotermo, 0) as NUMEROCONTRATO, " +
            "       valor " +
            "  from ( " +
            "select extract(year from liq.dataLiquidacao) as ano, " +
            "       extract(month from liq.dataLiquidacao) as mes, " +
            "       extract(day from liq.dataLiquidacao) as dia, " +
            "       substr(c.codigo, 0, 12) as contaCodigo,  " +
            "       VW.codigo as orgao,  " +
            "       f.codigo as funcaoCodigo,  " +
            "       prog.codigo as programaCodigo,  " +
            "       sf.codigo as subFuncaoCodigo,  " +
            "       tpa.codigo as tipoAcaoCodigo,  " +
            "       a.codigo as acaoCodigo,  " +
            "       fr.codigo as fonteCodigo,  " +
            "       case when coalesce((select '1' from CALAMIDADEPUBLICACONTRATO cpc " +
            "                 where cpc.CONTRATO_ID = cont.id), '0') = '1' then '1' " +
            "            when tpa.codigo || a.codigo || '.' || sub.codigo in ('1396.0000', '1397.0000', '1398.0000', '1399.0000', '1400.0000', '1400.0001', '1401.0000') " +
            "                 then '1' " +
            "            when fr.codigo = '126' then '1' " +
            "            else '0' " +
            "       end as subFonteCodigo, " +
            "       emp.FORNECEDOR_ID as fornecedor, " +
            "       to_number(cont.numerotermo) as numerotermo, " +
            "       desd.valor as valor " +
            "  from liquidacao liq " +
            " inner join desdobramento desd on liq.id = desd.liquidacao_id" +
            " inner join empenho emp on liq.empenho_id = emp.id" +
            "  left join contrato cont on emp.contrato_id = cont.id " +
            " inner join fontedespesaorc fontOrc on emp.FONTEDESPESAORC_ID = fontOrc.id " +
            " inner join provisaoppafonte provFonte on fontOrc.PROVISAOPPAFONTE_ID = provFonte.id " +
            " inner join CONTADEDESTINACAO cd on provFonte.DESTINACAODERECURSOS_ID= cd.id " +
            " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_ID = fr.id " +
            " inner join despesaorc desp on emp.DESPESAORC_ID = desp.id " +
            " inner join provisaoppadespesa provDesp on desp.PROVISAOPPADESPESA_ID = provDesp.id " +
            " inner join conta c on desd.conta_id = c.id " +
            " inner join VWHIERARQUIAORCAMENTARIA vw on liq.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
            " INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            " INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            " INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            " INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            " where TO_DATE(:dataOperacao, 'dd/mm/yyyy') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, TO_DATE(:dataOperacao, 'dd/mm/yyyy')) " +
            "   and liq.EXERCICIO_ID = :exercicio " +
            "   and liq.CATEGORIAORCAMENTARIA = :categoria " +
            " union all " +
            "select extract(year from est.dataEstorno) as ano, " +
            "       extract(month from est.dataEstorno) as mes, " +
            "       extract(day from est.dataEstorno) as dia, " +
            "       substr(c.codigo, 0, 12) as contaCodigo,  " +
            "       VW.codigo as orgao,  " +
            "       f.codigo as funcaoCodigo,  " +
            "       prog.codigo as programaCodigo,  " +
            "       sf.codigo as subFuncaoCodigo,  " +
            "       tpa.codigo as tipoAcaoCodigo,  " +
            "       a.codigo as acaoCodigo,  " +
            "       fr.codigo as fonteCodigo,  " +
            "       case when coalesce((select '1' from CALAMIDADEPUBLICACONTRATO cpc " +
            "                 where cpc.CONTRATO_ID = cont.id), '0') = '1' then '1' " +
            "            when tpa.codigo || a.codigo || '.' || sub.codigo in ('1396.0000', '1397.0000', '1398.0000', '1399.0000', '1400.0000', '1400.0001', '1401.0000') " +
            "                 then '1' " +
            "            when fr.codigo = '126' then '1' " +
            "            else '0' " +
            "       end as subFonteCodigo, " +
            "       emp.FORNECEDOR_ID as fornecedor, " +
            "       to_number(cont.numerotermo) as numerotermo , " +
            "       desdEst.valor * -1 as valor " +
            "  from liquidacaoestorno est " +
            " inner join liquidacao liq on liq.id = est.liquidacao_id" +
            " inner join DesdobramentoLiqEstorno desdEst on est.id = desdEst.liquidacaoEstorno_id" +
            " inner join empenho emp on liq.empenho_id = emp.id" +
            "  left join contrato cont on emp.contrato_id = cont.id " +
            " inner join fontedespesaorc fontOrc on emp.FONTEDESPESAORC_ID = fontOrc.id " +
            " inner join provisaoppafonte provFonte on fontOrc.PROVISAOPPAFONTE_ID = provFonte.id " +
            " inner join CONTADEDESTINACAO cd on provFonte.DESTINACAODERECURSOS_ID= cd.id " +
            " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_ID = fr.id " +
            " inner join despesaorc desp on emp.DESPESAORC_ID = desp.id " +
            " inner join provisaoppadespesa provDesp on desp.PROVISAOPPADESPESA_ID = provDesp.id " +
            " inner join conta c on desdEst.conta_ID = c.id " +
            " inner join VWHIERARQUIAORCAMENTARIA vw on est.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
            " INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            " INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            " INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            " INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            " where TO_DATE(:dataOperacao, 'dd/mm/yyyy') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, TO_DATE(:dataOperacao, 'dd/mm/yyyy')) " +
            "   and est.EXERCICIO_ID = :exercicio " +
            "   and est.CATEGORIAORCAMENTARIA = :categoria) " +
            " order by dia, mes, ano ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("categoria", CategoriaOrcamentaria.NORMAL.name());
        return q.getResultList();
    }

    public List<Liquidacao> buscarPorEmpenho(Empenho empenho) {
        String sql = " select li.* from liquidacao li where li.empenho_id =:idEmpenho ";
        Query q = em.createNativeQuery(sql, Liquidacao.class);
        q.setParameter("idEmpenho", empenho.getId());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public List<TipoDocumentoFiscal> buscarTiposDeDocumentosPorContaDeDespesa(ContaDespesa contaDespesa, TipoContaDespesa tipoContaDespesa, String filtro) {
        return configContaDespesaTipoDocumentoFacade.buscarTiposDeDocumentosPorContaDeDespesa(contaDespesa, tipoContaDespesa, filtro);
    }

    public List<NotaExecucaoOrcamentaria> buscarNotaLiquidacao(String condicao, CategoriaOrcamentaria categoriaOrcamentaria, String nomeDaNota) {
        String sql = " SELECT " +
            "    nota.numero||'/'||exerc.ano as numero, " +
            "    nota.dataliquidacao as data_nota, " +
            "    coalesce(nota.HISTORICONOTA, 'Histórico não cadastrado') as historico_liq, " +
            "    emp.tipoempenho as tipo, " +
            "    TRIM(vworg.CODIGO) AS CD_ORGAO, " +
            "    TRIM(vworg.DESCRICAO) AS DESC_ORGAO, " +
            "    TRIM(vw.CODIGO) AS CD_UNIDADE, " +
            "    TRIM(vw.DESCRICAO) AS DESC_UNIDADE, " +
            "    f.codigo || '.' || sf.codigo || '.' || p.codigo || '.' || tpa.codigo || a.codigo || '.' || sub.codigo as CD_PROG_TRABALHO, " +
            "    ct_desp.codigo as elemento, " +
            "    ct_desp.descricao as especificao_despesa, " +
            "    coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " +
            "    formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "    fonte_r.codigo || ' - ' || trim(fonte_r.descricao)  || ' (' || cdest.codigo || ')' as desc_destinacao, " +
            "    NOTA.valor as valor, " +
            "    frt_extenso_monetario(NOTA.valor)||'  ***********************' as valor_extenso, " +
            "    COALESCE(endereco.logradouro,'Pessoa não possui logradouro cadastrado') as logradouro, " +
            "    COALESCE(endereco.localidade,'Pessoa não possui cidade cadastrado') as cidade, " +
            "    A.DESCRICAO AS DESC_ACAO, " +
            "    coalesce(lic.numero, disp.numeroDaDispensa, regext.numeromodalidade)  || ' - Processo Licitatório ' || coalesce(lic.numerolicitacao, disp.numerodadispensa, regext.numeroregistro) || ' - ' || to_char(coalesce(lic.emitidaem, disp.datadadispensa, regext.dataregistrocarona),'dd/MM/YYYY') as modalidade, " +
            "    emp.MODALIDADELICITACAO as MODALIDADELICITACAO, " +
            "    classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " +
            "    COALESCE(EMP.VALOR,0) - coalesce((SELECT SUM(EST.VALOR) FROM EMPENHOESTORNO EST WHERE EMPENHO_ID = NOTA.EMPENHO_ID), 0) AS VALOR_EMPENHADO, " +
            "    COALESCE(EMP.SALDO,0) AS SALDO_LIQUIDAR, " +
            "    COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "    COALESCE(ENDERECO.UF,'sem UF ') AS UF, " +
            "    COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP, " +
            "    vw.subordinada_id as idUnidadeOrcamentaria, " +
            "    nota.id as idNota " +
            " FROM liquidacao NOTA " +
            " INNER JOIN EMPENHO EMP ON NOTA.EMPENHO_ID = EMP.ID " +
            (categoriaOrcamentaria.isResto() ? " inner join empenho emp_original on emp.empenho_id = emp_original.id " : "") +
            " INNER JOIN fontedespesaorc FONTE ON EMP.fontedespesaorc_id = fonte.id " +
            " INNER JOIN despesaorc DESPESA ON fonte.despesaorc_id = despesa.id " +
            " INNER JOIN provisaoppadespesa PROVISAO ON despesa.provisaoppadespesa_id= provisao.id " +
            " inner join provisaoppafonte ppf on ppf.id = fonte.PROVISAOPPAFONTE_ID " +
            " INNER JOIN SUBACAOPPA SUB ON SUB.ID = provisao.subacaoppa_id " +
            " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            " INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            " INNER JOIN programappa P ON P.ID = A.programa_id " +
            " INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            " INNER JOIN CONTA CT_DESP ON provisao.contadedespesa_id = CT_DESP.ID " +
            " inner join exercicio exerc on nota.exercicio_id = exerc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id  = vw.subordinada_id " +
            " inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            " INNER JOIN PESSOA Pes ON emp.fornecedor_id = Pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " left join enderecocorreio endereco on pes.enderecoprincipal_id = endereco.id " +
            " left join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id " +
            " left join conta cdest on cdest.id = cd.id " +
            " left join fontederecursos fonte_r on fonte_r.id = cd.fontederecursos_id " +
            (categoriaOrcamentaria.isResto() ? "  and fonte_r.exercicio_id = emp_original.exercicio_id " : "  and fonte_r.exercicio_id = exerc.id ") +
            " left join contrato contrato on emp.contrato_id = contrato.id " +
            " left join conlicitacao conlic on conlic.contrato_id = contrato.id " +
            " left join licitacao lic on lic.id = conlic.licitacao_id " +
            " left join condispensalicitacao conDisp on condisp.contrato_id = contrato.id " +
            " left join dispensadelicitacao disp on disp.id = condisp.dispensadelicitacao_id " +
            " left join CONREGPRECOEXTERNO conReg on conreg.contrato_id = contrato.id " +
            " left join REGISTROSOLMATEXT regExt on regExt.id = conreg.regsolmatext_id " +
            " left join classecredor classe on emp.classecredor_id = classe.id " +
            " where trunc(nota.dataliquidacao) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(nota.dataliquidacao)) " +
            "   AND trunc(nota.dataliquidacao) BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, trunc(nota.dataliquidacao)) " +
            "   and nota.categoriaOrcamentaria = :categoriaOrcamentaria " +
            condicao;
        Query q = em.createNativeQuery(sql);
        q.setParameter("categoriaOrcamentaria", categoriaOrcamentaria.name());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setNomeDaNota(nomeDaNota);
                nota.setNumeroLiquidacao((String) obj[0]);
                nota.setDataLiquidacao(DataUtil.getDataFormatada((Date) obj[1]));
                nota.setHistorico((String) obj[2]);
                nota.setTipoEmpenho(obj[3] != null ? TipoEmpenhoDTO.valueOf((String) obj[3]).getDescricao() : "");
                nota.setCodigoOrgao((String) obj[4]);
                nota.setDescricaoOrgao((String) obj[5]);
                nota.setCodigoUnidade((String) obj[6]);
                nota.setDescricaoUnidade((String) obj[7]);
                nota.setCodigoProgramaTrabalho((String) obj[8]);
                nota.setNaturezaDespesa((String) obj[9]);
                nota.setEspecificacaoDespesa((String) obj[10]);
                nota.setNomePessoa((String) obj[11]);
                nota.setCpfCnpj((String) obj[12]);
                nota.setDescricaoDestinacao((String) obj[13]);
                nota.setValor((BigDecimal) obj[14]);
                nota.setValorPorExtenso((String) obj[15]);
                nota.setLogradouro((String) obj[16]);
                nota.setCidade((String) obj[17]);
                nota.setDescricaoProjetoAtividade((String) obj[18]);
                nota.setModalidadeLicitacao(obj[20] != null ? ModalidadeLicitacaoEmpenho.valueOf((String) obj[20]).getDescricao() + " " + (String) obj[19] : "");
                nota.setClassePessoa((String) obj[21]);
                nota.setValorEmpenhado((BigDecimal) obj[22]);
                nota.setSaldoAtual((BigDecimal) obj[23]);
                nota.setBairro((String) obj[24]);
                nota.setUf((String) obj[25]);
                nota.setCep((String) obj[26]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[27]);
                nota.setId(((BigDecimal) obj[28]).longValue());
                nota.setDocumentosComprobatorios(buscarDocumentos(nota.getId()));
                nota.setDetalhamentos(buscarDetalhamentos(nota.getId()));
                retorno.add(nota);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDocumentoComprobatorio> buscarDocumentos(Long idLiquidacao) {
        String sql = " select " +
            "       coalesce(doc.numero, '') as numero, " +
            "       coalesce(doc.serie, '') as serie, " +
            "       doc.datadocto as dataDoc, " +
            "       coalesce(tipo.descricao, '') as tipo, " +
            "       coalesce(uf.nome, '') as uf, " +
            "       coalesce(df.valorLiquidado, 0) as valor  " +
            "   from DoctoFiscalLiquidacao doc " +
            " inner join liquidacaodoctofiscal df on df.doctoFiscalLiquidacao_id = doc.id " +
            " inner join tipodocumentofiscal tipo on doc.tipodocumentofiscal_id = tipo.id " +
            " left join uf uf on doc.uf_id = uf.id " +
            " where df.liquidacao_id = :idLiquidacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLiquidacao", idLiquidacao);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDocumentoComprobatorio> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDocumentoComprobatorio dc = new NotaExecucaoOrcamentariaDocumentoComprobatorio();
                dc.setNumero((String) obj[0]);
                dc.setSerie((String) obj[1]);
                dc.setData((Date) obj[2]);
                dc.setTipo((String) obj[3]);
                dc.setUf((String) obj[4]);
                dc.setValor((BigDecimal) obj[5]);
                retorno.add(dc);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDetalhamento> buscarDetalhamentos(Long idLiquidacao) {
        String sql = " select " +
            "     c.codigo as codigoConta, " +
            "     c.descricao, " +
            "     eve.codigo as evento, " +
            "     desd.valor " +
            "   from Desdobramento desd " +
            " inner join conta c on desd.conta_id = c.id " +
            " left join eventocontabil eve on desd.eventocontabil_id = eve.id " +
            " where desd.liquidacao_id = :idLiquidacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLiquidacao", idLiquidacao);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDetalhamento> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDetalhamento d = new NotaExecucaoOrcamentariaDetalhamento();
                d.setConta((String) obj[0]);
                d.setDescricao((String) obj[1]);
                d.setEvento((String) obj[2]);
                d.setValor((BigDecimal) obj[3]);
                retorno.add(d);
            }
        }
        return retorno;
    }

    public List<NotaFiscal> buscarNotasFiscais(Long idPessoaEmpenho, Exercicio exercicio, LiquidacaoDoctoFiscal documentoFiscal) {
        String sql = " select nf.* " +
            " from notafiscal nf " +
            "    inner join cadastroeconomico ce on ce.id = nf.prestador_id " +
            "    inner join DeclaracaoPrestacaoServico dps on dps.id = nf.declaracaoPrestacaoServico_id " +
            " where ce.pessoa_id = :idPessoaEmpenho " +
            "   and extract(year from nf.emissao) = :anoExercicio " +
            (documentoFiscal.getDoctoFiscalLiquidacao().getNumero() != null && !documentoFiscal.getDoctoFiscalLiquidacao().getNumero().isEmpty() ? " and nf.numero = :numeroDocFiscal " : "") +
            (documentoFiscal.getDoctoFiscalLiquidacao().getDataDocto() != null ? " and trunc(nf.emissao) = to_date(:dataEmissaoDoc, 'dd/MM/yyyy') " : "") +
            (documentoFiscal.getDoctoFiscalLiquidacao().getValor() != null && documentoFiscal.getDoctoFiscalLiquidacao().getValor().compareTo(BigDecimal.ZERO) != 0 ? " and dps.totalNota = :valorDoc " : "") +
            " order by to_number(nf.numero)";
        Query q = em.createNativeQuery(sql, NotaFiscal.class);
        q.setParameter("idPessoaEmpenho", idPessoaEmpenho);
        q.setParameter("anoExercicio", exercicio.getAno());
        if (documentoFiscal.getDoctoFiscalLiquidacao().getNumero() != null && !documentoFiscal.getDoctoFiscalLiquidacao().getNumero().isEmpty()) {
            q.setParameter("numeroDocFiscal", documentoFiscal.getDoctoFiscalLiquidacao().getNumero());
        }
        if (documentoFiscal.getDoctoFiscalLiquidacao().getDataDocto() != null) {
            q.setParameter("dataEmissaoDoc", DataUtil.getDataFormatada(documentoFiscal.getDoctoFiscalLiquidacao().getDataDocto()));
        }
        if (documentoFiscal.getDoctoFiscalLiquidacao().getValor() != null && documentoFiscal.getDoctoFiscalLiquidacao().getValor().compareTo(BigDecimal.ZERO) != 0) {
            q.setParameter("valorDoc", documentoFiscal.getDoctoFiscalLiquidacao().getValor());
        }
        return q.getResultList();
    }

    public NotaFiscalFacade getNotaFiscalFacade() {
        return notaFiscalFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public List<Liquidacao> buscarLiquidacaoPorChaveDeAcessoEEmpenhoNaoEstornado(Liquidacao liquidacao, String chaveAcesso) {
        String sql = " select liq.* " +
            " from doctofiscalliquidacao doc " +
            "   inner join liquidacaodoctofiscal liqdoc on liqdoc.DoctoFiscalLiquidacao_id = doc.id " +
            "   inner join liquidacao liq on liqdoc.liquidacao_id = liq.id " +
            "   left join liquidacaoestorno est on liq.id = est.liquidacao_id " +
            "   inner join empenho emp on liq.empenho_id = emp.id " +
            " where emp.id = :idEmpenho and trim(lower(doc.chaveDeAcesso)) = :chaveAcesso " +
            " and est.id is null ";
        if (liquidacao.getId() != null) {
            sql += " and liq.id <> :idLiquidacao ";
        }
        Query q = em.createNativeQuery(sql, Liquidacao.class);
        q.setParameter("idEmpenho", liquidacao.getEmpenho().getId());
        q.setParameter("chaveAcesso", chaveAcesso.trim().toLowerCase());
        if (liquidacao.getId() != null) {
            q.setParameter("idLiquidacao", liquidacao.getId());
        }
        return q.getResultList();
    }

    public NaturezaRendimentoReinfFacade getNaturezaRendimentoReinfFacade() {
        return naturezaRendimentoReinfFacade;
    }

    public IntegradorDocumentoFiscalLiquidacaoFacade getIntegradorDocumentoFiscalLiquidacaoFacade() {
        return integradorDocumentoFiscalLiquidacaoFacade;
    }

    public BigDecimal getRetencaoMaximaContaExtra(Conta conta) {
        return configuracaoContabilFacade.buscarRetencaoMaximaReinfPorContaExtraETipoArquivo(conta, null);
    }

    public LiquidacaoDoctoFiscal atualizarValoresReinfComRetencoesDoPagamento(LiquidacaoDoctoFiscal documentoFiscal, ConfiguracaoContabil configuracaoContabil) {
        String sql = " select distinct " +
            "    cextra.codigo as codigocontaextra, " +
            "    ret.valor as valorretencao, " +
            "    pag.valor as valorpagamento " +
            " from retencaopgto ret " +
            "         inner join conta cextra on cextra.id = ret.contaextraorcamentaria_id " +
            "         inner join pagamento pag on pag.id = ret.pagamento_id " +
            "         inner join liquidacaodoctofiscal liqdoc on liqdoc.liquidacao_id = pag.liquidacao_id " +
            " where liqdoc.id = :idLiqDoc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLiqDoc", documentoFiscal.getId());
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                if (configuracaoContabil.getContaExtraIrrfPadraoDocLiq().getCodigo().equals(obj[0])) {
                    documentoFiscal.setValorRetidoIrrf((BigDecimal) obj[1]);
                    documentoFiscal.calcularPorcentagemIrrf();
                }
                if (configuracaoContabil.getContaExtraInssPadraoDocLiq().getCodigo().equals(obj[0])) {
                    documentoFiscal.setValorRetido((BigDecimal) obj[1]);
                    if (documentoFiscal.getValorBaseCalculo() == null || documentoFiscal.getValorBaseCalculo().compareTo(BigDecimal.ZERO) == 0) {
                        documentoFiscal.setValorBaseCalculo((BigDecimal) obj[2]);
                    }
                    documentoFiscal.calcularPorcentagemInss();
                }
            }
        }
        return documentoFiscal;
    }

    public List<Liquidacao> buscarLiquidacaoComSaldoPorDocumentoFiscal(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        String sql = " select distinct liq.* from liquidacao liq " +
            "           inner join liquidacaodoctofiscal liqdoc on liqdoc.liquidacao_id = liq.id " +
            "          where liqdoc.doctofiscalliquidacao_id = :idDocumento " +
            "          and liq.saldo > 0 ";
        Query q = em.createNativeQuery(sql, Liquidacao.class);
        q.setParameter("idDocumento", doctoFiscalLiquidacao.getId());
        return q.getResultList();
    }

    public boolean hasConfiguracaoExcecaoVigente(Date dataOperacao, Conta contaDespesa) {
        return configuracaoExcecaoDespesaContratoFacade.hasConfiguracaoVigente(dataOperacao, contaDespesa);
    }

    public SolicitacaoEmpenhoFacade getSolicitacaoEmpenhoFacade() {
        return solicitacaoEmpenhoFacade;
    }
}
