/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.DePara;
import br.com.webpublico.entidades.DeParaItem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DeParaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author reidocrime
 */
@ManagedBean(name = "deParaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-depara", pattern = "/depara/novo/", viewId = "/faces/ferramentas/depara/edita.xhtml"),
    @URLMapping(id = "editar-depara", pattern = "/depara/editar/#{deParaControlador.id}/", viewId = "/faces/ferramentas/depara/edita.xhtml"),
    @URLMapping(id = "ver-depara", pattern = "/depara/ver/#{deParaControlador.id}/", viewId = "/faces/ferramentas/depara/visualizar.xhtml"),
    @URLMapping(id = "listar-depara", pattern = "/depara/listar/", viewId = "/faces/ferramentas/depara/lista.xhtml")
})
public class DeParaControlador extends PrettyControlador<DePara> implements Serializable, CRUD {

    @EJB
    private DeParaFacade deParaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Date dataVigenteAdmNova;
    private Date dataVigenteAdmAntiga;
    private Date dataVigenteOrcAntiga;
    private Integer paginador;
    private HierarquiaOrganizacional hoVigenteAdmNova;
    private HierarquiaOrganizacional hoVigenteAdmAntiga;
    private HierarquiaOrganizacional hoVigenteOrcAntiga;
    private List<HierarquiaOrganizacional> listaHoVigenteAdmAntiga;
    private List<HierarquiaOrganizacional> listaHoVigenteOrcAntiga;

    public DeParaControlador() {
        super(DePara.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return deParaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/depara/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "ver-depara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editar-depara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        selecionado = deParaFacade.recuperar(super.getId());
        paginador = 50;
        listaHoVigenteAdmAntiga = new ArrayList<>();
        listaHoVigenteOrcAntiga = new ArrayList<>();
    }

    @Override
    @URLAction(mappingId = "novo-depara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        paginador = 50;
        listaHoVigenteAdmAntiga = new ArrayList<>();
        listaHoVigenteOrcAntiga = new ArrayList<>();
        super.novo();

    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Date getDataVigenteAdmNova() {
        return dataVigenteAdmNova;
    }

    public void setDataVigenteAdmNova(Date dataVigenteAdmNova) {
        this.dataVigenteAdmNova = dataVigenteAdmNova;
    }

    public Date getDataVigenteAdmAntiga() {
        return dataVigenteAdmAntiga;
    }

    public void setDataVigenteAdmAntiga(Date dataVigenteAdmAntiga) {
        this.dataVigenteAdmAntiga = dataVigenteAdmAntiga;
    }

    public Date getDataVigenteOrcAntiga() {
        return dataVigenteOrcAntiga;
    }

    public void setDataVigenteOrcAntiga(Date dataVigenteOrcAntiga) {
        this.dataVigenteOrcAntiga = dataVigenteOrcAntiga;
    }

    public Integer getPaginador() {
        return paginador;
    }

    public void setPaginador(Integer paginador) {
        this.paginador = paginador;
    }

    public HierarquiaOrganizacional getHoVigenteAdmNova() {
        return hoVigenteAdmNova;
    }

    public void setHoVigenteAdmNova(HierarquiaOrganizacional hoVigenteAdmNova) {
        this.hoVigenteAdmNova = hoVigenteAdmNova;
    }

    public HierarquiaOrganizacional getHoVigenteAdmAntiga() {
        return hoVigenteAdmAntiga;
    }

    public void setHoVigenteAdmAntiga(HierarquiaOrganizacional hoVigenteAdmAntiga) {
        this.hoVigenteAdmAntiga = hoVigenteAdmAntiga;
    }

    public HierarquiaOrganizacional getHoVigenteOrcAntiga() {
        return hoVigenteOrcAntiga;
    }

    public void setHoVigenteOrcAntiga(HierarquiaOrganizacional hoVigenteOrcAntiga) {
        this.hoVigenteOrcAntiga = hoVigenteOrcAntiga;
    }

    public List<HierarquiaOrganizacional> hierarquiasNovasAdm() {
        List<HierarquiaOrganizacional> toReturn = new ArrayList<>();
        try {
            if (dataVigenteAdmNova != null) {
                toReturn = hierarquiaOrganizacionalFacade.listaSimplesFiltrandoPorDataTipo(dataVigenteAdmNova, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError("Erro: ", ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Erro: ", e.getMessage());
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> hierarquiasAntigasOrc() {
        List<HierarquiaOrganizacional> toReturn = new ArrayList<>();
        try {
            if (dataVigenteOrcAntiga != null) {
                toReturn = deParaFacade.listaSimplesFiltrandoPorDataTipo(dataVigenteOrcAntiga, TipoHierarquiaOrganizacional.ORCAMENTARIA, selecionado);
                toReturn.removeAll(listaHoVigenteOrcAntiga);
            }

        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError("Erro: ", ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Erro: ", e.getMessage());
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> hierarquiasAntigasAdm() {
        List<HierarquiaOrganizacional> toReturn = new ArrayList<>();
        try {
            if (dataVigenteAdmAntiga != null) {
                toReturn = deParaFacade.listaSimplesFiltrandoPorDataTipo(dataVigenteAdmAntiga, TipoHierarquiaOrganizacional.ADMINISTRATIVA, selecionado);
                toReturn.removeAll(listaHoVigenteAdmAntiga);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError("Erro: ", ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Erro: ", e.getMessage());
        }
        return toReturn;
    }

    public void adiconaItemDePara() {
        DePara dePara = (DePara) selecionado;

        if (validaAddItem()) {
            DeParaItem it = new DeParaItem();
            it.setDePara(dePara);
            it.setUnidadeAdmNova(hoVigenteAdmNova.getSubordinada());
            it.setCodigoAdmNova(hoVigenteAdmNova.getCodigo());
            if (hoVigenteAdmAntiga != null) {
                it.setCodigoAdmAntiga(hoVigenteAdmAntiga.getCodigo());
                it.setUnidadeAdmAntiga(hoVigenteAdmAntiga.getSubordinada());
                listaHoVigenteAdmAntiga.add(hoVigenteAdmAntiga);
            }
            if (hoVigenteOrcAntiga != null) {
                it.setCodigoOrcAntiga(hoVigenteOrcAntiga.getCodigo());
                it.setUnidadeOrcAntiga(hoVigenteOrcAntiga.getSubordinada());
                listaHoVigenteOrcAntiga.add(hoVigenteOrcAntiga);
            }
            dePara.getDeParaItems().add(it);
        }
        hoVigenteAdmAntiga = null;
        hoVigenteAdmNova = null;
        hoVigenteOrcAntiga = null;
    }

    public boolean validaAddItem() {

        if (hoVigenteAdmNova != null && (hoVigenteAdmAntiga != null || hoVigenteOrcAntiga != null)) {
            return true;
        } else {
            FacesUtil.addError("Erro: ", "Deve ser selecionado pelo menos um item Administrativo Novo e um item Administrativo antigo ou Orçamentario Antigo");
            return false;
        }
    }

    @Override
    public void salvar() {
        if (!selecionado.getDeParaItems().isEmpty()) {
            super.salvar();
        } else {
            FacesUtil.addInfo("Problema ao Salvar.", "Deve existir pelo menos um Item na Lista!");
        }
    }

    public void removerItem(DeParaItem item) {
        selecionado.getDeParaItems().remove(item);
    }
}
