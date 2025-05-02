/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cadastro;
import br.com.webpublico.entidades.HistoricoInscricaoCadastral;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class HistoricoInscricaoCadastralFacade extends AbstractFacade<HistoricoInscricaoCadastral> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistoricoInscricaoCadastralFacade() {
        super(HistoricoInscricaoCadastral.class);
    }

    public List<HistoricoInscricaoCadastral> buscarHistoricosPorCadastro(Cadastro cadastro) {
        return em.createQuery("from HistoricoInscricaoCadastral h " +
                " where h.cadastro = :cadastro " +
                " order by h.dataRegistro ")
            .setParameter("cadastro", cadastro)
            .getResultList();
    }
}
