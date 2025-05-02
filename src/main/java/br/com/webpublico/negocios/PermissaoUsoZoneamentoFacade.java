package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PermissaoUsoZoneamento;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PermissaoUsoZoneamentoFacade extends AbstractFacade<PermissaoUsoZoneamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ClassificacaoUsoFacade classificacaoUsoFacade;

    public PermissaoUsoZoneamentoFacade() {
        super(PermissaoUsoZoneamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClassificacaoUsoFacade getClassificacaoUsoFacade() {
        return classificacaoUsoFacade;
    }
}
