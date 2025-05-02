package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoUnidadeRequerente;
import br.com.webpublico.entidades.UnidadeDistribuidora;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.SituacaoSolicitacaoUnidadeRequerente;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class UnidadeDistribuidoraFacade extends AbstractFacade<UnidadeDistribuidora> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SolicitacaoUnidadeRequerenteFacade solicitacaoUnidadeRequerenteFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public UnidadeDistribuidora recuperar(Object id) {
        UnidadeDistribuidora entity = super.recuperar(id);
        HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), entity.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        if (hierarquia != null){
            entity.setHierarquiaOrganizacional(hierarquia);
        }
        return entity;
    }

    public UnidadeDistribuidora salvarUnidadeDistribuidora(UnidadeDistribuidora entity){
        return em.merge(entity);
    }

    public boolean buscarUnidadeDistribuidora(UnidadeDistribuidora unidadeDistribuidora) {
        String sql = " SELECT * FROM UNIDADEDISTRIBUIDORA WHERE UNIDADEORGANIZACIONAL_ID = :unidade ";
        if (unidadeDistribuidora.getId() != null) {
            sql += " AND id <> :id_selecionado ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidade", unidadeDistribuidora.getUnidadeOrganizacional().getId());
        if (unidadeDistribuidora.getId() != null) {
            q.setParameter("id_selecionado", unidadeDistribuidora.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<UnidadeDistribuidora> buscarUnidadesDistribuidoraPorCodigoOuDescricao(String codigoOuDescricao) {
        String sql = " select und.* from UNIDADEDISTRIBUIDORA und " +
            " inner join HIERARQUIAORGANIZACIONAL hi on hi.SUBORDINADA_ID = und.UNIDADEORGANIZACIONAL_ID " +
            " where hi.TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA' " +
            " and (to_char(hi.CODIGO) like :codigoOuDescricao " +
            " or lower (hi.DESCRICAO) like :codigoOuDescricao) " +
            " and :dataOperacao between hi.INICIOVIGENCIA and coalesce(hi.FIMVIGENCIA, :dataOperacao) ";

        Query q = em.createNativeQuery(sql, UnidadeDistribuidora.class);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("codigoOuDescricao", "%" + codigoOuDescricao.toLowerCase().trim() + "%");

        List<UnidadeDistribuidora> list = q.getResultList();

        for (UnidadeDistribuidora unidade : list) {
            HierarquiaOrganizacional hoAdm = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                sistemaFacade.getDataOperacao(),
                unidade.getUnidadeOrganizacional(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            unidade.setHierarquiaOrganizacional(hoAdm);
        }
        return list;
    }

    public List<UnidadeDistribuidora> buscarUnidadesDistribuidorasPorUnidadeRequerente(UnidadeOrganizacional unidadeRequerente) {
        String sql = " select ud.* from unidadedistribuidora ud " +
            "          inner join solicitacaounidrequerente sol on sol.unidadedistribuidora_id = ud.id " +
            "          inner join solicitacaoundrequnidade req on req.solicitacao_id = sol.id " +
            "          inner join efetivacaounidaderequerent efet on sol.id = efet.solicitacao_id " +
            "          where req.unidadeorganizacional_id = :unidadeRequerente " +
            "          and sol.situacao = :situacaoEfetivada";
        Query q = em.createNativeQuery(sql, UnidadeDistribuidora.class);
        q.setParameter("unidadeRequerente", unidadeRequerente.getId());
        q.setParameter("situacaoEfetivada", SituacaoSolicitacaoUnidadeRequerente.EFETIVADA.name());
        return (List<UnidadeDistribuidora>) q.getResultList();
    }

    public UnidadeDistribuidoraFacade() {
        super(UnidadeDistribuidora.class);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public SolicitacaoUnidadeRequerenteFacade getSolicitacaoUnidadeRequerenteFacade() {
        return solicitacaoUnidadeRequerenteFacade;
    }
}
