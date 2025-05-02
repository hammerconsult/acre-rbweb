package br.com.webpublico.singletons;

import br.com.webpublico.entidades.comum.PessoaFisicaRFB;
import br.com.webpublico.negocios.comum.PessoaFisicaRFBFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcPessoaFisicaRFBDAO;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 240000)
public class SingletonPessoaFisicaRFB implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(SingletonPessoaFisicaRFB.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private JdbcPessoaFisicaRFBDAO jdbcPessoaFisicaRFBDAO;

    @PostConstruct
    private void init() {
        jdbcPessoaFisicaRFBDAO = (JdbcPessoaFisicaRFBDAO) Util.getSpringBeanPeloNome("pessoaFisicaRFBDAO");
    }

    @Lock(LockType.WRITE)
    public List<PessoaFisicaRFB> getProximasPessoasAtualizar() {
        List<PessoaFisicaRFB> proximas = em.createQuery("from PessoaFisicaRFB " +
                " where situacao in (:situacoes) ")
            .setParameter("situacoes", Lists.newArrayList(PessoaFisicaRFB.Situacao.AGUARDANDO, PessoaFisicaRFB.Situacao.ATUALIZANDO))
            .setMaxResults(50)
            .getResultList();
        for (PessoaFisicaRFB pessoaFisicaRFB : proximas) {
            pessoaFisicaRFB.setSituacao(PessoaFisicaRFB.Situacao.ATUALIZANDO);
        }
        jdbcPessoaFisicaRFBDAO.update(proximas);
        return proximas;
    }

}
