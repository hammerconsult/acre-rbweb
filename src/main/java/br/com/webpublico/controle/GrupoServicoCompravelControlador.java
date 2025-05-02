/*
 * Codigo gerado automaticamente em Wed Jan 11 15:47:45 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoServicoCompravel;
import br.com.webpublico.entidades.ServicoCompravel;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoServicoCompravelFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "grupoServicoCompravelControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-grupo-servico-compravel", pattern = "/grupo-servico-compravel/novo/", viewId = "/faces/administrativo/licitacao/gruposervicocompravel/edita.xhtml"),
    @URLMapping(id = "editar-grupo-servico-compravel", pattern = "/grupo-servico-compravel/editar/#{grupoServicoCompravelControlador.id}/", viewId = "/faces/administrativo/licitacao/gruposervicocompravel/edita.xhtml"),
    @URLMapping(id = "ver-grupo-servico-compravel", pattern = "/grupo-servico-compravel/ver/#{grupoServicoCompravelControlador.id}/", viewId = "/faces/administrativo/licitacao/gruposervicocompravel/visualizar.xhtml"),
    @URLMapping(id = "adiconar-filho-grupo-servico-compravel", pattern = "/grupo-servico-compravel/adicionar-filho/#{grupoServicoCompravelControlador.id}/", viewId = "/faces/administrativo/licitacao/gruposervicocompravel/edita.xhtml"),
    @URLMapping(id = "listar-grupo-servico-compravel", pattern = "/grupo-servico-compravel/listar/", viewId = "/faces/administrativo/licitacao/gruposervicocompravel/lista.xhtml")
})
public class GrupoServicoCompravelControlador extends PrettyControlador<GrupoServicoCompravel> implements Serializable, CRUD {

    @EJB
    private GrupoServicoCompravelFacade grupoServicoCompravelFacade;
    private GrupoServicoCompravel grupoServicoCompravelSelecionado;
    private GrupoServicoCompravel grupoServicoCompravel;
    private ConverterAutoComplete converterSuperior;
    private transient TreeNode root;
    private transient TreeNode noSelecionado;

    public GrupoServicoCompravelControlador() {
        super(GrupoServicoCompravel.class);
    }

    public GrupoServicoCompravelFacade getFacade() {
        return grupoServicoCompravelFacade;
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

    public GrupoServicoCompravel getGrupoServicoCompravel() {
        return grupoServicoCompravel;
    }

    public void setGrupoServicoCompravel(GrupoServicoCompravel grupoServicoCompravel) {
        this.grupoServicoCompravel = grupoServicoCompravel;
    }

    public Converter getConverterSuperior() {
        if (converterSuperior != null) {
            converterSuperior = new ConverterAutoComplete(GrupoServicoCompravel.class, grupoServicoCompravelFacade);
        }
        return converterSuperior;
    }

    public void montaArvore(TreeNode raizArvore) {
        GrupoServicoCompravel raiz = (GrupoServicoCompravel) raizArvore.getData();
        acharNoSelecionado(raizArvore);
        for (GrupoServicoCompravel grupoServicoCompravel : grupoServicoCompravelFacade.getFilhosDe(raiz)) {
            TreeNode filhoArvore = new DefaultTreeNode(grupoServicoCompravel, raizArvore);
            acharNoSelecionado(filhoArvore);
            montaArvore(filhoArvore);
        }
    }

    public TreeNode getArvore() {
        root = new DefaultTreeNode("root", null);
        GrupoServicoCompravel raiz = grupoServicoCompravelFacade.getRaiz();
        TreeNode raizVisual = new DefaultTreeNode(raiz, root);
        if (raiz != null) {
            montaArvore(raizVisual);
        }
        return root;
    }

    public TreeNode acharNoSelecionado(TreeNode treeNode) {
        if (selecionado != null) {
            if (treeNode.getData().equals(((GrupoServicoCompravel) selecionado).getSuperior())) {
                treeNode.setSelected(true);
                treeNode.setExpanded(true);
            }
        }
        return treeNode;
    }

    public GrupoServicoCompravel getGrupoServicoCompravelSelecionado() {
        return grupoServicoCompravelSelecionado;
    }

    public void setGrupoServicoCompravelSelecionado(GrupoServicoCompravel grupoServicoCompravelSelecionado) {
        this.grupoServicoCompravelSelecionado = grupoServicoCompravelSelecionado;
    }

    public void addFilho(ActionEvent evento) {
    }

    @Override
    public void salvar() {
        if (noSelecionado != null) {
            ((GrupoServicoCompravel) selecionado).setSuperior((GrupoServicoCompravel) noSelecionado.getData());
        }
        super.salvar();
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoServicoCompravelFacade;
    }

    public void setaSuperior() {
        if (grupoServicoCompravel != null) {
            ((GrupoServicoCompravel) selecionado).setSuperior(grupoServicoCompravel);
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Selecione para filtrar!"));
        }
    }

    public List<SelectItem> getSuperiores() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (GrupoServicoCompravel object : grupoServicoCompravelFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novo-grupo-servico-compravel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((GrupoServicoCompravel) selecionado).setCodigo(grupoServicoCompravelFacade.geraCodigoNovo());
    }

    @URLAction(mappingId = "ver-grupo-servico-compravel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaVerEditar();
    }

    @URLAction(mappingId = "editar-grupo-servico-compravel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaVerEditar();
    }

    public void recuperaVerEditar() {
        grupoServicoCompravel = new GrupoServicoCompravel();
        operacao = Operacoes.EDITAR;
        selecionado = grupoServicoCompravelFacade.recuperar(super.getId());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grupo-servico-compravel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "adiconar-filho-grupo-servico-compravel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoFilho() {
        GrupoServicoCompravel pai = grupoServicoCompravelFacade.recuperar(super.getId());
        super.novo();
        ((GrupoServicoCompravel) selecionado).setCodigo(grupoServicoCompravelFacade.geraCodigoFilho(pai));
        ((GrupoServicoCompravel) selecionado).setSuperior(pai);
    }

    @Override
    public void excluir() {
        List<GrupoServicoCompravel> filhosDe = grupoServicoCompravelFacade.getFilhosDe(selecionado);
        List<ServicoCompravel> servicos = grupoServicoCompravelFacade.getObjetosDeCompra(selecionado);

        boolean podeExcluir = false;
        if (filhosDe == null || filhosDe.isEmpty()) {
            podeExcluir = true;
        } else {
            podeExcluir = false;
            FacesUtil.addError("Não foi possível remover!", "O Grupo " + selecionado + " possui " + (filhosDe.size() == 1 ? " " + filhosDe.get(0) + " como filho." : filhosDe.size() + " filhos associados."));
        }
        if (servicos == null || servicos.isEmpty() && podeExcluir) {
            podeExcluir = true;
        } else {
            FacesUtil.addError("Não foi possível remover!", "O Grupo " + selecionado + " possui " + servicos.size() + " Serviço(s) de Compravel(is) associado(s).");
            podeExcluir = false;
        }
        if (podeExcluir) {
            super.excluir();
        }
    }
}
