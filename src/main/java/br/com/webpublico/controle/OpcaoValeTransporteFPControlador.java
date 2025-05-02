/*
 * Codigo gerado automaticamente em Fri Sep 16 09:00:47 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.OpcaoValeTransporteFP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.OpcaoValeTransporteFPFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class OpcaoValeTransporteFPControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private OpcaoValeTransporteFPFacade opcaoValeTransporteFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterGenerico converterContratoFP;

    public OpcaoValeTransporteFPControlador() {
        metadata = new EntidadeMetaData(OpcaoValeTransporteFP.class);
    }

    public OpcaoValeTransporteFPFacade getFacade() {
        return opcaoValeTransporteFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return opcaoValeTransporteFPFacade;
    }

    public List<SelectItem> getContratoFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ContratoFP object : contratoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getMatriculaFP().getPessoa().getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterGenerico(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }
}
