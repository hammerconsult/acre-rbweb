package br.com.webpublico.nfse.rest;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.DataUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by renato on 22/02/2020.
 */
@RequestMapping("/dashboard-nfse")
@Controller
public class DashboardNFSEResource implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SistemaFacade sistemaFacade;
    private NotaFiscalFacade notaFiscalFacade;

    @PostConstruct
    public void init() {
        try {
            sistemaFacade = (SistemaFacade) new InitialContext().lookup("java:module/SistemaFacade");
            notaFiscalFacade = (NotaFiscalFacade) new InitialContext().lookup("java:module/NotaFiscalFacade");

        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/notas-por-naturezaoperacao", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardNotasPorNaturezaOperacao() throws UnsupportedEncodingException {
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject data = new JSONObject();
            data.put("labels", getJsonNaturezas());
            data.put("datasets", getJsonValores(DataUtil.getAno(sistemaFacade.getDataOperacao())));

            jsonObject.put("type", "bar");
            jsonObject.put("data", data);
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/notas-por-pessoa", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardNotasPorPessoa() throws UnsupportedEncodingException {
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject data = new JSONObject();
            Integer ano = DataUtil.getAno(sistemaFacade.getDataOperacao());
            List<Object[]> pessoas = notaFiscalFacade.buscarMaioresPessoasEmitentesDeNota(ano);

            data.put("labels", getJsonPessoas(pessoas));
            data.put("datasets", getJsonValoresPessoas(pessoas, ano));

            jsonObject.put("type", "bar");
            jsonObject.put("data", data);
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/guia-emitidas", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardGuiasEmitida() throws UnsupportedEncodingException {
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject data = new JSONObject();
            data.put("labels", getJsonMes());
            data.put("datasets", getJsonValoresGuiaEmitida(DataUtil.getAno(sistemaFacade.getDataOperacao())));

            jsonObject.put("type", "bar");
            jsonObject.put("data", data);
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/iss-arrecadado", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardTotalIssArrecadado() throws UnsupportedEncodingException {
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject data = new JSONObject();
            data.put("labels", getJsonMes());
            data.put("datasets", getJsonValoresTotalISS(DataUtil.getAno(sistemaFacade.getDataOperacao())));

            jsonObject.put("type", "bar");
            jsonObject.put("data", data);
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/servico-arrecadado", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardTotalServico() throws UnsupportedEncodingException {
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject data = new JSONObject();
            data.put("labels", getJsonMes());
            data.put("datasets", getJsonValoresTotalServico(DataUtil.getAno(sistemaFacade.getDataOperacao())));

            jsonObject.put("type", "bar");
            jsonObject.put("data", data);
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/notas-emitidas", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardNotasEmitidas() throws UnsupportedEncodingException {
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject data = new JSONObject();
            data.put("labels", getJsonMes());
            data.put("datasets", getJsonValoresTotalNotas(DataUtil.getAno(sistemaFacade.getDataOperacao())));

            jsonObject.put("type", "bar");
            jsonObject.put("data", data);
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public JSONArray getJsonValoresTotalNotas(Integer ano) throws JSONException {
        JSONArray jsonObject = new JSONArray();
        jsonObject.put(getJsonDataValorTotalNotas(ano, "33, 112, 146"));
        //jsonObject.put(getJsonDataValorTotalNotas(ano - 1, "54, 162, 235"));
        return jsonObject;
    }

    public JSONObject getJsonDataValorTotalNotas(Integer ano, String cor) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("label", "Quantidade em " + ano);
        jsonObject.put("backgroundColor", "rgba(" + cor + ", 0.2)");
        jsonObject.put("borderColor", "rgba(" + cor + ", 1)");
        jsonObject.put("borderWidth", "1");
        /*jsonObject.put("hoverBackgroundColor", "rgba(255,99,132,0.4)");
        jsonObject.put("hoverBorderColor", "rgba(255,99,132,1)");*/
        jsonObject.put("data", getJsonValorTotalNotas(ano));
        return jsonObject;
    }

    private JSONArray getJsonValorTotalNotas(Integer ano) {
        JSONArray jsonObject = new JSONArray();
        for (Mes e : Mes.values()) {
            jsonObject.put(notaFiscalFacade.buscarTotalDeNotas(ano, e));
        }
        return jsonObject;
    }

    public JSONArray getJsonValoresTotalServico(Integer ano) throws JSONException {
        JSONArray jsonObject = new JSONArray();
        jsonObject.put(getJsonDataValorTotalServico(ano, "102, 176, 163"));
        //jsonObject.put(getJsonDataValorTotalServico(ano - 1, "54, 162, 235"));
        return jsonObject;
    }

    public JSONObject getJsonDataValorTotalServico(Integer ano, String cor) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("label", "Quantidade em " + ano);
        jsonObject.put("backgroundColor", "rgba(" + cor + ", 0.2)");
        jsonObject.put("borderColor", "rgba(" + cor + ", 1)");
        jsonObject.put("borderWidth", "1");
        /*jsonObject.put("hoverBackgroundColor", "rgba(255,99,132,0.4)");
        jsonObject.put("hoverBorderColor", "rgba(255,99,132,1)");*/
        jsonObject.put("data", getJsonValorTotalServico(ano));
        return jsonObject;
    }

    private JSONArray getJsonValorTotalServico(Integer ano) {
        JSONArray jsonObject = new JSONArray();
        for (Mes e : Mes.values()) {
            jsonObject.put(notaFiscalFacade.buscarValorTotalDeServico(ano, e));
        }
        return jsonObject;
    }

    public JSONArray getJsonValoresTotalISS(Integer ano) throws JSONException {
        JSONArray jsonObject = new JSONArray();
        jsonObject.put(getJsonDataValorTotalISS(ano, "238, 84, 125"));
        //jsonObject.put(getJsonDataValorTotalISS(ano - 1, "54, 162, 235"));
        return jsonObject;
    }

    public JSONObject getJsonDataValorTotalISS(Integer ano, String cor) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("label", "Quantidade em " + ano);
        jsonObject.put("backgroundColor", "rgba(" + cor + ", 0.2)");
        jsonObject.put("borderColor", "rgba(" + cor + ", 1)");
        jsonObject.put("borderWidth", "1");
        /*jsonObject.put("hoverBackgroundColor", "rgba(255,99,132,0.4)");
        jsonObject.put("hoverBorderColor", "rgba(255,99,132,1)");*/
        jsonObject.put("data", getJsonValorTotalIss(ano));
        return jsonObject;
    }

    private JSONArray getJsonValorTotalIss(Integer ano) {
        JSONArray jsonObject = new JSONArray();
        for (Mes e : Mes.values()) {
            jsonObject.put(notaFiscalFacade.buscarValorTotalISS(ano, e));
        }
        return jsonObject;
    }

    public JSONArray getJsonValoresGuiaEmitida(Integer ano) throws JSONException {
        JSONArray jsonObject = new JSONArray();
        jsonObject.put(getJsonDataGuiEmitida(ano, "203, 32, 36"));
        //jsonObject.put(getJsonDataGuiEmitida(ano - 1, "54, 162, 235"));
        return jsonObject;
    }

    public JSONObject getJsonDataGuiEmitida(Integer ano, String cor) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("label", "Quantidade em " + ano);
        jsonObject.put("backgroundColor", "rgba(" + cor + ", 0.2)");
        jsonObject.put("borderColor", "rgba(" + cor + ", 1)");
        jsonObject.put("borderWidth", "1");
        /*jsonObject.put("hoverBackgroundColor", "rgba(255,99,132,0.4)");
        jsonObject.put("hoverBorderColor", "rgba(255,99,132,1)");*/
        jsonObject.put("data", getJsonValorGuiasEmitidas(ano));
        return jsonObject;
    }

    private JSONArray getJsonValorGuiasEmitidas(Integer ano) {
        JSONArray jsonObject = new JSONArray();
        for (Mes e : Mes.values()) {
            jsonObject.put(notaFiscalFacade.buscarTotalDeGuias(ano, e));
        }
        return jsonObject;
    }

    public JSONArray getJsonPessoas(List<Object[]> pessoas) {
        JSONArray jsonObject = new JSONArray();
        for (Object[] e : pessoas) {
            jsonObject.put(e[1]);
        }
        return jsonObject;
    }

    public JSONArray getJsonValoresPessoas(List<Object[]> pessoas, Integer ano) throws JSONException {
        JSONArray jsonObject = new JSONArray();
        jsonObject.put(getJsonDataPessoas(pessoas, ano));
        return jsonObject;
    }

    public JSONObject getJsonDataPessoas(List<Object[]> pessoas, Integer ano) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("label", "Quantidade em " + ano);
        jsonObject.put("backgroundColor", "rgba(" + "101, 40, 69" + ", 0.2)");
        jsonObject.put("borderColor", "rgba(" + "101, 40, 69" + ", 1)");
        jsonObject.put("borderWidth", "1");
        /*jsonObject.put("hoverBackgroundColor", "rgba(255,99,132,0.4)");
        jsonObject.put("hoverBorderColor", "rgba(255,99,132,1)");*/
        jsonObject.put("data", getJsonValorNotasPorPessoa(pessoas));
        return jsonObject;
    }

    private JSONArray getJsonValorNotasPorPessoa(List<Object[]> pessoas) {
        JSONArray jsonObject = new JSONArray();
        for (Object[] e : pessoas) {
            jsonObject.put(e[0]);
        }
        return jsonObject;
    }

    public JSONArray getJsonValores(Integer ano) throws JSONException {
        JSONArray jsonObject = new JSONArray();
        jsonObject.put(getJsonData(ano, "13, 64, 91"));
        //jsonObject.put(getJsonData(ano - 1, "54, 162, 235"));
        return jsonObject;
    }

    public JSONObject getJsonData(Integer ano, String cor) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("label", "Quantidade em " + ano);
        jsonObject.put("backgroundColor", "rgba(" + cor + ", 0.2)");
        jsonObject.put("borderColor", "rgba(" + cor + ", 1)");
        jsonObject.put("borderWidth", "1");
        /*jsonObject.put("hoverBackgroundColor", "rgba(255,99,132,0.4)");
        jsonObject.put("hoverBorderColor", "rgba(255,99,132,1)");*/
        jsonObject.put("data", getJsonValorNotasPorNatureza(ano));
        return jsonObject;
    }

    private JSONArray getJsonValorNotasPorNatureza(Integer ano) {
        JSONArray jsonObject = new JSONArray();
        for (Exigibilidade e : Exigibilidade.values()) {
            jsonObject.put(notaFiscalFacade.buscarValorPorNatureza(ano, e));
        }
        return jsonObject;
    }


    public JSONArray getJsonNaturezas() {
        JSONArray jsonObject = new JSONArray();
        for (Exigibilidade e : Exigibilidade.values()) {
            jsonObject.put(e.getDescricao());
        }
        return jsonObject;
    }

    public JSONArray getJsonMes() {
        JSONArray jsonObject = new JSONArray();
        for (Mes e : Mes.values()) {
            jsonObject.put(e.getDescricao());
        }
        return jsonObject;
    }
}
