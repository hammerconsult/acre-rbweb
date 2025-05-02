/*
 * Codigo gerado automaticamente em Fri Apr 01 09:51:01 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ProgramaPPAFacade extends AbstractFacade<ProgramaPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private ItemPlanejamentoEstrategicoFacade itemFacade;
    @EJB
    private IndicadorFacade indicadorFacade;
    @EJB
    private PublicoAlvoFacade publicoAlvoFacade;
    @EJB
    private ValorIndicadorFacade valorIndicadorFacade;
    @EJB
    private PeriodicidadeFacade periodicidadeFacade;
    @EJB
    private ItemPlanejamentoEstrategicoFacade itemPlanejamentoEstrategicoFacade;
    @EJB
    private MacroObjetivoEstrategicoFacade macroObjetivoEstrategicoFacade;
    @EJB
    private AcaoPrincipalFacade acaoPpaFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoPlanejamentoPublicoFacade configuracaoPlanejamentoPublicoFacade;

    public ProgramaPPAFacade() {
        super(ProgramaPPA.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ProgramaPPA recuperar(Object id) {
        ProgramaPPA p = em.find(ProgramaPPA.class, id);
        p.getIndicadores().size();
        p.getProjetosAtividades().size();
        p.getPublicoAlvo().size();
        p.getAcoes().size();
        p.getParticipantesProgramaPPA().size();
        return p;
    }

    @Override
    public void salvarNovo(ProgramaPPA entity) {
        ProgramaPPA p = getEntityManager().merge(entity);
    }

    public Boolean verificaExistenciaProgramaPPAPorCodigo(ProgramaPPA programaPPA) {
        String hql = "select p.* from ProgramaPPA p where p.codigo = :codigo and p.exercicio_id = :idExercicio and p.PPA_ID = :idPpa ";
        if (programaPPA.getId() != null) {
            hql += " and p.id <> :idPrograma";
        }
        hql += " order by p.id desc";
        Query q = em.createNativeQuery(hql, ProgramaPPA.class);
        q.setParameter("codigo", programaPPA.getCodigo());
        q.setParameter("idExercicio", programaPPA.getExercicio().getId());
        q.setParameter("idPpa", programaPPA.getPpa().getId());
        if (programaPPA.getId() != null) {
            q.setParameter("idPrograma", programaPPA.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<ProgramaPPA> recuperaProgramasPPA(PPA ppa) {
        List<ProgramaPPA> recuperaPro = new ArrayList<>();
        String hql = "from ProgramaPPA p where p.ppa = :ppa order by id desc";
        if (ppa != null) {
            Query q = em.createQuery(hql);
            q.setParameter("ppa", ppa);
            recuperaPro = q.getResultList();
        } else {
            recuperaPro = new ArrayList<>();
        }
        return recuperaPro;
    }

    public List<ProgramaPPA> recuperaProgramasPPAOrdenandoPorCodigo(PPA ppa) {
        List<ProgramaPPA> recuperaPro = new ArrayList<>();
        String hql = "from ProgramaPPA p where p.ppa = :ppa order by codigo";
        if (ppa != null) {
            Query q = em.createQuery(hql);
            q.setParameter("ppa", ppa);
            recuperaPro = q.getResultList();
        } else {
            recuperaPro = new ArrayList<>();
        }
        return recuperaPro;
    }

    public List<ProgramaPPA> recuperaProgramasPPAOrdenandoPorCodigoQuePossuiLancamento(PPA ppa) {
        List<ProgramaPPA> recuperaPro = new ArrayList<>();
        String hql = "select distinct p from ProvisaoPPADespesa prov " +
            " inner join prov.subAcaoPPA.acaoPPA.programa p " +
            " where p.ppa.id = :ppa " +
            " order by p.codigo";
        if (ppa != null) {
            Query q = em.createQuery(hql);
            q.setParameter("ppa", ppa.getId());
            recuperaPro = q.getResultList();
        } else {
            recuperaPro = new ArrayList<>();
        }
        return recuperaPro;
    }

    public List<ProgramaPPA> buscarProgramasPPAOrdenandoPorCodigoQuePossuiLancamento(PPA ppa, Date dataIncial, Date dataFinal) {
        String sql = " select distinct p.* from ProvisaoPPADespesa prov " +
            " inner join subAcaoPPA sub on prov.subAcaoPPA_id = sub.id  " +
            " inner join acaoPPA ac on sub.acaoPPA_id = ac.id " +
            " inner join ProgramaPPA p on ac.programa_id = p.id " +
            " where p.ppa_id = :ppa " +
            " and trunc(p.dataCadastro) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            " order by p.codigo";
        if (ppa != null) {
            Query q = em.createNativeQuery(sql, ProgramaPPA.class);
            q.setParameter("ppa", ppa.getId());
            q.setParameter("dataInicial", DataUtil.getDataFormatada(dataIncial));
            q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
            return q.getResultList();
        }
        return Lists.newArrayList();
    }


    public String recuperaUltimoCodigo(PPA ppa) {
        String num;
        String hql = " from ProgramaPPA a where a.codigo = (select max(b.codigo) as codigo from ProgramaPPA b where b.ppa = :PPA) ";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("PPA", ppa);
        if (!q.getResultList().isEmpty()) {
            Long l = Long.parseLong(((ProgramaPPA) q.getSingleResult()).getCodigo()) + 1;
            num = String.valueOf(l);
        } else {
            return "1";
        }
        return num;
    }

    @Override
    public void remover(ProgramaPPA entity) {
        Object chave = Persistencia.getId(entity);
        Object obj = recuperar(chave);
        if (obj != null) {
            for (AcaoPPA a : entity.getProjetosAtividades()) {
                a.setPrograma(null);
                getEntityManager().merge(a);
            }

            getEntityManager().remove(obj);

        }
    }
//
//     public List<ProgramaPPA> listaProgPPAPorPPA(PPA ppa){
//        String hql = "select p from ProgramaPPA p where p.ppa =:PPA";
//        Query q = em.createQuery(hql);
//        q.setParameter("PPA", ppa);
//        return q.getResultList();
//    }

    public List<ProgramaPPA> listaNaoAprovados(String parte) {
        String sql = "SELECT P.* "
            + " FROM PROGRAMAPPA P"
            + " WHERE ((LOWER (P.DENOMINACAO) LIKE :parte) OR (LOWER (P.CODIGO) LIKE :parte)) AND P.SOMENTELEITURA = 0"
            + " ORDER BY P.CODIGO";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<ProgramaPPA> listaFiltrandoProgramasPorPPA(String parte, PPA ppa) {
        String sql = "SELECT PROG.* "
            + " FROM PROGRAMAPPA PROG"
            + " WHERE ((PROG.CODIGO LIKE :parte) OR (LOWER(PROG.DENOMINACAO) LIKE :parte))"
            + " AND PROG.PPA_ID = :ppa "
            + " AND PROG.SOMENTELEITURA = 0"
            + " ORDER BY PROG.CODIGO";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ppa", ppa.getId());
        return q.getResultList();
    }

    public List<ProgramaPPA> buscarProgramasPorPPA(String filter, PPA ppa) {
        String sql = " SELECT PROG.* "
            + " FROM PROGRAMAPPA PROG"
            + " WHERE ((PROG.CODIGO LIKE :filter) OR (LOWER(PROG.DENOMINACAO) LIKE :filter))"
            + " AND PROG.PPA_ID = :ppa "
            + " order by prog.codigo ";

        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("filter", "%" + filter.toLowerCase().trim() + "%");
        q.setParameter("ppa", ppa.getId());
        return q.getResultList();

    }

    public List<ProgramaPPA> buscarProgramas(String parte){
        String sql = " select pPpa.* from PROGRAMAPPA pPpa where lower(pPpa.DENOMINACAO) like :parte order by pPpa.CODIGO ";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<ProgramaPPA> listaFiltrandoProgramasPorPPAeExercicio(String parte, PPA ppa, Exercicio ex) {
        String sql = "SELECT PROG.* "
            + " FROM PROGRAMAPPA PROG"
            + " WHERE ((PROG.CODIGO LIKE :parte) OR (LOWER(PROG.DENOMINACAO) LIKE :parte))"
            + " AND PROG.PPA_ID = :ppa "
            + " AND PROG.EXERCICIO_ID = :exercicio "
            + " AND PROG.SOMENTELEITURA = 0"
            + " ORDER BY PROG.CODIGO";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ppa", ppa.getId());
        q.setParameter("exercicio", ex.getId());
        return q.getResultList();
    }

    public List<ProgramaPPA> buscarProgramasPorPPAeExercicioPriorizadoLDO(String parte, PPA ppa, Exercicio ex) {
        String sql = "SELECT DISTINCT PROG.* " +
            " FROM PROGRAMAPPA PROG " +
            " INNER JOIN acaoprincipal ACAO ON PROG.ID = acao.programa_id " +
            " INNER JOIN PRODUTOPPA PRO ON ACAO.ID = pro.acaoprincipal_id " +
            " WHERE ((PROG.CODIGO LIKE :parte) OR (LOWER(PROG.DENOMINACAO) LIKE :parte)) " +
            " AND PROG.PPA_ID = :ppa " +
            " AND PROG.EXERCICIO_ID = :exercicio " +
            " ORDER BY PROG.CODIGO";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ppa", ppa.getId());
        q.setParameter("exercicio", ex.getId());
        return q.getResultList();
    }

    public List<ProgramaPPA> listaFiltrandoProgramasPorExercicio(String parte, Exercicio ex) {
        String sql = "SELECT PROG.* "
            + " FROM PROGRAMAPPA PROG"
            + " WHERE ((PROG.CODIGO LIKE :parte) OR (LOWER(PROG.DENOMINACAO) LIKE :parte))"
            + " AND PROG.EXERCICIO_ID = :exercicio "
            + " AND PROG.SOMENTELEITURA = 0"
            + " ORDER BY PROG.CODIGO";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", ex.getId());
        return q.getResultList();
    }


    public List<ProgramaPPA> buscarProgramasPorExercicio(String parte, Exercicio ex) {
        String sql = "SELECT PROG.* "
            + " FROM PROGRAMAPPA PROG"
            + " WHERE ((PROG.CODIGO LIKE :parte) OR (LOWER(PROG.DENOMINACAO) LIKE :parte))"
            + " AND PROG.EXERCICIO_ID = :exercicio "
            + " ORDER BY PROG.CODIGO";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", ex.getId());
        return q.getResultList();
    }

    public ProgramaPPA buscarProgramasPorExercicio(String codigo, String descricao, Exercicio exercicio) {
        String sql = "SELECT PROG.* "
            + " FROM PROGRAMAPPA PROG"
            + " WHERE trim(PROG.CODIGO) LIKE :codigo and trim(PROG.DENOMINACAO) LIKE :descricao "
            + " AND PROG.EXERCICIO_ID = :exercicio "
            + " AND PROG.SOMENTELEITURA = 0";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("codigo", codigo.trim());
        q.setParameter("descricao", descricao.trim());
        q.setParameter("exercicio", exercicio.getId());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (ProgramaPPA) q.getSingleResult();
    }

    public List<ProgramaPPA> listaFiltrandoProgramasPorMacroObjetivo(String parte, MacroObjetivoEstrategico moe) {
        String sql = "SELECT PROG.* "
            + " FROM PROGRAMAPPA PROG"
            + " WHERE ((PROG.CODIGO LIKE :parte) OR (LOWER(PROG.DENOMINACAO) LIKE :parte))"
            + " AND PROG.MACROOBJETIVOESTRATEGICO_ID = :moe "
            + " ORDER BY PROG.CODIGO";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("moe", moe.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public Boolean hasProgramaNaProvisaoPPADespesa(ProgramaPPA prog) throws ExcecaoNegocioGenerica {
        try {
            Query q = getEntityManager().createQuery(" SELECT pr FROM ProvisaoPPADespesa prov "
                + " inner join prov.subAcaoPPA sub "
                + " INNER JOIN sub.acaoPPA ac "
                + " inner join ac.programa pr "
                + " where pr =:prog");
            q.setParameter("prog", prog);
            return !q.getResultList().isEmpty();
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro a o executar pesquisa para verificar se o Programa esta vinculado a uma Provisão PPA :" + e.getMessage());
        }
    }

    public List<ProgramaPPA> buscarProgramasPpa (String parte){
        String sql = " SELECT pPpa.* from PROGRAMAPPA pPpa WHERE pPpa.DENOMINACAO LIKE :parte OR pPpa.CODIGO LIKE :parte ";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }

    public PPAFacade getPpaFacade() {
        return ppaFacade;
    }

    public ItemPlanejamentoEstrategicoFacade getItemFacade() {
        return itemFacade;
    }

    public IndicadorFacade getIndicadorFacade() {
        return indicadorFacade;
    }

    public PublicoAlvoFacade getPublicoAlvoFacade() {
        return publicoAlvoFacade;
    }

    public ValorIndicadorFacade getValorIndicadorFacade() {
        return valorIndicadorFacade;
    }

    public PeriodicidadeFacade getPeriodicidadeFacade() {
        return periodicidadeFacade;
    }

    public ItemPlanejamentoEstrategicoFacade getItemPlanejamentoEstrategicoFacade() {
        return itemPlanejamentoEstrategicoFacade;
    }

    public MacroObjetivoEstrategicoFacade getMacroObjetivoEstrategicoFacade() {
        return macroObjetivoEstrategicoFacade;
    }

    public AcaoPrincipalFacade getAcaoPpaFacade() {
        return acaoPpaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public HierarquiaOrganizacionalFacadeOLD getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ConfiguracaoPlanejamentoPublicoFacade getConfiguracaoPlanejamentoPublicoFacade() {
        return configuracaoPlanejamentoPublicoFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public BigDecimal getValorTotalProgramaPPA(ProgramaPPA programaPPA) {
        try {
            Query consulta = em.createNativeQuery("SELECT SUM(COALESCE(valor,0)) " +
                " FROM programappa prog " +
                " inner join acaoprincipal ac on prog.id = ac.programa_id " +
                " inner join acaoppa acao on ac.id = acao.acaoprincipal_id " +
                " inner join subacaoppa sub on acao.id = sub.acaoppa_id " +
                " INNER JOIN provisaoppadespesa prov ON sub.id= prov.subacaoppa_id " +
                " WHERE prog.id = :prog");
            consulta.setParameter("prog", programaPPA.getId());
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

    public ProgramaPPA getProgramaPPANovoDaOrigem(ProgramaPPA programa) {
        Query consulta = em.createNativeQuery("select p.* from programappa p where p.origem_id = :id", ProgramaPPA.class);
        consulta.setParameter("id", programa.getId());
        consulta.setMaxResults(1);
        try {
            return (ProgramaPPA) consulta.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProgramaPPA salvarNovoPrograma(ProgramaPPA entity) {
        return em.merge(entity);
    }

    public List<ProgramaPPA> buscarProgramasPorExercicioDaSubAcao(Exercicio ex) {
        String sql = "  select distinct a.* " +
            "  from programappa a " +
            "  inner join acaoppa acao on a.id = acao.PROGRAMA_ID" +
            "  inner join subacaoppa sba on acao.id = sba.ACAOPPA_ID" +
            "  inner join PROVISAOPPADESPESA prov on sba.id = prov.SUBACAOPPA_ID" +
            "      where sba.EXERCICIO_ID = :idExercicio " +
            "      order by a.codigo ";
        Query q = em.createNativeQuery(sql, ProgramaPPA.class);
        q.setParameter("idExercicio", ex.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }
}
