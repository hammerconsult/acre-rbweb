package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigTipoViagemContaDespesa;
import br.com.webpublico.enums.TipoViagem;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 11/09/14
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfigTipoViagemContaDespesaFacade extends AbstractFacade<ConfigTipoViagemContaDespesa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaDespesaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigTipoViagemContaDespesaFacade() {
        super(ConfigTipoViagemContaDespesa.class);
    }

    @Override
    public ConfigTipoViagemContaDespesa recuperar(Object id) {
        ConfigTipoViagemContaDespesa configTipoViagemContaDespesa = super.recuperar(id);
        configTipoViagemContaDespesa.getListaContaDespesa().size();
        return configTipoViagemContaDespesa;
    }

    public ConfigTipoViagemContaDespesa recuperaConfiguracaoVigente(TipoViagem tipoViagem, Date date) {
        String sql = " SELECT CONFIG.* FROM TIPOVIAGEMCONTADESPESA TV " +
                " INNER JOIN CONFIGTIPOVIAGEMCONTADESP CONFIG ON CONFIG.ID = TV.CONFIGTIPOVIAGEMCONTADESP_ID " +
                " WHERE TV.TIPOVIAGEM = :tipo "+
                " AND TO_DATE(:DATA,'dd/MM/yyyy') BETWEEN trunc(CONFIG.INICIOVIGENCIA) AND COALESCE(trunc(CONFIG.FIMVIGENCIA), TO_DATE(:DATA,'dd/MM/yyyy'))";
        Query consulta = em.createNativeQuery(sql, ConfigTipoViagemContaDespesa.class);
        consulta.setMaxResults(1);
        consulta.setParameter("DATA", DataUtil.getDataFormatada(date));
        consulta.setParameter("tipo", tipoViagem.name());
        try {
            ConfigTipoViagemContaDespesa retorno = (ConfigTipoViagemContaDespesa) consulta.getSingleResult();
            if (retorno != null) {
                retorno.getListaContaDespesa().size();
                return retorno;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void validarConfiguracaoVigente(ConfigTipoViagemContaDespesa config, TipoViagem tipoViagem) {
        String sql = " SELECT C.* FROM TIPOVIAGEMCONTADESPESA T " +
                " INNER JOIN CONFIGTIPOVIAGEMCONTADESP C ON C.ID = T.CONFIGTIPOVIAGEMCONTADESP_ID " +
                " WHERE T.TIPOVIAGEM = '" + tipoViagem.name() + "'" +
                " AND TO_DATE(:DATA,'dd/MM/yyyy') BETWEEN trunc(C.INICIOVIGENCIA) AND COALESCE(trunc(C.FIMVIGENCIA), TO_DATE(:DATA,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " and c.id <> :id";
        }
        Query consulta = em.createNativeQuery(sql, ConfigTipoViagemContaDespesa.class);
        consulta.setMaxResults(1);
        consulta.setParameter("DATA", DataUtil.getDataFormatada(config.getInicioVigencia()));
        if (config.getId() != null) {
            consulta.setParameter("id", config.getId());
        }
        try {
            ConfigTipoViagemContaDespesa retorno = (ConfigTipoViagemContaDespesa) consulta.getSingleResult();
            if (retorno != null) {
                throw new ExcecaoNegocioGenerica(" Já existe uma configuração vigente utilizando o Tipo de Viagem: <b>" + tipoViagem + "</b> com início de vigência em <b>" + DataUtil.getDataFormatada(retorno.getInicioVigencia()) + "</b>.");
            }
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        } catch (Exception e) {

        }
    }

    public ContaFacade getContaDespesaFacade() {
        return contaDespesaFacade;
    }
}
