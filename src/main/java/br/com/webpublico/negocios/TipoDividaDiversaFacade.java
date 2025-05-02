/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoDividaDiversa;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.entidades.TributoTaxaDividasDiversas;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wellington
 */
@Stateless
public class TipoDividaDiversaFacade extends AbstractFacade<TipoDividaDiversa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TributoTaxasDividasDiversasFacade tributoTaxasDividasDiversasFacade;
    @EJB
    private DividaFacade dividaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoDividaDiversaFacade() {
        super(TipoDividaDiversa.class);
    }

    public TributoTaxasDividasDiversasFacade getTributoTaxasDividasDiversasFacade() {
        return tributoTaxasDividasDiversasFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    @Override
    public TipoDividaDiversa recuperar(Object id) {
        TipoDividaDiversa td = em.find(TipoDividaDiversa.class, id);
        td.getTributosTaxas().size();
        return td;
    }

    public List<TipoDividaDiversa> buscarTipoDividaDiversaAtivo(String parte) {
        String hql = "select t from TipoDividaDiversa t where t.situacao = 'ATIVO' and (lower(t.descricao) like :parte)";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<TributoTaxaDividasDiversas> listaTributosDoTipoDividaDiversa(TipoDividaDiversa tipoDividaDiversa) {
        Query q = em.createNativeQuery(" SELECT ttd.* "
            + "  FROM tributotaxadividasdiversas ttd "
            + " INNER JOIN tributo t ON ttd.tributo_id = t.id "
            + " INNER JOIN tipodivdiversatributotaxa rel ON rel.tributotaxadividasdiversas_id = ttd.id "
            + " WHERE rel.tipodividadiversa_id = :tipodividadiversa_id "
            + "   AND (t.tipoTributo = '" + Tributo.TipoTributo.TAXA.name() + "' or t.tipoTributo = '" + Tributo.TipoTributo.IMPOSTO.name() + "')"
            + "", TributoTaxaDividasDiversas.class);
        q.setParameter("tipodividadiversa_id", tipoDividaDiversa.getId());
        return (List<TributoTaxaDividasDiversas>) q.getResultList();
    }

    public String possuiRelacionamento(TipoDividaDiversa tipoDividaDiversa) {
        String retorno = "";
        String hql = "from CalculoDividaDiversa cdd where cdd.tipoDividaDiversa = :tipoDividaDiversa";
        Query q = em.createQuery(hql);
        q.setParameter("tipoDividaDiversa", tipoDividaDiversa);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno += "Lançamento Dívidas Diversas; ";
        }
        hql = "from ParametroTipoDividaDiversa ptdd where ptdd.tipoDividaDiversa = :tipoDividaDiversa";
        q = em.createQuery(hql);
        q.setParameter("tipoDividaDiversa", tipoDividaDiversa);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno += "Parâmetros do Tipo de Dívidas Diversas; ";
        }
        return retorno;
    }

    public BigDecimal recuperMaiorCodigo() {
        String sql = "SELECT max(c.codigo) + 1 FROM tipoDividaDiversa c";
        Query q = em.createNativeQuery(sql);
        BigDecimal retorno = (BigDecimal) q.getSingleResult();
        if (retorno == null) {
            return new BigDecimal(1);
        }
        return retorno;
    }

    public boolean verificaSeExisteCodigo(Integer aux) {
        String hql = "select c from TipoDividaDiversa c where c.codigo = :aux";
        Query q = em.createQuery(hql);
        q.setParameter("aux", aux);
        try {
            TipoDividaDiversa result = (TipoDividaDiversa) q.getSingleResult();
            return true;
        } catch (javax.persistence.NoResultException e) {
            return false;
        }
    }
}
