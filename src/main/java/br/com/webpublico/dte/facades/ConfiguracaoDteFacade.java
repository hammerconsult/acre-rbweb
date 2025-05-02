package br.com.webpublico.dte.facades;

import br.com.webpublico.dte.entidades.ConfiguracaoDte;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConfiguracaoDteFacade extends AbstractFacade<ConfiguracaoDte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConfiguracaoDteFacade() {
        super(ConfiguracaoDte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ConfiguracaoDte recuperarConfiguracao() {
        String sql = " select * from configuracaodte ";
        Query q = em.createNativeQuery(sql, ConfiguracaoDte.class);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            ConfiguracaoDte configuracaoDte = (ConfiguracaoDte) resultList.get(0);
            if (configuracaoDte.getConfiguracaoDteRelatorio() != null && configuracaoDte.getConfiguracaoDteRelatorio().getDetentorArquivoComposicao() != null)
                Hibernate.initialize(configuracaoDte.getConfiguracaoDteRelatorio().getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes());
            Hibernate.initialize(configuracaoDte.getParametros());
            return configuracaoDte;
        }
        throw new ExcecaoNegocioGenerica("Configuração da DT-e não definida.");
    }
}
