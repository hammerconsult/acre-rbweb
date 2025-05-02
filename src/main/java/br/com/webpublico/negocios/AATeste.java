/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.UsuarioSistema;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Munif
 */
@Stateless
@DeclareRoles(value = {"ADMIN", "CONT"})
public class AATeste {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    //@RolesAllowed(value = {"ADMIN"})
    public List<UsuarioSistema> listaTodosUsuarios() {
        Query q = em.createQuery("from UsuarioSistema");
        return q.getResultList();
    }

    @RolesAllowed(value = {"CONT", "ADMIN"})
    public String getLoginCorrente() {
        return sistemaFacade.getLogin();
    }
}
