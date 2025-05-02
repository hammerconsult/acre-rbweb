package br.com.webpublico.nfse.facades;

import br.com.webpublico.StringUtils;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.Cosif;
import br.com.webpublico.nfse.domain.dtos.Operador;
import br.com.webpublico.nfse.domain.dtos.ParametroQuery;
import br.com.webpublico.nfse.domain.dtos.ParametroQueryCampo;
import br.com.webpublico.nfse.util.GeradorQuery;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CosifFacade extends AbstractFacade<Cosif> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public CosifFacade() {
        super(Cosif.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Cosif> buscarContasCosifPorPardao(Integer padrao) {
        Query q = em.createQuery("select conta from Cosif conta where conta.padrao = :padrao");
        q.setParameter("padrao", padrao);
        return q.getResultList();
    }

    public Cosif buscarPorConta(String conta) throws Exception {
        List<ParametroQuery> parametros = Lists.newArrayList();
        parametros.add(new ParametroQuery(Lists.newArrayList(new ParametroQueryCampo("c.conta", Operador.IGUAL, conta))));
        String sql = " select c.* " +
            "   from cosif c ";
        Query query = new GeradorQuery(em, Cosif.class, sql).parametros(parametros).orderBy(" order by c.conta ").build();
        if (!query.getResultList().isEmpty()) {
            return (Cosif) query.getResultList().get(0);
        }
        return null;
    }

    @Override
    public void preSave(Cosif entidade) {
        entidade.realizarValidacoes();
        if (hasContaRegistrada(entidade)) {
            throw new ValidacaoException("A conta informada já está registrada.");
        }
        entidade.setConta(StringUtils.getApenasNumeros(entidade.getConta()));
    }

    private boolean hasContaRegistrada(Cosif cosif) {
        Query q = em.createQuery("from Cosif c " +
            " where c.versao = :versao " +
            "   and c.conta = :conta " +
            (cosif.getId() != null ? " and c.id != :id " : ""));
        q.setParameter("versao", cosif.getVersao());
        q.setParameter("conta", StringUtils.getApenasNumeros(cosif.getConta()));
        if (cosif.getId() != null) {
            q.setParameter("id", cosif.getId());
        }
        return !q.getResultList().isEmpty();
    }
}
