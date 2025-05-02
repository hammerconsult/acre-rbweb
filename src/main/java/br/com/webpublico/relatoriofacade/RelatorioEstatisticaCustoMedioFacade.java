package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.EstatisticaCustoMedio;
import br.com.webpublico.negocios.MaterialFacade;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 26/05/2017.
 */
@Stateless
public class RelatorioEstatisticaCustoMedioFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MaterialFacade materialFacade;

    public List<EstatisticaCustoMedio> buscarDados(String filtros, String mascara) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select datamovimento, ");
        sql.append("        sum(valor) as valor ");
        sql.append("   from (");
        sql.append(" SELECT to_char(MO.DATAMOVIMENTO, '").append(mascara).append("') AS DATAMOVIMENTO,  ");
        sql.append("        case mo.tipooperacao ");
        sql.append("           when 'CREDITO' then sum(MO.FINANCEIRO) else sum(MO.FINANCEIRO) * - 1 ");
        sql.append("        end AS VALOR  ");
        sql.append("   FROM LOCALESTOQUE L ");
        sql.append("  INNER JOIN LOCALESTOQUEORCAMENTARIO LEO ON LEO.LOCALESTOQUE_ID = L.ID ");
        sql.append("  INNER JOIN ESTOQUE E ON E.LOCALESTOQUEORCAMENTARIO_ID = LEO.ID ");
        sql.append("  INNER JOIN MOVIMENTOESTOQUE MO ON MO.ESTOQUE_ID = E.ID ");
        sql.append("  INNER JOIN MATERIAL M ON M.ID = E.MATERIAL_ID ");
        sql.append("  INNER JOIN GRUPOMATERIAL GP ON GP.ID = M.GRUPO_ID ");
        sql.append("  INNER JOIN VWHIERARQUIAADMINISTRATIVA VW ON VW.SUBORDINADA_ID = L.UNIDADEORGANIZACIONAL_ID ");
        sql.append("    AND MO.DATAMOVIMENTO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, MO.DATAMOVIMENTO) ");
        sql.append(filtros);
        sql.append("   group by to_char(MO.DATAMOVIMENTO, '").append(mascara).append("'), mo.tipooperacao ");
        sql.append("   ) group by dataMovimento ");
        sql.append("   order by dataMovimento ");
        Query q = em.createNativeQuery(sql.toString());
        List<EstatisticaCustoMedio> toReturn = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                EstatisticaCustoMedio estatisticaCustoMedio = new EstatisticaCustoMedio();
                estatisticaCustoMedio.setPeriodo((String) obj[0]);
                estatisticaCustoMedio.setCustoMedio((BigDecimal) obj[1]);
                toReturn.add(estatisticaCustoMedio);
            }
        }
        return toReturn;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }
}
