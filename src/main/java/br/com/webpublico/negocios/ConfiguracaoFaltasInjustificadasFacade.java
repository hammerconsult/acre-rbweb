/*
 * Codigo gerado automaticamente em Wed Mar 07 16:44:51 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoFaltasInjustificadas;
import br.com.webpublico.exception.rh.SemConfiguracaoDeFaltaInjustificada;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class ConfiguracaoFaltasInjustificadasFacade extends AbstractFacade<ConfiguracaoFaltasInjustificadas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoFaltasInjustificadasFacade() {
        super(ConfiguracaoFaltasInjustificadas.class);
    }

    public List<ConfiguracaoFaltasInjustificadas> buscarConfiguracoesFaltasInjustificadasOrdenadasDecrescentementePorInicioVigencia() {
        String sql = "select c.* from configfaltainjustificadas c order by c.inicioVigencia desc";
        Query q = em.createNativeQuery(sql, ConfiguracaoFaltasInjustificadas.class);
        return q.getResultList();
    }

    public ConfiguracaoFaltasInjustificadas buscarConfiguracaoFaltasInjustificadasPorOrgaoAndDataOperacao(String codigo2NivelOrgao, Date dataOperacao) {
        String sql = "SELECT cfi.* "
            + "FROM ConfigFaltaInjustificadas cfi "
            + "  INNER JOIN UnidadeOrganizacional uo ON "
            + "    uo.id = cfi.unidadeOrganizacional_ID "
            + "  INNER JOIN VwHierarquiaAdministrativa vw ON "
            + "    vw.subordinada_ID = uo.id "
            + "WHERE :dataOperacao BETWEEN cfi.inicioVigencia AND COALESCE(cfi.finalVigencia, :dataOperacao) "
            + "  AND :dataOperacao BETWEEN vw.inicioVigencia AND COALESCE(vw.fimVigencia, :dataOperacao) "
            + "  AND substr(vw.codigo, 4, 2) = :codOrgao ";

        Query q = em.createNativeQuery(sql, ConfiguracaoFaltasInjustificadas.class);
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("codOrgao", codigo2NivelOrgao);

        try {
            return (ConfiguracaoFaltasInjustificadas) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public void salvar(ConfiguracaoFaltasInjustificadas entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(ConfiguracaoFaltasInjustificadas entity) {
        super.salvarNovo(entity);
    }


    public List<ConfiguracaoFaltasInjustificadas> buscarConfiguracoesFaltasInjustificadasVigentes(Date dataVigencia) throws SemConfiguracaoDeFaltaInjustificada {
        Query q = em.createQuery("select config from ConfiguracaoFaltasInjustificadas config where :data between config.inicioVigencia and coalesce(config.finalVigencia, :data)");
        q.setParameter("data", dataVigencia);

        if (q.getResultList().isEmpty()) {
            throw new SemConfiguracaoDeFaltaInjustificada("Nenhuma configuração de faltas injustificadas vigente encontrada em " + DataUtil.getDataFormatada(dataVigencia));
        }
        return q.getResultList();
    }
}
