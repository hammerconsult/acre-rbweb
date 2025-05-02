package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by William on 05/06/2017.
 */
@ManagedBean(name = "assistenteDetentorArquivoControlador")
@ViewScoped
public class AssistenteDetentorArquivoControlador extends SuperEntidade {

    private Date dataOperacao;
    private PossuidorArquivo possuidorArquivo;
    private static final Logger log = LoggerFactory.getLogger(AssistenteDetentorArquivoControlador.class);
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public AssistenteDetentorArquivoControlador() {
        super();
    }

    @Override
    public Long getId() {
        return null;
    }

    public void novo(PossuidorArquivo possuidorArquivo) {
        this.possuidorArquivo = possuidorArquivo;
        this.dataOperacao = new Date();
    }

    public void handleFileUpload(FileUploadEvent event) {
        if (possuidorArquivo.getDetentorArquivoComposicao() == null) {
            possuidorArquivo.setDetentorArquivoComposicao(new DetentorArquivoComposicao());
        }

        try {
            adicionarArquivo(event.getFile());
        } catch (IOException e) {
            log.debug("Assistente Arquivo Controlador: " + e.getMessage());
        }
    }

    private void adicionarArquivo(UploadedFile file) throws IOException {
        ArquivoComposicao arquivoComposicao = criarArquivoComposicao(arquivoFacade.criarArquivo(file), file);
        possuidorArquivo.getDetentorArquivoComposicao().getArquivosComposicao().add(arquivoComposicao);
    }

    public void handleFileUploadUnicoArquivo(FileUploadEvent event) {
        if (possuidorArquivo.getDetentorArquivoComposicao() == null) {
            possuidorArquivo.setDetentorArquivoComposicao(new DetentorArquivoComposicao());
        }
        try {
            adicionarUnicoArquivo(event.getFile());
        } catch (IOException e) {
            log.debug("Assistente Arquivo Controlador: " + e.getMessage());
        }
    }

    private void adicionarUnicoArquivo(UploadedFile file) throws IOException {
        possuidorArquivo.getDetentorArquivoComposicao().setArquivoComposicao(new ArquivoComposicao());
        Arquivo arq = arquivoFacade.criarArquivo(file);
        ArquivoComposicao arquivoComposicao = criarArquivoComposicao(arq, file);
        possuidorArquivo.getDetentorArquivoComposicao().setArquivoComposicao(arquivoComposicao);
        carregarFoto(arq);
    }

    private ArquivoComposicao criarArquivoComposicao(Arquivo arquivo, UploadedFile file) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setFile(file);
        arquivoComposicao.setDataUpload(dataOperacao);
        arquivoComposicao.setUsuario(sistemaFacade.getUsuarioCorrente());
        arquivoComposicao.setDetentorArquivoComposicao(possuidorArquivo.getDetentorArquivoComposicao());

        return arquivoComposicao;
    }

    public void removerArquivo(ArquivoComposicao arquivoComposicao) {
        possuidorArquivo.getDetentorArquivoComposicao().getArquivosComposicao().remove(arquivoComposicao);

        if (possuidorArquivo.getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
            possuidorArquivo.setDetentorArquivoComposicao(null);
        }
    }

    public void removerUnicoArquivo() {
        possuidorArquivo.getDetentorArquivoComposicao().setArquivoComposicao(null);
    }

    public StreamedContent getStream(ArquivoComposicao arquivoComposicao) throws IOException {
        UploadedFile download = (UploadedFile) arquivoComposicao.getFile();
        return new DefaultStreamedContent(download.getInputstream(), download.getContentType(), download.getFileName());
    }

    public void sendToMinio(ArquivoComposicao arquivoComposicao) {
        String url = arquivoFacade.enviarArquivoParaStorage(arquivoComposicao.getArquivo());
        if (Strings.isNullOrEmpty(url)) {
            FacesUtil.addOperacaoNaoRealizada("Arquivo NÃO enviado ao storage, confira a log para mais detalhes");
            return;
        }
        FacesUtil.addOperacaoRealizada(String.format("Arquivo enviado ao storage com sucesso, dispoinível em [%s]", url));
    }


    public void uploadArquivo(FileUploadEvent evt) {
        handleFileUpload(evt);
    }

    public StreamedContent baixarAnexo(Arquivo arquivo) {
        try {
            if (arquivo != null) {
                return arquivoFacade.montarArquivoParaDownloadPorArquivo(arquivo);
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível baixar o anexo. Detalhes: " + e.getMessage());
        }
        return null;
    }

    public void carregaFoto(DetentorArquivoComposicao detentorArquivoComposicao) {
        if (detentorArquivoComposicao != null && detentorArquivoComposicao.getArquivoComposicao() != null) {
            carregarFoto(detentorArquivoComposicao.getArquivoComposicao().getArquivo());
        }
    }

    public void carregarFoto(Arquivo arq) {
        if (arq != null) {
            if (arq.getId() != null) arq = arquivoFacade.recuperaDependencias(arq.getId());
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (ArquivoParte a : arq.getPartes()) {
                    buffer.write(a.getDados());
                }
                ByteArrayInputStream byteArray = new ByteArrayInputStream(buffer.toByteArray());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-fileupload", new DefaultStreamedContent(byteArray, arq.getMimeType(), arq.getNome()));
            } catch (Exception ex) {
                log.error("Erro ao carregar foto: ", ex);
            }
        }
    }
}
