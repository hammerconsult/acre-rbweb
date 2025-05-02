package br.com.webpublico.controle;

import br.com.webpublico.entidades.ExameComplementar;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExameComplementarFacade;

import javax.ejb.EJB;

/**
 * Created by carlos on 23/06/15.
 */
public class ExameComplementarControlador extends PrettyControlador<ExameComplementar> implements CRUD {

    @EJB
    private ExameComplementarFacade exameComplementarFacade;

    public ExameComplementarControlador() {
        super(ExameComplementar.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return null;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return exameComplementarFacade;
    }

    @Override
    public void novo() {
        super.novo();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    @Override
    public void editar() {
        super.editar();
    }
}
