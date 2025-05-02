package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametroSaud;
import br.com.webpublico.entidades.ParametroSaudDocumento;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroSaudFacade extends AbstractFacade<ParametroSaud> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public ParametroSaudFacade() {
        super(ParametroSaud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public ParametroSaud recuperar(Object id) {
        ParametroSaud parametroSaud = super.recuperar(id);
        if (parametroSaud == null) return null;
        Hibernate.initialize(parametroSaud.getDocumentos());
        return parametroSaud;
    }

    public synchronized ParametroSaud recuperarUltimo() {
        Query q = em.createQuery("from ParametroSaud order by dataRegistro desc ");
        List<ParametroSaud> parametros = q.getResultList();
        if (!parametros.isEmpty()) {
            ParametroSaud parametroSaud = parametros.stream().findFirst().get();
            Hibernate.initialize(parametroSaud.getDocumentos());
            return parametroSaud;
        }
        return null;
    }

    public ParametroSaudDocumento recuperarParametroSaudDocumento(Long id) {
        return em.find(ParametroSaudDocumento.class, id);
    }
}
