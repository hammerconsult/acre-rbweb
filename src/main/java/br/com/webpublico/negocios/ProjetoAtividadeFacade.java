package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

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

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 20/09/13
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ProjetoAtividadeFacade extends AbstractFacade<AcaoPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private ProdutoPPAFacade produtoPPAFacade;
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
    private AcaoPrincipalFacade acaoPrincipalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProjetoAtividadeFacade() {
        super(AcaoPPA.class);
    }

    public void salvarMedicoesAcaoPPAAprovada(AcaoPPA ac) {
        for (SubAcaoPPA s : ac.getSubAcaoPPAs()) {
            for (MedicaoSubAcaoPPA m : s.getMedicoesSubAcaoPPA()) {
                em.merge(m);
            }
        }
    }

    public String geraProximoCodigo(ProgramaPPA programa) {
        String hql = "select a from AcaoPPA a where a.codigo = (select max(b.codigo) from AcaoPPA b where b.programa =:prog)";
        Long cod = 0l;
        Query q = em.createQuery(hql);
        q.setParameter("prog", programa);
        q.setMaxResults(1);
        if (q.getResultList().size() >= 1) {
            AcaoPPA acao = (AcaoPPA) q.getSingleResult();
            cod = Long.parseLong(acao.getCodigo());
        }
        return String.valueOf(cod + 1);

    }

    public List<AcaoPPA> buscarAcoesPPAPorExercicioUnidade(String parte, Exercicio ex, UnidadeOrganizacional unidadeOrganizacional, String codigoProgramaARemover) {
        String sql = " SELECT A.* FROM ACAOPPA A " +
            " inner join programappa prog on a.programa_id = prog.id " +
            " WHERE (lower(A.descricao) LIKE :PARTE or a.codigo like :PARTE)" +
            " AND a.EXERCICIO_ID = :EXERCICIO " +
            " AND a.RESPONSAVEL_ID = :UO " +
            (!Strings.isNullOrEmpty(codigoProgramaARemover) ? " AND prog.codigo not like :codigoProgramaARemover " : "") +
            " order by a.codigo ";
        Query q = em.createNativeQuery(sql, AcaoPPA.class);
        q.setParameter("PARTE", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("EXERCICIO", ex.getId());
        q.setParameter("UO", unidadeOrganizacional.getId());
        if (!Strings.isNullOrEmpty(codigoProgramaARemover)) {
            q.setParameter("codigoProgramaARemover", codigoProgramaARemover);
        }
        List<AcaoPPA> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return Lists.newArrayList();

    }

    public List<AcaoPPA> buscarProjetoAtividadeViculadoAProvisaoPPADespesa(String parte, Exercicio ex, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "  select distinct a.* " +
            "  from acaoppa a " +
            "  inner join subacaoppa sba on a.id = sba.acaoppa_id " +
            "  inner join provisaoppadespesa prov on sba.id = prov.SUBACAOPPA_ID " +
            "  inner join despesaorc desp on prov.id = desp.provisaoppadespesa_id " +
            "  inner join PROGRAMAPPA prog on a.PROGRAMA_ID = prog.ID " +
            "      where (lower(a.descricao) like :parte or a.codigo like :parte) " +
            "      and a.exercicio_id = :idExercicio " +
            "      and a.responsavel_id = :idUnidade " +
            "      order by a.codigo ";
        Query q = em.createNativeQuery(sql, AcaoPPA.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }

    }

    public List<AcaoPPA> buscarAcoesPPAPorExercicio(String parte, Exercicio ex) {
        String sql = " SELECT A.* FROM ACAOPPA A " +
            " inner join tipoacaoppa t on t.id = a.tipoacaoppa_id " +
            " inner join programappa prog on A.PROGRAMA_ID = prog.ID " +
            " WHERE (lower(A.descricao) LIKE :PARTE or t.codigo || a.codigo like :PARTE) AND a.EXERCICIO_ID = :EXERCICIO order by a.codigo ";
        Query q = em.createNativeQuery(sql, AcaoPPA.class);
        q.setParameter("PARTE", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("EXERCICIO", ex.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }

    }

    public String getProximoCodigo(Exercicio exercicio) {
        try {
            Query consulta = em.createNativeQuery("select max(to_number(coalesce((ac.codigo),'1'))) from acaoppa ac where ac.exercicio_id = :ex");
            consulta.setParameter("ex", exercicio.getId());
            BigDecimal valor = (BigDecimal) consulta.getSingleResult();
            return valor.toString();
        } catch (Exception e) {
            return "001";
        }
    }

    @Override
    public AcaoPPA recuperar(Object id) {
        AcaoPPA pf = em.find(AcaoPPA.class, id);
        for (SubAcaoPPA s : pf.getSubAcaoPPAs()) {
            s.getMedicoesSubAcaoPPA().size();

            s.getProvisaoPPADespesas().size();
        }

        pf.getParticipanteAcaoPPA()
            .size();
        return pf;
    }

    public List<AcaoPPA> recuperaAcoesPpa(ProgramaPPA prog) {
        List<AcaoPPA> recuperaAcao = new ArrayList<AcaoPPA>();
        String hql = "from AcaoPPA a where a.programa = :p";
        if (prog != null) {
            Query q = em.createQuery(hql);
            q.setParameter("p", prog);
            recuperaAcao = q.getResultList();
        } else {
            recuperaAcao = new ArrayList<AcaoPPA>();
        }
        return recuperaAcao;
    }

    public List<AcaoPPA> recuperaAcoesPpaPorAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        List<AcaoPPA> recuperaAcao = new ArrayList<AcaoPPA>();
        String hql = "from AcaoPPA a where a.acaoPrincipal = :ac";
        if (acaoPrincipal != null) {
            Query q = em.createQuery(hql);
            q.setParameter("ac", acaoPrincipal);
            recuperaAcao = q.getResultList();
        } else {
            recuperaAcao = new ArrayList<AcaoPPA>();
        }
        return recuperaAcao;
    }

    public List<SubAcaoPPA> recuperaSubAcaoPPA(AcaoPPA ac) {
        String hql = "from SubAcaoPPA s where s.acaoPPA = :acao ";

        Query q = em.createQuery(hql);
        q.setParameter("acao", ac);

        return q.getResultList();
    }

    public List<AcaoPPA> listaAcaoPPAPorPPA(PPA ppa) {
        String hql = " select ap from AcaoPPA ap "
            + " inner join ap.programa pp  "
            + " inner join pp.ppa p "
            + " where p =:PPA  "
            + "order by ap.codigo";
        Query q = em.createQuery(hql);
        q.setParameter("PPA", ppa);
        return q.getResultList();
    }

    public List<AcaoPPA> buscarAcoesPPAPorPPAComLancamento(PPA ppa, Date dataInicial, Date dataFinal) {
        String sql = " select distinct ap.* from ProvisaoPPADespesa prov"
            + " inner join subAcaoPPA sub on prov.subAcaoPPA_id = sub.id "
            + " inner join acaoPPA ap on sub.acaoPPA_id = ap.id "
            + " inner join programappa pp on ap.programa_id = pp.id "
            + " where pp.ppa_id = :PPA  "
            + " and trunc(ap.dataCadastro) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') "
            + " order by ap.codigo, (select sum(coalesce(valor,0)) from subacaoppa sub " +
            "                                                            inner join provisaoppadespesa prov on sub.id = prov.subacaoppa_id " +
            "                     where sub.acaoppa_id = ap.id)  desc";
        Query q = em.createNativeQuery(sql, AcaoPPA.class);
        q.setParameter("PPA", ppa.getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        return q.getResultList();
    }

    public List<AcaoPPA> listaAcaoPPAPorPPA(PPA ppa, String descricao) {
        String hql = " select ap from AcaoPPA ap "
            + " inner join ap.programa pp  "
            + " inner join pp.ppa p "
            + " where p = :PPA " +
            "   and (ap.descricao like :descricao " +
            "     or ap.codigo like :descricao)"
            + "order by ap.codigo asc";
        Query q = em.createQuery(hql);
        q.setParameter("PPA", ppa);
        q.setParameter("descricao", "%" + descricao + "%");
        return q.getResultList();
    }

    public List<AcaoPPA> listaAcaoPPAPorPPAEPrograma(PPA ppa, ProgramaPPA programaPPA, String descricao) {
        String hql = " select ap from AcaoPPA ap "
            + " inner join ap.programa pp  "
            + " inner join pp.ppa p "
            + " where p = :PPA "
            + " and pp = :Programa "
            + "   and (ap.descricao like :descricao "
            + "     or ap.codigo like :descricao)"
            + "order by ap.codigo asc";
        Query q = em.createQuery(hql);
        q.setParameter("PPA", ppa);
        q.setParameter("Programa", programaPPA);
        q.setParameter("descricao", "%" + descricao + "%");
        return q.getResultList();
    }

    public List<AcaoPPA> listaAcaoPPANaoVinculadaProgramas(String descricao) {

        Query q = em.createQuery("select ac from AcaoPPA ac where ac.programa is null and ac.descricao like :descricao ");
        q.setParameter("descricao", "%" + descricao + "%");
        return q.getResultList();
    }

    public BigDecimal buscaValorFinanceiroLiquidacoes(SubAcaoPPA sub, Date data) {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        String sql = "SELECT sum(li.saldo) FROM liquidacao li "
            + "INNER JOIN empenho e ON e.id = li.empenho_id "
            + "INNER JOIN fontedespesaorc fdo ON fdo.id = e.fontedespesaorc_id "
            + "INNER JOIN despesaorc do ON do.id = fdo.despesaorc_id "
            + "INNER JOIN provisaoppadespesa ppd ON ppd.id = do.provisaoppadespesa_id "
            + "INNER JOIN provisaoppa pp ON pp.id = ppd.provisaoppa_id "
            + "INNER JOIN subacaoppa sp ON sp.id = pp.subacao_id "
            + "WHERE sp.id =:sub AND (li.dataliquidacao < :data)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("sub", sub.getId());
        q.setParameter("data", data, TemporalType.DATE);
        if (q.getSingleResult() != null) {
            valor = (BigDecimal) q.getSingleResult();
        }
        return valor;
    }


    public boolean hasCodigoIgualParaPrograma(AcaoPPA acaoPPA, Exercicio exercicio) {
        if (acaoPPA.getPrograma() != null) {
            if (acaoPPA.getPrograma().getId() == null) {
                return true;
            }
        } else {
            return true;
        }


        String sql = "select acao.* from acaoppa acao"
            + " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id "
            + " where acao.codigo = :codigo "
            + " and acao.programa_id = :idPrograma "
            + " and acao.exercicio_id = :exercicio "
            + " and acao.responsavel_id = :responsavel "
            + " and tipo.id = :idTipo ";

        if (acaoPPA.getId() != null) {
            sql += " and acao.id <> :idAcao";
        }
        Query consulta = getEntityManager().createNativeQuery(sql);
        consulta.setParameter("codigo", acaoPPA.getCodigo());
        consulta.setParameter("idPrograma", acaoPPA.getPrograma().getId());
        consulta.setParameter("idTipo", acaoPPA.getTipoAcaoPPA().getId());
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("responsavel", acaoPPA.getResponsavel().getId());

        if (acaoPPA.getId() != null) {
            consulta.setParameter("idAcao", acaoPPA.getId());
        }
        try {
            return consulta.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean validaCodigoIgualParaProgramaDiferente(AcaoPPA acaoPPA) {
        if (acaoPPA.getPrograma() != null) {
            if (acaoPPA.getPrograma().getId() == null) {
                return true;
            }
        } else {
            return true;
        }


        String sql = "select acao.* from acaoppa acao"
            + " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id "
            + " where acao.codigo = :codigo "
            + " and tipo.id = :idTipo "
            + " and acao.exercicio_id = :exercicio";

        if (acaoPPA.getId() != null) {
            sql += " and acao.id <> :idAcao";
        }
        Query consulta = getEntityManager().createNativeQuery(sql, AcaoPPA.class);
        consulta.setParameter("codigo", acaoPPA.getCodigo());
        consulta.setParameter("idTipo", acaoPPA.getTipoAcaoPPA().getId());
        consulta.setParameter("exercicio", acaoPPA.getExercicio().getId());
        if (acaoPPA.getId() != null) {
            consulta.setParameter("idAcao", acaoPPA.getId());
        }
        try {
            if (consulta.getResultList().isEmpty()) {
                return true;
            } else {
                for (AcaoPPA acao : (List<AcaoPPA>) consulta.getResultList()) {
                    if (!acao.getDescricao().equals(acaoPPA.getDescricao())) {
//                        FacesUtil.addError("Não foi possível salvar!", " O código informado: " + acaoPPA.getCodigo() + " deve utilizar a descrição (" + acao.getDescricao() + ")");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<AcaoPPA> buscarProjetoAtividadePorPrograma(ProgramaPPA prog, String descricao) {
        String hql = "from AcaoPPA a where a.programa = :p " +
            "                and (lower(a.descricao) like :desc " +
            "                        or a.codigo like :desc)";
        Query q = em.createQuery(hql);
        q.setParameter("p", prog);
        q.setParameter("desc", "%" + descricao.toLowerCase().trim() + "%");
        if (!q.getResultList().isEmpty()) {
            return (List<AcaoPPA>) q.getResultList();
        } else {
            return new ArrayList<AcaoPPA>();
        }
    }

    public List<AcaoPPA> listaProjetoAtividadePorAcaoPrincipal(AcaoPrincipal ap, String descricao) {
        String hql = "from " +
            "       AcaoPPA a " +
            "       where a.acaoPrincipal = :ap " +
            "       and (lower(a.descricao) like :desc" +
            "         or a.codigo like :desc) ";
        Query q = em.createQuery(hql);
        q.setParameter("ap", ap);
        q.setParameter("desc", "%" + descricao.toLowerCase().trim() + "%");
        if (!q.getResultList().isEmpty()) {
            return (List<AcaoPPA>) q.getResultList();
        } else {
            return new ArrayList<AcaoPPA>();
        }
    }

    @Override
    public void salvar(AcaoPPA entity) {
        if (entity.getSubAcaoPPAs() != null) {
            for (SubAcaoPPA subAcaoPPA : entity.getSubAcaoPPAs()) {
                if (subAcaoPPA != null) {
                    if (subAcaoPPA.getProvisaoPPADespesas() != null) {
                        for (ProvisaoPPADespesa provisaoPPADespesa : subAcaoPPA.getProvisaoPPADespesas()) {
                            provisaoPPADespesa.setUnidadeOrganizacional(entity.getResponsavel());
                        }
                    }
                }
            }
        }
        super.salvar(entity);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void salvarNovo(AcaoPPA entity) {
        super.salvarNovo(entity);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public BigDecimal getValorTotalProjetoAtividade(AcaoPPA acaoPPA) {
        try {
            Query consulta = em.createNativeQuery("select sum(coalesce(valor,0)) from subacaoppa sub " +
                " inner join provisaoppadespesa prov on sub.id = prov.subacaoppa_id" +
                " where sub.acaoppa_id = :acao");
            consulta.setParameter("acao", acaoPPA.getId());
            BigDecimal valor = (BigDecimal) consulta.getSingleResult();
            if (valor == null) {
                return BigDecimal.ZERO;
            }
            return valor;
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public void retornaCodigoSubAcaoPPA(AcaoPPA acaoPPA, SubAcaoPPA subAcaoPPA) {
        Long codigoMaior = 0L;
        Long codigoSub = 0L;
        for (SubAcaoPPA s : acaoPPA.getSubAcaoPPAs()) {
            codigoSub = Long.parseLong(s.getCodigo());
            if (codigoSub > codigoMaior) {
                codigoMaior = codigoSub;
            }
        }
        Long codigo = codigoMaior + 1;

        ConfiguracaoPlanejamentoPublico cpp = configuracaoPlanejamentoPublicoFacade.retornaUltima();
        if (cpp.getMascaraCodigoSubAcao() == null) {
            subAcaoPPA.setCodigo(StringUtil.cortaOuCompletaDireita(String.valueOf(codigo), cpp.getMascaraCodigoSubAcao().length(), "0"));
        } else {
            subAcaoPPA.setCodigo("000" + String.valueOf(codigo));
        }
    }

    public AcaoPPA buscarAcaoPPAPeloCodigoEDescricaoEExercicio(String codigo, String descricao, Exercicio exercicio) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from acaoppa where exercicio_id = :exercicio ")
            .append(" and descricao like :descricao ")
            .append(" and codigo like :codigo ")
            .append(" and ROWNUM = 1 ");
        Query q = em.createNativeQuery(sql.toString(), AcaoPPA.class);
        q.setParameter("codigo", codigo);
        q.setParameter("descricao", descricao);
        q.setParameter("exercicio", exercicio.getId());

        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (AcaoPPA) q.getSingleResult();
        }
    }

    public List<AcaoPPA> buscarProjetoAtividadeExercicio(Exercicio ex) {
        String sql = "  select distinct a.* " +
            "  from acaoppa a " +
            "  inner join subacaoppa sba on a.id = sba.acaoppa_id " +
            "  inner join PROVISAOPPADESPESA prov on sba.id = prov.SUBACAOPPA_ID" +
            "      where sba.EXERCICIO_ID = :idExercicio " +
            "      order by a.codigo ";
        Query q = em.createNativeQuery(sql, AcaoPPA.class);
        q.setParameter("idExercicio", ex.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<AcaoPPA> buscarAcoesPPAPorLoa(LOA loa) {
        String sql = " select acao.* from ACAOPPA acao " +
            " inner join PROGRAMAPPA prog on prog.ID = acao.PROGRAMA_ID " +
            " inner join PPA ppa on ppa.ID = prog.PPA_ID " +
            " inner join LDO ldo on ppa.ID = ldo.PPA_ID " +
            " inner join LOA loa on loa.LDO_ID = ldo.ID " +
            " where loa.ID = :idLoa ";
        Query q = em.createNativeQuery(sql, AcaoPPA.class);
        q.setParameter("idLoa", loa.getId());
        return q.getResultList();
    }

    public SubAcaoPPA buscarSubacaoPorAcaoAndDescricao(String descricao, AcaoPPA acaoPPA) {
        String sql = " select * from SubAcaoPPA s where s.acaoPPA_id = :acao and lower(trim(s.descricao)) like :descricao ";
        Query q = em.createNativeQuery(sql, SubAcaoPPA.class);
        q.setParameter("acao", acaoPPA.getId());
        q.setParameter("descricao", descricao.trim().toLowerCase());
        q.setMaxResults(1);
        List<SubAcaoPPA> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public ProvisaoPPAFacade getProvisaoPPAFacade() {
        return provisaoPPAFacade;
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

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    public ProdutoPPAFacade getProdutoPPAFacade() {
        return produtoPPAFacade;
    }

    public AcaoPrincipalFacade getAcaoPrincipalFacade() {
        return acaoPrincipalFacade;
    }
}
