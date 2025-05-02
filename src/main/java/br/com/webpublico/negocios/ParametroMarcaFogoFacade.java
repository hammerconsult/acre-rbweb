package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParametroMarcaFogo;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroMarcaFogoFacade extends AbstractFacade<ParametroMarcaFogo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParametroMarcaFogoFacade() {
        super(ParametroMarcaFogo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParametroMarcaFogo recuperar(Object id) {
        ParametroMarcaFogo parametro = super.recuperar(id);
        inicializarListas(parametro);
        return parametro;
    }

    private void inicializarListas(ParametroMarcaFogo parametro) {
        if (parametro == null) return;
        Hibernate.initialize(parametro.getTaxas());
        Hibernate.initialize(parametro.getDocumentos());
        Hibernate.initialize(parametro.getCertidoes());
    }

    public ParametroMarcaFogo buscarParametroPeloExercicio(Exercicio exercicio) {
        if (exercicio == null) return null;
        String sql = "select param.* from parametromarcafogo param " +
            " where param.exercicio_id = :idExercicio";
        Query q = em.createNativeQuery(sql, ParametroMarcaFogo.class);
        q.setParameter("idExercicio", exercicio.getId());
        List<ParametroMarcaFogo> resultado = q.getResultList();
        ParametroMarcaFogo parametro = null;
        if (!resultado.isEmpty()) {
            parametro = recuperar(resultado.get(0).getId());
        }
        return parametro;
    }
}
