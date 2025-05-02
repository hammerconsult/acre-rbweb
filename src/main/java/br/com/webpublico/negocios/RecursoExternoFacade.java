package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.RecursoExterno;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.Util;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class RecursoExternoFacade extends AbstractFacade<RecursoExterno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public RecursoExternoFacade() {
        super(RecursoExterno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasRecursoCadastrado(RecursoExterno recursoExterno) {
        Query query = em.createQuery(" from RecursoExterno re " +
                " where lower(trim(re.caminho)) = lower(trim(:caminho)) " +
                "   and re.sistema = :sistema " +
                (recursoExterno.getId() != null ? " and re.id != :id " : ""))
            .setParameter("caminho", recursoExterno.getCaminho())
            .setParameter("sistema", recursoExterno.getSistema());
        if (recursoExterno.getId() != null) {
            query.setParameter("id", recursoExterno.getId());
        }
        return !query.getResultList().isEmpty();
    }

    @Override
    public void preSave(RecursoExterno entidade) {
        entidade.realizarValidacoes();
        if (hasRecursoCadastrado(entidade)) {
            throw new ValidacaoException("O recurso externo para o sistema " + entidade.getSistema() + " com o caminho " +
                entidade.getCaminho() + " já está cadastrado.");
        }
    }

    public void limparCacheSistemasExternos() {
        limparCachePortalContribuinte();
    }

    private void limparCachePortalContribuinte() {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
            "Removendo cache dos recursos do portal do contribuinte", 0);
        AsyncExecutor.getInstance().execute(assistente, () -> {
            try {
                RestTemplate restTemplate = Util.getRestTemplateWithConnectTimeout();
                restTemplate.exchange(Util.getUrlPortalContribuinte() + "/api/recurso/limpar-cache",
                    HttpMethod.GET, null, String.class);
            } catch (Exception e) {
                logger.error("Erro ao remover cache de recursos do portal do contribuinte. Erro: {}", e.getMessage());
                logger.debug("Detalhes do erro ao remover cache de recursos do portal do contribuinte.", e);
            }
            return null;
        });
    }
}
