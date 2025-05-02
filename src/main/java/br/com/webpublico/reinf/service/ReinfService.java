package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EmpregadorESocialDTO;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ConfiguracaoNaoEncontradaException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoWSRHFacade;
import br.com.webpublico.reinf.eventos.EventoReinfDTO;
import br.com.webpublico.reinf.eventos.EventosReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.reinf.eventos.domain.*;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Arrays;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;
import static br.com.webpublico.util.Util.readValue;

@Service(value = "reinfService")
public class ReinfService {

    private static final String GET_EMPREGADOR_PARAM = "/reinf/empregadores/{cnpj}";
    public static final String TAG = "\\{.*?\\}";
    protected static final String CNPJ_MGA = "09032577000150"; // TODO informar o CNPJ da ENTIDADE
    private final Logger log = LoggerFactory.getLogger(ReinfService.class);
    RestTemplate restTemplate = new RestTemplate();

    private ConfiguracaoWebServiceRH configuracaoWebServiceRH;
    private ConfiguracaoRHFacade configuracaoRHFacade;
    private ConfiguracaoWSRHFacade configuracaoWSRHFacade;

    public static String removerBarraFinalCaminho(String caminho) {
        String substring = caminho.substring(caminho.length() - 1);
        if ("/".equals(substring)) {
            return caminho.substring(0, caminho.length() - 1);
        }
        return caminho;
    }

    @PostConstruct
    public void init() {
        try {
            configuracaoRHFacade = (ConfiguracaoRHFacade) new InitialContext().lookup("java:module/ConfiguracaoRHFacade");
            configuracaoWSRHFacade = (ConfiguracaoWSRHFacade) new InitialContext().lookup("java:module/ConfiguracaoWSRHFacade");
            restTemplate = getRestTemplate();
        } catch (NamingException e) {
            log.debug("Erro no método ReinfService.init", e);
            log.error("Erro no método ReinfService.init. Habilite o debug para visualizar o erro.");
        } catch (Exception ex) {
            log.debug("Erro no método ReinfService.init", ex);
            log.error("Erro no método ReinfService.init. Habilite o debug para visualizar o erro.");
        }
    }

    public RestTemplate getRestTemplate() {
        try {
            restTemplate = new RestTemplate();
        } catch (Exception e) {
            log.debug("Erro no método ReinfService.getRestTemplate", e);
            log.error("Erro no método ReinfService.getRestTemplate. Habilite o debug para visualizar o erro.");
        }
        return restTemplate;
    }


    public void enviarEventoR1000(EventoR1000 r1000) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/r1000";
            restTemplate.postForEntity(urlWebpublico, r1000, EventoR1000.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR1000", e);
            log.error("Erro no método ReinfService.enviarEventoR1000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR1000", error);
            log.error("Erro no método ReinfService.enviarEventoR1000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR1000 getEventoR1000(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/r1000/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R1000.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR1000", e);
            log.error("Erro no método ReinfService.getEventoR1000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR1000", error);
            log.error("Erro no método ReinfService.getEventoR1000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR1000", error);
            log.error("Erro no método ReinfService.getEventoR1000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public void enviarEventoR1070(EventoR1070 r1070) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/r1070";
            restTemplate.postForEntity(urlWebpublico, r1070, EventoR1070.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método enviarEventoR1070", e);
            log.error("Erro no método enviarEventoR1070. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método enviarEventoR1070", error);
            log.error("Erro no método enviarEventoR1070. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR1070 getEventoR1070(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/r1070/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R1070.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método getEventoR1070", e);
            log.error("Erro no método getEventoR1070. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método getEventoR1070", error);
            log.error("Erro no método getEventoR1070. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método getEventoR1070", error);
            log.error("Erro no método enviarEventoR1070. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    public void enviarEventoR2010(EventoR2010 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/r2010";
            restTemplate.postForEntity(urlWebpublico, r, EventoR2010.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método enviarEventoR2010", e);
            log.error("Erro no método enviarEventoR2010. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método enviarEventoR2010", error);
            log.error("Erro no método enviarEventoR2010. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR2010 getEventoR2010(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/r2010/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R2010.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método getEventoR2010", e);
            log.error("Erro no método getEventoR2010. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método getEventoR2010", error);
            log.error("Erro no método getEventoR2010. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            error.printStackTrace();
            log.debug("Erro no método getEventoR2010", error);
            log.error("Erro no método getEventoR2010. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public void enviarEventoR2020(EventoR2020 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/r2020";
            restTemplate.postForEntity(urlWebpublico, r, EventoR2020.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR2020", e);
            log.error("Erro no método ReinfService.getEventoR1000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR2020", error);
            log.error("Erro no método ReinfService.enviarEventoR2020. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR2020 getEventoR2020(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/r2020/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R2020.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR2020", e);
            log.error("Erro no método ReinfService.getEventoR2020. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR2020", error);
            log.error("Erro no método ReinfService.getEventoR2020. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR2020", error);
            log.error("Erro no método ReinfService.getEventoR2020. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public void enviarEventoR2098(EventoR2098 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/r2098";
            restTemplate.postForEntity(urlWebpublico, r, EventoR2098.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR2098", e);
            log.error("Erro no método ReinfService.enviarEventoR2098. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR2098", error);
            log.error("Erro no método ReinfService.enviarEventoR2098. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public void enviarEventoR2098V2(EventoR2098 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/v2/r2098";
            restTemplate.postForEntity(urlWebpublico, r, EventoR2098.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR2098V2", e);
            log.error("Erro no método ReinfService.enviarEventoR2098V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR2098V2", error);
            log.error("Erro no método ReinfService.enviarEventoR2098V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR2098 getEventoR2098(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/r2098/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R2098.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR2098", e);
            log.error("Erro no método ReinfService.getEventoR2098. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR2098", error);
            log.error("Erro no método ReinfService.getEventoR2098. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR2098", error);
            log.error("Erro no método ReinfService.getEventoR2098. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public EventoR2098 getEventoR2098V2(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/v2/r2098/{cnpj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R2098V2.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR2098V2", e);
            log.error("Erro no método ReinfService.getEventoR2098V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR2098V2", error);
            log.error("Erro no método ReinfService.getEventoR2098V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR2098V2", error);
            log.error("Erro no método ReinfService.getEventoR2098V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    public void enviarEventoR2099(EventoR2099 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/r2099";
            restTemplate.postForEntity(urlWebpublico, r, EventoR2099.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR2099", e);
            log.error("Erro no método ReinfService.enviarEventoR2099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR2099", error);
            log.error("Erro no método ReinfService.enviarEventoR2099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR2099 getEventoR2099(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/r2099/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R2099.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR2099", e);
            log.error("Erro no método ReinfService.getEventoR2099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR2099", error);
            log.error("Erro no método ReinfService.getEventoR2099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR2099", error);
            log.error("Erro no método ReinfService.getEventoR2099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public void enviarEventoR9000(EventoR9000 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/r9000";
            restTemplate.postForEntity(urlWebpublico, r, EventoR9000.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR9000", e);
            log.error("Erro no método ReinfService.enviarEventoR9000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR9000", error);
            log.error("Erro no método ReinfService.enviarEventoR9000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR9000 getEventoR9000(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/r9000/{cnpj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R9000.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR9000", e);
            log.error("Erro no método ReinfService.getEventoR9000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR9000", error);
            log.error("Erro no método ReinfService.getEventoR9000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR9000", error);
            log.error("Erro no método ReinfService.getEventoR9000. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public EventoR9001 getEventoR9001(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/r9001/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R9001.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR9001", e);
            log.error("Erro no método ReinfService.getEventoR9001. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR9001", error);
            log.error("Erro no método ReinfService.getEventoR9001. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR9001", error);
            log.error("Erro no método ReinfService.getEventoR9001. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    public List<EventoReinfDTO> getEventosPorEmpregadorAndTipoArquivo(EmpregadorESocial empregadorESocial,
                                                                      TipoArquivoReinf tipoArquivoESocial,
                                                                      SituacaoESocial situacao,
                                                                      int page, int pageSize) {
        try {
            String url = "/reinf/empregadores/{cnpj}/eventos";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlWebpublico)
                .queryParam("tipo_arquivo", tipoArquivoESocial.name())
                .queryParam("page_size", pageSize)
                .queryParam("page", page);
            if (situacao != null) {
                builder.queryParam("situacao", situacao.name());
            }

            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpEntity<String> esocialResponse = restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.GET, entity, String.class);

            EventoReinfDTO[] eventos = readValue(esocialResponse.getBody(), EventoReinfDTO[].class);
            return Arrays.asList(eventos);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventosPorEmpregadorAndTipoArquivo", error);
            log.error("Erro no método ReinfService.getEventosPorEmpregadorAndTipoArquivo. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.debug("Erro no método ReinfService.getEventosPorEmpregadorAndTipoArquivo", e);
            log.error("Erro no método ReinfService.getEventosPorEmpregadorAndTipoArquivo. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    public EventoReinfDTO getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(String cnpj, TipoArquivoReinf tipoArquivoESocial, Mes mes, Integer ano, String cnpjFornecedor) {
        try {
            String url = "/reinf/empregadores/{cnpj}/evento/fornecedor";
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", cnpj);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlWebpublico)
                .queryParam("tipo_arquivo", tipoArquivoESocial.name())
                .queryParam("cnpj_fornecedor", cnpjFornecedor)
                .queryParam("mes", mes.getNumeroMes())
                .queryParam("ano", ano);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            HttpEntity<String> esocialResponse = restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.GET, entity, String.class);
            EventoReinfDTO[] eventos = readValue(esocialResponse.getBody(), EventoReinfDTO[].class);
            return (eventos != null && !Arrays.asList(eventos).isEmpty()) ? eventos[0] : null;
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJBeneficiado", error);
            log.error("Erro no método ReinfService.getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJBeneficiado. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.debug("Erro no método ReinfService.getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJBeneficiado", e);
            log.error("Erro no método ReinfService.getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJBeneficiado. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }


    public Integer getQuantidadeEventosPorEmpregadorAndTipoArquivo(EmpregadorESocial empregadorESocial,
                                                                   TipoArquivoReinf tipoArquivoESocial,
                                                                   SituacaoESocial situacao) {
        try {
            String url = "/reinf/empregadores/{cnpj}/eventos/quantidade";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlWebpublico)
                .queryParam("tipo_arquivo", tipoArquivoESocial.name());
            if (situacao != null) {
                builder.queryParam("situacao", situacao.name());
            }

            HttpEntity<?> entity = new HttpEntity<>(headers);
            HttpEntity<String> esocialResponse = restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.GET, entity, String.class);

            return readValue(esocialResponse.getBody(), Integer.class);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getQuantidadeEventosPorEmpregadorAndTipoArquivo", error);
            log.error("Erro no método ReinfService.getQuantidadeEventosPorEmpregadorAndTipoArquivo. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.debug("Erro no método ReinfService.getQuantidadeEventosPorEmpregadorAndTipoArquivo", e);
            log.error("Erro no método ReinfService.getQuantidadeEventosPorEmpregadorAndTipoArquivo. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    public List<EventoReinfDTO> getEventosPorEmpregadorAndPeriodo(EmpregadorESocial empregadorESocial,
                                                                  Integer mes, Integer ano) {
        try {
            String url = "/reinf/empregadores/{cnpj}/consultaeventosperiodo";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlWebpublico)
                .queryParam("mes", mes)
                .queryParam("ano", ano);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpEntity<String> esocialResponse = restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.GET, entity, String.class);

            EventoReinfDTO[] eventos = readValue(esocialResponse.getBody(), EventoReinfDTO[].class);
            return Arrays.asList(eventos);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventosPorEmpregadorAndPeriodo", error);
            log.error("Erro no método ReinfService.getEventosPorEmpregadorAndPeriodo. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.debug("Erro no método ReinfService.getEventosPorEmpregadorAndPeriodo", e);
            log.error("Erro no método ReinfService.getEventosPorEmpregadorAndPeriodo. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EventoReinfDTO> buscarEventosESocialNaoSincronizados(Boolean logESocial, String cnpj) {
        try {
            String url = "/reinf/eventos-nao-sincronizados/{cnpj}";
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", StringUtil.retornaApenasNumeros(cnpj));
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventoReinfDTO[] eventos = readValue(esocialResponse.getBody(), EventoReinfDTO[].class);
            return Arrays.asList(eventos);
        } catch (ResourceAccessException error) {
            if (logESocial) {
                log.error("Não foi possível estabelecer conexão,  Erro: ", error);
            }
            return Lists.newArrayList();
        } catch (HttpServerErrorException error) {
            if (logESocial) {
                log.error("Não foi possível estabelecer conexão com o e-social HttpServerErrorException");
            }
            return Lists.newArrayList();
        } catch (ConfiguracaoNaoEncontradaException error) {
            log.error(error.getMessage());
            return Lists.newArrayList();
        } catch (Exception e) {
            if (logESocial) {
                log.error("Erro: ", e);
            }
            return Lists.newArrayList();
        }
    }

    public void enviarEventosIntegrados(List<Long> ids) {
        try {
            String url = getBaseUrl() + "/esocial/eventos-integrados";
            log.debug("URL: [{}]", url);
            ResponseEntity<Long> esocialResponse = restTemplate.postForEntity(url, ids, Long.class);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventosIntegrados", error);
            log.error("Erro no método ReinfService.enviarEventosIntegrados. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public String getBaseUrl() {
        if (configuracaoWebServiceRH == null) {
            configuracaoWebServiceRH = configuracaoWSRHFacade.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.REINF);
        }
        String caminho = configuracaoRHFacade.buscarBaseUrlESocial(configuracaoWebServiceRH);
        if (caminho == null) {
            throw new ConfiguracaoNaoEncontradaException("Nenhuma configuração ESocial encontrada vigente");
        }
        return removerBarraFinalCaminho(caminho);
    }

    private SistemaFacade getSistemaFacadeSemInjetar() throws NamingException {
        return (SistemaFacade) new InitialContext()
            .lookup("java:module/SistemaFacade");
    }

    public Boolean isPerfilDev() {
        try {
            if (getSistemaFacadeSemInjetar().isPerfilDev()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public EventoR1000 getEventoR1000V2(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/v2/r1000/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R1000V2.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR1000V2", e);
            log.error("Erro no método ReinfService.getEventoR1000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR1000V2", error);
            log.error("Erro no método ReinfService.getEventoR1000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR1000V2", error);
            log.error("Erro no método ReinfService.getEventoR1000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public EventoR1070 getEventoR1070V2(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/v2/r1070/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R1070V2.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR1070V2", e);
            log.error("Erro no método ReinfService.getEventoR1070V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR1070V2", error);
            log.error("Erro no método ReinfService.getEventoR1070V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR1070V2", error);
            log.error("Erro no método ReinfService.getEventoR1070V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public void enviarEventoR1000V2(EventoR1000 r1000) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/v2/r1000";
            restTemplate.postForEntity(urlWebpublico, r1000, EventoR1000.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR1000V2", e);
            log.error("Erro no método ReinfService.enviarEventoR1000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR1000V2", error);
            log.error("Erro no método ReinfService.enviarEventoR1000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public void enviarEventoR1070V2(EventoR1070 r1070) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/v2/r1070";
            restTemplate.postForEntity(urlWebpublico, r1070, EventoR1070.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR1070V2", e);
            log.error("Erro no método ReinfService.enviarEventoR1070V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR1070V2", error);
            log.error("Erro no método ReinfService.enviarEventoR1070V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR2010 getEventoR2010V2(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/v2/r2010/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R2010V2.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR2010V2", e);
            log.error("Erro no método ReinfService.getEventoR2010V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR2010V2", error);
            log.error("Erro no método ReinfService.getEventoR2010V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR2010V2", error);
            log.error("Erro no método ReinfService.getEventoR2010V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public void enviarEventoR2010V2(EventoR2010 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/v2/r2010";
            restTemplate.postForEntity(urlWebpublico, r, EventoR2010.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR2010V2", e);
            log.error("Erro no método ReinfService.enviarEventoR2010V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR2010V2", error);
            log.error("Erro no método ReinfService.enviarEventoR2010V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR2020 getEventoR2020V2(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/v2/r2020/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosReinfDTO.R2020V2 r2020 = readValue(esocialResponse.getBody(), EventosReinfDTO.R2020V2.class);

            return r2020;
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR2020V2", e);
            log.error("Erro no método ReinfService.getEventoR2020V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR2020V2", error);
            log.error("Erro no método ReinfService.getEventoR2020V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR2020V2", error);
            log.error("Erro no método ReinfService.getEventoR2020V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public void enviarEventoR2020V2(EventoR2020 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/v2/r2020";
            restTemplate.postForEntity(urlWebpublico, r, EventoR2020.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR2020V2", e);
            log.error("Erro no método ReinfService.enviarEventoR2020V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR2020V2", error);
            log.error("Erro no método ReinfService.enviarEventoR2020V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR2099 getEventoR2099V2(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/v2/r2099/{cnpj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R2099V2.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR2099V2", e);
            log.error("Erro no método ReinfService.getEventoR2099V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR2099V2", error);
            log.error("Erro no método ReinfService.getEventoR2099V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR2099V2", error);
            log.error("Erro no método ReinfService.getEventoR2099V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    public void enviarEventoR2099V2(EventoR2099 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/v2/r2099";
            restTemplate.postForEntity(urlWebpublico, r, EventoR2099.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR2099V2", e);
            log.error("Erro no método ReinfService.enviarEventoR2099V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR2099V2", error);
            log.error("Erro no método ReinfService.enviarEventoR2099V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR4099 getEventoR4099(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/v2/r4099/{cnpj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R4099.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR4099", e);
            log.error("Erro no método ReinfService.getEventoR4099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR4099", error);
            log.error("Erro no método ReinfService.getEventoR4099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR4099", error);
            log.error("Erro no método ReinfService.getEventoR4099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public void enviarEventoR4099(EventoR4099 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/v2/r4099";
            restTemplate.postForEntity(urlWebpublico, r, EventoR4099.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR4099", e);
            log.error("Erro no método ReinfService.enviarEventoR4099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR4099", error);
            log.error("Erro no método ReinfService.enviarEventoR4099. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR9000 getEventoR9000V2(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/v2/r9000/{cnpj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R9000V2.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.getEventoR9000V2", e);
            log.error("Erro no método ReinfService.getEventoR9000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.getEventoR9000V2", error);
            log.error("Erro no método ReinfService.getEventoR9000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.debug("Erro no método ReinfService.getEventoR9000V2", error);
            log.error("Erro no método ReinfService.getEventoR9000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    public void enviarEventoR9000V2(EventoR9000 r) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/v2/r9000";
            restTemplate.postForEntity(urlWebpublico, r, EventoR9000.class);
        } catch (HttpClientErrorException e) {
            log.debug("Erro no método ReinfService.enviarEventoR9000V2", e);
            log.error("Erro no método ReinfService.enviarEventoR9000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.debug("Erro no método ReinfService.enviarEventoR9000V2", error);
            log.error("Erro no método ReinfService.enviarEventoR9000V2. Habilite o debug para visualizar o erro.");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EventoR4020 getEventoR4020(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String url = "/reinf/v2/r4020/{cnopj}";
            String value = empregadorESocial.getInfoCadastro().getNrInsc();
            String urlWebpublico = getBaseUrl() + url.replaceAll("\\{.*?\\}", value);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosReinfDTO.R4020.class);

        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            error.printStackTrace();
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    public void enviarEventoR4020(EventoR4020 r4020) {
        try {
            String urlWebpublico = getBaseUrl() + "/reinf/v2/r4020";
            restTemplate.postForEntity(urlWebpublico, r4020, EventoR4020.class);
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    public EmpregadorESocial getEmpregadorPorCnpj(String cnpj) {
        try {
            String urlWebpublico = getBaseUrl() + trocaTag(GET_EMPREGADOR_PARAM, cnpj);
            ResponseEntity<EmpregadorESocialDTO> esocialResponse = restTemplate.getForEntity(urlWebpublico, EmpregadorESocialDTO.class);
            EmpregadorESocial body = esocialResponse.getBody();
            return body;
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }

    }

    public static String trocaTag(String url, String valor) {
        return url.replaceAll("\\{.*?\\}", valor);
    }

    public List<EventoReinfDTO> getEventosReinfEmpregadorAndTipoArquivo(Entidade empregador, TipoArquivoReinf tipoArquivoESocial, SituacaoESocial situacao, int page, int pageSize) {
        String cnpj = getCnpj(empregador.getPessoaJuridica());
        List<EventoReinfDTO> eventosPorEmpregador = getEventosPorEmpregadorAndTipoArquivo(getEmpregadorPorCnpj(cnpj), tipoArquivoESocial, situacao, page, pageSize);
        return eventosPorEmpregador;
    }

    public Integer getQuantidadeEventosReinfPorEmpregadorAndTipoArquivo(Entidade empregador, TipoArquivoReinf tipoArquivoESocial, SituacaoESocial situacao) {
        String cnpj = getCnpj(empregador.getPessoaJuridica());
        return getQuantidadeEventosPorEmpregadorAndTipoArquivo(getEmpregadorPorCnpj(cnpj), tipoArquivoESocial, situacao);
    }

    public List<EventoReinfDTO> getEventosPorEmpregadorAndPeriodo(Entidade empregador, Integer mes, Integer ano) {
        String cnpj = getCnpj(empregador.getPessoaJuridica());
        List<EventoReinfDTO> eventosPorEmpregador = getEventosPorEmpregadorAndPeriodo(getEmpregadorPorCnpj(cnpj), mes, ano);
        return eventosPorEmpregador;
    }

    public EventoReinfDTO getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(Entidade empregador, TipoArquivoReinf tipoArquivoESocial, Mes mes, Integer ano, String cnpjFornecedor) {
        String cnpj = getCnpj(empregador.getPessoaJuridica());
        return getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(cnpj, tipoArquivoESocial, mes, ano, cnpjFornecedor);
    }

    private String getCnpj(PessoaJuridica pj) {
        if (pj != null) {
            return retornaApenasNumeros(pj.getCnpj());
        }
        return null;
    }
}
