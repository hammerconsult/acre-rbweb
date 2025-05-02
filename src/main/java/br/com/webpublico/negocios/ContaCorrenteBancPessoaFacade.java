/*
 * Codigo gerado automaticamente em Tue Feb 22 16:08:31 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContaCorrenteBancPessoa;
import br.com.webpublico.entidades.ContaCorrenteBancaria;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.enums.SituacaoConta;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ContaCorrenteBancPessoaFacade extends AbstractFacade<ContaCorrenteBancPessoa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContaCorrenteBancPessoaFacade() {
        super(ContaCorrenteBancPessoa.class);
    }

    @Override
    public ContaCorrenteBancPessoa recuperar(Object id) {
        ContaCorrenteBancPessoa cc = em.find(ContaCorrenteBancPessoa.class, id);
        return cc;
    }

    public List<ContaCorrenteBancaria> listaContasPorPessoaFisica(PessoaFisica pessoa) {
        String hql = " select ccb.contaCorrenteBancaria from ContaCorrenteBancPessoa ccb "
            + " where ccb.pessoa = :parametroPessoa ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroPessoa", pessoa);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<ContaCorrenteBancPessoa> listaContasBancariasPorPessoa(Pessoa p, String parte) {
        String sql = "SELECT CCBP.* "
            + " FROM CONTACORRENTEBANCPESSOA CCBP"
            + " INNER JOIN CONTACORRENTEBANCARIA CCB ON CCBP.CONTACORRENTEBANCARIA_ID = CCB.ID"
            + " INNER JOIN PESSOA P ON CCBP.PESSOA_ID = P.ID"
            + " WHERE P.ID = :pessoa"
            + " AND (CCB.NUMEROCONTA  LIKE :parte)";
        Query q = em.createNativeQuery(sql, ContaCorrenteBancPessoa.class);
        q.setParameter("pessoa", p.getId());
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }

    public List<ContaCorrenteBancPessoa> listaContasBancariasPorPessoa(Pessoa p, SituacaoConta situacao) {
        String sql = "SELECT CCBP.* "
            + " FROM CONTACORRENTEBANCPESSOA CCBP"
            + " INNER JOIN CONTACORRENTEBANCARIA CCB ON CCBP.CONTACORRENTEBANCARIA_ID = CCB.ID"
            + " INNER JOIN PESSOA P ON CCBP.PESSOA_ID = P.ID"
            + " WHERE P.ID = :pessoa"
            + " AND CCB.SITUACAO = :situacao";
        Query q = em.createNativeQuery(sql, ContaCorrenteBancPessoa.class);
        q.setParameter("pessoa", p.getId());
        q.setParameter("situacao", situacao.name());
        return q.getResultList();
    }

    public List<ContaCorrenteBancaria> listaContasPorPessoaJuridica(Pessoa pessoa) {
        String hql = " select ccb.contaCorrenteBancaria from ContaCorrenteBancPessoa ccb  where ccb.pessoa = :parametroPessoa ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroPessoa", pessoa);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<Pessoa> listaPorContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        String hql = " select p from ContaCorrenteBancPessoa ccbp "
            + " inner join ccbp.pessoa p "
            + " where ccbp.contaCorrenteBancaria = :contaCorrenteBancaria ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("contaCorrenteBancaria", contaCorrenteBancaria);
        return q.getResultList();
    }

    public List<ContaCorrenteBancPessoa> buscarContasCorrenteBancPorPessoaFisica(PessoaFisica pessoa) {
        String hql = " select ccb from ContaCorrenteBancPessoa ccb "
            + " where ccb.pessoa = :parametroPessoa ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroPessoa", pessoa);
        return q.getResultList();
    }
}
