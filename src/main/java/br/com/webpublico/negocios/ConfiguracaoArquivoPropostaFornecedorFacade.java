package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoArquivoPropostaFornecedor;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 11/03/15
 * Time: 18:00
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfiguracaoArquivoPropostaFornecedorFacade extends AbstractFacade<ConfiguracaoArquivoPropostaFornecedor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoArquivoPropostaFornecedorFacade() {
        super(ConfiguracaoArquivoPropostaFornecedor.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public PropostaFornecedorFacade getPropostaFornecedorFacade() {
        return propostaFornecedorFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void salvar(ConfiguracaoArquivoPropostaFornecedor entity) {
        try {
            em.merge(entity);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    @Override
    public void salvarNovo(ConfiguracaoArquivoPropostaFornecedor entity) {
        try {
            em.persist(entity);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public ConfiguracaoArquivoPropostaFornecedor recuperaUltimaConfiguracao() {
        String sql = "select obj.* from CONFIGARQUIVOPROPOSTAFORN obj order by obj.versao desc ";
        Query q = getEntityManager().createNativeQuery(sql, ConfiguracaoArquivoPropostaFornecedor.class);
        q.setMaxResults(1);
        try {
            ConfiguracaoArquivoPropostaFornecedor singleResult = (ConfiguracaoArquivoPropostaFornecedor) q.getSingleResult();
            singleResult.getArquivo().getPartes().size();
            singleResult.getManual().getPartes().size();
            return singleResult;
        } catch (Exception e) {
            return null;
        }
    }

    public ConfiguracaoArquivoPropostaFornecedor buscarUltimaConfiguracao() {
        String sql = "select obj.* from CONFIGARQUIVOPROPOSTAFORN obj order by obj.versao desc ";
        Query q = getEntityManager().createNativeQuery(sql, ConfiguracaoArquivoPropostaFornecedor.class);
        q.setMaxResults(1);
        try {
            ConfiguracaoArquivoPropostaFornecedor singleResult = (ConfiguracaoArquivoPropostaFornecedor) q.getSingleResult();
            Hibernate.initialize(singleResult.getArquivo().getPartes());
            Hibernate.initialize(singleResult.getManual().getPartes());
            return singleResult;
        } catch (NoResultException e) {
            throw new ValidacaoException("Não foi possível localizar configuração válida para o Arquivo de Proposta do Fornecedor," +
                " por favor realize a configuração em " +
                Util.linkBlack("/licitacao/configuracao-arquivo-proposta-fornecedor/", "Configuração do Arquivo da Proposta do Fornecedor"));
        }
    }
}
