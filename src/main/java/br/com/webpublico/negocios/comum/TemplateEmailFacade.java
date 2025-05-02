package br.com.webpublico.negocios.comum;

import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.entidades.comum.TemplateEmail;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.*;

@Stateless
public class TemplateEmailFacade extends AbstractFacade<TemplateEmail> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TemplateEmailFacade() {
        super(TemplateEmail.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TemplateEmail findByTipoTemplateEmail(TipoTemplateEmail tipoTemplateEmail) {
        try {
            return em.createQuery("from TemplateEmail t where t.tipo = :tipo ", TemplateEmail.class)
                .setParameter("tipo", tipoTemplateEmail)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void preSave(TemplateEmail entidade) {
        if (hasRegistrado(entidade)) {
            throw new ValidacaoException("Tipo de template " + entidade.getTipo().getDescricao() + " já registrado. ");
        }
    }

    private boolean hasRegistrado(TemplateEmail entidade) {
        String hql = "from TemplateEmail t where t.tipo = :tipo ";
        if (entidade.getId() != null) {
            hql += " and id != :id ";
        }
        Query query = em.createQuery(hql);
        query.setParameter("tipo", entidade.getTipo());
        if (entidade.getId() != null) {
            query.setParameter("id", entidade.getId());
        }
        return query.getResultList() != null && !query.getResultList().isEmpty();
    }
}
