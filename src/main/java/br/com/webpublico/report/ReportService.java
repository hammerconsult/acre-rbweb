package br.com.webpublico.report;


import br.com.webpublico.controle.MonitoramentoNodeServerControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.DashboarWebReportDTO;
import br.com.webpublico.entidadesauxiliares.WebReportRelatorioDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.HtmlPdfDTO;
import br.com.webpublico.webreportdto.dto.comum.ParametroDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpush.push.Push;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    public static String URL_WEBPUBLICO = "URL_WEBPUBLICO";
    @Autowired
    private SistemaService sistemaService;
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private final Map<UsuarioSistema, Map<String, WebReportDTO>> relatorios = Maps.newHashMap();

    @PostConstruct
    public void init() {
        try {
            InitialContext initialContext = new InitialContext();
            configuracaoDeRelatorioFacade = (ConfiguracaoDeRelatorioFacade) initialContext.lookup("java:module/ConfiguracaoDeRelatorioFacade");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Não foi possivel fazer lookup do configuracaoDeRelatorioFacade");
        }
    }

    public Map<String, WebReportDTO> getRelatorios(UsuarioSistema usuario) {
        if (relatorios.containsKey(usuario)) {
            return sortByValue(relatorios.get(usuario));
        }
        return Maps.newHashMap();
    }

    public List<UsuarioSistema> getRelatoriosUsuarios() {
        List<UsuarioSistema> retorno = Lists.newArrayList();
        retorno.addAll(relatorios.keySet());
        return retorno;
    }

    private static Map<String, WebReportDTO> sortByValue(Map<String, WebReportDTO> unsortMap) {
        List<Map.Entry<String, WebReportDTO>> list = new LinkedList<Map.Entry<String, WebReportDTO>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, WebReportDTO>>() {
            public int compare(Map.Entry<String, WebReportDTO> o1,
                               Map.Entry<String, WebReportDTO> o2) {
                return (o2.getValue()).getSolicitadoEm().compareTo((o1.getValue()).getSolicitadoEm());
            }
        });
        Map<String, WebReportDTO> sortedMap = new LinkedHashMap<String, WebReportDTO>();
        for (Map.Entry<String, WebReportDTO> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public byte[] converterHtmlParaPdf(HtmlPdfDTO htmlPdfDTO) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Lists.newArrayList(new MediaType[]{MediaType.APPLICATION_OCTET_STREAM}));
        HttpEntity<HtmlPdfDTO> request = new HttpEntity<>(htmlPdfDTO, headers);

        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        try {
            if (configuracao == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma configuração foi encontrada para a chave " + configuracaoDeRelatorioFacade.getUsuarioBanco());
            }
            ve.lancarException();
            ResponseEntity<byte[]> responseEntity = new RestTemplate().exchange(configuracao.getUrl() + "converter-html-pdf/converter", HttpMethod.POST, request, byte[].class);
            if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                return responseEntity.getBody();
            }
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de relatório. Url: " + configuracao.getUrl();
            new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida(mensagem).lancarException();
        } catch (ValidacaoException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida(e.getResponseBodyAsString()).lancarException();
        }
        return null;
    }

    public void gerarRelatorio(UsuarioSistema usuario, RelatorioDTO dto) {
        log.debug("vai gerar ...");
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        try {
            instanciarRelatorio(usuario);
            validarGeracaoRelatorio(usuario, configuracao);
            verificarHashRelatorio(configuracao, usuario, dto);
            solicitarGeracaoRelatorio(usuario, dto, configuracao);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de relatório. Url: " + configuracao.getUrl();
            new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida(mensagem).lancarException();
        } catch (ValidacaoException | WebReportRelatorioExistenteException e) {
            throw e;
        } catch (Exception e) {
            new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida(e.getMessage()).lancarException();
        }
    }

    private void verificarHashRelatorio(ConfiguracaoDeRelatorio configuracao, UsuarioSistema usuarioLogado, RelatorioDTO dto) {
        if (configuracao.getVerificarCache() && dto.getVerificarCache()) {
            for (UsuarioSistema usuarioSistema : relatorios.keySet()) {
                for (Map.Entry<String, WebReportDTO> mapa : relatorios.get(usuarioSistema).entrySet()) {
                    WebReportDTO webReportDTO = mapa.getValue();
                    String hash = webReportDTO.getHash();
                    String hashDTO = calcularHash(dto);
                    if (webReportDTO.getNome().equals(dto.getNomeRelatorio())
                        && hash.equals(hashDTO)) {
                        if (webReportDTO.isConcluido()) {
                            UUID uuid = UUID.randomUUID();
                            WebReportDTO novo = new WebReportDTO(dto.getNomeRelatorio(), usuarioLogado, uuid.toString(), webReportDTO.getHash(), webReportDTO.getDto());
                            novo.setConteudo(webReportDTO.getConteudo());
                            novo.setPorcentagem(webReportDTO.getPorcentagem());
                            novo.setConteudo(webReportDTO.getConteudo());
                            novo.setFim(webReportDTO.getFim());
                            novo.setVisualizado(Boolean.TRUE);
                            novo.setDto(dto);
                            this.relatorios.get(usuarioLogado).put(novo.getUuid(), novo);
                            String message = "Este Relatório já foi gerado por <b>" + webReportDTO.getUsuarioSistema().getNome()
                                + " às " + DataUtil.getDataFormatadaDiaHora(webReportDTO.getSolicitadoEm()) + "</b>" +
                                " e levou <b>" + webReportDTO.getTempoAsString() + ".</b> ";
                            throw new WebReportRelatorioExistenteException(message, novo);
                        }
                    }
                }
            }
        }
    }

    private void solicitarGeracaoRelatorio(UsuarioSistema usuario, RelatorioDTO dto, ConfiguracaoDeRelatorio configuracao) {
        dto.setLoginUsuario(sistemaService.getUsuarioCorrente().getLogin());

        String urlWebpublico = System.getenv(URL_WEBPUBLICO);
        dto.setUrlWebpublico(urlWebpublico != null ? urlWebpublico : configuracao.getUrlWebpublico());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<UUID> responseEntity = new RestTemplate().exchange(configuracao.getUrlCompleta(dto.getApi() + "gerar"), HttpMethod.POST, request, UUID.class);
        if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            UUID uuid = responseEntity.getBody();
            relatorios.get(usuario).put(uuid.toString(), new WebReportDTO(dto.getNomeRelatorio(), usuario, uuid.toString(), calcularHash(dto), dto));
            log.debug("ta gerando ..." + uuid);

            Push.sendTo(usuario, "imprimirRelatorio");
        }
    }

    public byte[] gerarRelatorioSincrono(RelatorioDTO dto) {
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        dto.setLoginUsuario(SistemaFacade.obtemLogin());

        String urlWebpublico = System.getenv(URL_WEBPUBLICO);
        dto.setUrlWebpublico(urlWebpublico != null ? urlWebpublico : configuracao.getUrlWebpublico());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<byte[]> responseEntity = new RestTemplate()
            .exchange(configuracao.getUrlCompleta(dto.getApi() + "sincrono/gerar"),
                HttpMethod.POST, request, byte[].class);
        return responseEntity.getBody();
    }

    private String calcularHash(RelatorioDTO dto) {
        Integer hash = 0;
        for (ParametroDTO parametroDTO : dto.getParametroDTO()) {
            if (parametroDTO.getConsiderarNoHash()) {
                hash = hash + parametroDTO.hashCode();
            }
        }
        return hash.toString();
    }

    private void instanciarRelatorio(UsuarioSistema usuario) {
        if (!relatorios.containsKey(usuario)) {
            relatorios.put(usuario, new HashMap<String, WebReportDTO>());
        }
    }

    private void validarGeracaoRelatorio(UsuarioSistema usuario, ConfiguracaoDeRelatorio configuracao) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracao == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma configuração foi encontrada para a chave " + configuracaoDeRelatorioFacade.getUsuarioBanco());
        } else {
            if (relatorios.get(usuario).size() >= configuracao.getQuantidadeRelatorio()) {
                removerRelatorioMaisAntigoGerado(usuario);
            }
        }
        ve.lancarException();
    }

    private void removerRelatorioMaisAntigoGerado(UsuarioSistema usuario) {
        Date menorData = null;
        WebReportDTO webReportDTO = null;
        for (Map.Entry<String, WebReportDTO> mapa : relatorios.get(usuario).entrySet()) {
            if (menorData == null || menorData.after(mapa.getValue().getSolicitadoEm())) {
                menorData = mapa.getValue().getSolicitadoEm();
                webReportDTO = mapa.getValue();
            }
        }
        removerRelatorio(usuario, webReportDTO.getUuid());
    }

    public String getUrl() {
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        if (configuracao == null) {
            throw new ValidacaoException("Nenhuma configuração foi encontrada para a chave " + configuracaoDeRelatorioFacade.getUsuarioBanco());
        }
        return configuracao.getUrl();
    }


    public void publicarRelatorio(String uuid) {
        log.debug("Ta chengando ..." + uuid);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();

        String urlConsulta = UriComponentsBuilder
            .fromUriString(getUrl() + "relatorio/imprimir" + configuracao.getParametroBancoUrl())
            .queryParam("uuid", uuid)
            .build()
            .toUriString();

        ResponseEntity<byte[]> response = new RestTemplate()
            .getForEntity(urlConsulta, byte[].class);

        if (!response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            for (UsuarioSistema usuarioSistema : relatorios.keySet()) {
                if (relatorios.get(usuarioSistema).containsKey(uuid)) {
                    relatorios.get(usuarioSistema).get(uuid).setConteudo(response.getBody());
                    relatorios.get(usuarioSistema).get(uuid).setFim(System.currentTimeMillis());
                    log.debug("Chegou ..." + uuid);

                    Push.sendTo(usuarioSistema, "imprimirRelatorio");
                    break;
                }
            }
        } else {
            for (UsuarioSistema usuarioSistema : relatorios.keySet()) {
                if (relatorios.get(usuarioSistema).containsKey(uuid)) {
                    List<String> erros = response.getHeaders().get(WebReportDTO.HEADER_MESSAGE_ERROR);
                    if (erros != null && !erros.isEmpty()) {
                        relatorios.get(usuarioSistema).get(uuid).setErro(erros.get(0));
                    }
                    log.debug("Chegou com erro ..." + uuid);
                    Push.sendTo(usuarioSistema, "imprimirRelatorio");
                    break;
                }
            }
        }

    }

    public void porcentagemRelatorio(String uuid, BigDecimal porcentagem) {
        log.debug("porcentagem do relatorio ..." + porcentagem + " : " + uuid);

        for (UsuarioSistema usuarioSistema : relatorios.keySet()) {
            if (relatorios.get(usuarioSistema).containsKey(uuid)) {
                relatorios.get(usuarioSistema).get(uuid).setPorcentagem(porcentagem);
                log.debug("alterou porcentagem ..." + uuid);
                Push.sendTo(usuarioSistema, "imprimirRelatorio");
                break;
            }
        }
    }

    public void alterarErro(String uuid, String error) {
        log.debug("deu erro no relatorio ..." + error + " : " + uuid);

        for (UsuarioSistema usuarioSistema : relatorios.keySet()) {
            if (relatorios.get(usuarioSistema).containsKey(uuid)) {
                relatorios.get(usuarioSistema).get(uuid).setErro(error);
                Push.sendTo(usuarioSistema, "imprimirRelatorio");
                break;
            }
        }
    }

    public void imprimirRelatorio(UsuarioSistema usuarioSistema, String uuid) throws IOException {
        Map<String, WebReportDTO> relatorios = this.relatorios.get(usuarioSistema);
        if (relatorios != null) {
            WebReportDTO webReportDTO = relatorios.get(uuid);
            if (webReportDTO != null) {
                if (TipoRelatorioDTO.PDF.equals(webReportDTO.getDto().getTipoRelatorio())) {
                    AbstractReport.poeRelatorioNaSessao(webReportDTO.getConteudo(), webReportDTO.getNome());
                } else {
                    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                    response.setContentType("application/x-download");
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader("Content-disposition", "attachment;filename=" + webReportDTO.getDto().getNomeRelatorio() + webReportDTO.getDto().getTipoRelatorio().getExtension());
                    int tamanho = webReportDTO.getConteudo() != null ? webReportDTO.getConteudo().length : 0;
                    response.setContentLength(tamanho);
                    ServletOutputStream outputStream = response.getOutputStream();
                    outputStream.write(webReportDTO.getConteudo(), 0, tamanho);
                    outputStream.flush();
                    outputStream.close();
                    FacesContext.getCurrentInstance().responseComplete();
                }
            }
        }
    }

    public void removerRelatorio(UsuarioSistema usuarioSistema, String uuid) {
        if (relatorios.get(usuarioSistema) != null) {
            relatorios.get(usuarioSistema).remove(uuid);
            try {
                MonitoramentoNodeServerControlador.cancelarGeracaoRelatorio(buscarRelatorio(uuid), false);
            } catch (Exception e) {
                log.error("Erro ao encerrar a thread do relatório com o uuid: " + uuid, e);
            }
        }
    }

    private WebReportRelatorioDTO buscarRelatorio(String uuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DashboarWebReportDTO> request = new HttpEntity<>(headers);
        String url = configuracaoDeRelatorioFacade.getConfiguracaoPorChave().getUrlCompleta("relatorios/por-uuid");
        url += "&uuid=" + uuid;
        ResponseEntity<WebReportRelatorioDTO> exchange = new RestTemplate()
            .exchange(url, HttpMethod.GET, request,
                new ParameterizedTypeReference<WebReportRelatorioDTO>() {
                });
        return exchange.getBody();
    }

    public static ReportService getInstance() {
        return (ReportService) Util.getSpringBeanPeloNome("reportService");
    }

    public void abrirDialogConfirmar(WebReportRelatorioExistenteException e) {
        e.getWebReportDTO().setErro(e.getMessage());
        Util.getSistemaControlador().setWebReportDTO(e.getWebReportDTO());
        FacesUtil.executaJavaScript("visualizarRelatorioOutroUsuario.show()");
        FacesUtil.atualizarComponente("formvisualizarRelatorioOutroUsuario");
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void rotinaApagarRelatorio() {
        log.debug("iniciando sincronização do webreport as " + DataUtil.getDataFormatadaDiaHora(new Date()));

        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        String urlConsulta = UriComponentsBuilder
            .fromUriString(getUrl() + "relatorio/sincronizacao" + configuracao.getParametroBancoUrl())
            .build()
            .toUriString();

        ResponseEntity<Void> response = new RestTemplate().getForEntity(urlConsulta, Void.class);
        if (!response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            log.debug("finalizado a sincronização do webreport as " + DataUtil.getDataFormatadaDiaHora(new Date()));
        }
    }
}

