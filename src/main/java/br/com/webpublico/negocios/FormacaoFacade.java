package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Formacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by AndreGustavo on 26/09/2014.
 */
@Stateless
public class FormacaoFacade extends AbstractFacade<Formacao> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public FormacaoFacade() {
        super(Formacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
