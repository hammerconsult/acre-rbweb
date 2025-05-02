/*
 * Codigo gerado automaticamente em Fri Mar 04 11:02:44 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "subAssuntoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoSubAssunto", pattern = "/subassunto/novo/", viewId = "/faces/tributario/cadastromunicipal/subassunto/edita.xhtml"),
        @URLMapping(id = "editarSubAssunto", pattern = "/subassunto/editar/#{subAssuntoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/subassunto/edita.xhtml"),
        @URLMapping(id = "listarSubAssunto", pattern = "/subassunto/listar/", viewId = "/faces/tributario/cadastromunicipal/subassunto/lista.xhtml"),
        @URLMapping(id = "verSubAssunto", pattern = "/subassunto/ver/#{subAssuntoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/subassunto/visualizar.xhtml")
})
public class SubAssuntoControlador extends PrettyControlador<SubAssunto> implements Serializable, CRUD {

    @EJB
    private SubAssuntoFacade subAssuntoFacade;
    protected ConverterGenerico converterDocumento;
    protected ConverterGenerico converterAssunto;
    private ItemRota itemRota;
    private TreeNode root;
    private TreeNode noSelecionado;
    private HierarquiaOrganizacional unidadeSelecionada;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private SubAssuntoDocumento subAssuntoDocumento;
    private Documento documento;
    private TreeNode rootOrc;
    private TreeNode selectedNode;

    public SubAssuntoControlador() {
        super(SubAssunto.class);
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getRootOrc() {
        return rootOrc;
    }

    public void setRootOrc(TreeNode rootOrc) {
        this.rootOrc = rootOrc;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public SubAssuntoDocumento getSubAssuntoDocumento() {
        return subAssuntoDocumento;
    }

    public void setSubAssuntoDocumento(SubAssuntoDocumento subAssuntoDocumento) {
        this.subAssuntoDocumento = subAssuntoDocumento;
    }

    @URLAction(mappingId = "novoSubAssunto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setItensRota(new ArrayList<ItemRota>());
        itemRota = new ItemRota();
        unidadeSelecionada = new HierarquiaOrganizacional();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        subAssuntoDocumento = new SubAssuntoDocumento();
        recuperaArvoreHierarquiaOrganizacionalOrc();
    }

    public ItemRota getItemRota() {
        return itemRota;
    }

    public void setItemRota(ItemRota itemRota) {
        this.itemRota = itemRota;
    }

    public SubAssuntoFacade getFacade() {
        return subAssuntoFacade;
    }

    public TreeNode getNoSelecionado() {
        return noSelecionado;
    }

    public void setNoSelecionado(TreeNode noSelecionado) {
        this.noSelecionado = noSelecionado;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    @Override
    public AbstractFacade getFacede() {
        return subAssuntoFacade;
    }

    public void removeItens(ActionEvent e) {
        selecionado.getItensRota().remove((ItemRota) e.getComponent().getAttributes().get("iten"));
    }

    @URLAction(mappingId = "editarSubAssunto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        unidadeSelecionada = new HierarquiaOrganizacional();
        itemRota = new ItemRota();
        subAssuntoDocumento = new SubAssuntoDocumento();
        recuperaArvoreHierarquiaOrganizacionalOrc();
    }

    @URLAction(mappingId = "verSubAssunto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getAssunto() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Assunto object : subAssuntoFacade.getAssuntoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public void removeUnidade(ItemRota uni) {
        selecionado.getItensRota().remove(uni);
    }

    public List<ItemRota> getItems() {
        return selecionado.getItensRota();
    }

    public List<SelectItem> getDocumentos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        List<Documento> listaDocumentos = subAssuntoFacade.getDocumentoFacade().lista();
        List<Documento> listaDocumentosCadastrados = new ArrayList<Documento>();
        for (SubAssuntoDocumento s : selecionado.getSubAssuntoDocumentos()) {
            listaDocumentosCadastrados.add(s.getDocumento());
        }
        listaDocumentos.removeAll(listaDocumentosCadastrados);
        for (Documento d : listaDocumentos) {
            toReturn.add(new SelectItem(d, d.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterAssunto() {
        if (converterAssunto == null) {
            converterAssunto = new ConverterGenerico(Assunto.class, subAssuntoFacade.getAssuntoFacade());
        }
        return converterAssunto;
    }

    public ConverterGenerico getConverterDocumento() {
        if (converterDocumento == null) {
            converterDocumento = new ConverterGenerico(Documento.class, subAssuntoFacade.getDocumentoFacade());
        }
        return converterDocumento;
    }

    public Boolean validaItemRota() {
        if (unidadeSelecionada == null) {
            FacesUtil.addError("Atenção!", "Informe a Unidade Organizacional.");
            return false;
        }
        if (itemRota.getPrazo() == 0d) {
            FacesUtil.addError("Atenção!", "Informe o prazo.");
            return false;
        }
        return true;
    }

    public void novoItemRota() {
        unidadeSelecionada = hierarquiaOrganizacional;
        if (validaItemRota()) {
            itemRota.setUnidadeOrganizacional(unidadeSelecionada.getSubordinada());
            selecionado.getItensRota().add(itemRota);
            itemRota = new ItemRota();
            unidadeSelecionada = new HierarquiaOrganizacional();
            hierarquiaOrganizacional = new HierarquiaOrganizacional();
        }
    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        return subAssuntoFacade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaOrganizacionalTipo(parte.trim(), "ADMINISTRATIVA", getSistemaControlador().getDataOperacao());
    }

    public void setaUnidade() {
        if (unidadeSelecionada != null) {
            itemRota.setUnidadeOrganizacional(unidadeSelecionada.getSubordinada());
        } else {
            FacesUtil.addError("Atenção!", "Selecione para filtrar!");
        }
    }

    public Converter getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, subAssuntoFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public void setConverterUnidadeOrganizacional(ConverterAutoComplete converterUnidadeOrganizacional) {
        this.converterUnidadeOrganizacional = converterUnidadeOrganizacional;
    }

    public HierarquiaOrganizacional getUnidadeSelecionada() {
        return unidadeSelecionada;
    }

    public void setUnidadeSelecionada(HierarquiaOrganizacional unidadeSelecionada) {
        this.unidadeSelecionada = unidadeSelecionada;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public void addDocumento() {
        subAssuntoDocumento.setSubAssunto(selecionado);
        subAssuntoDocumento.setDocumento(documento);
        if (documento == null) {
            subAssuntoDocumento = new SubAssuntoDocumento();
            FacesUtil.addError("Não foi possivel continuar", "Selecione um Documento");
        } else {
            selecionado.getSubAssuntoDocumentos().add(subAssuntoDocumento);
            subAssuntoDocumento = new SubAssuntoDocumento();
        }
    }

    public void removerDocumentos(ActionEvent e) {
        subAssuntoDocumento = (SubAssuntoDocumento) e.getComponent().getAttributes().get("objeto");
        selecionado.getSubAssuntoDocumentos().remove(subAssuntoDocumento);
        subAssuntoDocumento = new SubAssuntoDocumento();
    }

    public List<SelectItem> getTiposPrazo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoPrazo object : TipoPrazo.values()) {
            if (object.equals(TipoPrazo.DIAS) || object.equals(TipoPrazo.HORAS)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public void recuperaArvoreHierarquiaOrganizacionalOrc() {
        rootOrc = new DefaultTreeNode(" ", null);
        rootOrc = subAssuntoFacade.getSingletonHO().getArvoreAdministrativa(subAssuntoFacade.getSistemaFacade().getDataOperacao());
    }

    public void selecionarHierarquiaDaArvore(ActionEvent event) {
        if(selectedNode != null) {
            hierarquiaOrganizacional = (HierarquiaOrganizacional)selectedNode.getData();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/subassunto/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void novoAssunto() {
        Web.navegacao(getCaminhoPadrao()+"novo/",
                "/assunto/novo/",
                selecionado);
    }

}
