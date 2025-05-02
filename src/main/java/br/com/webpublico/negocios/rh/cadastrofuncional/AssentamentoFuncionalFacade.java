package br.com.webpublico.negocios.rh.cadastrofuncional;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.cadastrofuncional.AssentamentoFuncional;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class AssentamentoFuncionalFacade extends AbstractFacade<AssentamentoFuncional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AssentamentoFuncionalFacade() {
        super(AssentamentoFuncional.class);
    }

    public Long ultimoNumeroMaisUm(ContratoFP contratoFP) {
        Query q = em.createNativeQuery("select coalesce(max(sequencial), 0) + 1 as numero " +
            "from ASSENTAMENTOFUNCIONAL where CONTRATOFP_ID = :idContrato");
        q.setParameter("idContrato", contratoFP.getId());
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();

    }

    public List<AssentamentoFuncional> buscarAssentamentosFuncionaisPorContrato(ContratoFP contrato) {
        String sql = " select assentamento.* from AssentamentoFuncional assentamento " +
            " where assentamento.CONTRATOFP_ID = :contrato ";
        Query q = em.createNativeQuery(sql, AssentamentoFuncional.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
