package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.interfaces.PossuidorArquivo;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 02/07/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class AssistenteDetentorArquivoComposicao extends SuperEntidade {

    private Date dataOperacao;
    private PossuidorArquivo possuidorArquivo;
    private static final Logger log = LoggerFactory.getLogger(AssistenteDetentorArquivoComposicao.class);

    public AssistenteDetentorArquivoComposicao(PossuidorArquivo possuidorArquivo, Date dataOperacao) {
        super();
        this.possuidorArquivo = possuidorArquivo;
        this.dataOperacao = dataOperacao;
    }

    public void handleFileUpload(FileUploadEvent event) {
        if (possuidorArquivo.getDetentorArquivoComposicao() == null) {
            possuidorArquivo.setDetentorArquivoComposicao(new DetentorArquivoComposicao());
        }

        try {
            adicionarArquivo(event.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleFileUploadSobrepondo(FileUploadEvent event) {
        if (possuidorArquivo.getDetentorArquivoComposicao() != null) {
            possuidorArquivo.getDetentorArquivoComposicao().getArquivosComposicao().clear();
        } else {
            possuidorArquivo.setDetentorArquivoComposicao(new DetentorArquivoComposicao());
        }

        try {
            adicionarArquivo(event.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removerArquivos() {
        if (possuidorArquivo.getDetentorArquivoComposicao() != null) {
            possuidorArquivo.getDetentorArquivoComposicao().setArquivosComposicao(new ArrayList());
        }
    }

    private void adicionarArquivo(UploadedFile file) throws IOException {
        ArquivoComposicao arquivoComposicao = criarArquivoComposicao(criarArquivo(file), file);
        possuidorArquivo.getDetentorArquivoComposicao().getArquivosComposicao().add(arquivoComposicao);
    }

    private Arquivo criarArquivo(UploadedFile file) throws IOException {
        Arquivo arquivo = new Arquivo();

        arquivo.setDescricao(file.getFileName());
        arquivo.setNome(file.getFileName());
        arquivo.setTamanho(file.getSize());
        arquivo.setInputStream(file.getInputstream());
        arquivo.setMimeType(getMimeType(file.getInputstream()));
        arquivo = criarPartesDoArquivo(arquivo);

        return arquivo;
    }

    public String getMimeType(InputStream is) {
        TikaConfig tika = null;
        try {
            tika = new TikaConfig();
            MediaType mimetype = tika.getDetector().detect(
                TikaInputStream.get(is), new Metadata());
            return mimetype.getType() + "/" + mimetype.getSubtype();
        } catch (TikaException e) {
            log.error("Não foi possível gerar o mimeType: " + e);
        } catch (IOException e) {
            log.error("Não foi possível gerar o mimeType: " + e);
        }
        return "application/octet-stream";
    }

    public String getExtension(String mimeType) {
        try {
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType extension = allTypes.forName(mimeType);
            return extension.getExtension();
        } catch (MimeTypeException e) {
            log.error("Não foi possível recuperar a extensão do arquivo: " + e);
        }
        return "";
    }

    private Arquivo criarPartesDoArquivo(Arquivo arquivo) throws IOException {
        int bytesLidos = 0;

        while (true) {
            int restante = arquivo.getInputStream().available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = arquivo.getInputStream().read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            arquivo.getPartes().add(arquivoParte);
        }

        return arquivo;
    }

    private ArquivoComposicao criarArquivoComposicao(Arquivo arquivo, UploadedFile file) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setFile(file);
        arquivoComposicao.setDataUpload(dataOperacao);
        arquivoComposicao.setDetentorArquivoComposicao(possuidorArquivo.getDetentorArquivoComposicao());

        return arquivoComposicao;
    }

    public void removerArquivo(ArquivoComposicao arquivoComposicao) {
        possuidorArquivo.getDetentorArquivoComposicao().getArquivosComposicao().remove(arquivoComposicao);

        if (possuidorArquivo.getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
            possuidorArquivo.setDetentorArquivoComposicao(null);
        }
    }

    public StreamedContent getStream(ArquivoComposicao arquivoComposicao) throws IOException {
        UploadedFile download = (UploadedFile) arquivoComposicao.getFile();
        return new DefaultStreamedContent(download.getInputstream(), download.getContentType(), download.getFileName());
    }

    public StreamedContent getStreamDaEntidadeArquivo(ArquivoComposicao arquivoComposicao) {
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arquivoComposicao.getArquivo().getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, arquivoComposicao.getArquivo().getMimeType(), arquivoComposicao.getArquivo().getNome());
        return s;
    }

    public void setPossuidorArquivo(PossuidorArquivo possuidorArquivo) {
        this.possuidorArquivo = possuidorArquivo;
    }

    public void uploadArquivo(FileUploadEvent evt) {
        handleFileUpload(evt);
    }

    @Override
    public Long getId() {
        return null;
    }
}
