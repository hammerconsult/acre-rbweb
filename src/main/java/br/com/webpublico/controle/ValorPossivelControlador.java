/*
 * Codigo gerado automaticamente em Wed Mar 23 09:29:06 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ValorPossivel;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ValorPossivelFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class ValorPossivelControlador extends PrettyControlador<ValorPossivel> implements Serializable {

    @EJB
    private ValorPossivelFacade facade;

    public ValorPossivelControlador() {
        super(ValorPossivel.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }
}
