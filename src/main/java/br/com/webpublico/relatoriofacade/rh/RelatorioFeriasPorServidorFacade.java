package br.com.webpublico.relatoriofacade.rh;

import br.com.webpublico.entidadesauxiliares.rh.VOFeriasPorServidor;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by israeleriston on 16/12/15.
 */
@Stateless
public class RelatorioFeriasPorServidorFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
