package br.com.webpublico.controle;

import br.com.webpublico.entidades.Seguradora;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SeguradoraFacade;
import br.com.webpublico.util.ConverterAutoComplete;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Alex on 04/05/2017.
 */
@ManagedBean
@ViewScoped
public class SeguradoraControlador extends PrettyControlador<Seguradora> implements Serializable {

    @EJB
    private SeguradoraFacade seguradoraFacade;
    private Boolean consultarSeguradora;
    private ConverterAutoComplete converterSeguradora;

    public SeguradoraControlador() {
        consultarSeguradora = Boolean.FALSE;

    }

    @Override
    public AbstractFacade getFacede() {
        return seguradoraFacade;
    }

    public void exibirAutoCompleteSeguradora() {
        consultarSeguradora = Boolean.TRUE;
    }

    public void exibirNovaSeguradora() {
        consultarSeguradora = Boolean.FALSE;
    }

    public List<Seguradora> buscarSeguradoras(String partes) {
        return seguradoraFacade.buscarSeguradoras(partes);
    }

    public ConverterAutoComplete getConverterSeguradora() {
        if (converterSeguradora == null) {
            converterSeguradora = new ConverterAutoComplete(Seguradora.class, seguradoraFacade);
        }
        return converterSeguradora;
    }

    public Boolean getConsultarSeguradora() {
        return consultarSeguradora;
    }

    public void setConsultarSeguradora(Boolean consultarSeguradora) {
        this.consultarSeguradora = consultarSeguradora;
    }
}
