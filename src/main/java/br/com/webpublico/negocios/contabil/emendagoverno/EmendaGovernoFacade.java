package br.com.webpublico.negocios.contabil.emendagoverno;

import br.com.webpublico.entidades.contabil.emendagoverno.EmendaGoverno;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class EmendaGovernoFacade extends AbstractFacade<EmendaGoverno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EmendaGovernoFacade() {
        super(EmendaGoverno.class);
    }

    @Override
    public EmendaGoverno recuperar(Object id) {
        EmendaGoverno entity = em.find(EmendaGoverno.class, id);
        Hibernate.initialize(entity.getParlamentares());
        return entity;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void preSave(EmendaGoverno entidade) {
        ValidacaoException ve = new ValidacaoException();
        if (entidade.getParlamentares().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Deve conter um ou mais Parlamentares adicionados.");
        }
        ve.lancarException();
    }
}
