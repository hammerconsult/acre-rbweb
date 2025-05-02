/*
 * Codigo gerado automaticamente em Fri Feb 11 08:09:57 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BiAgrupadorTaxa;
import br.com.webpublico.entidadesauxiliares.VOTributoBi;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class BiAgrupadorTaxaFacade extends AbstractFacade<BiAgrupadorTaxa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public BiAgrupadorTaxaFacade() {
        super(BiAgrupadorTaxa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public BiAgrupadorTaxa recuperar(Object id) {
        BiAgrupadorTaxa agrupador = super.recuperar(id);
        Hibernate.initialize(agrupador.getTributos());
        return agrupador;
    }

    public Long buscarUltimoNumeroMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(codigo), 0) + 1 as numero from BiAgrupadorTaxa");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public List<VOTributoBi> buscarTributosAgrupados() {
        String sql = "select codigo, descricao from (" +
            "select distinct coalesce(agr.codigo, tr.codigo) as codigo, coalesce(agr.descricao, tr.descricao) as descricao " +
            "from tributo tr " +
            "left join BIAGRUPADORTAXATRIBUTO btr on btr.tributo_id = tr.id " +
            "left join BIAGRUPADORTAXA agr on agr.ID = btr.BIAGRUPADORTAXA_ID) " +
            "order by codigo";
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultList = q.getResultList();
        List<VOTributoBi> retorno = Lists.newArrayList();
        for (Object[] o : resultList) {
            VOTributoBi vo = new VOTributoBi();
            vo.setCodigo(o[0] != null ? ((BigDecimal)o[0]).longValue() : null);
            vo.setDescricao(o[1] != null ? (String)o[1] : "");
            retorno.add(vo);
        }
        return retorno;
    }
}
