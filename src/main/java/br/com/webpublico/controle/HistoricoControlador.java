/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;


import br.com.webpublico.entidades.Historico;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.HistoricoFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author leonardo
 */
@ManagedBean
@SessionScoped
public class HistoricoControlador implements Serializable {

    @EJB
    private HistoricoFacade historicoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;

    public HistoricoControlador() {

    }

    public List<Historico> getLista() {
        return this.historicoFacade.lista();
    }

}
