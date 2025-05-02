package br.com.webpublico.relatoriofacade;

import br.com.webpublico.negocios.ReferenciaAnualFacade;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class RelatorioRREOAnexo06NovoFacade extends RelatorioItemDemonstrativoCalculador {

    @EJB
    private ReferenciaAnualFacade referenciaAnualFacade;

    public ReferenciaAnualFacade getReferenciaAnualFacade() {
        return referenciaAnualFacade;
    }
}
