/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoDiaria;
import br.com.webpublico.entidades.Pessoa;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Usuario
 */
@Stateless
public class GrupoDiariaFacade extends AbstractFacade<GrupoDiaria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoDiariaFacade() {
        super(GrupoDiaria.class);
    }

    public String getUltimoNumero() {
        String sql = "select max(to_number(cr.codigo))+1 as ultimoNumero from GrupoDiaria cr";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    @Override
    public GrupoDiaria recuperar(Object id) {
        GrupoDiaria gd = em.find(GrupoDiaria.class, id);
        gd.getGrupoPessoasDiarias().size();
        return gd;
    }

    public GrupoDiaria buscarGrupoDiariaPorPessoa(Pessoa pessoa) {
        String sql = " select grupo.* from GRUPOPESSOASDIARIAS GPD " +
            " inner join GRUPODIARIA GRUPO ON GRUPO.ID = GPD.GRUPODIARIA_ID " +
            " where GPD.PESSOA_ID = :pessoa ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pessoa", pessoa.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (GrupoDiaria) q.getSingleResult();
        }
        return null;

    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ContaCorrenteBancPessoaFacade getContaCorrenteBancPessoaFacade() {
        return contaCorrenteBancPessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }
}
