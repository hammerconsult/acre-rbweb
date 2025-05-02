package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ProdutoFeira;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ProdutoFeiraFacade extends AbstractFacade<ProdutoFeira> {
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProdutoFeiraFacade() {
        super(ProdutoFeira.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(ProdutoFeira entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(ProdutoFeira.class, "codigo"));
        }
        super.salvarNovo(entity);
    }

}
