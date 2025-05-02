/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DocumentoPessoalFacade extends AbstractFacade<DocumentoPessoal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentoPessoalFacade() {
        super(DocumentoPessoal.class);
    }

    public CarteiraTrabalho recuperaCarteiraTrabalho(PessoaFisica pessoaFisica) {
        try {
            String hql = "select carteira from CarteiraTrabalho carteira "
                + " inner join carteira.pessoaFisica pessoa "
                + " where pessoa = :pessoa "
                + " order by carteira.dataEmissaoPisPasep desc";
            Query q = em.createQuery(hql);
            q.setParameter("pessoa", pessoaFisica);
            q.setMaxResults(1);
            return (CarteiraTrabalho) q.getSingleResult();
        } catch (NoResultException nre) {
            return new CarteiraTrabalho();
        }
    }

    public String recuperaCarteiraTrabalhoAtuarial(PessoaFisica pessoaFisica) {
        try {
            String hql = "select carteira.pisPasep from CarteiraTrabalho carteira "
                + " inner join carteira.pessoaFisica pessoa "
                + " where pessoa = :pessoa "
                + " order by carteira.dataEmissaoPisPasep desc";
            Query q = em.createQuery(hql);
            q.setParameter("pessoa", pessoaFisica);
            q.setMaxResults(1);
            return (String) q.getSingleResult();
        } catch (NoResultException nre) {
            return "";
        }
    }


    public CertidaoCasamento recuperaCertidaoCasamento(PessoaFisica pessoaFisica) {
        String hql = "select certidao from CertidaoCasamento certidao "
            + " inner join certidao.pessoaFisica pessoa "
            + " where pessoa = :pessoa "
            + " order by certidao.dataRegistro desc";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoaFisica);
        q.setMaxResults(1);

        try {
            return (CertidaoCasamento) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Habilitacao recuperaHabilitacao(PessoaFisica pessoaFisica) {
        String hql = "select habilitacao from Habilitacao habilitacao "
            + " inner join habilitacao.pessoaFisica pessoa "
            + " where pessoa = :pessoa "
            + " order by habilitacao.dataRegistro desc";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoaFisica);

        List<Habilitacao> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public RG recuperaRG(PessoaFisica pessoaFisica) {
        String hql = "select rg from RG rg "
            + " inner join rg.pessoaFisica pessoa "
            + " where pessoa = :pessoa "
            + " order by rg.dataRegistro desc";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoaFisica);

        List<RG> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public void removerRGPorPessoa(PessoaFisica pf) {
        String hql = "select rg from RG rg "
            + " inner join rg.pessoaFisica pessoa "
            + " where pessoa = :pessoa ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pf);
        List<RG> lista = q.getResultList();
        for (RG rg : lista) {
            remover(rg);
        }

    }

    public void removerTituloEletiorPorPessoa(PessoaFisica pf) {
        String hql = "select titulo from TituloEleitor titulo "
            + " inner join titulo.pessoaFisica pessoa "
            + " where pessoa = :pessoa ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pf);
        List<TituloEleitor> lista = q.getResultList();
        for (TituloEleitor rg : lista) {
            remover(rg);
        }

    }

    public void removerHabilitcaoPorPessoa(PessoaFisica pf) {
        String hql = "select docu from Habilitacao docu "
            + " inner join docu.pessoaFisica pessoa "
            + " where pessoa = :pessoa ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pf);
        List<Habilitacao> lista = q.getResultList();
        for (Habilitacao obj : lista) {
            remover(obj);
        }

    }

    public void removerCarteiraTrabalhoPorPessoa(PessoaFisica pessoaFisica) {
        String hql = "select docu from CarteiraTrabalho docu "
            + " inner join docu.pessoaFisica pessoa "
            + " where pessoa = :pessoa ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoaFisica);
        List<CarteiraTrabalho> lista = q.getResultList();
        for (CarteiraTrabalho obj : lista) {
            remover(obj);
        }
    }

    public void removerSituacaoMilitarPorPessoa(PessoaFisica pessoaFisica) {
        String hql = "select docu from SituacaoMilitar docu "
            + " inner join docu.pessoaFisica pessoa "
            + " where pessoa = :pessoa ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoaFisica);
        List<SituacaoMilitar> lista = q.getResultList();
        for (SituacaoMilitar obj : lista) {
            remover(obj);
        }
    }

    public void removerCertidaoNascimentoPorPessoa(PessoaFisica pessoaFisica) {
        String hql = "select docu from CertidaoNascimento docu "
            + " inner join docu.pessoaFisica pessoa "
            + " where pessoa = :pessoa ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoaFisica);
        List<CertidaoNascimento> lista = q.getResultList();
        for (CertidaoNascimento obj : lista) {
            remover(obj);
        }
    }

    public void removerCertidaoCasamentoPorPessoa(PessoaFisica pessoaFisica) {
        String hql = "select docu from CertidaoCasamento docu "
            + " inner join docu.pessoaFisica pessoa "
            + " where pessoa = :pessoa ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoaFisica);
        List<CertidaoCasamento> lista = q.getResultList();
        for (CertidaoCasamento obj : lista) {
            remover(obj);
        }
    }

    public boolean verificarCpf(CertidaoNascimento c) {

        String hql = "";

        if (c.getId() != null) {
            hql = "from CertidaoNascimento c where replace(replace(c.cpfCompanheiro,'.',''),'-','') = :cpf and c != :certidao ";
        } else {
            hql = "from CertidaoNascimento c where replace(replace(c.cpfCompanheiro,'.',''),'-','') = :cpf ";
        }
        Query q = em.createQuery(hql, CertidaoNascimento.class);
        q.setParameter("cpf", c.getCpfCompanheiro().replace(".", "").replaceAll("-", ""));

        if (c.getId() != null) {
            q.setParameter("certidao", c);
        }

        return q.getResultList().isEmpty();
    }
}
