/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean
@SessionScoped
public class ComponenteCadastroInicialCadastroFinalControlador implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(ComponenteCadastroInicialCadastroFinalControlador.class);

    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterAutoComplete converterPessoa;
    private String tipoCadastro;
    private Integer tamanhoMaximo;
    private String idFormulario;

    public String getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(String idFormulario) {
        this.idFormulario = idFormulario;
    }

    public List<SelectItem> tiposCadastros(boolean mostrarContribuinte, String tipo) {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tpCad : TipoCadastroTributario.values()) {

            if (tipo != null && !tipo.isEmpty()) {
                if (tpCad.getDescricao().toLowerCase().equals(tipo.toLowerCase())) {
                    lista.add(new SelectItem(tpCad, tpCad.getDescricao()));
                }
            } else {
                if (mostrarContribuinte || !tpCad.equals(TipoCadastroTributario.PESSOA)) {
                    lista.add(new SelectItem(tpCad, tpCad.getDescricao()));
                }
            }
        }
        return lista;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public String getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(String tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public void processarTamanhoMaximoInput(AjaxBehaviorEvent ev) {
        TipoCadastroTributario tpCadastro = (TipoCadastroTributario) ((HtmlSelectOneMenu) ev.getComponent()).getValue();
        if (tpCadastro != null) {
            if (tpCadastro.equals(TipoCadastroTributario.IMOBILIARIO) || tpCadastro.equals(TipoCadastroTributario.RURAL)) {
                tamanhoMaximo = 15;
            } else if (tpCadastro.equals(TipoCadastroTributario.ECONOMICO)) {
                tamanhoMaximo = 14;
            } else {
                tamanhoMaximo = 50;
            }
        }
    }

    public Integer getTamanhoMaximo() {
        return tamanhoMaximo;
    }

    public void setTamanhoMaximo(Integer tamanhoMaximo) {
        this.tamanhoMaximo = tamanhoMaximo;
    }

    public void recuperaIdFormulario(ComponentSystemEvent evento) {
        String id = (evento.getComponent()).getClientId();
        String[] split = id.split(":");
        idFormulario = split[0];
        ////System.out.println("idFormulario..: " + idFormulario);
    }

    public UIComponent getForm(UIComponent comp) {
        if (comp == null || comp.getParent() == null) {
            return null;
        } else {
            if (comp instanceof HtmlForm) {
                return comp;
            }
        }
        return getForm(comp.getParent());
    }

    public void itemChange(){
       logger.debug("trocou o cadastro");
    }
}
