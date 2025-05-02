/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cadastro;
import br.com.webpublico.entidades.Historico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author leonardo
 */
@Stateless
public class HistoricoFacade extends AbstractFacade<Historico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistoricoFacade() {
        super(Historico.class);
    }

    public List<Historico> recuperaPorCadastro(Cadastro selecionado, Date ate) {
        if (selecionado == null || selecionado.getId() == null) {
            return new ArrayList<Historico>();
        }
        String hql = "from Historico h where h.cadastro = :cadastro and h.dataRegistro <= :ate order by h.dataRegistro desc";
        Query q = em.createQuery(hql);
        q.setParameter("cadastro", selecionado);
        q.setParameter("ate", ate);
        return q.getResultList();
    }

}
