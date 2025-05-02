/*
 * Codigo gerado automaticamente em Wed Oct 17 17:16:18 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoContaExtra;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoContaExtraFacade;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class TipoContaExtraControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private TipoContaExtraFacade tipoContaExtraFacade;

    public TipoContaExtraControlador() {
        metadata = new EntidadeMetaData(TipoContaExtra.class);
    }

    public TipoContaExtraFacade getFacade() {
        return tipoContaExtraFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoContaExtraFacade;
    }
}
