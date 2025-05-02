/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EstornoMovimentoDividaPublica;
import br.com.webpublico.enums.OperacaoDiarioDividaPublica;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Renato
 */
@Stateless
public class EstornoMovimentoDividaPublicaFacade extends AbstractFacade<EstornoMovimentoDividaPublica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private MovimentoDividaPublicaFacade movimentoDividaPublicaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public EstornoMovimentoDividaPublicaFacade() {
        super(EstornoMovimentoDividaPublica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DividaPublicaFacade getDividaPublicaFacade() {
        return dividaPublicaFacade;
    }

    public SaldoDividaPublicaFacade getSaldoDividaPublicaFacade() {
        return saldoDividaPublicaFacade;
    }

    public MovimentoDividaPublicaFacade getMovimentoDividaPublicaFacade() {
        return movimentoDividaPublicaFacade;
    }

    @Override
    public Long retornaUltimoCodigoLong() {
        Long num;
        String sql = " SELECT max(coalesce(obj.numero,0)) FROM ESTORNOMOVIDIVIDAPUBLICA obj ";
        Query query = getEntityManager().createNativeQuery(sql);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            BigDecimal b = (BigDecimal) query.getSingleResult();
            if (b != null) {
                b = b.add(BigDecimal.ONE);
            } else {
                b = BigDecimal.ONE;
            }
            num = b.setScale(0, BigDecimal.ROUND_UP).longValueExact();
        } else {
            return 1l;
        }
        return num;
    }

    @Override
    public void salvar(EstornoMovimentoDividaPublica entity) {
        super.salvar(entity);
    }

    public void gerarSaldo(EstornoMovimentoDividaPublica estorno) throws ExcecaoNegocioGenerica, Exception {
        saldoDividaPublicaFacade.gerarMovimento(estorno.getData(), estorno.getValor(), estorno.getUnidadeOrganizacional(), estorno.getMovimentoDividaPublica().getDividaPublica(), estorno.getOperacaoMovimentoDividaPublica(), true, OperacaoDiarioDividaPublica.MOVIMENTO);
        estorno.getMovimentoDividaPublica().setSaldo(estorno.getMovimentoDividaPublica().getSaldo().subtract(estorno.getSaldo()));
        if (estorno.getMovimentoDividaPublica().getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocioGenerica("O Saldo do Movimento da Dívida Pública " + estorno.getMovimentoDividaPublica() + " não pode ser negativo. valor: " + new DecimalFormat("#,###,##0.00").format(estorno.getMovimentoDividaPublica().getSaldo()));
        }
        salvar(estorno);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
