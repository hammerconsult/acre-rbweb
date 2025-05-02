/*
 * Codigo gerado automaticamente em Fri Feb 11 13:51:59 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.UsuarioAlvaraSeker;
import br.com.webpublico.entidades.UsuarioSistema;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class UsuarioAlvaraSekerFacade extends AbstractFacade<UsuarioAlvaraSeker> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public UsuarioAlvaraSekerFacade() {
        super(UsuarioAlvaraSeker.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public List<UsuarioAlvaraSeker> buscarUsuariosSeker() {
        return em.createNativeQuery("select * from USUARIOALVARASEKER", UsuarioAlvaraSeker.class).getResultList();
    }

    public UsuarioSistema buscarUsuariosWp(String cpf) {
        Query q = em.createNativeQuery("select us.* from usuariosistema us inner join pessoafisica pf on us.PESSOAFISICA_ID = pf.ID where REPLACE(REPLACE(pf.cpf, '-'), '.') = :cpf", UsuarioSistema.class);
        q.setParameter("cpf", cpf == null ? "02272791743": cpf.replace(".", "").replace("-", ""));
        List<UsuarioSistema> resultado = q.getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
    }
}
