/*
 * Codigo gerado automaticamente em Mon Feb 14 08:59:15 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContaCorrenteBancaria;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ContaCorrenteFacade extends AbstractFacade<ContaCorrenteBancaria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContaCorrenteFacade() {
        super(ContaCorrenteBancaria.class);
    }

    public List<ContaCorrenteBancaria> listaFiltrandoConstasPorPessoa(String s, PessoaFisica pessoa) {
//        List<PessoaFisica> pessoas = new ArrayList<PessoaFisica>();
//        pessoas.add(pessoa);
        Pessoa p = pessoa;
        String hql = "from ContaCorrenteBancaria obj where ";
        hql += ":pessoa in elements(obj.pessoas) and lower(obj.numeroConta) like :filtro OR lower(obj.digitoVerificador) like :filtro ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("pessoa", p);
        q.setMaxResults(50);
        return q.getResultList();
    }
}
