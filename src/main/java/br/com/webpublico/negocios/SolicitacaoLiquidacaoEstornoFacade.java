package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Liquidacao;
import br.com.webpublico.entidades.SolicitacaoEmpenhoEstorno;
import br.com.webpublico.entidades.SolicitacaoLiquidacaoEstorno;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SolicitacaoLiquidacaoEstornoFacade extends AbstractFacade<SolicitacaoLiquidacaoEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SolicitacaoLiquidacaoEstornoFacade() {
        super(SolicitacaoLiquidacaoEstorno.class);
    }

    public List<SolicitacaoLiquidacaoEstorno> buscarSolicitacoesPendentesPorUnidadeAndLiquidacao(UnidadeOrganizacional unidadeOrganizacional, Liquidacao liquidacao, CategoriaOrcamentaria categoriaOrcamentaria) {
        String sql = " select sol.* from solicitacaoliquidacaoest sol " +
            "          inner join liquidacao liq on liq.id = sol.liquidacao_id " +
            "          where sol.liquidacaoestorno_id is null " +
            "           and liq.id = :idLiquidacao " +
            "           and liq.categoriaorcamentaria = :categoria " +
            "           and liq.unidadeorganizacional_id = :idUnidade ";
        Query q = em.createNativeQuery(sql, SolicitacaoLiquidacaoEstorno.class);
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("idLiquidacao", liquidacao.getId());
        q.setParameter("categoria", categoriaOrcamentaria.name());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        }
        return resultList;
    }

    public List<SolicitacaoLiquidacaoEstorno> buscarSolicitacaoLiquidacaoEstorno(UnidadeOrganizacional unidadeOrganizacional, CategoriaOrcamentaria categoriaOrcamentaria) {
        String sql = " select sol.* from solicitacaoliquidacaoest sol " +
            "          inner join liquidacao liq on liq.id = sol.liquidacao_id " +
            "          where sol.liquidacaoestorno_id is null " +
            "          and liq.unidadeOrganizacional_id = :idUnidade " +
            "          and liq.categoriaorcamentaria = :categoria " +
            "          order by dataSolicitacao desc ";
        Query q = em.createNativeQuery(sql, SolicitacaoLiquidacaoEstorno.class);
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("categoria", categoriaOrcamentaria.name());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }


    public List<SolicitacaoLiquidacaoEstorno> buscarPorSolicitacaoEstornoEmpenho(SolicitacaoEmpenhoEstorno solicitacao) {
        String sql = " select sol.* from solicitacaoliquidacaoest sol " +
            "  inner join execucaocontratoliquidest seel on seel.solicitacaoliquidacaoest_id = sol.id " +
            "  inner join execucaocontratoempenhoest est on est.id = seel.execucaocontratoempenhoest_id " +
            "  where est.solicitacaoempenhoestorno_id = :idSolicitacao " +
            "  and sol.liquidacaoestorno_id is null ";
        Query q = em.createNativeQuery(sql, SolicitacaoLiquidacaoEstorno.class);
        q.setParameter("idSolicitacao", solicitacao.getId());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
