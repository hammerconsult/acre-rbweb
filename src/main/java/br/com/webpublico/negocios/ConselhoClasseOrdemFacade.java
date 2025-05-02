/*
 * Codigo gerado automaticamente em Wed Dec 07 15:56:19 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConselhoClasseContratoFP;
import br.com.webpublico.entidades.ConselhoClasseOrdem;
import br.com.webpublico.entidades.PessoaFisica;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConselhoClasseOrdemFacade extends AbstractFacade<ConselhoClasseOrdem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConselhoClasseOrdemFacade() {
        super(ConselhoClasseOrdem.class);
    }

    public ConselhoClasseContratoFP recuperaCreaPessoa(PessoaFisica pessoa) {
        String hql = "select cccfp from ConselhoClasseContratoFP cccfp "
                + " inner join cccfp.pessoaFisica pf "
                + " inner join cccfp.conselhoClasseOrdem cco "
                + " where pf = :pessoa "
                + " and cco.sigla = 'CREA' ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);

        List<ConselhoClasseContratoFP> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new ConselhoClasseContratoFP();
        }
        return lista.get(0);
    }
}
