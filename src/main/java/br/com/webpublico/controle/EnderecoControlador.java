/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.negocios.BairroFacade;
import br.com.webpublico.negocios.CidadeFacade;
import br.com.webpublico.negocios.LogradouroFacade;
import br.com.webpublico.util.ConverterAutoComplete;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author terminal4
 */
@ManagedBean
@SessionScoped
public class EnderecoControlador implements Serializable {

    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;
    private ConverterAutoComplete converterCidade;
    private ConverterAutoComplete converterBairro;
    private ConverterAutoComplete converterLogradouro;

    public List<Cidade> completaCidade(String parte) {
        return cidadeFacade.listaFiltrando(parte.trim(), "nome");
    }

    public Converter getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterAutoComplete(Cidade.class, cidadeFacade);
        }
        return converterCidade;
    }

    public List<Logradouro> completa(String parte) {
        return logradouroFacade.listaFiltrando(parte.trim(), "nome", "bairro");
    }

    public List<Bairro> completaBairro(String parte) {
        return bairroFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public Converter getConverterLogradouro() {
        if (converterLogradouro == null) {
            converterLogradouro = new ConverterAutoComplete(Logradouro.class, logradouroFacade);
        }
        return converterLogradouro;
    }

    public Converter getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(Bairro.class, bairroFacade);
        }
        return converterBairro;
    }

    public List<SelectItem> getTipoEndereco() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEndereco object : TipoEndereco.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }
}
