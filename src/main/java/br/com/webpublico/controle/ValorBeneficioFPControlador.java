/*
 * Codigo gerado automaticamente em Wed Aug 24 11:02:36 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.BeneficioFP;
import br.com.webpublico.entidades.ValorBeneficioFP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BeneficioFPFacade;
import br.com.webpublico.negocios.ValorBeneficioFPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class ValorBeneficioFPControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ValorBeneficioFPFacade valorBeneficioFPFacade;
    @EJB
    private BeneficioFPFacade beneficioFPFacade;
    private ConverterAutoComplete converterBeneficioFP;

    public ValorBeneficioFPControlador() {
        metadata = new EntidadeMetaData(ValorBeneficioFP.class);
    }

    public ValorBeneficioFPFacade getFacade() {
        return valorBeneficioFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return valorBeneficioFPFacade;
    }

    public List<SelectItem> getBeneficioFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (BeneficioFP object : beneficioFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterBeneficioFP() {
        if (converterBeneficioFP == null) {
            converterBeneficioFP = new ConverterAutoComplete(BeneficioFP.class, beneficioFPFacade);
        }
        return converterBeneficioFP;
    }

    public List<BeneficioFP> completaBeneficioFP(String parte) {
        return beneficioFPFacade.listaFiltrandoBeneficioFP(parte.trim(), "");
    }
}
