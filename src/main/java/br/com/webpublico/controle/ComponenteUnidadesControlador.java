/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

/**
 * @author venon
 */
@SessionScoped
@ManagedBean
public class ComponenteUnidadesControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ComponenteHOControlador.class);
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private String nivelHierarquia;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional hoSelecionado;

    public String getNivelHierarquia() {
        return nivelHierarquia;
    }

    public void setNivelHierarquia(String nivelHierarquia) {
        this.nivelHierarquia = nivelHierarquia;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        boolean temUnidade = false;
        List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacade.filtraPorNivel(parte, nivelHierarquia, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
        if (hoSelecionado != null) {
            for (HierarquiaOrganizacional h : lista) {
                if (h.getSubordinada().equals(hoSelecionado.getSubordinada())) {
                    temUnidade = true;
                }
            }
            if (temUnidade == true) {
                return lista;
            } else {
                String str = hoSelecionado.getSubordinada().getDescricao() + hoSelecionado.getCodigo();
                if (str.toLowerCase().contains(parte.toLowerCase())) {
                    lista.add(hoSelecionado);
                }
                return lista;
            }
        } else {
            return lista;
        }
    }

    public void actionListener(SelectEvent evt) {
        hierarquiaOrganizacionalSelecionada = ((HierarquiaOrganizacional) evt.getObject());
    }

    public void setaNivel(String nivel) {
        this.nivelHierarquia = nivel;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void setaHoSelecionado(HierarquiaOrganizacional hoSelecionado) {
        if (hoSelecionado != null && hoSelecionado.getSubordinada() != null) {
            this.hoSelecionado = hoSelecionado;
        }
    }

}
