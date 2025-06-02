package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.enums.URLESocial;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.*;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.*;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.*;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.BuscarEventoDTO;
import br.com.webpublico.esocial.dto.EmpregadorESocialDTO;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ConfiguracaoNaoEncontradaException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoWSRHFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
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
import javax.faces.application.FacesMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static br.com.webpublico.util.Util.readValue;

@Service(value = "eSocialService")
public class ESocialServiceImpl implements ESocialService {

    public static final String TAG = "\\{.*?\\}";
    protected static final String CNPJ_MGA = "09032577000150"; // TODO informar o CNPJ da ENTIDADE
    private final Logger log = LoggerFactory.getLogger(ESocialServiceImpl.class);
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

    public static String trocaTag(URLESocial url, String valor) {
        return url.getCaminho().replaceAll("\\{.*?\\}", valor);
    }

    @PostConstruct
    public void init() {
        try {
            configuracaoRHFacade = (ConfiguracaoRHFacade) new InitialContext().lookup("java:module/ConfiguracaoRHFacade");
            configuracaoWSRHFacade = (ConfiguracaoWSRHFacade) new InitialContext().lookup("java:module/ConfiguracaoWSRHFacade");
            restTemplate = getRestTemplate();
        } catch (NamingException e) {
            log.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            log.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @Override
    public EmpregadorESocial enviarEmpregador() {
        EmpregadorESocial empregador = getEmpregadorESocial();
        enviarEmpregador(empregador);
        return null;
    }

    public EmpregadorESocial getEmpregadorESocial() {
        EmpregadorESocial empregador = getNovoEmpregador();
        empregador.setDataInicioOperacao(new DateTime(2018, 1, 1, 0, 0).toDate());
        empregador.getInfoCadastro().setTpInsc(1);
        empregador.getInfoCadastro().setNrInsc(CNPJ_MGA);
        empregador.getInfoCadastro().setClassTrib("02");
        empregador.getInfoCadastro().setNatJurid("2061");
        empregador.setSenhaCertificado("asçdfjkasçlkjf");
        empregador.getInfoCadastro().setTpAmb(1);
        return empregador;
    }

    @Override
    @Deprecated
    public void enviarS1000() {
    }

    @Override
    public EventoS1000 enviarEventoS1000(EventoS1000 eventoS1000) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1000.getCaminho();
            ResponseEntity<EventoS1000> esocialResponse = restTemplate.postForEntity(url, eventoS1000, EventoS1000.class);
            EventoS1000 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS1030 enviarEventoS1030(EventoS1030 eventoS1030) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1030.getCaminho();
            ResponseEntity<EventoS1030> esocialResponse = restTemplate.postForEntity(url, eventoS1030, EventoS1030.class);
            EventoS1030 body = esocialResponse.getBody();
            return body;
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


    @Override
    public EventoS1020 enviarEventoS1020(EventoS1020 eventoS1020) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1020.getCaminho();
            ResponseEntity<EventoS1020> esocialResponse = restTemplate.postForEntity(url, eventoS1020, EventoS1020.class);
            EventoS1020 body = esocialResponse.getBody();
            return body;
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


    @Override
    public EventoS1005 enviarEventoS1005(EventoS1005 s1005) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1005.getCaminho();
            ResponseEntity<EventoS1005> esocialResponse = restTemplate.postForEntity(url, s1005, EventoS1005.class);
            EventoS1005 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS1010 enviarEventoS1010(EventoS1010 s1010) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1010.getCaminho();
            ResponseEntity<EventoS1010> esocialResponse = restTemplate.postForEntity(url, s1010, EventoS1010.class);
            EventoS1010 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS1000 getEventoS1000(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1000, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1000 s1000 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1000.class);

            return s1000;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1020 getEventoS1020(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1020, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1020 s1020 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1020.class);
            return s1020;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    @Override
    public EventoS1030 getEventoS1030(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1030, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1030 s1030 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1030.class);
            return s1030;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1035 getEventoS1035(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1035, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1035 s1035 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1035.class);
            return s1035;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1040 getEventoS1040(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1040, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1040 s1040 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1040.class);
            return s1040;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    @Override
    public EventoS1050 getEventoS1050(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1050, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1050 s1050 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1050.class);
            return s1050;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1035 enviarEventoS1035(EventoS1035 eventoS1035) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1035.getCaminho();
            ResponseEntity<EventoS1035> esocialResponse = restTemplate.postForEntity(url, eventoS1035, EventoS1035.class);
            EventoS1035 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS1040 enviarEventoS1040(EventoS1040 eventoS1040) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1040.getCaminho();
            ResponseEntity<EventoS1040> esocialResponse = restTemplate.postForEntity(url, eventoS1040, EventoS1040.class);
            EventoS1040 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS1050 enviarEventoS1050(EventoS1050 eventoS1050) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1050.getCaminho();
            ResponseEntity<EventoS1050> esocialResponse = restTemplate.postForEntity(url, eventoS1050, EventoS1050.class);
            EventoS1050 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS1005 getEventoS1005(EmpregadorESocial empregador) {
        try {
            Preconditions.checkNotNull(empregador);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1005, empregador.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1005 s1005 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1005.class);
            return s1005;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1010 getEventoS1010(EmpregadorESocial empregador) {
        try {
            Preconditions.checkNotNull(empregador);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1010, empregador.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1010 s1010 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1010.class);
            return s1010;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public void enviarEventosIntegrados(List<Long> ids) {
        try {
            String url = getBaseUrl() + URLESocial.POST_EVENTOS_INTEGRADOS.getCaminho();
            log.debug("URL: [{}]", url);
            ResponseEntity<Long> esocialResponse = restTemplate.postForEntity(url, ids, Long.class);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", "Ocorreu um erro ao integrar eventos com o e-social");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    @Override
    public EmpregadorESocial enviarEmpregador(EmpregadorESocial empregador) {
        try {
            String url = getBaseUrl() + URLESocial.POST_EMPREGADOR.getCaminho();
            log.debug("URL: [{}]", url);

            /*HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            MultiValueMap<String, EmpregadorESocial> map= new LinkedMultiValueMap<String, EmpregadorESocial>();
            map.add("empregadorESocial", empregador);

            HttpEntity<MultiValueMap<String, EmpregadorESocial>> request = new HttpEntity<MultiValueMap<String, EmpregadorESocial>>(map, headers);*/

            ResponseEntity<EmpregadorESocialDTO> esocialResponse = restTemplate.postForEntity(url, empregador, EmpregadorESocialDTO.class);
            EmpregadorESocial body = esocialResponse.getBody();
            return body;
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    @Override
    public EmpregadorESocial getEmpregadorPorCnpj(String cnpj) {
        try {
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_EMPREGADOR_PARAM, cnpj);
            ResponseEntity<EmpregadorESocialDTO> esocialResponse = restTemplate.getForEntity(urlWebpublico, EmpregadorESocialDTO.class);
            EmpregadorESocial body = esocialResponse.getBody();
            return body;
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    @Override
    public EmpregadorESocial getNovoEmpregador() {
        try {
            String urlWebpublico = getBaseUrl() + URLESocial.GET_EMPREGADOR.getCaminho();
            ResponseEntity<EmpregadorESocialDTO> esocialResponse = restTemplate.getForEntity(urlWebpublico, EmpregadorESocialDTO.class);
            EmpregadorESocial body = esocialResponse.getBody();
            return body;
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    @Override
    public List<EventoESocialDTO> getEventosPorEmpregador(EmpregadorESocial empregadorESocial) {
        try {
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_EVENTOS_POR_EMPREGADOR, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventoESocialDTO[] eventos = readValue(esocialResponse.getBody(), EventoESocialDTO[].class);
            if (eventos == null) {
                return Lists.newArrayList();
            }
            return Arrays.asList(eventos);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e);
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public List<EventoESocialDTO> getEventosPorEmpregadorAndTipoArquivo(EmpregadorESocial empregadorESocial,
                                                                        TipoArquivoESocial tipoArquivoESocial,
                                                                        SituacaoESocial situacao, int page, int pageSize) {
        try {
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_EVENTOS_POR_EMPREGADOR, empregadorESocial.getInfoCadastro().getNrInsc());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlWebpublico)
                .queryParam("tipo_arquivo", tipoArquivoESocial.name())
                .queryParam("situacao", situacao != null ? situacao.name() : null)
                .queryParam("page_size", pageSize)
                .queryParam("page", page);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpEntity<String> esocialResponse = restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.GET, entity, String.class);

            EventoESocialDTO[] eventos = readValue(esocialResponse.getBody(), EventoESocialDTO[].class);
            return Arrays.asList(eventos);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e);
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public Integer getQuantidadeEventosPorEmpregadorAndTipoArquivo(EmpregadorESocial empregadorESocial,
                                                                   TipoArquivoESocial tipoArquivoESocial,
                                                                   SituacaoESocial situacaoESocial) {
        try {
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_QUANTIDADE_EVENTOS_POR_EMPREGADOR, empregadorESocial.getInfoCadastro().getNrInsc());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);


            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlWebpublico)
                .queryParam("situacao", situacaoESocial != null ? situacaoESocial.name() : null)
                .queryParam("tipo_arquivo", tipoArquivoESocial.name());

            HttpEntity<?> entity = new HttpEntity<>(headers);
            HttpEntity<String> esocialResponse = restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.GET, entity, String.class);

            return readValue(esocialResponse.getBody(), Integer.class);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e);
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public List<EventoESocialDTO> getEventosPorIdEsocial(String idEsocial) {
        try {
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_EVENTOS_POR_IDENTIFICACAO, idEsocial) + "/";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlWebpublico)
                .queryParam("idEsocial", idEsocial);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpEntity<String> esocialResponse = restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.GET, entity, String.class);

            EventoESocialDTO[] eventos = readValue(esocialResponse.getBody(), EventoESocialDTO[].class);

            List<EventoESocialDTO> eventosOrdenados = Arrays.asList(eventos);
            eventosOrdenados.sort(Comparator.comparing(EventoESocialDTO::getDataOperacao).reversed());
            return eventosOrdenados;

        } catch (HttpServerErrorException error) {
            log.debug("Não foi possível buscar os registros do e-social");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.debug("Não foi possível buscar os registros do e-social");
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public List<EventoESocialDTO> getEventosPorIdEsocialOrCPF(BuscarEventoDTO dto) {
        try {
            String urlWebpublico = getBaseUrl() + URLESocial.GET_EVENTOS_POR_IDENTIFICACAO_ID_CPF.getCaminho();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);

            ResponseEntity<String> esocialResponse = restTemplate.postForEntity(urlWebpublico, dto, String.class);
            EventoESocialDTO[] eventos = readValue(esocialResponse.getBody(), EventoESocialDTO[].class);

            List<EventoESocialDTO> eventosOrdenados = Arrays.asList(eventos);
            eventosOrdenados.sort(Comparator.comparing(EventoESocialDTO::getDataOperacao).reversed());
            return eventosOrdenados;
        } catch (HttpServerErrorException error) {
            log.error("Não foi possível buscar os registros do e-social {}", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.error("Não foi possível buscar os registros do e-social {}", e);
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public List<EventoESocialDTO> getEventosPorEmpregadorAndIdEsocial(EmpregadorESocial empregadorESocial, String idEsocial) {
        try {
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_EVENTOS_POR_EMPREGADOR, empregadorESocial.getInfoCadastro().getNrInsc());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlWebpublico)
                .queryParam("idEsocial", idEsocial);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpEntity<String> esocialResponse = restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.GET, entity, String.class);

            EventoESocialDTO[] eventos = readValue(esocialResponse.getBody(), EventoESocialDTO[].class);
            return Arrays.asList(eventos);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e);
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }


    @Override
    public List<EventoESocialDTO> buscarEventosPorIdESocial(String idESocial, Boolean logESocial) {
        try {
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_EVENTOS_POR_ID_ESOCIAL, idESocial);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventoESocialDTO[] eventos = readValue(esocialResponse.getBody(), EventoESocialDTO[].class);
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
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            if (logESocial) {
                log.error("Erro: ", e);
            }
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EventoESocialDTO> buscarEventosESocialNaoSincronizados(Boolean logESocial, String cnpj) throws Exception {
        String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_EVENTOS_NAO_SINCRONIZADOS, StringUtil.retornaApenasNumeros(cnpj));
        ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
        if (esocialResponse.getBody() != null) {
            EventoESocialDTO[] eventos = readValue(esocialResponse.getBody(), EventoESocialDTO[].class);
            return Arrays.asList(eventos);
        } else {
            return null;
        }
    }

    @Override
    public String getBaseUrl() {
        if (configuracaoWebServiceRH == null) {
            configuracaoWebServiceRH = configuracaoWSRHFacade.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.E_SOCIAL);
        }
        String caminho = configuracaoRHFacade.buscarBaseUrlESocial(configuracaoWebServiceRH);
        if (caminho == null) {
            throw new ConfiguracaoNaoEncontradaException("Nenhuma configuração ESocial encontrada vigente");
        }
        return removerBarraFinalCaminho(caminho);
    }

    public RestTemplate getRestTemplate() {
        try {

            /*@Bean
            public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
                TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

                SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

                SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

                CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();

                HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();

                requestFactory.setHttpClient(httpClient);
                RestTemplate restTemplate = new RestTemplate(requestFactory);
                return restTemplate;
            }*/
            restTemplate = new RestTemplate();
        } catch (Exception e) {
            log.error("could not create SSL Context - cause::: {}", e.getMessage(), e);
        }
        return restTemplate;
    }

    @Override
    public EventoS1070 enviarEventoS1070(EventoS1070 eventoS1070) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1070.getCaminho();
            ResponseEntity<EventoS1070> esocialResponse = restTemplate.postForEntity(url, eventoS1070, EventoS1070.class);
            EventoS1070 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS2190 enviarEventoS2190(EventoS2190 eventoS2190) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2190.getCaminho();
            ResponseEntity<EventoS2190> esocialResponse = restTemplate.postForEntity(url, eventoS2190, EventoS2190.class);
            EventoS2190 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS1070 getEventoS1070(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1070, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1070 s1070 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1070.class);
            return s1070;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2190 getEventoS2190(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2190, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2190 s2190 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2190.class);
            return s2190;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2200 getEventoS2200(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2200, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2200 s2200 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2200.class);
            return s2200;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2220 getEventoS2220(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2220, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2220 s2220 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2220.class);
            return s2220;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    @Override
    public EventoS2200 enviarEventoS2200(EventoS2200 eventoS2200, AssistenteBarraProgresso assistenteBarraProgresso, ContratoFP contratoFP) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2200.getCaminho();
            ResponseEntity<EventoS2200> esocialResponse = restTemplate.postForEntity(url, eventoS2200, EventoS2200.class);
            EventoS2200 body = esocialResponse.getBody();
            return body;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            String mensagem = e.getResponseBodyAsString();
            if (e.getResponseBodyAsString().contains("Deve ser posterior à data de nascimento do trabalhador e igual ou posterior à data de início da obrigatoriedade")) {
                mensagem = contratoFP + " - Data de admissão do trabalhador - Deve ser posterior à data de nascimento do trabalhador e igual ou posterior à data de início da obrigatoriedade dos eventos não periódicos para o empregador.";
            }
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(mensagem));
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            String mensagem = error.getResponseBodyAsString();
            if (assistenteBarraProgresso == null) {
                assistenteBarraProgresso = new AssistenteBarraProgresso();
            }
            if (error.getResponseBodyAsString().contains("Deve ser posterior à data de nascimento do trabalhador e igual ou posterior à data de início da obrigatoriedade")) {
                mensagem = contratoFP + " - Data de admissão do trabalhador - Deve ser posterior à data de nascimento do trabalhador e igual ou posterior à data de início da obrigatoriedade dos eventos não periódicos para o empregador.";
            }
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(mensagem));
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    @Override
    public EventoS2220 enviarEventoS2220(EventoS2220 eventoS2220) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2220.getCaminho();
            ResponseEntity<EventoS2220> esocialResponse = restTemplate.postForEntity(url, eventoS2220, EventoS2220.class);
            EventoS2220 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS2210 getEventoS2210(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2210, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2210 s2210 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2210.class);
            return s2210;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1200 getEventoS1200(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1200, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1200 s1200 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1200.class);
            return s1200;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1202 getEventoS1202(EmpregadorESocial empregador) {
        try {
            Preconditions.checkNotNull(empregador);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1202, empregador.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1202 s1202 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1202.class);
            return s1202;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1200 enviarEventoS1200(EventoS1200 eventoS1200) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1200.getCaminho();
            ResponseEntity<EventoS1200> esocialResponse = restTemplate.postForEntity(url, eventoS1200, EventoS1200.class);
            EventoS1200 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS1298 getEventoS1298(EmpregadorESocial empregador) {
        try {
            Preconditions.checkNotNull(empregador);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1298, empregador.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1298 s1298 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1298.class);
            return s1298;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.info("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1298 enviarEventoS1298(EventoS1298 s1298) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1298.getCaminho();
            ResponseEntity<EventoS1298> esocialResponse = restTemplate.postForEntity(url, s1298, EventoS1298.class);
            return esocialResponse.getBody();
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

    @Override
    public EventoS1202 enviarEventoS1202(EventoS1202 s1202, VinculoFP vinculoFP) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1202.getCaminho();
            ResponseEntity<EventoS1202> esocialResponse = restTemplate.postForEntity(url, s1202, EventoS1202.class);
            return esocialResponse.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.info("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

    @Override
    public EventoS1295 getEventoS1295(EmpregadorESocial empregador) {
        try {
            Preconditions.checkNotNull(empregador);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1295, empregador.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1295 s1295 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1295.class);
            return s1295;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1295 enviarEventoS1295(EventoS1295 eventoS1295) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1295.getCaminho();
            ResponseEntity<EventoS1295> esocialResponse = restTemplate.postForEntity(url, eventoS1295, EventoS1295.class);
            return esocialResponse.getBody();
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

    @Override
    public EventoS1210 enviarEventoS1210(EventoS1210 s1210) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1210.getCaminho();
            ResponseEntity<EventoS1210> esocialResponse = restTemplate.postForEntity(url, s1210, EventoS1210.class);
            return esocialResponse.getBody();
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

    @Override
    public EventoS1210 getEventoS1210(EmpregadorESocial empregador) {
        try {
            Preconditions.checkNotNull(empregador);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1210, empregador.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            return readValue(esocialResponse.getBody(), EventosESocialDTO.S1210.class);
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1280 getEventoS1280(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1280, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1280 s1280 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1280.class);
            return s1280;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1280 enviarEventoS1280(EventoS1280 s1280) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1280.getCaminho();
            ResponseEntity<EventoS1280> esocialResponse = restTemplate.postForEntity(url, s1280, EventoS1280.class);
            return esocialResponse.getBody();
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

    @Override
    public EventoS1207 enviarEventoS1207(EventoS1207 eventoS1207) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1207.getCaminho();
            ResponseEntity<EventoS1207> esocialResponse = restTemplate.postForEntity(url, eventoS1207, EventoS1207.class);
            EventoS1207 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS1207 getEventoS1207(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1207, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1207 s1207 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1207.class);
            return s1207;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2205 getEventoS2205(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2205, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2205 S2205 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2205.class);
            return S2205;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2205 enviarEventoS2205(EventoS2205 eventoS2205) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2205.getCaminho();
            ResponseEntity<EventoS2205> esocialResponse = restTemplate.postForEntity(url, eventoS2205, EventoS2205.class);
            EventoS2205 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS2206 enviarEventoS2206(EventoS2206 eventoS2206) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2206.getCaminho();
            ResponseEntity<EventoS2206> esocialResponse = restTemplate.postForEntity(url, eventoS2206, EventoS2206.class);
            EventoS2206 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS2210 enviarEventoS2210(EventoS2210 eventoS2210) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2210.getCaminho();
            ResponseEntity<EventoS2210> esocialResponse = restTemplate.postForEntity(url, eventoS2210, EventoS2210.class);
            EventoS2210 body = esocialResponse.getBody();
            return body;
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

    @Override
    public EventoS2206 getEventoS2206(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2206, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2206 s2206 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2206.class);
            return s2206;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2230 enviarEventoS2230(EventoS2230 eventoS2230) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2230.getCaminho();
            ResponseEntity<EventoS2230> esocialResponse = restTemplate.postForEntity(url, eventoS2230, EventoS2230.class);
            EventoS2230 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2230 getEventoS2230(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2230, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2230 s2230 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2230.class);
            return s2230;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2231 enviarEventoS2231(EventoS2231 eventoS2231) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2231.getCaminho();
            ResponseEntity<EventoS2231> esocialResponse = restTemplate.postForEntity(url, eventoS2231, EventoS2231.class);
            EventoS2231 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2231 getEventoS2231(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2231, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2231 s2231 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2231.class);
            return s2231;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2250 enviarEventoS2250(EventoS2250 eventoS2250) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2250.getCaminho();
            ResponseEntity<EventoS2250> esocialResponse = restTemplate.postForEntity(url, eventoS2250, EventoS2250.class);
            EventoS2250 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2240 enviarEventoS2240(EventoS2240 eventoS2240) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2240.getCaminho();
            ResponseEntity<EventoS2240> esocialResponse = restTemplate.postForEntity(url, eventoS2240, EventoS2240.class);
            EventoS2240 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2240 getEventoS2240(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2240, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2240 s2240 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2240.class);
            return s2240;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2250 getEventoS2250(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2250, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2250 s2250 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2250.class);
            return s2250;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2298 getEventoS2298(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2298, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2298 s2298 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2298.class);
            return s2298;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2298 enviarEventoS2298(EventoS2298 eventoS2298) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2298.getCaminho();
            ResponseEntity<EventoS2298> esocialResponse = restTemplate.postForEntity(url, eventoS2298, EventoS2298.class);
            EventoS2298 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2299 enviarEventoS2299(EventoS2299 eventoS2299) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2299.getCaminho();
            ResponseEntity<EventoS2299> esocialResponse = restTemplate.postForEntity(url, eventoS2299, EventoS2299.class);
            EventoS2299 body = esocialResponse.getBody();
            return body;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2299 getEventoS2299(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2299, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2299 s2299 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2299.class);
            return s2299;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2300 enviarEventoS2300(EventoS2300 eventoS2300) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2300.getCaminho();
            ResponseEntity<EventoS2300> esocialResponse = restTemplate.postForEntity(url, eventoS2300, EventoS2300.class);
            EventoS2300 body = esocialResponse.getBody();
            return body;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2300 getEventoS2300(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2300, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2300 s2300 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2300.class);
            return s2300;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2306 enviarEventoS2306(EventoS2306 eventoS2306) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2306.getCaminho();
            ResponseEntity<EventoS2306> esocialResponse = restTemplate.postForEntity(url, eventoS2306, EventoS2306.class);
            EventoS2306 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2306 getEventoS2306(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2306, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2306 s2306 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2306.class);
            return s2306;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2400 enviarEventoS2400(EventoS2400 eventoS2400) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2400.getCaminho();
            ResponseEntity<EventoS2400> esocialResponse = restTemplate.postForEntity(url, eventoS2400, EventoS2400.class);
            EventoS2400 body = esocialResponse.getBody();
            return body;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2405 enviarEventoS2405(EventoS2405 eventoS2405) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2405.getCaminho();
            ResponseEntity<EventoS2405> esocialResponse = restTemplate.postForEntity(url, eventoS2405, EventoS2405.class);
            EventoS2405 body = esocialResponse.getBody();
            return body;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2410 enviarEventoS2410(EventoS2410 s2410) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2410.getCaminho();
            ResponseEntity<EventoS2410> esocialResponse = restTemplate.postForEntity(url, s2410, EventoS2410.class);
            EventoS2410 body = esocialResponse.getBody();
            return body;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro no envio do EventoS2410: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2416 enviarEventoS2416(EventoS2416 s2416) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2416.getCaminho();
            ResponseEntity<EventoS2416> esocialResponse = restTemplate.postForEntity(url, s2416, EventoS2416.class);
            EventoS2416 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro no envio do EventoS2410: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2400 getEventoS2400(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2400, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2400 s2400 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2400.class);
            return s2400;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2405 getEventoS2405(EmpregadorESocial empregador) {
        try {
            Preconditions.checkNotNull(empregador);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2405, empregador.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2405 s2405 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2405.class);
            return s2405;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    @Override
    public EventoS2418 enviarEventoS2418(EventoS2418 eventoS2418) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2418.getCaminho();
            ResponseEntity<EventoS2418> esocialResponse = restTemplate.postForEntity(url, eventoS2418, EventoS2418.class);
            EventoS2418 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2418 getEventoS2418(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2418, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2418 s2418 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2418.class);
            return s2418;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2399 enviarEventoS2399(EventoS2399 eventoS2400) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2399.getCaminho();
            ResponseEntity<EventoS2399> esocialResponse = restTemplate.postForEntity(url, eventoS2400, EventoS2399.class);
            EventoS2399 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2399 getEventoS2399(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2399, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2399 s2399 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2399.class);
            return s2399;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2410 getEventoS2410(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2410, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2410 s2410 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2410.class);
            return s2410;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2416 getEventoS2416(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2416, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2416 s2416 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2416.class);
            return s2416;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS2420 enviarEventoS2420(EventoS2420 eventoS2420) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S2420.getCaminho();
            ResponseEntity<EventoS2420> esocialResponse = restTemplate.postForEntity(url, eventoS2420, EventoS2420.class);
            EventoS2420 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS2420 getEventoS2420(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S2420, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S2420 s2420 = readValue(esocialResponse.getBody(), EventosESocialDTO.S2420.class);
            return s2420;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }


    @Override
    public EventoS3000 enviarEventoS3000(EventoS3000 eventoS3000) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S3000.getCaminho();
            ResponseEntity<EventoS3000> esocialResponse = restTemplate.postForEntity(url, eventoS3000, EventoS3000.class);
            EventoS3000 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS3000 enviarEventoS3000Simples(String idXMLEvento) {
        try {
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S3000_SIMPLES, idXMLEvento);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S3000 s3000 = readValue(esocialResponse.getBody(), EventosESocialDTO.S3000.class);
            return s3000;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS3000 getEventoS3000(String idXMLEvento) {
        try {
            Preconditions.checkNotNull(idXMLEvento);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S3000, idXMLEvento);
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S3000 s3000 = readValue(esocialResponse.getBody(), EventosESocialDTO.S3000.class);
            return s3000;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1300 enviarEventoS1300(EventoS1300 eventoS1300) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1300.getCaminho();
            ResponseEntity<EventoS1300> esocialResponse = restTemplate.postForEntity(url, eventoS1300, EventoS1300.class);
            EventoS1300 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS1300 getEventoS1300(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1300, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1300 s1300 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1300.class);
            return s1300;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public EventoS1299 enviarEventoS1299(EventoS1299 eventoS1299) {
        try {
            String url = getBaseUrl() + URLESocial.POST_S1299.getCaminho();
            ResponseEntity<EventoS1299> esocialResponse = restTemplate.postForEntity(url, eventoS1299, EventoS1299.class);
            EventoS1299 body = esocialResponse.getBody();
            return body;
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }

    @Override
    public EventoS1299 getEventoS1299(EmpregadorESocial empregadorESocial) {
        try {
            Preconditions.checkNotNull(empregadorESocial);
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_S1299, empregadorESocial.getInfoCadastro().getNrInsc());
            ResponseEntity<String> esocialResponse = restTemplate.getForEntity(urlWebpublico, String.class);
            EventosESocialDTO.S1299 s1299 = readValue(esocialResponse.getBody(), EventosESocialDTO.S1299.class);
            return s1299;
        } catch (HttpClientErrorException e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception error) {
            log.error("Erro: ", error);
            throw new ExcecaoNegocioGenerica(error.getMessage(), error);
        }
    }

    @Override
    public List<EventoESocialDTO> getEventosFolhaByEmpregador(EmpregadorESocial empregadorESocial, TipoArquivoESocial tipoArquivoESocial,
                                                              Integer mesApur, Integer anoApur, List<SituacaoESocial> situacoes,
                                                              String numeroRecibo, String idVinculo, String idXml, String cpfVinculo,
                                                              List<String> idsExoneracao) {
        try {
            StringBuilder situacaoESocial = new StringBuilder();
            situacoes.forEach(situacao -> {
                situacaoESocial.append(situacao.name()).append(",");
            });
            String urlWebpublico = getBaseUrl() + trocaTag(URLESocial.GET_EVENTOS_POR_EMPREGADOR_AND_FILTROS, empregadorESocial.getInfoCadastro().getNrInsc());
            HttpHeaders headers = new HttpHeaders();
            headers.set("Esocial", MediaType.APPLICATION_JSON_VALUE);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlWebpublico)
                .queryParam("tipo_arquivo", tipoArquivoESocial.name())
                .queryParam("mesApur", mesApur)
                .queryParam("anoApur", anoApur)
                .queryParam("situacoesESocial", situacaoESocial.toString())
                .queryParam("numeroRecibo", numeroRecibo)
                .queryParam("idVinculo", idVinculo)
                .queryParam("cpfVinculo", cpfVinculo)
                .queryParam("idXml", idXml);

            if (idsExoneracao != null && !idsExoneracao.isEmpty()) {
                idsExoneracao.forEach(id -> builder.queryParam("idsExoneracao", id));
            }

            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpEntity<String> esocialResponse = restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.GET, entity, String.class);

            EventoESocialDTO[] eventos = Util.carregarEventoEsocial(esocialResponse.getBody(), EventoESocialDTO[].class);
            return Arrays.asList(eventos);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", error);
            log.debug("Detalhes do Erro: ", error.getResponseBodyAsString());
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        } catch (Exception e) {
            log.error("Erro: ", e);
            log.debug("Detalhes do Erro: ", e);
            throw new ExcecaoNegocioGenerica(e.getMessage(), e);
        }
    }


    @Override
    public void enviarEventosAtualizarDescricao(List<EventoESocialDTO> eventos) {
        try {
            String url = getBaseUrl() + URLESocial.POST_ATUALIZAR_DESCRICOES.getCaminho();
            ResponseEntity<EventoESocialDTO> esocialResponse = restTemplate.postForEntity(url, eventos, EventoESocialDTO.class);
        } catch (HttpServerErrorException error) {
            log.error("Erro: ", "Ocorreu um erro ao integrar eventos com o e-social");
            throw new ExcecaoNegocioGenerica(error.getResponseBodyAsString(), error);
        }
    }

}
