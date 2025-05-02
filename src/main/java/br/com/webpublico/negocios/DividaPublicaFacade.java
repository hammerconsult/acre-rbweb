/*
 * Codigo gerado automaticamente em Tue Mar 27 11:26:57 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class DividaPublicaFacade extends AbstractFacade<DividaPublica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private OcorrenciaDividaPublicaFacade ocorrenciaDividaPublicaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private TribunalFacade tribunalFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    @EJB
    private PeriodicidadeFacade periodicidadeFacade;
    @EJB
    private UnidadeJudiciariaFacade unidadeJudiciariaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private CategoriaDividaPublicaFacade categoriaDividaPublicaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private IndicadorEconomicoFacade indicadorEconomicoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ValorIndicadorEconomicoFacade valorIndicadorEconomicoFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public DividaPublica recuperar(Object id) {
        DividaPublica dp = em.find(DividaPublica.class, id);
        dp.getBeneficiarios().size();
        dp.getParcelas().size();
        dp.getTramites().size();
        dp.getUnidades().size();
        dp.getContasFinanceiras().size();
        dp.getAtosLegais().size();
        dp.getLiberacaoRecursos().size();
        dp.getContasReceitas().size();
        dp.getValorIndicadoresEconomicos().size();
        dp.getArquvios().size();
        return dp;
    }


    public DividaPublica recuperarSomenteUnidades(Object id) {
        DividaPublica dp = em.find(DividaPublica.class, id);
        dp.getUnidades().size();
        return dp;
    }

    public List<PessoaDividaPublica> listaBeneficiariosCancelados() {
        String hql = "from PessoaDividaPublica pdp where pdp.cancelado = true";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public List<PessoaDividaPublica> listaBeneficiarios() {
        String hql = "from PessoaDividaPublica pdp where pdp.cancelado = false";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public boolean listaNumeroDocContProc(DividaPublica dp) {
        String sql = "SELECT * FROM DIVIDAPUBLICA WHERE NUMERODOCCONTPROC = :PARAMETRO AND rownum = 1";
        Query q = em.createNativeQuery(sql, DividaPublica.class);
        q.setParameter("PARAMETRO", dp.getNumeroDocContProc());
        if (!q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public DividaPublica buscarDividaPublicaPorSubContaAndUnidade(SubConta sc, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio) {
        String sql = " SELECT distinct DP.* "
            + " FROM DIVIDAPUBLICA DP "
            + " INNER JOIN SUBCONTADIVIDAPUBLICA SCDP ON SCDP.DIVIDAPUBLICA_ID = DP.ID "
            + " INNER JOIN UnidadeDividaPublica udp ON udp.DIVIDAPUBLICA_ID = DP.ID "
            + " WHERE SCDP.SUBCONTA_ID = :sc "
            + " and udp.exercicio_id = :exercicio "
            + " and udp.unidadeorganizacional_id = :unidade "
            + " and scdp.exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql, DividaPublica.class);
        q.setParameter("sc", sc.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("exercicio", exercicio.getId());
        if (q.getResultList().isEmpty() || q.getResultList() == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrada nenhuma Dívida Pública para a Conta Financeira: " + sc + ", Unidade Organizacional: " + unidadeOrganizacional + " no exercício: " + exercicio);
        } else {
            return (DividaPublica) q.getSingleResult();
        }
    }

    public PessoaDividaPublica recarregaPessoaDivi(PessoaDividaPublica pe) {
        String hql = "from PessoaDividaPublica p where p = :param";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("param", pe);
        return (PessoaDividaPublica) q.getSingleResult();
    }

    public ParcelaDividaPublica recarregaParcela(ParcelaDividaPublica pdp) {
        String hql = "from ParcelaDividaPublica p where p = :param";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("param", q);
        return (ParcelaDividaPublica) q.getSingleResult();
    }

    public String retornaUltimoIdentificador() {
        String sql = "SELECT max(to_number(d.numero))+1 AS Utimonumero FROM dividapublica d ";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
//        q.setMaxResults(1);
//        if (!q.getResultList().isEmpty()) {
//            return (BigDecimal) q.getSingleResult();
//        } else {
//            return BigDecimal.ZERO;
//        }
    }

    public List<DividaPublica> listaFiltrandoDividaPublica(String parte) {
        String sql = "SELECT dp.* "
            + " FROM dividapublica dp"
            + " WHERE (dp.numero LIKE :paramNumero "
            + " OR dp.numeroAntigo LIKE :paramNumero "
            + " OR dp.NUMERODOCCONTPROC LIKE :paramNumero "
            + " OR dp.NUMEROPROTOCOLO LIKE :paramNumero "
            + " OR lower(dp.nomeDivida) LIKE :paramDescricao "
            + " OR (lower (dp.descricaodivida) LIKE :paramDescricao)) "
            + " ORDER BY cast(DP.NUMERO as numeric) DESC";
        Query q = getEntityManager().createNativeQuery(sql, DividaPublica.class);
        q.setParameter("paramDescricao", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("paramNumero", parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<DividaPublica> buscarDividasPublicasPorUsuario(String parte, UsuarioSistema usuarioSistema, Date data, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " SELECT distinct div.* FROM USUARIOUNIDADEORGANIZACIO UH " +
            " INNER JOIN HIERARQUIAORGANIZACIONAL HADM ON UH.UNIDADEORGANIZACIONAL_ID = HADM.SUBORDINADA_ID AND TO_DATE(:data, 'dd/MM/yyyy') BETWEEN HADM.INICIOVIGENCIA AND COALESCE(HADM.FIMVIGENCIA, TO_DATE(:data, 'dd/MM/yyyy')) " +
            " INNER JOIN HIERARQUIAORGRESP RESP ON HADM.ID = RESP.HIERARQUIAORGADM_ID AND TO_DATE(:data, 'dd/MM/yyyy') BETWEEN RESP.DATAINICIO AND COALESCE(RESP.DATAFIM, TO_DATE(:data, 'dd/MM/yyyy')) " +
            " INNER JOIN HIERARQUIAORGANIZACIONAL HORC ON RESP.HIERARQUIAORGORC_ID = HORC.ID " +
            " INNER JOIN UNIDADEDIVIDAPUBLICA UD ON HORC.SUBORDINADA_ID = UD.UNIDADEORGANIZACIONAL_ID " +
            " INNER JOIN DIVIDAPUBLICA DIV ON UD.DIVIDAPUBLICA_ID = DIV.ID " +
            " WHERE UH.USUARIOSISTEMA_ID = :usuario ";
        if (unidadeOrganizacional != null) {
            sql += " and ud.UNIDADEORGANIZACIONAL_ID = :unidadeOrganizacional ";
        }
        sql += " AND (div.numero LIKE :paramNumero " +
            "   OR div.numeroAntigo LIKE :paramNumero " +
            "   OR div.NUMERODOCCONTPROC LIKE :paramNumero " +
            "   OR div.NUMEROPROTOCOLO LIKE :paramNumero " +
            "   OR lower(div.nomeDivida) LIKE :paramDescricao " +
            "   OR (lower(div.descricaodivida) LIKE :paramDescricao)) " +
            " ORDER BY cast(div.NUMERO as numeric) DESC ";
        Query q = getEntityManager().createNativeQuery(sql, DividaPublica.class);
        q.setParameter("paramDescricao", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("paramNumero", parte.toLowerCase().trim() + "%");
        q.setParameter("usuario", usuarioSistema.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        if (unidadeOrganizacional != null) {
            q.setParameter("unidadeOrganizacional", unidadeOrganizacional.getId());
        }
        return q.getResultList();
    }

    public List<FonteDeRecursos> buscarFontesPorDividaPublica(DividaPublica dividaPublica, TipoValidacao tipoValidacao, Exercicio exercicioCorrente, SubConta contaFinanceira) {
        String sql = " select distinct fonte.* from subcontadividapublica subcontadiv " +
            " inner join fontederecursos fonte on subcontadiv.fontederecursos_id = fonte.id " +
            " where SUBCONTADIV.DIVIDAPUBLICA_ID = :dividaPublica " +
            "   and SUBCONTADIV.TIPOVALIDACAO = :tipoValidacao " +
            "   and SUBCONTADIV.EXERCICIO_ID = :exercicio ";
        if (contaFinanceira != null) {
            sql += " and subcontadiv.subconta_id = :subconta ";
        }
        sql += " order by fonte.codigo ";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("dividaPublica", dividaPublica.getId());
        q.setParameter("tipoValidacao", tipoValidacao.name());
        q.setParameter("exercicio", exercicioCorrente.getId());
        if (contaFinanceira != null) {
            q.setParameter("subconta", contaFinanceira.getId());
        }
        return q.getResultList();
    }

    public List<ContaDeDestinacao> buscarContasDeDestinacaoPorDividaPublica(DividaPublica dividaPublica, TipoValidacao tipoValidacao, Exercicio exercicioCorrente, SubConta contaFinanceira) {
        String sql = " select distinct c.*, cd.fonteDeRecursos_id, cd.dataCriacao, cd.codigoco " +
            " from subcontadividapublica subcontadiv " +
            " inner join contadedestinacao cd on subcontadiv.contadedestinacao_id = cd.id " +
            " inner join conta c on cd.id = c.id " +
            " where SUBCONTADIV.DIVIDAPUBLICA_ID = :dividaPublica " +
            "   and SUBCONTADIV.TIPOVALIDACAO = :tipoValidacao " +
            "   and SUBCONTADIV.EXERCICIO_ID = :exercicio " +
            "   and c.EXERCICIO_ID = :exercicio ";
        if (contaFinanceira != null) {
            sql += " and subcontadiv.subconta_id = :subconta ";
        }
        sql += " order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("dividaPublica", dividaPublica.getId());
        q.setParameter("tipoValidacao", tipoValidacao.name());
        q.setParameter("exercicio", exercicioCorrente.getId());
        if (contaFinanceira != null) {
            q.setParameter("subconta", contaFinanceira.getId());
        }
        return q.getResultList();
    }

    public boolean hasFonteConfiguradaNaDividaPublica(DividaPublica dividaPublica, TipoValidacao tipoValidacao, Exercicio exercicioCorrente, FonteDeRecursos fonteDeRecursos, SubConta contaFinanceira) {
        String sql = " select 1 from subcontadividapublica subcontadiv " +
            " inner join fontederecursos fonte on fonte.id = subcontadiv.fontederecursos_id " +
            " where SUBCONTADIV.DIVIDAPUBLICA_ID = :dividaPublica " +
            "   and SUBCONTADIV.TIPOVALIDACAO = :tipoValidacao " +
            "   and SUBCONTADIV.EXERCICIO_ID = :exercicio " +
            "   and fonte.codigo = :fonte ";
        if (contaFinanceira != null) {
            sql += " and subcontadiv.subconta_id = :subconta ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dividaPublica", dividaPublica.getId());
        q.setParameter("tipoValidacao", tipoValidacao.name());
        q.setParameter("exercicio", exercicioCorrente.getId());
        q.setParameter("fonte", fonteDeRecursos.getCodigo());
        if (contaFinanceira != null) {
            q.setParameter("subconta", contaFinanceira.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<DividaPublica> completaDividaPublicaPorNatureza(String parte, NaturezaDividaPublica naturezaDividaPublica) {
        String sql = "SELECT dp.* FROM DIVIDAPUBLICA dp "
            + " INNER JOIN CATEGORIADIVIDAPUBLICA CDP ON dp.CATEGORIADIVIDAPUBLICA_ID = CDP.ID "
            + " WHERE CDP.NATUREZADIVIDAPUBLICA = :natureza"
            + " AND ((dp.numero LIKE :dividaNumero) "
            + " OR  (lower(dp.nomeDivida) LIKE :dividaNome) )"
            + " ORDER BY to_number(dp.numero)";
        Query q = em.createNativeQuery(sql, DividaPublica.class);
        q.setParameter("natureza", naturezaDividaPublica.name());
        q.setParameter("dividaNumero", parte.trim() + "%");
        q.setParameter("dividaNome", "%" + parte.toLowerCase() + "%");
        return (List<DividaPublica>) q.getResultList();
    }

    public List<CategoriaDividaPublica> listaPrecatorios(String parte) {
        String sql = "SELECT CDP.* FROM CATEGORIADIVIDAPUBLICA CDP "
            + "WHERE CDP.NATUREZADIVIDAPUBLICA LIKE 'PRECATORIO'"
            + " AND ( CDP.CODIGO LIKE :parte OR lower(CDP.DESCRICAO) LIKE :parte)";
        Query q = em.createNativeQuery(sql, CategoriaDividaPublica.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<CategoriaDividaPublica> listaOperacaoCredito(String parte) {
        String sql = "SELECT CDP.* FROM CATEGORIADIVIDAPUBLICA CDP "
            + "WHERE CDP.NATUREZADIVIDAPUBLICA LIKE 'OPERACAO_CREDITO'"
            + " AND (REPLACE(CDP.CODIGO, '.','') LIKE :parte OR lower(CDP.DESCRICAO) LIKE :parte)";
        Query q = em.createNativeQuery(sql, CategoriaDividaPublica.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public Boolean verificaGerouMovimentacao(DividaPublica divida) {
        String sql = " SELECT dp.* FROM DIVIDAPUBLICA DP "
            + " INNER JOIN MOVIMENTODIVIDAPUBLICA MDP ON MDP.DIVIDAPUBLICA_ID = DP.ID "
            + " WHERE DP.ID = :ID ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ID", divida.getId());
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<CategoriaDividaPublica> listaNaoPrecatorios(String parte) {
        String sql = "SELECT CDP.* FROM CATEGORIADIVIDAPUBLICA CDP "
            + "WHERE CDP.NATUREZADIVIDAPUBLICA <> 'PRECATORIO' AND CDP.NATUREZADIVIDAPUBLICA <> 'OPERACAO_CREDITO' AND CDP.ATIVOINATIVO = 'ATIVO' "
            + " AND (REPLACE(CDP.CODIGO, '.', '') LIKE :parte OR lower(CDP.DESCRICAO) LIKE :parte)"
            + " order by CDP.CODIGO ASC";
        Query q = em.createNativeQuery(sql, CategoriaDividaPublica.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<DividaPublica> listaPre() {
        String sql = "SELECT DP.* FROM DIVIDAPUBLICA DP "
            + "INNER JOIN CATEGORIADIVIDAPUBLICA CDP ON DP.CATEGORIADIVIDAPUBLICA_ID = CDP.ID "
            + "WHERE CDP.NATUREZADIVIDAPUBLICA LIKE 'PRECATORIO'";
        Query q = em.createNativeQuery(sql, DividaPublica.class);
        return (List<DividaPublica>) q.getResultList();
    }

    public List<DividaPublica> listaOpeCred() {
        String sql = "SELECT DP.* FROM DIVIDAPUBLICA DP "
            + "INNER JOIN CATEGORIADIVIDAPUBLICA CDP ON DP.CATEGORIADIVIDAPUBLICA_ID = CDP.ID "
            + "WHERE CDP.NATUREZADIVIDAPUBLICA LIKE 'OPERACAO_CREDITO'";
        Query q = em.createNativeQuery(sql, DividaPublica.class);
        return (List<DividaPublica>) q.getResultList();
    }

    public List<DividaPublica> listaOutros() {
        String sql = "SELECT * FROM DIVIDAPUBLICA DP "
            + "INNER JOIN CATEGORIADIVIDAPUBLICA CDP ON DP.CATEGORIADIVIDAPUBLICA_ID = CDP.ID "
            + "WHERE CDP.NATUREZADIVIDAPUBLICA NOT IN ('PRECATORIO','OPERACAO_CREDITO')";
        Query q = em.createNativeQuery(sql, DividaPublica.class);
        return (List<DividaPublica>) q.getResultList();
    }

    public List<ParcelaDividaPublica> recuperaParcelamentoPorDividaPublica(DividaPublica dividaPublica) {
        String sql = " SELECT PD.* FROM PARCELADIVIDAPUBLICA PD "
            + " WHERE PD.DIVIDAPUBLICA_ID = :divida ";
        Query consulta = em.createNativeQuery(sql, ParcelaDividaPublica.class);
        consulta.setParameter("divida", dividaPublica.getId());
        if (consulta.getResultList() != null) {
            return consulta.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Empenho> buscarEmpenhosPorDividaPublica(DividaPublica dividaPublica) {
        String sql = " SELECT EMP.* FROM EMPENHO EMP "
            + adicionarJoinDividaPublicaPelaNatureza(dividaPublica, "EMP")
            + " WHERE DP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql, Empenho.class);
        q.setParameter("param", dividaPublica.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return Lists.newArrayList();
        }
    }

    public BigDecimal buscarTotalDePagamentosPorDividaPublica(DividaPublica dividaPublica, SubTipoDespesa subTipoDespesa) {
        String sql = " SELECT coalesce(sum(P.valor), 0) FROM PAGAMENTO P " +
            " INNER JOIN LIQUIDACAO L ON L.ID = P.LIQUIDACAO_ID " +
            " INNER JOIN EMPENHO E ON L.EMPENHO_ID = E.ID " +
            adicionarJoinDividaPublicaPelaNatureza(dividaPublica, "E") +
            " WHERE E.SUBTIPODESPESA = :subTipoDespesa " +
            " AND DP.ID = :idDivida " +
            " AND P.DATAPAGAMENTO IS NOT NULL ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("idDivida", dividaPublica.getId());
        q.setParameter("subTipoDespesa", subTipoDespesa.name());
        if (q.getResultList() != null) {
            return (BigDecimal) q.getSingleResult();
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal buscarTotalDePagamentosEstornoPorDividaPublica(DividaPublica dividaPublica, SubTipoDespesa subTipoDespesa) {
        String sql = " SELECT coalesce(sum(PE.valor), 0) FROM PAGAMENTOESTORNO PE " +
            " INNER JOIN PAGAMENTO P ON PE.PAGAMENTO_ID = P.ID " +
            " INNER JOIN LIQUIDACAO L ON L.ID = P.LIQUIDACAO_ID " +
            " INNER JOIN EMPENHO E ON L.EMPENHO_ID = E.ID " +
            adicionarJoinDividaPublicaPelaNatureza(dividaPublica, "E") +
            " WHERE E.SUBTIPODESPESA = :subTipoDespesa " +
            " AND DP.ID = :idDivida ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("idDivida", dividaPublica.getId());
        q.setParameter("subTipoDespesa", subTipoDespesa.name());
        if (q.getResultList() != null) {
            return (BigDecimal) q.getSingleResult();
        } else {
            return BigDecimal.ZERO;
        }
    }

    private String adicionarJoinDividaPublicaPelaNatureza(DividaPublica dividaPublica, String aliasEmpenho) {
        if (dividaPublica.getNaturezaDividaPublica() != null && NaturezaDividaPublica.OPERACAO_CREDITO.equals(dividaPublica.getNaturezaDividaPublica())) {
            return " INNER JOIN DIVIDAPUBLICA DP ON DP.ID = " + aliasEmpenho + ".OPERACAODECREDITO_ID ";
        } else {
            return " INNER JOIN DIVIDAPUBLICA DP ON DP.ID = " + aliasEmpenho + ".DIVIDAPUBLICA_ID ";
        }
    }

    public List<EmpenhoEstorno> buscarEmpenhosEstornoPorDividaPublica(DividaPublica dividaPublica) {
        String sql = " SELECT EMPEST.* FROM EMPENHO EMP "
            + " INNER JOIN EMPENHOESTORNO EMPEST ON EMPEST.EMPENHO_ID = EMP.ID "
            + adicionarJoinDividaPublicaPelaNatureza(dividaPublica, "EMP")
            + " WHERE DP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql, EmpenhoEstorno.class);
        q.setParameter("param", dividaPublica.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return Lists.newArrayList();
        }
    }

    public List<Liquidacao> buscarLiquidacoesPorDividaPublica(DividaPublica dividaPublica) {
        String sql = " SELECT LIQ.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + adicionarJoinDividaPublicaPelaNatureza(dividaPublica, "EMP")
            + " WHERE DP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql, Liquidacao.class);
        q.setParameter("param", dividaPublica.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return Lists.newArrayList();
        }
    }

    public List<LiquidacaoEstorno> buscarLiquidacoesEstornoPorDividaPublica(DividaPublica dividaPublica) {
        String sql = " SELECT LIQEST.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN LIQUIDACAOESTORNO LIQEST ON LIQEST.LIQUIDACAO_ID = LIQ.ID "
            + adicionarJoinDividaPublicaPelaNatureza(dividaPublica, "EMP")
            + " WHERE DP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql, LiquidacaoEstorno.class);
        q.setParameter("param", dividaPublica.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return Lists.newArrayList();
        }
    }

    public List<Pagamento> buscarPagamentosPorDividaPublica(DividaPublica dividaPublica) {
        String sql = " SELECT PAG.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PAGAMENTO PAG ON PAG.LIQUIDACAO_ID = LIQ.ID "
            + adicionarJoinDividaPublicaPelaNatureza(dividaPublica, "EMP")
            + " WHERE DP.ID = :param "
            + " AND PAG.DATAPAGAMENTO IS NOT NULL";
        Query q = getEntityManager().createNativeQuery(sql, Pagamento.class);
        q.setParameter("param", dividaPublica.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return Lists.newArrayList();
        }
    }

    public List<PagamentoEstorno> buscarPagamentosEstornoPorDividaPublica(DividaPublica dividaPublica) {
        String sql = " SELECT PAGEST.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PAGAMENTO PAG ON PAG.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN PAGAMENTOESTORNO PAGEST ON PAGEST.PAGAMENTO_ID = PAG.ID "
            + adicionarJoinDividaPublicaPelaNatureza(dividaPublica, "EMP")
            + " WHERE DP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql, PagamentoEstorno.class);
        q.setParameter("param", dividaPublica.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return Lists.newArrayList();
        }
    }

    public List<MovimentoDividaPublica> listaMovimentoDividaPublica(DividaPublica divida, TipoLancamento tipoLancamento) {
        String sql = " SELECT MDP.* FROM MOVIMENTODIVIDAPUBLICA MDP " +
            " INNER JOIN DIVIDAPUBLICA DP ON DP.ID = MDP.DIVIDAPUBLICA_ID " +
            " WHERE DP.ID = :param " +
            " AND MDP.TIPOLANCAMENTO = :tipoLanc ";

        Query q = getEntityManager().createNativeQuery(sql.toString(), MovimentoDividaPublica.class);
        q.setParameter("param", divida.getId());
        q.setParameter("tipoLanc", tipoLancamento.name());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean verificaSeExisteArquivo(Arquivo arq) {
        Arquivo ar = null;
        if (arq == null) {
            return false;
        }
        try {
            ar = arquivoFacade.recupera(arq.getId());
        } catch (Exception e) {
            return false;
        }
        if (arq.equals(ar)) {
            return true;
        }
        return false;
    }

    public void salvarArquivo(FileUploadEvent fileUploadEvent, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = fileUploadEvent.getFile();
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arq.setTamanho(arquivoRecebido.getSize());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public DividaPublicaFacade() {
        super(DividaPublica.class);
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public OcorrenciaDividaPublicaFacade getOcorrenciaDividaPublicaFacade() {
        return ocorrenciaDividaPublicaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public TribunalFacade getTribunalFacade() {
        return tribunalFacade;
    }

    public IndiceEconomicoFacade getIndiceEconomicoFacade() {
        return indiceEconomicoFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public PeriodicidadeFacade getPeriodicidadeFacade() {
        return periodicidadeFacade;
    }

    public UnidadeJudiciariaFacade getUnidadeJudiciariaFacade() {
        return unidadeJudiciariaFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public CategoriaDividaPublicaFacade getCategoriaDividaPublicaFacade() {
        return categoriaDividaPublicaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public IndicadorEconomicoFacade getIndicadorEconomicoFacade() {
        return indicadorEconomicoFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ValorIndicadorEconomicoFacade getValorIndicadorEconomicoFacade() {
        return valorIndicadorEconomicoFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public List<UnidadeOrganizacional> recuperaUnidadesDaDividaPublica(DividaPublica dividaPublica) {

        String sql = " SELECT U.* FROM UNIDADEDIVIDAPUBLICA UNID " +
            " INNER JOIN DIVIDAPUBLICA DP ON DP.ID = UNID.DIVIDAPUBLICA_ID " +
            " INNER JOIN UNIDADEORGANIZACIONAL U ON U.ID = UNID.UNIDADEORGANIZACIONAL_ID " +
            " WHERE DP.ID = :divida";
        Query consulta = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        consulta.setParameter("divida", dividaPublica.getId());
        consulta.setMaxResults(10);
        if (consulta.getResultList() != null) {
            return consulta.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<DividaPublica> listaFiltrandoDividaPublicaPorUnidade(String parte, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " SELECT DP.* FROM DIVIDAPUBLICA DP " +
            " INNER JOIN UNIDADEDIVIDAPUBLICA UDP ON UDP.DIVIDAPUBLICA_ID = DP.ID " +
            " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = UDP.UNIDADEORGANIZACIONAL_ID " +
            " WHERE UO.ID = :idUnidade " +
            " AND UDP.EXERCICIO_ID = :exercicio" +
            " AND ((DP.NUMERO LIKE :numeroDivida) " +
            " OR (lower (DP.DESCRICAODIVIDA) LIKE :descricao)) " +
            " ORDER BY to_number(DP.NUMERO)";
        Query consulta = em.createNativeQuery(sql, DividaPublica.class);
        consulta.setParameter("idUnidade", unidadeOrganizacional.getId());
        consulta.setParameter("numeroDivida", parte.trim() + "%");
        consulta.setParameter("descricao", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("exercicio", sistemaFacade.getExercicioCorrente().getId());
        consulta.setMaxResults(10);
        if (consulta.getResultList() != null) {
            return consulta.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public Boolean verificaSolicitacaoEmpenhoParaDividaPublica(ParcelaDividaPublica parcela) {
        String sql = " SELECT PARC.* FROM DIVIDAPUBLICASOLICEMP DPS " +
            " INNER JOIN DIVIDAPUBLICA DP ON DPS.DIVIDAPUBLICA_ID = DP.ID " +
            " INNER JOIN PARCELADIVIDAPUBLICA PARC ON PARC.ID = DPS.PARCELADIVIDAPUBLICA_ID " +
            " WHERE PARC.ID = :idParcela ";
        Query q = em.createNativeQuery(sql, ParcelaDividaPublica.class);
        q.setParameter("idParcela", parcela.getId());
        if (q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void gerarSolicitacaoEmpenhoValorDaParcela(DividaPublica dp, ParcelaDividaPublica parcela, UnidadeOrganizacional unidade) {
        DividaPublicaSolicitacaoEmpenho soliciacao = new DividaPublicaSolicitacaoEmpenho();
        String historicoSolicitacao = gerarHistoricoDaSolicitacao(dp, soliciacao);
        Boolean reverva = false;
        soliciacao.setSolicitacaoEmpenho(solicitacaoEmpenhoFacade.gerarSolicitacaoEmpenhoSalvando(
            parcela.getValorPrestacao(),
            unidade, null, null, null, null,
            UtilRH.getDataOperacao(), null,
            historicoSolicitacao,
            dp.getPessoa(),
            reverva, null,
            dp.getClasseCredor(),
            null,
            OrigemSolicitacaoEmpenho.VALOR_PARCELA,
            dp.getNumero() + (dp.getExercicio() != null ? "/" + dp.getExercicio().getAno() : "")));
        soliciacao.setParcelaDividaPublica(parcela);
        soliciacao.setSubTipoDespesa(SubTipoDespesa.VALOR_PRINCIPAL);
        em.persist(soliciacao);
        em.merge(dp);
    }

    private String gerarHistoricoDaSolicitacao(DividaPublica dp, DividaPublicaSolicitacaoEmpenho soliciacao) {
        String historicoSolicitacao = "Solicitação gerada para a ";
        if (NaturezaDividaPublica.OPERACAO_CREDITO.equals(dp.getNaturezaDividaPublica())) {
            soliciacao.setOperacaoDeCredito(dp);
            historicoSolicitacao += "Operação de Crédito";
        } else {
            historicoSolicitacao += "Dívida Pública";
            soliciacao.setDividaPublica(dp);
        }
        historicoSolicitacao += " : N° " + dp.getNumero() + " (" + dp.getNaturezaDividaPublica().getDescricao() + ") na data " + DataUtil.getDataFormatada(UtilRH.getDataOperacao());
        return historicoSolicitacao;
    }

    public void gerarSolicitacaoEmpenhoOutrosEncargos(DividaPublica dp, ParcelaDividaPublica parcela, UnidadeOrganizacional unidade) {
        DividaPublicaSolicitacaoEmpenho soliciacao = new DividaPublicaSolicitacaoEmpenho();
        String historicoSolicitacao = gerarHistoricoDaSolicitacao(dp, soliciacao);
        Boolean reverva = false;
        soliciacao.setSolicitacaoEmpenho(solicitacaoEmpenhoFacade.geraSolicitacaoEmpenho(parcela.getOutrosEncargos(), unidade, null, null, null, null, UtilRH.getDataOperacao(), null,
            historicoSolicitacao, dp.getPessoa(),
            reverva, null, dp.getClasseCredor(),
            OrigemSolicitacaoEmpenho.OUTROS_ENCARGOS,
            dp.getNumero() + (dp.getExercicio() != null ? "/" + dp.getExercicio().getAno() : "")));
        soliciacao.setParcelaDividaPublica(parcela);
        soliciacao.setSubTipoDespesa(SubTipoDespesa.OUTROS_ENCARGOS);
        em.persist(soliciacao);
        em.merge(dp);
    }

    public void geraSolicitacaoEmpenhoJuros(DividaPublica dp, ParcelaDividaPublica parcela, UnidadeOrganizacional unidade) {
        DividaPublicaSolicitacaoEmpenho soliciacao = new DividaPublicaSolicitacaoEmpenho();
        String historicoSolicitacao = gerarHistoricoDaSolicitacao(dp, soliciacao);
        Boolean reverva = false;
        soliciacao.setSolicitacaoEmpenho(solicitacaoEmpenhoFacade.geraSolicitacaoEmpenho(parcela.getValorJuros(), unidade, null, null, null, null, UtilRH.getDataOperacao(), null,
            historicoSolicitacao, dp.getPessoa(), reverva, null, dp.getClasseCredor(), OrigemSolicitacaoEmpenho.JUROS,
            dp.getNumero() + (dp.getExercicio() != null ? "/" + dp.getExercicio().getAno() : "")));

        soliciacao.setParcelaDividaPublica(parcela);
        soliciacao.setSubTipoDespesa(SubTipoDespesa.JUROS);
        em.persist(soliciacao);
        em.merge(dp);
    }

    public List<DividaPublica> recuperarDividaPublicasDaPessoa(Pessoa pessoa) {
        String sql = " select d.* from dividapublica d     " +
            "       where d.pessoa_id = :pessoa        ";
        Query q = em.createNativeQuery(sql, DividaPublica.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public Object[] recuperarIdENumeroDividaPublica(SolicitacaoEmpenho solicitacaoEmpenho) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select dp.id, dp.numero ")
            .append(" from DividaPublica dp ")
            .append("  inner join DIVIDAPUBLICASOLICEMP dpse on dpse.dividapublica_id = dp.id ")
            .append(" where dpse.solicitacaoempenho_id = :solicitacaoEmpenhoId ");
        Query q = em.createNativeQuery(sb.toString());
        try {
            q.setParameter("solicitacaoEmpenhoId", solicitacaoEmpenho.getId());
            return (Object[]) q.getSingleResult();
        } catch (NoResultException | NullPointerException e) {
            return null;
        }
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }
}
