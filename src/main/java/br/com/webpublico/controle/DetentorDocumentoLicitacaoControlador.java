package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.ConfiguracaoLicitacaoFacade;
import br.com.webpublico.negocios.TipoDocumentoAnexoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class DetentorDocumentoLicitacaoControlador {
    private static final Logger log = LoggerFactory.getLogger(DetentorDocumentoLicitacaoControlador.class);

    private DocumentoLicitacao documentoLicitacao;
    private List<SelectItem> siTiposDocumentoAnexo;
    private List<SelectItem> siTiposRegistro;
    private List<TipoDocumentoAnexo> tipoDocumentoAnexoList;
    @EJB
    private TipoDocumentoAnexoFacade tipoDocumentoAnexoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;

    public DetentorDocumentoLicitacaoControlador() {
    }

    public DocumentoLicitacao getDocumentoLicitacao() {
        return documentoLicitacao;
    }

    public void setDocumentoLicitacao(DocumentoLicitacao documentoLicitacao) {
        this.documentoLicitacao = documentoLicitacao;
    }

    public void preRenderComponent(EntidadeDetendorDocumentoLicitacao selecionado) {
        try {
            if (selecionado.getDetentorDocumentoLicitacao() == null) {
                selecionado.setDetentorDocumentoLicitacao(new DetentorDocumentoLicitacao());
            }
            if (selecionado.getTipoAnexo() !=null) {
                tipoDocumentoAnexoList = configuracaoLicitacaoFacade.buscarTipoDocumentoAnexo(selecionado.getTipoAnexo());
                siTiposDocumentoAnexo = carregarSiTiposDocumentoAnexo();
                siTiposRegistro = Util.getListSelectItem(DocumentoLicitacao.TipoRegistro.values(), false);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void novo() {
        this.documentoLicitacao = new DocumentoLicitacao();
        this.documentoLicitacao.setTipoRegistro(DocumentoLicitacao.TipoRegistro.ARQUIVO);
    }

    public void salvar(EntidadeDetendorDocumentoLicitacao selecionado) {
        try {
            documentoLicitacao.realizarValidacoes();
            documentoLicitacao.setDetentorDocumentoLicitacao(selecionado.getDetentorDocumentoLicitacao());
            documentoLicitacao.setDataRegistro(new Date());
            selecionado.getDetentorDocumentoLicitacao().setDocumentoLicitacaoList(Util.adicionarObjetoEmLista(selecionado.getDetentorDocumentoLicitacao()
                .getDocumentoLicitacaoList(), documentoLicitacao));
            documentoLicitacao = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void cancelar() {
        this.documentoLicitacao = null;
    }

    public void alterar(DocumentoLicitacao documentoLicitacao) {
        this.documentoLicitacao = (DocumentoLicitacao) Util.clonarObjeto(documentoLicitacao);
    }

    public void excluir(EntidadeDetendorDocumentoLicitacao selecionado, DocumentoLicitacao documentoLicitacao) {
        selecionado.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList().remove(documentoLicitacao);
    }

    public List<SelectItem> carregarSiTiposDocumentoAnexo() {
        List<SelectItem> selectItems = Lists.newArrayList();
        List<TipoDocumentoAnexo> tiposDocumentoAnexo = tipoDocumentoAnexoFacade.buscarTodosTipoDocumentosAnexo();
        if (!Util.isListNullOrEmpty(tipoDocumentoAnexoList)) {
            tiposDocumentoAnexo = tipoDocumentoAnexoList;
        }
        selectItems.add(new SelectItem(null, ""));
        for (TipoDocumentoAnexo tipoDocumentoAnexo : tiposDocumentoAnexo) {
            selectItems.add(new SelectItem(tipoDocumentoAnexo, tipoDocumentoAnexo.getDescricao()));
        }
        return selectItems;
    }

    public List<SelectItem> getSiTiposDocumentoAnexo() {
        return siTiposDocumentoAnexo;
    }

    public List<SelectItem> getSiTiposRegistro() {
        return siTiposRegistro;
    }

    public void mudouTipoRegistro() {
        this.documentoLicitacao.setArquivo(null);
        this.documentoLicitacao.setLink("");
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            this.documentoLicitacao.setArquivo(arquivoFacade.criarArquivo(event.getFile()));
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void redirect(String link) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect(link);
    }

    public void limparArquivo() {
        this.documentoLicitacao.setArquivo(null);
    }

    public StreamedContent getStreamedContent(Arquivo arquivo) {
        return arquivoFacade.montarArquivoParaDownloadPorArquivo(arquivo);
    }

    public String getTamanhoFormatado(Arquivo arquivo) {
        return Util.formatFileSize(arquivo.getTamanho());
    }

    public List<TipoDocumentoAnexo> getTipoDocumentoAnexoList() {
        return tipoDocumentoAnexoList;
    }

    public void setTipoDocumentoAnexoList(List<TipoDocumentoAnexo> tipoDocumentoAnexoList) {
        this.tipoDocumentoAnexoList = tipoDocumentoAnexoList;
    }

}
