package br.com.webpublico.relatoriofacade.administrativo.frotas;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.frotas.LaudoInspecaoTecnicaEquipamentoVO;
import br.com.webpublico.negocios.EquipamentoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRelatorioContabil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 25/05/17.
 */
@Stateless
public class RelatorioLaudoInspecaoTecnicaEquipamentoFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private EquipamentoFacade equipamentoFacade;

    public List<LaudoInspecaoTecnicaEquipamentoVO> recuperarEquipamentos(List<ParametrosRelatorios> parametros) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ID_EQUIPAMENTO, ")
            .append("      DESCRICAO, ")
            .append("      IDENTIFICACAO, ")
            .append("      ITEM, ")
            .append("      TIPOINSPECAOEQUIPAMENTO, ")
            .append("      UNIDADE ")
            .append(" FROM  ")
            .append("   (SELECT ")
            .append("     EQUIPAMENTO.ID AS ID_EQUIPAMENTO,  ")
            .append("     COALESCE(OBJ.DESCRICAO, BEM.DESCRICAO) AS DESCRICAO,  ")
            .append("     OBJ.IDENTIFICACAO,  ")
            .append("     ITENS.DESCRICAO AS ITEM,  ")
            .append("     ITENS.TIPOINSPECAOEQUIPAMENTO AS TIPOINSPECAOEQUIPAMENTO,  ")
            .append("     VW.DESCRICAO AS UNIDADE ")
            .append("   FROM OBJETOFROTA OBJ  ")
            .append("   INNER JOIN UNIDADEOBJETOFROTA UBF ON UBF.OBJETOFROTA_ID = OBJ.ID ")
            .append("         AND TO_DATE(:DATAOPERACAO, 'dd/MM/YYYY') BETWEEN trunc(UBF.INICIOVIGENCIA) AND COALESCE(trunc(UBF.FIMVIGENCIA), TO_DATE(:DATAOPERACAO, 'dd/MM/YYYY')) ")
            .append("   INNER JOIN VWHIERARQUIAADMINISTRATIVA VW ON VW.SUBORDINADA_ID = UBF.UNIDADEORGANIZACIONAL_ID  ")
            .append("         AND TO_DATE(:DATAOPERACAO, 'dd/MM/YYYY') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:DATAOPERACAO, 'dd/MM/YYYY')) ")
            .append("   INNER JOIN EQUIPAMENTO ON EQUIPAMENTO.ID = OBJ.ID  ")
            .append("   LEFT JOIN BEM ON BEM.ID = OBJ.BEM_ID  ")
            .append("   LEFT JOIN  ")
            .append("     (SELECT IT.ID  AS ID_ITEM,  ")
            .append("       IT.DESCRICAO AS DESCRICAO,  ")
            .append("       IT.TIPOINSPECAOEQUIPAMENTO AS TIPOINSPECAOEQUIPAMENTO,  ")
            .append("       IT.ATIVO  ")
            .append("     FROM ITEMINSPECAOEQUIPAMENTO IT  ")
            .append("     WHERE IT.ATIVO = 1  ")
            .append("     ) ITENS  ")
            .append("   ON ITENS.ATIVO = 1  ")
            .append("   )  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 0, " WHERE "))
            .append(" AND   ")
            .append("   (TIPOINSPECAOEQUIPAMENTO IS NULL OR ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " "))
            .append("   )")
            .append(" GROUP BY ID_EQUIPAMENTO, DESCRICAO, IDENTIFICACAO, ITEM, TIPOINSPECAOEQUIPAMENTO, UNIDADE ")
            .append(" ORDER BY TIPOINSPECAOEQUIPAMENTO, ITEM ");
        Query q = em.createNativeQuery(sb.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        q.setParameter("DATAOPERACAO", DataUtil.getDataFormatada(getEquipamentoFacade().getSistemaFacade().getDataOperacao()));
        List<LaudoInspecaoTecnicaEquipamentoVO> retorno = new ArrayList<>();
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                LaudoInspecaoTecnicaEquipamentoVO laudo = new LaudoInspecaoTecnicaEquipamentoVO();
                laudo.setEquipamentoId((BigDecimal) obj[0]);
                laudo.setDescricaoEquipamento((String) obj[1]);
                laudo.setIdentificacaoEquipamento((String) obj[2]);
                laudo.setItemInspecaoEquipamento((String) obj[3]);
                laudo.setTipoInspecaoEquipamento((String) obj[4]);
                laudo.setUnidade((String) obj[5]);
                retorno.add(laudo);
            }
        }
        return retorno;
    }

    public EquipamentoFacade getEquipamentoFacade() {
        return equipamentoFacade;
    }
}
