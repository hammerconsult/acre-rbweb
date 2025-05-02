package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.tributario.AssuntoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.CategoriaAssuntoLicenciamentoAmbiental;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AssuntoLicenciamentoAmbientalFacade extends AbstractFacade<AssuntoLicenciamentoAmbiental> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AssuntoLicenciamentoAmbientalFacade() {
        super(AssuntoLicenciamentoAmbiental.class);
    }

    @Override
    public AssuntoLicenciamentoAmbiental recuperar(Object id) {
        AssuntoLicenciamentoAmbiental assunto = super.recuperar(id);
        Hibernate.initialize(assunto.getCategorias());
        Hibernate.initialize(assunto.getSituacoesEmissaoDebito());
        Hibernate.initialize(assunto.getSituacoesEmissaoLicenca());
        return assunto;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<AssuntoLicenciamentoAmbiental> buscarFiltrando(String parte) {
        Query q = em.createNativeQuery("select * from assuntolicenciamentoambiental where descricao like :parte", AssuntoLicenciamentoAmbiental.class);
        q.setParameter("parte", "%" + parte + "%");
        return (List<AssuntoLicenciamentoAmbiental>) q.getResultList();
    }

    public void salvarCategoria(CategoriaAssuntoLicenciamentoAmbiental categoria) {
        em.merge(categoria);
    }

    @Override
    public void preSave(AssuntoLicenciamentoAmbiental entidade) {
        entidade.realizarValidacoes();
    }
}
