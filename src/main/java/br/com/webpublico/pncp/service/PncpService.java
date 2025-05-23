package br.com.webpublico.pncp.service;

import br.com.webpublico.entidades.ConfiguracaoLicitacao;
import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.enums.administrativo.UrlPncp;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoLicitacaoFacade;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.pncp.dto.*;
import br.com.webpublico.pncp.entidade.EventoPncp;
import br.com.webpublico.pncp.entidade.LogEventoPncp;
import br.com.webpublico.pncp.enums.OperacaoPncp;
import br.com.webpublico.pncp.enums.SituacaoPncp;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class PncpService implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(PncpService.class);

    @PersistenceContext
    protected EntityManager em;
    private final String DIGITO_IDENTIFICADOR_CONTRATACAO = "1";
    private final String URL_API_ORGAO_ENTIDADE = "/api/v1/orgao-entidades";
    private final String URL_API_UNIDADES = "/api/v1/unidades";
    private final String URL_API_EVENTO = "/api/v1/evento";
    private final String URL_API_CONTRATACOES = "/api/v1/contratacoes/";
    private final String URL_API_ATA = "/api/v1/ata-registro-preco/";
    private final String URL_API_CONTRATO_EMPENHO = "/api/v1/contrato-empenho/";
    private final String URL_API_DOCUMENTOS = "/api/v1/documentos/";
    private final String URL_API_ITENS = "/api/v1/itens/";
    private final String URL_API_PCA = "/api/v1/pca/";

    private final ObjectMapper objectMapper = new ObjectMapper();
    protected RestTemplate restTemplate = new RestTemplate();
    private String baseUrl;

    protected SistemaFacade sistemaFacade;
    protected ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    protected ConfiguracaoTributario configuracaoTributario;
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;

    @PostConstruct
    public void init() {
        try {
            sistemaFacade = (SistemaFacade) Util.getFacadeViaLookup("java:module/SistemaFacade");
            configuracaoTributarioFacade = (ConfiguracaoTributarioFacade) Util.getFacadeViaLookup("java:module/ConfiguracaoTributarioFacade");
            configuracaoLicitacaoFacade = (ConfiguracaoLicitacaoFacade) Util.getFacadeViaLookup("java:module/ConfiguracaoLicitacaoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    public static PncpService getService() {
        return (PncpService) Util.getSpringBeanPeloNome("pncpService");
    }

    public String getBaseUrl(ValidacaoException ve) {
        if (baseUrl == null) {
            ConfiguracaoLicitacao configuracaoLicitacao = configuracaoLicitacaoFacade.buscarConfiguracao();
            String urlConfiguracaoLicitacao = "<b><a style='color: red;' href='" + FacesUtil.getRequestContextPath() + "/configuracao/licitacao/editar/" + configuracaoLicitacao.getId() + "/' target='_blank'>Ajustar</a></b>";
            if (sistemaFacade.isPerfilDev()) {
                baseUrl = configuracaoLicitacao.getUrlPncpServiceHomologacao();

                if (baseUrl == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Deve ser informado a URL de homologação do pncp-service! ".concat(urlConfiguracaoLicitacao));
                    ve.lancarException();
                }
            } else {
                baseUrl = configuracaoLicitacao.getUrlPncpServiceProducao();
                if (baseUrl == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Deve ser informado a URL de produção do pncp-service! ".concat(urlConfiguracaoLicitacao));
                    ve.lancarException();
                }
            }
        }
        return baseUrl;
    }

    public void limparBaseUrl() {
        baseUrl = null;
    }

    protected ConfiguracaoTributario getConfiguracaoTributario() {
        if (configuracaoTributario == null) {
            return configuracaoTributarioFacade.lista().get(0);
        }
        return configuracaoTributario;
    }

    protected void tratarErroGenerico(ValidacaoException ve, HttpClientErrorException ex) {
        try {
            RespostaErroDTO respostaErroDTO = objectMapper.readValue(ex.getResponseBodyAsString().getBytes(StandardCharsets.ISO_8859_1), RespostaErroDTO.class);
            ve.adicionarMensagemWarn("Aviso!", respostaErroDTO.getMensagem());
        } catch (Exception e) {
            logger.error("Erro no tratarErroGenerico: ".concat(e.getMessage()));
            throw new ExcecaoNegocioGenerica("");
        }
    }

    protected boolean isPerfilDev() {
        return sistemaFacade.isPerfilDev();
    }

    public String getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente().getNome();
    }

    public String montarUrlPncp(String prefixo, String idPncp, Integer ano, String sequencial) {
        String urlPncp = isPerfilDev() ? UrlPncp.PNCP_HOMOLOGACAO.getDescricao() : UrlPncp.PNCP_PRODUCAO.getDescricao();

        if (urlPncp != null) {
            return urlPncp.concat(prefixo).concat(idPncp).concat("/").concat(ano.toString()).concat("/").concat(sequencial);
        }
        return null;
    }

    public String montarIdContratacaoPncp(String idPncp, Integer anoCompra, String sequencialIdPncp) {
        String sequencialFormatado = String.format("%06d", Integer.parseInt(sequencialIdPncp));

        return String.format("%s-%s-%s/%s", idPncp, DIGITO_IDENTIFICADOR_CONTRATACAO, sequencialFormatado, anoCompra);
    }

    public void verificarOrgaoEntidadeCadastradaPncp(List<OrgaoEntidadeConsultaDTO> entidades, ValidacaoException ve) {
        for (OrgaoEntidadeConsultaDTO entidade : entidades) {
            try {
                restTemplate.getForEntity(getBaseUrl(ve) + URL_API_ORGAO_ENTIDADE.concat("/") + entidade.getCnpj(), OrgaoEntidadeConsultaDTO.class);
                entidade.setIntegrado(true);
            } catch (HttpClientErrorException ex) {
                entidade.setIntegrado(false);
                tratarErroGenerico(ve, ex);
            }
        }
    }

    public void verificarUnidadeCadastradaPncp(List<OrgaoEntidadeConsultaDTO> entidades, ValidacaoException ve) {
        for (OrgaoEntidadeConsultaDTO orgaoEnt : entidades) {
            for (UnidadeConsultaDTO unid : orgaoEnt.getUnidades()) {
                try {
                    String url = getBaseUrl(ve) + URL_API_UNIDADES.concat("/") + orgaoEnt.getCnpj() + "/unidades/" + unid.getCodigoUnidade();
                    ResponseEntity<UnidadeConsultaDTO> forEntity = restTemplate.getForEntity(url, UnidadeConsultaDTO.class);
                    unid.setIntegradoPNCP(forEntity.getStatusCode().is2xxSuccessful());
                } catch (HttpClientErrorException ex) {
                    unid.setIntegradoPNCP(false);
                }
            }
        }
    }

    public List<ContratoEmpenhoConsultaDTO> buscarContratoEmpenho(ValidacaoException ve, FiltroConsultaDTO filtro) {
        List<ContratoEmpenhoConsultaDTO> list = Lists.newArrayList();
        try {
            ResponseEntity<ContratoEmpenhoConsultaDTO[]> responseEntity = restTemplate.getForEntity(
                getBaseUrl(ve) + URL_API_CONTRATO_EMPENHO + "/get-contrato-empenho/"
                    + filtro.getDataInicialAsString() + "/"
                    + filtro.getDataFinalAsString() + "/"
                    + filtro.getCodigoUnidade(),
                ContratoEmpenhoConsultaDTO[].class);
            list.addAll(Arrays.asList(responseEntity.getBody()));
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
        return list;
    }

    public List<AtaRegistroPrecoConsultaDTO> buscarAtaRegistroPreco(ValidacaoException ve, FiltroConsultaDTO filtro) {
        List<AtaRegistroPrecoConsultaDTO> list = Lists.newArrayList();
        try {
            ResponseEntity<AtaRegistroPrecoConsultaDTO[]> responseEntity = restTemplate.getForEntity(
                getBaseUrl(ve) + URL_API_ATA + "/get-ata/"
                    + filtro.getDataInicialAsString() + "/"
                    + filtro.getDataFinalAsString() + "/"
                    + filtro.getCodigoUnidade(),
                AtaRegistroPrecoConsultaDTO[].class);
            list.addAll(Arrays.asList(responseEntity.getBody()));
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
        return list;
    }

    public List<ContratacaoConsultaDTO> buscarContratacao(ValidacaoException ve, FiltroConsultaDTO filtro) {
        List<ContratacaoConsultaDTO> list = Lists.newArrayList();
        try {
            ResponseEntity<ContratacaoConsultaDTO[]> responseEntity = restTemplate.getForEntity(
                getBaseUrl(ve) + URL_API_CONTRATACOES + "/get-contratacao/"
                    + filtro.getDataInicialAsString() + "/"
                    + filtro.getDataFinalAsString() + "/"
                    + filtro.getCodigoUnidade(),
                ContratacaoConsultaDTO[].class);
            list.addAll(Arrays.asList(responseEntity.getBody()));
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
        return list;
    }

    public void buscarItensContratacao(ValidacaoException ve, ContratacaoConsultaDTO dto) {
        try {
            ResponseEntity<ContratacaoConsultaDTO> responseEntity = restTemplate.getForEntity(
                getBaseUrl(ve) + URL_API_ITENS + "/get-itens-resultado/" + dto.getId(), ContratacaoConsultaDTO.class);
            ContratacaoConsultaDTO body = responseEntity.getBody();
            dto.setItensCompra(body.getItensCompra());
            dto.setResultados(body.getResultados());
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
    }

    public List<PlanoContratacaoAnualConsultaDTO> buscarPlanosContratacoesAnuais(ValidacaoException ve, FiltroConsultaDTO filtro) {
        List<PlanoContratacaoAnualConsultaDTO> list = Lists.newArrayList();
        try {
            String url = getBaseUrl(ve) + URL_API_PCA + "get-pca/"
                + filtro.getDataInicialAsString() + "/"
                + filtro.getDataFinalAsString() + "/"
                + filtro.getCodigoUnidade();

            ResponseEntity<PlanoContratacaoAnualConsultaDTO[]> responseEntity = restTemplate.getForEntity(url, PlanoContratacaoAnualConsultaDTO[].class);
            list.addAll(Arrays.asList(responseEntity.getBody()));
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
        return list;
    }

    public void buscarItensPlanoContratacaoAnual(ValidacaoException ve, PlanoContratacaoAnualConsultaDTO dto) {
        try {
            String url = getBaseUrl(ve) + URL_API_PCA + "get-itens-pca/" + dto.getId();
            ResponseEntity<PlanoContratacaoAnualItemConsultaDTO[]> itens = restTemplate.getForEntity(
                url, PlanoContratacaoAnualItemConsultaDTO[].class);
            dto.setItensPlano(Arrays.asList(itens.getBody()));
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
    }

    public List<OrgaoEntidadeConsultaDTO> buscarOrgaoEntidade(ValidacaoException ve) {
        List<OrgaoEntidadeConsultaDTO> list = Lists.newArrayList();
        try {
            ResponseEntity<OrgaoEntidadeConsultaDTO[]> forEntity = restTemplate.getForEntity(getBaseUrl(ve) + URL_API_ORGAO_ENTIDADE.concat("/get-orgaos-entidades"), OrgaoEntidadeConsultaDTO[].class);
            list.addAll(Arrays.asList(forEntity.getBody()));
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
        return list;
    }

    public void enviar(TipoEventoPncp tipoEventoPncp, OperacaoPncp operacaoPncp, Long idOrigem) {
        ValidacaoException ve = new ValidacaoException();
        try {
            EventoPncp evento = criarEvento(tipoEventoPncp, operacaoPncp, idOrigem);
            EventoPncpDTO dto = new EventoPncpDTO(evento);
            restTemplate.postForEntity(getBaseUrl(ve) + URL_API_EVENTO, dto, String.class);
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
    }

    public void enviar(EventoPncp evento, ValidacaoException ve) {
        try {
            EventoPncpDTO dto = new EventoPncpDTO(evento);
            restTemplate.postForEntity(getBaseUrl(ve) + URL_API_EVENTO, dto, String.class);
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EventoPncp criarEvento(TipoEventoPncp tipoEvento, OperacaoPncp operacao, Long idOrigem) {
        EventoPncp evento = new EventoPncp();
        evento.setTipoEvento(tipoEvento);
        evento.setData(new Date());
        evento.setOperacao(operacao);
        evento.setUsuario(sistemaFacade.getUsuarioCorrente().getNome());
        evento.setSituacao(SituacaoPncp.NAO_ENVIADO);
        evento.setIdOrigem(idOrigem);
        return em.merge(evento);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void criarEventoAtualizacaoIdSequencialPncp(Long idOrigem, String numeroAnoRegistro) {
        EventoPncp evento = new EventoPncp();
        evento.setTipoEvento(TipoEventoPncp.ATUALIZACAO_ID_SEQUENCIAO_PNCP);
        evento.setOperacao(OperacaoPncp.ALTERAR);
        evento.setSituacao(SituacaoPncp.ENVIADO_COM_SUCESSO);
        evento.setData(new Date());
        evento.setDataSincronizacao(new Date());
        evento.setUsuario(sistemaFacade.getUsuarioCorrente().getNome());
        evento.setIdOrigem(idOrigem);
        evento.setLogs(Lists.newArrayList());

        LogEventoPncp log = new LogEventoPncp();
        log.setEvento(evento);
        log.setData(new Date());
        log.setDescricao(numeroAnoRegistro);
        log.setLog("Atualização manual do id/sequencial PNCP.");
        evento.getLogs().add(log);
        em.merge(evento);
    }
}
