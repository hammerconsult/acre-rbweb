package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParametroSimplesNacional;
import com.beust.jcommander.internal.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroSimplesNacionalFacade extends AbstractFacade<ParametroSimplesNacional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public ParametroSimplesNacionalFacade() {
        super(ParametroSimplesNacional.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParametroSimplesNacional recuperar(Object id) {
        ParametroSimplesNacional parametro = em.find(ParametroSimplesNacional.class, id);
        Hibernate.initialize(parametro.getDividas());
        return parametro;
    }

    public List<Divida> buscarDividas(String parte) {
        String sql = " select div.* from divida div " +
            " where lower((div.codigo || div.descricao)) like :parte " +
            " order by div.codigo ";

        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");

        List<Divida> dividas = q.getResultList();

        if(dividas != null && !dividas.isEmpty()) {
            return dividas;
        }
        return Lists.newArrayList();
    }

    public boolean hasParametroNoExercicio(ParametroSimplesNacional parametro) {
        String sql = " select param.* from parametrosimplesnacional param " +
            " where param.exercicio_id = :idExercicio " +
            (parametro.getId() != null ? " and param.id <> :idParametro " : "");

        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", parametro.getExercicio().getId());
        if(parametro.getId() != null) {
            q.setParameter("idParametro", parametro.getId());
        }

        return !q.getResultList().isEmpty();
    }

    public ParametroSimplesNacional buscarParametroSimplesNacionalPorExercicio(Exercicio exercicio) {

        String sql = " select param.* from parametrosimplesnacional param " +
            " where param.exercicio_id = :idExercicio ";

        Query q = em.createNativeQuery(sql, ParametroSimplesNacional.class);
        q.setParameter("idExercicio", exercicio);

        List<ParametroSimplesNacional> parametros = q.getResultList();
        ParametroSimplesNacional parametro = null;

        if(parametros != null && !parametros.isEmpty()) {
            parametro = parametros.get(0);
            Hibernate.initialize(parametro.getDividas());
        }
        return parametro;
    }
}
