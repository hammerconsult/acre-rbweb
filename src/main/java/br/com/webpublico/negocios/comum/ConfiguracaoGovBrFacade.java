package br.com.webpublico.negocios.comum;

import br.com.webpublico.GenerateCode;
import br.com.webpublico.entidades.ConfiguracaoGovBr;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ConfiguracaoGovBrFacade extends AbstractFacade<ConfiguracaoGovBr> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConfiguracaoGovBrFacade() {
        super(ConfiguracaoGovBr.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void preSave(ConfiguracaoGovBr entidade) {
        entidade.validarCamposObrigatorios();
        if (hasRegistroExistente(entidade)) {
            throw new ValidacaoException("Já existe uma configuração registrada para o sistema " + entidade.getSistema() +
                " no ambiente " + entidade.getAmbiente());
        }
        if (entidade.getId() == null) {
            entidade.setCodeVerifier(GenerateCode.generateCodeVerifier());
        }
    }

    private boolean hasRegistroExistente(ConfiguracaoGovBr configuracaoGovBr) {
        String sql = " select 1 from configuracaogovbr " +
            " where sistema = :sistema " +
            "   and ambiente = :ambiente ";
        if (configuracaoGovBr.getId() != null) {
            sql += " and id != :id ";
        }
        Query query = em.createNativeQuery(sql);
        query.setParameter("sistema", configuracaoGovBr.getSistema().name());
        query.setParameter("ambiente", configuracaoGovBr.getAmbiente().name());
        if (configuracaoGovBr.getId() != null) {
            query.setParameter("id", configuracaoGovBr.getId());
        }
        return !query.getResultList().isEmpty();
    }
}
