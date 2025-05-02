/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoUsuario;
import br.com.webpublico.seguranca.NotificacaoService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
@Stateless
public class GrupoUsuarioFacade extends AbstractFacade<GrupoUsuario> {

    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private GrupoRecursoFacade grupoRecursoFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoUsuarioFacade() {
        super(GrupoUsuario.class);
    }

    @Override
    public GrupoUsuario recuperar(Object id) {
        GrupoUsuario grupoUsuario = em.find(GrupoUsuario.class, id);
        grupoUsuario.getItens().size();
        grupoUsuario.getPeriodos().size();
        grupoUsuario.getUsuarios().size();
        grupoUsuario.getNotificacoes().size();
        return grupoUsuario;
    }

    @Override
    public void salvar(GrupoUsuario entity) {
        super.salvar(entity);
        NotificacaoService.getService().recarregarTodasNotificacoes();
    }

    @Override
    public void salvarNovo(GrupoUsuario entity) {
        super.salvarNovo(entity);
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public GrupoRecursoFacade getGrupoRecursoFacade() {
        return grupoRecursoFacade;
    }

    public List listaPeriodoGrupoUsuario(GrupoUsuario grupoUsuario) {
        String sql2 = "select * from ( "
            + " select p.diadasemana as dia, p.inicio as inicio, p.termino as termino, 1 as num "
            + " from GRUPOUSUARIOPERIODO gp "
            + " inner join periodo p on p.id = gp.periodo_id "
            + " where gp.grupousuario_id = 27487158 "
            + " ) PIVOT ( "
            + " sum(num) for dia in (1 as segunda, 2 as terca, 3 as quarta, 4 as quinta, 5 as sexta, 6 as sabado, 7 as domingo)  "
            + " )";

        String sql = "SELECT "
            + " coalesce((SELECT CASE WHEN p.diadasemana = 7 THEN 1 END AS domingo "
            + " FROM GRUPOUSUARIOPERIODO gp "
            + " INNER JOIN periodo p ON p.id = gp.periodo_id AND p.diadasemana = 7 "
            + " WHERE gp.grupousuario_id = :grupo_id "
            + "   AND p.inicio = per.inicio "
            + "   AND p.termino = per.termino),0) AS domingo, "
            + " coalesce((SELECT CASE WHEN p.diadasemana = 1 THEN 1 END AS segunda "
            + " FROM GRUPOUSUARIOPERIODO gp "
            + " INNER JOIN periodo p ON p.id = gp.periodo_id AND p.diadasemana = 1 "
            + " WHERE gp.grupousuario_id = :grupo_id "
            + "   AND p.inicio = per.inicio "
            + "   AND p.termino = per.termino),0) AS segunda, "
            + " coalesce((SELECT CASE WHEN p.diadasemana = 2 THEN 1 END AS terca "
            + " FROM GRUPOUSUARIOPERIODO gp "
            + " INNER JOIN periodo p ON p.id = gp.periodo_id AND p.diadasemana = 2 "
            + " WHERE gp.grupousuario_id = :grupo_id "
            + "   AND p.inicio = per.inicio "
            + "   AND p.termino = per.termino),0) AS terca, "
            + " coalesce((SELECT CASE WHEN p.diadasemana = 3 THEN 1 END AS quarta "
            + " FROM GRUPOUSUARIOPERIODO gp "
            + " INNER JOIN periodo p ON p.id = gp.periodo_id AND p.diadasemana = 3 "
            + " WHERE gp.grupousuario_id = :grupo_id "
            + "   AND p.inicio = per.inicio "
            + "   AND p.termino = per.termino),0) AS quarta, "
            + " coalesce((SELECT CASE WHEN p.diadasemana = 4 THEN 1 END AS quinta "
            + " FROM GRUPOUSUARIOPERIODO gp "
            + " INNER JOIN periodo p ON p.id = gp.periodo_id AND p.diadasemana = 4 "
            + " WHERE gp.grupousuario_id = :grupo_id "
            + "   AND p.inicio = per.inicio "
            + "   AND p.termino = per.termino),0) AS quinta, "
            + " coalesce((SELECT CASE WHEN p.diadasemana = 5 THEN 1 END AS sexta "
            + " FROM GRUPOUSUARIOPERIODO gp "
            + " INNER JOIN periodo p ON p.id = gp.periodo_id AND p.diadasemana = 5 "
            + " WHERE gp.grupousuario_id = :grupo_id "
            + "   AND p.inicio = per.inicio "
            + "   AND p.termino = per.termino),0) AS sexta, "
            + " coalesce((SELECT CASE WHEN p.diadasemana = 6 THEN 1 END AS sabado "
            + " FROM GRUPOUSUARIOPERIODO gp "
            + " INNER JOIN periodo p ON p.id = gp.periodo_id AND p.diadasemana = 6 "
            + " WHERE gp.grupousuario_id = :grupo_id "
            + "   AND p.inicio = per.inicio "
            + "   AND p.termino = per.termino),0) AS sabado, "
            + " per.inicio, per.termino "
            + " FROM GRUPOUSUARIOPERIODO grupo "
            + " INNER JOIN periodo per ON per.id = grupo.periodo_id "
            + " WHERE grupo.grupousuario_id = :grupo_id "
            + "   GROUP BY inicio, termino";
        Query q = em.createNativeQuery(sql);
        q.setParameter("grupo_id", grupoUsuario.getId());
        return q.getResultList();
    }

    public boolean jaExisteNomeGrupoUsuario(GrupoUsuario grupoUsuario) {
        String hql = "from GrupoUsuario where lower(nome) = :nome and id <> :id";
        Query q = em.createQuery(hql);
        q.setParameter("nome", grupoUsuario.getNome().toLowerCase());
        Long id = 0L;
        if (grupoUsuario.getId() != null) {
            id = grupoUsuario.getId();
        }
        q.setParameter("id", id);
        return !q.getResultList().isEmpty();
    }

    public List<GrupoUsuario> buscarGrupoUsuarioPorNome(String filter) {
        String sql = " SELECT gu.* FROM  grupousuario gu WHERE LOWER(gu.nome) LIKE :filter ";

        Query q = em.createNativeQuery(sql, GrupoUsuario.class);
        q.setParameter("filter", "%" + filter.trim().toLowerCase() + "%");
        q.setMaxResults(10);
        if (!q.getResultList().isEmpty())
            return q.getResultList();
        return new ArrayList<>();
    }
}
