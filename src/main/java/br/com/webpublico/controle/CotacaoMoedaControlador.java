/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CotacaoMoeda;
import br.com.webpublico.entidades.Moeda;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CotacaoMoedaFacade;
import br.com.webpublico.negocios.MoedaFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.MoneyConverter;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author terminal4
 */
@ManagedBean
@SessionScoped
public class CotacaoMoedaControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private CotacaoMoedaFacade facade;
    @EJB
    private MoedaFacade moedaFacade;
    protected ConverterGenerico converterMoeda;
    private MoneyConverter moneyConverter;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public CotacaoMoedaControlador() {
        metadata = new EntidadeMetaData(CotacaoMoeda.class);
    }

    public List<SelectItem> getMoeda() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (Moeda object : moedaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterMoeda() {
        if (converterMoeda == null) {
            converterMoeda = new ConverterGenerico(Moeda.class, moedaFacade);
        }
        return converterMoeda;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }
}
