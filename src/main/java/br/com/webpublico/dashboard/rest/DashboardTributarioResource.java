package br.com.webpublico.dashboard.rest;


import br.com.webpublico.entidades.comum.dashboard.ConfiguracaoDeDashboard;
import br.com.webpublico.negocios.comum.dashboard.ConfiguracaoDeDashboardFacade;
import br.com.webpublico.webboard.webboarddto.comum.TipoDashboard;
import br.com.webpublico.webboard.webboarddto.tributario.ArrecadacaoPorTributoDTO;
import com.google.common.base.Strings;
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
@RequestMapping("/dashboard-tributario")
@Controller
public class DashboardTributarioResource implements Serializable {

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

    @RequestMapping(value = "/arrecadacao-por-tributo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardPorModalidadeContrato(@RequestParam(value = "ano") Integer ano,
                                                                 @RequestParam(value = "mes") Integer mes,
                                                                 @RequestParam(value = "idEntidade") Long idEntidade,
                                                                 @RequestParam(value = "idTributo") Long idTributo,
                                                                 @RequestParam(value = "quantidade") Integer quantidade,
                                                                 @RequestParam(value = "tipo") String tipo) throws UnsupportedEncodingException {
        try {
            if (ano != null && idEntidade != null) {
                ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
                if (config != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    ArrecadacaoPorTributoDTO dto = new ArrecadacaoPorTributoDTO();
                    dto.setAno(ano);
                    dto.setMes(mes);
                    dto.setIdEntidade(idEntidade);
                    dto.setQuantidade(quantidade);
                    dto.setIdTributo(idTributo);
                    if (!Strings.isNullOrEmpty(tipo)) {
                        for (TipoDashboard tipoDashboard : TipoDashboard.values()) {
                            if(tipoDashboard.getDescricao().equals(tipo)){
                                dto.setTipo(tipoDashboard);
                            }
                        }
                    }
                    HttpEntity<ArrecadacaoPorTributoDTO> request = new HttpEntity<ArrecadacaoPorTributoDTO>(dto, headers);
                    ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-tributario/arrecadacao-por-tributo"), HttpMethod.POST, request, String.class);
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
