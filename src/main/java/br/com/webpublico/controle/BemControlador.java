package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ConsultaBemMovelFiltro;
import br.com.webpublico.entidadesauxiliares.VOBem;
import br.com.webpublico.entidadesauxiliares.VOEventoBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarBemMovel", pattern = "/bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/bem/movel/lista.xhtml"),
    @URLMapping(id = "verBemMovel", pattern = "/bem-movel/ver/#{bemControlador.id}/", viewId = "/faces/administrativo/patrimonio/bem/movel/visualizar.xhtml"),
    @URLMapping(id = "editarBemMovel", pattern = "/bem-movel/editar/#{bemControlador.id}/", viewId = "/faces/administrativo/patrimonio/bem/movel/editar.xhtml"),
    @URLMapping(id = "consulta-bem-movel", pattern = "/bem-movel/consulta/", viewId = "/faces/administrativo/patrimonio/bem/movel/consulta.xhtml"),

    @URLMapping(id = "listarBemImovel", pattern = "/bem-imovel/listar/", viewId = "/faces/administrativo/patrimonio/bem/imovel/lista.xhtml"),
    @URLMapping(id = "verBemImovel", pattern = "/bem-imovel/ver/#{bemControlador.id}/", viewId = "/faces/administrativo/patrimonio/bem/imovel/visualizar.xhtml")


})
public class BemControlador extends PrettyControlador<Bem> implements Serializable, CRUD {

    @EJB
    private BemFacade facade;

    private TipoBem tipoBem;
    private EventoBem eventoBemSelecionado;
    private EstadoBem estadoBemSelecionado;
    private BemNotaFiscal bemNotaFiscal;
    private BemNotaFiscalEmpenho bemNotaFiscalEmpenho;
    private OrigemRecursoBem origemRecursoBem;
    private ConverterBem converterBem;
    private List<VOEventoBem> eventosBem;
    private List<ArquivoComposicao> anexosEventoBem;

    //    Consulta bem movel
    private ConsultaBemMovelFiltro filtroConsulta;
    private List<VOBem> bensPesquisa;
    private Integer inicio;
    private Integer maximoRegistrosTabela;
    private Integer totalDeRegistrosExistentes;

    public BemControlador() {
        super(Bem.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "verBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verBemMovel() {
        adicionarFiltrosNaSessao();
        tipoBem = TipoBem.MOVEIS;
        operacao = Operacoes.VER;
        selecionado = facade.recuperarBemComDependenciasNotasFicaisAndOrigemRecurso(getId());
        eventosBem = facade.buscarEventosBem(selecionado);
        preencherInformacoesTransient();
    }

    @URLAction(mappingId = "verBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verBemImovel() {
        tipoBem = TipoBem.IMOVEIS;
        ver();
    }

    @URLAction(mappingId = "consulta-bem-movel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void consultarBemMovel() {
        novoFiltroPesquisaBem();
        ConsultaBemMovelFiltro filtro = (ConsultaBemMovelFiltro) Web.pegaDaSessao("FILTROS");
        if (filtro != null) {
            filtroConsulta = filtro;
            consultarBens();
        }
    }

    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        preencherInformacoesTransient();
    }

    private void preencherInformacoesTransient() {
        estadoBemSelecionado = facade.buscarEstadoDoBemPorData(selecionado, facade.getSistemaFacade().getDataOperacao());
        if (estadoBemSelecionado != null) {
            Bem.preencherDadosTrasientsDoBem(selecionado, estadoBemSelecionado);
            HierarquiaOrganizacional hoAdm = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), estadoBemSelecionado.getDetentoraAdministrativa(), facade.getSistemaFacade().getDataOperacao());
            if (hoAdm == null) {
                EventoBem eventoBem = facade.recuperarUltimoEventoBemComHierarquiaVigente(selecionado);
                if (eventoBem != null && eventoBem.getEstadoResultante().getDetentoraAdministrativa() != null) {
                    hoAdm = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), eventoBem.getEstadoResultante().getDetentoraAdministrativa(), eventoBem.getDataLancamento());
                }
            }
            selecionado.setAdministrativa(hoAdm != null ? hoAdm.toString() : "");

            HierarquiaOrganizacional hoOrc = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), estadoBemSelecionado.getDetentoraOrcamentaria(), facade.getSistemaFacade().getDataOperacao());
            selecionado.setOrcamentaria(hoOrc != null ? hoOrc.toString() : "");
        }
    }

    @Override
    public String getCaminhoPadrao() {
        if (TipoBem.IMOVEIS.equals(tipoBem)) {
            return "/bem-imovel/";
        }
        return "/bem-movel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "editarBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        preencherInformacoesTransient();
        adicionarFiltrosNaSessao();
    }

    @Override
    public void salvar() {
        try {
            validarBem();
            facade.salvarBem(selecionado, estadoBemSelecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarBem() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDetentorOrigemRecurso() == null || selecionado.getDetentorOrigemRecurso().getListaDeOriemRecursoBem() == null ||
            selecionado.getDetentorOrigemRecurso().getListaDeOriemRecursoBem().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Obrigatório adicionar ao menos uma origem do recurso.");
        }
        validarNotasFiscais(ve);
        ve.lancarException();
    }

    private void validarNotasFiscais(ValidacaoException ve) {
        if (selecionado.getNotasFiscais() == null || selecionado.getNotasFiscais().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Obrigatório adicionar ao menos uma nota fiscal.");
        } else {
            for (BemNotaFiscal notaFiscal : selecionado.getNotasFiscais()) {
                if (notaFiscal.getEmpenhos() == null || notaFiscal.getEmpenhos().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A nota fiscal " + notaFiscal.getNumeroNotaFiscal() + " não possui nenhum empenho.");
                }
            }
        }
    }

    public void novaNotaFiscal() {
        this.bemNotaFiscal = new BemNotaFiscal();
    }

    public void cancelarBemNotaFiscal() {
        this.bemNotaFiscal = null;
    }

    public void adicionarBemNotaFiscal() {
        try {
            validarBemNotaFiscal();
            if (this.bemNotaFiscal.getBem() == null) {
                this.bemNotaFiscal.setBem(selecionado);
            }
            Util.adicionarObjetoEmLista(this.selecionado.getNotasFiscais(), this.bemNotaFiscal);
            cancelarBemNotaFiscal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro: " + e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarBemNotaFiscal() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(bemNotaFiscal, ve);
        ve.lancarException();
        if (selecionado.getDataAquisicao() != null &&
            bemNotaFiscal.getDataNotaFiscal().compareTo(selecionado.getDataAquisicao()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data da Nota Fiscal não pode ser superior a Data de Aquisição.");
        }
        ve.lancarException();
    }

    public void alterarBemNotaFiscal(BemNotaFiscal bemNotaFiscal) {
        this.bemNotaFiscal = (BemNotaFiscal) Util.clonarObjeto(bemNotaFiscal);
    }

    public void excluirBemNotaFiscal(BemNotaFiscal bemNotaFiscal) {
        this.selecionado.getNotasFiscais().remove(bemNotaFiscal);
    }

    public void novaNotaEmpenho(BemNotaFiscal bemNotaFiscal) {
        this.bemNotaFiscalEmpenho = new BemNotaFiscalEmpenho();
        this.bemNotaFiscalEmpenho.setBemNotaFiscal(bemNotaFiscal);
        FacesUtil.executaJavaScript("dialogEmpenho.show()");
    }

    public void alterarBemNotaFiscalEmpenho(BemNotaFiscalEmpenho bemNotaFiscalEmpenho) {
        this.bemNotaFiscalEmpenho = (BemNotaFiscalEmpenho) Util.clonarObjeto(bemNotaFiscalEmpenho);
        FacesUtil.executaJavaScript("dialogEmpenho.show()");
    }

    public void excluirBemNotaFiscalEmpenho(BemNotaFiscal bemNotaFiscal, BemNotaFiscalEmpenho bemNotaFiscalEmpenho) {
        bemNotaFiscal.getEmpenhos().remove(bemNotaFiscalEmpenho);
    }

    private void validarBemNotaFiscalEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(bemNotaFiscalEmpenho, ve);
        ve.lancarException();
        if (bemNotaFiscalEmpenho.getDataEmpenho().compareTo(bemNotaFiscalEmpenho.getBemNotaFiscal().getDataNotaFiscal()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data do Empenho não pode ser superior a Data da Nota Fiscal.");
        }
        ve.lancarException();
    }

    public void adicionarBemNotaFiscalEmpenho() {
        try {
            validarBemNotaFiscalEmpenho();
            Util.adicionarObjetoEmLista(this.bemNotaFiscalEmpenho.getBemNotaFiscal().getEmpenhos(), this.bemNotaFiscalEmpenho);
            cancelarBemNotaFiscalEmpenho();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void cancelarBemNotaFiscalEmpenho() {
        this.bemNotaFiscalEmpenho = null;
        FacesUtil.executaJavaScript("dialogEmpenho.hide()");
    }

    public void novaOrigemRecursoBem() {
        this.origemRecursoBem = new OrigemRecursoBem();
    }

    public void cancelarOrigemRecursoBem() {
        this.origemRecursoBem = null;
    }

    public void adicionarOrigemRecursoBem() {
        try {
            validarOrigemRecursoBem();
            if (selecionado.getDetentorOrigemRecurso() == null) {
                selecionado.setDetentorOrigemRecurso(new DetentorOrigemRecurso());
            }
            if (origemRecursoBem.getDetentorOrigemRecurso() == null) {
                origemRecursoBem.setDetentorOrigemRecurso(selecionado.getDetentorOrigemRecurso());
            }
            Util.adicionarObjetoEmLista(selecionado.getDetentorOrigemRecurso().getListaDeOriemRecursoBem(), origemRecursoBem);
            cancelarOrigemRecursoBem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarOrigemRecursoBem() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(origemRecursoBem, ve);
        ve.lancarException();
    }

    public void alterarOrigemRecursoBem(OrigemRecursoBem origemRecursoBem) {
        this.origemRecursoBem = (OrigemRecursoBem) Util.clonarObjeto(origemRecursoBem);
    }

    public void excluirOrigemRecursoBem(OrigemRecursoBem origemRecursoBem) {
        this.selecionado.getDetentorOrigemRecurso().getListaDeOriemRecursoBem().remove(origemRecursoBem);
    }

    public List<SelectItem> getTiposRecursoAquisicaoBem() {
        return Util.getListSelectItem(TipoRecursoAquisicaoBem.values());
    }

    public void recuperarAnexosEventoBem(Long idDetentor) {
        try {
            DetentorArquivoComposicao detentor = facade.recuperarDependenciasArquivoComposicao(idDetentor);
            anexosEventoBem = detentor.getArquivosComposicao();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível recuperar os anexos do histórico do bem.");
            anexosEventoBem = Lists.newArrayList();
        }
    }

    public void recuperarDadosDoBem(Bem bem) {
        if (bem == null) {
            FacesUtil.addOperacaoNaoPermitida("Selecione o bem para buscar as informações.");
            return;
        }
        selecionado = facade.recuperar(bem.getId());
        preencherInformacoesTransient();
        FacesUtil.executaJavaScript("$('#modalDetalhesDoBem').modal('show');");
    }

    public ConverterBem getConverterBem() {
        if (converterBem == null) {
            converterBem = new ConverterBem(Bem.class, facade);
        }
        return converterBem;
    }

    public List<VOEventoBem> getEventosBem() {
        return eventosBem;
    }

    public void setEventosBem(List<VOEventoBem> eventosBem) {
        this.eventosBem = eventosBem;
    }

    public List<ArquivoComposicao> getAnexosEventoBem() {
        return anexosEventoBem;
    }

    public void setAnexosEventoBem(List<ArquivoComposicao> anexosEventoBem) {
        this.anexosEventoBem = anexosEventoBem;
    }

    public EventoBem getEventoBemSelecionado() {
        return eventoBemSelecionado;
    }

    public void setEventoBemSelecionado(EventoBem eventoBemSelecionado) {
        this.eventoBemSelecionado = eventoBemSelecionado;
    }

    public EstadoBem getEstadoBemSelecionado() {
        return estadoBemSelecionado;
    }

    public void setEstadoBemSelecionado(EstadoBem estadoBemSelecionado) {
        this.estadoBemSelecionado = estadoBemSelecionado;
    }

    public BemNotaFiscal getBemNotaFiscal() {
        return bemNotaFiscal;
    }

    public void setBemNotaFiscal(BemNotaFiscal bemNotaFiscal) {
        this.bemNotaFiscal = bemNotaFiscal;
    }

    public BemNotaFiscalEmpenho getBemNotaFiscalEmpenho() {
        return bemNotaFiscalEmpenho;
    }

    public void setBemNotaFiscalEmpenho(BemNotaFiscalEmpenho bemNotaFiscalEmpenho) {
        this.bemNotaFiscalEmpenho = bemNotaFiscalEmpenho;
    }

    public OrigemRecursoBem getOrigemRecursoBem() {
        return origemRecursoBem;
    }

    public void setOrigemRecursoBem(OrigemRecursoBem origemRecursoBem) {
        this.origemRecursoBem = origemRecursoBem;
    }

    public void adicionarFiltrosNaSessao() {
        if (filtroConsulta != null) {
            Web.poeNaSessao("FILTROS", filtroConsulta);
        }
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno("/bem-movel/consulta/");
    }

    public void selecionarObjetoHistorico(VOBem objeto) {
        Bem bem = recuperarBem(objeto.getId());
        Web.getSessionMap().put("pagina-anterior-auditoria", PrettyContext.getCurrentInstance().getRequestURL().toString());
        FacesUtil.redirecionamentoInterno("/auditoria/listar-por-entidade/" + Persistencia.getId(bem) + "/" + bem.getClass().getSimpleName() + "/");
    }

    private Bem recuperarBem(Long idBem) {
        return facade.recuperarBemPorIdBem(idBem);
    }

    public void limparFiltrosPesquisa() {
        novoFiltroPesquisaBem();
    }

    private void novoFiltroPesquisaBem() {
        filtroConsulta = new ConsultaBemMovelFiltro();
        totalDeRegistrosExistentes = 0;
        maximoRegistrosTabela = 10;
        inicio = 0;
        filtroConsulta.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        bensPesquisa = Lists.newArrayList();
    }

    private void consultarBensMundarPagina() {
        try {
            filtroConsulta.validarFiltros();
            bensPesquisa = Lists.newArrayList();
            Object[] objects = executarConsulta();
            processarRetornoConsulta(objects);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar bem {}", ex);
        }
    }

    public void consultarBens() {
        try {
            filtroConsulta.validarFiltros();
            bensPesquisa = Lists.newArrayList();
            maximoRegistrosTabela = 10;
            inicio = 0;
            Object[] objects = executarConsulta();
            processarRetornoConsulta(objects);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao pesquisar bem {}", ex);
        }
    }

    protected Object[] executarConsulta() throws Exception {
        return facade.consultarBensComContadorDeRegistros(filtroConsulta, maximoRegistrosTabela, inicio);
    }

    private void processarRetornoConsulta(Object[] retorno) {
        try {
            bensPesquisa = (ArrayList) retorno[0];
            totalDeRegistrosExistentes = ((Long) retorno[1]).intValue();
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    public List<ConsultaBemMovelFiltro.CampoOrdenacao> getCamposOrdenacao() {
        return Arrays.asList(ConsultaBemMovelFiltro.CampoOrdenacao.values());
    }

    public List<SelectItem> getTiposOrdenacao() {
        return Util.getListSelectItemSemCampoVazio(ConsultaBemMovelFiltro.TipoOrdem.values(), false);
    }

    public List<SelectItem> getTiposAquisicao() {
        return Util.getListSelectItem(TipoAquisicaoBem.values());
    }

    public List<SelectItem> getEstadosConservacao() {
        return Util.getListSelectItem(EstadoConservacaoBem.values());
    }

    public List<SelectItem> getSituacoesEstadoConservacao() {
        return Util.getListSelectItem(SituacaoConservacaoBem.values());
    }

    public List<SelectItem> getTiposGrupo() {
        return Util.getListSelectItem(TipoGrupo.tipoGrupoPorClasseDeUtilizacao(BensMoveis.class));
    }

    public List<SelectItem> getTiposIngresso() {
        return Util.getListSelectItem(TipoEventoBem.retornaTipoEventoIngressado());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtroConsulta.validarFiltros();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("administrativo/bens/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public StreamedContent gerarExcel() {
        try {
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("administrativo/bens/excel/");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ConfiguracaoDeRelatorio configuracao = facade.getConfiguracaoDeRelatorioFacade().getConfiguracaoPorChave();
            ResponseEntity<byte[]> responseEntity = new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", "RELATÓRIO BEM MÓVEL" + ".xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE BENS MÓVEIS");
        dto.adicionarParametro("EXERCICIO", facade.getSistemaFacade().getExercicioCorrente().getAno().toString());
        dto.adicionarParametro("MODULO", "Administrativo");
        dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("CONDICAO", filtroConsulta.montarCondicao());
        dto.adicionarParametro("ORDER", filtroConsulta.montarOrdenacao());
        dto.adicionarParametro("HAS_FILTROS_NOTA_OR_EMPENHO_UTILIZADOS", filtroConsulta.hasFiltrosNotaOrEmpenhoUtilizados());
        dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao()));
        dto.adicionarParametro("ID_USUARIO_SISTEMA", facade.getSistemaFacade().getUsuarioCorrente().getId());
        return dto;
    }

    public void alterarQuantidadeDeRegistrosPara(Long qtdeRegistro) {
        this.maximoRegistrosTabela = qtdeRegistro.intValue();
        this.inicio = 0;
        consultarBensMundarPagina();
    }

    public void proximos() {
        inicio += maximoRegistrosTabela;
        consultarBensMundarPagina();
    }

    public void anteriores() {
        inicio -= maximoRegistrosTabela;
        if (inicio < 0) {
            inicio = 0;
        }
        consultarBensMundarPagina();
    }

    public boolean isTemMaisResultados() {
        if (bensPesquisa == null) {
            consultarBensMundarPagina();
        }
        return bensPesquisa.size() >= maximoRegistrosTabela;
    }

    public boolean isTemAnterior() {
        return inicio > 0;
    }

    public void mostrarPrimeirosRegistros() {
        inicio = 0;
        consultarBensMundarPagina();
    }

    public void mostrarUltimosRegistros() {
        inicio = getTotalDeRegistrosExistentes();
        inicio = inicio - maximoRegistrosTabela;
        Integer pgAtual = getPaginaAtual();
        if (totalDeRegistrosExistentes - (maximoRegistrosTabela * pgAtual) < maximoRegistrosTabela) {
            inicio = maximoRegistrosTabela * pgAtual;
        }
        consultarBensMundarPagina();
    }

    public boolean desabilitarBotaoUtilmo() {
        return getTotalDeRegistrosExistentes() <= (inicio + maximoRegistrosTabela);
    }

    public boolean desabilitarBotaoPrimeiro() {
        return inicio <= 0;
    }

    public Integer getPaginaAtual() {
        Double inicialD = new Double("" + inicio);
        Double maximoD = new Double("" + maximoRegistrosTabela);
        Double totalD = new Double("" + getTotalDeRegistrosExistentes());

        Double pDivisao = totalD / maximoD;
        Double psoma = maximoD + inicialD;
        Double pMultiplicacao = pDivisao * psoma;
        pMultiplicacao = Math.ceil(pMultiplicacao);

        Double pResultado = pMultiplicacao / totalD;

        return pResultado.intValue();
    }

    public Integer getTotalDePaginas() {
        Double maximoD = new Double("" + maximoRegistrosTabela);
        Double totalD = new Double("" + getTotalDeRegistrosExistentes());
        Double totalDePaginas = Math.ceil(totalD / maximoD);

        return totalDePaginas.intValue();
    }

    public String botaoSelecionado(Integer i) {
        if (i == maximoRegistrosTabela) {
            return "botao-cabecalho-selecionado";
        }
        return "";
    }

    public Integer getTotalDeRegistrosExistentes() {
        if (totalDeRegistrosExistentes == null) {
            totalDeRegistrosExistentes = facade.recuperarQuantidadeRegistroExistentes().intValue();
        }
        return totalDeRegistrosExistentes;
    }

    public void setTotalDeRegistrosExistentes(Integer totalDeRegistrosExistentes) {
        this.totalDeRegistrosExistentes = totalDeRegistrosExistentes;
    }

    public List<VOBem> getBensPesquisa() {
        return bensPesquisa;
    }

    public void setBensPesquisa(List<VOBem> bensPesquisa) {
        this.bensPesquisa = bensPesquisa;
    }


    public int getInicio() {
        return inicio;
    }

    public int getMaximoRegistrosTabela() {
        return maximoRegistrosTabela;
    }

    public void setMaximoRegistrosTabela(int maximoRegistrosTabela) {
        this.maximoRegistrosTabela = maximoRegistrosTabela;
    }

    public ConsultaBemMovelFiltro getFiltroConsulta() {
        return filtroConsulta;
    }

    public void setFiltroConsulta(ConsultaBemMovelFiltro filtroConsulta) {
        this.filtroConsulta = filtroConsulta;
    }

    public String getDescricaoGrupoPatrimonialPorGrupoObjetoCompra(GrupoObjetoCompra grupo) {
        try {
            return facade.getGrupoObjetoCompraGrupoBemFacade().recuperarAssociacaoDoGrupoObjetoCompra(grupo, facade.getSistemaFacade().getDataOperacao()).getGrupoBem().toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public HierarquiaOrganizacional buscarHierarquiaAdministrativaVigente(UnidadeOrganizacional unidadeOrganizacional) {
        return facade.getHierarquiaDaUnidade(unidadeOrganizacional, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public HierarquiaOrganizacional buscarHierarquiaOrcamentariaVigente(UnidadeOrganizacional unidadeOrganizacional) {
        return facade.getHierarquiaDaUnidade(unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public BigDecimal valorTotalDosBens(List<EventoBem> eventoBens) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!eventoBens.isEmpty()) {
            for (EventoBem evento : eventoBens) {
                valorTotal = valorTotal.add(evento.getValorDoLancamento());
            }
        }
        return valorTotal;
    }

    public BigDecimal valorTotalDosAjustes(List<EventoBem> eventoBens) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!eventoBens.isEmpty()) {
            for (EventoBem evento : eventoBens) {
                valorTotal = valorTotal.add(evento.getEstadoResultante().getValorDosAjustes());
            }
        }
        return valorTotal;
    }
}
