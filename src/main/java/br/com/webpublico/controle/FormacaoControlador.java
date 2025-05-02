package br.com.webpublico.controle;

import br.com.webpublico.entidades.Formacao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by AndreGustavo on 26/09/2014.
 */
@ManagedBean
@ViewScoped
public class FormacaoControlador extends PrettyControlador<Formacao> {
    public FormacaoControlador() {
        super(Formacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return null;
    }
}
