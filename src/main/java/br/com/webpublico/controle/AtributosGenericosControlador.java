/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Atributo;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.entidades.ValorPossivel;
import br.com.webpublico.negocios.AtributoFacade;
import br.com.webpublico.negocios.ValorPossivelFacade;
import br.com.webpublico.util.ConverterGenerico;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
@ManagedBean
@RequestScoped
public class AtributosGenericosControlador implements Serializable {

    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private ValorPossivelFacade valorPossivelFacade;
    private ConverterGenerico converterValorPossivel;
    private List<Atributo> listaAtributos;

    public AtributosGenericosControlador() {
        listaAtributos = new ArrayList<Atributo>();
    }

    public boolean isListaVazia() {
        return listaAtributos.isEmpty();
    }

    public List<Atributo> listaAtributosPorClasse(ClasseDoAtributo classeDoAtributo) {
        listaAtributos = atributoFacade.listaAtributosPorClasse(classeDoAtributo);
        return listaAtributos;
    }

    public List<Atributo> listaAtributosPorClasseTabela(ClasseDoAtributo classeDoAtributo) {
        listaAtributos = atributoFacade.listaAtributosPorClasse(classeDoAtributo);
        return listaAtributos;
    }

    public Converter getConverterValorPossivel() {
        if (converterValorPossivel == null) {
            converterValorPossivel = new ConverterGenerico(ValorPossivel.class, valorPossivelFacade);
        }
        return converterValorPossivel;
    }
}
