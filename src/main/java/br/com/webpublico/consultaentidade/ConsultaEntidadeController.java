/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.consultaentidade;


import br.com.webpublico.controle.Web;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.Operador;
import br.com.webpublico.enums.OperadorLogico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.url.URL;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Usuario
 */
@ManagedBean(name = "consultaEntidadeController")
@ViewScoped
@URLMappings(mappings = {

    @URLMapping(id = "consulta-entidade-generica-listar",
        pattern = "/consulta-entidade/listar/#{consultaEntidadeController.chave}/",
        viewId = "/faces/consulta-entidade/lista.xhtml"),

    @URLMapping(id = "consulta-entidade-generica-editar",
        pattern = "/consulta-entidade/editar/#{consultaEntidadeController.chave}/",
        viewId = "/faces/consulta-entidade/editar.xhtml"),

    @URLMapping(id = "consulta-entidade-generica-novo",
        pattern = "/consulta-entidade/novo/",
        viewId = "/faces/consulta-entidade/editar.xhtml")
})

public class ConsultaEntidadeController implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(ConsultaEntidadeController.class);

    private static final String TAG_EXERCICIO_ANO = "$" + TAG.EXERCICIO_ANO.name();
    private static final String JSON_RESOURCE_PATH = "/br/com/webpublico/consultaentidade/";
    private static final String JSON_EXTENSION = ".json";
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String JSON_SUFFIX = "json";
    private static final int JSON_INDENTATION = 4;

    private static final String MENSAGEM_ERRO_AO_RECUPERAR_JSON_DA_CONSULTA_POR_CHAVE =
        "Erro ao recuperar JSON de consulta da chave";


    @EJB
    private ConsultaEntidadeFacade facade;
    private ConverterFieldConsultaEntidade converterFieldConsulta;
    private String chave;
    private FieldConsultaEntidade fieldConsulta = new FieldConsultaEntidade();
    private ConsultaEntidade consulta;
    private ExcelUtil excelUtil = new ExcelUtil();
    private Map<String, String> hrefs = Maps.newHashMap();
    private int statusExcel = 0;
    private StreamedContent streamedContentExcel;
    private Future<StreamedContent> futureExcel;

    public ConsultaEntidadeController() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public void setFieldConsulta(FieldConsultaEntidade fieldConsulta) {
        this.fieldConsulta = fieldConsulta;
    }

    public FieldConsultaEntidade getFieldConsulta() {
        return fieldConsulta;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public void adiconarAction(List<ActionConsultaEntidade> lista) {
        lista.add(new ActionConsultaEntidade());
    }

    public void adiconarPesquisavel() {
        consulta.getPesquisaveis().add(new FieldConsultaEntidade(consulta.getPesquisaveis().size()));
    }

    public void removerPesquisavel(FieldConsultaEntidade fieldConsulta) {
        consulta.getPesquisaveis().remove(fieldConsulta);
    }

    public void removerAction(ActionConsultaEntidade action, List<ActionConsultaEntidade> lista) {
        lista.remove(action);
    }

    public void copiarPesquisavelTabelavel(FieldConsultaEntidade fieldConsulta, List<FieldConsultaEntidade> lista) {
        lista.add(fieldConsulta.clonar());
    }

    public void moverItem(FieldConsultaEntidade field, List<FieldConsultaEntidade> lista, Long proximoIndex) {
        try {
            int index = lista.indexOf(field);
            FieldConsultaEntidade temp = lista.get(index + proximoIndex.intValue());
            lista.set(index + proximoIndex.intValue(), field);
            lista.set(index, temp);
        } catch (Exception e) {
            FacesUtil.addError("Operação não realizada", "Não foi possível mover o item");
        }
    }

    public void adiconarTabelavel() {
        consulta.getTabelaveis().add(new FieldConsultaEntidade(consulta.getTabelaveis().size()));
    }

    public void removerTabelavel(FieldConsultaEntidade fieldConsulta) {
        consulta.getTabelaveis().remove(fieldConsulta);
    }

    public ConverterFieldConsultaEntidade getConverterFieldConsulta() {
        return converterFieldConsulta;
    }

    public void definirOperacaoPadrao(FiltroConsultaEntidade filtro) {
        if (filtro.getField() != null && filtro.getField().getOperacaoPadrao() != null) {
            filtro.setOperacao(filtro.getField().getOperacaoPadrao());
        }
        filtro.setValor(null);
    }


    @URLAction(mappingId = "consulta-entidade-generica-listar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        montarPesquisa(chave);
    }

    @URLAction(mappingId = "consulta-entidade-generica-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        criarNovaConsultaEntidade();
    }

    @URLAction(mappingId = "consulta-entidade-generica-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        recupararConsultaDoBanco(chave);
        criarNovaConsultaEntidade();
    }

    private void atribuirContexto() {
        if (consulta != null) {
            consulta.setUsuarioCorrente(facade.getSistemaFacade().getUsuarioCorrente());
            consulta.setExercicioCorrente(facade.getSistemaFacade().getExercicioCorrente());
            consulta.setUnidadeOrganizacionalAdministrativaCorrente(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
            consulta.setUnidadeOrganizacionalOrcamentariaCorrente(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
            consulta.setDataOperacao(facade.getSistemaFacade().getDataOperacao());
        }
    }

    private void criarNovaConsultaEntidade() {
        if (consulta == null) {
            consulta = new ConsultaEntidade();
            consulta.setChave(chave);
            consulta.identificador = new FieldConsultaEntidade();
            consulta.identificador.setValor("id");
            consulta.estiloLinha = new FieldConsultaEntidade();

            addAdctionVer();
            addAdctionEditar();
            addAdctionHistorico();
            addAdctionNovo();
        } else {
            if (consulta.estiloLinha == null) {
                consulta.estiloLinha = new FieldConsultaEntidade();
            }
        }
    }

    private void recupararConsultaDoBanco(String chave) {
        try {
            if (Strings.isNullOrEmpty(chave)) return;
            consulta = facade.recuperarConsultaEntidade(chave);
            if (consulta != null && consulta.resultados != null) {
                consulta.resultados.clear();
            } else {
                logger.info("Não foi encontrada consulta no banco com a chave " + chave);
            }
        } catch (Exception e) {
            logger.error("Não foi possível recuperar a consulta para a chave", e);
        }
    }

    public String getIdentificadoSessao(String chave) {
        return "consulta-entidade-" + chave;
    }

    private Map<String, Object> getSessionMap() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }

    public void montarPesquisa(String chave) {
        getSessionMap().remove(TipoURLConsultaEntidade.URL_RETORNO.getKey());
        consulta = (ConsultaEntidade) Web.pegaDaSessao(getIdentificadoSessao(chave));
        if (consulta == null) {
            recupararConsultaDoBanco(chave);
            if (consulta == null) {
                recuperarConsultaDosResources(chave);
            }
            if (consulta != null) {
                adicionarFiltrosPadroes();
                //consultarEntidade();
            }
        }
        if (consulta != null) {
            converterFieldConsulta = new ConverterFieldConsultaEntidade(consulta.getPesquisaveis());
            FacesUtil.executaJavaScript("limparFiltros()");
        } else {
            consulta = new ConsultaEntidade();
            consulta.setChave(chave);
        }
    }

    private void recuperarConsultaDosResources(String chave) {
        try {
            InputStream resourceAsStream = getJsonResourceAsStreamPorChave(chave);
            if (resourceAsStream != null) {
                consulta = new ObjectMapper().readValue(IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8.name()), ConsultaEntidade.class);
                if (consulta != null) {
                    facade.salvarConsultaEntidade(consulta);
                }
            }
        } catch (Exception e) {
            String mensagemErro = MENSAGEM_ERRO_AO_RECUPERAR_JSON_DA_CONSULTA_POR_CHAVE + " " + chave;
            LogUtil.registrarExcecao(logger, mensagemErro, e);
        }
    }

    private InputStream getJsonResourceAsStreamPorChave(String chave) {
        String caminho = JSON_RESOURCE_PATH + chave + JSON_EXTENSION;
        return getClass().getResourceAsStream(caminho);
    }

    private void adicionarFiltrosPadroes() {
        if (consulta.getFiltros().isEmpty()) {
            Set<FieldConsultaEntidade> filtrosObrigatorioEPadrao = obterFiltrosObrigatorioEPadrao();
            if (filtrosObrigatorioEPadrao.isEmpty()) {
                addFiltro();
            } else {
                filtrosObrigatorioEPadrao.forEach(this::adicionarFiltroNaConsulta);
            }
        }
    }

    private Set<FieldConsultaEntidade> obterFiltrosObrigatorioEPadrao() {
        Set<FieldConsultaEntidade> filtrosPadraoAndObrigatorio = new HashSet<>();
        filtrosPadraoAndObrigatorio.addAll(obterFiltrosObrigatorios());
        filtrosPadraoAndObrigatorio.addAll(obterFiltrosPadroes());
        return filtrosPadraoAndObrigatorio;
    }

    private List<FieldConsultaEntidade> obterFiltrosObrigatorios() {
        return consulta.getPesquisaveis().stream()
            .filter(FieldConsultaEntidade::getObrigatorio)
            .collect(Collectors.toList());
    }

    private List<FieldConsultaEntidade> obterFiltrosPadroes() {
        return consulta.getPesquisaveis().stream()
            .filter(pesquisavel ->
                pesquisavel.getValorPadrao() != null &&
                    !pesquisavel.getValorPadrao().toString().isEmpty() &&
                    pesquisavel.getOperacaoPadrao() != null)
            .collect(Collectors.toList());
    }

    private void adicionarFiltroNaConsulta(FieldConsultaEntidade field) {
        FiltroConsultaEntidade filtro = new FiltroConsultaEntidade();
        filtro.setField(field);
        filtro.setOperacao(field.getOperacaoPadrao());
        filtro.setValor(obterValorPadraoComInformacaoDoSistema(field.getValorPadrao()));
        consulta.getFiltros().add(filtro);
    }

    private Object obterValorPadraoComInformacaoDoSistema(Object valorPadrao) {
        if (isValorPadraoExercicioAno(valorPadrao)) {
            return obterAnoExercicioCorrente();
        }
        return valorPadrao;
    }

    private boolean isValorPadraoExercicioAno(Object valorPadrao) {
        return TAG_EXERCICIO_ANO.equals(valorPadrao.toString().trim());
    }

    private Object obterAnoExercicioCorrente() {
        return facade.getSistemaFacade().getExercicioCorrente().getAno();
    }

    public void addAdctionVer() {
        ActionConsultaEntidade action = new ActionConsultaEntidade();
        action.setAlinhamento(TipoAlinhamento.ESQUERDA);
        action.href = "../ver/$identificador/";
        action.icone = "ui-icon-search";
        action.title = "Clique para visualizar este registro.";
        consulta.actionsTabela.add(action);
    }

    public void addAdctionEditar() {
        ActionConsultaEntidade action = new ActionConsultaEntidade();
        action.setAlinhamento(TipoAlinhamento.ESQUERDA);
        action.href = "../editar/$identificador/";
        action.icone = "ui-icon-pencil";
        action.title = "Clique para editar este registro.";
        consulta.actionsTabela.add(action);
    }

    public void addAdctionHistorico() {
        ActionConsultaEntidade action = new ActionConsultaEntidade();
        action.setAlinhamento(TipoAlinhamento.DIREITA);
        action.href = "/auditoria/listar-por-entidade/$identificador/" + chave + "/";
        action.icone = "ui-icon-clock";
        action.title = "Clique para ver o histórico deste registro.";
        consulta.actionsTabela.add(action);
    }

    public void addAdctionNovo() {
        ActionConsultaEntidade action = new ActionConsultaEntidade();
        action.href = "../novo/";
        action.icone = "ui-icon-plus";
        action.descricao = "Novo";
        action.title = "Clique para criar um novo registro.";
        consulta.actionsHeader.add(action);
    }

    public List<TAG> getTags() {
        return Lists.newArrayList(TAG.values());
    }

    public void limparFiltros() {
        consulta.filtros.clear();
        adicionarFiltrosPadroes();
    }

    public void voltarConsultaEntidade() {
        URL requestURL = (URL) Web.pegaDaSessao(TipoURLConsultaEntidade.URL_RETORNO.getKey());
        if (requestURL != null) {
            FacesUtil.redirecionamentoInterno(requestURL.toURL());
        } else {
            FacesUtil.redirecionamentoInterno("/consulta-entidade/listar/" + consulta.getChave());
        }
    }

    public void testarConsultaEntidade() {
        try {
            limparFiltros();
            consultarEntidade();
            FacesUtil.addOperacaoRealizada("Consulta funcionando corretamente!");
        } catch (Exception e) {
            logger.error("Erro ao executar a consulta {} ", consulta.chave);
            FacesUtil.addError("Erro ao executar", e.getMessage());
        }
    }

    public void salvarConsultaEntidade() {
        try {
            facade.salvarConsultaEntidade(consulta);
            voltarConsultaEntidade();
        } catch (Exception e) {
            logger.error("Erro ao executar a consulta {} ", consulta.chave);
            FacesUtil.addError("Erro ao executar", e.getMessage());
        }
    }

    public void redirecionaParaEditar() {
        URL requestURL = PrettyContext.getCurrentInstance().getRequestURL();
        Web.poeNaSessao(TipoURLConsultaEntidade.URL_RETORNO.getKey(), requestURL);
        FacesUtil.redirecionamentoInterno("/consulta-entidade/editar/" + consulta.getChave());
    }

    public void consultarEntidade() {
        if (!Strings.isNullOrEmpty(consulta.from)) {
            try {
                atribuirContexto();
                validarFiltrosObrigatorios();
                atribuirContexto();
                primeiraPagina();
                Web.poeNaSessao(getIdentificadoSessao(consulta.getChave()), consulta);
            } catch (ValidacaoException e) {
                FacesUtil.printAllFacesMessages(e);
            } catch (Exception e) {
                logger.error("Erro ao consultar", e);
                FacesUtil.addErrorGenerico(e);
            }
        }
    }

    private void validarFiltrosObrigatorios() {
        ValidacaoException ce = new ValidacaoException();
        List<FieldConsultaEntidade> obrigatorios = obterFiltrosObrigatorios();
        List<FieldConsultaEntidade> filtrosUtilizados = getFiltrosUtilizados();

        for (FieldConsultaEntidade obrigatorio : obrigatorios) {
            if (!filtrosUtilizados.contains(obrigatorio)) {
                ce.adicionarMensagemDeCampoObrigatorio("Informe o campo " + obrigatorio.getDescricao() + " para pesquisar");
            } else {
                for (FiltroConsultaEntidade filtro : consulta.getFiltros()) {
                    if (obrigatorio.equals(filtro.getField())) {
                        if ((filtro.getValor() != null && !filtro.getValor().toString().isEmpty()) ||
                            Operador.IS_NULL.equals(filtro.getOperacao()) ||
                            Operador.IS_NOT_NULL.equals(filtro.getOperacao())) {
                            continue;
                        }
                        ce.adicionarMensagemDeCampoObrigatorio("Informe o campo " + obrigatorio.getDescricao() + " para pesquisar");
                    }
                }
            }
        }
        ce.lancarException();
    }

    private List<FieldConsultaEntidade> getFiltrosUtilizados() {
        List<FieldConsultaEntidade> retorno = Lists.newArrayList();
        for (FiltroConsultaEntidade filtro : consulta.getFiltros()) {
            if (filtro.getField() != null) {
                retorno.add(filtro.getField());
            }
        }
        return retorno;
    }

    public void proximaPagina() {
        consulta.setPaginaAtual(consulta.getPaginaAtual() + 1);
        facade.consultarEntidades(consulta);
    }

    public void ultimaPagina() {
        consulta.setPaginaAtual(consulta.totalRegistros / consulta.registroPorPagina);
        facade.consultarEntidades(consulta);
    }

    public void primeiraPagina() {
        consulta.setPaginaAtual(0);
        facade.consultarEntidades(consulta);
    }

    public void definirTotalRegistros(Long i) {
        atribuirContexto();
        consulta.setRegistroPorPagina(i.intValue());
        facade.consultarEntidades(consulta);
    }

    public void paginaAnterior() {
        consulta.setPaginaAtual(consulta.getPaginaAtual() - 1);
        facade.consultarEntidades(consulta);
    }

    public ConsultaEntidade getConsulta() {
        return consulta;
    }

    public void adicionarOrdenacao(FieldConsultaEntidade campo) {
        campo.modarOrdem();
        facade.consultarEntidades(consulta);
    }

    public List<SelectItem> getPesquisaveis() {
        return Util.getListSelectItem(consulta.getPesquisaveis(), false);
    }

    public List<SelectItem> getTabelaveis() {
        return Util.getListSelectItem(consulta.getTabelaveis());
    }

    public List<SelectItem> getTiposCampo() {
        return Util.getListSelectItem(TipoCampo.values());
    }

    public List<SelectItem> getTiposAlinhamento() {
        return Util.getListSelectItem(TipoAlinhamento.values(), false);
    }

    public List<SelectItem> getTiposAlinhamentoActions() {
        List<SelectItem> itens = Lists.newArrayList();
        for (TipoAlinhamento valor : TipoAlinhamento.values()) {
            if (TipoAlinhamento.ESQUERDA.equals(valor) || TipoAlinhamento.DIREITA.equals(valor)) {
                itens.add(new SelectItem(valor, valor.getDescricao()));
            }
        }
        return itens;
    }

    public List<SelectItem> tiposEnum(String nomeEnum) {
        try {
            Class enumClass = Class.forName(nomeEnum);
            return Util.getListSelectItem(enumClass.getEnumConstants(), false);
        } catch (Exception e) {
            logger.error("Erro ao tentar carregar o enum [{}]", nomeEnum);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> tiposPorSelect(String select) {
        try {
            if (!select.isEmpty()) {
                //O primeiro campo do select deve ser o ID e o segundo será o campo que irá aparecer em tela para o usuário
                List<Object[]> resultado = facade.executarConsulta(select);
                List<SelectItem> retorno = Lists.newArrayList();
                retorno.add(new SelectItem(null, ""));
                for (Object[] obj : resultado) {
                    retorno.add(new SelectItem(obj[0], obj[1].toString()));
                }
                return retorno;
            }
        } catch (Exception e) {
            logger.error("Erro ao tentar carregar o select [{}]", select);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> montarOperadoresLogicos() {
        return Util.getListSelectItemSemCampoVazio(OperadorLogico.values());
    }

    public void addFiltro() {
        consulta.getFiltros().add(new FiltroConsultaEntidade());
    }

    public void removeFiltro(int index) {
        consulta.getFiltros().remove(index);
        if (consulta.getFiltros().isEmpty()) {
            addFiltro();
        }
    }

    public StreamedContent toJson() throws IOException {
        consulta.getResultados().clear();
        consulta.getFiltros().clear();

        File txt = File.createTempFile(consulta.chave + JSON_EXTENSION, JSON_SUFFIX);
        FileOutputStream fos = new FileOutputStream(txt);

        String json = new ObjectMapper().writeValueAsString(consulta);
        JSONObject jsonObject = new JSONObject(json);

        fos.write(jsonObject.toString(JSON_INDENTATION).getBytes(StandardCharsets.UTF_8));
        fos.close();

        InputStream stream = Files.newInputStream(txt.toPath());
        return new DefaultStreamedContent(stream, JSON_CONTENT_TYPE, consulta.chave + JSON_EXTENSION);
    }

    public int getStatusExcel() {
        return statusExcel;
    }

    public StreamedContent getStreamedContentExcel() {
        return streamedContentExcel;
    }

    public void iniciarGeracaoExcel() {
        statusExcel = 0;
        streamedContentExcel = null;
        futureExcel = null;
    }

    public void gerarExcel(Boolean todosRegistros) {
        statusExcel = 1;
        futureExcel = facade.gerarExcel(consulta, todosRegistros);
        FacesUtil.executaJavaScript("iniciarGeracaoExcel()");
    }

    public void acompanharGeracaoExcel() {
        if (futureExcel.isDone() || futureExcel.isCancelled()) {
            FacesUtil.executaJavaScript("pararInterval()");
            try {
                statusExcel = 2;
                streamedContentExcel = futureExcel.get();
                if (streamedContentExcel == null) {
                    statusExcel = 3;
                }
            } catch (Exception e) {
                statusExcel = 3;
                logger.error("Erro ao pegar streamContent da geracao de excel. Detalhe {}", e);
            }
            FacesUtil.executaJavaScript("atualizarFormularioExcel()");
        }
    }

    public StreamedContent gerarCSV() {
        try {
            List<String> colunas = Lists.newArrayList();
            List<Object[]> valores = Lists.newArrayList();
            for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
                colunas.add(StringUtil.removeCaracteresEspeciais(StringUtil.removeAcentuacao(tabelavel.getDescricao())));
            }
            for (Map<String, Object> resultado : consulta.getResultados()) {
                Object[] obj = new Object[resultado.size()];
                int i = 0;
                for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
                    Object o = resultado.get(tabelavel.getValor());
                    if (o != null) {
                        if (TipoCampo.STRING.equals(tabelavel.getTipo()) || TipoCampo.ENUM.equals(tabelavel.getTipo())) {
                            obj[i] = StringUtil.removeCaracteresEspeciais(StringUtil.removeAcentuacao(o.toString()));
                        } else {
                            obj[i] = o;
                        }
                    } else {
                        obj[i] = "";
                    }
                    i++;
                }
                valores.add(obj);
            }

            excelUtil
                .gerarCSV("Relatório de " + consulta.getNomeTela(),
                    "relatorio_" + consulta.getChave(),
                    colunas, valores, false);

            return excelUtil.fileDownload();
        } catch (Exception e) {
            return null;
        }
    }

    public void gerarPDF() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoHtml(), baos);
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            logger.error("Erro ao gerar o relatório de impressão da consulta de entidade");
        }
    }

    public StreamedContent gerarHtml() {
        try {
            File arquivo = new File("relatorio_" + consulta.getChave());
            FileOutputStream fos = new FileOutputStream(arquivo);
            fos.write(getConteudoHtml().getBytes(Charset.forName("UTF-8")));
            fos.close();
            InputStream stream = new FileInputStream(arquivo);
            return new DefaultStreamedContent(stream, "text/html; charset=UTF-8", "relatorio_" + consulta.getChave().toLowerCase() + ".html");
        } catch (Exception e) {
            return null;
        }
    }

    private String getConteudoHtml() {
        Date hoje = new Date();
        StringBuilder listItens = new StringBuilder("");

        for (Map<String, Object> resultado : consulta.getResultados()) {
            listItens.append("<tr>");
            for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
                listItens.append("<td style=\"text-align:")
                    .append(tabelavel.getTipoAlinhamento().getStyle())
                    .append("\">");
                listItens.append(resultado.get(tabelavel.getValor()) != null ? resultado.get(tabelavel.getValor()) : " ");
                listItens.append("</td>");
            }
            listItens.append("</tr>");
        }
        if (!consulta.getTotalizadores().isEmpty()) {
            listItens.append("<tr style='border-bottom: 1px solid #0000; background: #ebebeb'>");
            listItens.append("<td style='font-weight:bold; text-align: left' colspan='").append(consulta.getTabelaveis().size()).append("'>");
            listItens.append("Totalizadores");
            listItens.append("</td>");
            listItens.append("</tr>");
        }
        listItens.append("<tr style='border-bottom: 1px solid #0000; background: #ebebeb'>");
        for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
            listItens.append("<td style=\"text-align:")
                .append(tabelavel.getTipoAlinhamento().getStyle())
                .append("\">");
            listItens.append(consulta.getTotalizadores().containsKey(tabelavel.getValor())
                ? Util.formataNumero(consulta.getTotalizadores().get(tabelavel.getValor())) : " ");
            listItens.append("</td>");
        }
        listItens.append("</tr>");
        StringBuilder conteudo = new StringBuilder("");
        conteudo.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
        conteudo.append(" <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        conteudo.append(" <html>");
        conteudo.append(" <head>");
        conteudo.append(" <style type=\"text/css\"  media=\"all\">");
        conteudo.append(" @page{");
        conteudo.append(" size: A4 landscape; ");
        conteudo.append(" margin-top: 1.5in;");
        conteudo.append(" margin-bottom: 1.0in;");
        conteudo.append(" @bottom-center {");
        conteudo.append(" content: element(footer);");
        conteudo.append(" }");
        conteudo.append(" @top-center {");
        conteudo.append(" content: element(header);");
        conteudo.append(" }");
        conteudo.append("}");
        conteudo.append("#page-header {");
        conteudo.append(" display: block;");
        conteudo.append(" position: running(header);");
        conteudo.append(" }");
        conteudo.append(" #page-footer {");
        conteudo.append(" display: block;");
        conteudo.append(" position: running(footer);");
        conteudo.append(" }");
        conteudo.append(" .page-number:before {");
        conteudo.append("  content: counter(page) ");
        conteudo.append(" }");
        conteudo.append(" .page-count:before {");
        conteudo.append(" content: counter(pages);  ");
        conteudo.append("}");
        conteudo.append("</style>");
        conteudo.append(" <style type=\"text/css\">");
        conteudo.append(".igualDataTable{");
        conteudo.append("    border-collapse: collapse; /* CSS2 */");
        conteudo.append("    width: 100%;");
        conteudo.append("    ;");
        conteudo.append("}");
        conteudo.append(".igualDataTable th{");
        conteudo.append("    border: 0px solid #aaaaaa; ");
        conteudo.append("    height: 20px;");
        conteudo.append("    background: #ebebeb 50% 50% repeat-x;");
        conteudo.append("}");
        conteudo.append(".igualDataTable td{");
        conteudo.append("    padding: 2px;");
        conteudo.append("    border: 0px; ");
        conteudo.append("    height: 20px;");
        conteudo.append("}");
        conteudo.append(".igualDataTable thead td{");
        conteudo.append("    border: 1px solid #aaaaaa; ");
        conteudo.append("    background: #6E95A6 repeat-x scroll 50% 50%; ");
        conteudo.append("    border: 0px; ");
        conteudo.append("    text-align: center; ");
        conteudo.append("    height: 20px;");
        conteudo.append("}");
        conteudo.append(" .igualDataTable tr:nth-child(2n+1) {");
        conteudo.append(" background:lightgray;");
        conteudo.append(" }");
        conteudo.append("body{");
        conteudo.append("font-size: 8pt; font-family:\"Arial\", Helvetica, sans-serif");
        conteudo.append("}");
        conteudo.append("</style>");
        conteudo.append(" <title>");
        conteudo.append(" < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">");
        conteudo.append(" </title>");
        conteudo.append(" </head>");
        conteudo.append(" <body>");
        conteudo.append(" <div id=\"page-header\">");
        conteudo.append(" <table style='line-height:15%; width: 100%;'>");
        conteudo.append("<tr>");
        conteudo.append("<td >");
        conteudo.append(adicionaCabecalho());
        conteudo.append("</td >");
        conteudo.append("</tr>");
        conteudo.append(" </table>");
        conteudo.append(" </div>");
        conteudo.append(" <div id=\"page-footer\">");
        conteudo.append(" <hr noshade/>");
        conteudo.append(" <table style='line-height:15%; width: 100%;'>");
        conteudo.append("<tr>");
        conteudo.append("<td >");
        conteudo.append("<p>");
        conteudo.append("WebPúblico");
        conteudo.append("</p>");
        conteudo.append("</td>");
        conteudo.append("<td style='text-align: right'>");
        conteudo.append("<p>");
        conteudo.append("Usuário: ");
        conteudo.append(facade.getSistemaFacade().getLogin());
        conteudo.append(" - Emitido em ");
        conteudo.append(Util.formatterDiaMesAno.format(hoje));
        conteudo.append(" às ").append(Util.formatterHoraMinuto.format(hoje));
        conteudo.append("</p>");
        conteudo.append("</td>");
        conteudo.append("</tr>");
        conteudo.append(" </table>");
        conteudo.append(" </div>");
        conteudo.append(" <div id=\"page-content\">");
        conteudo.append(" <br/>");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
                conteudo.append("<th style=\"font-weight:bold;")
                    .append("text-align:")
                    .append(tabelavel.getTipoAlinhamento().getStyle())
                    .append("\">")
                    .append(tabelavel.getDescricao())
                    .append("</th>");
            }
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }

    public StreamedContent gerarTexto() {
        try {
            StringBuilder linha = getConteudoTexto();

            File arquivo = new File("relatorio_" + consulta.getChave());
            FileOutputStream fos = new FileOutputStream(arquivo);
            fos.write(linha.toString().getBytes());
            fos.close();
            InputStream stream = new FileInputStream(arquivo);
            return new DefaultStreamedContent(stream, "text/plain", "relatorio_" + consulta.getChave().toLowerCase() + ".txt");
        } catch (Exception e) {
            return null;
        }
    }

    private StringBuilder getConteudoTexto() {
        String quebraLinha = Util.quebraLinha();
        StringBuilder linha = new StringBuilder();

        linha.append("Município de Rio Branco").append(quebraLinha);
        linha.append(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente().getDescricao()).append(quebraLinha);
        linha.append("Relatório de " + consulta.getNomeTela()).append(quebraLinha);

        linha.append(quebraLinha);
        linha.append(quebraLinha);

        int colunas = consulta.getTabelaveis().size() - 1;
        Integer tamanhoTotal = 200;
        BigDecimal quantidadeSeOutro = new BigDecimal(tamanhoTotal).divide(new BigDecimal(colunas), 2, RoundingMode.HALF_UP);
        BigDecimal quantidadeSeValor = new BigDecimal(20);

        BigDecimal diferenca = quantidadeSeOutro.subtract(quantidadeSeValor);

        for (FieldConsultaEntidade colunaView : consulta.getTabelaveis()) {
            if (TipoCampo.MONETARIO.equals(colunaView.getTipo())) {
                if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
                    if (quantidadeSeOutro.add(diferenca).compareTo(new BigDecimal(tamanhoTotal)) <= 0) {
                        quantidadeSeOutro = quantidadeSeOutro.add(diferenca);
                    }
                }
            }
        }

        String espaco = "   ";
        for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
            if (TipoCampo.MONETARIO.equals(tabelavel.getTipo())) {
                linha.append(StringUtil.cortaOuCompletaEsquerda(tabelavel.getDescricao() + espaco, quantidadeSeValor.intValue(), ""));
            } else {
                linha.append(StringUtil.cortaOuCompletaDireita(tabelavel.getDescricao() + espaco, quantidadeSeValor.intValue(), ""));
            }
        }
        linha.append(quebraLinha);

        for (Map<String, Object> resultado : consulta.getResultados()) {

            for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
                Object o = resultado.get(tabelavel.getValor());
                if (o != null) {
                    if (TipoCampo.MONETARIO.equals(tabelavel.getTipo())) {
                        linha.append(StringUtil.cortaOuCompletaEsquerda(o.toString() + espaco, quantidadeSeValor.intValue(), ""));
                    } else {
                        linha.append(StringUtil.cortaOuCompletaDireita(o.toString() + espaco, quantidadeSeValor.intValue(), ""));
                    }
                } else {
                    linha.append(StringUtil.cortaOuCompletaDireita("", quantidadeSeValor.intValue(), ""));
                }
            }
            linha.append(quebraLinha);
        }
        linha.append(quebraLinha);
        return linha;
    }

    public StreamedContent gerarDocx() {
        try {
            XWPFDocument document = new XWPFDocument();
            ByteArrayOutputStream b = new ByteArrayOutputStream();

            XWPFParagraph title = document.createParagraph();
            XWPFRun runTitle = title.createRun();
            runTitle.setBold(true);
            runTitle.setFontSize(18);
            runTitle.setText("Município de Rio Branco");
            title.setAlignment(ParagraphAlignment.CENTER);


            XWPFParagraph subtitle = document.createParagraph();
            XWPFRun runSubtitle = subtitle.createRun();
            runSubtitle.setBold(true);
            runSubtitle.setFontSize(16);
            runSubtitle.setText(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente().getDescricao());
            subtitle.setAlignment(ParagraphAlignment.CENTER);


            XWPFParagraph subtitle2 = document.createParagraph();
            XWPFRun runSubtitle2 = subtitle2.createRun();
            runSubtitle2.setBold(true);
            runSubtitle2.setFontSize(16);
            runSubtitle2.setText("Relatório de " + consulta.getNomeTela());
            subtitle2.setAlignment(ParagraphAlignment.CENTER);

            int colunas = consulta.getTabelaveis().size();
            int linhas = consulta.getResultados().size() + 1;
            XWPFTable table = document.createTable(linhas, colunas);

            for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
                int posicao = consulta.getTabelaveis().indexOf(tabelavel);
                XWPFTableCell cell = table.getRow(0).getCell(posicao);
                XWPFParagraph header = cell.addParagraph();

                header.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun headerRun = header.createRun();
                headerRun.setBold(true);
                headerRun.setText(tabelavel.getDescricao());
            }

            for (Map<String, Object> resultado : consulta.getResultados()) {
                int linha = consulta.getResultados().indexOf(resultado) + 1;

                for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
                    Object o = resultado.get(tabelavel.getValor());
                    int posicao = consulta.getTabelaveis().indexOf(tabelavel);
                    XWPFTableCell cell = table.getRow(linha).getCell(posicao);
                    XWPFParagraph paragraph = cell.addParagraph();
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun paragraphRun = paragraph.createRun();
                    if (o != null) {
                        paragraphRun.setText(o.toString());
                    } else {
                        paragraphRun.setText("");
                    }
                }

            }

            document.write(b);
            b.close();

            InputStream inputStream = new ByteArrayInputStream(b.toByteArray());
            return new DefaultStreamedContent(inputStream, "docx", "relatorio_" + consulta.getChave().toLowerCase() + ".docx");
        } catch (Exception e) {
            return null;
        }
    }

    public StreamedContent gerarXml() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element results = doc.createElement("registros");
            doc.appendChild(results);

            for (Map<String, Object> resultado : consulta.getResultados()) {
                Element row = doc.createElement(consulta.getChave().toLowerCase());
                for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
                    Object o = resultado.get(tabelavel.getValor());
                    String descricao = tabelavel.getDescricao();
                    if (descricao.contains("R$")) {
                        descricao = descricao.replace("R$", "");
                    }
                    String tagName = StringUtil.removeCaracteresEspeciais(StringUtil.removeAcentuacao(descricao)).replaceAll("[^a-zA-Z0-9]", " ").replaceAll(" ", "");
                    Element node = doc.createElement(tagName.toLowerCase());
                    if (o != null) {
                        node.appendChild(doc.createTextNode(o.toString()));
                    } else {
                        node.appendChild(doc.createTextNode(""));
                    }
                    row.appendChild(node);
                }
                results.appendChild(row);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            DOMSource source = new DOMSource(doc);
            File arquivo = File.createTempFile(consulta.getChave(), "xml");
            StreamResult result = new StreamResult(arquivo);
            transformer.transform(source, result);
            InputStream stream = new FileInputStream(arquivo);
            return new DefaultStreamedContent(stream, "application/xml", consulta.getChave() + ".xml");
        } catch (Exception e) {
            return null;
        }
    }

    public String adicionaCabecalho() {
        String caminhoDaImagem = facade.getDocumentoOficialFacade().geraUrlImagemDir() + "img/Brasao_de_Rio_Branco_novo.gif";
        String conteudo =
            "<table>"
                + "<tbody>"
                + "<tr>"
                + "<td style=\"text-align: center;\">"
                + "<img src=\"" + caminhoDaImagem + "\" width=\"54\" height=\"70\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" />"
                + "</td>"
                + "<td style=\"line-height:100%; \">"
                + "<h2>Prefeitura do Município de Rio Branco</h2>"
                + "<h3>" + facade.getSistemaFacade().getSistemaService().getHierarquiAdministrativaCorrente().getDescricao() + " </h3>"
                + " <h3>Relatório de " + consulta.getNomeTela() + " </h3>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>";
        return conteudo;
    }

    public List<ActionConsultaEntidade> getDefaultActions() {
        return Lists.newArrayList();
    }

    public boolean canMostrarTabelaHistorico() {
        if (consulta != null && consulta.actionsTabela != null && !consulta.actionsTabela.isEmpty()) {
            boolean canMostrarHistorico = false;
            for (ActionConsultaEntidade action : consulta.actionsTabela) {
                if (TipoAlinhamento.DIREITA.equals(action.alinhamento)) {
                    canMostrarHistorico = true;
                    break;
                }
            }
            return canMostrarHistorico;
        }
        return false;
    }

    public String montarHref(ActionConsultaEntidade action, Map<String, Object> registo) {
        definirSessao();
        if (registo.containsKey("identificador") && hrefs.containsKey(registo.get("identificador"))) {
            return hrefs.get(registo.get("identificador"));
        }
        String url = action.href;
        if (registo.get("identificador") != null) {
            url = url.replace("$identificador", registo.get("identificador").toString());
            hrefs.put(registo.get("identificador").toString(), url);
        }
        return url;
    }

    public void definirSessao() {
        if (!getSessionMap().containsKey(TipoURLConsultaEntidade.URL_RETORNO.getKey()) || getSessionMap().get(TipoURLConsultaEntidade.URL_RETORNO.getKey()) == null) {
            getSessionMap().put(TipoURLConsultaEntidade.URL_RETORNO.getKey(), PrettyContext.getCurrentInstance().getRequestURL().toString());
        }
    }

    public void clonarValorParaOrdenacao(FieldConsultaEntidade fieldConsulta) {
        fieldConsulta.setValorOrdenacao(fieldConsulta.getValor());
    }

    public static class ConverterFieldConsultaEntidade implements Converter, Serializable {
        List<FieldConsultaEntidade> itens;

        public ConverterFieldConsultaEntidade(List<FieldConsultaEntidade> itens) {
            this.itens = itens;
        }

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            try {
                for (FieldConsultaEntidade item : itens) {
                    if (item.getCriadoEm().equals(Long.valueOf(value))) {
                        return item;
                    }
                }
            } catch (Exception ex) {
                logger.debug(ex.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Erro ao converter: " + value));
            }
            return value.toString();
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value != null) {
                return String.valueOf(((FieldConsultaEntidade) value).getCriadoEm());
            } else {
                return value != null ? value.toString() : null;
            }
        }
    }

    public void selecionarItem(List<Object> itensSelecionados, Map<String, Object> resultado, boolean varios) {
        if (!varios) {
            itensSelecionados.clear();
        }
        itensSelecionados.add(resultado.get("identificador"));
    }

    public boolean itemSelecionado(List<Object> itensSelecionados, Map<String, Object> resultado) {
        for (Object item : itensSelecionados) {
            if (resultado.get("identificador").equals(item)) {
                return true;
            }
        }
        return false;
    }

    public void desconsiderarItem(List<Object> itensSelecionados, Map<String, Object> resultado) {
        itensSelecionados.remove(resultado.get("identificador"));
    }
}
