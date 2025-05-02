package br.com.webpublico.controle.rh.documentoportalservidor;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.documentoportalservidor.DocumentoPortalContribuinte;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.documentoportalservidor.DocumentoPortalContribuinteFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "verArvoreDePublicacoes", pattern = "/documento-portal-contribuinte/", viewId = "/faces/rh/documento-portal-contribuinte/arvore-publicacoes.xhtml")
})
public class DocumentoPortalContribuinteControlador extends PrettyControlador<DocumentoPortalContribuinte> implements Serializable, CRUD {

    @EJB
    private DocumentoPortalContribuinteFacade documentoPortalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private TreeNode arvoreDocumentos;
    private TreeNode nodeSelecionado;
    private DocumentoPortalContribuinte pai;
    private List<DocumentoPortalContribuinte> documentos;

    public DocumentoPortalContribuinteControlador() {
        super(DocumentoPortalContribuinte.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/documento-portal-contribuinte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return documentoPortalFacade;
    }

    @URLAction(mappingId = "verArvoreDePublicacoes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        DocumentoPortalContribuinte mInicio = new DocumentoPortalContribuinte();
        mInicio.setNomeNoPortal("Publicações");
        mInicio.setOrdem(0);
        arvoreDocumentos = new DefaultTreeNode();
        TreeNode inicio = new DefaultTreeNode(mInicio, arvoreDocumentos);
        inicio.setExpanded(true);
        documentos = documentoPortalFacade.getItensParaContruirDocumentoPortalContribuinte();
        construirDocumentos(documentos, inicio);
    }

    public void construirDocumentos(List<DocumentoPortalContribuinte> documentos, TreeNode superior) {
        for (DocumentoPortalContribuinte doc : documentos) {
            TreeNode node = new DefaultTreeNode(doc, superior);
            if (doc.getFilhos() != null && !doc.getFilhos().isEmpty()) {
                construirDocumentos(doc.getFilhos(), node);
            }
        }
    }

    @Override
    public void salvar() {
        if (isOperacaoEditar()) {
            selecionado.setAtualizadoEm(sistemaFacade.getDataOperacao());
        }
        super.salvar();
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao());
    }

    public TreeNode getArvoreDocumentos() {
        return arvoreDocumentos;
    }

    public void setArvoreDocumentos(TreeNode arvoreDocumentos) {
        this.arvoreDocumentos = arvoreDocumentos;
    }

    public TreeNode getNodeSelecionado() {
        return nodeSelecionado;
    }

    public void setNodeSelecionado(TreeNode nodeSelecionado) {
        this.nodeSelecionado = nodeSelecionado;
    }

    public List<DocumentoPortalContribuinte> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoPortalContribuinte> documentos) {
        this.documentos = documentos;
    }

    public DocumentoPortalContribuinte getPai() {
        return pai;
    }

    public void setPai(DocumentoPortalContribuinte pai) {
        this.pai = pai;
    }

    public void alterarPublicacao() {
        this.setOperacao(Operacoes.EDITAR);
        this.selecionado = (DocumentoPortalContribuinte) this.nodeSelecionado.getData();
        if (selecionado != null && selecionado.getId() != null) {
            this.selecionado = this.documentoPortalFacade.recuperar(selecionado.getId());
            FacesUtil.executaJavaScript("dialogNovaPublicacao.show()");
        }
    }

    public void visualizarPublicacao() {
        this.setOperacao(Operacoes.VER);
        this.selecionado = (DocumentoPortalContribuinte) this.nodeSelecionado.getData();
        if (selecionado != null && selecionado.getId() != null) {
            this.selecionado = this.documentoPortalFacade.recuperar(selecionado.getId());
            FacesUtil.executaJavaScript("dialogNovaPublicacao.show()");
        }
    }

    public void excluirPublicacao() {
        this.setOperacao(Operacoes.EXCLUIR);
        this.selecionado = (DocumentoPortalContribuinte) this.nodeSelecionado.getData();
        if(selecionado != null && selecionado.getSuperior() != null){
            selecionado.getSuperior().getFilhos().remove(selecionado);
            documentoPortalFacade.salvar(selecionado.getSuperior());
        }
        excluir();
    }

    public void novaPublicacao() {
        this.setOperacao(Operacoes.NOVO);
        this.selecionado = new DocumentoPortalContribuinte();
        this.selecionado.setAtivo(Boolean.TRUE);
        this.selecionado.setOrdem(1);
        this.selecionado.setPublicadoEm(sistemaFacade.getDataOperacao());
        if (this.nodeSelecionado != null) {
            DocumentoPortalContribuinte doc = (DocumentoPortalContribuinte) this.nodeSelecionado.getData();
            if (doc.getId() != null) {
                this.selecionado.setSuperior((DocumentoPortalContribuinte) this.nodeSelecionado.getData());
            }
        }
        FacesUtil.executaJavaScript("dialogNovaPublicacao.show()");
    }
}
