package br.com.webpublico.singletons;

import br.com.webpublico.enums.TipoReducaoValorBem;
import com.google.common.collect.Maps;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonGeradorCodigoReducaoValorBem implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<TipoReducaoValorBem, Long> mapCodigoPorTipo;

    @PostConstruct
    public void init() {
        preencherMapa();
    }
    @Lock(LockType.READ)
    public void preencherMapa() {
        mapCodigoPorTipo = Maps.newHashMap();
        String sql = " select tiporeducao, max(codigo) " +
            "   from lotereducaovalorbem " +
            "group by tiporeducao ";
        Query q = em.createNativeQuery(sql);
        if (q.getResultList() != null) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                mapCodigoPorTipo.put(TipoReducaoValorBem.valueOf((String) obj[0]), ((BigDecimal) obj[1]).longValue());
            }
        }
    }

    @Lock(LockType.READ)
    public Long getProximoCodigo(TipoReducaoValorBem tipoReducaoValorBem) {
        Long ultimoCodigo = mapCodigoPorTipo.get(tipoReducaoValorBem);
        if (ultimoCodigo == null) {
            ultimoCodigo = 0l;
        }
        ultimoCodigo += 1;
        mapCodigoPorTipo.put(tipoReducaoValorBem, ultimoCodigo);
        return ultimoCodigo;
    }
}
