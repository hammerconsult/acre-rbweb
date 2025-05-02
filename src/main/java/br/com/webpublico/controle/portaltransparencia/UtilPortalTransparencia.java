package br.com.webpublico.controle.portaltransparencia;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by romanini on 18/08/15.
 */
public abstract class UtilPortalTransparencia implements Serializable {

    public static String METHOD_POST = "POST";
    public static String METHOD_UPDATE = "PUT";

    public static String enviar(String urlServer, JSONObject conteudoJSON, String method) {
        StringBuilder resultado = new StringBuilder();
        try {

            URL url = new URL(urlServer);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();

            StringBuffer sbf = new StringBuffer(conteudoJSON.toString());

            byte bytes[] = sbf.toString().getBytes();
            os.write(bytes);
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));
            String output  = new String();
            while ((output = br.readLine()) != null) {
                resultado.append(output + "\n");
            }
            conn.disconnect();
        } catch (Exception e) {
            resultado.append(e.getMessage());
        }
        return resultado.toString();
    }

    public static String getNomeRemovendoExtensao(String nomeArquivo){
        if(nomeArquivo == null){
            return " - ";
        }
        return nomeArquivo.replace("-", " ").replace(".", " ").replace("/", " ")
            .replace("DOC", " ").replace("DOCX", " ").replace("PDF", " ").replace("XLS", " ").replace("XLSX", " ");
    }
}
