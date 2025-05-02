package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoTributoBI;
import br.com.webpublico.entidades.Tributo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConfiguracaoTributoBIFacade extends AbstractFacade<ConfiguracaoTributoBI> {

    @EJB
    private TributoFacade tributoFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConfiguracaoTributoBIFacade() {
        super(ConfiguracaoTributoBI.class);
    }

    @Override
    public ConfiguracaoTributoBI recuperar(Object id) {
        return super.recuperar(id);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Tributo> buscarTributosPelaDescricao(String parte) {
        return tributoFacade.listaFiltrando(parte.trim().toLowerCase(), "codigo", "descricao");
    }

    public ConfiguracaoTributoBI salvarConfiguracao(ConfiguracaoTributoBI configuracaoTributoBI) {
        return em.merge(configuracaoTributoBI);
    }

    public ConfiguracaoTributoBI buscarConfiguracaoPorTipoAndTributo(ConfiguracaoTributoBI configuracaoTributoBI) {
        String sql = " select conf.* from configuracaotributobi conf " +
            " where conf.tipotributobi = :tipoTributo " +
            " and conf.tributo_id = :idTributo ";

        Query q = em.createNativeQuery(sql, ConfiguracaoTributoBI.class);
        q.setParameter("tipoTributo", configuracaoTributoBI.getTipoTributoBI().name());
        q.setParameter("idTributo", configuracaoTributoBI.getTributo().getId());

        List<ConfiguracaoTributoBI> configuracoes = q.getResultList();
        return (configuracoes != null && !configuracoes.isEmpty()) ? configuracoes.get(0) : null;
    }


}
