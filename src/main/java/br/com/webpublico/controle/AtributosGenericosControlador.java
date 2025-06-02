/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Atributo;
import br.com.webpublico.entidades.GrupoAtributo;
import br.com.webpublico.entidades.ValorAtributo;
import br.com.webpublico.entidades.ValorPossivel;
import br.com.webpublico.entidadesauxiliares.ComponenteAtributoGenerico;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.negocios.AtributoFacade;
import br.com.webpublico.negocios.GrupoAtributoFacade;
import br.com.webpublico.negocios.ValorPossivelFacade;
import br.com.webpublico.util.ConverterGenerico;
import com.google.common.collect.Maps;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Munif
 */
@ManagedBean
@RequestScoped
public class AtributosGenericosControlador implements Serializable {

    @EJB
    private GrupoAtributoFacade grupoAtributoFacade;
    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private ValorPossivelFacade valorPossivelFacade;
    private ConverterGenerico converterValorPossivel;
    private List<Atributo> listaAtributos;
    private Map<String, ComponenteAtributoGenerico> mapComponente;

    public AtributosGenericosControlador() {
        listaAtributos = new ArrayList<Atributo>();
        mapComponente = Maps.newHashMap();
    }

    public List<Atributo> getListaAtributos() {
        return listaAtributos;
    }

    public boolean isListaVazia() {
        return listaAtributos.isEmpty();
    }

    public List<GrupoAtributo> buscarGrupoAtributoPorClasse(ClasseDoAtributo classeDoAtributo) {
        return grupoAtributoFacade.buscarGrupoAtributoPorClasse(classeDoAtributo);
    }

    public List<Atributo> buscarAtributoPorGrupoAtributo(String clientId, GrupoAtributo grupoAtributo) {
        return mapComponente.get(clientId).getAtributos().stream()
            .filter(a -> a.getGrupoAtributo().equals(grupoAtributo)).collect(Collectors.toList());
    }

    public Boolean isEmpty(String clientId) {
        return mapComponente.get(clientId).getAtributos().isEmpty();
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

    public List<GrupoAtributo> initComponent(String clientId,
                                             ClasseDoAtributo classeDoAtributo,
                                             Map<Atributo, ValorAtributo> atributos) {
        if (mapComponente.get(clientId) != null) {
            return mapComponente.get(clientId).getGruposAtributo();
        }
        ComponenteAtributoGenerico componente = new ComponenteAtributoGenerico();
        componente.setAtributos(atributoFacade.listaAtributosPorClasse(classeDoAtributo));
        List<GrupoAtributo> gruposAtributo = componente.getAtributos().stream().map(Atributo::getGrupoAtributo)
            .distinct().collect(Collectors.toList());
        gruposAtributo.sort(Comparator.comparing(GrupoAtributo::getCodigo));
        componente.setGruposAtributo(gruposAtributo);
        if (atributos == null) {
            atributos = Maps.newHashMap();
        }
        for (Atributo atributo : componente.getAtributos()) {
            atributos.putIfAbsent(atributo, new ValorAtributo());
        }
        mapComponente.put(clientId, componente);
        return gruposAtributo;
    }
}
