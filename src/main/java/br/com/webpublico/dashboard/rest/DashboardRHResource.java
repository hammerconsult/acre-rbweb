package br.com.webpublico.dashboard.rest;


import br.com.webpublico.entidades.comum.dashboard.ConfiguracaoDeDashboard;
import br.com.webpublico.negocios.comum.dashboard.ConfiguracaoDeDashboardFacade;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Created by renato on 08/08/19.
 */
@RequestMapping("/dashboard-rh")
@Controller
public class DashboardRHResource implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ConfiguracaoDeDashboardFacade configuracaoDeDashboardFacade;

    @PostConstruct
    public void init() {
        try {
            configuracaoDeDashboardFacade = (ConfiguracaoDeDashboardFacade) new InitialContext().lookup("java:module/ConfiguracaoDeDashboardFacade");
        } catch (NamingException e) {
            logger.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/modalidade-contato", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardPorModalidadeContrato(@RequestParam(value = "ano") Integer ano,
                                                                 @RequestParam(value = "mes") Integer mes) throws UnsupportedEncodingException {
        try {
            if (ano != null && mes != null) {
                ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
                if (config != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity request = new HttpEntity<>(headers);
                    String url = config.getUrl() + "dashboard-rh/modalidade-contato?ano=" + ano + "&mes=" + mes + "&bancodados=" + config.getChave().toLowerCase();
                    ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, request, String.class);
                    if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/folha-de-pagamento", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardFolhaDePagamento(@RequestParam(value = "ano") Integer ano) throws UnsupportedEncodingException {
        try {
            if (ano != null) {
                ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
                if (config != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity request = new HttpEntity<>(headers);
                    String url = config.getUrl() + "dashboard-rh/folha-de-pagamento?ano=" + ano + "&bancodados=" + config.getChave().toLowerCase();
                    ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, request, String.class);
                    if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/afastamento", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardAfastamento(@RequestParam(value = "ano") Integer ano) throws UnsupportedEncodingException {
        try {
            if (ano != null) {
                ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
                if (config != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity request = new HttpEntity<>(headers);
                    String url = config.getUrl() + "dashboard-rh/afastamento?ano=" + ano + "&bancodados=" + config.getChave().toLowerCase();
                    ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, request, String.class);
                    if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
