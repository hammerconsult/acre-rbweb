package br.com.webpublico.controle.portaltransparencia;

import br.com.webpublico.util.StringUtil;
import org.json.JSONObject;

/**
 * Created by renat on 13/04/2016.
 */
public class ObjetoPortalTransparencia {


    private Object objeto;
    private JSONObject jsonObject;
    private String url;
    private String metodo;

    public ObjetoPortalTransparencia(Object objeto, JSONObject jsonObject, String url, String metodo) {
        this.objeto = objeto;
        this.jsonObject = jsonObject;
        this.url = url;
        this.metodo = metodo;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public Object getObjeto() {
        return objeto;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getUrl() {
        return url;
    }


}
