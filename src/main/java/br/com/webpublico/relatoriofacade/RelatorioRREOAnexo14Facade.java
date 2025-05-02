package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.RREOAnexo14Item;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.negocios.ProjecaoAtuarialFacade;
import br.com.webpublico.negocios.ReferenciaAnualFacade;
import br.com.webpublico.negocios.RelatorioRREOAnexo12Calculator2018;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mateus on 30/09/2014.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRREOAnexo14Facade extends RelatorioItemDemonstrativoCalculador {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private ItemDemonstrativoCalculator itemDemonstrativoCalculator;
    @EJB
    private RelatorioRREOAnexo02Calculator relatorioRREOAnexo02Calculator;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private RelatorioRREOAnexo05Calculator relatorioRREOAnexo05Calculator;
    @EJB
    private RelatorioRREOAnexo07Calculos relatorioRREOAnexo07Calculos;
    @EJB
    private RelatorioRREOAnexo08Facade2018 relatorioRREOAnexo08Facade2018;
    @EJB
    private RelatorioRREOAnexo12Calculator2018 relatorioRREOAnexo12Calculator2018;
    @EJB
    private ProjecaoAtuarialFacade projecaoAtuarialFacade;
    @EJB
    private ReferenciaAnualFacade referenciaAnualFacade;

    public RREOAnexo14Item buscaValorProjecao(Integer ordem, RREOAnexo14Item item, Integer menorData, Integer maiorData, Exercicio exercicio) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT IPA.EXERCICIO AS EXERCICIO, ")
            .append("        IPA.RECEITASPREVIDENCIARIAS AS RECEITA, ")
            .append("        IPA.DESPESASPREVIDENCIARIAS AS DESPESA, ")
            .append("        IPA.RESULTADOPREVIDENCIARIO AS RESULTADO, ")
            .append("        IPA.SALDOPREVIDENCIARIO AS SALDO ")
            .append(" FROM ITEMPROJECAOATUARIAL IPA ")
            .append(" INNER JOIN PROJECAOATUARIAL PA ON IPA.PROJECAOATUARIAL_ID = PA.ID ")
            .append(" where PA.EXERCICIO_ID = :EXERCICIO AND IPA.EXERCICIO >= :MENORDATA AND IPA.EXERCICIO <= :MAIORDATA ")
            .append(" and pa.dataAvaliacao = (select max(dataAvaliacao) from ProjecaoAtuarial where pa.exercicio_id = :EXERCICIO) ")
            .append(" order by ipa.exercicio ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("EXERCICIO", exercicio.getId());
        q.setParameter("MENORDATA", menorData);
        q.setParameter("MAIORDATA", maiorData);
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                if (ordem == 2) {
                    if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno())) {
                        item.setValorColuna1((BigDecimal) obj[1]);
                    } else if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno() + 10)) {
                        item.setValorColuna2((BigDecimal) obj[1]);
                    } else if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno() + 20)) {
                        item.setValorColuna3((BigDecimal) obj[1]);
                    } else if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno() + 35)) {
                        item.setValorColuna4((BigDecimal) obj[1]);
                    }
                } else if (ordem == 3) {
                    if (((BigDecimal) obj[0]).intValue() == exercicio.getAno()) {
                        item.setValorColuna1((BigDecimal) obj[2]);
                    } else if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno() + 10)) {
                        item.setValorColuna2((BigDecimal) obj[2]);
                    } else if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno() + 20)) {
                        item.setValorColuna3((BigDecimal) obj[2]);
                    } else if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno() + 35)) {
                        item.setValorColuna4((BigDecimal) obj[2]);
                    }
                } else if (ordem == 4) {
                    if (((BigDecimal) obj[0]).intValue() == exercicio.getAno()) {
                        item.setValorColuna1((BigDecimal) obj[3]);
                    } else if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno() + 10)) {
                        item.setValorColuna2((BigDecimal) obj[3]);
                    } else if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno() + 20)) {
                        item.setValorColuna3((BigDecimal) obj[3]);
                    } else if (((BigDecimal) obj[0]).intValue() == (exercicio.getAno() + 35)) {
                        item.setValorColuna4((BigDecimal) obj[3]);
                    }
                }
            }
        }
        return item;

    }

    public EntityManager getEm() {
        return em;
    }

    public RelatorioRREOAnexo02Calculator getRelatorioRREOAnexo02Calculator() {
        return relatorioRREOAnexo02Calculator;
    }

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }

    public RelatorioRREOAnexo05Calculator getRelatorioRREOAnexo05Calculator() {
        return relatorioRREOAnexo05Calculator;
    }

    public RelatorioRREOAnexo07Calculos getRelatorioRREOAnexo07Calculos() {
        return relatorioRREOAnexo07Calculos;
    }

    public RelatorioRREOAnexo08Facade2018 getRelatorioRREOAnexo08Facade2018() {
        return relatorioRREOAnexo08Facade2018;
    }

    public RelatorioRREOAnexo12Calculator2018 getRelatorioRREOAnexo12Calculator2018() {
        return relatorioRREOAnexo12Calculator2018;
    }

    public ProjecaoAtuarialFacade getProjecaoAtuarialFacade() {
        return projecaoAtuarialFacade;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public ReferenciaAnualFacade getReferenciaAnualFacade() {
        return referenciaAnualFacade;
    }

    public ItemDemonstrativoCalculator getItemDemonstrativoCalculator() {
        return itemDemonstrativoCalculator;
    }
}
