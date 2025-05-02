package br.com.webpublico.negocios.comum;

import br.com.webpublico.entidades.comum.PessoaFisicaRFB;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcPessoaFisicaRFBDAO;
import br.com.webpublico.util.Util;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PessoaFisicaRFBFacade extends AbstractFacade<PessoaFisicaRFB> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public PessoaFisicaRFBFacade() {
        super(PessoaFisicaRFB.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFisicaRFB buscarPessoaFisicaRFBPorCpf(String cpf) {
        Query query = em.createNativeQuery("select * " +
                "   from PessoaFisicaRFB " +
                " where regexp_replace(cpf, '[^0-9]') = regexp_replace(:cpf, '[^0-9]') ", PessoaFisicaRFB.class)
            .setParameter("cpf", cpf)
            .setMaxResults(1);
        List resultList = query.getResultList();
        return !resultList.isEmpty() ? (PessoaFisicaRFB) resultList.get(0) : null;
    }

    public int countNaoAtualizadas() {
        Query query = em.createNativeQuery(" select count(1) from PessoaFisicaRFB " +
                " where situacao = :situacao ")
            .setParameter("situacao", PessoaFisicaRFB.Situacao.AGUARDANDO.name());
        return ((Number) query.getSingleResult()).intValue();
    }

}
