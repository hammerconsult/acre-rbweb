package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoExcecaoDespesaContrato;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ConfiguracaoExcecaoDespesaContratoFacade extends AbstractFacade<ConfiguracaoExcecaoDespesaContrato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoExcecaoDespesaContratoFacade() {
        super(ConfiguracaoExcecaoDespesaContrato.class);
    }

    public boolean hasConfiguracaoVigente(ConfiguracaoExcecaoDespesaContrato configuracao) {
        String sql = buscarQueryPorVigencia();
        if (configuracao.getId() != null) {
            sql += " and cfg.id <> :cfgId ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(configuracao.getInicioVigencia()));
        q.setParameter("contaDespesa", configuracao.getContaDespesa().getId());
        if (configuracao.getId() != null) {
            q.setParameter("cfgId", configuracao.getId());
        }
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    private String buscarQueryPorVigencia() {
        return " select * from  CONFEXCECAODESPESACONTRATO cfg " +
            " where cfg.contadespesa_id = :contaDespesa " +
            " and to_date(:dataOperacao, 'dd/MM/yyyy') between cfg.iniciovigencia and coalesce(cfg.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ";
    }

    public boolean hasConfiguracaoVigente(Date dataOperacao, Conta contaDespesa) {
        String sql = buscarQueryPorVigencia();
        Query q = em.createNativeQuery(sql, ConfiguracaoExcecaoDespesaContrato.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("contaDespesa", contaDespesa.getId());
        return !q.getResultList().isEmpty();
    }


    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
