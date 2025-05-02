package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BancoObn;
import br.com.webpublico.entidades.ConfiguracaoArquivoObn;
import br.com.webpublico.entidades.ContratoObn;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by Edi on 05/04/2016.
 */
@Stateless
public class ConfiguracaoArquivoObnFacade extends AbstractFacade<ConfiguracaoArquivoObn> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;

    public ConfiguracaoArquivoObnFacade() {
        super(ConfiguracaoArquivoObn.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoArquivoObn recuperar(Object id) {
        ConfiguracaoArquivoObn config = em.find(ConfiguracaoArquivoObn.class, id);
        config.getListaBancosObn().size();
        for (BancoObn bancoObn : config.getListaBancosObn()) {
            bancoObn.getContratos().size();
            for (ContratoObn contratoObn : bancoObn.getContratos()) {
                contratoObn.getUnidades().size();
            }

        }

        return config;
    }

    public BancoObn recuperarBancoGeradorArquivo(String numeroBanco) {
        String sql = " select b.* from configuracaoarquivoobn c                     " +
            "           inner join bancoobn b on b.configuracaoarquivoobn_id = c.id " +
            "           where b.numerodobanco = :numeroBanco                        ";
        Query q = em.createNativeQuery(sql, BancoObn.class);
        q.setParameter("numeroBanco", numeroBanco.trim());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BancoObn) q.getSingleResult();
        }
        return null;
    }

    public ConfiguracaoArquivoObn recuperarConfiguracaoArquivoObn() {
        String sql = " select * from configuracaoarquivoobn ";
        Query q = em.createNativeQuery(sql, ConfiguracaoArquivoObn.class);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfiguracaoArquivoObn) q.getSingleResult();
        }
        return null;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }
}
