package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrauEscolaridadeSiprev;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class GrauEscolaridadeSiprevFacade extends AbstractFacade<GrauEscolaridadeSiprev> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrauEscolaridadeSiprevFacade() {
        super(GrauEscolaridadeSiprev.class);
    }

    public boolean validaCodigo(GrauEscolaridadeSiprev grauEscolaridadeSiprev) {
        Query q = this.em.createQuery("from GrauEscolaridadeSiprev grau where grau.codigo = :codigo");
        q.setParameter("codigo", grauEscolaridadeSiprev.getCodigo());
        List<GrauEscolaridadeSiprev> lista = new ArrayList<>();
        lista = q.getResultList();
        if (lista.contains(grauEscolaridadeSiprev)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }


}
