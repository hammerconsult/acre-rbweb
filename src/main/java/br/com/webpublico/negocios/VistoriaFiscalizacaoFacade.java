/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.VistoriaFiscalizacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Heinz
 */
@Stateless
public class VistoriaFiscalizacaoFacade extends AbstractFacade<VistoriaFiscalizacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VistoriaFiscalizacaoFacade() {
        super(VistoriaFiscalizacao.class);
    }

    public List<VistoriaFiscalizacao> listaPorCadastroEconomico(CadastroEconomico ce) {
        String hql = "from VistoriaFiscalizacao v where v.cadastroEconomico = :ce";
        Query q = em.createQuery(hql);
        q.setParameter("ce", ce);
        return q.getResultList();
    }

    @Override
    public VistoriaFiscalizacao recuperar(Object id) {
        VistoriaFiscalizacao vf = em.find(VistoriaFiscalizacao.class, id);
        vf.getItens().size();
        return vf;
    }


}
