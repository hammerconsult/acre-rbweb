package br.com.webpublico.util;

import br.com.webpublico.entidades.Cotacao;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by hudson on 13/11/15.
 */

@Singleton
@ConcurrencyManagement
public class GeradorCodigoCotacao implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Lock(LockType.READ)
    public Integer buscarNumeroCotacaoPorSecretaria(Cotacao cotacao) {
        HierarquiaOrganizacional hierarquiaOrganizacional =
            hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(cotacao.getUnidadeOrganizacional(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA,
                cotacao.getDataCotacao());

        String sql = " select COALESCE(MAX(c.NUMERO), 0) as numero from COTACAO c " +
            " inner join UNIDADEORGANIZACIONAL uo on uo.ID = c.UNIDADEORGANIZACIONAL_ID " +
            " inner join HIERARQUIAORGANIZACIONAL ho on ho.subordinada_id = uo.ID " +
            " where c.EXERCICIO_ID = :idExercicio " +
            " and substr(ho.codigo,0,5) = :codigoSecretaria " +
            " and to_date(:dataVigencia, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:dataVigencia, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", cotacao.getExercicio().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(cotacao.getDataCotacao()));
        q.setParameter("codigoSecretaria", hierarquiaOrganizacional.getCodigo().substring(0, 5));
        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }


}
