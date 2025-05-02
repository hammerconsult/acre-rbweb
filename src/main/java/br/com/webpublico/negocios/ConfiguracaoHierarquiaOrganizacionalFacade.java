/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@Stateless
public class ConfiguracaoHierarquiaOrganizacionalFacade extends AbstractFacade<ConfiguracaoHierarquiaOrganizacional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConfiguracaoHierarquiaOrganizacionalFacade() {
        super(ConfiguracaoHierarquiaOrganizacional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean verificaSePresistNovo(ConfiguracaoHierarquiaOrganizacional conf) {
        //System.out.println("Verifica Presiste");
        String sql = "SELECT conf.* FROM ConfiguracaoHierarquia conf WHERE conf.tipohierarquiaorganizacional=:paramTipo AND conf.finalvigencia IS null AND conf.mascara=:paramMascara";

        Query q = getEntityManager().createNativeQuery(sql, ConfiguracaoHierarquiaOrganizacional.class);
        q.setParameter("paramTipo", conf.getTipoHierarquiaOrganizacional().name());
        q.setParameter("paramMascara", conf.getMascara());
        if (q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }


    }

    public void salvarNovo(ConfiguracaoHierarquiaOrganizacional configuracaoAdm, ConfiguracaoHierarquiaOrganizacional configuracaoOrc) throws ExcecaoNegocioGenerica {

        boolean salvaAdm = verificaSePresistNovo(configuracaoAdm);
        boolean salvaOrc = verificaSePresistNovo(configuracaoOrc);

        if (salvaAdm) {
            String sql = "SELECT conf.* FROM ConfiguracaoHierarquia conf WHERE conf.tipohierarquiaorganizacional=:paramTipo AND conf.finalvigencia IS null";
            Query q = getEntityManager().createNativeQuery(sql, ConfiguracaoHierarquiaOrganizacional.class);
            q.setParameter("paramTipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
            if (!q.getResultList().isEmpty()) {
                ConfiguracaoHierarquiaOrganizacional vigente = (ConfiguracaoHierarquiaOrganizacional) q.getSingleResult();
                vigente.setFimVigencia(new Date());
                getEntityManager().merge(vigente);
            }
            getEntityManager().persist(configuracaoAdm.cloneConfiguracao(configuracaoAdm));
        }

        if (salvaOrc) {
            String sql = "SELECT conf.* FROM ConfiguracaoHierarquia conf WHERE conf.tipohierarquiaorganizacional=:paramTipo AND conf.finalvigencia IS null";
            Query q = getEntityManager().createNativeQuery(sql, ConfiguracaoHierarquiaOrganizacional.class);
            q.setParameter("paramTipo", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
            if (!q.getResultList().isEmpty()) {
                ConfiguracaoHierarquiaOrganizacional vigente = (ConfiguracaoHierarquiaOrganizacional) q.getSingleResult();
                vigente.setFimVigencia(new Date());
                getEntityManager().merge(vigente);
            }
            getEntityManager().persist(configuracaoOrc.cloneConfiguracao(configuracaoOrc));
        }
    }

    public ConfiguracaoHierarquiaOrganizacional configuracaoVigente(TipoHierarquiaOrganizacional tipoHO) throws ExcecaoNegocioGenerica {
        try {
            String sql = "SELECT conf.* FROM ConfiguracaoHierarquia conf WHERE conf.tipohierarquiaorganizacional=:paramTipo AND conf.finalvigencia IS null";
            Query q = getEntityManager().createNativeQuery(sql, ConfiguracaoHierarquiaOrganizacional.class);
            String teste = tipoHO.name();
            q.setParameter("paramTipo", tipoHO.name());
            List toResult = q.getResultList();
            if (toResult.isEmpty()) {
                //System.out.println("Retornou null");
                return null;
            } else if (toResult.size() > 1) {
                //System.out.println("Deu erro aquiii");
                throw new ExcecaoNegocioGenerica("Existe mais de uma Configuração vigente para Hierarquia Organizacional do Tipo " + tipoHO.getDescricao() + "!");
            }
            return (ConfiguracaoHierarquiaOrganizacional) toResult.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
