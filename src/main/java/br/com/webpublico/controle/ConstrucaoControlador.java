package br.com.webpublico.controle;

import br.com.webpublico.entidades.Construcao;
import br.com.webpublico.negocios.ConstrucaoFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ConstrucaoControlador extends PrettyControlador<Construcao> {

    @EJB
    private ConstrucaoFacade facade;

    public ConstrucaoControlador() {
        super(Construcao.class);
    }

    @Override
    public ConstrucaoFacade getFacede() {
        return facade;
    }
}
