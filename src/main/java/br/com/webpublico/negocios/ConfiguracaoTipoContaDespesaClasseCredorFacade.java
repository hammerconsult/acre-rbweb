package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoTipoContaDespesaClasseCredor;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 16/05/14
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfiguracaoTipoContaDespesaClasseCredorFacade extends AbstractFacade<ConfiguracaoTipoContaDespesaClasseCredor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ClasseCredorFacade classeCredorFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoTipoContaDespesaClasseCredorFacade() {
        super(ConfiguracaoTipoContaDespesaClasseCredor.class);
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    @Override
    public ConfiguracaoTipoContaDespesaClasseCredor recuperar(Object id) {
        ConfiguracaoTipoContaDespesaClasseCredor configuracaoTipoContaDespesaClasseCredor = super.recuperar(id);
        configuracaoTipoContaDespesaClasseCredor.getListaDeClasseCredor().size();
        return configuracaoTipoContaDespesaClasseCredor;
    }

    public ConfiguracaoTipoContaDespesaClasseCredor buscarConfiguracaoVigente(TipoContaDespesa tipoContaDespesa, SubTipoDespesa subTipoDespesa, Date date) {
        String sql = "select config.* from CONFTIPOCONTACLASSECREDOR config " +
            " where config.tipocontadespesa = :tipoConta " +
            " and config.subTipoDespesa = :subTipoDespesa " +
            " and to_date(:data,'dd/mm/yyyy') between trunc(config.inicioVigencia) and coalesce(trunc(config.fimVigencia), to_date(:data,'dd/mm/yyyy'))";
        Query consulta = em.createNativeQuery(sql, ConfiguracaoTipoContaDespesaClasseCredor.class);
        consulta.setMaxResults(1);
        consulta.setParameter("data", DataUtil.getDataFormatada(date));
        consulta.setParameter("tipoConta", tipoContaDespesa.name());
        consulta.setParameter("subTipoDespesa", subTipoDespesa.name());
        try {

            ConfiguracaoTipoContaDespesaClasseCredor retorno = (ConfiguracaoTipoContaDespesaClasseCredor) consulta.getSingleResult();
            if (retorno != null) {
                retorno.getListaDeClasseCredor().size();
                return retorno;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean hasConfiguracaoVigente(ConfiguracaoTipoContaDespesaClasseCredor configuracaoTipoContaDespesaClasseCredor) {
        String sql = "select config.* from CONFTIPOCONTACLASSECREDOR config " +
            " where config.tipocontadespesa = :tipoConta " +
            " and config.subTipoDespesa = :subTipoDespesa " +
            " and to_date(:data,'dd/mm/yyyy') between trunc(config.inicioVigencia) and coalesce(trunc(config.fimVigencia), to_date(:data,'dd/mm/yyyy')) ";
        if (configuracaoTipoContaDespesaClasseCredor.getId() != null) {
            sql += " and config.id <> :id";
        }
        Query consulta = em.createNativeQuery(sql, ConfiguracaoTipoContaDespesaClasseCredor.class);
        consulta.setMaxResults(1);
        consulta.setParameter("data", DataUtil.getDataFormatada(configuracaoTipoContaDespesaClasseCredor.getInicioVigencia()));
        consulta.setParameter("tipoConta", configuracaoTipoContaDespesaClasseCredor.getTipoContaDespesa().name());
        consulta.setParameter("subTipoDespesa", configuracaoTipoContaDespesaClasseCredor.getSubTipoDespesa().name());
        if (configuracaoTipoContaDespesaClasseCredor.getId() != null) {
            consulta.setParameter("id", configuracaoTipoContaDespesaClasseCredor.getId());
        }
        return !consulta.getResultList().isEmpty();
    }
}
