package br.com.webpublico.negocios.contabil.financeiro;

import br.com.webpublico.entidades.contabil.financeiro.UnidadeAcompanhamentoFinanceiro;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by renatoromanini on 18/04/17.
 */
@Stateless
public class UnidadeAcompanhamentoFinanceiroFacade extends AbstractFacade<UnidadeAcompanhamentoFinanceiro> implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public UnidadeAcompanhamentoFinanceiroFacade() {
        super(UnidadeAcompanhamentoFinanceiro.class);
    }

    protected EntityManager getEntityManager() {
        return em;
    }


    public List<UnidadeAcompanhamentoFinanceiro> recuperarConfiguracoes() {
        String sql = " select obj.* from UNIDADEACOMPAFINANCEIRO obj " +
            " inner join vwhierarquiaorcamentaria vw on obj.unidadeorganizacional_id = vw.subordinada_id " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " order by vw.codigo desc";
        Query q = em.createNativeQuery(sql, UnidadeAcompanhamentoFinanceiro.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List<UnidadeAcompanhamentoFinanceiro> retorno = q.getResultList();
        for (UnidadeAcompanhamentoFinanceiro unidadeAcompanhamentoFinanceiro : retorno) {
            unidadeAcompanhamentoFinanceiro.getFiltros().size();
            unidadeAcompanhamentoFinanceiro.getIntervalosConta().size();
            unidadeAcompanhamentoFinanceiro.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                UtilRH.getDataOperacao(), unidadeAcompanhamentoFinanceiro.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA));
        }
        Collections.sort(retorno, new Comparator<UnidadeAcompanhamentoFinanceiro>() {
            @Override
            public int compare(UnidadeAcompanhamentoFinanceiro o1, UnidadeAcompanhamentoFinanceiro o2) {
                if (o1.getHierarquiaOrganizacional() != null && o2.getHierarquiaOrganizacional() != null) {
                    return o1.getHierarquiaOrganizacional().getCodigo().compareTo(o2.getHierarquiaOrganizacional().getCodigo());
                }
                return 0;
            }
        });
        return retorno;
    }
}
