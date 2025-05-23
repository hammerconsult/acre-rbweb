/*
 * Codigo gerado automaticamente em Thu Mar 31 17:21:35 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class AcaoPrincipalFacade extends AbstractFacade<AcaoPrincipal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProdutoPPAFacade subAcaoPPAFacade;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private PeriodoFacade periodoFacade;
    @EJB
    private PeriodicidadeFacade periodicidadeFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ItemPlanejamentoEstrategicoFacade itemPlanejamentoEstrategicoFacade;
    @EJB
    private PlanejamentoEstrategicoFacade planejamentoEstrategicoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private TipoAcaoPPAFacade tipoAcaoPPAFacade;
    @EJB
    private LDOFacade lDOFacade;
    @EJB
    private ConfiguracaoPlanejamentoPublicoFacade configuracaoPlanejamentoPublicoFacade;
    @EJB
    private MedicaoSubAcaoPPAFacade medicaoSubAcaoPPAFacade;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AcaoPrincipalFacade() {
        super(AcaoPrincipal.class);
    }

    public String buscarProximoCodigo(ProgramaPPA programa) {
        String sql = "select max(cast(ac.codigo as numeric)) from AcaoPrincipal ac where ac.programa_id = :prog";
        Long codigo = 0l;
        Query q = em.createNativeQuery(sql);
        q.setParameter("prog", programa.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty() && q.getSingleResult() != null) {
            codigo = ((BigDecimal) q.getSingleResult()).longValue();
        }
        return String.valueOf(codigo + 1);

    }

    public String getProximoCodigo() {
        try {
            Query consulta = em.createNativeQuery("select max(to_number(coalesce((ac.codigo),'1'))) from AcaoPrincipal ac");
            BigDecimal valor = (BigDecimal) consulta.getSingleResult();
            return valor.toString();
        } catch (Exception e) {
            return "0001";
        }
    }

    @Override
    public AcaoPrincipal recuperar(Object id) {
        AcaoPrincipal pf = em.find(AcaoPrincipal.class, id);
        pf.getProdutoPPAs().size();
        pf.getProjetosAtividades().size();
        for (ProdutoPPA s : pf.getProdutoPPAs()) {
            s.getProvisoes().size();
            for (ProvisaoPPA provisaoPPA : s.getProvisoes()) {
                provisaoPPA.getMedicaoProvisaoPPAS().size();
            }
        }
        pf.getPrograma().getPublicoAlvo().size();
        pf.getParticipanteAcaoPrincipals().size();
        return pf;
    }

    public List<AcaoPrincipal> recuperaAcoesPpa(ProgramaPPA prog) {
        List<AcaoPrincipal> recuperaAcao = new ArrayList<AcaoPrincipal>();
        String hql = "from AcaoPrincipal a where a.programa = :p";
        if (prog != null) {
            Query q = em.createQuery(hql);
            q.setParameter("p", prog);
            recuperaAcao = q.getResultList();
        } else {
            recuperaAcao = new ArrayList<AcaoPrincipal>();
        }
        return recuperaAcao;
    }

    public List<ProdutoPPA> recuperaSubAcaoPPA(AcaoPrincipal ac) {
        String hql = "from ProdutoPPA s where s.acaoPrincipal = :acao ";

        Query q = em.createQuery(hql);
        q.setParameter("acao", ac);

        return q.getResultList();
    }

    public List<AcaoPrincipal> listaAcaoPPAPorPPA(PPA ppa) {
        String hql = " select ap from AcaoPrincipal ap "
            + " inner join ap.programa pp  "
            + " inner join pp.ppa p "
            + " where p =:PPA  "
            + "order by ap.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("PPA", ppa);
        return q.getResultList();
    }

    public List<AcaoPrincipal> listaAcaoPPAPorPPA(PPA ppa, String descricao) {
        String hql = " select ap from AcaoPrincipal ap "
            + " inner join ap.programa pp  "
            + " inner join pp.ppa p "
            + " where p =:PPA and ap.descricao like :descricao "
            + "order by ap.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("PPA", ppa);
        q.setParameter("descricao", "%" + descricao + "%");
        return q.getResultList();
    }

    public List<AcaoPrincipal> listaAcaoPPAPorProgramaPPA(ProgramaPPA programaPPA, String descricao) {
        String hql = " select ap from AcaoPrincipal ap "
            + " inner join ap.programa pp  "
            + " where pp = :PROGRAMA "
            + "   and (ap.descricao like :descricao "
            + "   or ap.codigo like :descricao) "
            + " order by ap.codigo desc";
        Query q = em.createQuery(hql);
        q.setParameter("PROGRAMA", programaPPA);
        q.setParameter("descricao", "%" + descricao + "%");
        return q.getResultList();
    }

    public List<AcaoPrincipal> buscarAcaoPPAPorProgramaPPAPriorizadaLDO(ProgramaPPA programaPPA, String parte, Exercicio exercicio) {
        String sql = " SELECT DISTINCT ACAO.* " +
            " FROM acaoprincipal ACAO " +
            " INNER JOIN PRODUTOPPA PRO ON ACAO.ID = pro.acaoprincipal_id " +
            " INNER JOIN PROGRAMAPPA PROG ON PROG.ID = ACAO.PROGRAMA_ID " +
            " WHERE ((ACAO.CODIGO LIKE :parte) OR (LOWER(ACAO.descricao) LIKE :parte)) " +
            " AND acao.programa_id = :programa " +
            " AND PROG.EXERCICIO_ID = :exercicio" +
            " ORDER BY ACAO.CODIGO";
        Query q = em.createNativeQuery(sql, AcaoPrincipal.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("programa", programaPPA.getId());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<AcaoPrincipal> buscarAcaoPPAPorExercicio(String parte, Exercicio exercicio) {
        String sql = " SELECT ACAO.* " +
            "      FROM acaoprincipal ACAO " +
            "        WHERE ((ACAO.CODIGO LIKE :parte) OR (LOWER(ACAO.descricao) LIKE :parte)) " +
            "        AND ACAO.EXERCICIO_ID = :exercicio " +
            "      ORDER BY ACAO.CODIGO ";
        Query q = em.createNativeQuery(sql, AcaoPrincipal.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<AcaoPrincipal> listaAcaoPPANaoVinculadaProgramas(String descricao) {
        Query q = em.createQuery("select ac from AcaoPrincipal ac " +
            "                 where ac.programa is null " +
            "                 and (ac.descricao like :descricao or ac.codigo like :descricao)");
        q.setParameter("descricao", "%" + descricao + "%");
        return q.getResultList();
    }

    public BigDecimal buscaValorFinanceiroLiquidacoes(ProdutoPPA sub, Date data) {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        String sql = "SELECT sum(li.saldo) FROM liquidacao li "
            + "INNER JOIN empenho e ON e.id = li.empenho_id "
            + "INNER JOIN fontedespesaorc fdo ON fdo.id = e.fontedespesaorc_id "
            + "INNER JOIN despesaorc do ON do.id = fdo.despesaorc_id "
            + "INNER JOIN provisaoppadespesa ppd ON ppd.id = do.provisaoppadespesa_id "
            + "INNER JOIN subacaoppa sp ON sp.id = ppd.subacaoppa_id "
            + "WHERE sp.id =:sub AND (li.dataliquidacao < :data)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("sub", sub.getId());
        q.setParameter("data", data, TemporalType.DATE);
        if (q.getSingleResult() != null) {
            valor = (BigDecimal) q.getSingleResult();
        }
        return valor;
    }

    public ProdutoPPAFacade getSubAcaoPPAFacade() {
        return subAcaoPPAFacade;
    }

    public Boolean validaCodigoIgualParaPrograma(AcaoPrincipal acaoPrincipal) {
        if (acaoPrincipal.getPrograma() != null) {
            if (acaoPrincipal.getPrograma().getId() == null) {
                return true;
            }
        } else {
            return true;
        }


        String sql = "select * from acaoprincipal "
            + " where codigo = :codigo "
            + " and programa_id = :idPrograma";

        if (acaoPrincipal.getId() != null) {
            sql += " and id <> :idAcao";
        }
        Query consulta = getEntityManager().createNativeQuery(sql);
        consulta.setParameter("codigo", acaoPrincipal.getCodigo());
        consulta.setParameter("idPrograma", acaoPrincipal.getPrograma().getId());

        if (acaoPrincipal.getId() != null) {
            consulta.setParameter("idAcao", acaoPrincipal.getId());
        }
        try {
            if (consulta.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public AcaoPrincipal getAcaoPrincipalNovoDaOrigem(AcaoPrincipal acaoPrincipal) {
        try {
            String sql = "select p.* from acaoprincipal p where p.origem_id = :id";
            Query consulta = em.createNativeQuery(sql, AcaoPrincipal.class);
            consulta.setParameter("id", acaoPrincipal.getId());
            consulta.setMaxResults(1);
            return (AcaoPrincipal) consulta.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<AcaoPrincipal> listaAcaoPPAPorPrograma(ProgramaPPA prog, String descricao) {
        String hql = "from AcaoPrincipal a " +
            "           where a.programa = :p " +
            "           and (lower(a.descricao) like :desc" +
            "              or a.codigo like :desc) " +
            "              order by to_number(a.codigo) ";
        Query q = em.createQuery(hql);
        q.setParameter("p", prog);
        q.setParameter("desc", "%" + descricao.toLowerCase().trim() + "%");
        if (!q.getResultList().isEmpty()) {
            return (List<AcaoPrincipal>) q.getResultList();
        } else {
            return new ArrayList<AcaoPrincipal>();
        }
    }

    public List<AcaoPrincipal> buscarAcoesPrincipais(String parte){
        String sql = " SELECT ap.* FROM ACAOPRINCIPAL ap WHERE ap.DESCRICAO LIKE :parte OR ap.CODIGO LIKE :parte ";
        Query q = em.createNativeQuery(sql, AcaoPrincipal.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }

    public PPAFacade getPpaFacade() {
        return ppaFacade;
    }

    public ProgramaPPAFacade getProgramaPPAFacade() {
        return programaPPAFacade;
    }

    public FuncaoFacade getFuncaoFacade() {
        return funcaoFacade;
    }

    public SubFuncaoFacade getSubFuncaoFacade() {
        return subFuncaoFacade;
    }

    public PeriodoFacade getPeriodoFacade() {
        return periodoFacade;
    }

    public PeriodicidadeFacade getPeriodicidadeFacade() {
        return periodicidadeFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public UnidadeMedidaFacade getUnidadeMedidaFacade() {
        return unidadeMedidaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ItemPlanejamentoEstrategicoFacade getItemPlanejamentoEstrategicoFacade() {
        return itemPlanejamentoEstrategicoFacade;
    }

    public PlanejamentoEstrategicoFacade getPlanejamentoEstrategicoFacade() {
        return planejamentoEstrategicoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public TipoAcaoPPAFacade getTipoAcaoPPAFacade() {
        return tipoAcaoPPAFacade;
    }

    public LDOFacade getlDOFacade() {
        return lDOFacade;
    }

    public ConfiguracaoPlanejamentoPublicoFacade getConfiguracaoPlanejamentoPublicoFacade() {
        return configuracaoPlanejamentoPublicoFacade;
    }

    public MedicaoSubAcaoPPAFacade getMedicaoSubAcaoPPAFacade() {
        return medicaoSubAcaoPPAFacade;
    }

    public ProvisaoPPAFacade getProvisaoPPAFacade() {
        return provisaoPPAFacade;
    }
}
