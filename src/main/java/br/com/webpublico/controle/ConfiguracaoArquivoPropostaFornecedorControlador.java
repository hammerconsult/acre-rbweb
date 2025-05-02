package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ConfiguracaoArquivoPropostaFornecedor;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoArquivoPropostaFornecedorFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 11/03/15
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configuracaoArquivoPropostaFornecedorControlador")
@ViewScoped
@URLMapping(id = "novo-configuracao-arquivo-proposta", pattern = "/licitacao/configuracao-arquivo-proposta-fornecedor/", viewId = "/faces/administrativo/licitacao/arquivopropostafornecedor/configuracao.xhtml")
public class ConfiguracaoArquivoPropostaFornecedorControlador implements Serializable {

    @EJB
    private ConfiguracaoArquivoPropostaFornecedorFacade configuracaoArquivoPropostaFornecedorFacade;
    private ConfiguracaoArquivoPropostaFornecedor selecionado;
    protected static final Logger logger = LoggerFactory.getLogger(ConfiguracaoArquivoPropostaFornecedorControlador.class);

    public ConfiguracaoArquivoPropostaFornecedorControlador() {
    }

    @URLAction(mappingId = "novo-configuracao-arquivo-proposta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        recuperarUltimaConfiguracao();
        if (selecionado == null) {
            selecionado = new ConfiguracaoArquivoPropostaFornecedor();
            selecionado.setData(configuracaoArquivoPropostaFornecedorFacade.getSistemaFacade().getDataOperacao());
        }
    }

    public void novaVersao() {
        selecionado.setVersao(selecionado.getVersao() + 1);
    }

    private void recuperarUltimaConfiguracao() {
        ConfiguracaoArquivoPropostaFornecedor configuracaoArquivoPropostaFornecedor = configuracaoArquivoPropostaFornecedorFacade.recuperaUltimaConfiguracao();
        if (configuracaoArquivoPropostaFornecedor != null) {
            selecionado = configuracaoArquivoPropostaFornecedor;
        }
    }

    public void removerArquivoSistema() {
        selecionado.setArquivo(null);
    }

    public void removerArquivoManual() {
        selecionado.setManual(null);
    }

    public void handleFilesUploads(FileUploadEvent event) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setMimeType("application/java-archive");
            arquivo.setNome(event.getFile().getFileName());
            arquivo.setTamanho(event.getFile().getSize());
            arquivo.setDescricao(event.getFile().getFileName());
            selecionado.setArquivo(configuracaoArquivoPropostaFornecedorFacade.getArquivoFacade().novoArquivoMemoria(arquivo, event.getFile().getInputstream()));
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void handleFilesUploadsManual(FileUploadEvent event) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setMimeType("application/pdf");
            arquivo.setNome(event.getFile().getFileName());
            arquivo.setTamanho(event.getFile().getSize());
            arquivo.setDescricao(event.getFile().getFileName());
            selecionado.setManual(configuracaoArquivoPropostaFornecedorFacade.getArquivoFacade().novoArquivoMemoria(arquivo, event.getFile().getInputstream()));
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public ConfiguracaoArquivoPropostaFornecedor getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ConfiguracaoArquivoPropostaFornecedor selecionado) {
        this.selecionado = selecionado;
    }

    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            if (selecionado.getId() == null) {
                this.configuracaoArquivoPropostaFornecedorFacade.salvarNovo(selecionado);
                FacesUtil.addOperacaoRealizada("A Configuração foi salva com sucesso.");
            } else {
                this.configuracaoArquivoPropostaFornecedorFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada("A Configuração foi alterada com sucesso.");
            }
            redirecionarHome();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void cancelar() {
        redirecionarHome();
    }

    private void redirecionarHome() {
        FacesUtil.redirecionamentoInterno("/");
    }
}
