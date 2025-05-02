package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoUnidadeRequerente;
import br.com.webpublico.entidades.SolicitacaoUnidadeRequerenteUnidade;
import br.com.webpublico.entidades.UnidadeDistribuidora;
import br.com.webpublico.enums.SituacaoSolicitacaoUnidadeRequerente;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

@Stateless
public class SolicitacaoUnidadeRequerenteFacade extends AbstractFacade<SolicitacaoUnidadeRequerente> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeDistribuidoraFacade unidadeDistribuidoraFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;

    public SolicitacaoUnidadeRequerenteFacade() {
        super(SolicitacaoUnidadeRequerente.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SolicitacaoUnidadeRequerente recuperar(Object id) {
        SolicitacaoUnidadeRequerente solicitacao = em.find(SolicitacaoUnidadeRequerente.class, id);
        Hibernate.initialize(solicitacao.getUnidadesRequerentes());
        for (SolicitacaoUnidadeRequerenteUnidade unidade : solicitacao.getUnidadesRequerentes()) {
            HierarquiaOrganizacional hoAdm = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                sistemaFacade.getDataOperacao(),
                unidade.getUnidadeOrganizacional(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            unidade.setHierarquiaOrganizacional(hoAdm);
        }
        HierarquiaOrganizacional hoAdmUnidadeRequerente = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
            sistemaFacade.getDataOperacao(),
            solicitacao.getUnidadeDistribuidora().getUnidadeOrganizacional(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        solicitacao.getUnidadeDistribuidora().setHierarquiaOrganizacional(hoAdmUnidadeRequerente);
        Collections.sort(solicitacao.getUnidadesRequerentes());
        return solicitacao;
    }

    public SolicitacaoUnidadeRequerente salvarSolicitacao(SolicitacaoUnidadeRequerente entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoUnidadeRequerente.class, "codigo"));
        }
        return em.merge(entity);
    }

    public List<SolicitacaoUnidadeRequerente> burcarSolicitacaoPorSituacao(SituacaoSolicitacaoUnidadeRequerente situacao, String filtro) {
        String sql = " select sol.* from solicitacaounidrequerente sol  " +
            "           inner join unidadedistribuidora ud on ud.id = sol.unidadedistribuidora_id " +
            "           inner join hierarquiaorganizacional hi on hi.subordinada_id = ud.unidadeorganizacional_id " +
            "          where hi.tipohierarquiaorganizacional = :tipoHierarquia " +
            "           and (hi.codigo like :filtro or lower(hi.descricao) like :filtro or sol.codigo like :filtro) " +
            "           and :dataOperacao between hi.iniciovigencia and coalesce(hi.fimvigencia, :dataOperacao) " +
            "           and sol.situacao = :situacao " +
            "           order by sol.codigo desc ";
        Query q = em.createNativeQuery(sql, SolicitacaoUnidadeRequerente.class);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("situacao", situacao.name());
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        List<SolicitacaoUnidadeRequerente> list = q.getResultList();
        for (SolicitacaoUnidadeRequerente sol : list) {
            HierarquiaOrganizacional hoAdmUnidadeRequerente = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                sistemaFacade.getDataOperacao(),
                sol.getUnidadeDistribuidora().getUnidadeOrganizacional(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            sol.getUnidadeDistribuidora().setHierarquiaOrganizacional(hoAdmUnidadeRequerente);
        }
        return list;
    }

    public List<SolicitacaoUnidadeRequerente> buscarSolicitacaoPorUnidadeDistribuidora(UnidadeDistribuidora unidadeDistribuidora) {
        String sql = " select sol.* from solicitacaounidrequerente sol " +
            "          where sol.unidadedistribuidora_id = :idUnidadeDistribuidora " +
            "          order by sol.codigo ";
        Query q = em.createNativeQuery(sql, SolicitacaoUnidadeRequerente.class);
        q.setParameter("idUnidadeDistribuidora", unidadeDistribuidora.getId());
        List<SolicitacaoUnidadeRequerente> list = Lists.newArrayList();
        for (SolicitacaoUnidadeRequerente sol : (List<SolicitacaoUnidadeRequerente>) q.getResultList()) {
            SolicitacaoUnidadeRequerente solicitacao = recuperar(sol.getId());
            list.add(solicitacao);
        }
        return list;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public UnidadeDistribuidoraFacade getUnidadeDistribuidoraFacade() {
        return unidadeDistribuidoraFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
