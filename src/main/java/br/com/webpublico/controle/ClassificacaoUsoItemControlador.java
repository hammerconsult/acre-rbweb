package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClassificacaoUsoItem;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ClassificacaoUsoItemFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ClassificacaoUsoItemControlador extends PrettyControlador<ClassificacaoUsoItem> {

    @EJB
    private ClassificacaoUsoItemFacade facade;

    public ClassificacaoUsoItemControlador() {
        super(ClassificacaoUsoItem.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }
}
