package br.com.webpublico.controle.rh.pccr;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.pccr.ReferenciaProgressaoPCCR;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.pccr.ReferenciaProgressaoPCCRFacade;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 27/06/14
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ReferenciaProgressaoPCCRControlador extends PrettyControlador<ReferenciaProgressaoPCCR> implements Serializable, CRUD {
    @EJB
    private ReferenciaProgressaoPCCRFacade referenciaProgressaoPCCRFacade;

    public ReferenciaProgressaoPCCRControlador() {
        super(ReferenciaProgressaoPCCR.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/referenciaprogressaopccr/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AbstractFacade getFacede() {
        return referenciaProgressaoPCCRFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
