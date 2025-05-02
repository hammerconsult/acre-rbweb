/*
 * Codigo gerado automaticamente em Wed Mar 14 18:54:58 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClasseDiaria;
import br.com.webpublico.entidades.ConfiguracaoDiaria;
import br.com.webpublico.entidades.PropostaConcessaoDiaria;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Stateless
public class ConfiguracaoDiariaFacade extends AbstractFacade<ConfiguracaoDiaria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtoLegalFacade atoLegalFacade;

    public ConfiguracaoDiariaFacade() {
        super(ConfiguracaoDiaria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConfiguracaoDiaria recuperar(Object id) {
        ConfiguracaoDiaria cd = em.find(ConfiguracaoDiaria.class, id);
        cd.getClasseDiarias().size();
        return cd;
    }

    @Override
    public ConfiguracaoDiaria recarregar(ConfiguracaoDiaria entity) {

        Query q = getEntityManager().createQuery("from ConfiguracaoDiaria c left join fetch c.classeDiarias where c=:param ");
        q.setParameter("param", entity);
        entity = (ConfiguracaoDiaria) q.getSingleResult();
        return entity;
    }

    @Override
    public List<ConfiguracaoDiaria> lista() {
        List<ConfiguracaoDiaria> lista = em.createQuery("from ConfiguracaoDiaria").getResultList();
        for (ConfiguracaoDiaria c : lista) {
            c.getClasseDiarias().size();
        }
        return lista;

    }

    public boolean verificaConfiguracaoExistente(ConfiguracaoDiaria config) {
        String sql = "SELECT * FROM CONFIGURACAODIARIA WHERE (INICIOVIGENCIA <= :data AND (FIMVIGENCIA >= :data OR FIMVIGENCIA IS null))";
        if (config.getId() != null) {
            sql += " AND ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfiguracaoDiaria.class);
        q.setParameter("data", config.getInicioVigencia(), TemporalType.DATE);

        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (!q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public List<ClasseDiaria> listaClassDiariaConfiguracaoAtiva(PropostaConcessaoDiaria diaria) {
        String sql = "SELECT CLA.* FROM CONFIGURACAODIARIA CONF "
                + " INNER JOIN CLASSEDIARIA cla ON conf.id = cla.CONFIGURACAODIARIA_ID"
                + " WHERE (CONF.INICIOVIGENCIA <= :data AND (CONF.FIMVIGENCIA >= :data OR CONF.FIMVIGENCIA IS null))";
        Query q = getEntityManager().createNativeQuery(sql, ClasseDiaria.class);
        q.setParameter("data", diaria.getDataLancamento(), TemporalType.DATE);
        return q.getResultList();
    }

    public List<ClasseDiaria> listaClassDiariaConfiguracaoAtivaPorConfiguracao(Date data, ConfiguracaoDiaria config) {
        String sql = "SELECT CLA.* FROM CONFIGURACAODIARIA CONF "
                + " INNER JOIN CLASSEDIARIA cla ON conf.id = cla.CONFIGURACAODIARIA_ID"
                + " WHERE (CONF.INICIOVIGENCIA <= to_date(:data,'dd/mm/yyyy') " +
                "  AND (CONF.FIMVIGENCIA >= to_date(:data,'dd/mm/yyyy') " +
                "  OR CONF.FIMVIGENCIA IS null))" +
                " and conf.id = :idConfi";
        Query q = getEntityManager().createNativeQuery(sql, ClasseDiaria.class);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("idConfi", config.getId());
        return q.getResultList();
    }

    public List<ConfiguracaoDiaria> listaConfiguracaoDiaria(Date data) {
        String sql = "SELECT CONF.* FROM CONFIGURACAODIARIA CONF "
                + " WHERE (CONF.INICIOVIGENCIA <= to_date(:data,'dd/mm/yyyy') AND (CONF.FIMVIGENCIA >= to_date(:data,'dd/mm/yyyy') OR CONF.FIMVIGENCIA IS null))";
        Query q = getEntityManager().createNativeQuery(sql, ConfiguracaoDiaria.class);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        return q.getResultList();
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }
}
