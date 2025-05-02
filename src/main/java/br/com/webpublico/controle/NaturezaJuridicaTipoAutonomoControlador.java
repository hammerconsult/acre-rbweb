package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoNaturezaJuridica;
import br.com.webpublico.negocios.NaturezaJuridicaFacade;
import br.com.webpublico.negocios.TipoAutonomoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 26/08/13
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "naturezaJuridicaTipoAutonomoControlador")
@ViewScoped
public class NaturezaJuridicaTipoAutonomoControlador implements Serializable {
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    private ConverterAutoComplete converterNaturezaJuridica;
    private ConverterGenerico converterTipoAutonomo;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    private transient HtmlSelectOneMenu comboTipoAutonomo;

    public HtmlSelectOneMenu getComboTipoAutonomo() {
        return comboTipoAutonomo;
    }

    public void setComboTipoAutonomo(HtmlSelectOneMenu comboTipoAutonomo) {
        this.comboTipoAutonomo = comboTipoAutonomo;
    }

    public Converter getConverterNaturezaJuridica() {
        if (converterNaturezaJuridica == null) {
            converterNaturezaJuridica = new ConverterAutoComplete(NaturezaJuridica.class, naturezaJuridicaFacade);
        }
        return converterNaturezaJuridica;
    }

    public Converter getConverterTipoAutonomo() {
        if (converterTipoAutonomo == null) {
            converterTipoAutonomo = new ConverterGenerico(TipoAutonomo.class, tipoAutonomoFacade);
        }
        return converterTipoAutonomo;
    }

    private TipoNaturezaJuridica retornaTipoNaturezaJuridica(Pessoa pessoa) {
        if (pessoa != null) {
            if (pessoa instanceof PessoaFisica) {
                return TipoNaturezaJuridica.FISICA;
            }
            return TipoNaturezaJuridica.JURIDICA;
        }
        return null;
    }

    public List<SelectItem> listaNaturezasJuridica(Pessoa pessoa, Boolean somenteAutonomo) {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, ""));
        List<NaturezaJuridica> listaNaturezaJuridica = new ArrayList<>();
        if ((somenteAutonomo != null && somenteAutonomo) || (pessoa != null && pessoa instanceof PessoaFisica)) {
            listaNaturezaJuridica = naturezaJuridicaFacade.listaNaturezaJuridicaAutonomo();
        } else {
            listaNaturezaJuridica = naturezaJuridicaFacade.listaNaturezaJuridicaPorTipo(retornaTipoNaturezaJuridica(pessoa));
        }
        for (NaturezaJuridica natureza : listaNaturezaJuridica) {
            toReturn.add(new SelectItem(natureza, natureza.getCodigo() + " - " + natureza.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> listaNaturezasJuridica() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, ""));
        List<NaturezaJuridica> listaNaturezaJuridica = new ArrayList<>();
        listaNaturezaJuridica = naturezaJuridicaFacade.listaNaturezaJuridicaAutonomo();
        for (NaturezaJuridica natureza : listaNaturezaJuridica) {
            toReturn.add(new SelectItem(natureza, natureza.getCodigo() + " - " + natureza.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> listaTiposAutonomos() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAutonomo tipo : tipoAutonomoFacade.lista()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public void defineNulo() {
        if (comboTipoAutonomo != null) {
            comboTipoAutonomo.setValue("");
        }
    }
}
