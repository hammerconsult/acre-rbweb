/*
 * Codigo gerado automaticamente em Fri Mar 04 11:23:37 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoProcesso;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoProcessoFacade;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class TipoProcessoControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private TipoProcessoFacade tipoProcessoFacade;

    public TipoProcessoControlador() {
        metadata = new EntidadeMetaData(TipoProcesso.class);
    }

    public TipoProcessoFacade getFacade() {
        return tipoProcessoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoProcessoFacade;
    }
}
