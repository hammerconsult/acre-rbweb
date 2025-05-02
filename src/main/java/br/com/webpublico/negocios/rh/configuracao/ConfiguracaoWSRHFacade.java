package br.com.webpublico.negocios.rh.configuracao;

import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.GeradorTokenPontoDTO;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.RetornoGeracaoTokenPonto;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.rh.GeracaoTokenException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by William on 23/08/2018.
 */
@Stateless
public class ConfiguracaoWSRHFacade extends AbstractFacade<ConfiguracaoWebServiceRH> {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracaoWSRHFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoWSRHFacade() {
        super(ConfiguracaoWebServiceRH.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoWebServiceRH getConfiguracaoPorTipoDaKeyCorrente(TipoWebService tipo) {
        return getConfiguracaoPorTipoDaKeyCorrente(tipo, sistemaFacade.getUsuarioBancoDeDados());
    }

    public ConfiguracaoWebServiceRH getConfiguracaoPorTipoDaKeyCorrente(TipoWebService tipo, String chave) {
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigenteDataAtual();
        for (ConfiguracaoWebServiceRH config : configuracaoRH.getConfiguracaoWebServiceRH()) {
            if (tipo.equals(config.getTipoWebService()) && chave.equals(config.getChave())) {
                return config;
            }
        }
        return null;
    }

    public RetornoGeracaoTokenPonto gerarToken(ConfiguracaoWebServiceRH configuracaoWebServiceRH) throws JSONException, GeracaoTokenException {
        RetornoGeracaoTokenPonto retornoToken = new RetornoGeracaoTokenPonto();
        try {
            RestTemplate restTemplate = new RestTemplate();
            GeradorTokenPontoDTO geradorTokenPontoDTO = criarDTOToken(configuracaoWebServiceRH);

            HttpEntity<GeradorTokenPontoDTO> requestEntity = new HttpEntity<>(geradorTokenPontoDTO, montarHeader());
            String url = configuracaoWebServiceRH.getUrl() + "/oauth/token";
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            validarGeracaoToken(responseEntity);

            if (responseEntity.getStatusCode().equals(HttpStatus.OK) && responseEntity.getBody() != null && !responseEntity.getBody().isEmpty()) {
                JSONObject jsonObject = new JSONObject(responseEntity.getBody());
                retornoToken.setToken(jsonObject.get("access_token").toString());
                salvarTokenNaConfiguracaoWebServiceRH(configuracaoWebServiceRH, jsonObject);
            }
        } catch (GeracaoTokenException en) {
            retornoToken.setStatusGeracaoToken(en.getMessage() + "- Verifique os campos Client ID e Client Secret na aba WebServices nas configurações do RH, no item correspondente.");
            logger.error(en.getMessage(), "- Verifique os campos Client ID e Client Secret na aba WebServices nas configurações do RH, no item correspondente.");
        } catch (Exception ex) {
            retornoToken.setStatusGeracaoToken("Erro na geração do token " + ex.getMessage());
            logger.error("Erro na geração do token {}", ex.getMessage());
        }
        return retornoToken;
    }

    private HttpHeaders montarHeader() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return requestHeaders;
    }

    private void salvarTokenNaConfiguracaoWebServiceRH(ConfiguracaoWebServiceRH configuracaoWebServiceRH, JSONObject jsonObject) {
        configuracaoWebServiceRH.setToken(jsonObject.get("access_token").toString());
        configuracaoWebServiceRH.setValidadeToken(DateUtils.addSeconds(new Date(), jsonObject.getInt("expires_in")));
        salvar(configuracaoWebServiceRH);
    }

    public RetornoGeracaoTokenPonto buscarToken(ConfiguracaoWebServiceRH configuracaoWebServiceRH) {
        if (configuracaoWebServiceRH.tokenValido()) {
            return new RetornoGeracaoTokenPonto(configuracaoWebServiceRH.getToken(), "");
        }
        return gerarToken(configuracaoWebServiceRH);
    }

    private GeradorTokenPontoDTO criarDTOToken(ConfiguracaoWebServiceRH configuracaoWebServiceRH) throws JSONException {
        GeradorTokenPontoDTO dto = new GeradorTokenPontoDTO();
        dto.setGrant_type("client_credentials");
        dto.setClient_id(configuracaoWebServiceRH.getClientId());
        dto.setClient_secret(configuracaoWebServiceRH.getClientSecret());
        return dto;
    }

    private void validarGeracaoToken(ResponseEntity<String> responseEntity) throws GeracaoTokenException {
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new GeracaoTokenException("Na geração do token houve o erro HTTP " + responseEntity.getStatusCode().toString() + responseEntity.getBody());
        }
    }
}
