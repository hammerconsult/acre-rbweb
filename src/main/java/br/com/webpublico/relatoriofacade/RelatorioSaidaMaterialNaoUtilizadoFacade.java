package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.SaidaMaterialNaoUtilizado;
import br.com.webpublico.negocios.*;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by HardRock on 26/05/2017.
 */
@Stateless
public class RelatorioSaidaMaterialNaoUtilizadoFacade implements Serializable {
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
    @EJB
    private TipoBaixaBensFacade tipoBaixaBensFacade;

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<SaidaMaterialNaoUtilizado> gerarConsulta(String condicao) {

        StringBuilder query = new StringBuilder();
        query.append(" ")
            .append(" select vwadm.codigo || ' - ' || vwadm.descricao as unidade, ")
            .append("        loc.codigo || ' - ' || loc.descricao as localestoque, ")
            .append("        mat.codigo || ' - ' || mat.descricao as material, ")
            .append("        tipo.descricao as tipobaixa, ")
            .append("        coalesce(item.valorunitario, 0) as valor  ")
            .append(" from saidamaterial saida  ")
            .append("   inner join saidamatinutilizavel inut on inut.id = saida.id ")
            .append("   inner join tipobaixabens tipo on tipo.id = inut.tipobaixabens_id ")
            .append("   inner join itemsaidamaterial item on item.saidamaterial_id = saida.id ")
            .append("   inner join itemsaidadesincorporacao itemunit on itemunit.itemsaidamaterial_id = item.id ")
            .append("   inner join material mat on mat.id = itemunit.material_id ")
            .append("   inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id ")
            .append("   inner join localestoque loc on loc.id = leo.localestoque_id ")
            .append("   inner join unidadeorganizacional adm on adm.id = loc.unidadeorganizacional_id ")
            .append("   inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = adm.id ")
            .append("   inner join unidadeorganizacional orc on orc.id = leo.unidadeorcamentaria_id ")
            .append(" where saida.datasaida between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, saida.datasaida) ")
            .append(condicao);
        Query q = em.createNativeQuery(query.toString());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<SaidaMaterialNaoUtilizado> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                SaidaMaterialNaoUtilizado item = new SaidaMaterialNaoUtilizado();
                item.setUnidadeAdministrativa((String) obj[0]);
                item.setLocalEstoque((String) obj[1]);
                item.setMaterial((String) obj[2]);
                item.setTipoBaixa((String) obj[3]);
                item.setValor((BigDecimal) obj[4]);
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

    public TipoBaixaBensFacade getTipoBaixaBensFacade() {
        return tipoBaixaBensFacade;
    }
}
