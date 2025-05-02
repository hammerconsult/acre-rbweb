package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.DemonstrativoMaterialSemMovimentacao;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.LocalEstoqueFacade;
import br.com.webpublico.negocios.MaterialFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by HardRock on 25/05/2017.
 */
@Stateless
public class DemonstrativoMaterialSemMovimentacaoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<DemonstrativoMaterialSemMovimentacao> gerarConsulta(String condicao, Date dataInicial, Date dataFinal) {

        StringBuilder query = new StringBuilder();
        query.append(" ")
            .append(" select ")
            .append("   vwadm.codigo || ' - ' || vwadm.descricao as unidade, ")
            .append("   l.codigo || ' - ' || l.descricao as localestoque, ")
            .append("   mat.codigo || ' - ' || mat.descricao as material, ")
            .append("   coalesce(e.financeiro / e.fisico, 0) as valor ")
            .append(" from estoque e ")
            .append("  inner join material mat on mat.id = e.material_id ")
            .append("  inner join localestoqueorcamentario leo on leo.id = e.localestoqueorcamentario_id ")
            .append("  inner join localestoque l on l.id = leo.localestoque_id ")
            .append("  inner join unidadeorganizacional adm on adm.id = l.unidadeorganizacional_id ")
            .append("  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = adm.id ")
            .append(" where e.fisico > 0 ")
            .append("  and e.dataestoque between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, e.dataestoque) ")
            .append("  and e.dataestoque = (select max(est.dataestoque) ")
            .append("                       from estoque est ")
            .append("                       where est.material_id = mat.id ")
            .append("                          and est.localestoqueorcamentario_id = leo.id ")
            .append("                          and est.dataestoque <= to_date(:dataFinal, 'dd/MM/yyyy')")
            .append("                      ) ")
            .append("  and not exists(select 1 ")
            .append("                 from movimentoestoque m ")
            .append("                 where m.material_id = mat.id ")
            .append("                    and m.localestoqueorcamentario_id = leo.id ")
            .append("                    and trunc(m.datamovimento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ")
            .append("                  ) ")
            .append(condicao)
            .append(" group by vwadm.codigo, vwadm.descricao, l.codigo, l.descricao, mat.codigo, mat.descricao, e.financeiro, e.fisico ")
            .append(" order by vwadm.codigo, vwadm.descricao, l.codigo, l.descricao, mat.codigo, mat.descricao, e.financeiro, e.fisico ");
        Query q = em.createNativeQuery(query.toString());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<DemonstrativoMaterialSemMovimentacao> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                DemonstrativoMaterialSemMovimentacao item = new DemonstrativoMaterialSemMovimentacao();
                item.setUnidade((String) obj[0]);
                item.setLocalEstoque((String) obj[1]);
                item.setMaterial((String) obj[2]);
                item.setValor((BigDecimal) obj[3]);
                item.setItem(retorno.size() + 1);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }
}
