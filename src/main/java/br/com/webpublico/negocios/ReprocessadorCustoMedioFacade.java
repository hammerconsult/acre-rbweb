/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Estoque;
import br.com.webpublico.entidades.ItemSaidaMaterial;
import br.com.webpublico.entidades.UnidadeOrganizacional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author cheles
 */
@Stateless
public class ReprocessadorCustoMedioFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EstoqueFacade estoqueFacade;

    public Date recuperarDataDaPrimeiraMovimentacaoRetroativa(UnidadeOrganizacional uo) {
        String sql = "  SELECT MIN(ME.DATAMOVIMENTO)\n" +
                "     FROM MOVIMENTOESTOQUE ME\n" +
                " LEFT JOIN ITEMENTRADAMATERIAL IEM ON IEM.MOVIMENTOESTOQUE_ID = ME.ID\n" +
                " LEFT JOIN ENTRADAMATERIAL EM ON EM.ID = IEM.ENTRADAMATERIAL_ID\n" +
                " LEFT JOIN ITEMSAIDAMATERIAL ISM ON ME.ID = ISM.MOVIMENTOESTOQUE_ID\n" +
                " LEFT JOIN SAIDAMATERIAL SM ON SM.ID = ISM.SAIDAMATERIAL_ID\n" +
                " INNER JOIN ESTOQUE E ON E.ID = ME.ESTOQUE_ID\n" +
                " INNER JOIN LOCALESTOQUEORCAMENTARIO LEO ON LEO.ID = E.LOCALESTOQUEORCAMENTARIO_ID \n" +
                " INNER JOIN LOCALESTOQUE LE ON LE.ID = LEO.LOCALESTOQUE_ID\n" +
                " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = LE.UNIDADEORGANIZACIONAL_ID\n" +
                "    WHERE (EM.RETROATIVO = 1 AND EM.PROCESSADO = 0)\n" +
                "       OR (SM.RETROATIVO = 1 AND SM.PROCESSADO = 0)"
                + "           AND uo.id = :unidade";

        Query q = em.createNativeQuery(sql);
        q.setParameter("unidade", uo.getId());

        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (Date) q.getSingleResult();
        }
    }

    public void recalcularCustoMedioDoItemSaida(ItemSaidaMaterial ism) {

        String sql = "    UPDATE itemsaidamaterial item"
                + "          SET item.valorunitarioreajustado = (SELECT ((e.financeiro + me.financeiro)/(e.fisico + ism.quantidadeaentregar))"
                + "                                                FROM estoque e"
                + "                                          INNER JOIN movimentoestoque me ON me.estoque_id = e.id"
                + "                                          INNER JOIN itemsaidamaterial ism ON ism.movimentoestoque_id = me.id"
                + "                                               WHERE ism.id = :idItem) "
                + "        WHERE item.id = :idItem";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", ism.getId());
        q.executeUpdate();
    }

    public BigDecimal recalcularCustoMedioDoItemSaidaSemQuery(ItemSaidaMaterial ism, UnidadeOrganizacional uo) {
        Estoque estoqueCalculo = estoqueFacade.recuperaEstoqueDaUltimaEntradaDaUnidadeOrgAteAData(ism.getSaidaMaterial().getDataSaida(), uo, ism.getMovimentoEstoque().getMaterial());
        Estoque estoqueParaAlterar = ism.getMovimentoEstoque().getEstoque();

        double financeiro = estoqueCalculo.getFinanceiro().doubleValue();
        double fisico = estoqueCalculo.getFisico().doubleValue();
        BigDecimal novoCusto = BigDecimal.valueOf(financeiro / fisico);


        estoqueParaAlterar.setFinanceiro(BigDecimal.valueOf(novoCusto.multiply(estoqueParaAlterar.getFisico()).doubleValue()));

        estoqueFacade.salvar(estoqueCalculo);
        estoqueFacade.salvar(estoqueParaAlterar);

        return novoCusto;
    }

    public void setaProcessadoNasSaidasEEntradasPosteriorADataSelecionada(Date dataDeInicio, UnidadeOrganizacional unidadeOrganizacional) {
        String sqlEntrada = " UPDATE entradamaterial em"
                + "           SET em.processado = 1"
                + "           WHERE em.id IN ( SELECT e.id FROM entradamaterial e"
                + "                               INNER JOIN itementradamaterial iem ON iem.entradamaterial_id = e.id"
                + "                               INNER JOIN localestoque le ON iem.localestoque_id = le.id"
                + "                               INNER JOIN unidadeorganizacional uo ON le.unidadeorganizacional_id = uo.id"
                + "                                    WHERE e.dataentrada >= :data"
                + "                                      AND uo.id = :unidade)";

        Query entrada = em.createNativeQuery(sqlEntrada);
        entrada.setParameter("data", dataDeInicio, TemporalType.DATE);
        entrada.setParameter("unidade", unidadeOrganizacional.getId());
        entrada.executeUpdate();

        String sqlSaida = " UPDATE saidamaterial sm"
                + "           SET sm.processado = 1"
                + "           WHERE sm.id IN ( SELECT s.id FROM saidamaterial s"
                + "                               INNER JOIN itemsaidamaterial ism ON ism.saidamaterial_id = s.id"
                + "                               INNER JOIN localestoque le ON ism.localestoque_id = le.id"
                + "                               INNER JOIN unidadeorganizacional uo ON le.unidadeorganizacional_id = uo.id"
                + "                                    WHERE s.datasaida >= :data"
                + "                                      AND uo.id = :unidade)";

        Query saida = em.createNativeQuery(sqlSaida);
        saida.setParameter("data", dataDeInicio, TemporalType.DATE);
        saida.setParameter("unidade", unidadeOrganizacional.getId());
        saida.executeUpdate();
    }
}
