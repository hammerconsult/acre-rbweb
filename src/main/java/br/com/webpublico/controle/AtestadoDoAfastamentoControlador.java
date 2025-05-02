/*
 * Codigo gerado automaticamente em Thu Sep 22 14:45:16 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Afastamento;
import br.com.webpublico.entidades.AtestadoDoAfastamento;
import br.com.webpublico.entidades.Medico;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AfastamentoFacade;
import br.com.webpublico.negocios.AtestadoDoAfastamentoFacade;
import br.com.webpublico.negocios.MedicoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.enums.Operacoes;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class AtestadoDoAfastamentoControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private AtestadoDoAfastamentoFacade atestadoDoAfastamentoFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    private ConverterAutoComplete converterAfastamento;
    @EJB
    private MedicoFacade atestadoMedicoFacade;
    private ConverterAutoComplete converterAtestadoMedico;

    public AtestadoDoAfastamentoControlador() {
        metadata = new EntidadeMetaData(AtestadoDoAfastamento.class);
    }

    public AtestadoDoAfastamentoFacade getFacade() {
        return atestadoDoAfastamentoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return atestadoDoAfastamentoFacade;
    }

    public List<SelectItem> getAfastamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Afastamento object : afastamentoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterAfastamento() {
        if (converterAfastamento == null) {
            converterAfastamento = new ConverterAutoComplete(Afastamento.class, afastamentoFacade);
        }
        return converterAfastamento;
    }

    public List<SelectItem> getAtestadoMedico() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Medico object : atestadoMedicoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterAtestadoMedico() {
        if (converterAtestadoMedico == null) {
            converterAtestadoMedico = new ConverterAutoComplete(Medico.class, atestadoMedicoFacade);
        }
        return converterAtestadoMedico;
    }

    public List<Afastamento> completaTipoAfastamento(String parte) {
        return afastamentoFacade.listaFiltrandoAtributosAfastamento(parte);
    }

    public List<Medico> completaAtestadoMedico(String parte) {
        return atestadoMedicoFacade.listaFiltrandoMedico(parte.trim());
    }

    @Override
    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        AtestadoDoAfastamento atestadoDoAfastamento = (AtestadoDoAfastamento) evento.getComponent().getAttributes().get("objeto");
        selecionado = atestadoDoAfastamentoFacade.recuperar(atestadoDoAfastamento.getId());
    }


}
