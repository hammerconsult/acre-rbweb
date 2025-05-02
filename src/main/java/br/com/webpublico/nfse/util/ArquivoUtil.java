package br.com.webpublico.nfse.util;

import br.com.webpublico.entidades.Arquivo;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;

/**
 * Created by rodolfo on 23/10/17.
 */
public class ArquivoUtil {


    public static Arquivo base64ToArquivo(String conteudo, String nome) {
        String dataInfo = conteudo.contains("base64") ? conteudo.split(";base64,")[0] : "data:image/jpeg";
        String data = conteudo.contains("base64") ? conteudo.split("base64,")[1] : conteudo;
        Base64 decoder = new Base64();

        byte[] imgBytes = decoder.decode(data);

        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao(nome);
        arquivo.setMimeType(dataInfo.split(":")[1]);
        arquivo.setNome(nome);
        arquivo.setTamanho((long) imgBytes.length);
        arquivo.setInputStream(new ByteArrayInputStream(imgBytes));

        return arquivo;
    }
}
