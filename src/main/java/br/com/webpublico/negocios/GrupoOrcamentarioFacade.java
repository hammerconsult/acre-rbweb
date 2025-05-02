/*
 * Codigo gerado automaticamente em Tue Jan 17 13:45:12 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.GrupoOrcamentario;
import br.com.webpublico.entidades.UnidadeOrganizacional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless

public class GrupoOrcamentarioFacade extends AbstractFacade<GrupoOrcamentario> {

    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ContaFacade contaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoOrcamentarioFacade() {
        super(GrupoOrcamentario.class);
    }

    @Override
    public GrupoOrcamentario recuperar(Object id) {
        GrupoOrcamentario go = em.find(GrupoOrcamentario.class, id);
        go.getFonteDespesaOrc().size();
        return go;
    }

    @Override
    public void salvarNovo(GrupoOrcamentario entity) {
        em.persist(entity);
        for (FonteDespesaORC fon : entity.getFonteDespesaOrc()) {
            fon.setGrupoOrcamentario(entity);
            em.merge(fon);
        }
    }

    public boolean validaMesmoCodigo(GrupoOrcamentario grupo) {
        String hql = " FROM GrupoOrcamentario G WHERE G.codigo = :codigo and G.exercicio.id = :exercicio";
        if (grupo.getId() != null) {
            hql += " and g.id <> :id ";
        }
        Query consulta = em.createQuery(hql);
        consulta.setParameter("codigo", grupo.getCodigo().trim());
        consulta.setParameter("exercicio", grupo.getExercicio().getId());
        if (grupo.getId() != null) {
            consulta.setParameter("id", grupo.getId());
        }
        if (consulta.getResultList().isEmpty()) {
            return true;
        }
        return false;

    }

    public void meuSalvar(GrupoOrcamentario entity) {
        em.merge(entity);
    }

    @Override
    public void salvar(GrupoOrcamentario entity) {
        GrupoOrcamentario go = recuperar(entity.getId());
        List<FonteDespesaORC> novas = new ArrayList<FonteDespesaORC>();
        List<FonteDespesaORC> continuadas = new ArrayList<FonteDespesaORC>();
        for (FonteDespesaORC f : entity.getFonteDespesaOrc()) {
            if (!go.getFonteDespesaOrc().contains(f)) {
                novas.add(f);
            } else {
                continuadas.add(f);
            }
        }
        for (FonteDespesaORC f : novas) {
            f.setGrupoOrcamentario(entity);
            em.merge(f);
        }
        go.getFonteDespesaOrc().removeAll(continuadas);
        for (FonteDespesaORC f : go.getFonteDespesaOrc()) {
            f.setGrupoOrcamentario(null);
            em.merge(f);
        }
        em.merge(entity);
    }

    @Override
    public void remover(GrupoOrcamentario entity) {
        for (FonteDespesaORC f : entity.getFonteDespesaOrc()) {
            f.setGrupoOrcamentario(null);
            em.merge(f);
        }
        entity.setFonteDespesaOrc(new ArrayList<FonteDespesaORC>());
        em.merge(entity);
        GrupoOrcamentario go = recuperar(entity.getId());
        em.remove(go);
    }

    public List<GrupoOrcamentario> listaSemCotasPorExercicio(String parte, Exercicio ex, String... atributos) {
        String sql = "SELECT go.* FROM grupoorcamentario go WHERE (";
        for (String atributo : atributos) {
            sql += "lower(go." + atributo + ") like :parte OR ";
        }
        sql = sql.substring(0, sql.length() - 3);
        sql += ") and (go.exercicio_id = :exer) "
                + "and go.id not in ( "
                + "select gru.id from grupoorcamentario gru "
                + "inner join grupocotaorc gc on gru.id = gc.grupoorcamentario_id "
                + "inner join cotaorcamentaria co on gc.id = co.grupocotaorc_id)";
        Query q = em.createNativeQuery(sql, GrupoOrcamentario.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("exer", ex.getId());
        return q.getResultList();
    }

    public List<GrupoOrcamentario> listaExercicio(Exercicio ex) {
        String sql = "SELECT * FROM grupoorcamentario grupo INNER JOIN exercicio ex ON grupo.exercicio_id = ex.id WHERE ex.ano = :ano";
        Query q = em.createNativeQuery(sql, GrupoOrcamentario.class);
        q.setParameter("ano", ex.getAno());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<GrupoOrcamentario>();
        } else {
            return q.getResultList();
        }
    }

    public List<GrupoOrcamentario> listaGrupoPorExercicio(Exercicio ex, String parte) {
        String sql = " SELECT * FROM grupoorcamentario grupo " +
                " INNER JOIN exercicio ex ON grupo.exercicio_id = ex.id " +
                " WHERE ex.ano = :ano " +
                " AND (replace(grupo.codigo, '.', '') LIKE :parte" +
                "     OR grupo.codigo LIKE :parte " +
                "     OR grupo.descricao LIKE :parte)";
        Query q = em.createNativeQuery(sql, GrupoOrcamentario.class);
        q.setParameter("ano", ex.getAno());
        q.setParameter("parte", "%" + parte.toUpperCase() + "%");
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<GrupoOrcamentario>();
        } else {
            return q.getResultList();
        }
    }

    public List<GrupoOrcamentario> listaGrupoOrcamentarioPorExercicioEUnidde(String parte, Exercicio ex, UnidadeOrganizacional unidade) {
        String sql = " select g.* from grupoorcamentario g                              " +
                "      inner join unidadeorganizacional unid on unid.id = g.unidade_id  " +
                "      inner join exercicio ex on ex.id = g.exercicio_id                " +
                "       where ex.id = :idExercicio                                      " +
                "       and unid.id = :idUnidade                                        " +
                "       and (replace(g.codigo, '.', '') like :parte                     " +
                "                 or g.codigo like :parte                               " +
                "                 or g.descricao like :parte)                           " +
                "      order by g.codigo desc                                           ";
        Query q = em.createNativeQuery(sql, GrupoOrcamentario.class);
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("parte", "%" + parte.toUpperCase() + "%");
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<GrupoOrcamentario>();
        } else {
            return q.getResultList();
        }
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public FuncaoFacade getFuncaoFacade() {
        return funcaoFacade;
    }

    public SubFuncaoFacade getSubFuncaoFacade() {
        return subFuncaoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public HierarquiaOrganizacionalFacadeOLD getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ProgramaPPAFacade getProgramaPPAFacade() {
        return programaPPAFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }
}
