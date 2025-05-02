package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AprovacaoEmenda;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AprovacaoEmendaFacade extends AbstractFacade<AprovacaoEmenda> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public AprovacaoEmendaFacade() {
        super(AprovacaoEmenda.class);
    }

    @Override
    public AprovacaoEmenda recuperar(Object id) {
        AprovacaoEmenda aprovacaoEmenda = em.find(AprovacaoEmenda.class, id);
        aprovacaoEmenda.getUsuarios().size();
        return aprovacaoEmenda;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
