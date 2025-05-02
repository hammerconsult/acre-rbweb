package br.com.webpublico.controle.comum;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.negocios.comum.FormularioCampoFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class FormularioCampoControlador extends PrettyControlador<FormularioCampo> implements Serializable {

    @EJB
    private FormularioCampoFacade facade;

    public FormularioCampoControlador() {
        super(FormularioCampo.class);
    }

    @Override
    public FormularioCampoFacade getFacede() {
        return facade;
    }

}
