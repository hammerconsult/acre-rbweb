package br.com.webpublico.relatoriofacade;

import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 16/07/14
 * Time: 08:03
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RelatorioDemonstrativoDisponibilidadeRecursoFacade extends RelatorioItemDemonstrativoCalculador implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
