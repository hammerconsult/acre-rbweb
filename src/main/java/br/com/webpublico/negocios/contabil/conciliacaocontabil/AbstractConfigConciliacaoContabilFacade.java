package br.com.webpublico.negocios.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabil;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public abstract class AbstractConfigConciliacaoContabilFacade extends AbstractFacade<ConfigConciliacaoContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public AbstractConfigConciliacaoContabilFacade() {
        super(ConfigConciliacaoContabil.class);
    }

    @Override
    public ConfigConciliacaoContabil recuperar(Object id) {
        ConfigConciliacaoContabil config = em.find(ConfigConciliacaoContabil.class, id);
        Hibernate.initialize(config.getContas());
        Hibernate.initialize(config.getContasFinanceiras());
        Hibernate.initialize(config.getTiposDeContaDeReceita());
        Hibernate.initialize(config.getNaturezasTipoGrupoMaterial());
        Hibernate.initialize(config.getGruposBens());
        Hibernate.initialize(config.getNaturezasDaDividaPublica());
        Hibernate.initialize(config.getPassivosAtuariais());
        Hibernate.initialize(config.getCategoriasOrcamentarias());
        Hibernate.initialize(config.getGruposMateriais());
        Hibernate.initialize(config.getCategoriasDaDividaPublica());
        return super.recuperar(id);
    }

    public boolean hasConfigParaOrdemQuadroAndTipo(ConfigConciliacaoContabil entity) {
        String sql = " select 1 from ConfigConciliacaoContabil cfg " +
            " where cfg.ordem = :ordem " +
            "   and cfg.totalizador = :totalizador " +
            "   and cfg.tipoMovimentoSaldo = :tipoMovimentoSaldo " +
            "   and cfg.quadro = :quadro ";
        if (entity.getId() != null) {
            sql += " and cfg.id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("ordem", entity.getOrdem());
        q.setParameter("totalizador", entity.getTotalizador().name());
        q.setParameter("tipoMovimentoSaldo", entity.getTipoMovimentoSaldo().name());
        q.setParameter("quadro", entity.getQuadro());
        if (entity.getId() != null) {
            q.setParameter("id", entity.getId());
        }
        return !q.getResultList().isEmpty();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
