package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.ConfiguracaoDivida;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConfiguracaoDividaFacade extends AbstractFacade<ConfiguracaoDivida> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private DividaFacade dividaFacade;

    public ConfiguracaoDividaFacade() {
        super(ConfiguracaoDivida.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    @Override
    public void preSave(ConfiguracaoDivida entidade) {
        super.preSave(entidade);
        if (hasConfiguracaoDivida(entidade)) {
            throw new ValidacaoException("Já existe uma configuração registrada para " +
                entidade.getTipoCalculo().getDescricao() + " / " + entidade.getEntidade() + " / " +
                entidade.getTipoCadastroTributario().getDescricao() + ".");
        }
    }

    public boolean hasConfiguracaoDivida(ConfiguracaoDivida configuracaoDivida) {
        ConfiguracaoDivida configuracaoDividaRegistrada = getConfiguracaoDivida(configuracaoDivida.getTipoCalculo(), configuracaoDivida.getEntidade(),
            configuracaoDivida.getTipoCadastroTributario());
        return configuracaoDividaRegistrada != null && !configuracaoDividaRegistrada.getId().equals(configuracaoDivida.getId());
    }

    public ConfiguracaoDivida getConfiguracaoDivida(Calculo.TipoCalculo tipoCalculo,
                                                     Entidade entidade,
                                                     TipoCadastroTributario tipoCadastroTributario) {
        Query query = em.createNativeQuery("select cd.* from configuracaodivida cd " +
                " where cd.tipocalculo = :tipocalculo " +
                "   and cd.entidade_id = :entidade_id " +
                "   and cd.tipocadastrotributario = :tipocadastrotributario ", ConfiguracaoDivida.class)
            .setParameter("tipocalculo", tipoCalculo.name())
            .setParameter("entidade_id", entidade.getId())
            .setParameter("tipocadastrotributario", tipoCadastroTributario.name());
        List resultList = query.getResultList();
        return !resultList.isEmpty() ? (ConfiguracaoDivida) resultList.get(0) : null;
    }
}
