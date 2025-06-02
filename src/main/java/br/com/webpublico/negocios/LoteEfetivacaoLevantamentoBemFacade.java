package br.com.webpublico.negocios;

import br.com.webpublico.controle.LoteEfetivacaoLevantamentoBemControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.patrimonio.EfetivacaoDepreciacaoBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.util.AssistenteBarraProgressoLoteEfetivacaoBem;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 04/12/13
 * Time: 16:16
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class LoteEfetivacaoLevantamentoBemFacade extends AbstractFacade<LoteEfetivacaoLevantamentoBem> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;

    public LoteEfetivacaoLevantamentoBemFacade() {
        super(LoteEfetivacaoLevantamentoBem.class);
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IntegradorPatrimonialContabilFacade getIntegradorPatrimonialContabilFacade() {
        return integradorPatrimonialContabilFacade;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteBarraProgressoLoteEfetivacaoBem> salvarLoteEfetivacaoLevantamentoBem(AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso, List<EfetivacaoLevantamentoBem> efetivacoes, LoteEfetivacaoLevantamentoBem lote) {
        try {
            List<EfetivacaoLevantamentoBem> efetivacoesParticionadas = Lists.newArrayList();
            efetivacoesParticionadas.addAll(efetivacoes);

            int contador = 0;
            List<EfetivacaoLevantamentoBem> objetoParaMergiar = Lists.newArrayList();
            for (EfetivacaoLevantamentoBem elb : efetivacoesParticionadas) {
                contador++;
                elb.setBem(elb.getBem());
                elb.getLevantamento().setBem(elb.getBem());
                elb.setEstadoResultante(em.merge(elb.getEstadoResultante()));
                elb.setLote(lote);
                assistenteBarraProgresso.conta();
                objetoParaMergiar.add(elb);
                if (contador >= 100) {
                    contador = 0;
                    mergearEfetivacoesPorParte(objetoParaMergiar);
                    lote.getEfetivacoes().addAll(objetoParaMergiar);
                    objetoParaMergiar.clear();
                }
            }
            mergearEfetivacoesPorParte(objetoParaMergiar);
            lote.getEfetivacoes().addAll(objetoParaMergiar);
        } catch (Exception e) {
            logger.error("problemas na efetivacao de levantamento do bem movel " + e.getMessage());
        }
        assistenteBarraProgresso.setLote(lote);
        return new AsyncResult(assistenteBarraProgresso);
    }

    private void mergearEfetivacoesPorParte(List<EfetivacaoLevantamentoBem> objetoParaMergiar) {
        for (EfetivacaoLevantamentoBem objetos : objetoParaMergiar) {
            em.merge(objetos);
        }
    }


    @Override
    public LoteEfetivacaoLevantamentoBem recuperar(Object id) {
        LoteEfetivacaoLevantamentoBem lote = super.recuperar(id);
        lote.getInformacoes().size();
        return lote;
    }

    public void atualizar(LoteEfetivacaoLevantamentoBem lote) {
        em.merge(lote);
    }

    public void validarAssociacaoDosGruposObjetoCompraComGrupoBem(List<LevantamentoBemPatrimonial> levantamentos, Date dataOperacao) {
        ValidacaoException ve = new ValidacaoException();
        Map<GrupoObjetoCompra, String> msgs = new HashMap<>();

        for (LevantamentoBemPatrimonial lev : levantamentos) {
            GrupoObjetoCompra grupo = lev.getItem().getGrupoObjetoCompra();

            if (grupoObjetoCompraGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(grupo, dataOperacao) == null) {
                if (!msgs.containsKey(grupo)) {
                    String mensagem = "O grupo de objeto de compra " + grupo + " não possui nenhuma associação com grupo de bem patrimonial.";
                    msgs.put(grupo, mensagem);
                    ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, mensagem);
                }
            }
        }

        ve.lancarException();
    }

    public EfetivacaoLevantamentoBem getEfetivacaoDoLevantamento(LevantamentoBemPatrimonial levantamento) {
        String sql = "from EfetivacaoLevantamentoBem where levantamento = :levantamento";

        Query q = em.createQuery(sql);
        q.setParameter("levantamento", levantamento);

        List resultList = q.getResultList();

        if (resultList.isEmpty()) {
            return null;
        }

        return (EfetivacaoLevantamentoBem) resultList.get(0);
    }

    public EfetivacaoLevantamentoBem salvarEfetivacao(EfetivacaoLevantamentoBem efetivacaoLevantamentoBem) {
        return em.merge(efetivacaoLevantamentoBem);
    }

    public EstornoEfetivacaoLevantamentoMovel salvarEstornoDaEfetivacaoLevantamentoMovel(EstornoEfetivacaoLevantamentoMovel estorno) {
        return em.merge(estorno);
    }

    public BigDecimal saldoGrupoBemDepreciacaoContabil(UnidadeOrganizacional orcamentaria, GrupoBem grupoBem, Date datareferencia) {
        String sql =
            " SELECT COALESCE(SUM(COALESCE(SALDO.CREDITO, 0) - COALESCE(SALDO.DEBITO, 0)), 0) as total " +
                " FROM SALDOGRUPOBEM SALDO" +
                " WHERE SALDO.TIPOGRUPO = :bem_movel_principal" +
                "  AND SALDO.NATUREZATIPOGRUPOBEM = :depreciacao" +
                "  AND SALDO.UNIDADEORGANIZACIONAL_ID = :unidadeorcamentaria" +
                "  AND GRUPOBEM_ID = :grupo" +
                "  AND trunc(SALDO.DATASALDO) =" +
                "    (SELECT MAX(trunc(SLD.DATASALDO))" +
                "     FROM SALDOGRUPOBEM SLD" +
                "     WHERE SLD.UNIDADEORGANIZACIONAL_ID = SALDO.UNIDADEORGANIZACIONAL_ID" +
                "       AND SLD.TIPOGRUPO = SALDO.TIPOGRUPO" +
                "       AND SLD.NATUREZATIPOGRUPOBEM = SALDO.NATUREZATIPOGRUPOBEM" +
                "       AND SLD.GRUPOBEM_ID = SALDO.GRUPOBEM_ID" +
                "       AND trunc(SLD.DATASALDO) <= to_date(:DATAREFERENCIA, 'dd/MM/yyyy'))";
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidadeorcamentaria", orcamentaria.getId());
        q.setParameter("grupo", grupoBem.getId());
        q.setParameter("depreciacao", NaturezaTipoGrupoBem.DEPRECIACAO.name());
        q.setParameter("bem_movel_principal", TipoGrupo.BEM_MOVEL_PRINCIPAL.name());
        q.setParameter("DATAREFERENCIA", DataUtil.getDataFormatada(datareferencia));
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException | IndexOutOfBoundsException ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal saldoGrupoBemDepreciacao(UnidadeOrganizacional orcamentaria, GrupoBem grupoBem, Date dataOperacao) {
        if (grupoBem == null) {
            return BigDecimal.ZERO;
        }
        String sql =
            "    select " +
                "   coalesce(SUM(EST.VALORACUMULADODADEPRECIACAO),0)" +
                " FROM BEM" +
                "   INNER JOIN EVENTOBEM EV ON EV.BEM_ID = BEM.ID" +
                "   INNER JOIN estadobem est ON est.id = ev.estadoresultante_id" +
                " WHERE EST.ESTADOBEM <> :BAIXADO" +
                "   AND EV.DATAOPERACAO = " +
                "       (SELECT MAX(EV1.DATAOPERACAO) " +
                "        FROM EVENTOBEM EV1" +
                "        WHERE ev1.bem_id = bem.id " +
                "          and trunc(EV1.datalancamento) <= to_date(:DATAREFERENCIA, 'dd/MM/yyyy')) " +
                "          AND EST.DETENTORAORCAMENTARIA_ID = :ORCAMENTARIA " +
                "          and EST.GRUPOBEM_ID = :GRUPO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ORCAMENTARIA", orcamentaria.getId());
        q.setParameter("GRUPO", grupoBem.getId());
        q.setParameter("BAIXADO", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("DATAREFERENCIA", DataUtil.getDataFormatada(dataOperacao));
        return (BigDecimal) q.getResultList().get(0);
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public EstadoBem salvarEstadoResultanteEfetivacaoDepreciacao(EstadoBem estado) {
        return em.merge(estado);
    }

    public void salvarEfetivacaoDepreciacaoBem(EfetivacaoDepreciacaoBem efetivacao) {
        em.persist(efetivacao);
    }

    public LoteEfetivacaoLevantamentoBem salvarLoteEfetivacaoLevantamento(LoteEfetivacaoLevantamentoBem lote) {
        return em.merge(lote);
    }

    public void atualizarSituacaoLoteEstornado(LoteEfetivacaoLevantamentoBem lote) {
        em.createNativeQuery("update LOTEEFETLEVANTBEM set situacao = '" + SituacaoEventoBem.ESTORNADO.name() + "' where id = " + lote.getId()).executeUpdate();
    }

    public Boolean hasEfetivacaoRealizadaParaUnidade(UnidadeOrganizacional unidadeOrganizacional, LoteEfetivacaoLevantamentoBem selecionado) {
        if (unidadeOrganizacional == null) {
            return Boolean.FALSE;
        }
        String sql = "Select 1" +
            "           from LOTEEFETLEVANTBEM ef " +
            "         where ef.unidadeOrcamentaria_id = :unidade" +
            "           and ef.situacao <> :estornado";
        if (selecionado.getId() != null) {
            sql += " and ef.id <> :lote";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("estornado", SituacaoEventoBem.ESTORNADO.name());
        if (selecionado.getId() != null) {
            q.setParameter("lote", selecionado.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public BigDecimal totalPorGrupoPatrimonialAndOrcamentaria(GrupoBem grupoBem, UnidadeOrganizacional orcamentaria, Date dataOperacao) {
        if (grupoBem == null) {
            return BigDecimal.ZERO;
        }
        String sql = "    " +
            " select " +
            "   coalesce(SUM(EST.VALORORIGINAL),0)" +
            " FROM BEM" +
            "   INNER JOIN EVENTOBEM EV ON EV.BEM_ID = BEM.ID" +
            "   INNER JOIN estadobem est ON est.id = ev.estadoresultante_id" +
            " WHERE EST.ESTADOBEM <> :BAIXADO" +
            "   AND EV.DATAOPERACAO = " +
            "       (SELECT MAX(EV1.DATAOPERACAO) " +
            "        FROM EVENTOBEM EV1" +
            "        WHERE ev1.bem_id = bem.id " +
            "           and trunc(EV1.datalancamento) <= to_date(:DATAREFERENCIA, 'dd/MM/yyyy')) " +
            "          AND EST.DETENTORAORCAMENTARIA_ID = :ORCAMENTARIA " +
            "          and EST.GRUPOBEM_ID = :GRUPO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ORCAMENTARIA", orcamentaria.getId());
        q.setParameter("GRUPO", grupoBem.getId());
        q.setParameter("BAIXADO", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("DATAREFERENCIA", DataUtil.getDataFormatada(dataOperacao));
        return (BigDecimal) q.getResultList().get(0);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public List<Object[]> gruposEfetivados(TipoBem tipoBem, UnidadeOrganizacional orcamentaria, Date dataOperacao, List<GrupoBem> listaGrupos) {
        String sql =
            "SELECT     GRUPOBEM.ID AS GRUPOPATRIMONIAL," +
                "       GRUPOBEM.codigo AS CODIGOGRUPO," +
                "       SUM(QUANTIDADE) AS QUANTIDADE," +
                "       COALESCE(SUM(VL_CONTABIL * -1), 0) AS VALORCONTABIL," +
                "       COALESCE(SUM(VL_LEVANTAMENTO), 0) AS VALORLEVANTAMENTO," +
                "       COALESCE(SUM(VL_ADMINISTRATIVO), 0) AS VALORADMINISTRATIVO," +
                "       COALESCE(SUM(VL_DEPRECIACAO_CONT), 0) AS VL_DEPRECIACAO_CONTABIL," +
                "       COALESCE(SUM(VL_DEPRECIACAO_BEM), 0) AS VL_DEPRECIACAO_BEM, " +
                "       (COALESCE(SUM(VL_CONTABIL * -1), 0) - (COALESCE(SUM(VL_LEVANTAMENTO), 0) + COALESCE(SUM(VL_ADMINISTRATIVO), 0))) AS DIFERENCA" +
                " FROM" +
                "  (SELECT COUNT(LEV.ID) AS QUANTIDADE," +
                "          SUM(LEV.VALORBEM) AS VL_LEVANTAMENTO," +
                "          0 AS VL_CONTABIL," +
                "          VWORC.SUBORDINADA_ID AS UNIDADE," +
                "          ASSOCIACAO.GRUPOBEM_ID AS GRUPOBEM," +
                "          0 AS VL_ADMINISTRATIVO," +
                "          0 AS VL_DEPRECIACAO_CONT," +
                "          0 AS VL_DEPRECIACAO_BEM" +
                "   FROM LEVANTAMENTOBEMPATRIMONIAL LEV" +
                "   INNER JOIN OBJETOCOMPRA OBJ ON OBJ.ID = LEV.ITEM_ID" +
                "   INNER JOIN GRUPOOBJCOMPRAGRUPOBEM ASSOCIACAO ON ASSOCIACAO.GRUPOOBJETOCOMPRA_ID = OBJ.GRUPOOBJETOCOMPRA_ID" +
                "   INNER JOIN VWHIERARQUIAADMINISTRATIVA VW ON VW.SUBORDINADA_ID = LEV.UNIDADEADMINISTRATIVA_ID" +
                "   INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = LEV.UNIDADEORCAMENTARIA_ID" +
                "   WHERE to_date(:DATAREFERENCIA, 'dd/MM/yyyy') BETWEEN trunc(VW.INICIOVIGENCIA) AND COALESCE(trunc(VW.FIMVIGENCIA), to_date(:DATAREFERENCIA, 'dd/MM/yyyy'))" +
                "     AND to_date(:DATAREFERENCIA, 'dd/MM/yyyy') BETWEEN trunc(VWORC.INICIOVIGENCIA) AND COALESCE(trunc(VWORC.FIMVIGENCIA), to_date(:DATAREFERENCIA, 'dd/MM/yyyy'))" +
                "     AND to_date(:DATAREFERENCIA, 'dd/MM/yyyy') BETWEEN trunc(ASSOCIACAO.INICIOVIGENCIA) AND COALESCE(trunc(ASSOCIACAO.FIMVIGENCIA), to_date(:DATAREFERENCIA, 'dd/MM/yyyy'))" +
                "     AND NOT EXISTS" +
                "       (SELECT 1" +
                "        FROM EFETIVACAOLEVANTAMENTOBEM EFET" +
                "        INNER JOIN EVENTOBEM EVB_EFET ON EVB_EFET.ID = EFET.ID" +
                "        AND TRUNC(EVB_EFET.DATALANCAMENTO) <= to_date(:DATAREFERENCIA, 'dd/MM/yyyy') " +
                "        WHERE EFET.LEVANTAMENTO_ID = LEV.ID" +
                "          AND NOT EXISTS" +
                "            (SELECT 1" +
                "             FROM ESTORNOEFETIVACAOLEVMOVEL ESTORNO" +
                "             INNER JOIN EVENTOBEM EVB_ESTORNO ON EVB_ESTORNO.ID = ESTORNO.ID" +
                "             AND TRUNC(EVB_ESTORNO.DATALANCAMENTO) <= to_date(:DATAREFERENCIA, 'dd/MM/yyyy') " +
                "             WHERE ESTORNO.EFETIVACAOLEVANTAMENTOBEM_ID = EFET.ID ))" +
                "     AND VWORC.SUBORDINADA_ID = :ORCAMENTARIA" +
                "   GROUP BY ASSOCIACAO.GRUPOBEM_ID," +
                "            VWORC.SUBORDINADA_ID" +
                "   UNION ALL SELECT 0 AS QUANTIDADE," +
                "                    0 AS VL_LEVANTAMENTO," +
                "                    COALESCE(SUM(CREDITO), 0) - COALESCE(SUM(DEBITO), 0) AS VL_CONTABIL," +
                "                    UNIDADE AS UNIDADE," +
                "                    GRUPO AS GRUPO," +
                "                    0 AS VL_ADMINISTRATIVO," +
                "                    0 AS VL_DEPRECIACAO_CONT," +
                "                    0 AS VL_DEPRECIACAO_BEM" +
                "   FROM" +
                "     (SELECT COALESCE(SALDO.CREDITO, 0) AS CREDITO," +
                "             COALESCE(SALDO.DEBITO, 0) AS DEBITO," +
                "             SALDO.UNIDADEORGANIZACIONAL_ID AS UNIDADE," +
                "             SALDO.GRUPOBEM_ID AS GRUPO" +
                "      FROM SALDOGRUPOBEM SALDO" +
                "      WHERE SALDO.TIPOGRUPO = 'BEM_MOVEL_PRINCIPAL'" +
                "        AND SALDO.NATUREZATIPOGRUPOBEM = 'ORIGINAL'" +
                "        AND SALDO.UNIDADEORGANIZACIONAL_ID = :ORCAMENTARIA" +
                "        AND trunc(SALDO.DATASALDO) =" +
                "          (SELECT MAX(trunc(SLD.DATASALDO))" +
                "           FROM SALDOGRUPOBEM SLD" +
                "           WHERE SLD.UNIDADEORGANIZACIONAL_ID = SALDO.UNIDADEORGANIZACIONAL_ID" +
                "             AND SLD.TIPOGRUPO = SALDO.TIPOGRUPO" +
                "             AND SLD.NATUREZATIPOGRUPOBEM = SALDO.NATUREZATIPOGRUPOBEM" +
                "             AND SLD.GRUPOBEM_ID = SALDO.GRUPOBEM_ID" +
                "             AND trunc(SLD.DATASALDO) <= to_date(:DATAREFERENCIA, 'dd/MM/yyyy') ) )" +
                "   GROUP BY UNIDADE," +
                "            GRUPO" +
                "   UNION SELECT 0 AS QUANTIDADE," +
                "                0 AS VL_LEVANTAMENTO," +
                "                0 AS VL_CONTABIL," +
                "                EST.DETENTORAORCAMENTARIA_ID AS UNIDADE," +
                "                EST.GRUPOBEM_ID AS GRUPO," +
                "                COALESCE(SUM(EST.VALORORIGINAL), 0) AS VL_ADMINISTRATIVO," +
                "                0 AS VL_DEPRECIACAO_CONT," +
                "                COALESCE(SUM(EST.VALORACUMULADODADEPRECIACAO) , 0) AS VL_DEPRECIACAO_BEM " +
                "   FROM BEM" +
                "   INNER JOIN EVENTOBEM EV ON EV.BEM_ID = BEM.ID" +
                "   INNER JOIN estadobem est ON est.id = ev.estadoresultante_id" +
                "   WHERE EST.ESTADOBEM <> :BAIXADO" +
                "     AND EV.DATAOPERACAO =" +
                "       (SELECT MAX(EV1.DATAOPERACAO)" +
                "        FROM EVENTOBEM EV1" +
                "        WHERE ev1.bem_id = bem.id" +
                "          AND trunc(EV1.DATALANCAMENTO) <= to_date(:DATAREFERENCIA, 'dd/MM/yyyy') )" +
                "     AND EST.DETENTORAORCAMENTARIA_ID = :ORCAMENTARIA" +
                "   GROUP BY EST.GRUPOBEM_ID," +
                "            EST.DETENTORAORCAMENTARIA_ID" +
                "   UNION SELECT 0 AS QUANTIDADE," +
                "                0 AS VL_LEVANTAMENTO," +
                "                0 AS VL_CONTABIL," +
                "                UNIDADE AS UNIDADE," +
                "                GRUPO AS GRUPO," +
                "                0 AS VL_ADMINISTRATIVO," +
                "                COALESCE(SUM(CREDITO), 0) - COALESCE(SUM(DEBITO), 0) AS VL_DEPRECIACAO_CONT," +
                "                0 AS VL_DEPRECIACAO_BEM" +
                "   FROM" +
                "     (SELECT COALESCE(SALDO.CREDITO, 0) AS CREDITO," +
                "             COALESCE(SALDO.DEBITO, 0) AS DEBITO," +
                "             SALDO.UNIDADEORGANIZACIONAL_ID AS UNIDADE," +
                "             SALDO.GRUPOBEM_ID AS GRUPO" +
                "      FROM SALDOGRUPOBEM SALDO" +
                "      WHERE SALDO.TIPOGRUPO = 'BEM_MOVEL_PRINCIPAL'" +
                "        AND SALDO.NATUREZATIPOGRUPOBEM = 'DEPRECIACAO'" +
                "        AND SALDO.UNIDADEORGANIZACIONAL_ID = :ORCAMENTARIA" +
                "        AND trunc(SALDO.DATASALDO) =" +
                "          (SELECT MAX(trunc(SLD.DATASALDO))" +
                "           FROM SALDOGRUPOBEM SLD" +
                "           WHERE SLD.UNIDADEORGANIZACIONAL_ID = SALDO.UNIDADEORGANIZACIONAL_ID" +
                "             AND SLD.TIPOGRUPO = SALDO.TIPOGRUPO" +
                "             AND SLD.NATUREZATIPOGRUPOBEM = SALDO.NATUREZATIPOGRUPOBEM" +
                "             AND SLD.GRUPOBEM_ID = SALDO.GRUPOBEM_ID" +
                "             AND trunc(SLD.DATASALDO) <= to_date(:DATAREFERENCIA, 'dd/MM/yyyy') ) )" +
                "   GROUP BY UNIDADE," +
                "            GRUPO) S" +
                " INNER JOIN GRUPOBEM ON GRUPOBEM.ID = S.GRUPOBEM" +
                " WHERE GRUPOBEM.TIPOBEM = :TIPOBEM ";
        if (listaGrupos != null && !listaGrupos.isEmpty()) {
            sql += "   AND GRUPOBEM.ID NOT IN(:GRUPOS) ";
        }
        sql += " GROUP BY GRUPOBEM.ID," +
            "         GRUPOBEM.CODIGO" +
            " ORDER BY 2";
        Query q = em.createNativeQuery(sql);

        q.setParameter("ORCAMENTARIA", orcamentaria.getId());
        q.setParameter("BAIXADO", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("DATAREFERENCIA", DataUtil.getDataFormatada(dataOperacao));
        if (listaGrupos != null && !listaGrupos.isEmpty()) {
            q.setParameter("GRUPOS", getIdsGrupos(listaGrupos));
        }
        q.setParameter("TIPOBEM", tipoBem.name());
        return q.getResultList();
    }

    private List<Long> getIdsGrupos(List<GrupoBem> listaGrupos) {
        List<Long> ids = Lists.newArrayList();
        for (GrupoBem listaGrupo : listaGrupos) {
            ids.add(listaGrupo.getId());
        }
        return ids;
    }


    public GrupoBem buscarGrupoBemPorId(Long id) {
        return em.find(GrupoBem.class, id);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteBarraProgressoLoteEfetivacaoBem> efetivarLoteLevantamentoBem(AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso,
                                                                                         LoteEfetivacaoLevantamentoBem loteEfetivacaoLevantamentoBem,
                                                                                         List<LevantamentoBemPatrimonial> levantamentos) {
        try {
            assistenteBarraProgresso.setProcessoEfetivacaoLevantamentoBens(AssistenteBarraProgressoLoteEfetivacaoBem.ProcessoEfetivacaoLevantamentoBens.CRIANDO_EFETIVACAO);
            loteEfetivacaoLevantamentoBem.getEfetivacoes().addAll(criarEfetivacoes(assistenteBarraProgresso, loteEfetivacaoLevantamentoBem, levantamentos));
            assistenteBarraProgresso.setLote(loteEfetivacaoLevantamentoBem);
        } catch (Exception ex) {
            singletonGeradorCodigoPatrimonio.reset();
            singletonGeradorCodigoPatrimonio.desbloquear(LoteEfetivacaoLevantamentoBem.class);
            throw ex;
        }
        return new AsyncResult(assistenteBarraProgresso);
    }


    public List<EfetivacaoLevantamentoBem> criarEfetivacoes(AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso,
                                                            LoteEfetivacaoLevantamentoBem lote,
                                                            List<LevantamentoBemPatrimonial> levantamentos) {
        assistenteBarraProgresso.setDescricaoProcesso("Criando efetivações");

        List<EfetivacaoLevantamentoBem> retorno = new ArrayList<>();
        Entidade entidade = entidadeFacade.recuperarEntidadePorUnidadeOrcamentaria(lote.getUnidadeOrcamentaria());
        ParametroPatrimonio parametroPatrimonio = parametroPatrimonioFacade.recuperarParametroComDependenciasEntidadeGeradoCodigo();
        for (LevantamentoBemPatrimonial levantamento : levantamentos) {
            BigDecimal valorDepreciacao = levantamento.getDepreciacao();
            levantamento = em.find(LevantamentoBemPatrimonial.class, levantamento.getId());

            Date dataEfetivacao = DataUtil.getDataComHoraAtual(lote.getDataEfetivacao());
            EfetivacaoLevantamentoBem efetivacao = new EfetivacaoLevantamentoBem(lote, levantamento, dataEfetivacao);
            efetivacao.setEstadoResultante(bemFacade.criarNovoEstadoResultanteAPartirDoCaracterizador(efetivacao));
            efetivacao.getEstadoResultante().setTipoGrupo(levantamento.getTipoGrupo());
            efetivacao.setValorDoLancamento(efetivacao.getEstadoResultante().getValorOriginal());
            efetivacao.setDepreciacao(valorDepreciacao.abs());

            if (levantamento.getBem() != null && levantamento.getBem().getId() != null) {
                efetivacao.setBem(levantamento.getBem());
                if (Strings.isNullOrEmpty(efetivacao.getBem().getIdentificacao())) {
                    efetivacao.setBem(em.merge(gerarCodigoRegistroPatrimonial(entidade, levantamento, efetivacao.getBem(), parametroPatrimonio)));
                }
                definirDetentorOrigemRecurso(levantamento, efetivacao.getBem());
                definirNotaFiscalEmpenho(levantamento, efetivacao.getBem());
                bemFacade.salvarPortal(efetivacao.getBem());
            } else {
                Bem bem = new Bem(efetivacao);
                if (levantamento.getSeguradora() != null) {
                    bem.setSeguradora(levantamento.getSeguradora());
                }
                bem.setDataAquisicao(levantamento.getDataAquisicao());
                bem = gerarCodigoRegistroPatrimonial(entidade, levantamento, bem, parametroPatrimonio);

                definirDetentorOrigemRecurso(levantamento, bem);
                definirNotaFiscalEmpenho(levantamento, bem);

                bem.setGarantia(levantamento.getGarantia());

                efetivacao.getEstadoResultante().setIdentificacao(bem.getIdentificacao());
                efetivacao.setBem(em.merge(bem));

                configMovimentacaoBemFacade.inserirMovimentoBloqueioInicial(efetivacao.getBem());
            }
            efetivacao.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
            retorno.add(efetivacao);
            assistenteBarraProgresso.conta();
        }
        return retorno;
    }

    private void definirNotaFiscalEmpenho(LevantamentoBemPatrimonial levantamento, Bem bem) {
        if (bem.getNotasFiscais() != null && bem.getNotasFiscais().isEmpty()) {
            BemNotaFiscal bemNotaFiscal = new BemNotaFiscal();
            bemNotaFiscal.setBem(bem);
            bemNotaFiscal.setDataNotaFiscal(levantamento.getDataNotaFiscal());
            bemNotaFiscal.setNumeroNotaFiscal(levantamento.getNotaFiscal());

            BemNotaFiscalEmpenho bemNotaFiscalEmpenho = new BemNotaFiscalEmpenho();
            bemNotaFiscalEmpenho.setBemNotaFiscal(bemNotaFiscal);
            bemNotaFiscalEmpenho.setDataEmpenho(levantamento.getDataNotaEmpenho());
            bemNotaFiscalEmpenho.setNumeroEmpenho(levantamento.getNotaEmpenho() + "");
            bemNotaFiscal.getEmpenhos().add(bemNotaFiscalEmpenho);
            bem.getNotasFiscais().add(bemNotaFiscal);
        }
    }


    private void definirDetentorOrigemRecurso(LevantamentoBemPatrimonial levantamento, Bem bem) {
        if (bem.getDetentorOrigemRecurso() == null) {
            if (levantamento.getListaDeOriemRecursoBem() != null && !levantamento.getListaDeOriemRecursoBem().isEmpty()) {
                DetentorOrigemRecurso detentorOrigemRecurso = new DetentorOrigemRecurso();
                for (OrigemRecursoBem origem : levantamento.getListaDeOriemRecursoBem()) {
                    OrigemRecursoBem origemRecursoBem = new OrigemRecursoBem();
                    origemRecursoBem.setDetentorOrigemRecurso(detentorOrigemRecurso);
                    origemRecursoBem.setDescricao(origem.getDescricao());
                    origemRecursoBem.setDespesaOrcamentaria(origem.getDespesaOrcamentaria());
                    origemRecursoBem.setFonteDespesa(origem.getFonteDespesa());
                    origemRecursoBem.setTipoRecursoAquisicaoBem(origem.getTipoRecursoAquisicaoBem());
                    detentorOrigemRecurso.getListaDeOriemRecursoBem().add(origemRecursoBem);
                }
                bem.setDetentorOrigemRecurso(em.merge(detentorOrigemRecurso));
            }
        }
    }

    private Bem gerarCodigoRegistroPatrimonial(Entidade entidade, LevantamentoBemPatrimonial levantamento, Bem bem, ParametroPatrimonio parametroPatrimonio) {
        if (EstadoConservacaoBem.OPERACIONAL.equals(levantamento.getEstadoConservacaoBem())) {
            bem.setIdentificacao(singletonGeradorCodigoPatrimonio.getProximoCodigo(entidade, TipoBem.MOVEIS, parametroPatrimonio).toString());
        } else if (EstadoConservacaoBem.INSERVIVEL.equals(levantamento.getEstadoConservacaoBem())) {
            bem.setIdentificacao(singletonGeradorCodigoPatrimonio.getProximoCodigoInservivel(entidade, TipoBem.MOVEIS, parametroPatrimonio).toString());
        }
        return bem;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteBarraProgressoLoteEfetivacaoBem> criarEventoDepreciacaoBem(AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso, LoteEfetivacaoLevantamentoBem lote, List<Long> idsEfetivacoes) {
        for (Long id : idsEfetivacoes) {
            EfetivacaoLevantamentoBem efetivacao = em.find(EfetivacaoLevantamentoBem.class, id);
            if (efetivacao.getDepreciacao().compareTo(BigDecimal.ZERO) == 0) {
                assistenteBarraProgresso.conta();
                continue;
            }
            EfetivacaoDepreciacaoBem depreciacaoEfetivacao = new EfetivacaoDepreciacaoBem(efetivacao);
            Date dataEfetivacao = DataUtil.getDataComHoraAtual(lote.getDataEfetivacao());
            depreciacaoEfetivacao.setDataLancamento(dataEfetivacao);
            depreciacaoEfetivacao.setEstadoInicial(efetivacao.getEstadoResultante());
            EstadoBem estadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(efetivacao.getEstadoResultante());
            estadoResultante.setValorAcumuladoDaDepreciacao(efetivacao.getDepreciacao());
            estadoResultante = salvarEstadoResultanteEfetivacaoDepreciacao(estadoResultante);
            depreciacaoEfetivacao.setEstadoResultante(estadoResultante);
            depreciacaoEfetivacao.setValorDoLancamento(estadoResultante.getValorAcumuladoDaDepreciacao());
            depreciacaoEfetivacao.setBem(efetivacao.getBem());
            depreciacaoEfetivacao.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);

            salvarEfetivacaoDepreciacaoBem(depreciacaoEfetivacao);
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult(assistenteBarraProgresso);
    }

    public BigDecimal calcularDepreciacaoLevantamento(LoteEfetivacaoLevantamentoBem lote,
                                                      LevantamentoBemPatrimonial levantamentoBemPatrimonial,
                                                      HashMap<GrupoBem, BigDecimal> mapaSaldoValorAcumuladoDepreciacao,
                                                      HashMap<GrupoBem, BigDecimal> mapaSaldoValorAcumuladoOriginalItem,
                                                      HashMap<GrupoBem, BigDecimal> mapaValorTotalDosLevantamentosPorGrupo) {
        BigDecimal e = BigDecimal.ZERO;
        if (!hasEfetivacaoRealizadaParaUnidade(levantamentoBemPatrimonial.getUnidadeOrcamentaria(), lote)) {
            e = saldoGrupoBemDepreciacaoContabil(levantamentoBemPatrimonial.getUnidadeOrcamentaria(), levantamentoBemPatrimonial.getGrupoBem(), lote.getDataEfetivacao());
        }

        if (BigDecimal.ZERO.equals(e)) {
            mapaSaldoValorAcumuladoDepreciacao.put(levantamentoBemPatrimonial.getGrupoBem(), BigDecimal.ZERO);
            return e;
        }

        /*
        a = Valor Original de um Item
        b = Valor Original de todos os itens

        d = Valor da Depreciação de um item
        e = Valor da Depreciação de todos os itens

        c = a / b
        d = Truncar(c * e, 2)

        b = b – a
        e = e – d
        */

        BigDecimal b = mapaValorTotalDosLevantamentosPorGrupo.get(levantamentoBemPatrimonial.getGrupoBem());
        BigDecimal a = levantamentoBemPatrimonial.getValorBem();

        try {
            b = b.subtract(mapaSaldoValorAcumuladoOriginalItem.get(levantamentoBemPatrimonial.getGrupoBem()));
            e = e.subtract(mapaSaldoValorAcumuladoDepreciacao.get(levantamentoBemPatrimonial.getGrupoBem()));
        } catch (NullPointerException nu) {
        }

        if (mapaSaldoValorAcumuladoOriginalItem.containsKey(levantamentoBemPatrimonial.getGrupoBem())) {
            BigDecimal acumulado = mapaSaldoValorAcumuladoOriginalItem.get(levantamentoBemPatrimonial.getGrupoBem());
            mapaSaldoValorAcumuladoOriginalItem.put(levantamentoBemPatrimonial.getGrupoBem(), acumulado.add(a));
        } else {
            mapaSaldoValorAcumuladoOriginalItem.put(levantamentoBemPatrimonial.getGrupoBem(), a);
        }

        Double c = a.doubleValue() / b.doubleValue();
        Double x = c * e.doubleValue();
        DecimalFormat df = new DecimalFormat("#.00");

        BigDecimal d = null;
        try {
            d = new BigDecimal(df.format(x).replace(",", "."));
        } catch (NumberFormatException nu) {
            d = BigDecimal.ZERO;
        }

        if (mapaSaldoValorAcumuladoDepreciacao.containsKey(levantamentoBemPatrimonial.getGrupoBem())) {
            BigDecimal acumulado = mapaSaldoValorAcumuladoDepreciacao.get(levantamentoBemPatrimonial.getGrupoBem());
            mapaSaldoValorAcumuladoDepreciacao.put(levantamentoBemPatrimonial.getGrupoBem(), acumulado.add(d));
        } else {
            mapaSaldoValorAcumuladoDepreciacao.put(levantamentoBemPatrimonial.getGrupoBem(), d);

        }
        return d;
    }

    public void preencherValorTotalLevantamentosPorGrupo(HashMap<GrupoBem, BigDecimal> mapaValorTotalDosLevantamentosPorGrupo, List<LevantamentoBemPatrimonial> levantamentos) {
        if (mapaValorTotalDosLevantamentosPorGrupo == null) {
            mapaValorTotalDosLevantamentosPorGrupo = new HashMap<>();
        }
        for (LevantamentoBemPatrimonial levantamento : levantamentos) {
            if (mapaValorTotalDosLevantamentosPorGrupo.containsKey(levantamento.getGrupoBem())) {
                BigDecimal acumulado = mapaValorTotalDosLevantamentosPorGrupo.get(levantamento.getGrupoBem());
                mapaValorTotalDosLevantamentosPorGrupo.put(levantamento.getGrupoBem(), acumulado.add(levantamento.getValorBem()));
            } else {
                mapaValorTotalDosLevantamentosPorGrupo.put(levantamento.getGrupoBem(), levantamento.getValorBem());
            }
        }
    }

    public int quantidadeEfetivacoes(LoteEfetivacaoLevantamentoBemControlador.FiltroEfetivacaoLevantamentoBem filtro) {
        Criteria criteria = getCriteria(filtro);
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.uniqueResult()).intValue();

    }

    private Criteria getCriteria(LoteEfetivacaoLevantamentoBemControlador.FiltroEfetivacaoLevantamentoBem filtro) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(EfetivacaoLevantamentoBem.class);
        criteria.add(Restrictions.eq("lote", filtro.getLote()));
        return criteria;
    }

    @SuppressWarnings("unchecked")
    public List<EfetivacaoLevantamentoBem> recuperarEfetivacaoLevantamentoBem(LoteEfetivacaoLevantamentoBemControlador.FiltroEfetivacaoLevantamentoBem filtro) {
        Criteria criteria = getCriteria(filtro);
        criteria.setFirstResult(filtro.getPrimeiroRegistro());
        criteria.setMaxResults(filtro.getQuantidadeRegistro());
        return criteria.list();
    }

    public BigDecimal calcularValorTotalDoBemEfetivado(LoteEfetivacaoLevantamentoBem lote) {
        String sql = "     select sum(levantamento.VALORBEM) " +
            "      from grupoobjcompragrupobem associacao " +
            "      inner join grupoobjetocompra grupo on grupo.id = associacao.grupoobjetocompra_id " +
            "      inner join OBJETOCOMPRA objetocompra on objetocompra.GRUPOOBJETOCOMPRA_ID = grupo.id " +
            "      inner join LevantamentoBemPatrimonial levantamento on levantamento.ITEM_ID = objetocompra.id " +
            "      inner join EfetivacaoLevantamentoBem efetivacao on efetivacao.LEVANTAMENTO_ID = levantamento.id " +
            "      inner join LOTEEFETLEVANTBEM lote on efetivacao.LOTE_ID = lote.id " +
            "      where :dataOperacao between ASSOCIACAO.INICIOVIGENCIA and coalesce(ASSOCIACAO.FIMVIGENCIA,:dataOperacao )" +
            "      and lote.id = :lote";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("lote", lote.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal calcularValorTotalDaDepreciacaoEfetivada(LoteEfetivacaoLevantamentoBem lote) {
        String sql = "select " +
            "              coalesce(sum(est.VALORACUMULADODADEPRECIACAO), 0)  " +
            "           from EFETIVACAODEPRECIACAOBEM depreciacao  " +
            "         inner join eventobem ev on ev.id = depreciacao.id " +
            "         inner join estadobem est on est.id = ev.estadoresultante_id  " +
            "         inner join efetivacaoLevantamentoBem efetivacao on depreciacao.efetivacaoLevantamentoBem_id = efetivacao.id " +
            "         inner join LOTEEFETLEVANTBEM LOTE on EFETIVACAO.LOTE_ID = LOTE.id    " +
            "         where lote.id = :lote";
        Query q = em.createNativeQuery(sql);
        q.setParameter("lote", lote.getId());
        return (BigDecimal) q.getSingleResult();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<EfetivacaoLevantamentoBem> recuperarEfetivacoes(LoteEfetivacaoLevantamentoBem lote) {
        String sql = "select efetivacao from EfetivacaoLevantamentoBem efetivacao where efetivacao.lote = :lote";
        Query q = em.createQuery(sql);
        q.setParameter("lote", lote);
        return q.getResultList();
    }

    public List<Long> recuperarIdsEfetivacao(LoteEfetivacaoLevantamentoBem lote) {
        String sql = "select id from EFETIVACAOLEVANTAMENTOBEM where LOTE_ID = :lote";
        Query q = em.createNativeQuery(sql);
        q.setParameter("lote", lote.getId());
        List<Long> ids = Lists.newArrayList();
        for (Object o : q.getResultList()) {
            ids.add(((BigDecimal) o).longValue());
        }
        return ids;
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteBarraProgressoLoteEfetivacaoBem> buscarEfetivacoesParaEstorno(List<Long> ids, AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso) {
        for (Long id : ids) {
            EfetivacaoLevantamentoBem ef = em.find(EfetivacaoLevantamentoBem.class, id);
            validarEstornoLevantamentoBem(ef, assistenteBarraProgresso);
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteBarraProgressoLoteEfetivacaoBem> estornarEfetivacaoLevantamentoBem(List<Long> ids, AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso) {
        for (Long id : ids) {
            EfetivacaoLevantamentoBem ef = em.find(EfetivacaoLevantamentoBem.class, id);
            assistenteBarraProgresso.conta();
            criarEstornoEfetivacaoLevantamentoMovelPorEfetivacaoLevantamentoBem(ef, assistenteBarraProgresso);
        }
        assistenteBarraProgresso.setDescricaoProcesso("Finalizando Operação de Estorno de Efetivações de Levantamento de Bens Móveis");
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    private void validarEstornoLevantamentoBem(EfetivacaoLevantamentoBem ef, AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso) {
        ValidacaoException ve = new ValidacaoException();
        try {
            if (bemFacade.validarEstornoDaEfetivacaoDeLevantamentoMovelPorBem(ef.getBem())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A efetivação do bem " + ef.getBem() + " não pôde ser estornada pois este bem já foi utilizado em outras operações.");
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().addAll(ve.getMensagens());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        ve.lancarException();
    }

    public void criarEstornoEfetivacaoLevantamentoMovelPorEfetivacaoLevantamentoBem(EfetivacaoLevantamentoBem efetivacao, AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso) {
        EstornoEfetivacaoLevantamentoMovel estorno = new EstornoEfetivacaoLevantamentoMovel();
        Date dataAtual = DataUtil.getDataComHoraAtual(assistenteBarraProgresso.getDataAtual());
        estorno.setDataLancamento(dataAtual);
        estorno.setBem(efetivacao.getBem());
        EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(efetivacao.getBem());
        estorno.setEfetivacaoLevantamentoBem(efetivacao);
        estorno.setEstadoInicial(ultimoEstado);
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        novoEstado.setValorOriginal(BigDecimal.ZERO);
        estorno.setEstadoResultante(bemFacade.salvarEstadoBem(novoEstado));
        salvarEstornoDaEfetivacaoLevantamentoMovel(estorno);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Object[] filtarComContadorDeRegistros(String sql, String sqlCount, int inicio, int max) throws Exception {
        Query consulta = em.createNativeQuery(sql);
        Query consultaCount = em.createNativeQuery(sqlCount);
        Long count = 0L;
        List<LoteEfetivacaoLevantamentoBem> list = new ArrayList<>();
        try {
            count = ((BigDecimal) consultaCount.getSingleResult()).longValue();
            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }
            List<Object[]> lista = consulta.getResultList();
            for (Object[] object : lista) {
                LoteEfetivacaoLevantamentoBem efetivacao = new LoteEfetivacaoLevantamentoBem();
                efetivacao.setId(((BigDecimal) object[0]).longValue());
                efetivacao.setCodigo(object[1] != null ? ((BigDecimal) object[1]).longValue() : null);
                efetivacao.setDescricaoUsuario(object[2] != null ? (String) object[2] : null);
                efetivacao.setDataEfetivacao(object[3] != null ? (Date) object[3] : null);
                efetivacao.setDescricaoUnidadeOrc(object[4] != null ? (String) object[4] : null);
                efetivacao.setSituacao(object[5] != null ? SituacaoEventoBem.valueOf((String) object[5]) : null);
                list.add(efetivacao);
            }
        } catch (NoResultException nre) {
            logger.error("Erro ao montar lista de efetivação de levantamento de bens móveis{} ", nre);
        }
        Object[] retorno = new Object[2];
        retorno[0] = list;
        retorno[1] = count;
        return retorno;
    }
}
