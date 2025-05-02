package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.entidadesauxiliares.VOServidoresPorProvimento;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 28/01/16.
 */
@Stateless
public class RelatorioServidoresPorProvimentoFacade implements Serializable {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public List<VOServidoresPorProvimento> buscarDadosServidoresPorProvimento(HierarquiaOrganizacional orgao, Date dataInicial, Date dataFinal, TipoProvimento tipoProvimento) {
        String sql = " SELECT MATRICULA.MATRICULA ||'/'||VINCULO.NUMERO AS SERVIDOR, " +
            "  PESSOA.NOME AS NOME, " +
            "  TPROVIMENTO.DESCRICAO                            AS TIPOPROVIMENTO, " +
            "  TO_CHAR(PROVIMENTO.DATAPROVIMENTO, 'dd/MM/yyyy') AS DATAPROVIMENTO, " +
            "  UO.DESCRICAO AS UNIDADE " +
            " FROM VINCULOFP VINCULO " +
            " INNER JOIN CONTRATOFP CONTRATO " +
            " ON CONTRATO.ID = VINCULO.ID " +
            " INNER JOIN MATRICULAFP MATRICULA " +
            " ON MATRICULA.ID = VINCULO.MATRICULAFP_ID " +
            " INNER JOIN PESSOAFISICA PESSOA " +
            " ON PESSOA.ID = MATRICULA.PESSOA_ID " +
            " INNER JOIN PROVIMENTOFP PROVIMENTO " +
            " ON PROVIMENTO.VINCULOFP_ID = VINCULO.ID " +
            " INNER JOIN TIPOPROVIMENTO TPROVIMENTO " +
            " ON TPROVIMENTO.ID = PROVIMENTO.TIPOPROVIMENTO_ID " +
            " INNER JOIN UNIDADEORGANIZACIONAL UO " +
            " ON UO.ID = VINCULO.UNIDADEORGANIZACIONAL_ID " +
            " INNER JOIN VWHIERARQUIAADMINISTRATIVA HO " +
            " ON HO.SUBORDINADA_ID = UO.ID " +
            " WHERE PROVIMENTO.DATAPROVIMENTO = (SELECT MAX(P.DATAPROVIMENTO) " +
            " FROM PROVIMENTOFP P " +
            " WHERE P.VINCULOFP_ID = VINCULO.ID and p.tipoProvimento_id = PROVIMENTO.TIPOPROVIMENTO_ID ) " +
            " AND PROVIMENTO.DATAPROVIMENTO BETWEEN to_date(:dataInicial, 'dd/MM/yyyy') " +
            " AND to_date(:dataFinal, 'dd/MM/yyyy') " +
            " AND (to_date(:dataInicial, 'dd/MM/yyyy') BETWEEN HO.INICIOVIGENCIA  AND COALESCE(HO.FIMVIGENCIA, to_date(:dataInicial, 'dd/MM/yyyy')) " +
            " OR to_date(:dataFinal, 'dd/MM/yyyy') BETWEEN HO.INICIOVIGENCIA AND COALESCE(HO.FIMVIGENCIA, to_date(:dataFinal, 'dd/MM/yyyy')))" +
            " AND TPROVIMENTO.ID = :tipoProvimento ";
        if (orgao != null) {
            sql += " AND HO.SUBORDINADA_ID = :orgao ";
            sql += " GROUP BY MATRICULA.MATRICULA||'/'||VINCULO.NUMERO, PESSOA.NOME , TPROVIMENTO.DESCRICAO, " +
                " TO_CHAR(PROVIMENTO.DATAPROVIMENTO, 'dd/MM/yyyy'), UO.DESCRICAO ";
            sql += " ORDER BY UO.DESCRICAO, PESSOA.NOME";
        } else {
            sql += " GROUP BY MATRICULA.MATRICULA||'/'||VINCULO.NUMERO, PESSOA.NOME , TPROVIMENTO.DESCRICAO, " +
                " TO_CHAR(PROVIMENTO.DATAPROVIMENTO, 'dd/MM/yyyy'), UO.DESCRICAO ";
            sql += " ORDER BY UO.DESCRICAO, PESSOA.NOME";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("tipoProvimento", tipoProvimento.getId());
        if (orgao != null) {
            q.setParameter("orgao", orgao.getSubordinada().getId());
        }
        List<VOServidoresPorProvimento> dadosServidor = montarDadosServidoresPorProvimento(q.getResultList());
        return dadosServidor;
    }

    List<VOServidoresPorProvimento> montarDadosServidoresPorProvimento(List<Object[]> resultList) {
        List<VOServidoresPorProvimento> retorno = Lists.newArrayList();
        for (Object[] obj : resultList) {
            VOServidoresPorProvimento vo = new VOServidoresPorProvimento();
            vo.setServidor((String) obj[0]);
            vo.setNome((String) obj[1]);
            vo.setTipoProvimento((String) obj[2]);
            vo.setDataProvimento((String) obj[3]);
            vo.setUnidade((String) obj[4]);
            retorno.add(vo);
        }
        return retorno;
    }
}
