package br.com.webpublico.util;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.negocios.ArquivoFacade;
import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ArquivoUtil {

    public static final Logger logger = LoggerFactory.getLogger(ArquivoUtil.class);

    public static Arquivo converterArquivoDTOToArquivo(ArquivoDTO dto, ArquivoFacade arquivoFacade) {
        if (dto == null || Strings.isNullOrEmpty(dto.getConteudo())) return null;
        try {
            String dataInfo = dto.getConteudo().split(";base64,")[0];
            String data = dto.getConteudo().split("base64,")[1];
            String mimeType;
            try {
                mimeType = dataInfo.split(":")[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                mimeType = null;
            }
            Base64 decoder = new Base64();
            byte[] imgBytes = decoder.decode(data);
            Arquivo arquivo = null;
            arquivo = arquivoFacade.novoArquivoMemoria(new Arquivo(), new ByteArrayInputStream(imgBytes));
            arquivo.setNome(dto.getNome());
            arquivo.setDescricao(dto.getDescricao());
            arquivo.setMimeType(mimeType);
            return arquivo;
        } catch (Exception e) {
            logger.error("Erro ao converter ArquivoDTO para Arquivo. Erro: {}", e.getMessage());
            logger.debug("Stack Error.", e);
        }
        return null;
    }

    public static ArquivoDTO converterArquivoToArquivoDTO(Arquivo arquivo, ArquivoFacade arquivoFacade) {
        if (arquivo == null) return null;
        try {
            arquivo = arquivoFacade.recuperaDependencias(arquivo.getId());

            ArquivoDTO dto = new ArquivoDTO();
            dto.setNome(arquivo.getNome());
            dto.setDescricao(arquivo.getDescricao());
            dto.setMimeType(arquivo.getMimeType());
            dto.setConteudo("data:application/json;base64," + new Base64().encodeAsString(arquivo.getByteArrayDosDados()));
            return dto;
        } catch (Exception e) {
            logger.error("Erro ao converter Arquivo para ArquivoDTO. Erro: {}", e.getMessage());
            logger.debug("Stack Error.", e);
        }
        return null;
    }

    public static File createTempFile(String nome, InputStream inputStream) throws IOException {
        try {
            File tempFile = File.createTempFile(nome, ".tmp");
            tempFile.deleteOnExit();

            FileOutputStream out = new FileOutputStream(tempFile);
            IOUtils.copy(inputStream, out);

            return tempFile;
        } catch (Exception e) {
            throw new IOException("Error occurred while creating temp file for uploaded :", e);
        }
    }

    public static String stringHexa(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
            int parteBaixa = bytes[i] & 0xf;
            if (parteAlta == 0) {
                s.append('0');
            }
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }

    public static byte[] gerarHashMD5(String frase) {
        return gerarHash(frase, "MD5");
    }

    public static byte[] gerarHash(String frase, String algoritmo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            md.update(frase.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}
