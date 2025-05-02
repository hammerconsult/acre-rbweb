package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.ConfiguracaoCredencialRBTrans;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.ConfiguracaoCredencialRBTransFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;

/**
 * Created by zaca on 06/01/17.
 */
@ManagedBean(name = "configCredencialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "nova-configuracao-credencial",
                pattern = "/configuracao-assinatura/novo/",
                viewId = "/faces/tributario/rbtrans/parametros/configuracaocredencial/edita.xhtml"),
        @URLMapping(id = "editar-configuracao-credencial",
                pattern = "/configuracao-assinatura/editar/#{configCredencialControlador.id}/",
                viewId = "/faces/tributario/rbtrans/parametros/configuracaocredencial/edita.xhtml"),
        @URLMapping(id = "ver-configuracao-credencial",
                pattern = "/configuracao-assinatura/ver/#{configCredencialControlador.id}/",
                viewId = "/faces/tributario/rbtrans/parametros/configuracaocredencial/visualizar.xhtml"),
        @URLMapping(id = "listar-configuracao-credencial",
                pattern = "/configuracao-assinatura/listar/",
                viewId = "/faces/tributario/rbtrans/parametros/configuracaocredencial/lista.xhtml")
})
public class ConfiguracaoCredencialRBTransControlador extends PrettyControlador<ConfiguracaoCredencialRBTrans> implements Serializable, CRUD {

    private static Logger logger = Logger.getLogger(ConfiguracaoCredencialRBTransControlador.class);
    @EJB
    private ConfiguracaoCredencialRBTransFacade configuracaoCredencialRBTransFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private DefaultStreamedContent imagem;
    private FileUploadEvent fileUploadEvent;

    public ConfiguracaoCredencialRBTransControlador() {
        super(ConfiguracaoCredencialRBTransControlador.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-assinatura/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return getConfiguracaoCredencialRBTransFacade();
    }

    @URLAction(mappingId = "nova-configuracao-credencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = new ConfiguracaoCredencialRBTrans();
        imagem = new DefaultStreamedContent();
        fileUploadEvent = null;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", null);
    }

    @URLAction(mappingId = "editar-configuracao-credencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        carregarImagem();
    }

    @URLAction(mappingId = "ver-configuracao-credencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        carregarImagem();
    }

    @Override
    public void excluir() {
        try {
            validarExclusao();
            super.excluir();
            definirConfiguracaoCredencialAnteriorVigente();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }catch (Exception e){
            logger.error("erro ao excluir.: ", e);
        }
    }

    public void validarExclusao(){
        ValidacaoException ve = new ValidacaoException();
        if (this.selecionado != null){
            if (this.selecionado.getFinalEm() != null){
                ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor selecione o registro vigente para realizar a exclusão ");
            }
        }
        ve.lancarException();
    }

    public void definirConfiguracaoCredencialAnteriorVigente(){
        configuracaoCredencialRBTransFacade.definirConfiguracaoAnteriorToVigente();
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            if (selecionado.getId() == null) {
                validarSelecionado();
            }
            selecionado = configuracaoCredencialRBTransFacade.salvarSelecionadoWithArquivo(selecionado, fileUploadEvent);
            redireciona();
            FacesUtil.addOperacaoRealizada("Registro Salvo com Sucesso. ");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("erro ao salvar.: {} ", ex);
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void validarSelecionado() {
        ValidacaoException ex = new ValidacaoException();
        if (selecionado.getFinalEm() != null && selecionado.getInicioEm() != null) {
            if (selecionado.getFinalEm().before(selecionado.getInicioEm())) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("A data final deve ser maior que a data inicial");
            }
        }
        if (selecionado.getInicioEm() != null){
            if (configuracaoCredencialRBTransFacade.isExistsConfiguracaoValidaPorData(selecionado.getInicioEm())){
                ex.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração válida iniciando em " + Util.formatterDiaMesAno.format(selecionado.getInicioEm()) + " ");
            }
        }
        if (this.fileUploadEvent == null){
            ex.adicionarMensagemDeCampoObrigatorio("Por Favor adicione uma imagem ");
        }
        ex.lancarException();
    }


    public void carregarArquivo(FileUploadEvent event) {
        try {
            fileUploadEvent = event;
            imagem = new DefaultStreamedContent(event.getFile().getInputstream(), "image/png", event.getFile().getFileName());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagem);
        } catch (IOException ex) {
            logger.error("Erro carregar imagem: ", ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void carregarImagem() {
        Arquivo arq = selecionado.getChancelaRBTrans().getArquivo();
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (ArquivoParte a : arquivoFacade.recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }
                InputStream img = new ByteArrayInputStream(buffer.toByteArray());
                imagem = new DefaultStreamedContent(img, arq.getMimeType(), arq.getNome());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagem);
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public boolean exibirSiluetaImagem() {
        try {
            return ((StreamedContent) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("imagem-foto")).getName().trim().length() <= 0;
        } catch (Exception e) {
            return true;
        }
    }

    public ConfiguracaoCredencialRBTransFacade getConfiguracaoCredencialRBTransFacade() {
        return configuracaoCredencialRBTransFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public DefaultStreamedContent getImagem() {
        return imagem;
    }

    public void setImagem(DefaultStreamedContent imagem) {
        this.imagem = imagem;
    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }
}
