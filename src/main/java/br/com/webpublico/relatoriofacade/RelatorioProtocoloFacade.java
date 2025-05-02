/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.negocios.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Usuario
 */
@Stateless
public class RelatorioProtocoloFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ProcessoFacade processoFacade;
    @EJB
    private SubAssuntoFacade subAssuntoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ProcessoFacade getProcessoFacade() {
        return processoFacade;
    }

    public SubAssuntoFacade getSubAssuntoFacade() {
        return subAssuntoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List consultaClasses(Class classe) {
        String hql = "from " + classe.getSimpleName();
        Query q = em.createQuery(hql);
        return q.getResultList();
    }


}
