package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Fase;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import org.primefaces.model.DualListModel;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 15/10/13
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class FaseFacade extends AbstractFacade<Fase> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private RecursoSistemaFacade recursoSistemaFacade;
    @EJB
    private GrupoRecursoFacade grupoRecursoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FaseFacade() {
        super(Fase.class);
    }

    @Override
    public Fase recuperar(Object id) {
        Fase fase = em.find(Fase.class, id);
        ;
        fase.getPeriodoFases().size();
        fase.getRecursos().size();
        return fase;
    }

    public void salvarNovo2(Fase entity) {
        List<RecursoSistema> recursos = new ArrayList<RecursoSistema>();
        for (RecursoSistema rs : entity.getRecursos()) {
            recursos.add(em.find(RecursoSistema.class, rs.getId()));
        }
        entity.getRecursos().clear();
        entity.getRecursos().addAll(recursos);
        entity.setCodigo(getUltimoCodigo());
        em.persist(entity);
    }

    public void salvar2(Fase entity) {
        List<RecursoSistema> recursos = new ArrayList<RecursoSistema>();
        for (RecursoSistema rs : entity.getRecursos()) {
            recursos.add(em.find(RecursoSistema.class, rs.getId()));

        }
        entity.getRecursos().clear();
        entity.getRecursos().addAll(recursos);
        em.merge(entity);
    }

    public String getUltimoCodigo() {
        String sql = "select max(to_number(f.codigo)) + 1 as ultimoCodigo from fase f ";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public DualListModel<Fase> recuperaTodasFases() {
        String sql = "SELECT * FROM FASE ";
        Query q = em.createNativeQuery(sql, Fase.class);
        if (q.getResultList().isEmpty()) {
            return new DualListModel<>();
        } else {
            return (DualListModel<Fase>) q.getResultList();
        }
    }

    public RecursoSistemaFacade getRecursoSistemaFacade() {
        return recursoSistemaFacade;
    }

    public GrupoRecursoFacade getGrupoRecursoFacade() {
        return grupoRecursoFacade;
    }

    public boolean temBloqueioFaseParaRecurso(String caminho, Date data, UnidadeOrganizacional unidade, Exercicio exercicio) {
        String sql = " select rec.id from FASERECURSOSISTEMA frs " +
            " inner join recursosistema rec on rec.id = frs.recursosistema_id " +
            " inner join fase on fase.id = frs.fase_id " +
            " left join PeriodoFase pf on pf.fase_id = fase.id " +
            " left join PeriodoFaseUnidade pfu on pfu.periodofase_id = pf.id " +
            " inner join HIERARQUIAORGANIZACIONAL ho on pfu.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID" +
            "  and ho.TIPOHIERARQUIAORGANIZACIONAL = :tipoHierarquia" +
            " where rec.caminho = :caminho " +
            " and to_date(:data, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:data, 'dd/MM/yyyy'))" +
            " and ((pf.id is null or pf.id not in (" +
            "                              select periodo.PERIODOFASE_ID from PeriodoFaseUnidade periodo " +
            "                              inner join HIERARQUIAORGANIZACIONAL vw on periodo.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID\n" +
            "                              and vw.TIPOHIERARQUIAORGANIZACIONAL = :tipoHierarquia" +
            "                              where to_date(:data, 'dd/MM/yyyy') > trunc(periodo.iniciovigencia) and to_date(:data, 'dd/MM/yyyy') <= trunc(periodo.fimvigencia) " +
            "                              and to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))" +
            "                              and periodo.PERIODOFASE_ID = pfu.PERIODOFASE_ID " +
            "                              and periodo.unidadeOrganizacional_id = pfu.unidadeOrganizacional_id  )) ";
        if (unidade != null) {
            sql += " and pfu.unidadeOrganizacional_id = :unidade ";
        }
        sql += " ) ";
        if (exercicio != null) {
            sql += " and pf.exercicio_id = :idExercicio ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("caminho", caminho);
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        if (unidade != null) {
            q.setParameter("unidade", unidade.getId());
        }
        if (exercicio != null) {
            q.setParameter("idExercicio", exercicio.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public boolean existePeriodoFaseVigenteNaDataDeFechamento(String caminho, Date data, UnidadeOrganizacional unidade) {
        String sql = "select pf.* " +
            "   from FASERECURSOSISTEMA frs " +
            " inner join recursosistema rec on rec.id = frs.recursosistema_id " +
            " inner join fase on fase.id = frs.fase_id " +
            " inner join PeriodoFase pf on pf.fase_id = fase.id " +
            " inner join PeriodoFaseUnidade pfu on pfu.periodofase_id = pf.id " +
            " where rec.caminho = :caminho " +
            " and to_date(:data, 'dd/mm/yyyy') between trunc(pfu.iniciovigencia + 1) and trunc(pfu.fimvigencia) " +
            " and pfu.unidadeOrganizacional_id = :unidade";

        Query q = em.createNativeQuery(sql);
        q.setParameter("caminho", caminho);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("unidade", unidade.getId());

        return !q.getResultList().isEmpty();
    }

    public Fase faseDoRecurso(String caminho, Date data, UnidadeOrganizacional unidade) {
        try {
            String sql = "select DISTINCT fase.* " +
                "   from FASERECURSOSISTEMA frs " +
                "inner join recursosistema rec on rec.id = frs.recursosistema_id " +
                "inner join fase on fase.id = frs.fase_id " +
                "left join PeriodoFase pf on pf.fase_id = fase.id " +
                "left join PeriodoFaseUnidade pfu on pfu.periodofase_id = pf.id " +
                "where rec.caminho = :caminho " +
                "and ((pf.id is null or to_date(:data, 'dd/mm/yyyy') not between trunc(pfu.iniciovigencia) and trunc(pfu.fimvigencia)) and pfu.unidadeOrganizacional_id = :unidade)";

            Query q = em.createNativeQuery(sql, Fase.class);
            q.setParameter("caminho", caminho);
            q.setParameter("data", DataUtil.getDataFormatada(data));
            q.setParameter("unidade", unidade.getId());
            q.setMaxResults(1);
            return (Fase) q.getSingleResult();
        } catch (NoResultException no) {
            return null;
        }
    }

    public Fase buscarFase(String caminho, UnidadeOrganizacional unidadeOrganizacional, Date... datas) {
        for (Date data : datas) {
            if (data != null) {
                return faseDoRecurso(caminho, data, unidadeOrganizacional);
            }
        }
        return null;
    }
}
