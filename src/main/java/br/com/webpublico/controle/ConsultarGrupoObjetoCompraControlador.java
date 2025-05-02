
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ConsultarGrupoObjetoCompraControlador implements Serializable {

    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;

    private String codigoDescricaoGrupoObjetoCompra;

    private DefaultTreeNode raizGrupoObjetoCompra;

    private DefaultTreeNode nodeSelecionado;

    private String idComponente;

    private Boolean mostrarArvoreExpandida;

    private GrupoObjetoCompra grupoObjetoCompraSelecionado;

    private String filtro;

    private TreeNode rootAdm;

    private TipoObjetoCompra tipoObjetoCompra;

    private Boolean buscarGrupoObjetoCompraNaoAgrupado;

    private Logger logger = LoggerFactory.getLogger(ConsultarGrupoObjetoCompraControlador.class);

    private List<GrupoObjetoCompra> listaSuperioresGrupoObjetoCompra = new ArrayList<>();

    public Boolean getMostrarArvoreExpandida() {
        return mostrarArvoreExpandida;
    }

    public void setMostrarArvoreExpandida(Boolean mostrarArvoreExpandida) {
        this.mostrarArvoreExpandida = mostrarArvoreExpandida;
    }

    public String getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(String idComponente) {
        this.idComponente = idComponente;
    }

    public DefaultTreeNode getNodeSelecionado() {
        return nodeSelecionado;
    }

    public void setNodeSelecionado(DefaultTreeNode nodeSelecionado) {
        this.nodeSelecionado = nodeSelecionado;
    }

    public TreeNode getRaizGrupoObjetoCompra() {
        return rootAdm;
    }

    public void setRaizGrupoObjetoCompra(DefaultTreeNode raizGrupoObjetoCompra) {
        this.raizGrupoObjetoCompra = raizGrupoObjetoCompra;
    }

    public String getCodigoDescricaoGrupoObjetoCompra() {
        return codigoDescricaoGrupoObjetoCompra;
    }

    public void setCodigoDescricaoGrupoObjetoCompra(String codigoReduzidoGrupoObjetoCompra) {
        this.codigoDescricaoGrupoObjetoCompra = codigoReduzidoGrupoObjetoCompra;
    }

    public void limpar() {
        tipoObjetoCompra = null;
        grupoObjetoCompraSelecionado = null;
    }

    public void limparSelecaoGrupoObjetoCompra() {
        this.filtro = null;
        this.raizGrupoObjetoCompra = null;
        this.codigoDescricaoGrupoObjetoCompra = null;
        this.grupoObjetoCompraSelecionado = null;
        this.nodeSelecionado = null;
    }

    public GrupoObjetoCompra getGrupoObjetoCompraSelecionado() {
        return grupoObjetoCompraSelecionado;
    }

    public void setGrupoObjetoCompraSelecionado(GrupoObjetoCompra grupoObjetoCompraSelecionado) {
        if (grupoObjetoCompraSelecionado != null && !grupoObjetoCompraSelecionado.equals(this.grupoObjetoCompraSelecionado)) {
            obterGrupoObjetoCompraSuperiores(grupoObjetoCompraSelecionado);
        }
        this.grupoObjetoCompraSelecionado = grupoObjetoCompraSelecionado;

    }

    public List<GrupoObjetoCompra> getListaSuperioresGrupoObjetoCompra() {
        return listaSuperioresGrupoObjetoCompra;
    }

    public void setListaSuperioresGrupoObjetoCompra(List<GrupoObjetoCompra> listaSuperioresGrupoObjetoCompra) {
        this.listaSuperioresGrupoObjetoCompra = listaSuperioresGrupoObjetoCompra;
    }

    public void confirmarSelecaoGrupoObjetoCompra() {
        if (nodeSelecionado == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Selecione uma dotação orçamentária no último nível para poder continuar.");
            return;
        }

        if (nodeSelecionado.getChildCount() == 0) {
            setGrupoObjetoCompraSelecionado((GrupoObjetoCompra) nodeSelecionado.getData());
            setCodigoDescricaoGrupoObjetoCompra(grupoObjetoCompraSelecionado.getCodigo() + " - " + grupoObjetoCompraSelecionado.getDescricao());
            RequestContext.getCurrentInstance().execute("dialogGrupoObjetoCompra.hide()");

            RequestContext.getCurrentInstance().update(idComponente + ":codigo-descricao-grupo-objeto");
            RequestContext.getCurrentInstance().update(idComponente + ":grid-info-descricao-grupo-objeto");
            RequestContext.getCurrentInstance().update(idComponente + ":bt-info-descricao-grupo-objeto");

            RequestContext.getCurrentInstance().execute("setar()");

        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Só é permitido selecionar o último nível da estrutura.");
            nodeSelecionado = null;
        }
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Boolean getBuscarGrupoObjetoCompraNaoAgrupado() {
        return buscarGrupoObjetoCompraNaoAgrupado;
    }

    public void setBuscarGrupoObjetoCompraNaoAgrupado(Boolean buscarGrupoObjetoCompraNaoAgrupado) {
        this.buscarGrupoObjetoCompraNaoAgrupado = buscarGrupoObjetoCompraNaoAgrupado;
    }

    public void validarFiltro() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.length() < 2) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Filtro deve ter ao menos 2 caracteres.");
        }
        ve.lancarException();
    }

    public void filtrar() {
        try {
            validarFiltro();
            rootAdm = null;
            carregarArvore();
            filtro = null;
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        }
    }

    public TreeNode montaArvore() {
        TreeNode raiz = new DefaultTreeNode(" ", null);
        try {
            List<GrupoObjetoCompra> listaHO = grupoObjetoCompraFacade.listaOrdenadaPorIndiceDoNo(filtro, tipoObjetoCompra, buscarGrupoObjetoCompraNaoAgrupado);
            if (!listaHO.isEmpty()) {
                arvoreMontada(raiz, listaHO);
            }
            raiz.isExpanded();
        } catch (ExcecaoNegocioGenerica e) {
            logger.error("Erro Montando Arvore de grupo de objeto de compra ", e);
        }
        return raiz;
    }

    public void arvoreMontada(TreeNode raiz, List<GrupoObjetoCompra> listaHO) {
        TreeNode no = null;
        for (GrupoObjetoCompra grupoObjetoCompra : listaHO) {
            if (grupoObjetoCompra.getSuperior() == null || tipoObjetoCompra.isObraInstalacoes()) {
                TreeNode treeNode = new DefaultTreeNode(grupoObjetoCompra, raiz);
                treeNode.isExpanded();
                treeNode.setExpanded(true);
                no = treeNode;
            } else {
                TreeNode treeNode = new DefaultTreeNode(grupoObjetoCompra, getPai(grupoObjetoCompra, no));
                treeNode.isExpanded();
                treeNode.setExpanded(true);
            }
        }
    }

    public TreeNode getPai(GrupoObjetoCompra ho, TreeNode root) {
        GrupoObjetoCompra no = (GrupoObjetoCompra) root.getData();
        if (no.getId().equals(ho.getSuperior().getId())) {
            return root;
        }
        for (TreeNode treeNodeDaVez : root.getChildren()) {
            TreeNode treeNode = getPai(ho, treeNodeDaVez);
            GrupoObjetoCompra hoRecuparado = (GrupoObjetoCompra) treeNode.getData();
            if (hoRecuparado.getId().equals(ho.getSuperior().getId())) {
                return treeNode;
            }
        }
        return root;
    }

    public void carregarArvore() {
        rootAdm = getArvore();

    }

    private TreeNode getArvore() {
        TreeNode retorno;
        retorno = montaArvore();
        return retorno;
    }

    public void atribuirVariaveisEditando(GrupoObjetoCompra grupoObjetoCompra) {
        if (grupoObjetoCompra != null) {
            if (grupoObjetoCompra.getId() != null) {
                setGrupoObjetoCompraSelecionado(grupoObjetoCompra);
                setCodigoDescricaoGrupoObjetoCompra(grupoObjetoCompraSelecionado.getCodigo() + " - " + grupoObjetoCompraSelecionado.getDescricao());
            }
        }
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraPermanenteMovelPorCodigoOrDescricao(String codigoOrDescricao) {
        if (tipoObjetoCompra == null) {
            if (buscarGrupoObjetoCompraNaoAgrupado) {
                return grupoObjetoCompraFacade.buscarGrupoObjetoCompraPorCodigoOrDescricaoNaoAgrupado(codigoOrDescricao.trim().toLowerCase());
            }
            return grupoObjetoCompraFacade.buscarFiltrandoGrupoObjetoCompraPorCodigoOrDescricao(codigoOrDescricao.trim().toLowerCase());
        }
        return grupoObjetoCompraFacade.buscarFiltrandoGrupoObjetoCompraPorTipoObjetoCompra(codigoOrDescricao.trim().toLowerCase(), tipoObjetoCompra);
    }

    public void obterGrupoObjetoCompraSuperiores(GrupoObjetoCompra grupoObjetoCompra) {
        List<GrupoObjetoCompra> listaSuperioresGrupoObjetoCompra = new ArrayList<>();
        listaSuperioresGrupoObjetoCompra.add(grupoObjetoCompra);
        obterGrupoObjetoCompraSuperioresRecursivo(grupoObjetoCompra, listaSuperioresGrupoObjetoCompra);
        Collections.reverse(listaSuperioresGrupoObjetoCompra);
        this.listaSuperioresGrupoObjetoCompra = listaSuperioresGrupoObjetoCompra;
    }

    private void obterGrupoObjetoCompraSuperioresRecursivo(GrupoObjetoCompra grupoObjetoCompra, List<GrupoObjetoCompra> listaSuperioresGrupoObjetoCompra) {
        GrupoObjetoCompra grupoObjetoCompraSuperior = grupoObjetoCompra.getSuperior();
        if (grupoObjetoCompraSuperior != null) {
            listaSuperioresGrupoObjetoCompra.add(grupoObjetoCompraSuperior);
            obterGrupoObjetoCompraSuperioresRecursivo(grupoObjetoCompraSuperior, listaSuperioresGrupoObjetoCompra);
        }
    }

    public void atualizarTipoObjetoCompra(String tipoObjetoCompra) {
        if (Util.getListOfEnumName(TipoObjetoCompra.values()).contains(tipoObjetoCompra.toUpperCase())) {
            this.tipoObjetoCompra = TipoObjetoCompra.valueOf(tipoObjetoCompra.toUpperCase());
        }
    }

}
