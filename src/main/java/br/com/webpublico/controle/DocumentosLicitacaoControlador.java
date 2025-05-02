/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.DoctoLicitacao;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.ModeloDocto;
import br.com.webpublico.entidades.VersaoDoctoLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LicitacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@ManagedBean(name = "documentosLicitacaoControlador")
@ViewScoped
@URLMapping(id = "novo-documento-licitacao", pattern = "/licitacao/documentos/", viewId = "/faces/administrativo/licitacao/documentoslicitacao/edita.xhtml")
public class DocumentosLicitacaoControlador extends PrettyControlador<Licitacao> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(DocumentosLicitacaoControlador.class);

    @EJB
    private LicitacaoFacade licitacaoFacade;
    private ConverterAutoComplete converterLicitacao;
    private ConverterAutoComplete converterModeloDocto;
    private DoctoLicitacao doctoLicitacao;
    private VersaoDoctoLicitacao versaoDoctoLicitacao;
    private VersaoDoctoLicitacao versaoDoctoLicitacaoEditarDocumento;
    @ManagedProperty(name = "licitacaoControlador", value = "#{licitacaoControlador}")
    private LicitacaoControlador licitacaoControlador;
    private Licitacao licitacao;

    public DocumentosLicitacaoControlador() {
        super(Licitacao.class);
    }

    public void carregarDocumentos(SelectEvent event) {
        licitacao = licitacaoFacade.recuperar(((Licitacao) event.getObject()).getId());
    }

    @URLAction(mappingId = "novo-documento-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        licitacao = null;
    }

    public void editarDocumento(VersaoDoctoLicitacao versao) {
        versaoDoctoLicitacaoEditarDocumento = versao;
    }

    public void confirmarDocumento() {
        for (DoctoLicitacao doctoLicitacao1 : licitacao.getListaDeDocumentos()) {
            for (VersaoDoctoLicitacao versaoDoctoLicitacao1 : doctoLicitacao1.getListaDeVersoes()) {
                if (versaoDoctoLicitacao1.equals(versaoDoctoLicitacaoEditarDocumento)) {
                    versaoDoctoLicitacao1.getModeloDocto().setId(null);
                    versaoDoctoLicitacao1.getModeloDocto().setModelo(versaoDoctoLicitacaoEditarDocumento.getModeloDocto().getModelo());
                    break;
                }
            }
        }
        versaoDoctoLicitacaoEditarDocumento = null;
    }

    public void cancelarDocumento() {
    }

    @Override
    public void salvar() {
        licitacaoControlador.selecionado = licitacao;
        try {
            licitacaoFacade.salvarLicitacao(licitacaoControlador.selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao());
            FacesUtil.addInfo("Licitaçao salvo com sucesso.", " A licitação " + licitacaoControlador.selecionado + " foi salvo com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao());
    }

    @Override
    public AbstractFacade getFacede() {
        return licitacaoFacade;
    }

    public DoctoLicitacao getDoctoLicitacao() {
        return doctoLicitacao;
    }

    public void setDoctoLicitacao(DoctoLicitacao doctoLicitacao) {
        this.doctoLicitacao = doctoLicitacao;
    }

    public VersaoDoctoLicitacao getVersaoDoctoLicitacao() {
        return versaoDoctoLicitacao;
    }

    public void setVersaoDoctoLicitacao(VersaoDoctoLicitacao versaoDoctoLicitacao) {
        this.versaoDoctoLicitacao = versaoDoctoLicitacao;
    }

    public LicitacaoControlador getLicitacaoControlador() {
        return licitacaoControlador;
    }

    public void setLicitacaoControlador(LicitacaoControlador licitacaoControlador) {
        this.licitacaoControlador = licitacaoControlador;
    }

    public VersaoDoctoLicitacao getVersaoDoctoLicitacaoEditarDocumento() {
        return versaoDoctoLicitacaoEditarDocumento;
    }

    public void setVersaoDoctoLicitacaoEditarDocumento(VersaoDoctoLicitacao versaoDoctoLicitacaoEditarDocumento) {
        this.versaoDoctoLicitacaoEditarDocumento = versaoDoctoLicitacaoEditarDocumento;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licitacao/documentos/";

    }

    @Override
    public Object getUrlKeyValue() {
        return licitacao.getId();
    }

    public ConverterAutoComplete getConverterLicitacao() {
        if (converterLicitacao == null) {
            converterLicitacao = new ConverterAutoComplete(Licitacao.class, licitacaoFacade);
        }
        return converterLicitacao;
    }

    public ConverterAutoComplete getConverterModeloDocto() {
        if (converterModeloDocto == null) {
            converterModeloDocto = new ConverterAutoComplete(ModeloDocto.class, licitacaoFacade.getModeloDoctoFacade());
        }
        return converterModeloDocto;
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        return licitacaoFacade.buscarLicitacoes(parte.trim());
    }

    public List<ModeloDocto> completaModeloDocto(String parte) {
        return licitacaoFacade.getModeloDoctoFacade().completaModeloDocto(parte.trim());
    }

    public void adicionarDocumento() {
        if (doctoLicitacao.getModeloDocto() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:modeloDocto", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Documento não informado.", "Por favor, selecione um documento para adicioná-lo."));
            return;
        }
        if (estaNaLista(doctoLicitacao)) {
            FacesContext.getCurrentInstance().addMessage("Formulario:modeloDocto", new FacesMessage(FacesMessage.SEVERITY_ERROR, doctoLicitacao.getModeloDocto() + " já foi adicionado na lista de documentos", null));
            return;
        } else {
            if (licitacao.getListaDeDocumentos() == null) {
                licitacao.setListaDeDocumentos(new ArrayList<DoctoLicitacao>());
            }
            licitacao.getListaDeDocumentos().add(doctoLicitacao);
            setarNullDoctoLicitacao();
        }
    }

    public boolean estaNaLista(DoctoLicitacao d) {
        for (DoctoLicitacao doc : licitacao.getListaDeDocumentos()) {
            if (doc.getModeloDocto().equals(d.getModeloDocto())) {
                return true;
            }
        }
        return false;
    }

    public void parametrosIniciaisDocumentoLicitacao() {
        doctoLicitacao = new DoctoLicitacao();
        doctoLicitacao.setListaDeVersoes(new ArrayList<VersaoDoctoLicitacao>());
//        doctoLicitacao.setModeloDocto(new ModeloDocto());
        doctoLicitacao.setLicitacao(licitacao);
    }

    public void setarNullDoctoLicitacao() {
        doctoLicitacao = null;
    }

    private void parametrosIniciaisVersaoDocto() {
    }

    public void visualizarDocto(DoctoLicitacao doc) {
        doctoLicitacao = doc;
    }

    public void geraVersao(ActionEvent evnt) {
        DoctoLicitacao doc = (DoctoLicitacao) evnt.getComponent().getAttributes().get("doc");
        try {
            versaoDoctoLicitacao = new VersaoDoctoLicitacao();
            versaoDoctoLicitacao.setGeradoEm(new Date());
            versaoDoctoLicitacao.setDoctoLicitacao(doc);
            versaoDoctoLicitacao.setModeloDocto(clonaModelo(doc.getModeloDocto()));
            versaoDoctoLicitacao.setVersao(doc.getListaDeVersoes().size() + 1);
            versaoDoctoLicitacao.setUsuarioSistema(licitacaoFacade.getSistemaFacade().getUsuarioCorrente());

        } catch (Exception ex) {
        }
    }

    public void geraDocumento(VersaoDoctoLicitacao versao) {
//        VersaoDoctoLicitacao novaVersao = new VersaoDoctoLicitacao();
//        novaVersao.setModeloDocto(new ModeloDocto());
//
//        licitacaoControlador.selecionado = licitacao;
//
//        novaVersao.getModeloDocto().setModelo(licitacaoControlador.mesclaTagsModelo(versao.getModeloDocto()));
//        novaVersao.getModeloDocto().setNome(versao.getModeloDocto().getNome());
//
//        licitacaoControlador.geraDocumento(novaVersao.getModeloDocto());
        licitacaoControlador.selecionado = licitacao;
        licitacaoControlador.geraDocumento(versao);
    }

    public void geraDocumentoPadrao(DoctoLicitacao docto) {
//        ModeloDocto novoModelo = new ModeloDocto();
//
//        licitacaoControlador.selecionado = licitacao;
//        novoModelo.setModelo(licitacaoControlador.mesclaTagsModelo(docto.getModeloDocto()));
//        novoModelo.setNome(docto.getModeloDocto().getNome());
//
//
//        licitacaoControlador.geraDocumento(novoModelo);
        licitacaoControlador.selecionado = licitacao;
        licitacaoControlador.geraDocumentoPadrao(docto);
    }

    public void cancelarVersao() {
        versaoDoctoLicitacao = null;
    }

    public void removerDocto(ActionEvent event) {
        DoctoLicitacao doc = (DoctoLicitacao) event.getComponent().getAttributes().get("doc");
        if (!doc.getListaDeVersoes().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("Formulario:mensagemRemoverDocumento", new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getModeloDocto() + " não pode ser removido, pois há versão(ões) associadas!", null));
        } else {
            if (estaNaLista(doc)) {
                licitacao.getListaDeDocumentos().remove(doc);
            }
        }
    }

    public void adicionarVersao() {
        int index = -1;
        for (int i = 0; i < licitacao.getListaDeDocumentos().size(); i++) {
            if (licitacao.getListaDeDocumentos().get(i).equals(versaoDoctoLicitacao.getDoctoLicitacao())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            if (validaNumeroVersao(licitacao.getListaDeDocumentos().get(index).getListaDeVersoes(), versaoDoctoLicitacao)) {
                licitacao.getListaDeDocumentos().get(index).getListaDeVersoes().add(versaoDoctoLicitacao);

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Versão Adicionada com Sucesso.", " A Versão " + versaoDoctoLicitacao.getVersao() + " foi adicionada no Documento " + licitacao.getListaDeDocumentos().get(0) + "."));
                versaoDoctoLicitacao = null;
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Já existe uma versão com este mesmo número.", null));
            }
        }
    }

    private boolean validaNumeroVersao(List<VersaoDoctoLicitacao> listaDeVersoes, VersaoDoctoLicitacao versaoDoctoLicitacao) {
        for (int i = 0; i < listaDeVersoes.size(); i++) {
            if (listaDeVersoes.get(i).getVersao().equals(versaoDoctoLicitacao.getVersao())) {
                return false;
            }
        }
        return true;
    }

    public void removerVersao(VersaoDoctoLicitacao versao) {
        int index = -1;
        for (int i = 0; i < licitacao.getListaDeDocumentos().size(); i++) {
            if (licitacao.getListaDeDocumentos().get(i).equals(versao.getDoctoLicitacao())) {
                index = i;
            }
        }
        if (index >= 0) {
            licitacao.getListaDeDocumentos().get(index).getListaDeVersoes().remove(versao);
            doctoLicitacao.getListaDeVersoes().remove(versao);

        }
    }

    public DoctoLicitacao clonaDocumento(DoctoLicitacao doc) throws InvocationTargetException {
        DoctoLicitacao novoDoc = new DoctoLicitacao();
        try {
            novoDoc = (DoctoLicitacao) BeanUtils.cloneBean(doc);
        } catch (IllegalAccessException ex) {
            logger.error("Erro: ", ex);
        } catch (InstantiationException ex) {
            logger.error("Erro: ", ex);
        } catch (InvocationTargetException ex) {
            logger.error("Erro: ", ex);
        } catch (NoSuchMethodException ex) {
            logger.error("Erro: ", ex);
        }
        return novoDoc;
    }

    public ModeloDocto clonaModelo(ModeloDocto modelo) throws InvocationTargetException {
        ModeloDocto novoModelo = new ModeloDocto();
        try {
            novoModelo = (ModeloDocto) BeanUtils.cloneBean(modelo);
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
        return novoModelo;
    }

    public List<SelectItem> recuperaTagsPeloTipo() {
        if (versaoDoctoLicitacaoEditarDocumento != null) {
            if (versaoDoctoLicitacaoEditarDocumento.getModeloDocto().getTipoModeloDocto() != null) {
                ModeloDoctoControlador modeloDocto = new ModeloDoctoControlador();
                return modeloDocto.recuperarTagsDoModelo(this.versaoDoctoLicitacaoEditarDocumento.getModeloDocto());
            }
        }
        return null;
    }
}
