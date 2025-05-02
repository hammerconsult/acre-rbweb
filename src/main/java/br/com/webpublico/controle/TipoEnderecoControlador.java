/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoEnderecoFacade;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * @author terminal4
 */
@ManagedBean
@SessionScoped
public class TipoEnderecoControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private TipoEnderecoFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public TipoEnderecoControlador() {
        metadata = new EntidadeMetaData(TipoEndereco.class);
    }

}
