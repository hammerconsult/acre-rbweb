package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LevantamentoEstoque;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoLevantamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Desenvolvimento on 26/01/2017.
 */
@Stateless
public class LevantamentoEstoqueFacade extends AbstractFacade<LevantamentoEstoque> {

    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private MaterialFacade materialFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LevantamentoEstoqueFacade() {
        super(LevantamentoEstoque.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(LevantamentoEstoque entity) {
        entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(LevantamentoEstoque.class, "codigo"));
        super.salvarNovo(entity);
    }

    public LevantamentoEstoque salvarLevantamento(LevantamentoEstoque entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(LevantamentoEstoque.class, "codigo"));
        }
        return em.merge(entity);
    }

    @Override
    public LevantamentoEstoque recuperar(Object id) {
        LevantamentoEstoque entity = super.recuperar(id);
        Hibernate.initialize(entity.getItensLevantamentoEstoque());
        entity.setHierarquiaAdministrativa(hierarquiaFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), entity.getUnidadeAdministrativa(), entity.getDataLevantamento()));
        entity.setHierarquiaOrcamentaria(hierarquiaFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), entity.getUnidadeOrcamentaria(), entity.getDataLevantamento()));
        return entity;
    }

    public LevantamentoEstoque efetivarLevantamento(LevantamentoEstoque entity) {
        entity.setSituacao(SituacaoLevantamento.FINALIZADO);
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(LevantamentoEstoque.class, "codigo"));
        }
        return em.merge(entity);
    }

    public List<LevantamentoEstoque> buscarLevantamentoPorUnidadeOrcamentaria(HierarquiaOrganizacional orcamentaria) {
        String sql = "Select l.* " +
            "           from LevantamentoEstoque l " +
            "         where " +
            "           l.unidadeorcamentaria_id = :orcamentaria" +
            "       and l.situacao = :finalizado";
        Query q = em.createNativeQuery(sql, LevantamentoEstoque.class);
        q.setParameter("orcamentaria", orcamentaria.getSubordinada().getId());
        q.setParameter("finalizado", SituacaoLevantamento.FINALIZADO.name());
        List<LevantamentoEstoque> retorno = q.getResultList();
        if (retorno != null && !retorno.isEmpty()) {
            for (LevantamentoEstoque entity : retorno) {
                entity.setHierarquiaAdministrativa(hierarquiaFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), entity.getUnidadeAdministrativa(), entity.getDataLevantamento()));
                entity.setHierarquiaOrcamentaria(hierarquiaFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), entity.getUnidadeOrcamentaria(), entity.getDataLevantamento()));
                Hibernate.initialize(entity.getItensLevantamentoEstoque());
            }
        }
        return retorno;
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            query.setParameter("dataOperacao", DataUtil.dataSemHorario(sistemaFacade.getDataOperacao()));
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    public void setSituacaoEmElaboracao(LevantamentoEstoque levantamentoEstoque) {
        levantamentoEstoque.setSituacao(SituacaoLevantamento.EM_ELABORACAO);
        em.merge(levantamentoEstoque);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaFacade() {
        return hierarquiaFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

}
