/*
 * Codigo gerado automaticamente em Thu Dec 08 11:12:57 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.DiariaPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.PagamentoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.execucao.DesdobramentoFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcRevisaoAuditoria;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.contabil.ModalidadeLicitacaoEmpenhoDTO;
import br.com.webpublico.webreportdto.dto.contabil.SituacaoContaDTO;
import br.com.webpublico.webreportdto.dto.contabil.TipoContaBancariaDTO;
import br.com.webpublico.webreportdto.dto.contabil.TipoEmpenhoDTO;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class PagamentoFacade extends SuperFacadeContabil<Pagamento> {

    private static final String NAO_AUTENTICADA = "Não Autenticada";
    private static final String AUTENTICACAO_MANUAL = "Autenticação Manual";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private LOAFacade lOAFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private CLPRealizadoFacade cLPRealizadoFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private PagamentoEstornoFacade pagamentoEstornoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigDiariaCivilFacade configDiariaCivilFacade;
    @EJB
    private ConfigDiariaDeCampoFacade configDiariaDeCampoFacade;
    @EJB
    private ConfigPagamentoFacade configPagamentoFacade;
    @EJB
    private ConfigPagamentoRestoPagarFacade configPagamentoRestoPagarFacade;
    @EJB
    private DiariaContabilizacaoFacade diariaContabilizacaoFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private FinalidadePagamentoFacade finalidadePagamentoFacade;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private ConfigContaBancariaPessoaFacade configContaBancariaPessoaFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private DesdobramentoFacade desdobramentoFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private CodigoCOFacade codigoCOFacade;
    @EJB
    private ConvenioDespesaFacade convenioDespesaFacade;
    private JdbcPagamento jdbcPagamento;
    private JdbcRevisaoAuditoria jdbcRevisaoAuditoria;

    @PostConstruct
    private void init() {
        jdbcPagamento = (JdbcPagamento) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcPagamento");
        jdbcRevisaoAuditoria = (JdbcRevisaoAuditoria) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcRevisaoAuditoria");
    }

    public PagamentoFacade() {
        super(Pagamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public ConfigPagamentoFacade getConfigPagamentoFacade() {
        return configPagamentoFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    public ConvenioDespesaFacade getConvenioDespesaFacade() {
        return convenioDespesaFacade;
    }

    @Override
    public Pagamento recarregar(Pagamento entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        Query q;
        q = getEntityManager().createQuery("From Pagamento p left join fetch p.pagamentoEstornos where p.id=:param");
        q = getEntityManager().createQuery("From Pagamento p left join fetch p.retencaoPgtos where p.id=:param");
        q.setParameter("param", entity.getId());
        return (Pagamento) q.getSingleResult();
    }

    @Override
    public Pagamento recuperar(Object id) {
        try {
            String hql;
            Object param;
            if (id instanceof Pagamento) {
                hql = "from Pagamento p where p=:param";
                param = (Pagamento) id;
            } else {
                hql = "from Pagamento p where p.id=:param";
                param = id;
            }

            Query q = getEntityManager().createQuery(hql);
            q.setParameter("param", param);
            Pagamento toReturn = (Pagamento) q.getSingleResult();
            toReturn.getPagamentoEstornos().size();
            toReturn.getRetencaoPgtos().size();
            toReturn.getGuiaPagamento().size();
            toReturn.getDesdobramentos().size();
            toReturn.getLiquidacao().getDoctoFiscais().size();
            return toReturn;
        } catch (NoResultException ex) {
            throw new ExcecaoNegocioGenerica("Foi não foi encontrado nenhum pagamento. " + ex.getMessage());
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de um pagamento " + ex.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao Recuperar o pagamento. " + ex.getMessage());
        }
    }

    @Override
    public void remover(Pagamento entity) {
        List<ExecucaoOrcDocumentoOficial> execucoesDocumentosOficiais = notaOrcamentariaFacade.buscarExecucoesOrcsDocumentosOficiais(entity.getId(), ModuloTipoDoctoOficial.NOTA_PAGAMENTO);
        for (ExecucaoOrcDocumentoOficial edo : execucoesDocumentosOficiais) {
            em.remove(edo);
            em.remove(edo.getDocumentoOficial());
        }
        super.remover(entity);
    }

    public void geraSaldoMigracao(Exercicio ex) throws ExcecaoNegocioGenerica {
        try {
            //System.out.println("Buscando pagamentos...");
            List<Pagamento> listapagamento = listaTodosNormalPorExercicio(ex);
            //System.out.println("Busca terminada! Iniciando geração de saldos...");
            int qtde = listapagamento.size();
            int i = 0;
            double perc = 0;
            for (Pagamento pag : listapagamento) {
                if (pag.getLiquidacao() != null && pag.getLiquidacao().getEmpenho() != null && pag.getLiquidacao().getEmpenho().getFonteDespesaORC() != null) {
                    SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                        pag.getLiquidacao().getEmpenho().getFonteDespesaORC(),
                        OperacaoORC.PAGAMENTO,
                        TipoOperacaoORC.NORMAL,
                        pag.getValor(),
                        pag.getDataPagamento(),
                        pag.getUnidadeOrganizacional(),
                        pag.getId().toString(),
                        pag.getClass().getSimpleName(),
                        pag.getNumeroPagamento(),
                        pag.getHistoricoRazao());
                    MovimentoDespesaORC mdorc = saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
                    pag.setMovimentoDespesaORC(mdorc);
                    em.merge(pag);
                } else {
                    //System.out.println("Pagamento sem vínculo completo! " + pag.getId());
                }
                i++;
                perc = ((100 * i) / qtde);
                //System.out.println(perc + "%");
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Problema ao gerar saldo na migração!" + e.getMessage());
        }
    }

    public List<Pagamento> listaPorPessoaUnidadeStatusDeferidoIndeferidoPagoCategoriaNormal(String parte, Integer ano, UnidadeOrganizacional uo) {
        String sql = "SELECT P.* "
            + " FROM PAGAMENTO P"
            + " INNER JOIN LIQUIDACAO L ON P.LIQUIDACAO_ID = L.ID"
            + " INNER JOIN EMPENHO E ON L.EMPENHO_ID = E.ID"
            + " LEFT JOIN PESSOAFISICA PF ON E.FORNECEDOR_ID = PF.ID AND ((LOWER(PF.NOME) LIKE :PARAM) OR (PF.CPF LIKE :PARAM) OR (E.NUMERO LIKE :PARAM)) "
            + " LEFT JOIN PESSOAJURIDICA PJ ON E.FORNECEDOR_ID = PJ.ID AND ((LOWER(PJ.NOMEFANTASIA) LIKE :PARAM) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :PARAM) OR (PJ.CNPJ LIKE :PARAM)) "
            + " WHERE P.SALDO > 0 "
            + " AND (P.STATUS = '" + StatusPagamento.DEFERIDO.name() + "'"
            + "      or P.STATUS = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + " AND P.NUMEROPAGAMENTO LIKE :PARAM "
            + " AND extract(year from E.DATAEMPENHO) = :ANO "
            + " AND P.CATEGORIAORCAMENTARIA='NORMAL' "
            + " AND P.UNIDADEORGANIZACIONAL_ID = :UO "
            + " ORDER BY P.NUMEROPAGAMENTO DESC ";
        Query q = em.createNativeQuery(sql, Pagamento.class);
        q.setParameter("PARAM", "%" + parte.toLowerCase() + "%");
        q.setParameter("ANO", ano);
        q.setParameter("UO", uo.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Pagamento> listaPorPessoaUnidadeStatusDeferidoIndeferidoPagoCategoriaResto(String parte, Integer ano, UnidadeOrganizacional uo) {
        String sql = "SELECT P.* "
            + " FROM PAGAMENTO P"
            + " INNER JOIN LIQUIDACAO L ON P.LIQUIDACAO_ID = L.ID"
            + " INNER JOIN EMPENHO E ON L.EMPENHO_ID = E.ID"
            + " LEFT JOIN PESSOAFISICA PF ON E.FORNECEDOR_ID = PF.ID AND ((LOWER(PF.NOME) LIKE :PARAM) OR (PF.CPF LIKE :PARAM) OR (E.NUMERO LIKE :PARAM)) "
            + " LEFT JOIN PESSOAJURIDICA PJ ON E.FORNECEDOR_ID = PJ.ID AND ((LOWER(PJ.NOMEFANTASIA) LIKE :PARAM) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :PARAM) OR (PJ.CNPJ LIKE :PARAM)) "
            + " WHERE P.SALDO > 0 "
            + " AND (P.STATUS = '" + StatusPagamento.DEFERIDO.name() + "'"
            + "      or P.STATUS = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + " AND P.NUMEROPAGAMENTO LIKE :PARAM "
            + " AND extract(year from E.DATAEMPENHO) = :ANO "
            + " AND P.CATEGORIAORCAMENTARIA='RESTO' "
            + " AND P.UNIDADEORGANIZACIONAL_ID = :UO "
            + " ORDER BY P.NUMEROPAGAMENTO DESC ";
        Query q = em.createNativeQuery(sql, Pagamento.class);
        q.setParameter("PARAM", "%" + parte.toLowerCase() + "%");
        q.setParameter("ANO", ano);
        q.setParameter("UO", uo.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Pagamento> listaPagamentosParaDeferir(Date dtInicio, Date dtFim, SubConta contaInicial, SubConta contaFinal, UnidadeOrganizacional unidadeOrganizacional, Exercicio ex, CategoriaOrcamentaria categoria) {
        String sql = " SELECT P.*  FROM PAGAMENTO P "
            + " INNER JOIN SUBCONTA SUB ON SUB.ID = P.SUBCONTA_ID "
            + " WHERE P.SALDO > 0 "
            + "  AND P.EXERCICIO_ID = :idExercicio "
            + "  AND P.UNIDADEORGANIZACIONAL_ID = :idUnidade "
            + "  AND P.STATUS = '" + StatusPagamento.ABERTO.name() + "'"
            + "  AND trunc(P.PREVISTOPARA) BETWEEN to_date(:dataInicial, 'dd/MM/yyyy') AND to_date(:dataFinal, 'dd/MM/yyyy') ";
        if (contaInicial != null && contaFinal != null) {
            sql += " AND SUB.CODIGO BETWEEN '" + contaInicial.getCodigo() + "' and '" + contaFinal.getCodigo() + "'";
        }
        if (categoria != null) {
            sql += " AND P.CATEGORIAORCAMENTARIA = '" + categoria.name() + "'";
        }
        sql += " order by P.PREVISTOPARA desc, P.NUMEROPAGAMENTO desc";
        Query q = em.createNativeQuery(sql, Pagamento.class);
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dtInicio));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dtFim));
        List<Pagamento> pagamentos = q.getResultList();
        if (!pagamentos.isEmpty()) {
            return pagamentos;
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pagamento> listaPorLiquidacao(Liquidacao li) {
        String hql = "from Pagamento p where p.liquidacao = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", li);
        return q.getResultList();
    }

    public List<Pagamento> listaPagamentoAbertoPorLiquidacao(Liquidacao li) {
        try {
            String sql = "select p.* from Pagamento p " +
                " where p.liquidacao_id = :param" +
                " and p.status = 'ABERTO'";
            Query q = em.createNativeQuery(sql, Pagamento.class);
            q.setParameter("param", li);
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<Pagamento>();
        }
    }

    public List<Pagamento> buscarPagamentoPorLiquidacaoAndSituacoes(Liquidacao liquidacao, List<StatusPagamento> status) {
        try {
            String sql = "select p.* from Pagamento p " +
                "           where p.liquidacao_id = :idLiquidacao" +
                "           and p.status in (:status)";
            Query q = em.createNativeQuery(sql, Pagamento.class);
            q.setParameter("status", StatusPagamento.getStatusPagamentoAsString(status));
            q.setParameter("idLiquidacao", liquidacao.getId());
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<Pagamento>();
        }
    }

    public List<Pagamento> listaPorEmpenho(Empenho e) {
        String sql = " SELECT pag.* FROM pagamento pag "
            + " INNER JOIN liquidacao li ON li.id = pag.liquidacao_id "
            + " WHERE li.empenho_id = :param "
            + " AND pag.datapagamento is not null ";
        Query q = em.createNativeQuery(sql, Pagamento.class);
        q.setParameter("param", e.getId());
        return q.getResultList();
    }

    public List<Pagamento> listaPorBancoUnidade(Banco banco, UnidadeOrganizacional unidadeOrganizacional, SubConta subConta, Exercicio ex) {
        String sql = "SELECT PAG.* "
            + " FROM PAGAMENTO PAG "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.ID = PAG.LIQUIDACAO_ID "
            + " INNER JOIN EMPENHO EMP ON EMP.ID = LIQ.EMPENHO_ID "
            + " INNER JOIN SUBCONTA SC ON PAG.SUBCONTA_ID = SC.ID "
            + " INNER JOIN CONTABANCARIAENTIDADE CBE ON SC.CONTABANCARIAENTIDADE_ID = CBE.ID "
            + " INNER JOIN AGENCIA AG ON CBE.AGENCIA_ID = AG.ID "
            + " INNER JOIN BANCO BC ON AG.BANCO_ID = BC.ID "
            + " WHERE (PAG.STATUS = '" + StatusPagamento.DEFERIDO.name() + "' or  PAG.STATUS = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + "   AND PAG.TIPOOPERACAOPAGTO <> '" + TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO + "' "
            + "   AND PAG.SALDOFINAL > 0 "
            + "   AND EMP.UNIDADEORGANIZACIONAL_ID = :uo "
            + "   AND SC.ID = :idSubconta "
            + "   AND BC.ID = :banco "
            + "   AND EMP.EXERCICIO_ID = :idExercicio "
            + " ORDER BY PAG.DATAPAGAMENTO, PAG.NUMEROPAGAMENTO ";
        Query q = em.createNativeQuery(sql, Pagamento.class);
        q.setParameter("uo", unidadeOrganizacional.getId());
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("banco", banco.getId());
        q.setParameter("idSubconta", subConta.getId());
        return q.getResultList();
    }

    public List<Pagamento> listaPorSubContaStatus(SubConta conta, UnidadeOrganizacional unidade, StatusPagamento status) {
        String sql = "SELECT pag.* FROM pagamento pag "
            + " INNER JOIN liquidacao liq ON liq.id = pag.liquidacao_id "
            + " INNER JOIN empenho emp ON emp.id = liq.empenho_id "
            + " WHERE pag.status = :status "
            + " AND pag.subConta_id = :conta "
            + " AND emp.unidadeOrganizacional_id = :unidade";
        Query q = em.createNativeQuery(sql, Pagamento.class);
        q.setParameter("status", status.name());
        q.setParameter("conta", conta.getId());
        q.setParameter("unidade", unidade.getId());
        return q.getResultList();
    }

    public List<Pagamento> listaPorEmpenhoSaldoNaoZero(Empenho e) {
        String sql = "SELECT pag.* FROM pagamento pag "
            + "INNER JOIN liquidacao li ON li.id = pag.liquidacao_id "
            + "WHERE li.empenho_id = :param AND pag.saldo != 0";
        Query q = em.createNativeQuery(sql, Pagamento.class);
        q.setParameter("param", e.getId());
        return q.getResultList();
    }

    public String retornaUltimoNumeroPagamento(UnidadeOrganizacional uni) {
//        String sql = "select p.numeroPagamento from pagamento p "
//                + "inner join liquidacao li on p.liquidacao_id = li.id "
//                + "inner join empenho e on li.empenho_id = e.id "
//                + "where e.unidadeorganizacional_id = :uni "
//                + "and p.categoriaorcamentaria = 'NORMAL' "
//                + "order by to_number(p.numeroPagamento) desc";
//        Query q = em.createNativeQuery(sql);

        String hql = "select p from Pagamento p "
            + "inner join p.liquidacao li "
            + "inner join li.empenho e "
            + "where e.unidadeOrganizacional = :uni "
            + "and p.categoriaOrcamentaria = :categoriaOrcamentaria "
            + "order by to_number(p.numeroPagamento) desc";
        Query q = em.createQuery(hql);
        q.setParameter("uni", uni);
        q.setParameter("categoriaOrcamentaria", CategoriaOrcamentaria.NORMAL);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return ((Pagamento) q.getSingleResult()).getNumeroPagamento();
        } else {
            return "0";
        }
    }

    public String retornaUltimoNumeroPagamentoResto(UnidadeOrganizacional uni) {
        String sql = "SELECT p.numeroPagamento FROM pagamento p "
            + "INNER JOIN liquidacao li ON p.liquidacao_id = li.id "
            + "INNER JOIN empenho e ON li.empenho_id = e.id "
            + "WHERE e.unidadeorganizacional_id = :uni "
            + "AND p.categoriaorcamentaria = 'RESTO' "
            + "ORDER BY p.numeroPagamento DESC";
        Query q = em.createNativeQuery(sql);
        q.setParameter("uni", uni.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getSingleResult();
        } else {
            return "0";
        }
    }

    public void salvarNovoPagto(Pagamento entity) throws ExcecaoNegocioGenerica {
        try {
            if (taLiberadoLiquidacao(entity)) {
                singletonConcorrenciaContabil.bloquear(entity.getLiquidacao());
                geraNumeroPagamento(entity);
                entity.gerarHistoricos();
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getPrevistoPara());
                entity.getLiquidacao().setEmpenho(em.merge(entity.getLiquidacao().getEmpenho()));
                if (entity.getId() == null) {
                    em.persist(entity);
                } else {
                    entity = em.merge(entity);
                }
                singletonConcorrenciaContabil.desbloquear(entity.getLiquidacao());
            } else {
                throw new ExcecaoNegocioGenerica("A Liquidação " + entity.getLiquidacao() + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente a Liquidação.");
            }
        } catch (Exception ex) {
            singletonConcorrenciaContabil.desbloquear(entity.getLiquidacao());
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void validarConvenioDespesaVigente(Pagamento selecionado, Date dataReferencia) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLiquidacao().getEmpenho().getConvenioDespesa() != null &&
            !convenioDespesaFacade.isConvenioDespesaVigente(dataReferencia, selecionado.getLiquidacao().getEmpenho().getConvenioDespesa())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Convênio de Despesa <b>"+ selecionado.getLiquidacao().getEmpenho().getConvenioDespesa().toString() + "</b> deve estar vigênte.");
        }
        ve.lancarException();
    }

    private void geraNumeroPagamento(Pagamento entity) {
        entity.setNumeroPagamento(singletonGeradorCodigoContabil.getNumeroPagamento(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getPrevistoPara()));
    }

    public List listaNoExercicio(Exercicio ex) {
        String sql = "SELECT * FROM (SELECT P.ID "
            + " , P.DATAPAGAMENTO"
            + " , P.PREVISTOPARA"
            + " , P.NUMEROPAGAMENTO"
            + " , P.VALOR"
            + " , L.NUMERO"
            + " , PF.NOME"
            + " FROM PAGAMENTO P "
            + "INNER JOIN LIQUIDACAO L ON L.ID = P.LIQUIDACAO_ID "
            + "INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID "
            + "INNER JOIN PESSOAFISICA PF ON PF.ID = E.FORNECEDOR_ID "
            + "WHERE P.EXERCICIO_ID = :EXER  AND P.CATEGORIAORCAMENTARIA = 'NORMAL' "
            + "UNION "
            + "SELECT P.ID"
            + ", P.DATAPAGAMENTO"
            + ", P.PREVISTOPARA"
            + ", P.NUMEROPAGAMENTO"
            + ", P.VALOR"
            + ", L.NUMERO"
            + ", PJ.RAZAOSOCIAL "
            + "FROM PAGAMENTO P "
            + "INNER JOIN LIQUIDACAO L ON L.ID = P.LIQUIDACAO_ID "
            + "INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID "
            + "INNER JOIN PESSOAJURIDICA PJ ON PJ.ID = E.FORNECEDOR_ID "
            + "WHERE P.EXERCICIO_ID = :EXER  AND P.CATEGORIAORCAMENTARIA = 'NORMAL' ) X "
            + "ORDER BY X.ID DESC ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("EXER", ex.getId());
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList();
        } else {
            return lista;
        }
    }

    public List<Pagamento> listaTodosNormalPorExercicio(Exercicio ex) {
        String sql = "SELECT pag.* FROM pagamento pag "
            + "WHERE pag.exercicio_id = :ex "
            + "AND pag.categoriaorcamentaria = 'NORMAL'";
        Query q = getEntityManager().createNativeQuery(sql, Pagamento.class);
        q.setParameter("ex", ex.getId());
        List<Pagamento> pagamentos = q.getResultList();
        if (pagamentos.isEmpty()) {
            return new ArrayList<Pagamento>();
        } else {
            return pagamentos;
        }
    }

    public List listaNoExercicioResto(Exercicio ex) {
        String sql = "SELECT p.id, p.datapagamento, p.previstopara, p.numeropagamento, p.valor,l.numero,  pf.nome "
            + " FROM pagamento p "
            + " INNER JOIN liquidacao l ON l.id = p.liquidacao_id "
            + " INNER JOIN empenho e ON e.id = l.empenho_id "
            + " INNER JOIN pessoafisica pf ON pf.id = e.fornecedor_id "
            + " WHERE p.exercicio_id = :exer  AND p.categoriaorcamentaria = 'RESTO' "
            + " UNION "
            + " SELECT p.id, p.datapagamento, p.previstopara, p.numeropagamento, p.valor, l.numero, pj.razaosocial "
            + " FROM pagamento p "
            + " INNER JOIN liquidacao l ON l.id = p.liquidacao_id "
            + " INNER JOIN empenho e ON e.id = l.empenho_id "
            + " INNER JOIN pessoajuridica pj ON pj.id = e.fornecedor_id "
            + " WHERE p.exercicio_id = :exer  AND p.categoriaorcamentaria = 'RESTO' ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exer", ex.getId());
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList();
        } else {
            return lista;
        }
    }

    public void estornarConciliacao(Pagamento pagamento) {
        try {
            pagamento.setDataConciliacao(null);
            getEntityManager().merge(pagamento);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public void indeferirPagamento(Pagamento entity) {
        try {
            entity.setStatus(StatusPagamento.INDEFERIDO);
            em.merge(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao indeferir: " + ex.getMessage());
        }
    }

    public void baixar(Pagamento pagamento, StatusPagamento statusPagamento) throws ExcecaoNegocioGenerica {
        try {
            validarDataPagamento(pagamento);
            pagamento.setSaldo(BigDecimal.ZERO);
            pagamento.setSaldoFinal(BigDecimal.ZERO);
            pagamento.setStatus(statusPagamento);
            baixarGuiaPagamento(pagamento, SituacaoGuiaPagamento.DEFERIDA, AUTENTICACAO_MANUAL);
            em.merge(pagamento);
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao baixar. Consulte o suporte!");
        }
    }

    private void validarDataPagamento(Pagamento pagamento) {
        ValidacaoException ve = new ValidacaoException();
        if (pagamento.getDataPagamento() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Pagamento " + pagamento.getNumeroPagamento() + " ainda não foi deferido. Para baixar, será necessário deferir o pagamento antes de efetuar a baixa.");
        }
        ve.lancarException();
    }

    private void baixarGuiaPagamento(Pagamento pagamento, SituacaoGuiaPagamento situacaoGuiaPagamento, String autenticacao) {
        if (!pagamento.getGuiaPagamento().isEmpty()) {
            for (GuiaPagamento guiaPagamento : pagamento.getGuiaPagamento()) {
                guiaPagamento.setSituacaoGuiaPagamento(situacaoGuiaPagamento);
                guiaPagamento.setNumeroAutenticacao(autenticacao);
            }
        }
    }

    public Pagamento estornarBaixa(Pagamento pagamento, StatusPagamento statusPagamento) {
        try {
            pagamento.setSaldo(pagamento.getValor().subtract(getSomaEstornosPagamento(pagamento)));
            pagamento.setSaldoFinal(pagamento.getValorFinal().subtract(getSomaValorFinalEstornosPagamento(pagamento)));
            pagamento.setStatus(statusPagamento);
            baixarGuiaPagamento(pagamento, SituacaoGuiaPagamento.ABERTA, NAO_AUTENTICADA);
            em.merge(pagamento);
            return pagamento;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa. Consulte o suporte!");
        }
    }

    public List<PagamentoEstorno> listaEstornosPagamento(Pagamento pag) {
        try {
            return pagamentoEstornoFacade.listaEstornosPagamento(pag);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar estorno de pgamento " + ex.getMessage());
        }
    }

    public BigDecimal getSomaEstornosPagamento(Pagamento pag) {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (PagamentoEstorno est : listaEstornosPagamento(pag)) {
            estornos = estornos.add(est.getValor());
        }
        return estornos;
    }

    public BigDecimal getSomaValorFinalEstornosPagamento(Pagamento pag) {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (PagamentoEstorno est : listaEstornosPagamento(pag)) {
            estornos = estornos.add(est.getValorFinal());
        }
        return estornos;
    }

    public String retornaUltimoNumeroRetencao() {
        String sql = "SELECT MAX(NUMERO) FROM RETENCAOPGTO";
        Query q = em.createNativeQuery(sql);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getSingleResult();
        } else {
            return "1";
        }
    }

    public void setaSituacaoDiariaParaComprovada(Pagamento pag) {
        PropostaConcessaoDiaria pcd = pag.getLiquidacao().getEmpenho().getPropostaConcessaoDiaria();
        Liquidacao liquidacao = liquidacaoFacade.recuperar(pag.getLiquidacao().getId());
        if (pcd != null) {
            if (liquidacao.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
                if (pcd.getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIA)
                    || pcd.getTipoProposta().equals(TipoProposta.SUPRIMENTO_FUNDO)) {
                    pcd.setSituacaoDiaria(SituacaoPropostaConcessaoDiaria.COMPROVADO);
                    em.merge(pcd);
                }
            }
        }
    }

    public Pagamento salvarRetornando(Pagamento entity) {
        entity.gerarHistoricos();
        entity = em.merge(entity);
        return entity;
    }

    @Override
    public void salvar(Pagamento entity) {
        entity.getLiquidacao().setEmpenho(em.merge(entity.getLiquidacao().getEmpenho()));
        em.merge(entity);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Pagamento deferirPagamento(Pagamento pag, Date dataPagamento) {
        if (StatusPagamento.ABERTO.equals(pag.getStatus())) {
            pag.setDataPagamento(dataPagamento);
            hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(pag.getUnidadeOrganizacionalAdm(), pag.getUnidadeOrganizacional(), pag.getDataPagamento());
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                validarSaldoDaLiquidacao(pag);
                validarConcorrencia(pag);
                bloquearPagamento(pag);
                gerarInscricaoDeDiarias(pag);
                lancarConsignacao(pag);
                geraMovimentoDespesaORC(pag);
                gerarSaldoDividaPublica(pag);
                geraSaldoContaFinanceira(pag);
                atualizarPagamentoAndLiquidacao(pag);
                contabilizaPagamento(pag);
                salvarPortal(pag);
            }
        }
        return pag;
    }

    public void desbloquearPagamento(Pagamento pag) {
        singletonConcorrenciaContabil.desbloquear(pag.getLiquidacao());
        singletonConcorrenciaContabil.desbloquear(pag);
//        singletonConcorrenciaContabil.desbloquearUnidadePagamento(pag.getUnidadeOrganizacional());
    }

    public void bloquearPagamento(Pagamento pag) {
        singletonConcorrenciaContabil.bloquear(pag.getLiquidacao());
        singletonConcorrenciaContabil.bloquear(pag);
//        singletonConcorrenciaContabil.bloquearUnidadePagamento(pag.getUnidadeOrganizacional());
    }

    public void validarConcorrencia(Pagamento pagamento) {
        ValidacaoException ve = new ValidacaoException();
        if (!taLiberadoPagamento(pagamento)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Pagamento " + pagamento + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente o Pagamento.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void salvarPortal(Pagamento pag) {
        empenhoFacade.getPortalTransparenciaNovoFacade().salvarPortal(new PagamentoPortal(pag));
        TipoContaDespesa tipoContaDespesa = pag.getLiquidacao().getEmpenho().getTipoContaDespesa();
        if (pag.getLiquidacao().getEmpenho().getPropostaConcessaoDiaria() != null
            && (tipoContaDespesa.equals(TipoContaDespesa.DIARIA_CIVIL)
            || tipoContaDespesa.equals(TipoContaDespesa.DIARIA_CAMPO)
            || tipoContaDespesa.equals(TipoContaDespesa.SUPRIMENTO_FUNDO))) {
            empenhoFacade.getPortalTransparenciaNovoFacade().salvarPortal(new DiariaPortal(pag.getLiquidacao().getEmpenho().getPropostaConcessaoDiaria()));
        }
    }

    private void geraMovimentoDespesaORC(Pagamento pag) {
        if (pag.getCategoriaOrcamentaria().isNormal()) {
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                pag.getLiquidacao().getEmpenho().getFonteDespesaORC(),
                OperacaoORC.PAGAMENTO,
                TipoOperacaoORC.NORMAL,
                pag.getValor(),
                pag.getDataPagamento(),
                pag.getUnidadeOrganizacional(),
                pag.getId().toString(),
                pag.getClass().getSimpleName(),
                pag.getNumeroPagamento(),
                pag.getHistoricoRazao());
            MovimentoDespesaORC mdorc = saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            pag.setMovimentoDespesaORC(mdorc);
        }
    }

    private boolean taLiberadoLiquidacao(Pagamento pag) {
        return singletonConcorrenciaContabil.isDisponivel(pag.getLiquidacao());
    }

    public boolean taLiberadoPagamento(Pagamento pag) {
        return singletonConcorrenciaContabil.isDisponivel(pag);
    }


    private void atualizaPagamentoELiquidacao(Pagamento pag) {
        pag.setSaldo(pag.getValor());
        pag.setSaldoFinal(pag.getValorFinal());
        pag.setStatus(StatusPagamento.DEFERIDO);
        pag.setMovimentoDespesaORC(null);
        pag = em.merge(pag);
        Liquidacao liquidacao = liquidacaoFacade.recarregar(pag.getLiquidacao());
        liquidacao.setSaldo(pag.getLiquidacao().getSaldo().subtract(pag.getValor()));
        liquidacao = em.merge(liquidacao);
        pag.setLiquidacao(liquidacao);
    }

    private void atualizarPagamentoAndLiquidacao(Pagamento pag) {
        Liquidacao liquidacao = liquidacaoFacade.recuperar(pag.getLiquidacao().getId());
        pag.setSaldo(pag.getValor());
        pag.setSaldoFinal(pag.getValorFinal());
        pag.setStatus(StatusPagamento.DEFERIDO);
        pag.setMovimentoDespesaORC(null);
        pag.setLiquidacao(liquidacao);
        for (DesdobramentoPagamento desdobramentoPagamento : pag.getDesdobramentos()) {
            desdobramentoPagamento.getDesdobramento().setSaldo(desdobramentoPagamento.getDesdobramento().getSaldo().subtract(desdobramentoPagamento.getValor()));
            em.merge(desdobramentoPagamento.getDesdobramento());
        }
        pag = em.merge(pag);
        liquidacao.setSaldo(liquidacao.getSaldo().subtract(pag.getValor()));
        em.merge(liquidacao);
    }

    private void validarSaldoDaLiquidacao(Pagamento pag) {
        ValidacaoException ve = new ValidacaoException();
        if (pag.getLiquidacao() != null) {
            BigDecimal valor = pag.getLiquidacao().getSaldo().subtract(pag.getValor());
            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Não é permitido que o saldo da liquidação fique negativo. Saldo disponível: " + Util.formataValor(valor));
            }
        }
        if (pag.getSubConta() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" É obrigatório informar uma Conta Financeira para efetuar esta operação.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void geraSaldoContaFinanceira(Pagamento pag) {
        saldoSubContaFacade.gerarSaldoFinanceiro(pag.getLiquidacao().getEmpenho().getUnidadeOrganizacional(),
            pag.getSubConta(),
            buscarContaDeDestinacaoAtual(pag),
            pag.getValorFinal(),
            TipoOperacao.DEBITO,
            pag.getDataPagamento(),
            pag.getEventoContabil(),
            pag.getHistoricoRazao(),
            MovimentacaoFinanceira.PAGAMENTO,
            pag.getUuid(),
            true);
    }

    private ContaDeDestinacao buscarContaDeDestinacaoAtual(Pagamento pag) {
        try {
            if (pag.getLiquidacao() != null) {
                if (pag.getCategoriaOrcamentaria().isNormal()) {
                    return ((ContaDeDestinacao) pag.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos());
                } else if (pag.getCategoriaOrcamentaria().isResto()) {
                    return (ContaDeDestinacao) pag.getLiquidacao().getEmpenho().getContaDeDestinacao();
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar a conta de destinação atual: {}", e);
        }
        return null;
    }

    private void gerarSaldoDividaPublica(Pagamento pag) {
        if (pag.getLiquidacao().getEmpenho().getDividaPublica() != null &&
            SubTipoDespesa.VALOR_PRINCIPAL.equals(pag.getLiquidacao().getEmpenho().getSubTipoDespesa()) &&
            (TipoContaDespesa.PRECATORIO.equals(pag.getLiquidacao().getEmpenho().getTipoContaDespesa()) ||
                TipoContaDespesa.DIVIDA_PUBLICA.equals(pag.getLiquidacao().getEmpenho().getTipoContaDespesa()))) {
            ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
            try {
                if (hasContaDeDespesaConfiguradaParaGerarSaldoDividaPublica(pag, configuracaoContabil)) {
                    saldoDividaPublicaFacade.gerarMovimento(pag.getDataPagamento(),
                        pag.getValor(),
                        pag.getUnidadeOrganizacional(),
                        pag.getLiquidacao().getEmpenho().getDividaPublica(),
                        OperacaoMovimentoDividaPublica.PAGAMENTO_AMORTIZACAO,
                        false,
                        OperacaoDiarioDividaPublica.EMPENHO,
                        Intervalo.CURTO_PRAZO,
                        pag.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao(),
                        true);
                }
            } catch (Exception e) {
                throw new ExcecaoNegocioGenerica(e.getMessage());
            }
        }
    }

    private boolean hasContaDeDespesaConfiguradaParaGerarSaldoDividaPublica(Pagamento pagamento, ConfiguracaoContabil configuracaoContabil) {
        for (ConfiguracaoContabilContaDespesa configuracaoContabilContaDespesa : configuracaoContabil.getContasDespesa()) {
            if (pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo().startsWith(configuracaoContabilContaDespesa.getContaDespesa().getCodigoSemZerosAoFinal())) {
                return true;
            }
        }
        return false;
    }

    public void contabilizaPagamento(Pagamento pag) {
        if (pag.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            contabilizarPagamento(pag);
        } else {
            contabilizarPagamentoResto(pag);
        }
    }

    private void lancarConsignacao(Pagamento pag) {
        Empenho empenho = empenhoFacade.recuperarComFind(pag.getLiquidacao().getEmpenho().getId());
        for (RetencaoPgto rp : pag.getRetencaoPgtos()) {
            ReceitaExtra re = new ReceitaExtra();
            String historico = (CategoriaOrcamentaria.NORMAL.equals(pag.getCategoriaOrcamentaria()) ? "NPDO " : "NPRP ") + pag.getNumeroPagamento() + "/" + pag.getExercicio().getAno();
            re.setComplementoHistorico(historico + " " + (rp.getComplementoHistorico() != null ? rp.getComplementoHistorico() : ""));
            re.setContaExtraorcamentaria(rp.getContaExtraorcamentaria());
            re.setDataReceita(pag.getDataPagamento());
            re.setFonteDeRecursos(rp.getFonteDeRecursos());
            re.setContaDeDestinacao(contaFacade.recuperarContaDestinacaoPorFonte(rp.getFonteDeRecursos()));
            re.setValor(rp.getValor());
            re.setSaldo(re.getValor());
            re.setUnidadeOrganizacional(empenho.getUnidadeOrganizacional());
            re.setUnidadeOrganizacionalAdm(empenho.getUnidadeOrganizacionalAdm());
            re.setExercicio(pag.getExercicio());
            re.setSubConta(rp.getSubConta());
            re.setSituacaoReceitaExtra(SituacaoReceitaExtra.ABERTO);
            re.setTransportado(Boolean.FALSE);
            re.setRetencaoPgto(rp);
            re.setUsuarioSistema(rp.getUsuarioSistema());
            re.setPessoa(rp.getPessoa());
            re.setClasseCredor(rp.getClasseCredor());
            re.setTipoConsignacao(rp.getTipoConsignacao());
            receitaExtraFacade.salvarNovo(re);
        }
    }

    public void gerarInscricaoDeDiarias(Pagamento pgto) {
        TipoContaDespesa tipoContaDespesa = pgto.getLiquidacao().getEmpenho().getTipoContaDespesa();
        if (pgto.getLiquidacao().getEmpenho().getPropostaConcessaoDiaria() != null) {
            PropostaConcessaoDiaria pcd = pgto.getLiquidacao().getEmpenho().getPropostaConcessaoDiaria();

            DiariaContabilizacao dc = new DiariaContabilizacao();
            dc.setOperacaoDiariaContabilizacao(OperacaoDiariaContabilizacao.INSCRICAO);
            dc.setTipoLancamento(TipoLancamento.NORMAL);
            dc.setPropostaConcessaoDiaria(pcd);
            dc.setValor(pgto.getValor());
            dc.setHistorico(pgto.getComplementoHistorico());
            dc.setUnidadeOrganizacional(pgto.getUnidadeOrganizacional());
            dc.setDataDiaria(pgto.getDataPagamento());
            dc.setExercicio(pgto.getExercicio());
            switch (tipoContaDespesa) {
                case DIARIA_CIVIL:
                    dc.setTipoProposta(TipoProposta.CONCESSAO_DIARIA);
                    break;
                case DIARIA_CAMPO:
                    dc.setTipoProposta(TipoProposta.CONCESSAO_DIARIACAMPO);
                    break;
                case SUPRIMENTO_FUNDO:
                    dc.setTipoProposta(TipoProposta.SUPRIMENTO_FUNDO);
                    break;
            }
            diariaContabilizacaoFacade.salvarNovo(dc, true);
        }
    }

    public List<Pagamento> recuperaPorSolicitacaoCotaFinanceira(SolicitacaoCotaFinanceira sol) {
        String sql = "SELECT pag.* FROM pagamento pag "
            + "INNER JOIN solicitcotafinanc_pgto sp ON pag.id = sp.pagamento_id "
            + "WHERE sp.solicitcotafinanceira_id = :lib";
        Query q = em.createNativeQuery(sql, Pagamento.class);
        q.setParameter("lib", sol.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<Pagamento>();
        } else {
            return q.getResultList();
        }
    }

    public List<ReceitaExtra> buscarReceitasExtrasPorPagamento(Pagamento p) {
        try {
            Preconditions.checkNotNull(p, "O pagamento utilizado como parametro para recuperar a Receita Extra esta vazio.");
            String sql = " SELECT RE.* "
                + " FROM RECEITAEXTRA RE "
                + " INNER JOIN RETENCAOPGTO RP ON RE.RETENCAOPGTO_ID = RP.ID "
                + " INNER JOIN PAGAMENTO P ON RP.PAGAMENTO_ID = P.ID "
                + " WHERE P.ID = :pag ";
            Query q = em.createNativeQuery(sql, ReceitaExtra.class);
            q.setParameter("pag", p.getId());
            List<ReceitaExtra> resultado = q.getResultList();
            if (resultado.isEmpty()) {
                return Lists.newArrayList();
            } else {
                return resultado;
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar receitas extras. " + ex.getMessage());
        }
    }

    public List<ReceitaExtraEstorno> buscarReceitasExtrasEstornoPorPagamento(Pagamento p) {
        try {
            Preconditions.checkNotNull(p, "O pagamento utilizado como parametro para recuperar a Receita Extra esta vazio.");
            String sql = " SELECT REE.* " +
                " FROM RECEITAEXTRAESTORNO REE " +
                " INNER JOIN RECEITAEXTRA RE ON RE.ID = REE.RECEITAEXTRA_ID " +
                " INNER JOIN RETENCAOPGTO RP ON RE.RETENCAOPGTO_ID = RP.ID " +
                " INNER JOIN PAGAMENTO P ON RP.PAGAMENTO_ID = P.ID " +
                "  WHERE P.ID = :pag ";
            Query q = em.createNativeQuery(sql, ReceitaExtraEstorno.class);
            q.setParameter("pag", p.getId());
            List<ReceitaExtraEstorno> resultado = q.getResultList();
            if (resultado.isEmpty()) {
                return Lists.newArrayList();
            } else {
                return resultado;
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar receitas extras. " + ex.getMessage());
        }
    }

    public List<PagamentoExtra> buscarDespesasExtrasPorPagamento(Pagamento p) {
        try {
            Preconditions.checkNotNull(p, "O pagamento utilizado como parametro para recuperar a Despesa Extra está nulo.");
            String sql = " select distinct d.* from pagamentoextra d " +
                "          inner join pagamentoreceitaextra pr on pr.pagamentoextra_id = d.id " +
                "          inner join receitaextra rec on rec.id = pr.receitaextra_id " +
                "          inner join retencaopgto ret on ret.id = rec.retencaopgto_id " +
                "               where ret.pagamento_id = :idPagamento ";
            Query q = em.createNativeQuery(sql, PagamentoExtra.class);
            q.setParameter("idPagamento", p.getId());
            List<PagamentoExtra> resultado = q.getResultList();
            if (resultado.isEmpty()) {
                return Lists.newArrayList();
            } else {
                return resultado;
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar a despesa extra. " + ex.getMessage());
        }
    }

    public List<PagamentoExtraEstorno> buscarEstornoDespesaExtraPorPagamento(Pagamento p) {
        try {
            Preconditions.checkNotNull(p, "O pagamento utilizado como parametro para recuperar o Estorno da Despesa Extra está nulo.");

            String sql = " select distinct est.* " +
                "          from pagamentoextraestorno est " +
                "          inner join pagamentoestornorecextra pe on pe.pagamentoextraestorno_id = est.id " +
                "          inner join receitaextra rec on rec.id = pe.receitaextra_id " +
                "          inner join retencaopgto ret on ret.id = rec.retencaopgto_id " +
                "               where ret.pagamento_id = :idPagamento ";
            Query q = em.createNativeQuery(sql, PagamentoExtraEstorno.class);
            q.setParameter("idPagamento", p.getId());
            List<PagamentoExtraEstorno> resultado = q.getResultList();
            if (resultado.isEmpty()) {
                return Lists.newArrayList();
            } else {
                return resultado;
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar o estorno da despesa extra. " + ex.getMessage());
        }
    }

    public RetencaoPgto recuperaPorReceitaExtra(ReceitaExtra re) {
        String sql = "SELECT ret.* FROM receitaextra re "
            + "INNER JOIN retencaopgto ret ON ret.id = re.retencaopgto_id "
            + "WHERE re.id = :re";
        Query q = em.createNativeQuery(sql, RetencaoPgto.class);
        q.setParameter("re", re.getId());
        if (q.getResultList().isEmpty()) {
            return new RetencaoPgto();
        } else {
            return (RetencaoPgto) q.getSingleResult();
        }
    }

    private void contabilizarPagamento(Pagamento entity) {
        ConfigPagamento configuracao = configPagamentoFacade.recuperaEventoPorContaDespesa((ContaDespesa) entity.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), TipoLancamento.NORMAL, entity.getLiquidacao().getEmpenho().getTipoContaDespesa(), entity.getDataPagamento());
        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabil(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Pagamento.");
        }

        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos();
            contabilizacaoFacade.contabilizar(getParametroEvento(entity));
        } else {
            throw new ExcecaoNegocioGenerica("Não existe configuração de Pagamento");
        }
    }

    private void contabilizarPagamentoResto(Pagamento entity) {
        ConfigPagamentoRestoPagar configuracao = configPagamentoRestoPagarFacade.recuperaEventoRestoPorContaDespesa((ContaDespesa) entity.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), TipoLancamento.NORMAL, entity.getDataPagamento(), entity.getLiquidacao().getEmpenho().getTipoContaDespesa(), entity.getLiquidacao().getEmpenho().getTipoRestosProcessados());

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabil(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Pagamento de Resto a Pagar.");
        }

        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos();


            contabilizacaoFacade.contabilizar(getParametroEvento(entity));

        } else {
            throw new ExcecaoNegocioGenerica("Não existe configuração de Pagamento de Resto a Pagar");
        }
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public HistoricoContabilFacade getHistoricoContabilFacade() {
        return historicoContabilFacade;
    }

    public LOAFacade getlOAFacade() {
        return lOAFacade;
    }

    public LiquidacaoFacade getLiquidacaoFacade() {
        return liquidacaoFacade;
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public CLPRealizadoFacade getcLPRealizadoFacade() {
        return cLPRealizadoFacade;
    }

    public PagamentoEstornoFacade getPagamentoEstornoFacade() {
        return pagamentoEstornoFacade;
    }

    public void setPagamentoEstornoFacade(PagamentoEstornoFacade pagamentoEstornoFacade) {
        this.pagamentoEstornoFacade = pagamentoEstornoFacade;
    }

    public ConfigPagamentoRestoPagarFacade getConfigPagamentoRestoPagarFacade() {
        return configPagamentoRestoPagarFacade;
    }

    public Boolean isGestorFinanceiro(UsuarioSistema usuarioCorrente, UnidadeOrganizacional unidadeOrganizacionalAdministrativaCorrente, UnidadeOrganizacional unidadeOrcamentaria, Date data) {
        return usuarioSistemaFacade.isGestorFinanceiro(usuarioCorrente, unidadeOrganizacionalAdministrativaCorrente, unidadeOrcamentaria, data);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ContaCorrenteBancPessoaFacade getContaCorrenteBancPessoaFacade() {
        return contaCorrenteBancPessoaFacade;
    }

    public ContaCorrenteBancariaFacade getContaCorrenteBancariaFacade() {
        return contaCorrenteBancariaFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public FinalidadePagamentoFacade getFinalidadePagamentoFacade() {
        return finalidadePagamentoFacade;
    }

    public ConfigContaBancariaPessoaFacade getConfigContaBancariaPessoaFacade() {
        return configContaBancariaPessoaFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        Pagamento pagamento = (Pagamento) entidadeContabil;
        if (pagamento.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            contabilizarPagamento(pagamento);
        } else if (pagamento.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.RESTO)) {
            contabilizarPagamentoResto(pagamento);
        }
    }

    public ParametroEvento getParametroEvento(EntidadeContabil entidadeContabil) {
        try {
            Pagamento entity = (Pagamento) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEvento(entity);
            ItemParametroEvento item = criarItemParametroEvento(entity, parametroEvento);
            adicionarObjetosParametros(entity, item);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    private void adicionarObjetosParametros(Pagamento entity, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), item));
        objetos.add(new ObjetoParametro(entity.getSubConta(), item));
        Preconditions.checkNotNull(entity.getLiquidacao().getEmpenho().getClasseCredor(), "A Classe credor do empenho não foi preenchida.");
        objetos.add(new ObjetoParametro(entity.getLiquidacao().getEmpenho().getClasseCredor(), item));
        if (entity.getLiquidacao().getEmpenho().getDividaPublica() != null) {
            objetos.add(new ObjetoParametro(entity.getLiquidacao().getEmpenho().getDividaPublica().getCategoriaDividaPublica(), item));
        }
        objetos.add(new ObjetoParametro(entity, item));
        item.setObjetoParametros(objetos);
    }

    private ItemParametroEvento criarItemParametroEvento(Pagamento entity, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValorFinal());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private ParametroEvento criarParametroEvento(Pagamento entity) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataPagamento());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setExercicio(entity.getExercicio());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId() == null ? null : entity.getId().toString());
        return parametroEvento;
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    public List<Pagamento> buscarPagamentosPorUnidadesExercicio(List<HierarquiaOrganizacional> listaUnidades, Exercicio exercicio, CategoriaOrcamentaria categoria) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }
        String sql = "SELECT p "
            + " FROM Pagamento p"
            + " WHERE  p.dataPagamento is not null "
            + " AND p.exercicio.id = :exercicio"
            + " and p.unidadeOrganizacional in ( :unidades )" +
            " and p.categoriaOrcamentaria = :categoria";
        Query q = em.createQuery(sql, Pagamento.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("unidades", unidades);
        q.setParameter("categoria", categoria);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(Pagamento.class, filtros);
        consulta.incluirJoinsComplementar(" inner join liquidacao liq on obj.liquidacao_id = liq.id ");
        consulta.incluirJoinsComplementar(" inner join empenho emp on liq.empenho_id = emp.id ");
        consulta.incluirJoinsOrcamentoDespesa(" emp.fontedespesaorc_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataPagamento)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataPagamento)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CATEGORIA_ORCAMENTARIA, "obj.categoriaorcamentaria");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "emp.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "emp.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numeroPagamento");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "obj.subConta_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public DesdobramentoFacade getDesdobramentoFacade() {
        return desdobramentoFacade;
    }


    public BigDecimal buscarValorPorFonteDespesaOrc(FonteDespesaORC fonteDespesaORC, Date dataInicial, Date dataFinal) {
        String sql = "select coalesce(sum(valor), 0) from ( " +
            " select coalesce(pag.valor, 0) as valor " +
            "  FROM Pagamento pag  " +
            " inner join liquidacao liq on pag.liquidacao_id = liq.id  " +
            " inner join empenho emp on liq.empenho_id = emp.id  " +
            " INNER JOIN FONTEDESPESAORC fontdesp ON emp.FONTEDESPESAORC_ID = fontdesp.ID  " +
            " where pag.categoriaorcamentaria = :categoria  " +
            " and fontdesp.id = :fontedespesaorc  " +
            " and trunc(pag.datapagamento) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') and pag.status <> 'ABERTO' " +
            "  " +
            " union all " +
            "  " +
            " select coalesce(est.valor, 0)*-1 as valor " +
            "  FROM Pagamentoestorno est  " +
            " inner join Pagamento pag on est.pagamento_id = pag.id " +
            " inner join liquidacao liq on pag.liquidacao_id = liq.id  " +
            " inner join empenho emp on liq.empenho_id = emp.id  " +
            " INNER JOIN FONTEDESPESAORC fontdesp ON emp.FONTEDESPESAORC_ID = fontdesp.ID  " +
            " where pag.categoriaorcamentaria = :categoria  " +
            " and fontdesp.id = :fontedespesaorc  " +
            " and trunc(est.dataestorno) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') and pag.status <> 'ABERTO' " +
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

    public List<Object[]> buscarPagamentos(Exercicio exercicio, Date dataOperacao, CategoriaOrcamentaria categoriaOrcamentaria) {
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
            "       valor, " +
            "       tipoResto " +
            "  from ( " +
            "select extract(year from pag.dataPagamento) as ano, " +
            "       extract(month from pag.dataPagamento) as mes, " +
            "       extract(day from pag.dataPagamento) as dia, " +
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
            "       coalesce(desdPag.valor, pag.valor) as valor, " +
            "       case when emp.tipoRestosProcessados = 'PROCESSADOS' then 1 else 2 end as tipoResto   " +
            "  from pagamento pag " +
            " inner join liquidacao liq on pag.LIQUIDACAO_ID = liq.id" +
            "  left join DesdobramentoPagamento desdPag on pag.id = desdPag.pagamento_id" +
            "  left join desdobramento desd on desd.id = desdPag.desdobramento_id" +
            " inner join empenho emp on liq.empenho_id = emp.id" +
            "  left join contrato cont on emp.contrato_id = cont.id " +
            " inner join fontedespesaorc fontOrc on emp.FONTEDESPESAORC_ID = fontOrc.id " +
            " inner join provisaoppafonte provFonte on fontOrc.PROVISAOPPAFONTE_ID = provFonte.id " +
            " inner join CONTADEDESTINACAO cd on provFonte.DESTINACAODERECURSOS_ID= cd.id " +
            " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_ID = fr.id " +
            " inner join despesaorc desp on emp.DESPESAORC_ID = desp.id " +
            " inner join provisaoppadespesa provDesp on desp.PROVISAOPPADESPESA_ID = provDesp.id " +
            " inner join conta c on coalesce(desd.conta_id, provDesp.CONTADEDESPESA_ID) = c.id " +
            " inner join VWHIERARQUIAORCAMENTARIA vw on pag.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
            " INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            " INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            " INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            " INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            " where TO_DATE(:dataOperacao, 'dd/mm/yyyy') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, TO_DATE(:dataOperacao, 'dd/mm/yyyy')) " +
            "   and pag.EXERCICIO_ID = :exercicio " +
            "   and pag.CATEGORIAORCAMENTARIA = :categoria " +
            "   and pag.status <> :aberto " +
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
            "       to_number(cont.numerotermo) as numerotermo, " +
            "       coalesce(desdEst.valor, est.valor) * -1 as valor, " +
            "       case when emp.tipoRestosProcessados = 'PROCESSADOS' then 1 else 2 end as tipoResto " +
            "  from pagamentoestorno est " +
            " inner join pagamento pag on pag.id = est.PAGAMENTO_ID" +
            " inner join liquidacao liq on pag.LIQUIDACAO_ID = liq.id" +
            "  left join DESDOBRAMENTOPAGAMESTORNO desdEst on est.id = desdEst.PAGAMENTOESTORNO_ID " +
            "  left join DesdobramentoPagamento desdPag on desdEst.desdobramentoPagamento_id = desdPag.id " +
            "  left join desdobramento desd on desd.id = desdPag.desdobramento_id " +
            " inner join empenho emp on liq.empenho_id = emp.id" +
            "  left join contrato cont on emp.contrato_id = cont.id " +
            " inner join fontedespesaorc fontOrc on emp.FONTEDESPESAORC_ID = fontOrc.id " +
            " inner join provisaoppafonte provFonte on fontOrc.PROVISAOPPAFONTE_ID = provFonte.id " +
            " inner join CONTADEDESTINACAO cd on provFonte.DESTINACAODERECURSOS_ID= cd.id " +
            " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_ID = fr.id " +
            " inner join despesaorc desp on emp.DESPESAORC_ID = desp.id " +
            " inner join provisaoppadespesa provDesp on desp.PROVISAOPPADESPESA_ID = provDesp.id " +
            " inner join conta c on coalesce(desd.conta_id, provDesp.CONTADEDESPESA_ID) = c.id " +
            " inner join VWHIERARQUIAORCAMENTARIA vw on est.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
            " INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            " INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            " INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            " INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            " where TO_DATE(:dataOperacao, 'dd/mm/yyyy') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, TO_DATE(:dataOperacao, 'dd/mm/yyyy')) " +
            "   and est.EXERCICIO_ID = :exercicio " +
            "   and pag.status <> :aberto " +
            "   and est.CATEGORIAORCAMENTARIA = :categoria) " +
            " order by dia, mes, ano ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("categoria", categoriaOrcamentaria.name());
        q.setParameter("aberto", StatusPagamento.ABERTO.name());
        return q.getResultList();
    }

    public List<NotaExecucaoOrcamentaria> buscarNotaDePagamento(String condicao, CategoriaOrcamentaria categoriaOrcamentaria, String nomeDaNota) {
        String sql = " SELECT " +
            "  nota.numeropagamento||'/'||exerc.ano as numero, " +
            "  coalesce(NOTA.DATAPAGAMENTO, NOTA.PREVISTOPARA) as data_nota, " +
            "  coalesce(nota.historiconota, '') || '. ' || coalesce(nota.COMPLEMENTOHISTORICO, '') as historico_nota, " +
            "  emp.tipoempenho as tipo, " +
            "  TRIM(vworg.CODIGO) AS CD_ORGAO, " +
            "  TRIM(vworg.DESCRICAO) AS DESC_ORAGAO, " +
            "  TRIM(vw.CODIGO) AS CD_UNIDADE, " +
            "  TRIM(vw.DESCRICAO) AS DESC_UNIDADE, " +
            "  f.codigo || '.' || sf.codigo || '.' || p.codigo || '.' || tpa.codigo || a.codigo || '.' || sub.codigo as CD_PROG_TRABALHO, " +
            "  ct_desp.codigo as elemento, " +
            "  ct_desp.descricao as especificao_despesa, " +
            "  coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " +
            "  formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "  fonte_r.codigo || ' - ' || trim(fonte_r.descricao)  || ' (' || cdest.codigo || ')' as desc_destinacao, " +
            "  NOTA.VALORFINAL as valor, " +
            "  frt_extenso_monetario(NOTA.VALORFINAL)||'  ***********************' as valor_extenso, " +
            "  COALESCE(ENDERECO.LOGRADOURO,'Pessoa não possui Logradouro') AS LOGRADOURO, " +
            "  coalesce(endereco.localidade,'Pessoa não possui Localidade') as localidade, " +
            "  coalesce(ag.nomeagencia,'Pessoa não possui agência cadastrada') as nomeagencia, " +
            "  coalesce(banco.descricao,'Pessoa não possui banco cadastrado') as descricao, " +
            "  banco.numerobanco as numerobanco, " +
            "  ag.numeroagencia as numeroagencia, " +
            "  AG.DIGITOVERIFICADOR as digitoverificador, " +
            "  cc.numeroconta as numerocontacorrente, " +
            "  cc.digitoverificador as digitoverificadorcontacorrente, " +
            "  cbe.TIPOCONTABANCARIA as tipocontabancaria, " +
            "  cbe.SITUACAO as situacaocontabancaria, " +
            "  coalesce(banco.numerobanco||' / '||ag.numeroagencia || '-' || AG.DIGITOVERIFICADOR ||' / '||cc.numeroconta||'-'||cc.digitoverificador,'Pessoa sem Dados Bancarios') as banco_agencia_conta, " +
            "  bco.numerobanco as banco, " +
            "  A.DESCRICAO AS DESC_ACAO, " +
            "  coalesce(lic.numero, disp.numeroDaDispensa, regext.numeromodalidade)  || ' - Processo Licitatório ' || coalesce(lic.numerolicitacao, disp.numerodadispensa, regext.numeroregistro) || ' - ' || to_char(coalesce(lic.emitidaem, disp.datadadispensa, regext.dataregistrocarona),'dd/MM/YYYY') as modalidade, " +
            "  emp.MODALIDADELICITACAO as MODALIDADELICITACAO, " +
            "  classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " +
            "  COALESCE(LIQ.valor,0) - coalesce((SELECT SUM(EST.VALOR) FROM LIQUIDACAOESTORNO EST WHERE liquidacao_ID = nota.liquidacao_id), 0) + (SELECT COALESCE(SUM(RET.VALOR), 0) AS VALOR_RETENCAO FROM RETENCAOPGTO RET WHERE RET.PAGAMENTO_ID = nota.id) - coalesce(nota.valor, 0) AS SALDO_PAGAR, " +
            "  COALESCE(LIQ.VALOR,0) - coalesce((SELECT SUM(EST.VALOR) FROM LIQUIDACAOESTORNO EST WHERE liquidacao_ID = nota.liquidacao_id), 0) AS VALOR_LIQUIDADO, " +
            "  COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "  COALESCE(ENDERECO.UF,'sem UF ') AS UF, " +
            "  COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP," +
            "  coalesce(bco.numerobanco||' / '||age.numeroagencia || '-' || age.DIGITOVERIFICADOR ||' / '||cbe.numeroconta||'-'||cbe.digitoverificador || '/' || sc.codigo || '-' || sc.descricao,' ') as contaFinanceira, " +
            "  VW.SUBORDINADA_ID as idUnidadeOrcamentaria, " +
            "  NOTA.ID as idNota " +
            "   FROM PAGAMENTO NOTA " +
            "  INNER JOIN LIQUIDACAO LIQ ON LIQ.ID = nota.liquidacao_id " +
            "  INNER JOIN EMPENHO EMP ON LIQ.EMPENHO_ID = EMP.ID " +
            (categoriaOrcamentaria.isResto() ? " inner join empenho emp_original on emp.empenho_id = emp_original.id " : "") +
            "  INNER JOIN fontedespesaorc FONTE ON EMP.fontedespesaorc_id = fonte.id " +
            "  INNER JOIN despesaorc DESPESA ON fonte.despesaorc_id = despesa.id " +
            "  INNER JOIN provisaoppadespesa PROVISAO ON despesa.provisaoppadespesa_id= provisao.id " +
            "  inner join provisaoppafonte ppf on ppf.id = fonte.PROVISAOPPAFONTE_ID " +
            "  INNER JOIN SUBACAOPPA SUB ON SUB.ID = provisao.subacaoppa_id " +
            "  INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "  INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "  INNER JOIN programappa P ON P.ID = A.programa_id " +
            "  INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "  INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "  INNER JOIN CONTA CT_DESP ON provisao.contadedespesa_id = CT_DESP.ID " +
            "  inner join subconta sc on nota.subconta_id = sc.id " +
            "  inner join contabancariaentidade cbe on sc.CONTABANCARIAENTIDADE_ID = cbe.id " +
            "  inner join agencia age on cbe.agencia_id = age.id " +
            "  inner join banco bco on age.banco_id = bco.id " +
            "  INNER JOIN EXERCICIO EXERC ON EMP.EXERCICIO_ID = EXERC.ID " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            "  inner join VWHIERARQUIAORCAMENTARIA vworg on vw.superior_id = VWorg.SUBORDINADA_ID " +
            "  INNER JOIN PESSOA Pes ON emp.fornecedor_id = Pes.id " +
            "  left join pessoafisica pf on pes.id = pf.id " +
            "  left join pessoajuridica pj on pes.id = pj.id " +
            "  left join enderecocorreio endereco on pes.enderecoprincipal_id = endereco.id " +
            "  left join ContaCorrenteBancaria cc on nota.contapgto_id = cc.id " +
            "  left join agencia ag on cc.agencia_id =ag.id " +
            "  left join banco banco on ag.banco_id=banco.id " +
            "  left join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id " +
            "  left join conta cdest on cdest.id = cd.id " +
            "  left join fontederecursos fonte_r on fonte_r.id = cd.fontederecursos_id " +
            (categoriaOrcamentaria.isResto() ? "  and fonte_r.exercicio_id = emp_original.exercicio_id " : "  and fonte_r.exercicio_id = exerc.id ") +
            "  left join classecredor classe on emp.classecredor_id = classe.id " +
            "  LEFT JOIN CONTRATO CONTRATO ON EMP.CONTRATO_ID = CONTRATO.ID " +
            " left join conlicitacao conlic on conlic.contrato_id = contrato.id " +
            " left join licitacao lic on lic.id = conlic.licitacao_id " +
            "  left join condispensalicitacao conDisp on condisp.contrato_id = contrato.id " +
            "  left join dispensadelicitacao disp on disp.id = condisp.dispensadelicitacao_id " +
            "  left join CONREGPRECOEXTERNO conReg on conreg.contrato_id = contrato.id " +
            "  left join REGISTROSOLMATEXT regExt on regExt.id = conreg.regsolmatext_id " +
            "   WHERE " +
            "  (trunc(NOTA.DATAPAGAMENTO) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(NOTA.DATAPAGAMENTO)) " +
            "   OR " +
            "   trunc(NOTA.previstopara) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(NOTA.previstopara))) " +
            "  AND " +
            "  (trunc(NOTA.DATAPAGAMENTO) BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, trunc(NOTA.DATAPAGAMENTO)) " +
            "   OR " +
            "   trunc(NOTA.previstopara) BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, trunc(NOTA.previstopara)) " +
            "  ) " +
            "  AND NOTA.CATEGORIAORCAMENTARIA = :categoriaOrcamentaria " +
            condicao;
        Query q = em.createNativeQuery(sql);
        q.setParameter("categoriaOrcamentaria", categoriaOrcamentaria.name());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setNomeDaNota(nomeDaNota);
                nota.setNumeroDocumento((String) obj[0]);
                nota.setDataPagamento(DataUtil.getDataFormatada((Date) obj[1]));
                nota.setHistorico((String) obj[2] + " Usuário Emissor: " + sistemaFacade.getUsuarioCorrente().getNome());
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
                nota.setDescricaoAgencia((String) obj[18]);
                nota.setDescricaoBanco((String) obj[19]);
                nota.setNumeroBanco((String) obj[20]);
                nota.setNumeroAgencia((String) obj[21]);
                nota.setDigitoVerificador((String) obj[22]);
                nota.setNumeroContaCorrente((String) obj[23]);
                nota.setDigitoVerificadorContaCorrente((String) obj[24]);
                nota.setTipoContaBancaria(obj[25] != null ? TipoContaBancariaDTO.valueOf((String) obj[25]).getDescricao() : "");
                nota.setSituacaoContaBancaria(obj[26] != null ? SituacaoContaDTO.valueOf((String) obj[26]).getDescricao() : "");
                nota.setBancoAgenciaConta((String) obj[27]);
                nota.setBancoContaBancariaEntidade((String) obj[28]);
                nota.setDescricaoProjetoAtividade((String) obj[29]);
                nota.setModalidadeLicitacao((obj[31] != null ? ModalidadeLicitacaoEmpenhoDTO.valueOf((String) obj[31]).getDescricao() : "") + " " + obj[30]);
                nota.setClassePessoa((String) obj[32]);
                if (categoriaOrcamentaria.isResto()) {
                    nota.setSaldoAnterior((BigDecimal) obj[33]);
                    nota.setSaldoAtual((BigDecimal) obj[34]);
                } else {
                    nota.setSaldoAtual((BigDecimal) obj[33]);
                    nota.setValorLiquidado((BigDecimal) obj[34]);
                }
                nota.setBairro((String) obj[35]);
                nota.setUf((String) obj[36]);
                nota.setCep((String) obj[37]);
                nota.setContaFinanceira((String) obj[38]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[39]);
                nota.setId(((BigDecimal) obj[40]).longValue());
                nota.setRetencoes(buscarRetencoes(nota.getId()));
                nota.setFaturas(buscarFaturas(nota.getId()));
                nota.setConvenios(buscarConvenios(nota.getId()));
                nota.setGps(buscarGps(nota.getId()));
                nota.setDarfs(buscarDarfs(nota.getId()));
                nota.setDarfsSimples(buscarDarfsSimples(nota.getId()));
                retorno.add(nota);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaRetencao> buscarRetencoes(Long idPagamento) {
        String sql = " select " +
            "        'Nº ' || coalesce(re.numero, ret.numero) as numero, " +
            "        re.datareceita as dataRet, " +
            "       'Conta Extra: ' || C.CODIGO || ' - ' || TRIM(C.DESCRICAO) AS contaExtra, " +
            "       COALESCE(RET.VALOR, 0) AS VALOR_RETENCAO " +
            " FROM RETENCAOPGTO RET " +
            " LEFT JOIN RECEITAEXTRA RE ON RET.ID = RE.RETENCAOPGTO_ID " +
            " INNER JOIN CONTA C ON C.ID = RET.CONTAEXTRAORCAMENTARIA_ID " +
            " inner join subconta sub on sub.id = ret.subconta_id " +
            " WHERE RET.PAGAMENTO_ID = :idPagamento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamento", idPagamento);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaRetencao> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaRetencao retencao = new NotaExecucaoOrcamentariaRetencao();
                retencao.setNumero((String) obj[0]);
                retencao.setDataRet((Date) obj[1]);
                retencao.setContaExtra((String) obj[2]);
                retencao.setValor((BigDecimal) obj[3]);
                retorno.add(retencao);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaFatura> buscarFaturas(Long idPagamento) {
        String sql = " select " +
            "       'Fatura' as tipoGuia, " +
            "       fat.codigobarra as codigobarra, " +
            "       fat.datavencimento as datavencimento, " +
            "       (coalesce(fat.valornominal, 0) - coalesce(fat.valordescontos, 0)) + coalesce(fat.valorjuros, 0) as valor " +
            " from guiafatura fat " +
            " inner join guiapagamento guia on fat.id = guia.guiafatura_id " +
            " inner join pagamento pag on pag.id = guia.pagamento_id " +
            " where pag.id = :idPagamento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamento", idPagamento);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaFatura> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaFatura fatura = new NotaExecucaoOrcamentariaFatura();
                fatura.setTipoGuia((String) obj[0]);
                fatura.setCodigoBarra((String) obj[1]);
                fatura.setDataVencimento((Date) obj[2]);
                fatura.setValor((BigDecimal) obj[3]);
                retorno.add(fatura);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaConvenio> buscarConvenios(Long idPagamento) {
        String sql = " select " +
            "       'Guia Convênio' as tipoGuia, " +
            "       conv.codigobarra as codigobarra " +
            " from guiaconvenio conv " +
            " inner join guiapagamento guia on conv.id = guia.guiaconvenio_id " +
            " inner join pagamento pag on pag.id = guia.pagamento_id " +
            " where pag.id = :idPagamento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamento", idPagamento);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaConvenio> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaConvenio convenio = new NotaExecucaoOrcamentariaConvenio();
                convenio.setTipoGuia((String) obj[0]);
                convenio.setCodigoBarra((String) obj[1]);
                retorno.add(convenio);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaGps> buscarGps(Long idPagamento) {
        String sql = " select " +
            "       'Guia GPS' as tipoGuia, " +
            "       gps.codigoreceitatributo as coditotributo, " +
            "       gps.periodocompetencia as periodoComp, " +
            "       gps.codigoidentificacaotributo as digitotributo, " +
            " coalesce(gps.valorprevistoinss, 0) + coalesce(gps.atualizacaomonetaria, 0) + coalesce(gps.valoroutrasentidades, 0) as valor " +
            " from guiagps gps " +
            " inner join guiapagamento guia on gps.id = guia.guiagps_id " +
            " inner join pagamento pag on pag.id = guia.pagamento_id " +
            " where pag.id = :idPagamento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamento", idPagamento);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaGps> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaGps gps = new NotaExecucaoOrcamentariaGps();
                gps.setTipoGuia((String) obj[0]);
                gps.setCodigoTributo((String) obj[1]);
                gps.setPeriodoComp((Date) obj[2]);
                gps.setDigitoTributo((String) obj[3]);
                gps.setValor((BigDecimal) obj[4]);
                retorno.add(gps);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDarf> buscarDarfs(Long idPagamento) {
        String sql = " select " +
            "       'Guia Darf' as tipoGuia, " +
            "       darf.datavencimento as datavencimento, " +
            "       darf.codigoreceitatributo as coditotributo, " +
            "       darf.codigoidentificacaotributo as codigoidentificador, " +
            " coalesce(darf.valorprincipal, 0) + coalesce(darf.valormulta, 0) + coalesce(darf.valorjuros, 0) as valorPrincipal " +
            " from guiadarf darf " +
            " inner join guiapagamento guia on darf.id = guia.guiadarf_id " +
            " inner join pagamento pag on pag.id = guia.pagamento_id " +
            " where pag.id = :idPagamento  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamento", idPagamento);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDarf> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDarf darf = new NotaExecucaoOrcamentariaDarf();
                darf.setTipoGuia((String) obj[0]);
                darf.setDataVencimento((Date) obj[1]);
                darf.setCodigoTributo((String) obj[2]);
                darf.setCodigoIdentificador((String) obj[3]);
                darf.setValorPrincipal((BigDecimal) obj[4]);
                retorno.add(darf);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDarf> buscarDarfsSimples(Long idPagamento) {
        String sql = " select " +
            "       'Guia Darf Simples' as tipoguia, " +
            "       darf.periodoapuracao as periodoApuracao, " +
            "       darf.codigoreceitatributo as coditotributo, " +
            "       darf.codigoidentificacaotributo as codigoidentificador, " +
            "       coalesce(darf.valorprincipal, 0) + coalesce(darf.valormulta, 0) + coalesce(darf.valorjuros, 0) as valorPrincipal " +
            " from guiadarfsimples darf " +
            " inner join guiapagamento guia on darf.id = guia.guiadarfsimples_id " +
            " inner join pagamento pag on pag.id = guia.pagamento_id " +
            " where pag.id = :idPagamento  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamento", idPagamento);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDarf> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDarf ds = new NotaExecucaoOrcamentariaDarf();
                ds.setTipoGuia((String) obj[0]);
                ds.setPeriodoApuracao((Date) obj[1]);
                ds.setCodigoTributo((String) obj[2]);
                ds.setCodigoIdentificador((String) obj[3]);
                ds.setValorPrincipal((BigDecimal) obj[4]);
                retorno.add(ds);
            }
        }
        return retorno;
    }

    public boolean hasPagamentoNaoEstornadoParaLiquidacao(Liquidacao liquidacao) {
        if (liquidacao == null || liquidacao.getId() == null) return false;
        String sql = " select pag.id " +
            "from pagamento pag " +
            "         left join pagamentoestorno estpag on estpag.pagamento_id = pag.id " +
            "where pag.liquidacao_id = :idLiquidacao " +
            "  and estpag.id is null ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLiquidacao", liquidacao.getId());
        List<BigDecimal> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public BigDecimal getRetencaoMaximaContaExtra(Conta conta){
        return configuracaoContabilFacade.buscarRetencaoMaximaReinfPorContaExtraETipoArquivo(conta, null);
    }
    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public Pagamento salvarRetornandoEmJdbc(Pagamento selecionado){
        RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
        return jdbcPagamento.salvar(selecionado, rev.getId());
    }

    public Pagamento atualizarRetornandoEmJdbc(Pagamento selecionado) {
        RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
        return jdbcPagamento.atualizar(selecionado, rev.getId());
    }

    public CodigoCOFacade getCodigoCOFacade() {
        return codigoCOFacade;
    }
}
