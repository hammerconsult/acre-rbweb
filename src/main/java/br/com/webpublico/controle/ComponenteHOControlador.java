/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacadeOLD;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

/**
 * @author reidocrime
 *         <p>
 *         Classe para controlar as ações dos componentes de Hierarquia Organizacional.
 */
@SessionScoped
@ManagedBean
public class ComponenteHOControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ComponenteHOControlador.class);
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacadeOLD;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private UnidadeOrganizacional unidadeOrganizacionalSelecionada;
    private TipoHierarquiaOrganizacional tipoHO;
    private transient HtmlOutputText valor;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional hoSelecionado;
    private UnidadeOrganizacional uoSelecionado;

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public Converter getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
        }
        return converterUnidadeOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        boolean temUnidade = false;
        List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte, tipoHO.name(), sistemaControlador.getDataOperacao());
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

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalEntidade(String parte) {
        boolean temUnidade = false;
        List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacadeOLD.filtrandoHierarquiaHorganizacionalEntidade(parte, sistemaControlador.getExercicioCorrente());
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

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalTipo(String parte) {
        validaTipo(valor.getValue().toString());
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte, valor.getValue().toString(), sistemaControlador.getDataOperacao());

    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalEntidadeTipo(String parte) {
        validaTipo(valor.getValue().toString());
        return hierarquiaOrganizacionalFacadeOLD.filtrandoHierarquiaOrganizacionalEntidadeTipo(parte, valor.getValue().toString());
    }

    public List<UnidadeOrganizacional> completaUnidadeOrganizacional(String parte) {
        boolean temUnidade = false;
        List<UnidadeOrganizacional> lista = unidadeOrganizacionalFacade.listaFiltrando(parte, "descricao");
        if (uoSelecionado != null) {
            for (UnidadeOrganizacional h : lista) {
                if (h.equals(uoSelecionado)) {
                    temUnidade = true;
                }
            }
            if (temUnidade == true) {
                return lista;
            } else {
                String str = uoSelecionado.getDescricao();
                if (str.toLowerCase().contains(parte.toLowerCase())) {
                    lista.add(uoSelecionado);
                }
                return lista;
            }
        } else {
            return lista;
        }
    }

    public List<UnidadeOrganizacional> completaUnidadeOrganizacionalEntidade(String parte) {
        boolean temUnidade = false;
        List<UnidadeOrganizacional> lista = unidadeOrganizacionalFacade.filtrandoUnidadeorganizacionalEntidade(parte);
        if (uoSelecionado != null) {
            for (UnidadeOrganizacional h : lista) {
                if (h.equals(uoSelecionado)) {
                    temUnidade = true;
                }
            }
            if (temUnidade == true) {
                return lista;
            } else {
                String str = uoSelecionado.getDescricao();
                if (str.toLowerCase().contains(parte.toLowerCase())) {
                    lista.add(uoSelecionado);
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

    public void actionListenerUO(SelectEvent evt) {
        unidadeOrganizacionalSelecionada = ((UnidadeOrganizacional) evt.getObject());
    }

    public void action(ActionEvent event) {
        this.tipoHO = (TipoHierarquiaOrganizacional) event.getComponent().getAttributes().get("tipoHO");
    }

    public TipoHierarquiaOrganizacional getTipoHO() {
        return tipoHO;
    }

    public void setTipoHO(TipoHierarquiaOrganizacional tipoHO) {
        this.tipoHO = tipoHO;
    }

    public HtmlOutputText getValor() {
        return valor;
    }

    public void setValor(HtmlOutputText valor) {
        this.valor = valor;
    }

    private void validaTipo(String valorTipo) {
        String v = valorTipo;
        if (valor != null) {
            v = valor.getValue().toString();
            if (v.equals("ADMINISTRATIVA")) {
                tipoHO = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
            } else if (v.equals("ORCAMENTARIA")) {
                tipoHO = TipoHierarquiaOrganizacional.ORCAMENTARIA;
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tipo da Hierarquia Organizacional não existe", " Verfique o compenente auto complete o parametro tipo");
                FacesContext.getCurrentInstance().addMessage("painelMSG", msg);
            }
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void setaHoSelecionado(HierarquiaOrganizacional hoSelecionado) {
        if (hoSelecionado != null && hoSelecionado.getSubordinada() != null) {
            ////System.out.println("Mudou de HO: "+hoSelecionado);
            this.hoSelecionado = hoSelecionado;
        }
    }

    public void setaUoSelecionado(UnidadeOrganizacional uoSelecionado) {
        if (uoSelecionado != null) {
            this.uoSelecionado = uoSelecionado;
        }
    }
}
