/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.ConfiguracaoTituloSistemaFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonArquivosInterface;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

@ManagedBean(name = "uploadImagemControlador")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoUploadBrasao", pattern = "/configuracao/interface/",
        viewId = "/faces/admin/uploadbrasao.xhtml"),
})
public class UploadImagemControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(UploadImagemControlador.class);

    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private Arquivo arquivo;
    private UploadedFile recebido;
    private StreamedContent imagem;
    private boolean renderizaImagem;
    private Arquivo arquivoTopo;
    private UploadedFile recebidoTopo;
    private StreamedContent imagemTopo;
    private StreamedContent imagemFoto;
    private ConfiguracaoTituloSistema configuracaoTituloSistema;
    private ConfiguracaoPerfilApp configuracaoPerfilApp;
    @EJB
    private ConfiguracaoTituloSistemaFacade configuracaoTituloSistemaFacade;
    @EJB
    private SingletonArquivosInterface singletonArquivosInterface;

    public ConfiguracaoTituloSistema getConfiguracaoTituloSistema() {
        return configuracaoTituloSistema;
    }

    public void setConfiguracaoTituloSistema(ConfiguracaoTituloSistema configuracaoTituloSistema) {
        this.configuracaoTituloSistema = configuracaoTituloSistema;
    }

    public boolean isRenderizaImagem() {
        return renderizaImagem;
    }

    public void setRenderizaImagem(boolean renderizaImagem) {
        this.renderizaImagem = renderizaImagem;
    }

    public ConfiguracaoPerfilApp getConfiguracaoPerfilApp() {
        return configuracaoPerfilApp;
    }

    public void setConfiguracaoPerfilApp(ConfiguracaoPerfilApp configuracaoPerfilApp) {
        this.configuracaoPerfilApp = configuracaoPerfilApp;
    }

    public UploadImagemControlador() {
        arquivo = new Arquivo();
        arquivoTopo = new Arquivo();
        renderizaImagem = false;
    }

    public StreamedContent getImagemTopo() {
        return imagemTopo;
    }

    public StreamedContent getImagem() {
        return imagem;
    }

    public StreamedContent getImagemFoto() {
        return imagemFoto;
    }

    public void carregaImagem() {
        Arquivo arq = arquivoFacade.recuperaUltimaLogo();
        if (arq.getId() != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (ArquivoParte a : arquivoFacade.recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }
                InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                imagem = new DefaultStreamedContent(teste, arq.getMimeType());
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
        //
        carregaImagemTopo();
    }

    public void carregaImagemTopo() {
        Arquivo arq = arquivoFacade.recuperaUltimaLogoTopo();
        if (arq.getId() != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (ArquivoParte a : arquivoFacade.recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }
                InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                imagemTopo = new DefaultStreamedContent(teste, arq.getMimeType());
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public void upload(FileUploadEvent event) {
        try {
            recebido = event.getFile();
            arquivo.setNome(recebido.getFileName());
            arquivo.setMimeType(arquivoFacade.getMimeType(recebido.getFileName()));
            arquivo.setDescricao("Logo");
            arquivo.setTamanho(recebido.getSize());
            imagem = new DefaultStreamedContent(event.getFile().getInputstream(), arquivoFacade.getMimeType(recebido.getFileName()));
            renderizaImagem = true;
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void uploadLogoTopo(FileUploadEvent event) {
        try {
            recebidoTopo = event.getFile();
            arquivoTopo.setNome(recebidoTopo.getFileName());
            arquivoTopo.setMimeType(arquivoFacade.getMimeType(recebidoTopo.getFileName()));
            arquivoTopo.setDescricao("LogoTopo");
            arquivoTopo.setTamanho(recebidoTopo.getSize());
            imagemTopo = new DefaultStreamedContent(event.getFile().getInputstream(), arquivoFacade.getMimeType(recebidoTopo.getFileName()));

        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void confirmaUpload() {
        try {
            arquivoFacade.novoArquivo(arquivo, recebido.getInputstream());
            arquivo = new Arquivo();
            singletonArquivosInterface.limpaLogoLogin();
            FacesContext.getCurrentInstance().addMessage("Formulario:teste", new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Imagem alterada com sucesso!"));
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
            FacesContext.getCurrentInstance().addMessage("Formulario:teste", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Ocorreu um erro inesperado,  teste novamente caso o esta mensagem persista contate o suporte!"));
        }
    }

    public void confirmaUploadTopo() {
        try {
            arquivoFacade.novoArquivo(arquivoTopo, recebidoTopo.getInputstream());
            arquivoTopo = new Arquivo();
            FacesContext.getCurrentInstance().addMessage("Formulario:msgtopo", new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Imagem alterada com sucesso!"));
            singletonArquivosInterface.limpaLogoTopo();
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
            FacesContext.getCurrentInstance().addMessage("Formulario:msgtopo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Ocorreu um erro inesperado, teste novamente caso o esta mensagem persista contate o suporte!"));
        }
    }

    public StreamedContent fotoPessoaFisica(PessoaFisica pessoaFisica) {
        if (pessoaFisica != null) {
            Arquivo arq = pessoaFacade.recuperaFotoPessoaFisica(pessoaFisica);
            if (arq != null) {
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    for (ArquivoParte a : arquivoFacade.recuperaDependencias(arq.getId()).getPartes()) {
                        buffer.write(a.getDados());
                    }
                    InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                    return new DefaultStreamedContent(teste, arq.getMimeType());
                } catch (Exception ex) {
                    logger.error("Erro: ", ex);
                    return null;
                }
            } else {
                return null;
            }
        }
        return null;
    }


    @URLAction(mappingId = "novoUploadBrasao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void carregarTitulos() {
        this.configuracaoTituloSistema = configuracaoTituloSistemaFacade.recuperarTitulos();
        this.configuracaoPerfilApp = configuracaoTituloSistemaFacade.recuperaConfiguracaoPerfilApp();
    }

    public void salvarTituloSubtitulo() {
        configuracaoTituloSistemaFacade.salvar(configuracaoTituloSistema);
        FacesUtil.addInfo("", "Salvo com sucesso!");
    }

    public void salvarConfiguracaoApp() {
        configuracaoTituloSistemaFacade.salvar(configuracaoPerfilApp);
        FacesUtil.addInfo("", "Salvo com sucesso!");
        singletonArquivosInterface.limpaConfiguracaoPerfil();
    }
}
