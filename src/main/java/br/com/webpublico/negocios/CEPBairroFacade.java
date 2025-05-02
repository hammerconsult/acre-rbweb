/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CEPBairro;
import br.com.webpublico.entidades.CEPLocalidade;
import br.com.webpublico.ws.model.ConsultaCEP;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Wanderley
 */
@Stateless
public class CEPBairroFacade extends AbstractFacade<CEPBairro> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CEPBairro salvarCEPBairro(CEPBairro cepBairro){
        return em.merge(cepBairro);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public CEPBairroFacade() {
        super(CEPBairro.class);
    }

    public CEPBairro buscarCEPLocalidadePeloPorLocalidadeAndUF(String nomeBairro,CEPLocalidade cepLocalidade) {

        String sql = " select cb.* from cepbairro cb " +
            " where lower(nome) = :bairro " +
            " and cb.localidade_id = :idCepLocalidade ";

        Query q = em.createNativeQuery(sql, CEPBairro.class);
        q.setParameter("bairro", nomeBairro.toLowerCase().trim());
        q.setParameter("idCepLocalidade", cepLocalidade.getId());

        if (!q.getResultList().isEmpty()) {
            return (CEPBairro) q.getResultList().get(0);
        }
        return null;
    }
}
